package model.place;

import model.category.BlankCategory;

public abstract class Place {
    private String name;
    private BlankCategory category;

    public Place(String name, BlankCategory category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlankCategory getCategory() {
        return category;
    }

    public void setCategory(BlankCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", category=" + category.getName() +
                '}';
    }
}
