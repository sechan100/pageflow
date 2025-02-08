package org.pageflow.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.shared.utility.Forward;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Response가 이미 commit된 경우, 해당 매핑의 요청을 중단시키는 필터
 * 어쩔 수 없이, 필터 수준에서 Forward를 통해서 요청을 분기하여 응답하는 경우가 존재한다.
 * 이 때, 그냥 forward 해버리고 종료하면 그대로 FilterChain이 연쇄되어 DispatcherServlet까지 도달하게 된다.
 * DispatcherServlet은 요청은 매핑하기전에 Response의 isCommited()를 호출하여 commit여부를 판별한다.
 * 이 때 Response가 이미 commit된 상태이기 때문에, IllegalStateException이 발생한다.
 * 때문에, 만약 이전 필터에서 요청을 분기하였다면, request의 attribute에 플래그를 남기고,
 * 이를 필터 체인의 가장 마지막에 위치한 해당 필터에서 확인하여, filterChain의 책임 연쇄를 끊는 방식으로 요청의 흐름을 제어한다.
 */
@Slf4j
public class InFilterForwardedRequestCeaseFilter extends OncePerRequestFilter implements InFilterForwardManager {

  private static final String IN_FILTER_FORWARDED_REQUEST_ATTR = "InFilterForwardedRequestCeaseFilter.inFilterForwarded";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    Optional<ForwardedRequestAttr> op = getForwardedRequestAttr(request);
    if(op.isPresent()){
      log.debug("이미 {}에 의해서 InFilterForward로 commit된 요청입니다. filterChain 연쇄를 끊어, 요청을 중단합니다.", op.get().getSource());
    } else {
      filterChain.doFilter(request, response);
    }
  }

  @Override
  public void inFilterForward(Object source, Forward forward) {
    Optional<ForwardedRequestAttr> op = getForwardedRequestAttr(forward.getRequest());
    // 이미 inFilterForward한 경우
    if(op.isPresent()){
      throw new IllegalStateException(
        "이미 InFilterForward에 의해서 commit된 요청입니다." +
          "inFilterForward source: " + op.get().getSource() + ", uri: '" + forward + "'"
      );
    } else {
      forward.send();
    }
  }

  @Override
  public boolean isCommitedResponseByInFilterForward(HttpServletRequest request) {
    return getForwardedRequestAttr(request).isPresent();
  }

  private Optional<ForwardedRequestAttr> getForwardedRequestAttr(HttpServletRequest request) {
    return Optional.ofNullable((ForwardedRequestAttr) request.getAttribute(IN_FILTER_FORWARDED_REQUEST_ATTR));
  }

  @Value
  static class ForwardedRequestAttr {
    private final Object source;
    private final Forward forward;
  }
}
