package com.cyber;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cyber.constant.JWTTokenKey;
import com.cyber.entity.ESRequest;
import com.cyber.utils.ThreadLocals;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchClient {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClient.class);

    @Autowired
    RestClient restClient;

    public JSONObject search(ESRequest request) {
        if(StringUtils.isEmpty(request.getMethod())
                || StringUtils.isEmpty(request.getEndpoint())
                || StringUtils.isEmpty(request.getBody())) {
            LOG.error("Elastic Search Client Search Error , Miss Important Request Attribute ... ");
            return null;
        }

        try {
            Request esRequest = request.toRequest();
            esRequest.setEntity(new NStringEntity(request.getBody(), ContentType.APPLICATION_JSON));
            setEsheader(esRequest);

            Response response = restClient.performRequest(esRequest);

            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200) {
                String bodyString = EntityUtils.toString(response.getEntity());
                return JSONObject.parseObject(bodyString);
            }
        } catch (Exception exception) {
            LOG.error("Elastic Search Client Search Error, Exception {} ...", exception);
            LOG.error("Elastic Search Client Search Error, Request {} ...", JSON.toJSONString(request));
        }
        return null;
    }

    public void asysearch(ESRequest request,ResponseListener listener) {
        if(StringUtils.isEmpty(request.getMethod())
                || StringUtils.isEmpty(request.getEndpoint())
                || StringUtils.isEmpty(request.getBody())
                || listener == null) {
            LOG.error("Elastic Search Client Async Search Error , Miss Important Request Attribute ... ");
            return;
        }

        try {
            Request esRequest = request.toRequest();
            esRequest.setEntity(new NStringEntity(request.getBody(), ContentType.APPLICATION_JSON));
            setEsheader(esRequest);

            restClient.performRequestAsync(esRequest,listener);
        } catch (Exception exception) {
            LOG.error("Elastic Search Client Async Search Error, Request {} ...", JSON.toJSONString(request));
        }
        return;
    }

    public void setEsheader(Request request) {
        if(ThreadLocals.get(JWTTokenKey.X_CLIENT_JWT_TOKEN) != null) {
            String tokenString =  (String)ThreadLocals.get(JWTTokenKey.X_CLIENT_JWT_TOKEN);
            LOG.info(" Elastic Search Client Interceptor , Check The JWT_TOKEN_HEADER {} ...",tokenString);
            request.getOptions().getHeaders().add(new BasicHeader(JWTTokenKey.X_CLIENT_JWT_TOKEN,tokenString));
        }
    }
}
