package org.pageflow.base.response;

import lombok.Data;
import org.pageflow.base.request.AlertType;

/**
 * API 응답에 alert 메시지를 포함시키는 응답 스펙
 * @author : sechan
 */
@Data
public class WithAlertApiResponse<T> {
    
    private T data;
    private String alert;
    private AlertType alertType;
    
    
    public WithAlertApiResponse(T data, String alert, AlertType alertType) {
        this.data = data;
        this.alert = alert;
        this.alertType = alertType;
    }
    
    public static <T> WithAlertApiResponse<T> success(String alert, T data) {
        return new WithAlertApiResponse<>(data, alert, AlertType.SUCCESS);
    }
    
    public static <T> WithAlertApiResponse<T> success(String alert) {
        return new WithAlertApiResponse<>(null, alert, AlertType.SUCCESS);
    }
    
    public static <T> WithAlertApiResponse<T> error(String alert, T data) {
        return new WithAlertApiResponse<>(data, alert, AlertType.ERROR);
    }
    
    public static <T> WithAlertApiResponse<T> error(String alert) {
        return new WithAlertApiResponse<>(null, alert, AlertType.ERROR);
    }
}
