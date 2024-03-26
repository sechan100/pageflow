package org.pageflow.common.api;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.springframework.web.util.HtmlUtils;

public class HtmlCharacterEscapes extends CharacterEscapes {

  private final int[] asciiEscapes;

  public HtmlCharacterEscapes() {
    // 1. XSS 방지 처리할 특수 문자 지정
    asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
    asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
    asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
    asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
    asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
    asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
    asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
    asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
  }

  @Override
  public int[] getEscapeCodesForAscii() {
    return asciiEscapes;
  }

  @Override
  // Escape할 문자들이 어차피 다 아스키
  @SuppressWarnings("NumericCastThatLosesPrecision")
  public SerializableString getEscapeSequence(int ch) {
    return new SerializedString(HtmlUtils.htmlEscape(Character.toString((char) ch)));
  }
}