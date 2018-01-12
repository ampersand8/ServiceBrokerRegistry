package io.ampersand8.model;

import io.ampersand8.model.dto.PlanDto;
import io.ampersand8.model.dto.ServiceDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Service {
    private String id;
    private Broker broker;
    private String serviceId;
    private String name;
    private String description;
    private List<Tag> tags;
    private List<Require> requires;
    private boolean bindable;
    private boolean plan_updateable;
    private List<Plan> plans;


    public Service() {

    }

    public static Service makeServiceFromDto(ServiceDto dto) {
        return makeService(dto.getBroker(), dto.getId(), dto.getName(), dto.getDescription(), dto.getTags(), dto.getRequires(), dto.isBindable(), dto.isPlan_updateable(), dto.getPlans());
    }

    public static Service makeService(Broker broker, String serviceId, String name, String description, String[] tags, String[] requires, boolean bindable, boolean plan_updateable, List<PlanDto> plans) {
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
        List<Plan> planList = new ArrayList<>();
        if (plans != null) {
            for (PlanDto planDto : plans) {
                planList.add(Plan.makePlanFromDto(planDto));
            }
        }
        return new Service(broker, serviceId, name, description, tagList, requireList, bindable, plan_updateable, planList);
    }

    public Service(Broker broker, String serviceId, String name, String description, List<Tag> tags, List<Require> requires, boolean bindable, boolean plan_updateable, List<Plan> plans) {
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

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
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

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public String getAllTags() {
        return this.tags.stream().map(Tag::getValue).collect(Collectors.joining(","));
    }

    public String getAllRequires() {
        return this.requires.stream().map(Require::getValue).collect(Collectors.joining(","));
    }
}
