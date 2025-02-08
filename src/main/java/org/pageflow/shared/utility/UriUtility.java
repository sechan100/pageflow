package org.pageflow.shared.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author : sechan
 */
public abstract class UriUtility {
  // path를 받아서 맨 앞에 /가 없다면 붙이고, 맨 뒤에 /가 있다면 지운다.
  public static String addStartSlashAndRemoveEndSlash(final String path) {
    String p = path;
    if(p.endsWith("/")){
      p = p.substring(0, p.length() - 1);
    }
    if(!path.startsWith("/")){
      p = "/" + p;
    }
    return p;
  }

  @SuppressWarnings("MethodWithMoreThanThreeNegations")
  public static String encodeURI(String inputUri) {
    URI uri = null;
    try {
      uri = new URI(inputUri);
    } catch(URISyntaxException e){
      throw new RuntimeException(e);
    }
    StringBuilder encodedUri = new StringBuilder();

    // Base64 인코딩된 경로 부분 추가
    if(uri.getPath()!=null){
      String[] pathSegments = uri.getPath().split("/");
      for(String segment : pathSegments){
        if(!segment.isEmpty()){
          encodedUri.append("/");
          encodedUri.append(encode(segment));
        }
      }
    }

    // 쿼리 스트링 처리
    if(uri.getQuery()!=null){
      encodedUri.append("?");
      String[] queryParams = uri.getQuery().split("&");
      boolean isFirstParam = true;
      for(String param : queryParams){
        if(!isFirstParam){
          encodedUri.append("&");
        }
        String[] keyValue = param.split("=");
        encodedUri.append(encode(keyValue[0]));
        if(keyValue.length > 1){
          encodedUri.append("=");
          encodedUri.append(encode(keyValue[1]));
        }
        isFirstParam = false;
      }
    }
    return encodedUri.toString();
  }

  @SuppressWarnings({"MethodWithMoreThanThreeNegations", "ObjectAllocationInLoop"})
  public static String decodeURI(String encodedUri) {
    URI uri = null;
    try {
      uri = new URI(encodedUri);
    } catch(URISyntaxException e){
      throw new RuntimeException(e);
    }
    StringBuilder decodedUri = new StringBuilder();

    // Base64 디코딩된 경로 부분 추가
    if(uri.getPath()!=null){
      String[] pathSegments = uri.getPath().split("/");
      for(String segment : pathSegments){
        if(!segment.isEmpty()){
          if(!decodedUri.isEmpty()) decodedUri.append("/");
          decodedUri.append(decode(segment));
        }
      }
    }

    // 맨 앞에 "/"가 없다면 붙인다.
    if(!decodedUri.isEmpty() && !decodedUri.toString().startsWith("/")){
      decodedUri.insert(0, "/");
    }

    // 쿼리 스트링 디코딩 처리
    if(uri.getQuery()!=null){
      decodedUri.append("?");
      String[] queryParams = uri.getQuery().split("&");
      boolean isFirstParam = true;
      for(String param : queryParams){
        if(!isFirstParam){
          decodedUri.append("&");
        }
        String[] keyValue = param.split("=");
        decodedUri.append(decode(keyValue[0]));
        if(keyValue.length > 1){
          decodedUri.append("=");
          decodedUri.append(decode(keyValue[1]));
        }
        isFirstParam = false;
      }
    }

    return decodedUri.toString();
  }


  private static String encode(String input) {
    return Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(input.getBytes(StandardCharsets.UTF_8));
  }

  private static String decode(String input) {
    return new String(
      Base64.getUrlDecoder()
        .decode(input),
      StandardCharsets.UTF_8
    );
  }
}
