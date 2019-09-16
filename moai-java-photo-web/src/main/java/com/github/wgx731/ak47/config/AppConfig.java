package com.github.wgx731.ak47.config;

import com.github.wgx731.ak47.audit.AuditorAwareImpl;
import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.repository.PhotoRepository;
import com.github.wgx731.ak47.repository.ProjectRepository;
import com.github.wgx731.ak47.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AppConfig {

    @Autowired
    private SecurityUtils securityUtils;

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl(securityUtils);
    }

    @Bean
    public CommandLineRunner loadData(PhotoRepository photoRepository, ProjectRepository projectRepository) {
        return (args) -> {
            for (int i = 0; i < 5; i++) {
                Project p = new Project();
                p.setName(String.format("project %d", i));
                projectRepository.save(p);
            }
        };
    }

}
