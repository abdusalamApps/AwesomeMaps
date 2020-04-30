package model.category;


import javafx.scene.paint.Color;

public class BlankCategory {

    protected String name;
    protected Color color;

    public BlankCategory() {
        this.name = "Blank";
        this.color = Color.BLACK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
