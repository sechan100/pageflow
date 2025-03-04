package org.pageflow.test.e2e.shared.fixture;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * @author sechan
 */
public class FixtureExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) {
    Optional<Method> testMethod = context.getTestMethod();
    if(testMethod.isPresent()) {
      Method method = testMethod.get();
      Fixture fixtureAnnotation = method.getAnnotation(Fixture.class);
      if(fixtureAnnotation == null) {
        return;
      }
      ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
      // DataFixture
      Class<? extends TestFixture>[] fixtureClass = fixtureAnnotation.value();
      for(Class<? extends TestFixture> clazz : fixtureClass) {
    TestFixture fixture = applicationContext.getBean(clazz);
    fixture.configure();
  }
}
//      context.getStore(ExtensionContext.Namespace.create(FixtureExtension.class)).put("dataFixture", context);
  }

@Override
public void afterEach(ExtensionContext context) {
    ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
    JdbcTemplate jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
    List<String> tableNames = jdbcTemplate.queryForList(
      "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
      String.class);
    // 외래 키 제약 조건 비활성화 (필요한 경우)
    jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
    // 모든 테이블 truncate
    for (String tableName : tableNames) {
      jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
    }
    // 외래 키 제약 조건 다시 활성화
    jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
  }

}