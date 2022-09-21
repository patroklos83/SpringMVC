package com.patroclos.controller.core;

import java.util.HashMap;
import java.util.Queue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ScopedProxyMode;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DataHolder {

    private Object data;
    private HashMap<String, Object> dataMap;

    public DataHolder() {
        data = null;
        dataMap = new HashMap<String, Object>();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    public void addDataToMap(String key, Object data) {
        this.dataMap.put(key, data);
    }
    
    public Object getDataFromMap(String key) {
        return this.dataMap.get(key);
    }
    
    public Object removeDataFromMap(String key) {
        return this.dataMap.remove(key);
    }
}