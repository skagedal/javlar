package skagedal.javlar.mavencentral;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MavenCentralResponse(
    @JsonProperty("responseHeader") ResponseHeader responseHeader,
    @JsonProperty("response") Response response,
    @JsonProperty("spellcheck") SpellCheck spellcheck
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ResponseHeader(
        @JsonProperty("status") int status,
        @JsonProperty("QTime") int QTime,
        @JsonProperty("params") Params params
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Params(
            @JsonProperty("q") String q,
            @JsonProperty("core") String core,
            @JsonProperty("indent") String indent,
            @JsonProperty("spellcheck") String spellcheck,
            @JsonProperty("fl") String fl,
            @JsonProperty("start") String start,
            @JsonProperty("spellcheck.count") String spellcheck_count,
            @JsonProperty("sort") String sort,
            @JsonProperty("rows") String rows,
            @JsonProperty("wt") String wt,
            @JsonProperty("version") String version
        ) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
        @JsonProperty("numFound") int numFound,
        @JsonProperty("start") int start,
        @JsonProperty("docs") List<Document> docs
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Document(
            @JsonProperty("id") String id,
            @JsonProperty("g") String group,
            @JsonProperty("a") String artifact,
            @JsonProperty("latestVersion") String latestVersion,
            @JsonProperty("repositoryId") String repositoryId,
            @JsonProperty("p") String p,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("versionCount") int versionCount,
            @JsonProperty("text") List<String> text,
            @JsonProperty("ec") List<String> suffixes
        ) {
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpellCheck(
        @JsonProperty("suggestions") List<String> suggestions
    ) {
    }
}