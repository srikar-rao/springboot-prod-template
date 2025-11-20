package com.dev.org.common.response.astro;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AstronautsResponse {

    @JsonProperty("people")
    private List<Astronaut> people;

    @JsonProperty("number")
    private int number;

    @JsonProperty("message")
    private String message;
}
