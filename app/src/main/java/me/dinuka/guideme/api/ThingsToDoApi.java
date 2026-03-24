package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ThingsToDoApi {
    @GET("thingstodo/{key}")
    Call<List<HashMap<String, Object>>> getMainThingsToDo(@Path("key") String key);

    @POST("thingstodo/checkavailability")
    Call<HashMap<String, Object>> checkAvailability(@Body HashMap<String, Object> hashMap);

}
