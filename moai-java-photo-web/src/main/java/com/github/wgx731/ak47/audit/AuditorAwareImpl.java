package com.github.wgx731.ak47.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    public static final String AUDITOR = "auditor";

    // TODO: change user to current login user
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(AUDITOR);
    }

}
