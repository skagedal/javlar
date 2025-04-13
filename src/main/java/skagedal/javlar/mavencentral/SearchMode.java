package skagedal.javlar.mavencentral;

public sealed interface SearchMode {
    record LatestVersion() implements SearchMode { }
    record AllVersions() implements SearchMode { }
}
