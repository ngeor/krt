package com.github.ngeor;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EnsureOnDefaultBranchRuleTest {
    private final Git git = mock(Git.class);
    private final EnsureOnDefaultBranchRule rule = new EnsureOnDefaultBranchRule(git);

    @Test
    void testValid() throws IOException, InterruptedException {
        when(git.defaultBranch()).thenReturn("master");
        when(git.currentBranch()).thenReturn("master");
        assertDoesNotThrow(rule::validate);
    }

    @Test
    void testInvalid() throws IOException, InterruptedException {
        when(git.defaultBranch()).thenReturn("master");
        when(git.currentBranch()).thenReturn("develop");
        assertThrows(IllegalStateException.class, rule::validate);
    }
}
