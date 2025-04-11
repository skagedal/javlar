package skagedal.javlar.domain.model;

import java.util.List;

public record ListPackagesResponse(
    List<LibraryInfo> libraries
) {
}
