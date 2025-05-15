package com.animefront.frontinet.Controllers;

import com.animefront.frontinet.Entities.AnimeEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Controller
@RequestMapping("/web/animes")
public class AnimeWebController {

    private final String backendUrl = "https://animeproject-production.up.railway.app/api/v1/animes";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String listAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "animeName,asc") String[] sort,
            Model model
    ) {
        String sortParam = String.join(",", sort);
        String url = backendUrl + "?page=" + page + "&size=" + size + "&sort=" + sortParam;

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
        Map<String, Object> responseBody = response.getBody();

        model.addAttribute("animes", responseBody.get("Movies"));
        model.addAttribute("currentPage", responseBody.get("CurrentPage"));
        model.addAttribute("totalPages", responseBody.get("TotalPages"));
        model.addAttribute("size", size);
        model.addAttribute("sort", sortParam);

        return "anime-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("anime", new AnimeEntity());
        return "anime-new";
    }

    @PostMapping
    public String createAnime(@ModelAttribute AnimeEntity anime) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        anime.setId(UUID.randomUUID()); // Generar ID en el frontend
        HttpEntity<AnimeEntity> request = new HttpEntity<>(anime, headers);
        restTemplate.postForEntity(backendUrl, request, AnimeEntity.class);
        return "redirect:/web/animes";
    }

    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        ResponseEntity<Map> response = restTemplate.getForEntity(backendUrl + "/" + id, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // Extraer el mapa anidado de la clave "Anime"
        LinkedHashMap<String, Object> animeMap = (LinkedHashMap<String, Object>) responseBody.get("Anime");

        // Convertir el mapa a AnimeEntity manualmente
        AnimeEntity anime = new AnimeEntity();
        anime.setId(UUID.fromString((String) animeMap.get("id")));
        anime.setAnimeName((String) animeMap.get("animeName"));
        anime.setAnimeYear((String) animeMap.get("animeYear"));
        anime.setAnimeCaps((Integer) animeMap.get("animeCaps"));

        System.out.println("Objeto recibido: " + anime);

        model.addAttribute("anime", anime);
        return "anime-update";
    }



    @PostMapping("/update/{id}")
    public String updateAnime(@PathVariable UUID id, @ModelAttribute AnimeEntity anime) {
        anime.setId(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AnimeEntity> request = new HttpEntity<>(anime, headers);

        restTemplate.exchange(backendUrl + "/" + id, HttpMethod.PUT, request, Void.class);
        return "redirect:/web/animes";
    }

    @GetMapping("/delete/{id}")
    public String deleteAnime(@PathVariable UUID id) {
        restTemplate.delete(backendUrl + "/" + id);
        return "redirect:/web/animes";
    }
}
