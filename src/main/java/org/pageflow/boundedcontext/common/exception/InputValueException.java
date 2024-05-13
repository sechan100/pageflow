package org.pageflow.boundedcontext.common.exception;

import lombok.Getter;
import org.springframework.lang.Nullable;

/**
 * @author : sechan
 */
@Getter
public class InputValueException extends DomainException {
    private final String field;
    @Nullable
    private final Object value;

    private InputValueException(String message, String field, @Nullable Object value) {
        super(message);
        this.field = field;
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
        Builder3 field(String name, @Nullable Object value);
    }
    public interface Builder3 {
        InputValueException build();
    }
    public static class Builder implements Builder1, Builder2, Builder3 {
        private String message;
        private String field;
        private Object value;

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
        public Builder3 field(String name, @Nullable Object value) {
            this.field = name;
            this.value = value;
            return this;
        }

        @Override
        public InputValueException build() {
            return new InputValueException(message, field, value);
        }
    }




}
