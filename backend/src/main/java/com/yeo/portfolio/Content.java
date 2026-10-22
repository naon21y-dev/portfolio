package com.yeo.portfolio;

import jakarta.persistence.*;

@Entity
public class Content {
    public enum Type { REEL, FEED }
    public enum Category { SKINCARE, MAKEUP, HAIR_BODY, FRAGRANCE }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id")
    public Project project;
    @Enumerated(EnumType.STRING) public Type type;
    public String title;
    @Enumerated(EnumType.STRING) public Category category;
    public String brand;
    public String productName;
    @Column(length = 2000) public String intention;
    @Column(length = 2000) public String role;
    public String caption;
    public String imageUrl;
    public String thumbnailUrl;
    public String videoUrl;
    public String externalUrl;
    @Lob public String insightsJson;
    public boolean sample = true;
    protected Content() {}
}
