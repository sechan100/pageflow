package org.pageflow.boundedcontext.auth.port.in;

import lombok.Value;
import org.pageflow.boundedcontext.common.value.Email;
import org.pageflow.boundedcontext.common.value.UID;

/**
 * @author : sechan
 */
@Value
public class UserEmail {
    UID uid;
    Email email;
}
