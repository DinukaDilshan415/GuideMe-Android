package me.dinuka.guideme.api;

import java.util.List;

import me.dinuka.guideme.dto.FacilityDTO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface FacilityApi {
    @GET("facility/getall")
    Call<List<FacilityDTO>> getAllFacilities();
}
