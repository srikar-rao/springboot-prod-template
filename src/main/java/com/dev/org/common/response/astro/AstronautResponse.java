package com.dev.org.common.response.astro;

import java.util.List;

public record AstronautResponse(List<Astronaut> people, int number, String message) {}
