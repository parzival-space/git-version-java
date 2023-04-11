# git-version-java
A re-implementation in Java of the git-version tool made by Codacy with some tweaks.

Codacy describes this project as follows:
> The goal of this tool is to have a simple versioning system that we can use to track the different releases. 
> The tool prints the current version (e.g. to be used for tagging) depending on the git history and commit messages.
> <br />
> The versioning scheme is assumed to be Semver based.

## Usage
You can display a help page by passing the `--help`, or short `-h`, argument.
```
git-version v1.0-SNAPSHOT by Parzival
Simple tool to automate versioning of git repositories.

usage: git-version [-h] [--major-identifier <regex>] [--minor-identifier <regex>] [-n] [-p] [--release-branch <name>] [-s <name>] [--snapshot-branch
       <name>] [-t <path>]

   -h,--help                                   Show this help page.
      --major-identifier <regex>               A regex that can be used to identify commits that should bump the major version.
                                               Default: 'breaking:'
      --minor-identifier <regex>               A regex that can be used to identify commits that should bump the minor version.
                                               Default: 'feature:'
   -n,--no-hash                                Removes the commit hash that normally gets added to a version if you are not on the release or snapshot
                                               branch.
   -p,--previous-version                       Returns the last version instead of the next one.
      --release-branch <name>                  The name of the release branch.
                                               Default: main
   -s,--suffix <name>                          A Suffix that will be applied to all snapshot releases.
                                               Default: SNAPSHOT
      --snapshot-branch <name>                 The name of the snapshot branch.
                                               Default: development
   -t,--target <path>                          The path to the git repository.

```

## How To Version
This tool creates a version with the format `MAJOR.MINOR.PATCH`.

_Using this tool may look something like this:_
```bash
$ java -jar /path/to/git-version.jar
1.0.0
```

Versions are incremented since the last tag. 
The patch version is incremented by default, unless there is at least one commit since the last tag, containing a minor or major identifier (defaults to `feature:` or `breaking:`) in the message.

On branches other than the release and development branch (default to `main` and `development`), the version will look something like below.
The commit hash can be disabled using `--no-hash`, but is enabled by default.
```
{MAJOR}.{MINOR}.{PATCH}-{SNAPSHOT-SUFFIX}-{COMMIT-HASH}
```

On the development branch the format is the following:
```
{MAJOR}.{MINOR}.{PATCH}-{SNAPSHOT-SUFFIX}
```

### Examples
_Example:_
```
---A---B---C <= Main (tag: 1.0.1)          L <= Main (git-version: 1.0.2)
            \                             /
             D---E---F---G---H---I---J---K <= feature/cool-feature (git-version: 1.0.2-SNAPSHOT-9247756d)
```

_Example2 (with dev branch):_
```
---A---B---C <= Main (tag: 1.0.1)          L <= Main (git-version: 1.0.2)
            \                             / <= Fast-forward merges to master (same commit id)
             C                           L <= Dev (git-version: 1.0.2-SNAPSHOT)
              \                         /
               E---F---G---H---I---J---K <= feature/cool-feature (git-version: 1.0.2-SNAPSHOT-9247756d)
```

_Example3 (with breaking message):_
```
---A---B---C <= Main (tag: 1.0.1)          L <= Main (git-version: 2.0.0)
            \                             /
             D---E---F---G---H---I---J---K <= feature/cool-feature (git-version: 1.0.2-SNAPSHOT-9247756d)
                                         \\
                                          message: "breaking: added incompatible cool new feature"
```

## Requirements
Before running this app, ensure you have the following requirements:
* Java 17 or above.
  You can download the latest LTS binaries from [Adoptium](https://adoptium.net/).


## Build It Yourself
To compile the artifact locally, you need to have the following requirements setup:
* Java 17 JDK or above
* [Apache Maven](https://maven.apache.org/)

Now compile the project using:
```bash
$ mvn package
```

## Credits
This was originally intended as a direct port of the [git-version](https://github.com/codacy/git-version) project by [Codacy](https://github.com/codacy).
Later I also added some minor tweaks, like being able to toggle the commit hash.

I also want to mention the [GitVersion](https://github.com/GitTools/GitVersion) project, from wich [Codacy](https://github.com/codacy) took inspiration from.

## Contributing
Do you want to improve this project?
You are more than welcome to contribute.

To contribute, please follow these steps:
1. [Fork the Repository](https://github.com/parzival-space/git-version-java/fork)
2. Create a branch with a descriptive name for your feature or bugfix (ex: feature/super-cool-feature)
3. Submit a pull request
4. Profit!

## License
This project is distributed under the GNU GPL-3.0 license. 
See ``LICENSE`` for more information.