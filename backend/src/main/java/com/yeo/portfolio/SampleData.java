package com.yeo.portfolio;

import java.util.stream.StreamSupport;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

@Component
public class SampleData implements CommandLineRunner {
    private final ProfileRepository profiles;
    private final ProjectRepository projects;
    private final PortfolioData data;
    public SampleData(ProfileRepository profiles, ProjectRepository projects, PortfolioData data) {
        this.profiles = profiles;
        this.projects = projects;
        this.data = data;
    }
    @Override @Transactional public void run(String... args) {
        if (profiles.count() > 0) return;
        JsonNode information = data.information();
        Profile profile = new Profile();
        profile.name = information.path("koreanName").asText();
        profile.headline = information.path("headline").asText();
        profile.bio = String.join("\n\n", StreamSupport.stream(information.path("about").path("paragraphs").spliterator(), false).map(JsonNode::asText).toList());
        profile.email = information.path("email").asText();
        profile.instagramUrl = information.path("instagram").asText();
        profile.tasks = StreamSupport.stream(information.path("capabilities").spliterator(), false).map(item -> item.path("title").asText() + ": " + item.path("description").asText()).toList();
        profile.tools = StreamSupport.stream(information.path("tools").spliterator(), false).map(JsonNode::asText).toList();
        profile.sample = false;
        profiles.save(profile);
        int index = 0;
        for (JsonNode category : information.path("works").path("categories")) {
            JsonNode entry = information.path("works").path("projects").get(index);
            create(category, entry, new String[]{"skincare", "makeup", "scent"}[index]);
            index++;
        }
        for (JsonNode entry : data.gallery().path("items")) {
            Project project = projects.findById(entry.path("projectId").asLong()).orElseThrow();
            Content content = new Content();
            content.project = project;
            content.type = Content.Type.valueOf(entry.path("type").asText());
            content.title = entry.path("title").asText();
            content.brand = entry.path("brand").asText();
            content.productName = entry.path("productName").asText();
            content.caption = entry.path("caption").asText();
            content.intention = entry.path("intention").asText();
            content.role = entry.path("role").asText();
            content.imageUrl = entry.path("imageUrl").asText();
            content.thumbnailUrl = entry.has("thumbnailSrc") ? entry.path("thumbnailSrc").asText() : entry.path("thumbnailUrl").asText();
            content.videoUrl = entry.has("videoSrc") ? entry.path("videoSrc").asText() : entry.path("videoUrl").asText();
            content.externalUrl = entry.has("instagramUrl") ? entry.path("instagramUrl").asText() : entry.path("externalUrl").asText();
            content.sample = entry.path("sample").asBoolean();
            content.insightsJson = entry.has("insights") ? entry.path("insights").toString() : null;
            project.contents.add(content);
            projects.save(project);
        }
    }
    private void create(JsonNode category, JsonNode entry, String image) {
        Project project = new Project();
        project.title = entry.path("title").asText().isBlank() ? category.path("title").asText() : entry.path("title").asText();
        project.category = Project.Category.valueOf(category.path("key").asText());
        project.categoryLabel = category.path("title").asText();
        project.summary = entry.path("summary").asText().isBlank() ? category.path("description").asText() : entry.path("summary").asText();
        project.thumbnailUrl = "/media/" + image + ".svg";
        project.purpose = entry.path("purpose").asText();
        project.target = "";
        project.message = entry.path("message").asText();
        project.intention = entry.path("intention").asText();
        project.role = entry.path("role").asText();
        project.learning = entry.path("learning").asText();
        projects.save(project);
    }
}
