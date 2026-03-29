package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.ListingTypeDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.ReviewDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ListingApi {
    @GET("listing/types/getall")
    Call<List<ListingTypeDTO>> getAllListingTypes();

    @GET("listing/reviews/{lid}")
    Call<List<ReviewDTO>> getListingReviews(@Path("lid") int l_id);

    @GET("listing/{email}")
    Call<List<HashMap<String, Object>>> getPartnerListings(@Path("email") String email);

    @POST("listing/addnewlisting")
    Call<HashMap<String, Object>> addNewListing(@Body HashMap<String, Object> hashMap);

    @POST("listing/addreview")
    Call<HashMap<String, Object>> addNewReview(@Body ReviewDTO reviewDTO);
}
