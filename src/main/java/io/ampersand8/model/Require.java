package io.ampersand8.model;

public class Require {
    private Integer id;
    private String value;

    public Require() {

    }

    public Require(String value) {
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
