package edu.usc.LLM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NavigationOutputSaver {

    private NavigationOutputSaver() { }

    public static Path save(
            Path outputRoot,
            String subject,
            String json) throws IOException {

        Path subjectDirectory = outputRoot.resolve(subject);

        Files.createDirectories(subjectDirectory);

        Path outputFile = subjectDirectory.resolve(subject + ".json");

        Files.writeString(outputFile, json);

        return outputFile;
    }
}