package com.example.lab5_20220270.model;

import java.io.Serializable;

public class Course implements Serializable {
    private String id;
    private String name;
    private String type;
    private int frequencyValue;
    private String frequencyUnit;
    private long nextSessionMillis;
    private String actionSuggestion;

    public Course() {
    }

    public Course(String id, String name, String type, int frequencyValue, String frequencyUnit, long nextSessionMillis, String actionSuggestion) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.frequencyValue = frequencyValue;
        this.frequencyUnit = frequencyUnit;
        this.nextSessionMillis = nextSessionMillis;
        this.actionSuggestion = actionSuggestion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFrequencyValue() {
        return frequencyValue;
    }

    public void setFrequencyValue(int frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    public String getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(String frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

    public long getNextSessionMillis() {
        return nextSessionMillis;
    }

    public void setNextSessionMillis(long nextSessionMillis) {
        this.nextSessionMillis = nextSessionMillis;
    }

    public String getActionSuggestion() {
        return actionSuggestion;
    }

    public void setActionSuggestion(String actionSuggestion) {
        this.actionSuggestion = actionSuggestion;
    }
}
