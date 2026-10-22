package com.yeo.portfolio;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
@Transactional(readOnly = true)
public class PortfolioController {
    private final ProfileRepository profiles;
    private final ProjectRepository projects;
    private final ContentRepository contents;
    private final PortfolioData data;
    public PortfolioController(ProfileRepository profiles, ProjectRepository projects, ContentRepository contents, PortfolioData data) {
        this.profiles = profiles;
        this.projects = projects;
        this.contents = contents;
        this.data = data;
    }
    public record ProfileView(Long id, String name, String headline, String bio, String email, String instagramUrl, List<String> tasks, List<String> tools, boolean sample, tools.jackson.databind.JsonNode information) {}
    public record ProjectView(Long id, String title, Project.Category category, String categoryLabel, String summary, String thumbnailUrl, String purpose, String target, String message, String intention, String role, String learning, boolean sample, List<ContentView> contents) {}
    public record ContentView(Long id, Long projectId, Content.Type type, String title, String caption, String imageUrl, String thumbnailUrl, String videoUrl, String externalUrl, boolean sample, Content.Category category, String brand, String productName, String intention, String role, String videoSrc, String thumbnailSrc, String instagramUrl, tools.jackson.databind.JsonNode insights) {}
    static ContentView contentView(Content content) {
        return new ContentView(content.id, content.project.getId(), content.type, content.title, content.caption, content.imageUrl, content.thumbnailUrl, content.videoUrl, content.externalUrl, content.sample, content.category, content.brand, content.productName, content.intention, content.role, content.videoUrl, content.thumbnailUrl, content.externalUrl, content.insightsJson == null ? null : tools.jackson.databind.json.JsonMapper.builder().build().readTree(content.insightsJson));
    }
    static ProjectView projectView(Project project, boolean detailed) {
        return new ProjectView(project.id, project.title, project.category, project.categoryLabel, project.summary, project.thumbnailUrl, project.purpose, project.target, project.message, project.intention, project.role, project.learning, project.sample, detailed ? project.contents.stream().map(PortfolioController::contentView).toList() : List.of());
    }
    @GetMapping("/profile") public ProfileView profile() {
        Profile profile = profiles.findAll(Sort.by("id")).stream().findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new ProfileView(profile.id, profile.name, profile.headline, profile.bio, profile.email, profile.instagramUrl, List.copyOf(profile.tasks), List.copyOf(profile.tools), profile.sample, data.information());
    }
    @GetMapping("/projects") public List<ProjectView> projects() {
        return projects.findAll(Sort.by("id")).stream().map(project -> projectView(project, false)).toList();
    }
    @GetMapping("/projects/{id}") public ProjectView project(@PathVariable Long id) {
        return projectView(projects.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")), true);
    }
    @GetMapping("/contents") public List<ContentView> contents() {
        return contents.findAll(Sort.by("id")).stream().map(PortfolioController::contentView).toList();
    }
}
