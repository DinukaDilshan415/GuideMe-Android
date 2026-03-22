package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ThingsToDoApi {
    @GET("thingstodo/{key}")
    Call<List<HashMap<String, Object>>> getMainThingsToDo(@Path("key") String key);

}
