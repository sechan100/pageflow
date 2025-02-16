package org.pageflow.user.adapter.out;

import org.pageflow.user.port.out.LoadForbiddenWordPort;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

/**
 * @author : sechan
 */
@Repository
public class HardCodingLoadForbiddenWordAdapter implements LoadForbiddenWordPort {
  private static final Set<String> PENNAME =
    Set.of(
      "느금마", "니애미", "병신", "섹스","시발", "씨발", "원조교재", "원조교제", "좆", "지랄", "창녀", "창년", "창놈"
    );

  @Override
  public Collection<String> loadPennameForbiddenWords() {
    return PENNAME;
  }
}
