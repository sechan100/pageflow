package org.pageflow.global.api.init;

import lombok.extern.slf4j.Slf4j;
import org.pageflow.global.api.code.ApiCode;
import org.pageflow.global.initialize.RuntimeInitializer;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : sechan
 */
@Component
@Slf4j
public class ApiCodeRuntimeValidator implements RuntimeInitializer {

    @Override
    public void initialize() throws Throwable {
        Reflections loadAllApiCodeReflections = new Reflections(
            new ConfigurationBuilder()
                .forPackage("org.pageflow.global.api.code")
                .setScanners(Scanners.SubTypes)
        );
        Set<Class<? extends ApiCode>> allApiCodeClasses = loadAllApiCodeReflections.getSubTypesOf(ApiCode.class);

        Set<ApiCode> apiCodes;
        Set<Integer> codeNums;

        // Load With Reflections!
        apiCodes = new HashSet<>();
        for(Class<? extends ApiCode> clazz : allApiCodeClasses) {
            Method allEnumsMethod = clazz.getMethod("values");
            ApiCode[] apiCodeArr = (ApiCode[]) allEnumsMethod.invoke(null);
            apiCodes.addAll(Arrays.asList(apiCodeArr));
        }
        codeNums = apiCodes.stream().map(ApiCode::getCode).collect(Collectors.toSet());

        // 숫자 범위 검사
        boolean isRangeExceedCodeExist = codeNums.stream().anyMatch(num -> {
            return num < 1000 || num > 9999;
        });
        if(isRangeExceedCodeExist){
            List<Integer> invalidNums = codeNums.stream()
                .filter(code -> code < 1000 || code > 9999)
                .toList();
            throw new InvalidApiCodeSpecificationException("code값은 (|1000| <= code <= |9999|)를 만족하는 정수입니다. invalid codes:" + invalidNums);
        }

        // 중복된 숫자 검사
        if(codeNums.stream().distinct().count() != codeNums.size()){
            Set<Integer> seen = new HashSet<>();
            Set<Integer> duplicates = new HashSet<>();
            for(Integer num : codeNums) {
                if(!seen.add(num)) {
                    duplicates.add(num);
                }
            }
            throw new InvalidApiCodeSpecificationException("중복된 ApiCode가 존재합니다:" + duplicates);
        }

        log.info("ApiCode 정합성 검사를 완료했습니다.");
    }
}
