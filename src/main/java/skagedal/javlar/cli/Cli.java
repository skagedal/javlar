package skagedal.javlar.cli;

import picocli.CommandLine;
import picocli.CommandLine.Model.*;

import java.util.concurrent.atomic.AtomicReference;

/// Takes care of parsing the command line, and only that. Not super-happy about how the implementation turned
/// out, PicoCli is optimized for "let me take care of everything", but it's probably possible to do better than this.
public class Cli {
    private final CommandSpec serveCommand = CommandSpec.create()
        .name("serve");
    private final CommandSpec listCommand = CommandSpec.create()
        .name("list");
    private final CommandSpec describeCommand = CommandSpec.create()
        .name("describe")
        .addPositional(CommandLine.Model.PositionalParamSpec.builder()
            .paramLabel("ARTIFACT_ID")
            .type(String.class)
            .description("the artifact ID to describe")
            .build());
    private final CommandSpec spec = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .addSubcommand("serve", serveCommand)
        .addSubcommand("list", listCommand)
        .addSubcommand("describe", describeCommand);
    private final CommandLine commandLine = new CommandLine(spec);

    public Command parse(final String[] args) {
        final var parsed = commandLine.parseArgs(args);
        if (parsed.subcommand() instanceof CommandLine.ParseResult command) {
            if (command.commandSpec() == listCommand) {
                return new Command.List();
            } else if (command.commandSpec() == serveCommand) {
                return new Command.Serve();
            } else if (command.commandSpec() == describeCommand) {
                return new Command.Describe(command.matchedPositional(0).getValue());
            }
            throw new IllegalStateException("The programmer made a mistake here.");
        } else {
            return new Command.Help();
        }
    }

    public void printHelp() {
        commandLine.usage(System.out);
    }
}
