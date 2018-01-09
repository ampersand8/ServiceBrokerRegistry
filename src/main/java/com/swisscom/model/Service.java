package com.swisscom.model;

import java.util.UUID;

public class Service {
    private String id;
    private String broker;
    private String serviceId;
    private String name;
    private String description;
    private String[] tags;
    private String[] requires;
    private boolean bindable;
    private boolean plan_updateable;
    private Plan[] plans;


    public Service() {

    }

    public Service(String broker, String serviceId, String name, String description, String[] tags, String[] requires, boolean bindable, boolean plan_updateable, Plan[] plans) {
        this.id = UUID.randomUUID().toString();
        this.broker = broker;
        this.serviceId = serviceId;
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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public Plan[] getPlans() {
        return plans;
    }

    public void setPlans(Plan[] plans) {
        this.plans = plans;
    }
}
