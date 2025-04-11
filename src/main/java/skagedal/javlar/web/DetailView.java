package skagedal.javlar.web;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import skagedal.javlar.domain.model.LibraryInfo;

import static skagedal.javlar.web.Html.html;

public class DetailView {

    public @NotNull @Language("html") String render(LibraryInfo libraryInfo) {
        final var coorinates = libraryInfo.coordinates();
        return html("""
            
                <!DOCTYPE html>
            <html lang="en">
            <head>
                <title>Java Directory</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: system-ui, sans-serif;">
            <div style="max-width: 800px; margin: 0 auto; padding: 0 20px;">
                <div style="flex: 2;">
                    <h2>%s</h2>
                </div>
                <div style="flex: 1; border-left: 1px solid #ddd; padding-left: 15px; margin-left: 15px;">
                    <div>%s</div>
                    <div>Latest: %s</div>
                </div>
            
            </div>
            </body>
            </html>
            """,
            coorinates.artifactId(),
            coorinates.groupId(),
            coorinates.version()
            );
    }
}
