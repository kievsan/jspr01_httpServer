package ru.mail.knhel7;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IRequest {
    public default String getRequest() {
        var delimiter = "\r\n";
        return  requestString() + delimiter +
                headersString() + delimiter +
                bodyString();
    }
    public void parse(InputStream stream) throws IOException;
    public boolean isBadRequest();

    public String requestString();
    public String headersString();
    public String bodyString();

    public String method();
    public String path();
    public String queryString();
    public String version();

    public Map<String, String> queryStringParams();
    public Map<String, String> headers();
//    public Map<String, String> bodyParams();
}
