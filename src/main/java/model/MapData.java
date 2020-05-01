package model;

import model.category.BusCategory;
import model.place.NamedPlace;
import model.place.Place;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapData {
    private Map<Position, Place> allPlaces;
    private Set<Place> hidden;
    private Set<Place> marked;

    public MapData() {
        allPlaces = new HashMap<>();
        hidden = new HashSet<>();
        marked = new HashSet<>();
    }

    public void search(String placeName) {
        marked.clear();

        for (Place place : allPlaces.values()) {
            if (place.getName().equals(placeName)) {
                hidden.remove(place);
                marked.add(place);
            }
        }
    }

    public void hide() {
        hidden.addAll(marked);
        marked.clear();
    }

    public void hideCategory(String category) {
        for (Place place : allPlaces.values()) {
            if (place.getCategory().getName().equals(category)) {
                hidden.add(place);
                marked.remove(place);
            }
        }
    }

    public void showCategory(String category) {
        hidden.removeIf(place -> place.getCategory().getName().equals(category));
    }

    public void printHidden() {
        for (Place place : hidden) {
            System.out.println(place);
        }
    }

    public void remove() {
        for (Place place : marked) {
            allPlaces.entrySet()
                    .removeIf( entry -> (
                            place.equals(entry.getValue()
                            )
                    ));
        }
        marked.clear();
    }

    public String getPlaceByCoordinates(int x, int y) {
        if (allPlaces.get(new Position(x, y)) == null) {
            return "Not Found";
        }
        Place place = allPlaces.get(new Position(x, y));
        marked.add(place);
        hidden.remove(place);
        return place.getName();
    }


    public static void main(String[] args) {
        Map<Position, Place> map = new HashMap<>();
        map.put(
                new Position(1, 1),
                new NamedPlace("Larvi", new BusCategory())
        );
        map.put(
                new Position(1, 4),
                new NamedPlace("Huset", new BusCategory())
        );
        map.put(
                new Position(1, 5),
                new NamedPlace("Gatan", new BusCategory())
        );
        MapData mapData = new MapData();
        mapData.setAllPlaces(map);

        System.out.println("------getPlaceByCoordinates() return value-------");
        System.out.println(mapData.getPlaceByCoordinates(1, 4));

        System.out.println("------Marked Places-----");
        for (Place place : mapData.marked)
            System.out.println(place.getName());

    }

    public Map<Position, Place> getAllPlaces() {
        return allPlaces;
    }

    public void setAllPlaces(Map<Position, Place> allPlaces) {
        this.allPlaces = allPlaces;
    }

    public void printAllPlaces() {
        for (Map.Entry<Position, Place> entry : allPlaces.entrySet()) {
            System.out.println("Position: " + entry.getKey().getX() + ", " + entry.getKey().getY() + " | "
            + "Place: " + entry.getValue().getName() + ", "
            + entry.getValue().getCategory().getName() + ", "
            );
        }
    }

    public Set<Place> getHidden() {
        return hidden;
    }

    public void setHidden(Set<Place> hidden) {
        this.hidden = hidden;
    }

    public Set<Place> getMarked() {
        return marked;
    }

    public void setMarked(Set<Place> marked) {
        this.marked = marked;
    }
}
