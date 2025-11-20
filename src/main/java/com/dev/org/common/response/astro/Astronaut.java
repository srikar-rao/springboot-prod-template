package com.dev.org.common.response.astro;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Astronaut {

    @JsonProperty("name")
    private String name;

    @JsonProperty("craft")
    private String craft;
}
