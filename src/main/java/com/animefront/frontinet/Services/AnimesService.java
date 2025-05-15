package com.animefront.frontinet.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.animefront.frontinet.Repositories.AnimesRepository;
import com.animefront.frontinet.Entities.AnimeEntity;

import java.util.*;


@Service
public class AnimesService {
    private final AnimesRepository animesRepository;

    public AnimesService(AnimesRepository animesRepository) {
        this.animesRepository = animesRepository;
    }

    public ResponseEntity<?> getAllAnimes(Pageable pageable) {
        Page<AnimeEntity> anime = animesRepository.findAll(pageable);
        return getResponseEntity(anime);
    }

    public ResponseEntity<?> getAnimeById(UUID id) {
        Optional<AnimeEntity> anime = animesRepository.findById(id);
        if (anime.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("Status", String.format("No anime found with ID %s", id));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(Collections.singletonMap("Anime", anime.get()));
    }

    public ResponseEntity<?> getAnimeByName(String animeName, Pageable pageable) {
        Page<AnimeEntity> anime = animesRepository.findAllByAnimeNameContaining(animeName, pageable);
        return getResponseEntity(anime);
    }

    private ResponseEntity<?> getResponseEntity(Page<AnimeEntity> anime) {
        Map<String, Object> response = new HashMap<>();
        response.put("TotalElements", anime.getTotalElements());
        response.put("TotalPages", anime.getTotalPages());
        response.put("CurrentPage", anime.getNumber());
        response.put("NumberOfElements", anime.getNumberOfElements());
        response.put("Movies", anime.getContent());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> addAnime(AnimeEntity animeToAdd) {
        Page<AnimeEntity> anime = animesRepository.findAllByAnimeNameContaining(
                animeToAdd.getAnimeName(),
                Pageable.unpaged());
        if (anime.getTotalElements()>0){
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Anime already exists with %d coincidences.", anime.getTotalElements())), HttpStatus.CONFLICT);
        } else {
            AnimeEntity savedAnime = animesRepository.save(animeToAdd);
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Anime with ID %s", savedAnime.getId())), HttpStatus.CREATED);
        }
    }

    public ResponseEntity<?> updateAnime(UUID id, AnimeEntity animeToUpdate) {
        Optional<AnimeEntity> anime = animesRepository.findById(id);
        if (anime.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Anime with ID %s not found", id)), HttpStatus.NOT_FOUND);
        }
        AnimeEntity existingAnime = anime.get();

        existingAnime.setAnimeName(animeToUpdate.getAnimeName());
        existingAnime.setAnimeYear(animeToUpdate.getAnimeYear());
        existingAnime.setAnimeCaps(animeToUpdate.getAnimeCaps());

        animesRepository.save(existingAnime);

        return ResponseEntity.ok(Collections.singletonMap("Status", String.format("Updated Anime with ID %s", existingAnime.getId())));
    }

    public ResponseEntity<?> deleteAnime(UUID id) {
        Optional<AnimeEntity> anime = animesRepository.findById(id);
        if (anime.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("Status", String.format("Anime with ID %s doesn't exist.", id)),HttpStatus.NOT_FOUND);
        }
        animesRepository.delete(anime.get());
        return ResponseEntity.ok(Collections.singletonMap("Status", String.format("Deleted Anime with ID %s", id)));
    }
}
