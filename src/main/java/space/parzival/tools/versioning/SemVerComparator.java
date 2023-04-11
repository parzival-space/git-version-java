package space.parzival.tools.versioning;

import java.util.Comparator;

public class SemVerComparator implements Comparator<SemVer> {

    @Override
    public int compare(SemVer v1, SemVer v2) {
        if (v1.getMajor() != v2.getMajor()) {
            return Integer.compare(v1.getMajor(), v2.getMajor());
        } else if (v1.getMinor() != v2.getMinor()) {
            return Integer.compare(v1.getMinor(), v2.getMinor());
        } else if (v1.getPatch() != v2.getPatch()) {
            return Integer.compare(v1.getPatch(), v2.getPatch());
        } else {
            return v1.getSuffix().compareTo(v2.getSuffix());
        }
    }

}
