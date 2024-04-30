package org.pageflow.boundedcontext.file.model;

import com.google.common.base.Preconditions;
import lombok.Value;
import org.pageflow.global.property.PropsAware;

import java.util.regex.Pattern;

/**
 * 정적 파일의 경로를 나타내는 Value Object
 * /{YYYY}/{MM}/{dd}/{fileName}.{ext} 형식의 경로를 가진다.
 * @author : sechan
 */
@Value
public class FilePath {
    private static final Pattern REGEX = Pattern.compile(
        "^/\\d{4}/\\d{1,2}/\\d{1,2}/[\\w\\-]+\\.\\w+$"
    );

    private final String staticParent; // /{YYYY}/{MM}/{dd}
    private final String filename;
    private final String extension;


    public FilePath(String staticParent, String filename, String extension) {
        this.staticParent = staticParent;
        this.filename = filename;
        this.extension = extension;
    }

    public static FilePath fromWebUri(String webUri){
        Preconditions.checkState(
            isWebUri(webUri),
            "올바른 WebUri 형식이 아닙니다."
        );
        return new FilePath(webUri.substring(
            PropsAware.use().file.webUriPrefix.length()
        ));
    }

    public static FilePath fromFullPath(String fullPath){
        Preconditions.checkState(
            isFullPath(fullPath),
            "올바른 FullPath 형식이 아닙니다."
        );
        return new FilePath(fullPath.substring(
            PropsAware.use().file.parent.length()
        ));
    }

    public static FilePath fromStaticPath(String staticPath){
        return new FilePath(staticPath);
    }

    private FilePath(String staticPath){
        checkIsStaticPath(staticPath);
        String[] split = staticPath.split("/");
        this.staticParent = split[0] + "/" + split[1] + "/" + split[2];
        this.filename = split[3].split("\\.")[0];
        this.extension = split[3].split("\\.")[1];
    }


    public String getStaticPath(){
        return staticParent + "/" + filename + "." + extension;
    }

    public String getFullPath(){
        return getParent() + getStaticPath();
    }

    public String getWebUri(){
        return getWebUriPrefix() + getStaticPath();
    }



    private static String getWebUriPrefix(){
        return PropsAware.use().file.webUriPrefix;
    }

    private static String getParent(){
        return PropsAware.use().file.parent;
    }

    private static void checkIsStaticPath(String path){
        Preconditions.checkState(
            isStaticPath(path),
            "올바른 DefaultPath 형식이 아닙니다."
        );
    }

    private static boolean isStaticPath(String path){
        return REGEX.matcher(path).matches();
    }

    private static boolean isWebUri(String encodedUri) {
        String webUriPrefix = getWebUriPrefix();
        if(encodedUri.startsWith(webUriPrefix)){
            String withoutWebUriPrefix = encodedUri.substring(webUriPrefix.length());
            return isStaticPath(withoutWebUriPrefix);
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