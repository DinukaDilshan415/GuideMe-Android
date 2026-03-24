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
public class MyBooking {
    private int id;
    private String bookingId;
    private String name;
    private double price;
    private String location;
    private double rating;
    private String images;
    private String checkInDate;
    private String checkOutDate;
    private int adults;
    private int children;
    private String datetime;
    private String category;
    private String status;
}
