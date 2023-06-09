package space.parzival.tools.versioning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.util.StringUtils;

@AllArgsConstructor
public class SemVer {

    @Getter @Setter
    private int major = 0;

    @Getter @Setter
    private int minor = 0;

    @Getter @Setter
    private int patch = 0;

    @Getter @Setter
    private String suffix = "";


    public SemVer(String version) {
        if (!validate(version)) {
            throw new IllegalArgumentException("The provided version string (" + version + ") is not SemVer compliant!");
        }

        this.parse(version);
    }

    /**
     * Parses the given version string into the SemVer object.
     * @param version A SemVer version string.
     */
    public void parse(String version) {
        if (version.startsWith("v")) version = version.substring(1); // remove leading "v" that some version have
        String[] parts = version.split("-");
        String[] versionParts = parts[0].split("\\.");

        // try-catch to prevent IllegalArgumentException
        try {
            this.major = Integer.parseInt(versionParts[0]);
            this.minor = Integer.parseInt(versionParts[1]);
            this.patch = Integer.parseInt(versionParts[2]);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The provided version string (" + version + ") is not SemVer compliant!");
        }

        // suffix part
        if (parts.length > 1) {
            this.suffix = parts[1];
        }
    }

    public String toString() {
        if (StringUtils.isEmptyOrNull(this.suffix)) {
            return "%d.%d.%d".formatted(this.major, this.minor, this.patch);
        } else {
            return "%d.%d.%d-%s".formatted(this.major, this.minor, this.patch, this.suffix);
        }
    }

    public SemVer bumpMajor() { return this.bumpMajor(1); }
    public SemVer bumpMajor(int amount) {
        this.major += amount;
        this.minor = 0;
        this.patch = 0;
        return this;
    }

    public SemVer bumpMinor() { return this.bumpMinor(1); }
    public SemVer bumpMinor(int amount) {
        this.minor += amount;
        this.patch = 0;
        return this;
    }

    public SemVer bumpPatch() { return this.bumpPatch(1); }
    public SemVer bumpPatch(int amount) {
        this.patch += amount;
        return this;
    }

    /**
     * Checks if a given string is SemVer compliant.
     * @param version A SemVer version string.
     */
    public static boolean validate(String version) {
        if (version.startsWith("v")) version = version.substring(1); // remove leading "v" that some version have
        return version.matches("^\\d+\\.\\d+\\.\\d+(?:-[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*)?");
    }
}
