package com.animefront.frontinet.Controllers;

import com.app.animesinventory.Entities.AnimeEntity;
import com.app.animesinventory.Services.AnimesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/animes")
@Validated
public class AnimesController {
    private final AnimesService animesService;

    public AnimesController(AnimesService animesService) {this.animesService = animesService;}

    @GetMapping
    public ResponseEntity<?> getAllAnimes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "animeName,asc") String[] sort) {
            try {
                Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
                return animesService.getAllAnimes(pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid sorting direction. Use 'asc' or 'desc'.");
            }
    }

    private Sort.Order parseSort(String[] sort) {
        if (sort.length < 2) {
            throw new IllegalArgumentException("Sort parameter must have both field and direction (e.g., 'animeYear,desc').");
        }

        String property = sort[0];
        String direction = sort[1].toLowerCase();

        List<String> validDirections = Arrays.asList("asc", "desc");
        if (!validDirections.contains(direction)) {
            throw new IllegalArgumentException("Invalid sort direction. Use 'asc' or 'desc'.");
        }

        return new Sort.Order(Sort.Direction.fromString(direction), property);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnime(@PathVariable String id) {
        UUID uuid = UUID.fromString(id);
        return animesService.getAnimeById(id);}

    @GetMapping("/search")
    public ResponseEntity<?> getAnimesByName(
            @RequestParam String animeName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "animeName,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        return animesService.getAnimeByName(animeName, pageable);
    }

    @PostMapping
    public ResponseEntity<?> insertAnime(@Valid @RequestBody AnimeEntity animeEntity){
        System.out.println("todo bien");
        return animesService.addAnime(animeEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnime(@PathVariable String id, @Valid @RequestBody AnimeEntity animeEntity){
        UUID uuid = UUID.fromString(id);
        return animesService.updateAnime(id, animeEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnime(@PathVariable String id){
        UUID uuid = UUID.fromString(id);
        return animesService.deleteAnime(id);
    }
}
