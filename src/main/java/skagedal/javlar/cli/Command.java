package skagedal.javlar.cli;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

public sealed interface Command {
    record Serve() implements Command {
    }

    record List() implements Command {
    }

    record Describe(String artifactId) implements Command {
    }

    record Search(String query) implements Command {
    }

    record Help(CommandSpec spec) implements Command {
    }
}
