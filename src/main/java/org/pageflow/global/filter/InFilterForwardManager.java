package org.pageflow.global.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.pageflow.shared.utility.Forward;

/**
 * 필터 내부에서 forward하기 위해서는 해당 인터페이스를 통해야한다.
 * reserveForward를 통해서 예약한 forward 요청은, 모든 필터를 거친 후에 기존 요청체인을 종료하고 안전하게 forward를 실행할 수 있도록 한다.
 * @author : sechan
 */
public interface InFilterForwardManager {
    /**
     * @param source inFilterForward를 호출한 객체 - 필터체인 연쇄중 어떤 클래스에서 forward를 호출했는지 알기위함
     * @param forward forward 객체
     */
    void inFilterForward(Object source, Forward forward);
    boolean isCommitedResponseByInFilterForward(HttpServletRequest request);
}
