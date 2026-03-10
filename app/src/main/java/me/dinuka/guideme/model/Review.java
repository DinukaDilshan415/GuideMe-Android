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
public class Review {
    private int id;
    private String user;
    private String image;
    private String location;
    private String date;
    private String review;
    private double rating;
}
