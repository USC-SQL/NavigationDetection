package edu.usc.LLM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NavigationOutputReader {

    private NavigationOutputReader() { }

    public static String read(
            Path navigationOutputRoot,
            String subject) throws IOException {

        Path inputFile = navigationOutputRoot
                .resolve(subject)
                .resolve(subject + ".json");

        if (!Files.exists(inputFile)) {
            throw new IOException(
                    "Navigation output file not found: " + inputFile);
        }

        return Files.readString(inputFile);
    }
}