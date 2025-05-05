package com.animefront.frontinet.Repositories;

import com.app.animesinventory.Entities.AnimeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimesRepository extends JpaRepository<AnimeEntity, String> {
    Page<AnimeEntity> findAllByAnimeNameContaining(String animeName, Pageable pageable);

    @Override
    Page<AnimeEntity> findAll(Pageable pageable);
}
