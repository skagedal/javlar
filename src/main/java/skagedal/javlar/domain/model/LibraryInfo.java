package skagedal.javlar.domain.model;


import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.List;

public record LibraryInfo(
    FullCoordinates coordinates,
    List<String> suffixes,
    @Nullable AdditionalData additionalData,
    @Nullable URI homepage,
    @Nullable ScmLink scmLink
) {

    public @Nullable URI getUriForHomepage() {
        if (homepage != null) {
            return homepage;
        }
        if (additionalData != null && additionalData.homepage() != null) {
            return additionalData.homepage();
        }
        return null;
    }
}
