package com.dev.org.interfaces.api;

import com.dev.org.client.AstroClient;
import com.dev.org.common.dto.HelloResponse;
import com.dev.org.common.response.astro.AstronautsResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World REST Controller.
 */
@RestController
@RequiredArgsConstructor
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    private final AstroClient astroClient;

    /**
     * Returns a greeting message.
     *
     * @param name the name to greet (optional, defaults to "World")
     * @return HelloResponse with greeting message
     */
    @GetMapping("/hello")
    public ResponseEntity<HelloResponse> sayHello(
            @RequestParam(value = "name", defaultValue = "World") String name) {

        logger.info("Processing hello request for name: {}", name);

        String message = String.format("Hello, %s!", name);
        String timestamp = Instant.now().toString();

        HelloResponse response = new HelloResponse(message, timestamp, name);

        logger.debug("Generated hello response: {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/astro")
    public ResponseEntity<AstronautsResponse> getAstros() {
        return ResponseEntity.ok(astroClient.getAstronauts());
    }
}
