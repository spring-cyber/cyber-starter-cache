package com.cyber.entity;

import org.elasticsearch.client.Request;

public class ESRequest {
    private String method;

    private String endpoint;

    private String body;

    public Request toRequest()  {
        Request request = new Request(this.method,this.endpoint);
        return request;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
