package skagedal.javlar.domain.model;

import org.jspecify.annotations.NullMarked;

import java.net.URI;

/// The scm link that we get from the POM is not necessarily an URI, it is sometimes for example a
/// git protocol reference thing.
@NullMarked
public sealed interface ScmLink {
    record AsUri(URI uri) implements ScmLink {}
    record AsString(String string) implements ScmLink {}

    static ScmLink parse(String string) {
        try {
            return new AsUri(URI.create(string));
        } catch (IllegalArgumentException e) {
            return new AsString(string);
        }
    }
}
