package com.cyber.infrastructure.config;

public class JsonCacheObject<V> {
    private String className;
    private V object;

    public JsonCacheObject() {
    }

    public JsonCacheObject(String className, V object) {
        this.className = className;
        this.object = object;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public V getObject() {
        return object;
    }

    public void setObject(V object) {
        this.object = object;
    }
}
