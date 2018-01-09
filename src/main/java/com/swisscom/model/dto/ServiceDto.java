package com.swisscom.model.dto;

import java.util.List;

public class ServiceDto {
    private String id;
    private String broker;
    private String name;
    private String description;
    private String[] tags;
    private String[] requires;
    private boolean bindable;
    private boolean plan_updateable;
    private List<PlanDto> plans;


    public ServiceDto() {

    }

    public ServiceDto(String id, String broker, String name, String description, String[] tags, String[] requires, boolean bindable, boolean plan_updateable, List<PlanDto> plans) {
        this.id = id;
        this.broker = broker;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.requires = requires;
        this.bindable = bindable;
        this.plan_updateable = plan_updateable;
        this.plans = plans;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
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

    public boolean isBindable() {
        return bindable;
    }

    public void setBindable(boolean bindable) {
        this.bindable = bindable;
    }

    public boolean isPlan_updateable() {
        return plan_updateable;
    }

    public void setPlan_updateable(boolean plan_updateable) {
        this.plan_updateable = plan_updateable;
    }

    public List<PlanDto> getPlans() {
        return plans;
    }

    public void setPlans(List<PlanDto> plans) {
        this.plans = plans;
    }
}
