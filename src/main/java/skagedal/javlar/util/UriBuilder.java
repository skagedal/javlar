package skagedal.javlar.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UriBuilder {
    private String scheme;
    private String userInfo;
    private String host;
    private int port;
    private String path;
    private String query;
    private String fragment;

    private UriBuilder(
        String scheme,
        String userInfo,
        String host,
        int port,
        String path,
        String query,
        String fragment
    ) {
        this.scheme = scheme;
        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
    }

    public static UriBuilder fromUri(URI uri) {
        return new UriBuilder(
            uri.getScheme(),
            uri.getUserInfo(),
            uri.getHost(),
            uri.getPort(),
            uri.getPath(),
            uri.getQuery(),
            uri.getFragment()
        );
    }

    public UriBuilder scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public UriBuilder userInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public UriBuilder host(String host) {
        this.host = host;
        return this;
    }

    public UriBuilder port(int port) {
        this.port = port;
        return this;
    }

    public UriBuilder path(String path) {
        this.path = path;
        return this;
    }

    public UriBuilder query(String query) {
        this.query = query;
        return this;
    }

    public UriBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public URI build() {
        try {
            return new URI(scheme, userInfo, host, port, path, query, fragment);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
