package org.pageflow.domain.book.model.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BookForm {
    @NotEmpty(message = "제목은 필수")
    private String title;

    private MultipartFile file;
}
