package com.github.wgx731.ak47.repository;

import com.github.wgx731.ak47.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Page<Photo> findAllByUploader(String uploader, Pageable pageable);

}
