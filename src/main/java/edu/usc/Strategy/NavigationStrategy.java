package edu.usc.Strategy;

import edu.usc.Utilities.LoadConfig;

public interface NavigationStrategy {
    void run(LoadConfig config) throws Exception;
}
