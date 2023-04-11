package space.parzival.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import space.parzival.tools.versioning.SemVer;

import java.io.PrintWriter;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = parser.parse(options, args);

        // print help page if wanted
        if (cmd.hasOption("h")) {
            printHelpPage(options);
            return;
        }

        // get variables
        String repoPath =           cmd.hasOption("target")             ? cmd.getOptionValue("target")              : System.getProperty("user.dir"); // NOSONAR
        String majorIdentifier =    cmd.hasOption("major-identifier")   ? cmd.getOptionValue("major-identifier")    : "breaking:.*";                  // NOSONAR
        String minorIdentifier =    cmd.hasOption("minor-identifier")   ? cmd.getOptionValue("minor-identifier")    : "feature:.*";                   // NOSONAR
        String releaseBranch =      cmd.hasOption("release-branch")     ? cmd.getOptionValue("release-branch")      : "main";                         // NOSONAR
        String developmentBranch =  cmd.hasOption("snapshot-branch")    ? cmd.getOptionValue("snapshot-branch")     : "development";                  // NOSONAR
        String snapshotSuffix =     cmd.hasOption("suffix")             ? cmd.getOptionValue("suffix")              : "SNAPSHOT";                     // NOSONAR
        boolean addCommitHash =     !cmd.hasOption("no-hash");
        boolean previousVersion =   cmd.hasOption("previous-version");

        // prepare git version
        GitVersion gitVersion = new GitVersion(
                repoPath,
                majorIdentifier,
                minorIdentifier,
                releaseBranch,
                developmentBranch,
                snapshotSuffix,
                addCommitHash
        );

        // get and return version
        SemVer version = previousVersion ? gitVersion.getLastVersion() : gitVersion.getNextVersion();
        log.info(version.toString());
    }

    /**
     * Creates a list of Options.
     */
    private static Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder()
                .longOpt("target")
                .option("t")
                .argName("path")
                .desc("The path to the git repository.")
                .type(String.class)
                .hasArg()
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("major-identifier")
                .argName("regex")
                .desc("A regex that can be used to identify commits that should bump the major version.\nDefault: 'breaking:.*'")
                .type(String.class)
                .hasArg()
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("minor-identifier")
                .argName("regex")
                .desc("A regex that can be used to identify commits that should bump the minor version.\nDefault: 'feature:.*'")
                .type(String.class)
                .hasArg()
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("release-branch")
                .argName("name")
                .desc("The name of the release branch.\nDefault: main")
                .type(String.class)
                .hasArg()
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("snapshot-branch")
                .argName("name")
                .desc("The name of the snapshot branch.\nDefault: development")
                .type(String.class)
                .hasArg()
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("suffix")
                .option("s")
                .argName("name")
                .desc("A Suffix that will be applied to all snapshot releases.\nDefault: SNAPSHOT")
                .type(String.class)
                .hasArg()
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("no-hash")
                .option("n")
                .desc("Removes the commit hash that normally gets added to a version if you are not on the release or snapshot branch.")
                .type(Boolean.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("help")
                .option("h")
                .desc("Show this help page.")
                .type(Boolean.class)
                .build()
        );

        options.addOption(Option.builder()
                .longOpt("previous-version")
                .option("p")
                .desc("Returns the last version instead of the next one.")
                .type(Boolean.class)
                .build()
        );

        return options;
    }


    /**
     * Prints a fancy help page.
     */
    private static void printHelpPage(Options options) {
        final int width = 150;
        final int padLeft = 3;
        final int descPad = 15;

        // about info
        final String appTitle = Main.class.getPackage().getImplementationTitle();
        final String appVersion = Main.class.getPackage().getImplementationVersion();
        final String appAuthor = Main.class.getPackage().getImplementationVendor();

        // create formatter
        HelpFormatter formatter = new HelpFormatter();
        final PrintWriter writer = new PrintWriter(System.out); // NOSONAR

        // print message
        writer.println("%s v%s by %s".formatted(appTitle, appVersion, appAuthor));
        writer.println("Simple tool to automate versioning of git repositories.");
        writer.println();
        formatter.printUsage(writer, width, appTitle, options);
        writer.println();
        formatter.printOptions(writer, width, options, padLeft, descPad);
        writer.println();

        writer.flush();
    }
}