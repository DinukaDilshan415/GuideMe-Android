package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PartnerApi {
    @POST("partner/register")
    Call<HashMap<String, Object>> partnerRegister(@Body PartnerDTO partnerDTO);

    @GET("partner/{email}")
    Call<PartnerDTO> getCurrentPartner(@Path("email") String email);

}
