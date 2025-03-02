package org.pageflow.common.result;

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
// TODO: ApplicationListener<ContextRefreshedEvent> 구현으로 변경
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

    // ResultCode의 구현체가 Enum인지 검사
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

      // 중복되는 이름의 ResultCode가 있는지 검사
      if(!uniqueNames.add(resultCode.name())) {
        throw new InvalidResultCodeSpecificationException("중복된 ResultCode가 존재합니다: " + resultCode.name());
      }

      // Generic Type Parameter를 사용하는 클래스를 DataType으로 사용했는지 검사
      Class<?> dataType = resultCode.getDataType();
      if(dataType != null && dataType.getTypeParameters().length > 0){
        throw new InvalidResultCodeSpecificationException(String.format(
          "ResultCode의 DataType은 Generic Type Parameter를 포함하는 클래스를 사용할 수 없습니다. CODE: '%s', DataType: '%s'", resultCode.name(), dataType.getName()
        ));
      }

    }
    log.info("===== ResultCode 정합성 검사를 완료했습니다. =====");
  }
}
