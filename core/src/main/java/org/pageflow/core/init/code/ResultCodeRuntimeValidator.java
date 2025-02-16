package org.pageflow.core.init.code;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.common.initialize.RuntimeInitializer;
import org.pageflow.common.result.code.ResultCode;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * ResultCode를 구현하는 모든 Enum 타입들을 검사한다.
 * @author : sechan
 */
@Component
@Slf4j
public class ResultCodeRuntimeValidator implements RuntimeInitializer {

  @Override
  public void initialize() throws Throwable {
    Reflections loadAllApiCodeReflections = new Reflections(
      new ConfigurationBuilder()
        .forPackage("org.pageflow")
        .setScanners(Scanners.SubTypes)
    );
    Set<Class<? extends ResultCode>> allApiCodeClasses = loadAllApiCodeReflections.getSubTypesOf(ResultCode.class);

    List<ResultCode> resultCodes = new ArrayList<>();

    // Load With Reflections!
    for(Class<? extends ResultCode> clazz : allApiCodeClasses){
      if(!clazz.isEnum()) {
        throw new InvalidResultCodeSpecificationException("ResultCode의 구현은 반드시 Enum 타입이어야 합니다: " + clazz);
      }
      Method allEnumsMethod = clazz.getMethod("values");
      ResultCode[] resultCodeArr = (ResultCode[]) allEnumsMethod.invoke(null);
      resultCodes.addAll(Arrays.asList(resultCodeArr));
    }

    Set<String> uniqueNames = new HashSet<>();
    for(ResultCode resultCode : resultCodes){
      if(!uniqueNames.add(resultCode.name())) {
        throw new InvalidResultCodeSpecificationException("중복된 ResultCode가 존재합니다: " + resultCode.name());
      }
    }
    log.info("===== ResultCode 정합성 검사를 완료했습니다. =====");
  }
}
