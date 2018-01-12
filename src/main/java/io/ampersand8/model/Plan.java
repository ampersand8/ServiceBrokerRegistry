package io.ampersand8.model;

import io.ampersand8.model.dto.PlanDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Plan {
    private String id;
    private String service;
    private String planId;
    private String name;
    private String description;
    private boolean free;
    private boolean bindable;
    private List<Tag> tags;
    private List<Require> requires;

    public Plan() {

    }

    public static Plan makePlanFromDto(PlanDto dto) {
        return makePlan(dto.getService(), dto.getId(), dto.getName(), dto.getDescription(), dto.isFree(), dto.isBindable(), dto.getTags(), dto.getRequires());
    }

    public static Plan makePlan(String service, String planId, String name, String description, boolean free, boolean bindable, String[] tags, String[] requires) {
        List<Tag> tagList = new ArrayList<>();
        if (tags != null) {
            for (String tag : tags) {
                tagList.add(new Tag(tag));
            }
        }
        List<Require> requireList = new ArrayList<>();
        if (requires != null) {
            for (String require : requires) {
                requireList.add(new Require(require));
            }
        }
        return new Plan(service, planId, name, description, free, bindable, tagList, requireList);
    }

    public Plan(String service, String planId, String name, String description, boolean free, boolean bindable, List<Tag> tags, List<Require> requires) {
        this.id = UUID.randomUUID().toString();
        this.service = service;
        this.planId = planId;
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

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Require> getRequires() {
        return requires;
    }

    public void setRequires(List<Require> requires) {
        this.requires = requires;
    }
}
