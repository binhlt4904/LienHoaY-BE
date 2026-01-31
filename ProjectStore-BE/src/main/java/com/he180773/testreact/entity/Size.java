package com.he180773.testreact.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "[Size]")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private int id;
    @Column(name = "CODE", length = 50, nullable = false, unique = true)
    private String code;
    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
