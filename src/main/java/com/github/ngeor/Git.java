package com.github.ngeor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.ngeor.ProcessUtils.waitForSuccess;

/**
 * Encapsulates git operations.
 */
public class Git {
    private final File directory;

    public Git(File directory) {
        this.directory = directory;
    }

    /**
     * Gets the name of the default branch.
     */
    public String defaultBranch() throws IOException, InterruptedException {
        String prefix = "refs/remotes/origin/";
        String result = runAsString("symbolic-ref", prefix + "HEAD");
        return result.substring(prefix.length());
    }

    /**
     * Gets the name of the current branch.
     */
    public String currentBranch() throws IOException, InterruptedException {
        return runAsString("rev-parse", "--abbrev-ref", "HEAD");
    }

    public void checkoutNewBranch(String name) throws IOException, InterruptedException {
        run("checkout", "-b", name);
    }

    public void init() throws IOException, InterruptedException {
        run("init");
    }

    public void initBare() throws IOException, InterruptedException {
        run("init", "--bare");
    }

    public void clone(String url) throws IOException, InterruptedException {
        run("clone", url, ".");
    }

    public void push() throws IOException, InterruptedException {
        run("push", "--follow-tags");
    }

    /**
     * Adds a file to the git index.
     * @param path The path, relative to the git working directory.
     */
    public void add(String path) throws IOException, InterruptedException {
        run("add", path);
    }

    public void commit(String message) throws IOException, InterruptedException {
        // TODO only commit files we know are supposed to have changed
        run("commit", "-a", "-m", message);
    }

    public void tag(String msg, String tag) throws IOException, InterruptedException {
        run("tag", "-a", "-m", msg, tag);
    }

    public List<String> listTags(String pattern) throws IOException, InterruptedException {
        return pattern != null ? runAsStrings("tag", "-l", pattern) : runAsStrings("tag", "-l");
    }

    public void fetch() throws IOException, InterruptedException {
        run("fetch", "-p", "-t");
    }

    public void pull() throws IOException, InterruptedException {
        run("pull");
    }

    public void config(String key, String value) throws IOException, InterruptedException {
        run("config", key, value);
    }

    public boolean hasPendingChanges() throws IOException, InterruptedException {
        Process process = createProcessBuilder("diff-index", "--quiet", "HEAD", "--").start();
        int exitCode = process.waitFor();
        return exitCode != 0;
    }

    private void run(String... args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = createProcessBuilder(args);
        waitForSuccess(processBuilder.start());
    }

    private String runAsString(String... args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = createProcessBuilder(args);
        Process process = processBuilder.start();
        waitForSuccess(process);
        try (BufferedInputStream stdout = new BufferedInputStream(process.getInputStream())) {
            return new String(stdout.readAllBytes(), StandardCharsets.UTF_8).trim();
        }
    }

    private List<String> runAsStrings(String... args) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = createProcessBuilder(args);
        Process process = processBuilder.start();
        waitForSuccess(process);
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            result.add(reader.readLine());
        }
        return result;
    }

    private ProcessBuilder createProcessBuilder(String... args) {
        String[] command = Stream.concat(Stream.of("git"), Stream.of(args)).toArray(String[]::new);
        return new ProcessBuilder(command).directory(directory);
    }
}
