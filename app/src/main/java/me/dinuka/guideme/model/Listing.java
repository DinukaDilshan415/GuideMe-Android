package me.dinuka.guideme.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Listing {
    private int id;
    private String listingId;
    private String status;
    private String category;
    private String name;
    private double price;
    private String location;
    private String images;
    private String datetime;
}
