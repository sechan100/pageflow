package org.pageflow.global.api.types;

import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : sechan
 */
@Getter
@RequiredArgsConstructor
public class FieldError {
    private final String field;
    @Nullable
    private final String value;
    private final String message;


    @Getter
    public static class Errors {
        private final List<FieldError> errors;

        public Errors(FieldError... errors) {
            this.errors = Collections.arrayToList(errors);
        }

        public Errors() {
            this.errors = new ArrayList<>();
        }

        public void add(FieldError error) {
            this.errors.add(error);
        }
    }
}
