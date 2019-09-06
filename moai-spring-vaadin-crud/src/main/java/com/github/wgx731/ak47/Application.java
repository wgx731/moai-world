package com.github.wgx731.ak47;

import com.github.wgx731.ak47.audit.AuditorAwareImpl;
import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.repository.PhotoRepository;
import com.github.wgx731.ak47.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EntityScan(basePackages = {
    "com.github.wgx731.ak47.model"
})
public class Application {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    @Bean
    public CommandLineRunner loadData(PhotoRepository photoRepository, ProjectRepository projectRepository) {
        return (args) -> {
            List<Project> projects = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Project p = new Project();
                p.setName(String.format("project %d", i));
                projectRepository.save(p);
                projects.add(p);
            }
            /*
            for (int i = 0; i < 3; i++) {
                Project p = projects.get(i);
                for (int j = 0; j < 20; j++) {
                    photoRepository.save(new Photo(
                        p,
                        Photo.ProcessStatus.UPLOADED,
                        Files.readAllBytes(Paths.get("/tmp", "icon.png")),
                        "png",
                        "wgx",
                        "s1",
                        String.format("file:///s1/tmp/photo%d.png", j)
                    ));
                }
            }
            */
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
