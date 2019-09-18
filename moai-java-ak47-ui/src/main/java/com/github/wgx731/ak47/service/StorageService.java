package com.github.wgx731.ak47.service;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.model.Project;
import com.github.wgx731.ak47.repository.PhotoRepository;
import com.github.wgx731.ak47.repository.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageService {

    @NonNull
    private PhotoRepository photoRepository;

    @NonNull
    private ProjectRepository projectRepository;

    public Page<Photo> listUserPhotosByPage(String uploader, PageRequest pageRequest) {
        return photoRepository.findAllByUploader(uploader, pageRequest);
    }

    public List<Project> listAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Photo> getPhotoById(Long id) {
        return photoRepository.findById(id);
    }

    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    public void delete(Photo photo) {
        photoRepository.deleteById(photo.getId());
    }

}
