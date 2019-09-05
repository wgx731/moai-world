package com.github.wgx731.ak47.service;

import com.github.wgx731.ak47.model.Photo;
import com.github.wgx731.ak47.repository.PhotoRepository;
import com.github.wgx731.ak47.repository.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageService {

    @NonNull
    private PhotoRepository photoRepository;

    @NonNull
    private ProjectRepository projectRepository;

    public Page<Photo> listAllPhotosByPage(PageRequest pageRequest) {
        return photoRepository.findAll(pageRequest);
    }


}
