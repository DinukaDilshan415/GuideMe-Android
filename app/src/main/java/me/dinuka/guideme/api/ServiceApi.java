package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceApi {
    @GET("service/{key}")
    Call<List<HashMap<String, Object>>> getMainServices(@Path("key") String key);

    @POST("service/checkavailability")
    Call<HashMap<String, Object>> checkAvailability(@Body HashMap<String, Object> hashMap);

}
