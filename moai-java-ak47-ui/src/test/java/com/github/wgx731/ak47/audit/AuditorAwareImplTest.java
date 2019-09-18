package com.github.wgx731.ak47.audit;

import com.github.wgx731.ak47.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class AuditorAwareImplTest {

    private static final String AUDITOR = "auditor";

    @Mock
    private SecurityUtils securityUtils;

    private AuditorAwareImpl testCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        testCase = new AuditorAwareImpl(securityUtils);
        Mockito.when(securityUtils.getCurrentUser()).thenReturn(AUDITOR);
    }

    @AfterEach
    void tearDown() {
        testCase = null;
    }

    @Test
    void getCurrentAuditor() {
        assertThat(testCase.getCurrentAuditor().isPresent()).isTrue();
        assertThat(testCase.getCurrentAuditor().get()).isEqualTo(AUDITOR);
    }
}