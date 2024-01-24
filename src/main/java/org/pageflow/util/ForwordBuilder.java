package org.pageflow.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : sechan
 */
public class ForwordBuilder {
    
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String forwordUrl;
    private final Map<String, String> params;
    
    public ForwordBuilder(HttpServletRequest request, HttpServletResponse response, String forwordUrl) {
        this.request = request;
        this.response = response;
        this.forwordUrl = forwordUrl;
        this.params = new HashMap<>();
    }
    
    public ForwordBuilder param(String key, String value){
        params.put(key, value);
        return this;
    }
    
    public ForwordBuilder param(String key, int value){
        params.put(key, String.valueOf(value));
        return this;
    }
    
    public ForwordBuilder param(String key, long value){
        params.put(key, String.valueOf(value));
        return this;
    }
    
    public ForwordBuilder param(String key, boolean value){
        params.put(key, String.valueOf(value));
        return this;
    }
    
    public void forward(){
        StringBuilder sb = new StringBuilder();
        sb.append(forwordUrl);
        sb.append("?");
        for (String key : params.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(params.get(key));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        
        // url 완성
        String url = sb.toString();
        
        // forward
        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException("'" + url + "'로 forward 실패: " + e.getMessage());
        }
        
    }
}
