package com.swisscom.model.dto;

public class PlanDto {
    private String id;
    private String service;
    private String name;
    private String description;
    private boolean free;
    private boolean bindable;
    private String[] tags;
    private String[] requires;

    public PlanDto() {

    }

    public PlanDto(String id, String service, String name, String description, boolean free, boolean bindable, String[] tags, String[] requires) {
        this.id = id;
        this.service = service;
        this.name = name;
        this.description = description;
        this.free = free;
        this.bindable = bindable;
        this.tags = tags;
        this.requires = requires;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isBindable() {
        return bindable;
    }

    public void setBindable(boolean bindable) {
        this.bindable = bindable;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getRequires() {
        return requires;
    }

    public void setRequires(String[] requires) {
        this.requires = requires;
    }
}
