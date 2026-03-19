package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.FacilityDTO;
import me.dinuka.guideme.dto.PartnerAddressDTO;
import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PartnerApi {
    @POST("partner/register")
    Call<HashMap<String, Object>> partnerRegister(@Body PartnerDTO partnerDTO);

    @POST("partner/saveorupdateaddress")
    Call<HashMap<String, Object>> partnerSaveOrUpdateAddress(@Body PartnerAddressDTO partnerAddressDTO);

    @PUT("partner/updatemobile")
    Call<HashMap<String, Object>> partnerUpdateMobile(@Body PartnerDTO partnerDTO);

    @PUT("partner/updatenameandimage")
    Call<HashMap<String, Object>> partnerUpdateNameAndImage(@Body PartnerDTO partnerDTO);

    @PUT("partner/updatecountry")
    Call<HashMap<String, Object>> partnerUpdateContry(@Body PartnerDTO partnerDTO);

    @GET("partner/{email}")
    Call<PartnerDTO> getCurrentPartner(@Path("email") String email);

    @GET("partner/address/{email}")
    Call<PartnerAddressDTO> getCurrentPartnerAddress(@Path("email") String email);

}
