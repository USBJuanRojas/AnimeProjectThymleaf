package com.animefront.frontinet.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimeEntity {

    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("animeName")
    @NotBlank(message = "Anime name is required")
    @Size(min = 3, max = 100, message = "Anime name must be between 3 and 100 characters")
    private String animeName;

    @JsonProperty("animeYear")
    @NotBlank(message = "Year is required")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be a valid 4-digit number (1900-2099)")
    private String animeYear;

    @JsonProperty("animeCaps")
    @NotNull(message = "Caps are required")
    @Min(value = 1, message = "Caps must be at least 1")
    private Integer animeCaps;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }
}
