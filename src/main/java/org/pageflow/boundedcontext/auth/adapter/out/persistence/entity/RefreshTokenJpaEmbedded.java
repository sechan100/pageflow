package org.pageflow.boundedcontext.auth.adapter.out.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.Instant;

/**
 * @author : sechan
 */
@Embeddable
@Data
public class RefreshTokenJpaEmbedded {
    private Long exp;
    private Long iat;

    protected RefreshTokenJpaEmbedded() {
    }

    public RefreshTokenJpaEmbedded(Instant iat, Instant exp) {
        this.iat = iat.toEpochMilli();
        this.exp = exp.toEpochMilli();
    }
}
