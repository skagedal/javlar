package skagedal.javlar;

import skagedal.javlar.domain.JavaDirectoryService;
import skagedal.javlar.domain.data.StaticData;
import skagedal.javlar.util.LogbackConfig;

import java.io.PrintStream;

public class Application {
    private final String[] args;

    @SuppressWarnings("java:S106")
    private static PrintStream out() {
        return System.out;
    }

    public Application(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) {
        new Application(args).run();
    }

    private void run() {
        if (args.length == 0) {
            out().println("Give a command");
            return;
        }
        switch (args[0]) {
            case "server" -> {
                final var serverApp = new ServerApp();
                serverApp.run();
            }
            case "list" -> {
                LogbackConfig.setLogLevelToWarn();
                final var service = JavaDirectoryService.create();

                for (var library : StaticData.libraries()) {
                    final var coordinates = service.fetchInfo(library).coordinates();
                    out().printf("%s:%s:%s%n", coordinates.groupId(), coordinates.artifactId(), coordinates.version());
                }
            }
            case "describe" -> {
                final var service = JavaDirectoryService.create();
                final var artifactId = args[1];
                final var library = StaticData.libraries()
                    .stream().filter(p -> p.artifactId().contains(artifactId))
                    .findFirst().orElseThrow();
                final var libraryInfo = service.fetchInfo(library);
                final var coordinates = libraryInfo.coordinates();
                out().printf("%s:%s:%s%n", coordinates.groupId(), coordinates.artifactId(), coordinates.version());
                for (var artifact : service.artifacts(libraryInfo)) {
                    out().println(artifact);
                }
            }
            default -> out().println("Unknown command: " + args[0]);
        }
    }


}
