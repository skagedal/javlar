package skagedal.javlar.cli;

import picocli.CommandLine;
import picocli.CommandLine.Model.*;

/// Takes care of parsing the command line, and only that. Not super-happy about how the implementation turned
/// out, PicoCli is optimized for "let me take care of everything", but it's probably possible to do better than this.
public class Cli {
    private final CommandSpec listCommand = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .name("list");
    private final CommandSpec serveCommand = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .name("serve");
    private final CommandSpec describeCommand = subcommand()
        .name("describe")
        .addPositional(CommandLine.Model.PositionalParamSpec.builder()
            .paramLabel("UNVERSIONED_COORDINATES")
            .type(String.class)
            .description("For example org.jspecify:jspecify")
            .build());
    private final CommandSpec searchCommand = subcommand()
        .name("search");

    private final CommandSpec spec = CommandSpec.create()
        .mixinStandardHelpOptions(true)
        .addSubcommand("serve", serveCommand)
        .addSubcommand("list", listCommand)
        .addSubcommand("describe", describeCommand)
        .addSubcommand("search", searchCommand);

    private final CommandLine commandLine = new CommandLine(spec);

    public Command parse(final String[] args) {
        final var parsed = commandLine.parseArgs(args);
        if (parsed.subcommand() instanceof CommandLine.ParseResult command) {
            if (command.isUsageHelpRequested()) {
                return new Command.Help(command.commandSpec());
            }
            if (command.commandSpec() == listCommand) {
                return new Command.List();
            } else if (command.commandSpec() == serveCommand) {
                return new Command.Serve();
            } else if (command.commandSpec() == describeCommand) {
                return new Command.Describe(command.matchedPositional(0).getValue());
            } else if (command.commandSpec() == searchCommand) {
                return new Command.Search(command.matchedPositional(0).getValue());
            }
            throw new IllegalStateException("The programmer made a mistake here.");
        } else {
            return new Command.Help(spec);
        }
    }

    public void printHelp(final Command.Help help) {
        new CommandLine(help.spec()).usage(System.out);
    }

    private static CommandSpec subcommand() {
        return CommandSpec.create()
            .mixinStandardHelpOptions(true);
    }
}
