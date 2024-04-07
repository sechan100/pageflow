package org.pageflow.boundedcontext.user.service;

import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.dto.AccountDto;
import org.pageflow.boundedcontext.user.dto.ProfileDto;
import org.pageflow.boundedcontext.user.entity.AccountEntity;
import org.pageflow.boundedcontext.user.mapper.UserMapper;
import org.pageflow.boundedcontext.user.repository.AccountRepository;
import org.pageflow.boundedcontext.user.repository.ProfileRepository;
import org.pageflow.shared.annotation.QueryService;
import org.pageflow.shared.type.TSID;

/**
 * @author : sechan
 */
@QueryService
@RequiredArgsConstructor
public class UserQueriesImpl implements UserQueries {
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final UserMapper mapper;


    @Override
    public AccountDto fetchAccount(TSID uid){
        AccountEntity account = accountRepository.findById(uid.toLong()).get();
        return mapper.entityToDto(account);
    }

    @Override
    public AccountDto fetchAccount(String username){
        return mapper.entityToDto(
            accountRepository.findByUsername(username)
        );
    }

    @Override
    public ProfileDto fetchProfile(TSID uid){
        return mapper.entityToDto(
            profileRepository.findById(uid.toLong()).get()
        );
    }

    @Override
    public Tuple2<AccountDto, ProfileDto> fetchAccountAndProfile(TSID uid){
        AccountEntity account = accountRepository.findWithProfileById(uid.toLong());
        return new Tuple2(
            mapper.entityToDto(account),
            mapper.entityToDto(account.getProfile())
        );
    }

    @Override
    public Tuple2<AccountDto, ProfileDto> fetchAccountAndProfile(String username){
        AccountEntity account = accountRepository.findWithProfileByUsername(username);
        return new Tuple2(
            mapper.entityToDto(account),
            mapper.entityToDto(account.getProfile())
        );
    }

}
