package com.animefront.frontinet.Repositories;

import com.animefront.frontinet.Entities.AnimeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnimesRepository extends JpaRepository<AnimeEntity, UUID> {
    Page<AnimeEntity> findAllByAnimeNameContaining(String animeName, Pageable pageable);

    @Override
    Page<AnimeEntity> findAll(Pageable pageable);
}
