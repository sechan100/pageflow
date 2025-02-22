package org.pageflow.test.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pageflow.common.module.ArchModule;

import static com.tngtech.archunit.library.modules.syntax.ModuleRuleDefinition.modules;

public class ModuleArchitectureTest {

  private static JavaClasses importedClasses;

  @BeforeAll
  static void setup() {
    importedClasses = new ClassFileImporter().importPackages("org.pageflow");
  }

  @Test
  @DisplayName("모듈 규칙")
  void moduleArchitecture() {
    ArchRule rule = modules()
      .definedByAnnotation(ArchModule.class)
      .should(new ModuleCheckCondition<>());
    rule.check(importedClasses);
  }

}
