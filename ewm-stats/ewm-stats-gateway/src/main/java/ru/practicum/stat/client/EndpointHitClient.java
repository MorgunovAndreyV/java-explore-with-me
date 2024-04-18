package ru.practicum.stat.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.endpointhit.EndpointHitDto;
import ru.practicum.viewstat.ViewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EndpointHitClient extends BaseClient {

    private static final String API_PREFIX = "";

    @Autowired
    public EndpointHitClient(RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://stats-server:9090" + API_PREFIX))
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

    public ResponseEntity<List<ViewStatDto>> getStatsExplicit(String start, String end, String[] uris, Boolean unique) {
        StringBuilder path =
                new StringBuilder("/stats?start=" + URLEncoder.encode(start) + "&end=" + URLEncoder.encode(end));

        if (uris != null && uris.length > 0) {
            path.append("&").append(arrayToQueryString("uris", uris));

        }

        if (unique != null) {
            path.append("&unique=").append(unique);
        }

        return getExplicit(path.toString());
    }

    public String arrayToQueryString(String arrayKey, String[] stringArray) {
        if (stringArray == null) {

            return "";
        } else if (stringArray.length == 0) {

            return "";
        } else {

            return arrayKey + "=" + StringUtils.arrayToDelimitedString(stringArray, "&" + arrayKey + "=");
        }

    }

    public ResponseEntity<Object> registerHit(String appTitle, HttpServletRequest request) {
        ResponseEntity<Object> test = addNew(new EndpointHitDto(appTitle, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));
        return test;
    }

}
