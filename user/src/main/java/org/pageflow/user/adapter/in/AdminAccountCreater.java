//package org.pageflow.user.adapter.in;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.pageflow.core.user.RoleType;
//import org.pageflow.user.dto.UserDto;
//import org.pageflow.user.port.out.entity.AccountPersistencePort;
//import org.pageflow.user.port.in.CreateUserCmd;
//import org.pageflow.core.user.ProviderType;
//import org.pageflow.core.initialize.RuntimeInitializer;
//import org.pageflow.core.property.ApplicationProperties;
//import org.pageflow.common.shared.utility.JsonUtility;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * @author : sechan
// */
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class AdminAccountCreater implements RuntimeInitializer {
//  private final AdminUseCase adminUseCase;
//  private final AccountPersistencePort accountJpaRepo;
//  private final JsonUtility jsonUtility;
//  private final ApplicationProperties props;
//
//  @Override
//  @Transactional
//  public void initialize() {
//    if(accountJpaRepo.existsByRole(RoleType.ROLE_ADMIN)){
//      return;
//    }
//    ApplicationProperties.Admin admin = props.admin;
//    CreateUserCmd cmd = new CreateUserCmd(
//      admin.username,
//      admin.password,
//      admin.email,
//      admin.penname,
//      RoleType.ROLE_ADMIN,
//      ProviderType.NATIVE,
//      props.user.defaultProfileImageUrl
//    );
//
//    UserDto result = adminUseCase.registerAdmin(cmd);
//
//    log.info("관리자 계정을 생성했습니다. {}", jsonUtility.toJson(result));
//  }
//}
