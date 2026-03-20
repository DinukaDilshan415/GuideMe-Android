package me.dinuka.guideme.api;

import java.util.List;

import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.ListingTypeDTO;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ListingApi {
    @GET("listing/types/getall")
    Call<List<ListingTypeDTO>> getAllListingTypes();
}
