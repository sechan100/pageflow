
@ArchModule(
  name = BOOK,
  exposedPackages = {
    "org.pageflow.book.domain.toc.entity..", // FolderDesign Enum 때문
  },
  exposedClassAnnotation = {
    ApplicationModule.class
  }
)
package org.pageflow.book;

import org.pageflow.common.module.ApplicationModule;
import org.pageflow.common.module.ArchModule;

import static org.pageflow.common.module.Modules.BOOK;