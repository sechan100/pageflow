package org.pageflow.file.model;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.common.property.PropsAware;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 정적 파일의 경로를 나타내는 VO
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
      PropsAware.use().file.public_.webBaseUrl.length()
    ));
  }

  public static FilePath fromFullPath(String fullPath) {
    Preconditions.checkState(
      isFullPath(fullPath),
      "올바른 FullPath 형식이 아닙니다."
    );
    return new FilePath(fullPath.substring(
      PropsAware.use().file.public_.serverDirectory.length()
    ));
  }

  public static FilePath fromStaticPath(String staticPath) {
    return new FilePath(staticPath);
  }


  /**
   * 정적 파일 경로를 반환한다.
   * @return /{YYYY}/{MM}/{dd}/{fileName}.{ext}
   */
  public String getStaticPath() {
    return staticParent + "/" + filename + "." + extension;
  }

  /**
   * 실제 서버에 저장된 파일의 전체 경로를 반환한디.
   * @return {serverFileParent}/{YYYY}/{MM}/{dd}/{fileName}.{ext}
   */
  public String getFullPath() {
    return getParent() + getStaticPath();
  }

  /**
   * 웹 프로토콜로 접근 가능한 파일 주소를 반환한다.
   * @return {webBaseUrl}/{YYYY}/{MM}/{dd}/{fileName}.{ext}
   */
  public String getWebUrl() {
    return getWebBaseUrl() + getStaticPath();
  }


  private static String getWebBaseUrl() {
    return PropsAware.use().file.public_.webBaseUrl;
  }

  private static String getParent() {
    return PropsAware.use().file.public_.serverDirectory;
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