package com.example.foodtofood;

public class Ingredient {
    private String name;
    private String id;
    private boolean checked;

    public Ingredient() {

    }

    public Ingredient(String name, String id, boolean checked) {
        this.name = name;
        this.id = id;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
