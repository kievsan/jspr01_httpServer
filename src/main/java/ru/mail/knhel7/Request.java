package ru.mail.knhel7;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class Request implements IRequest {

    public static final char QUERY_DELIMITER = '?';
    public static final String STRING_DELIMITER = "\r\n";

    protected final Map<String, String> requestTXT = new ConcurrentHashMap<>();
        public static final String STRING = "requestString";
        public static final String HEADERS = "headersString";
        public static final String BODY = "bodyString";
        public static final String METHOD = "method";
        public static final String QUERY = "queryString";
        public static final String VERSION = "version";
        public static final String PATH = "path";
        public static final String QUERY_PARAMS = "queryParamsString";

    protected final Map<String, String> queryParams = new ConcurrentHashMap<>();
    protected final Map<String, String> headers = new ConcurrentHashMap<>();
//    protected final Map<String, String> bodyParams = new ConcurrentHashMap<>();

    protected boolean badRequest = true;

    public String toString() {
        return  getRequest();
    }

    @Override
    public String requestString() {
        return requestTXT.get(STRING);
    }

    @Override
    public String headersString() {
        return requestTXT.get(HEADERS);
    }

    @Override
    public String bodyString() {
        return requestTXT.get(BODY);
    }

    @Override
    public boolean isBadRequest() {
        return badRequest;
    }


    @Override
    public String method() {
        return requestTXT.get(METHOD);
    }

    @Override
    public String path() {
        return requestTXT.get(PATH);
    }

    @Override
    public String queryString() {
        return requestTXT.get(QUERY);
    }

    @Override
    public String version() {
        return requestTXT.get(VERSION);
    }


    @Override
    public Map<String, String> queryStringParams() {
        return queryParams;
    }

    public Map<String, String> headers() {
        return headers;
    }

//    @Override
//    public Map<String, String> bodyParams() {
//        return bodyParams;
//    }

}
