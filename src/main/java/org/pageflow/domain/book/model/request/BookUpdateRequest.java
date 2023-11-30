package org.pageflow.domain.book.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 책 수정 요청드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    
    private Long id;
    
    private String title;

    private MultipartFile coverImg;

}