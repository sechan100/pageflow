package org.pageflow.user.adapter.in.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.shared.utility.Forward;
import org.pageflow.user.adapter.in.filter.shared.InFilterForwarder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Response가 이미 commit된 경우, 해당 request의 진행을 중단시키는 필터
 *
 * DispatcherServlet은 요청은 적절한 Controller로 매핑하기 전, Response의 isCommited()를 호출하여 commit여부를 판별한다.
 * 한편, 필터 진행 도중에 forward 한 경우 다음과 같은 순서를 따른다.
 * filter → (forward) -> RequestDispatcher → filter(OncePerRequestFilter인 경우 이미 거친 필터는 제외) → disptcherServlet
 *
 * 해당 클래스는 SecurityFilterChain의 후반부에 위치하여, 도중에 forwarding한 요청에 대해서 request attribute를
 * 남기고, 해당 플래그를 가진 요청을 중단시키는 방식으로 문제를 해결한다.
 */
@Slf4j
public class InFilterForwardedRequestCeaseFilter extends OncePerRequestFilter implements InFilterForwarder {

  private static final String IN_FILTER_FORWARDED_REQUEST_ATTR = InFilterForwardedRequestCeaseFilter.class.getName() + "IN_FILTER_FORWARDED";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    Optional<ForwardedRequestAttr> op = getForwardedRequestAttr(request);
//    if(op.isPresent()){
//      var forwarded = op.get();
//      log.debug("filter 진행중, '{}'에서 '{}'로 forward되어 이미 처리된 요청을 중단했습니다.", forwarded.getSource(), forwarded.getForward().getForwordUri());
//    } else {
//      if(response.isCommitted()){
//        throw new IllegalStateException(
//          "이미 commit된 요청입니다. " +
//          "SecurityFilterChain 진행중 forward를 사용하였거나, 다른 이유로 응답이 이미 commit 되었을 수 있습니다." +
//          "만약, filter 진행중 forward를 사용해야할 경우, InFilterForwarder를 사용하십시오."
//        );
//      }
      filterChain.doFilter(request, response);
//    }
  }

  @Override
  public void forward(Object source, Forward forward) {
    Optional<ForwardedRequestAttr> op = getForwardedRequestAttr(forward.getRequest());
    if(op.isEmpty()){
      forward.getRequest().setAttribute(IN_FILTER_FORWARDED_REQUEST_ATTR, new ForwardedRequestAttr(source, forward));
      forward.send();
    } else {
    // 이미 inFilterForward한 요청인 경우
      throw new IllegalStateException(String.format(
        "이미 filter 진행중 '%s'에서 forward하여 commit된 요청에 대하여, 중복된 forward 요청이 발생했습니다. 요청자: '%s', uri: '%s'",
        op.get().getSource(),
        source,
        forward.getRequest().getRequestURI()
      ));
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
