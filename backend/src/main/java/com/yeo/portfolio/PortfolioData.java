package com.yeo.portfolio;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class PortfolioData {
    private final JsonNode information;
    private final JsonNode gallery;
    public PortfolioData() throws IOException {
        try (var stream = new ClassPathResource("portfolio.json").getInputStream()) {
            information = JsonMapper.builder().build().readTree(stream);
        }
        try (var stream = new ClassPathResource("gallery.json").getInputStream()) {
            gallery = JsonMapper.builder().build().readTree(stream);
        }
    }
    public JsonNode information() { return information; }
    public JsonNode gallery() { return gallery; }
}
