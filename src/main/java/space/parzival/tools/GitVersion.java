package space.parzival.tools;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import space.parzival.tools.versioning.SemVer;
import space.parzival.tools.versioning.SemVerComparator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
public class GitVersion {
    private Git git;

    private final String majorIdentifier;
    private final String minorIdentifier;
    private final String releaseBranch;
    private final String developmentBranch;
    private final String snapshotSuffix;
    private final boolean addCommitHash;

    /**
     * Re-Implementation of the git-version tool created by Codacy.
     * Allows you to fetch the last tagged and the next version of a repository by analysing its git history.
     * @param repoPath The path to the repository.
     * @param majorIdentifier A regex that can be used to identify commits that should bump the major version.
     * @param minorIdentifier A regex that can be used to identify commits that should bump the minor version.
     * @param releaseBranch The name of the release branch.
     * @param developmentBranch The name of the trunk or development branch.
     * @param snapshotSuffix A Suffix that will be applied to all snapshot releases.
     * @param addCommitHash Whether you want to add the commit hash to build made not on the release or development branch.
     */
    public GitVersion(String repoPath, String majorIdentifier, String minorIdentifier, String releaseBranch, String developmentBranch, String snapshotSuffix, boolean addCommitHash) {
        this.majorIdentifier = majorIdentifier;
        this.minorIdentifier = minorIdentifier;
        this.releaseBranch = releaseBranch;
        this.developmentBranch = developmentBranch;
        this.snapshotSuffix = snapshotSuffix;
        this.addCommitHash = addCommitHash;

        try {
            this.git = Git.open(new File(repoPath));
        } catch (IOException e) {
            log.error("Failed to open git repository.", e);
        }
    }

    /**
     * Fetches the next version from the git history.
     * @return A new SemVer version.
     */
    public SemVer getNextVersion() {
        SemVer currentVersion = this.getLastVersion();
        List<RevCommit> commits = this.getCommitsSince(currentVersion);

        // apply suffix depending on branch name
        try {
            String branchName = git.getRepository().getBranch();

            if (branchName.equals(this.releaseBranch)) {
                currentVersion.setSuffix(null);
            }
            else if (branchName.equals(this.developmentBranch)) {
                currentVersion.setSuffix(this.snapshotSuffix);
            }
            else {
                String commitHash = Integer.toHexString(commits.get(0).hashCode()).toLowerCase();
                currentVersion.setSuffix(this.addCommitHash ? "%s-%s".formatted(this.snapshotSuffix, commitHash) : this.snapshotSuffix);
            }
        }
        catch (IOException e) {
            log.error("Unexpected IOException. Is your hardware still hard? :)", e);
        }

        // contains major keyword -> bump major version
        if (commits.stream().anyMatch(c -> c.getShortMessage().matches(this.majorIdentifier)))
            return currentVersion.bumpMajor();

        // contains minor keyword -> bump minor version
        else if (commits.stream().anyMatch(c -> c.getShortMessage().matches(this.minorIdentifier)))
            return currentVersion.bumpMinor();

        // contains no keyword -> bump patch version
        else return currentVersion.bumpPatch();
    }

    /**
     * Fetches the last version from the git history.
     * @return A new SemVer instance of the last Version.
     */
    public SemVer getLastVersion() {
        try {
            // fetch all tags
            List<Ref> gitTags = this.git.tagList().call();

            // sort by version, oldest -> newest
            return gitTags.stream()
                    .map((tag) -> new SemVer(tag.getName().replaceAll("^refs/tags/" ,"")))
                    .sorted(new SemVerComparator()) // sort tags, oldest -> newest
                    .reduce((first, second) -> second) // return only the newest
                    .orElse(new SemVer("0.0.0"));
        }
        catch (GitAPIException e) {
            log.warn("No tags found in this repo. Falling back to version 0.0.0");
            return new SemVer("0.0.0");
        }
    }

    /**
     * Fetches all commits that where done since the given version.
     * @param version A SemVer version.
     * @return A list of commits.
     */
    public List<RevCommit> getCommitsSince(SemVer version) {
        Repository repository = this.git.getRepository();

        // get last commit ref
        String lastCommit;
        try {
            ObjectId objectId = repository.resolve("refs/tags/%s".formatted(version.toString()));

            // if no commit was found, set to head
            if (objectId == null)
                lastCommit = "HEAD";
            else
                lastCommit = objectId.getName();
        }
        catch (IOException e) {
            log.warn("Failed to find last commit. Falling back to HEAD.");
            lastCommit = "HEAD";
        }

        // get all commits
        ;
        try {
            Iterable<RevCommit> commits = git.log()
                    .addRange(repository.resolve(lastCommit), repository.resolve("HEAD"))
                    .call();

            return StreamSupport.stream(commits.spliterator(), false).toList();
        }
        catch (IOException | GitAPIException e) {
            // do nothing
            log.error("Something went wrong :(", e);
        }

        return null;
    }
}
