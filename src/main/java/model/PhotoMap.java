package model;

import model.category.BusCategory;
import model.place.NamedPlace;
import model.place.Place;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PhotoMap {
    Map<Position, Place> allPlaces;
    Set<Place> hidden;
    Set<Place> marked;

    public PhotoMap(Map<Position, Place> allPlaces) {
        this.allPlaces = allPlaces;
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

    public void remove() {
        for (Place place : marked) {
            allPlaces.remove(getKey(place));
            hidden.remove(place);
            marked.remove(place);
        }
    }

    // A helper method to find the key for a specific Place-object
    // in allPlaces map
    private Position getKey(Place place) {
        for (Map.Entry<Position, Place> entry : allPlaces.entrySet()) {
            if (entry.getValue().equals(place)) return entry.getKey();
        }
        return null;
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
        PhotoMap photoMap = new PhotoMap(map);

        System.out.println("------getPlaceByCoordinates() return value-------");
        System.out.println(photoMap.getPlaceByCoordinates(1, 4));

        System.out.println("------Marked Places-----");
        for (Place place : photoMap.marked)
            System.out.println(place.getName());

    }
}
