package com.yeo.portfolio;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String headline;
    @Column(length = 2000) public String bio;
    public String email;
    public String instagramUrl;
    @ElementCollection public List<String> tasks = new ArrayList<>();
    @ElementCollection public List<String> tools = new ArrayList<>();
    public boolean sample = true;
    protected Profile() {}
}
