package com.github.wgx731.ak47.config;

import com.github.wgx731.ak47.audit.AuditorAwareImpl;
import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.repository.ProjectRepository;
import com.github.wgx731.ak47.security.SecurityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Bean
    public AuditorAware<String> auditorAware(SecurityUtils securityUtils) {
        return new AuditorAwareImpl(securityUtils);
    }

    @Bean
    public CommandLineRunner loadData(ProjectRepository projectRepository) {
        return (args) -> {
            List<String> projectNames = projectRepository.findAll()
                .stream()
                .map(Project::getName)
                .collect(Collectors.toList());
            for (int i = 0; i < 5; i++) {
                Project p = new Project();
                p.setName(String.format("project %d", i));
                if (!projectNames.contains(p.getName())) {
                    projectRepository.save(p);
                }
            }
        };
    }

}
