package org.pageflow.domain.book.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 책 수정 요청드
 */
@Data
public class BookUpdateRequest {

    private Long id;

    @NotEmpty
    private String title;

    private MultipartFile coverImg;
    
}
