package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.ListingTypeDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingApi {

    @GET("booking/{email}")
    Call<List<HashMap<String, Object>>> getUserBookings(@Path("email") String email);

    @POST("booking/addnewbooking")
    Call<HashMap<String, Object>> addNewBooking(@Body HashMap<String, Object> hashMap);
}
