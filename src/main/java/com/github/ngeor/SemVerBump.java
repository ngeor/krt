package com.github.ngeor;

import com.github.zafarkhaja.semver.Version;
import java.util.Optional;

public enum SemVerBump {
    MAJOR,
    MINOR,
    PATCH;

    public Version bump(Version version) {
        switch (this) {
            case MAJOR:
                return version.nextMajorVersion();
            case MINOR:
                return version.nextMinorVersion();
            case PATCH:
                return version.nextPatchVersion();
            default:
                throw new IllegalStateException("Unknown enum value: " + this);
        }
    }

    public static Optional<SemVerBump> tryParse(String value) {
        try {
            return Optional.of(SemVerBump.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
