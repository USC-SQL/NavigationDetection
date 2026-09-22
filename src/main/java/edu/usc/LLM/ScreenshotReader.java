package edu.usc.LLM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Finds the annotated screenshots associated with a subject.
 */
public final class ScreenshotReader {

    private ScreenshotReader() { }

    public static List<Path> readScreenshots(Path screenshotRoot, String subject)
            throws IOException {

        Path subjectDirectory = screenshotRoot.resolve(subject);

        if (!Files.isDirectory(subjectDirectory)) {
            throw new IllegalArgumentException(
                    "Screenshot directory not found: " + subjectDirectory);
        }

        try (Stream<Path> files = Files.list(subjectDirectory)) {
            List<Path> screenshots = files
                    .filter(Files::isRegularFile)
                    .filter(ScreenshotReader::isSupportedImage)
                    .sorted(Comparator.comparingInt(
                            ScreenshotReader::extractImageNumber))
                    .toList();

            if (screenshots.isEmpty()) {
                throw new IllegalStateException(
                        "No screenshots found for subject: " + subject);
            }

            return screenshots;
        }
    }

    private static boolean isSupportedImage(Path path) {
        String name = path.getFileName().toString().toLowerCase();

        return name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".webp");
    }

    private static int extractImageNumber(Path path) {
        String name = path.getFileName().toString();

        int end = name.lastIndexOf('.');
        if (end < 0) {
            end = name.length();
        }

        int start = end;

        while (start > 0 && Character.isDigit(name.charAt(start - 1))) {
            start--;
        }

        if (start == end) {
            return Integer.MAX_VALUE;
        }

        return Integer.parseInt(name.substring(start, end));
    }
}