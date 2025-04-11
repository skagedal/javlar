package skagedal.javlar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skagedal.javlar.api.HealthController;
import skagedal.javlar.api.JavaDirectoryWebController;
import skagedal.javlar.api.RootController;
import skagedal.javlar.api.JavaDirectoryController;
import skagedal.javlar.domain.JavaDirectoryService;
import skagedal.javlar.domain.data.StaticData;

import java.io.IOException;
import java.util.List;

public class ServerApp {
    private static final Logger log = LoggerFactory.getLogger(ServerApp.class);

    public void run() {
        final var directoryService = JavaDirectoryService.create();
        loadDefaults(directoryService);

        final var controllers = List.of(
            new RootController.ControllerDescription("/web", new JavaDirectoryWebController((directoryService))),
            new RootController.ControllerDescription("/directory", new JavaDirectoryController(directoryService)),
            new RootController.ControllerDescription("/health", new HealthController())
        );
        try {
            final var server = new RootController(8080, controllers);
            server.start();
        } catch (IOException exception) {
            log.error("Failed to start server", exception);
        }
    }

    private void loadDefaults(JavaDirectoryService directoryService) {
        for (var library : StaticData.libraries()) {
            directoryService.createFavorite(library);
        }
    }
}
