package edu.usc.Strategy;

import edu.usc.LLM.KFGElementReader;
import edu.usc.LLM.NavigationOrderInference;
import edu.usc.LLM.NavigationOutputSaver;
import edu.usc.LLM.OpenAIClient;
import edu.usc.LLM.ScreenshotReader;
import edu.usc.Utilities.LoadConfig;

import java.nio.file.Path;
import java.util.List;

public final class LLMStrategy implements NavigationStrategy {

    @Override
    public void run(LoadConfig config) throws Exception {
        String screenshotLocation = config.getProperties()
                .getProperty("LLM_screenshot_location");

        String navigationOutputLocation = config.getProperties()
                .getProperty("LLM_navigation_output_location");

        if (screenshotLocation == null || screenshotLocation.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing config property: LLM_screenshot_location");
        }

        if (navigationOutputLocation == null || navigationOutputLocation.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing config property: LLM_navigation_output_location");
        }

        Path screenshotRoot = Path.of(screenshotLocation);
        Path navigationOutputRoot = Path.of(navigationOutputLocation);

        OpenAIClient openAIClient = new OpenAIClient("gpt-5.4");
        NavigationOrderInference navOrderInference =
                new NavigationOrderInference(openAIClient);

        for (String subject : config.getSubjects()) {
            System.out.println("Subject: " + subject);

            List<String> elements =
                    KFGElementReader.readElements(config, subject);

            List<Path> screenshots =
                    ScreenshotReader.readScreenshots(screenshotRoot, subject);

            System.out.println("KFG elements: " + elements.size());
            System.out.println("Screenshots: " + screenshots.size());
            System.out.println("Inferring navigation order...");

            String navigationOrder = navOrderInference.infer(
                    subject,
                    elements,
                    screenshots);

            Path savedOutput = NavigationOutputSaver.save(
                    navigationOutputRoot,
                    subject,
                    navigationOrder);

            System.out.println();
            System.out.println("Navigation order result:");
            System.out.println(navigationOrder);
            System.out.println();

            System.out.println("Saved navigation output to:");
            System.out.println(savedOutput);
            System.out.println();
        }
    }
}