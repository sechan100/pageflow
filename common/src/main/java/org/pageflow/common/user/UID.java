package org.pageflow.common.user;

import org.pageflow.common.shared.type.SingleValueWrapper;

import java.util.UUID;

/**
 * @author : sechan
 */
public final class UID extends SingleValueWrapper<UUID> {

  public static final UID ANONYMOUS_UID = UID.from("00000000-0000-0000-0000-000000000000");

  public UID(UUID id) {
    super(id);
  }

  public static UID from(String id) {
    return new UID(UUID.fromString(id));
  }

  public static UID random() {
    return new UID(UUID.randomUUID());
  }
}
