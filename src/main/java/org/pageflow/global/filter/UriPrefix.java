package org.pageflow.global.filter;

/**
 * @author : sechan
 */
public abstract class UriPrefix {
  public static final String PRIVATE = "/PRIVATE";

  public static String privateUri(String uri) {
    return PRIVATE + uri;
  }
}
