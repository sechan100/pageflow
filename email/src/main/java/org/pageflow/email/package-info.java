

@ArchModule(
  name = EMAIL,
  exposedPackages = { "org.pageflow.email.port.." },
  exposedClassAnnotation = {
    ApplicationModule.class
  }
)
package org.pageflow.email;

import org.pageflow.common.module.ApplicationModule;
import org.pageflow.common.module.ArchModule;

import static org.pageflow.common.module.Modules.EMAIL;