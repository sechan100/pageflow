package org.pageflow.test.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.modules.AnnotationDescriptor;
import com.tngtech.archunit.library.modules.ArchModule;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;

/**
 * @author : sechan
 */
public class ModuleCheckCondition<ANNOTATION extends Annotation> extends ArchCondition<ArchModule<AnnotationDescriptor<ANNOTATION>>> {
  public ModuleCheckCondition() {
    super("'exposedPackages, exposedClassAnnotation로 허용된 클래스만 외부 모듈에서 import 할 수 있습니다.");
  }

  @Override
  public void check(ArchModule<AnnotationDescriptor<ANNOTATION>> module, ConditionEvents events) {
    module.getModuleDependenciesFromSelf().forEach(moduleDependency -> {
      ANNOTATION descriptor = moduleDependency.getTarget().getDescriptor().getAnnotation();

      // exposedPackages
      String[] apiPackageIdentifiers = getStringArrayAnnotationProperty(descriptor, "exposedPackages"); // ArchModule 클래스 참고
      Predicate<JavaClass> exposedPackagePridicate = resideInAnyPackage(apiPackageIdentifiers);

      // exposedClassAnnotation
      Class<? extends Annotation>[] annotationType = (Class<? extends Annotation>[]) getAnnotationProperty(descriptor, "exposedClassAnnotation");
      Predicate<JavaClass> exposedClassWithAnnotationPredicate = clazz -> {
        for (Class<? extends Annotation> annotation : annotationType) {
          if(clazz.isAnnotatedWith(annotation)) {
            return true;
          }
        }
        return false;
      };

      Predicate<JavaClass> predicate = exposedPackagePridicate.or(exposedClassWithAnnotationPredicate);
//
      moduleDependency.toClassDependencies().stream()
        .filter(classDependency -> !predicate.test(classDependency.getTargetClass()))
        .forEach(classDependency -> events.add(SimpleConditionEvent.violated(classDependency, classDependency.getDescription())));
    });
  }


  private String[] getStringArrayAnnotationProperty(Annotation annotation, String annotationPropertyName) {
    Object value = getAnnotationProperty(annotation, annotationPropertyName);
    try {
      return (String[]) value;
    } catch (ClassCastException e) {
      String message = String.format("Property @%s.%s() must be of type String[]", annotation.annotationType().getSimpleName(), annotationPropertyName);
      throw new IllegalArgumentException(message, e);
    }
  }

  private static Object getAnnotationProperty(Annotation annotation, String annotationPropertyName) {
    try {
      return annotation.annotationType().getMethod(annotationPropertyName).invoke(annotation);
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      String message = String.format("Could not invoke @%s.%s()", annotation.annotationType().getSimpleName(), annotationPropertyName);
      throw new IllegalArgumentException(message, e);
    }
  }
}
