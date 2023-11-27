package org.pageflow.domain.user.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
abstract public class DefaultAccountDto implements AccountDto {

    @NotEmpty
    protected String provider;

    @NotEmpty
    protected String username;

    @NotEmpty
    protected String password;

    @Email
    protected String email;

}




