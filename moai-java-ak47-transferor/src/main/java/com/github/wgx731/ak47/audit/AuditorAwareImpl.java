package com.github.wgx731.ak47.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    public static final String AUDITOR = "IMAGE_TRANSFEROR_APP";

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(AUDITOR);
    }

}
