package org.pageflow.book.adapter.in.res.book;

import lombok.Value;

import java.util.List;

/**
 * @author : sechan
 */
@Value
public class MyBooksRes {
  List<SimpleBookRes> books;
}
