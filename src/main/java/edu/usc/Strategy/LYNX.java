package edu.usc.Strategy;

import edu.usc.LYNX.ContextTree.ContextTree;
import edu.usc.LYNX.KFG.UIGraph.UIGraphState;
import edu.usc.Utilities.LoadConfig;

import java.util.Locale;

import static edu.usc.LYNX.Detection.RunDetection.Detect;
import static edu.usc.LYNX.KFG.KFGUtilities.UtilityFunctions.LoadTheKFG;
import static edu.usc.LYNX.Helper.ContextTreeValidation.DrawContextTreeProxy.DrawMBRs;
import static edu.usc.LYNX.Helper.LNFValidation.DrawLNFLabels.DrawLabels;
import static edu.usc.LYNX.Helper.LNFValidation.ReviewFN.PrintFNs;
import static edu.usc.Utilities.SaveFiles.SaveXLSX;

/** The original LYNX workflow and its auxiliary operations. */
public final class LYNX implements NavigationStrategy {

    public enum Operation {
        ALL, CTREE, DETECT, DRAW_MBRS, DRAW_LABELS, PRINT_FNS;

        public static Operation fromName(String name) {
            return switch (name.toLowerCase(Locale.ROOT)) {
                case "all" -> ALL;
                case "ctree", "build-ctree" -> CTREE;
                case "detect", "detection" -> DETECT;
                case "draw-mbrs" -> DRAW_MBRS;
                case "draw-labels" -> DRAW_LABELS;
                case "print-fns" -> PRINT_FNS;
                default -> throw new IllegalArgumentException("Unknown LYNX operation: " + name);
            };
        }
    }

    private final Operation operation;

    public LYNX() {
        this(Operation.ALL);
    }

    public LYNX(Operation operation) {
        this.operation = operation;
    }

    @Override
    public void run(LoadConfig config) throws Exception {
        for (String subject : config.getSubjects()) {
            System.out.println("Subject: " + subject);
            System.out.println("Subject URL: " + config.getSubjectURL(subject));
            runOperation(config, subject);
        }
    }

    private void runOperation(LoadConfig config, String subject) throws Exception {
        switch (operation) {
            case ALL -> {
                buildContextTree(config, subject);
                Detect(config, subject);
            }
            case CTREE -> buildContextTree(config, subject);
            case DETECT -> Detect(config, subject);
            case DRAW_MBRS -> DrawMBRs(subject, config);
            case DRAW_LABELS -> DrawLabels(subject, config);
            case PRINT_FNS -> PrintFNs(subject, config);
        }
    }

    private static void buildContextTree(LoadConfig config, String subject) throws Exception {
        UIGraphState kfg = LoadTheKFG(config, subject);
        if (kfg == null) {
            throw new IllegalStateException("Unable to load KFG for subject: " + subject);
        }

        long startTime = System.nanoTime();
        ContextTree tree = new ContextTree();
        tree.Build(config, subject);
        tree.Save(config, subject);
        long durationSeconds = (System.nanoTime() - startTime) / 1_000_000_000;
        SaveXLSX(config, subject, durationSeconds, "CTree");
    }
}
