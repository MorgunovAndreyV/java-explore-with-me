package ru.practicum.endpointhit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;

import java.net.URL;
import java.net.URLEncoder;

@Service
public class EndpointHitClient extends BaseClient {

    private static final String API_PREFIX = "";

    @Autowired
    public EndpointHitClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addNew(EndpointHitDto endpointHitDto) {

        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, String[] uris, Boolean unique) {
        StringBuilder path =
                new StringBuilder("/stats?start=" + URLEncoder.encode(start) + "&end=" + URLEncoder.encode(end));

        if (uris != null && uris.length > 0) {
            path.append("&").append(arrayToQueryString("uris", uris));

        }

        if (unique != null) {
            path.append("&unique=").append(unique);
        }

        return get(path.toString());
    }

    public String arrayToQueryString(String arrayKey, String[] stringArray) {
        if (stringArray == null) {

            return "";
        } else if (stringArray.length == 0) {

            return "";
        } else {

            return arrayKey+ "="+ StringUtils.arrayToDelimitedString(stringArray, "&"+arrayKey+"=");
        }

    }

}
