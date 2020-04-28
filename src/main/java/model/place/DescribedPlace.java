package model.place;

import model.category.BlankCategory;

public class DescribedPlace extends Place {
    private String description;

    public DescribedPlace(String name, BlankCategory blankCategory, String description) {
        super(name, blankCategory);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
