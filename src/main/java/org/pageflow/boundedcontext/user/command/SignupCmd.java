package org.pageflow.boundedcontext.user.command;

import lombok.Getter;
import lombok.Setter;
import org.pageflow.boundedcontext.user.dto.SignupForm;
import org.pageflow.shared.infra.domain.AggregateRoot;
import org.pageflow.shared.infra.domain.DomainEvent;

@SuppressWarnings("NonSerializableFieldInSerializableClass")
@Getter
@Setter
public class SignupCmd extends DomainEvent {

    private SignupForm form;

    public SignupCmd(AggregateRoot source, SignupForm form){
        super(source);
        this.form = form;
    }
}
