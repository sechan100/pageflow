package org.pageflow.boundedcontext.common.exception;

import lombok.Getter;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class InputValueException extends DomainException {
    private final String fieldName;
    @Nullable
    private final String value;

    private InputValueException(String fieldName, @Nullable String value, String message) {
        super(message + " fieldName : %s, value : %s".formatted(fieldName, value));
        this.fieldName = fieldName;
        this.value = value;
    }

    public static Builder1 builder() {
        return new Builder();
    }



    public interface Builder1 {
        Builder2 message(String message);
        Builder2 message(String message, String... args);
    }
    public interface Builder2 {
        Builder3 field(String name, @Nullable String value);
    }
    public interface Builder3 {
        InputValueException build();
    }
    public static class Builder implements Builder1, Builder2, Builder3 {
        private String fieldName;
        private String value;
        private String message;

        @Override
        public Builder2 message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public Builder2 message(String message, String... args) {
            this.message = message.formatted((Object) args);
            return this;
        }

        @Override
        public Builder3 field(String name, @Nullable String value) {
            this.fieldName = name;
            this.value = value;
            return this;
        }

        @Override
        public InputValueException build() {
            return new InputValueException(fieldName, value, message);
        }
    }




}
