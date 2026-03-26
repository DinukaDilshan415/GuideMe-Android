package me.dinuka.guideme.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemReserve {
    private int id;
    private String bookingId;
    private String title;
    private String checkInDate;
    private String checkOutDate;
    private String created_at;
    private int adults;
    private int children;
    private int rooms;
    private String status;
    private String user_name;
    private String user_email;
    private String user_phone;
    private String user_country;
    private int categoryId;
}
