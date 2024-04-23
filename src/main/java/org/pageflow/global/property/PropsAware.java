package org.pageflow.global.property;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author : sechan
 */
@Component
public class PropsAware {

    private static AppProps props;

    public PropsAware(AppProps props){
        PropsAware.props = props;
    }

    @SuppressWarnings("StaticVariableUsedBeforeInitialization")
    public static AppProps use(){
        Assert.notNull(props, "AppProps가 초기화되지 않았습니다.");
        return props;
    }
}
