package com.github.ngeor;

import com.github.zafarkhaja.semver.Version;
import java.io.IOException;
import java.util.SortedSet;

public interface VersionsProvider {
    SortedSet<Version> listVersions() throws IOException, InterruptedException;
}
