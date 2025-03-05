package org.pageflow.test.shared.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.pageflow.test.shared.TestResourcePermissionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * @author sechan
 */
public class ResourcePermissionClearExtension implements AfterEachCallback {

  @Override
  public void afterEach(ExtensionContext context) {
    ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
    TestResourcePermissionContext testResourcePermissionContext = applicationContext.getBean(TestResourcePermissionContext.class);
    testResourcePermissionContext.clear();
  }

}