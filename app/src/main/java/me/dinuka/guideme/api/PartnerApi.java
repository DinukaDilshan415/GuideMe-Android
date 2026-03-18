package me.dinuka.guideme.api;

import java.util.HashMap;

import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PartnerApi {
    @POST("partner/register")
    Call<HashMap<String, Object>> partnerRegister(@Body PartnerDTO partnerDTO);
}
