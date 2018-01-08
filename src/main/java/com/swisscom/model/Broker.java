package com.swisscom.model;

import java.util.UUID;

public class Broker {
    private String id;
    private String owner;
    private String name;
    private String url;
    private String username;
    private String password;

    public Broker() {

    }

    public Broker(String owner, String name, String url, String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.owner = owner;
        this.name = name;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
