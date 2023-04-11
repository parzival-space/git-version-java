package versioning;

import org.junit.jupiter.api.Test;
import space.parzival.tools.versioning.SemVer;
import space.parzival.tools.versioning.SemVerComparator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SemVerComparatorTest {

    @Test
    void testSort() {
        List<SemVer> versions = new ArrayList<>();

        versions.add(new SemVer("0.0.1-SNAPSHOT"));
        versions.add(new SemVer("0.1.0"));
        versions.add(new SemVer("1.0.0"));

        versions.sort(new SemVerComparator());

        assertEquals("0.0.1-SNAPSHOT", versions.get(0).toString());
        assertEquals("1.0.0", versions.get(versions.size() - 1).toString());
    }
}
