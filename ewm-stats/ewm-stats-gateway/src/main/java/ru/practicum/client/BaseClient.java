package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.viewstat.ViewStatDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null, null);
    }

    protected ResponseEntity<List<ViewStatDto>> getExplicit(String path) {
        return makeAndSendRequestTest(HttpMethod.GET, path, null, null);
    }


    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    protected <T> ResponseEntity<Object> put(String path, T body) {
        return put(path, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null);
    }

    protected ResponseEntity<Object> delete(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> ewmServerResponse;
        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(ewmServerResponse);
    }

    private <T> ResponseEntity<List<ViewStatDto>> makeAndSendRequestTest(HttpMethod method, String path,
                                                                         @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<List<ViewStatDto>> ewmServerResponse;
        try {
            if (parameters != null) {
                ewmServerResponse = rest.exchange(path, method, requestEntity,
                        new ParameterizedTypeReference<List<ViewStatDto>>() {
                        }, parameters);
            } else {
                ewmServerResponse = rest.exchange(path, method, requestEntity, new ParameterizedTypeReference<List<ViewStatDto>>() {
                });
            }
        } catch (HttpStatusCodeException e) {
            log.error("Ошибка статуса ответа запроса клиента статистики: "
                    + Arrays.toString(e.getResponseBodyAsByteArray()));
            return ResponseEntity.status(e.getStatusCode()).body(null);

        }

        return prepareGatewayResponseDiamond(ewmServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private static <T> ResponseEntity<T> prepareGatewayResponseDiamond(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

}