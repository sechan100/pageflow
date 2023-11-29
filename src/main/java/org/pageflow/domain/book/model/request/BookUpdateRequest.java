package org.pageflow.domain.book.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 책 수정 요청드
 */
@Data
public class BookUpdateRequest {
    
    private Long id;
    
    private String title;

    private MultipartFile coverImg;

}