package org.pageflow.common.permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.pageflow.common.aop.JoinPointSpELDynamicValueExtractor;
import org.pageflow.common.result.ProcessResultException;
import org.pageflow.common.result.code.CommonCode;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ResourceAccessPermissionEvaluateAop {

  private final ResourcePermissionContext resourcePermissionContext;

  /**
   * {@link PermissionRequired} 어노테이션이 부착된 모든 메소드를 대상으로 한다.
   */
  @Pointcut("@annotation(org.pageflow.common.permission.PermissionRequired)")
  public void resourcePermissionRequiredMethods() {
  }


  @Before("resourcePermissionRequiredMethods()")
  public <ID> void evaluatePermission(JoinPoint joinPoint) {
    PermissionRequired[] annotations = this.extractAnnotations(joinPoint);

    for(PermissionRequired a : annotations) {
      Class<? extends ResourcePermission> permissionType = a.permissionType();
      List<ResourcePermission> permissions = resourcePermissionContext.getResourcePermissions();

      // PermissionType으로 permission을 특정
      ResourcePermission<ID> permission = null;
      for(ResourcePermission p : permissions) {
        if(p.getClass().equals(permissionType)) {
          if(permission != null) {
            throw new IllegalArgumentException("동일한 resource에 대한 Permission이 2개 이상 존재합니다.");
          }
          permission = p;
        }
      }
      if(permission == null) {
        throw new IllegalArgumentException("해당 리소스에 대한 Permission이 존재하지 않습니다. type: " + permissionType);
      }

      // resource id 일치 확인
      ID resourceId = extractResourceId(joinPoint, permissionType);
      if(!permission.getResourceId().equals(resourceId)) {
        throw new IllegalArgumentException("접근을 시도하는 resource의 id가 허가된 id와 일치하지 않습니다.");
      }

      Set<? extends ResourceAction> permittedActions = permission.getPermittedActions();
      List<String> requiredActions = Arrays.asList(a.actions());

      // 권한 평가
      if(permission.isFullActionPermitted()) {
        continue;
      }
      Set<String> permittedActionStrings = permittedActions.stream().map(ResourceAction::name).collect(Collectors.toSet());
      boolean isPermitted = permittedActionStrings.containsAll(requiredActions);

      if(!isPermitted) {
        log.debug("Resource 접근 권한이 부족합니다. required: {}, permitted: {}", requiredActions, permittedActionStrings);
        throw new ProcessResultException(CommonCode.RESOURCE_PERMISSION_DENIED);
      }
    }
  }


  private PermissionRequired[] extractAnnotations(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    PermissionRequired[] annotations = method.getAnnotationsByType(PermissionRequired.class);
    return annotations;
  }

  private <ID> ID extractResourceId(JoinPoint joinPoint, Class<? extends ResourcePermission> permissionType) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Object[] args = joinPoint.getArgs();
    String[] parameterNames = signature.getParameterNames();

    for(int i = 0; i < method.getParameterCount(); i++) {
      // ResourceId 어노테이션이 부착된 매개변수를 찾는다.
      ResourceId resourceIdAnnotation = AnnotatedElementUtils.findMergedAnnotation(method.getParameters()[i], ResourceId.class);
      if(resourceIdAnnotation == null) {
        continue;
      }

      // ResourcePermission 타입이 일치하는지 확인
      Class<? extends ResourcePermission> actualType = resourceIdAnnotation.permissionType();
      if(!actualType.equals(permissionType)) {
        continue;
      }

      String SpEL = resourceIdAnnotation.value();
      // SpEL 표현식이 지정된 경우
      if(!SpEL.isEmpty()) {
        JoinPointSpELDynamicValueExtractor extractor = new JoinPointSpELDynamicValueExtractor(joinPoint);
        return (ID) extractor.getDynamicValue(SpEL);

        // 표현식이 비어있다면 해당 매개변수가 resourceId이다.
      } else {
        return (ID) args[i];
      }
    }
    throw new IllegalArgumentException("ResourceId 어노테이션이 부착된 매개변수를 찾을 수 없습니다.");
  }
}
