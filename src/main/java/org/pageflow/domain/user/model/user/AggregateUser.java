package org.pageflow.domain.user.model.user;


import lombok.Builder;
import org.pageflow.domain.user.constants.UserFetchDepth;
import org.pageflow.domain.user.entity.Account;
import org.pageflow.domain.user.entity.Profile;


/**
 * Account와 Profile 엔티티의 동시 참조
 */
@Builder
public class AggregateUser {
    
    public AggregateUser(UserFetchDepth fetchDepth, Account accountOrNull, Profile profileOrNull) {
        this.fetchDepth = fetchDepth;
        this.account = accountOrNull;
        this.profile = profileOrNull;
    }
    
    private final UserFetchDepth fetchDepth;
    private final Account account;
    private final Profile profile;
    
    
    
    public Account getAccount() {
        if(!isAccountInitialized()) {
            throw new NullPointerException("Account 엔티티는 초기화되지 않았습니다.");
        }
        return account;
    }
    
    public Profile getProfile() {
        if(!isProfileInitialized()) {
            throw new NullPointerException("Profile 엔티티는 초기화되지 않았습니다.");
        }
        return profile;
    }
    
    
    
    public boolean isAccountInitialized() {
        return fetchDepth == UserFetchDepth.ACCOUNT || fetchDepth == UserFetchDepth.FULL;
    }
    
    public boolean isProfileInitialized() {
        return fetchDepth == UserFetchDepth.PROFILE || fetchDepth == UserFetchDepth.FULL;
    }
}