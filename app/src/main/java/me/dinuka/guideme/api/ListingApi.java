package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.ListingTypeDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ListingApi {
    @GET("listing/types/getall")
    Call<List<ListingTypeDTO>> getAllListingTypes();

    @POST("listing/addnewlisting")
    Call<HashMap<String, Object>> addNewListing(@Body HashMap<String, Object> hashMap);
}
