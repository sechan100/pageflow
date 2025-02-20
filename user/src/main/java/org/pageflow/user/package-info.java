

@ArchModule(
  name = USER,
  exposedPackages = {
    "org.pageflow.user.port",
    "org.pageflow.user.dto",
  },
  exposedClassAnnotation = {
    ApplicationModule.class,
//    Entity.class
  }
)
package org.pageflow.user;

import org.pageflow.common.module.ApplicationModule;
import org.pageflow.common.module.ArchModule;

import static org.pageflow.common.module.Modules.USER;