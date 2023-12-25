package ru.mail.knhel7;

import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestInString extends Request {

    private final List<String> validMethods;
    private final List<String> validPaths;

    public RequestInString(List<String> validMethods, List<String> validPaths) {
        this.validMethods = validMethods;
        this.validPaths = validPaths;
    }

    @Override
    public void parse(InputStream stream) throws IOException {
        final int limit = 4096;
        stream.mark(limit);
        final var reader = new BufferedReader(new InputStreamReader(stream));

        parseRequestString(reader, validMethods, validPaths);
        parseHeadersString(reader);
        parseBodyString(reader);
    }

    public void parseRequestString(BufferedReader reader,
                                   List<String> validMethods,
                                   List<String> validPaths) throws IOException {
        final var validMethodList = new CopyOnWriteArrayList<>(validMethods);
        final var validPathList = new CopyOnWriteArrayList<>(validPaths);

        requestTXT.put(STRING, reader.readLine());

        final var parts = new CopyOnWriteArrayList<>(requestTXT.get(STRING).split(" "));
        if (parts.size() != 3) return;

        requestTXT.put(METHOD, parts.getFirst());
        if (!validMethodList.contains(requestTXT.get(METHOD))) return;

        requestTXT.put(QUERY, parts.get(1));

        var index = new AtomicInteger(requestTXT.get(QUERY).indexOf(QUERY_DELIMITER));
        if (index.intValue() == -1) {
            requestTXT.put(PATH, requestTXT.get(QUERY));
        } else {
            requestTXT.put(PATH, requestTXT.get(QUERY).substring(0, index.get()));
            requestTXT.put(QUERY_PARAMS, requestTXT.get(QUERY).substring(index.incrementAndGet()));
            new CopyOnWriteArrayList<>(URLEncodedUtils.parse(
                    requestTXT.get(QUERY_PARAMS), StandardCharsets.UTF_8, QUERY_DELIMITER)
            ).forEach(pair -> queryParams.put(pair.getName(), pair.getValue()));
        }
        if (!validPathList.contains(requestTXT.get(PATH))) return;

        requestTXT.put(VERSION, parts.get(2));

        badRequest = false;
    }

    public void parseHeadersString(BufferedReader reader) {
        final var newHeadersString = new CopyOnWriteArrayList<String>();
        while (true) {
            try {
                var header = reader.readLine();
                if (header == null || header.isEmpty()) break;
                newHeadersString.add(header);
                var pairList = new CopyOnWriteArrayList<>(header.split(": "));
                headers.put(pairList.getFirst(), pairList.getLast());
            } catch (IOException e) {
                break;
            }
        }
        if (newHeadersString.isEmpty()) return;
        requestTXT.put(HEADERS, String.join(STRING_DELIMITER, newHeadersString));
    }

    public void parseBodyString(BufferedReader reader) {
        final var newBodyString = new CopyOnWriteArrayList<String>();
        while (true) {
            var body = "";
            try {
                body = reader.readLine();
                if (body == null || body.isEmpty()) break;
                newBodyString.add(body);
            } catch (IOException e) {
                break;
            }
        }
        if (newBodyString.isEmpty()) return;
        requestTXT.put(BODY, String.join(STRING_DELIMITER, newBodyString));
    }

}
