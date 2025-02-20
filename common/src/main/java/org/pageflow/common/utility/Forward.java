package org.pageflow.common.utility;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : sechan
 */
public class Forward implements ForwardPathSetter {

  @Getter
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  @Getter
  private String forwordUri;
  private final Map<String, String> params;


  private Forward(HttpServletRequest request, HttpServletResponse response) {
    this.request = request;
    this.response = response;
    this.params = new HashMap<>();
  }


  /**
   * 상속 받은 객체에서 request, response가 제공되지 않는 환경에서 Forward 객체를 생성할 때 사용
   * @param forwordUri
   */
  protected Forward(String forwordUri) {
    ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    assert servletRequest != null;
    this.request = servletRequest.getRequest();
    this.response = servletRequest.getResponse();
    this.forwordUri = forwordUri;
    this.params = new HashMap<>();
  }

  /**
   * 상속 받은 객체에서 사용할 때 쓰는 생성자
   */
  protected Forward(HttpServletRequest request, HttpServletResponse response, String forwordUri) {
    this.request = request;
    this.response = response;
    this.forwordUri = forwordUri;
    this.params = new HashMap<>();
  }

  public static ForwardPathSetter forward(HttpServletRequest request, HttpServletResponse response) {
    return new Forward(request, response);
  }

  /**
   * 되도록 사용하지 말 것.
   * request와 response 객체가 제공되지 않는 메소드에서 어쩔 수 없이 호출.
   * 단, 호출되는 컨텍스트가 반드시 http 요청 스레드임이 분명할 때 사용.
   *
   * @see Forward#forward(HttpServletRequest, HttpServletResponse) use this method instead
   */
  public static ForwardPathSetter confrimInServletRequestContext() {
    ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    assert servletRequest != null;
    return new Forward(servletRequest.getRequest(), servletRequest.getResponse());
  }


  public Forward param(String key, String value) {
    params.put(key, value);
    return this;
  }

  public Forward param(String key, int value) {
    params.put(key, String.valueOf(value));
    return this;
  }

  public Forward param(String key, long value) {
    params.put(key, String.valueOf(value));
    return this;
  }

  public Forward param(String key, boolean value) {
    params.put(key, String.valueOf(value));
    return this;
  }


  public Forward requestAttr(String key, Object value) {
    assert request != null;
    request.setAttribute(key, value);
    return this;
  }

  public void send() {
    StringBuilder sb = new StringBuilder();
    sb.append(forwordUri);
    sb.append("?");
    for(Map.Entry<String, String> entry : params.entrySet()){
      sb.append(entry.getKey());
      sb.append("=");
      sb.append(entry.getValue());
      sb.append("&");
    }
    sb.deleteCharAt(sb.length() - 1);

    // url 완성
    String url = sb.toString();

    // forward
    try {
      request.getRequestDispatcher(url).forward(request, response);
    } catch(ServletException e){
      throw new RuntimeException(e);
    } catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  @Override
  public Forward path(String path) {
    this.forwordUri = path;
    return this;
  }

  @Override
  public String toString() {
    return forwordUri;
  }

}

