package me.dinuka.guideme.model;

import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThingsToDo {
    private int id;
    private String name;
    private double price;
    private String location;
    private double rating;
    private int reviewsCount;
    private List<String> images;
    private HashMap<String, Object> detailsMap;
}
