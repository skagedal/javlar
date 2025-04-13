package skagedal.javlar.web;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import skagedal.javlar.domain.model.LibraryInfo;
import skagedal.javlar.domain.model.ScmLink;

import java.util.List;
import java.util.stream.Collectors;

import static skagedal.javlar.web.Html.html;

public class DirectoryView {
    public @NotNull @Language("html") String render(List<LibraryInfo> favoriteWithInfos) {
        return html("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <title>Java Directory</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: system-ui, sans-serif;">
            <div style="max-width: 800px; margin: 0 auto; padding: 0 20px;">
                <h1>Java Directory</h1>
                <p>Welcome to the Java Directory!</p>
                <ul style="padding: 0; margin: 0;">
                    %s
                </ul>
            </div>
            </body>
            </html>
            """, renderLibraries(favoriteWithInfos));
    }

    private @NotNull @Language("html") String renderLibraries(List<LibraryInfo> favoriteWithInfos) {
        return favoriteWithInfos.stream()
            .map(this::renderLibrary)
            .collect(Collectors.joining());
    }

    private @NotNull String renderLibrary(LibraryInfo libraryInfo) {
        final var coordinates = libraryInfo.coordinates();
        return html("""
                <li style="border: 1px solid #ccc; border-radius: 10px; padding: 10px; margin: 10px 0; list-style-type: none; display: flex;">
                    <div style="flex: 2;">
                        <h2 style="margin-top: 0; padding-top: 0;"><a href="%s">%s</a></h2>
                        <section class="section">%s</section>
                    </div>
                    <div style="flex: 1; border-left: 1px solid #ddd; padding-left: 15px; margin-left: 15px;">
                        <div>%s</div>
                        <div>Latest: %s</div>
                        %s
                        %s
                        %s
                    </div>
                </li>
                """,
            "/web/library/%s/%s".formatted(coordinates.groupId(), coordinates.artifactId()),
            coordinates.artifactId(),
            libraryInfo.additionalData() != null ? formatDescription(libraryInfo) : "",
            coordinates.groupId(),
            coordinates.version(),
            mavenCentralLink(libraryInfo),
            homepageLink(libraryInfo),
            sourceControl(libraryInfo)
        );
    }

    // https://unpkg.com/lucide-static@latest/icons/house.svg

    private String mavenCentralLink(LibraryInfo libraryInfo) {
        final var coordinates = libraryInfo.coordinates();
        return "<a href=\"https://central.sonatype.com/artifact/%s/%s\">Maven Central</a>"
            .formatted(coordinates.groupId(), coordinates.artifactId());
    }

    private String sourceControl(LibraryInfo libraryInfo) {
        return switch (libraryInfo.scmLink()) {
            case ScmLink.AsUri uri -> "<a href=\"%s\">Github</a>".formatted(uri.uri());
            case ScmLink.AsString s -> s.string();
            case null -> "";
        };
    }

    private String homepageLink(LibraryInfo libraryInfo) {
        final var uri = libraryInfo.getUriForHomepage();
        if (uri != null) {
            return "<a href=\"%s\">Homepage</a>".formatted(uri);
        } else {
            return "";
        }
    }

    private String formatDescription(LibraryInfo libraryInfo) {
        String description = libraryInfo.additionalData().description();
        return markdownToHtml(description);
    }

    private String markdownToHtml(String markdown) {
        final var options = new MutableDataSet();
        final var parser = Parser.builder(options).build();
        final var renderer = HtmlRenderer.builder(options).build();

        final var document = parser.parse(markdown);
        return renderer.render(document);
    }

}
