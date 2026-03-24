package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.ListingTypeDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HotelApi {
    @GET("hotels/{key}")
    Call<List<HashMap<String, Object>>> getMainHotels(@Path("key") String key);

    @POST("hotels/checkavailability")
    Call<HashMap<String, Object>> checkAvailability(@Body HashMap<String, Object> hashMap);
}
