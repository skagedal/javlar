package skagedal.javlar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class implements a body handler for the Java 11 HttpClient that uses Jackson to serialize and deserialize.
 */
public class BodyMapper {
    private final ObjectMapper objectMapper;

    public BodyMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> HttpResponse.BodyHandler<T> receiving(Class<T> klass) {
        return new ResponseHandler<>(klass, objectMapper);
    }

    public <T> HttpRequest.BodyPublisher sending(T body) {
        try {
            return HttpRequest.BodyPublishers.ofByteArray(objectMapper.writeValueAsBytes(body));
        } catch (JsonProcessingException exception) {
            throw new BodyMapperException(exception);
        }
    }

    private record ResponseHandler<Type>(
        Class<Type> klass,
        ObjectMapper mapper
    ) implements HttpResponse.BodyHandler<Type> {
        @Override
        public HttpResponse.BodySubscriber<Type> apply(HttpResponse.ResponseInfo responseInfo) {
            return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofInputStream(),
                this::deserializeInputStream
            );
        }

        private Type deserializeInputStream(InputStream inputStream) {
            try {
                return mapper.readValue(inputStream, klass);
            } catch (IOException exception) {
                throw new BodyMapperException(exception);
            }
        }
    }
}
