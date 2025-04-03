package org.pageflow.book.application;

import org.pageflow.common.utility.SingleValueWrapper;

import java.util.UUID;

/**
 * @author : sechan
 */
public class NodeId extends SingleValueWrapper<UUID> {
  public NodeId(UUID value) {
    super(value);
  }

  public static NodeId from(String string) {
    return new NodeId(UUID.fromString(string));
  }
}
