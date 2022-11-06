package com.cyber;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DefaultResponseListener implements ResponseListener {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClient.class);

    @Override
    public void onSuccess(Response response) {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200) {
                String bodyString = EntityUtils.toString(response.getEntity());
                esSuccess(JSONObject.parseObject(bodyString));
            }
        } catch (Exception exception) {
            LOG.error("Elastic Search Client Async Search Error ...");
            onFailure(exception);
        }
    }

    @Override
    public void onFailure(Exception exception) {
        LOG.error("Elastic Search Client Async Search Error ... ",exception);
    }

    public abstract JSONObject esSuccess(JSONObject jsonObject);
}
