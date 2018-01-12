package io.ampersand8.model;

public class Tag {
    private Integer id;
    private String value;

    public Tag() {

    }

    public Tag(String value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
