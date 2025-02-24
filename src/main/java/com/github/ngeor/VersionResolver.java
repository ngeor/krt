package com.github.ngeor;

import com.github.zafarkhaja.semver.Version;
import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VersionResolver {
    private final VersionsProvider versionsProvider;

    public VersionResolver(VersionsProvider versionsProvider) {
        this.versionsProvider = versionsProvider;
    }

    public Version resolve(String version) throws IOException, InterruptedException {
        SortedSet<Version> versions = versionsProvider.listVersions();
        SemVerBump bump = SemVerBump.tryParse(version).orElse(null);
        if (bump != null) {
            return bump.bump(versions.last());
        }

        Version result = Version.parse(version);
        if (!versions.isEmpty()) {
            if (versions.contains(result)) {
                throw new IllegalArgumentException(String.format("Version %s already exists", version));
            }

            Version latest = versions.last();
            Set<Version> allowed =
                    Stream.of(SemVerBump.values()).map(b -> b.bump(latest)).collect(Collectors.toSet());
            if (!allowed.contains(result)) {
                throw new IllegalArgumentException(
                        String.format("No sem ver gaps allowed. Allowed versions are: %s", allowed));
            }
        }

        return result;
    }
}
