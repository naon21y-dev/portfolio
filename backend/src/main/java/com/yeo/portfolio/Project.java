package com.yeo.portfolio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    public enum Category { SKINCARE, MAKEUP, LIFESTYLE }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public String title;
    @Enumerated(EnumType.STRING) public Category category;
    public String categoryLabel;
    public String summary;
    public String thumbnailUrl;
    @Column(length = 2000) public String purpose;
    @Column(length = 2000) public String target;
    @Column(length = 2000) public String message;
    @Column(length = 2000) public String intention;
    @Column(length = 2000) public String role;
    @Column(length = 2000) public String learning;
    public boolean sample = true;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC") public List<Content> contents = new ArrayList<>();
    protected Project() {}
    public Long getId() { return id; }
}
