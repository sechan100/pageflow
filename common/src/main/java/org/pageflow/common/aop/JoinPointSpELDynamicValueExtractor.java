package org.pageflow.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;


/**
 * @author : sechan
 */
public class JoinPointSpELDynamicValueExtractor {
  private final JoinPoint joinPoint;

  public JoinPointSpELDynamicValueExtractor(JoinPoint joinPoint) {
    this.joinPoint = joinPoint;
  }


  public Object getDynamicValue(String name) {
    ExpressionParser parser = new SpelExpressionParser();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Object[] args = joinPoint.getArgs();
    Method method = signature.getMethod();
//    String[] parameterNames = signature.getParameterNames();

    EvaluationContext context = new MethodBasedEvaluationContext(
      null, method, args, new DefaultParameterNameDiscoverer()
    );
//      new StandardEvaluationContext();
//
//    for(int i = 0; i < parameterNames.length; i++) {
//      context.setVariable(parameterNames[i], args[i]);
//    }

    return parser.parseExpression(name).getValue(context);
  }
}
