//package org.pageflow.core.advice;
//
//import jakarta.validation.constraints.Max;
//import jakarta.validation.constraints.Min;
//import lombok.extern.slf4j.Slf4j;
//import org.pageflow.core.validation.FieldValidationException;
//import org.pageflow.core.api.ApiResponse;
//import org.pageflow.core.validation.FieldReason;
//import org.pageflow.core.validation.FieldValidationResult;
//import org.pageflow.core.validation.InvalidField;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.Objects;
//
///**
// * @author : sechan
// */
//@Slf4j
//@RestControllerAdvice
//public class FieldExceptionRestAdvice {
//
//  // @Valid를 통한 Spring Bean Validation의 필드의 유효성 검사에 실패한 경우
//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  public ApiResponse<FieldValidationResult> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
//
//    FieldValidationResult result = new FieldValidationResult();
//    e.getBindingResult().getFieldErrors()
//      .forEach(fieldError -> {
//        String fieldName = fieldError.getField();
//        String rejectedValue = Objects.requireNonNullElse(fieldError.getRejectedValue(), "null").toString();
//        String errorMessage = Objects.requireNonNullElse(fieldError.getDefaultMessage(), "올바르지 않은 값입니다.");
//        result.addInvalidField(new InvalidField(
//          fieldName,
//          rejectedValue,
//          errorMessage
//        ));
//      });
//    return new ApiResponse<>(ResultCode4.FIELD_VALIDATION_FAIL, result);
//  }
//
//  @ExceptionHandler(FieldValidationException.class)
//  public ApiResponse<InvalidField.Errors> handleInputValueException(FieldValidationException e) {
//    InvalidField.Errors errors = new InvalidField.Errors();
//    errors.add(new InvalidField(
//      e.getFieldName(),
//      e.getValue(),
//      e.getMessage()
//    ));
//    return new ApiResponse<>(ResultCode4.FIELD_VALIDATION_FAIL, errors);
//  }
//
//  private FieldReason mapAnnotationToFieldReason(Field field) {
//    if (field.isAnnotationPresent(NotBlank.class) || field.isAnnotationPresent(NotEmpty.class)) {
//      return FieldReason.EMPTY;
//    }
//    if (field.isAnnotationPresent(Size.class)) {
//      Size size = field.getAnnotation(Size.class);
//      if (size.min() > 0) {
//        return FieldReason.TOO_SHORT;
//      }
//      if (size.max() < Integer.MAX_VALUE) {
//        return FieldReason.TOO_LONG;
//      }
//    }
//    if (field.isAnnotationPresent(Pattern.class)) {
//      return FieldReason.INVALID_FORMAT;
//    }
//    if (field.isAnnotationPresent(Min.class)) {
//      return FieldReason.NOT_POSITIVE;
//    }
//    if (field.isAnnotationPresent(Max.class)) {
//      return FieldReason.NOT_NEGATIVE;
//    }
//    return FieldReason.INVALID_VALUE; // 기본값
//  }
//
//}
