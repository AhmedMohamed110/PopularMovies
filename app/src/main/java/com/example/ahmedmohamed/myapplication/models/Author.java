package com.example.ahmedmohamed.myapplication.models;


public class Author {

    private String name, content;

    public Author(String name, String content) {

        this.name = name;

        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
