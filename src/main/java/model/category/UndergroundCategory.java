package model.category;

import javafx.scene.paint.Color;

public class UndergroundCategory extends BlankCategory {
    private String name;
    private Color color;

    public UndergroundCategory() {
        name = "Underground";
        color = Color.BLUE;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

}
