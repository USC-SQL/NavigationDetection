package edu.usc;

import edu.usc.Strategy.LYNX;
import edu.usc.Strategy.NewStrategy;
import edu.usc.Strategy.NavigationStrategy;
import edu.usc.Utilities.LoadConfig;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/** Selects a workflow strategy; the legacy Main remains available unchanged. */
public final class CrossPlatformMain {

    private CrossPlatformMain() { }

    public static void main(String[] args) throws Exception {
        LoadConfig config = new LoadConfig(configPath(args).toString());
        strategy(config).run(config);
    }

    private static NavigationStrategy strategy(LoadConfig config) {
        Properties properties = config.getProperties();
        String strategy = properties.getProperty("strategy", "lynx")
                .trim().toLowerCase(Locale.ROOT);

        return switch (strategy) {
            case "lynx" -> new LYNX(LYNX.Operation.fromName(
                    properties.getProperty("lynx_operation", "all")));
            case "new" -> new NewStrategy();
            default -> throw new IllegalArgumentException(
                    "Unsupported strategy: " + strategy);
        };
    }

    private static Path configPath(String[] args) {
        if (args.length == 0) {
            return Path.of("config.txt");
        }
        if (args.length == 2 && args[0].equals("--config")) {
            return Path.of(args[1]);
        }
        throw new IllegalArgumentException(
                "Usage: CrossPlatformMain [--config <path>]");
    }
}
