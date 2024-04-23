package org.pageflow.global.api.code;

import org.pageflow.global.initialize.RuntimeInitializer;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : sechan
 */
@Component
public class ApiCodeRuntimeValidator implements RuntimeInitializer {

    @Override
    public void initialize() throws Throwable {
        Reflections loadAllApiCodeReflections = new Reflections(
            new ConfigurationBuilder()
                .forPackage("org.pageflow.global.api.code")
                .setScanners(Scanners.SubTypes)
        );
        Set<Class<? extends ApiCode>> allApiCodes = loadAllApiCodeReflections.getSubTypesOf(ApiCode.class);

        List<Integer> allCodes = new ArrayList();

        for(Class<? extends ApiCode> clazz : allApiCodes) {
            Method allEnumsMethod = clazz.getMethod("values");
            ApiCode[] apiCodeArr = (ApiCode[]) allEnumsMethod.invoke(null);
            for(ApiCode code : apiCodeArr){
                allCodes.add(code.getCode());
            }
        }

        // 형식 검사
        boolean isExistInvalidApiCode = allCodes.stream()
            .anyMatch(code -> {
                int codeAbs = (code < 0) ? -code : code;
                if(codeAbs < 1000 || codeAbs > 9999){
                    return false;
                }
                return true;
            });
        if(isExistInvalidApiCode){
            List<Integer> invalidApiCodes = allCodes.stream()
                .filter(code -> code < 1000 || code > 9999)
                .toList();
            throw new InvalidApiCodeSpecificationException("code값은 (|1000| <= code <= |9999|)를 만족하는 정수입니다. violated codes:" + invalidApiCodes);
        }

        // 중복 검사
        if(allCodes.stream().distinct().count() != allCodes.size()){
                Set<Integer> seen = new HashSet<>();
                Set<Integer> duplicates = new HashSet<>();
                for (Integer code : allCodes) {
                    if(!seen.add(code)) {
                        duplicates.add(code);
                    }
                }
                throw new InvalidApiCodeSpecificationException("중복된 ApiCode가 존재합니다:" + duplicates);
            }
    }
}
