package com.github.wgx731.ak47.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuditorAwareImplTest {

    AuditorAwareImpl testCase;

    @BeforeEach
    void setUp() {
        testCase = new AuditorAwareImpl();
    }

    @AfterEach
    void tearDown() {
        testCase = null;
    }

    @Test
    void getCurrentAuditor() {
        assertThat(testCase.getCurrentAuditor().get()).isEqualTo(AuditorAwareImpl.AUDITOR);
    }
}