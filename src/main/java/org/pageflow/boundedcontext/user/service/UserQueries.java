package org.pageflow.boundedcontext.user.service;

import io.vavr.Tuple2;
import org.pageflow.boundedcontext.user.dto.AccountDto;
import org.pageflow.boundedcontext.user.dto.ProfileDto;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
public interface UserQueries {
    AccountDto fetchAccount(TSID uid);
    AccountDto fetchAccount(String username);

    ProfileDto fetchProfile(TSID uid);

    Tuple2<AccountDto, ProfileDto> fetchAccountAndProfile(TSID uid);
    Tuple2<AccountDto, ProfileDto> fetchAccountAndProfile(String username);
}
