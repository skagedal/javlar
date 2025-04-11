package skagedal.javlar.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.glassfish.jersey.uri.UriTemplate;
import org.intellij.lang.annotations.Language;
import skagedal.javlar.domain.JavaDirectoryService;
import skagedal.javlar.domain.model.UnversionedCoordinates;
import skagedal.javlar.web.DetailView;
import skagedal.javlar.web.DirectoryView;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JavaDirectoryWebController implements HttpHandler {
    private final JavaDirectoryService javaDirectoryService;
    private final DirectoryView directoryView = new DirectoryView();
    private final DetailView detailView = new DetailView();

    private final UriTemplate libraryTemplate = new UriTemplate("/web/library/{groupId}/{artifactId}");

    public JavaDirectoryWebController(JavaDirectoryService javaDirectoryService) {
        this.javaDirectoryService = javaDirectoryService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        sendOk(exchange, renderPath(exchange.getRequestURI().getPath()));
    }

    private @Language("html") String renderPath(String path) {
        Map<String, String> params = new HashMap<>();
        if (libraryTemplate.match(path, params)) {
            final var coordinates = new UnversionedCoordinates(params.get("groupId"), params.get("artifactId"));
            return detailView.render(javaDirectoryService.fetchInfo(coordinates));
        } else {
            return directoryView.render(javaDirectoryService.listFavorites());
        }
    }

    private void sendOk(HttpExchange exchange, @Language("html") String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        sendResponse(exchange, 200, response.getBytes(StandardCharsets.UTF_8));
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] responseBytes) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

}