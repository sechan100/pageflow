package org.pageflow.boundedcontext.file.model;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.global.property.PropsAware;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 정적 파일의 경로를 나타내는 Value Object
 * /{YYYY}/{MM}/{dd}/{fileName}.{ext} 형식의 경로를 가진다.
 *
 * @author : sechan
 */
@Value
public class FilePath {
  private static final Pattern STATIC_PATH_REGEX = Pattern.compile(
    "^/\\d{4}/\\d{1,2}/\\d{1,2}/[\\w\\-]+\\.\\w+$"
  );

  String staticParent; // /{YYYY}/{MM}/{dd}
  UUID filename;
  String extension;


  public FilePath(String staticParent, UUID filename, String extension) {
    this.staticParent = staticParent;
    this.filename = filename;
    this.extension = extension;
  }

  private FilePath(String staticPath) {
    checkIsStaticPath(staticPath);
    String[] split = staticPath.split("/");
    this.staticParent = "/" + split[1] + "/" + split[2] + "/" + split[3];
    String filename = split[4].split("\\.")[0];
    this.filename = UUID.fromString(filename);
    this.extension = split[4].split("\\.")[1];
  }

  public static FilePath fromWebUrl(String webUrl) {
    Preconditions.checkState(
      isWebUrl(webUrl),
      "올바른 내부 서버 WebUrl 형식이 아닙니다."
    );
    return new FilePath(webUrl.substring(
      PropsAware.use().file.webBaseUrl.length()
    ));
  }

  public static FilePath fromFullPath(String fullPath) {
    Preconditions.checkState(
      isFullPath(fullPath),
      "올바른 FullPath 형식이 아닙니다."
    );
    return new FilePath(fullPath.substring(
      PropsAware.use().file.parent.length()
    ));
  }

  public static FilePath fromStaticPath(String staticPath) {
    return new FilePath(staticPath);
  }


  public String getStaticPath() {
    return staticParent + "/" + filename + "." + extension;
  }

  public String getFullPath() {
    return getParent() + getStaticPath();
  }

  public String getWebUrl() {
    return getWebBaseUrl() + getStaticPath();
  }


  private static String getWebBaseUrl() {
    return PropsAware.use().file.webBaseUrl;
  }

  private static String getParent() {
    return PropsAware.use().file.parent;
  }

  private static void checkIsStaticPath(String path) {
    Preconditions.checkState(
      isStaticPath(path),
      "올바른 DefaultPath 형식이 아닙니다."
    );
  }

  private static boolean isStaticPath(String path) {
    return STATIC_PATH_REGEX.matcher(path).matches();
  }

  private static boolean isWebUrl(String encodedUri) {
    String webBaseUrl = getWebBaseUrl();
    if(encodedUri.startsWith(webBaseUrl)){
      String withoutWebBaseUrl = encodedUri.substring(webBaseUrl.length());
      return isStaticPath(withoutWebBaseUrl);
    }
    return false;
  }

  private static boolean isFullPath(String path) {
    String parent = getParent();
    if(path.startsWith(parent)){
      return isStaticPath(path.substring(parent.length()));
    }
    return false;
  }

}