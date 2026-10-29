package edu.usc.Strategy;

import edu.usc.LLM.KFGElementReader;
import edu.usc.LLM.NavigationOrderInference;
import edu.usc.LLM.NavigationOutputSaver;
import edu.usc.LLM.OpenAIClient;
import edu.usc.LLM.ScreenshotReader;
import edu.usc.LLM.LNFDetection;
import edu.usc.LLM.LNFOutputSaver;
import edu.usc.LLM.NavigationOutputReader;
import edu.usc.LLM.LNFPatternAnalysis;
import edu.usc.LLM.LNFAnalysisOutputSaver;
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

        String lnfOutputLocation = config.getProperties()
                .getProperty("LLM_lnf_output_location");

        String llmStage = config.getProperties()
                .getProperty("LLM_stage");

        if (screenshotLocation == null || screenshotLocation.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing config property: LLM_screenshot_location");
        }

        if (navigationOutputLocation == null || navigationOutputLocation.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing config property: LLM_navigation_output_location");
        }

        if (llmStage == null || llmStage.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing config property: LLM_stage");
        }

        Path screenshotRoot = Path.of(screenshotLocation);
        Path navigationOutputRoot = Path.of(navigationOutputLocation);

        OpenAIClient openAIClient = new OpenAIClient("gpt-5.4");

        if (llmStage.equals("1")) {

            runNavigationOrderStage(
                    config,
                    screenshotRoot,
                    navigationOutputRoot,
                    openAIClient);

        } else if (llmStage.equals("2")) {

            if (lnfOutputLocation == null || lnfOutputLocation.isBlank()) {
                throw new IllegalArgumentException(
                        "Missing config property: LLM_lnf_output_location");
            }

            Path lnfOutputRoot = Path.of(lnfOutputLocation);

            runLNFDetectionStage(
                    config,
                    screenshotRoot,
                    navigationOutputRoot,
                    lnfOutputRoot,
                    openAIClient);

        } else if (llmStage.equals("3")) {

            String analysisInputLocation = config.getProperties()
                    .getProperty("LLM_analysis_input_location");

            String analysisOutputLocation = config.getProperties()
                    .getProperty("LLM_analysis_output_location");

            if (analysisInputLocation == null ||
                    analysisInputLocation.isBlank()) {
                throw new IllegalArgumentException(
                        "Missing config property: LLM_analysis_input_location");
            }

            if (analysisOutputLocation == null ||
                    analysisOutputLocation.isBlank()) {
                throw new IllegalArgumentException(
                        "Missing config property: LLM_analysis_output_location");
            }

            runLNFAnalysisStage(
                    config,
                    screenshotRoot,
                    Path.of(analysisInputLocation),
                    Path.of(analysisOutputLocation),
                    openAIClient);

        } else {
            throw new IllegalArgumentException(
                    "Invalid LLM_stage: " + llmStage
                            + ". Expected 1, 2, or 3.");
        }
    }

    /**
     * Stage 1: Infer meaningful navigation order.
     */
    private void runNavigationOrderStage(
            LoadConfig config,
            Path screenshotRoot,
            Path navigationOutputRoot,
            OpenAIClient openAIClient) throws Exception {

        NavigationOrderInference navOrderInference =
                new NavigationOrderInference(openAIClient);

        for (String subject : config.getSubjects()) {
            System.out.println("Subject: " + subject);

            List<String> elements =
                    KFGElementReader.readElements(
                            config,
                            subject);

            List<Path> screenshots =
                    ScreenshotReader.readScreenshots(
                            screenshotRoot,
                            subject);

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

            System.out.println("Navigation order result:");
            System.out.println(navigationOrder);
            System.out.println("Saved navigation output to:");
            System.out.println(savedOutput);
        }
    }

    /**
     * Stage 2: Detect suspected Linear Navigation Failures.
     */
    private void runLNFDetectionStage(
            LoadConfig config,
            Path screenshotRoot,
            Path navigationOutputRoot,
            Path lnfOutputRoot,
            OpenAIClient openAIClient) throws Exception {

        LNFDetection lnfDetection =
                new LNFDetection(openAIClient);

        for (String subject : config.getSubjects()) {
            System.out.println("Subject: " + subject);

            List<Path> screenshots =
                    ScreenshotReader.readScreenshots(
                            screenshotRoot,
                            subject);

            String navigationOrderJson =
                    NavigationOutputReader.read(
                            navigationOutputRoot,
                            subject);

            System.out.println("Screenshots: " + screenshots.size());
            System.out.println("Loaded existing navigation order.");
            System.out.println("Detecting Linear Navigation Failures...");

            String lnfResponse;

            try {
                lnfResponse = lnfDetection.detect(
                        subject,
                        navigationOrderJson,
                        screenshots);

            } catch (Exception e) {
                System.err.println(
                        "FAILED LNF detection for subject: " + subject);
                System.err.println("Reason: " + e.getMessage());
                System.err.println("Continuing to next subject...");
                continue;
            }

            Path savedOutput = LNFOutputSaver.save(
                    lnfOutputRoot,
                    subject,
                    lnfResponse);

            System.out.println("LNF detection result:");
            System.out.println(lnfResponse);
            System.out.println("Saved LNF output to:");
            System.out.println(savedOutput);
        }
    }

    /**
     * Stage 3: Independently analyze TP, FP, and FN transitions.
     */
    private void runLNFAnalysisStage(
            LoadConfig config,
            Path screenshotRoot,
            Path analysisInputRoot,
            Path analysisOutputRoot,
            OpenAIClient openAIClient) throws Exception {

        LNFPatternAnalysis patternAnalysis =
                new LNFPatternAnalysis(openAIClient);

        for (String subject : config.getSubjects()) {
            System.out.println("Subject: " + subject);

            Path analysisInputFile = analysisInputRoot
                    .resolve(subject)
                    .resolve(subject + ".json");

            List<Path> screenshots =
                    ScreenshotReader.readScreenshots(
                            screenshotRoot,
                            subject);

            System.out.println("Screenshots: " + screenshots.size());
            System.out.println("Analyzing TP, FP, and FN patterns...");

            try {
                String analysisResponse = patternAnalysis.analyze(
                        subject,
                        analysisInputFile,
                        screenshots);

                Path savedOutput = LNFAnalysisOutputSaver.save(
                        analysisOutputRoot,
                        subject,
                        analysisResponse);

                System.out.println(
                        "Stage 3 analysis completed for: " + subject);
                System.out.println("Saved analysis to:");
                System.out.println(savedOutput);

            } catch (Exception e) {
                System.err.println(
                        "FAILED Stage 3 analysis for: " + subject);
                System.err.println("Reason: " + e.getMessage());
                System.err.println("Continuing to next subject...");
            }
        }
    }
}