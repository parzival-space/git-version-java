package versioning;

import org.junit.jupiter.api.Test;
import space.parzival.tools.versioning.SemVer;

import static org.junit.jupiter.api.Assertions.*;

class SemVerTest {
    @Test
    void test() {
        // valid input
        assertDoesNotThrow(() -> new SemVer("12.3.4-EXAMPLE"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> new SemVer("aRandomStringNotAVersion"));

        // verify parsing is correct
        SemVer version = new SemVer("12.3.4-EXAMPLE");

        assertEquals(12, version.getMajor());
        assertEquals(3, version.getMinor());
        assertEquals(4, version.getPatch());
        assertEquals("EXAMPLE", version.getSuffix());
    }

    @Test
    void testValidate() {
        assertTrue(SemVer.validate("1.2.3"));
        assertFalse(SemVer.validate("SomeRandomString"));
    }

    @Test
    void testBumps() {
        SemVer version = new SemVer("1.1.1");

        assertEquals(2, version.bumpMajor().getMajor());
        assertEquals(2, version.bumpMinor().getMinor());
        assertEquals(2, version.bumpPatch().getPatch());
    }

    @Test
    void testToString() {
        String originalVersion = "12.3.4-EXAMPLE";
        SemVer version = new SemVer(originalVersion);

        assertEquals(originalVersion, version.toString());
    }
}
