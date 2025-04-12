package skagedal.javlar.cli;

public sealed interface Command {
    record Serve() implements Command {
    }

    record List() implements Command {
    }

    record Describe(String artifactId) implements Command {
    }

    record Help() implements Command {
        public static final String NAME = "help";
    }
}
