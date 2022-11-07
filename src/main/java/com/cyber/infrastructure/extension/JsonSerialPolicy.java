package com.cyber.infrastructure.extension;

import com.alibaba.fastjson.JSONObject;
import com.alicp.jetcache.CacheValueHolder;
import com.alicp.jetcache.anno.SerialPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class JsonSerialPolicy implements SerialPolicy {
    private static Logger LOG = LoggerFactory.getLogger(JsonSerialPolicy.class);

    @Override
    public Function<Object, byte[]> encoder() {
        return o -> {
            if (o != null) {
                CacheValueHolder cacheValueHolder = (CacheValueHolder) o;
                Object realObj = cacheValueHolder.getValue();
                String objClassName = realObj.getClass().getName();
                JsonCacheObject jsonCacheObject = new JsonCacheObject(objClassName, realObj);
                cacheValueHolder.setValue(jsonCacheObject);
                return JSONObject.toJSONString(cacheValueHolder).getBytes(StandardCharsets.UTF_8);
            }
            return new byte[0];
        };
    }

    @Override
    public Function<byte[], Object> decoder() {
        return bytes -> {
            if (bytes != null) {
                try {
                    String str = new String(bytes, StandardCharsets.UTF_8);
                    CacheValueHolder cacheValueHolder = JSONObject.parseObject(str, CacheValueHolder.class);
                    JSONObject jsonObject = JSONObject.parseObject(str);
                    JSONObject jsonOfMy = jsonObject.getJSONObject("value");
                    if (jsonOfMy != null) {
                        JSONObject realObjOfJson = jsonOfMy.getJSONObject("object");
                        String className = jsonOfMy.getString("className");

                        Object realObj = realObjOfJson.toJavaObject(Class.forName(className));
                        cacheValueHolder.setValue(realObj);
                        return cacheValueHolder;
                    }
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }
            return null;
        };
    }
}
