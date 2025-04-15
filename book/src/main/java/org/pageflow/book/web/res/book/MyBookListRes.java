package org.pageflow.book.web.res.book;

import lombok.Value;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class MyBookListRes {
  List<MyBookRes> books;
}
