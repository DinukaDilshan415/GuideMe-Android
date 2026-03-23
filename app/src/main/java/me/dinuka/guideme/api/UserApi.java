package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.UserAddressDTO;
import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @POST("user/register")
    Call<HashMap<String, Object>> userRegister(@Body UserDTO userDTO);

    @GET("user/{email}")
    Call<UserDTO> getCurrentUser(@Path("email") String email);

    @POST("user/saveorupdateaddress")
    Call<HashMap<String, Object>> userSaveOrUpdateAddress(@Body UserAddressDTO userAddressDTO);

    @PUT("user/updatemobile")
    Call<HashMap<String, Object>> userUpdateMobile(@Body UserDTO userDTO);

    @PUT("user/updatenameandimage")
    Call<HashMap<String, Object>> userUpdateNameAndImage(@Body UserDTO userDTO);

    @PUT("user/updatecountry")
    Call<HashMap<String, Object>> userUpdateCountry(@Body UserDTO userDTO);

    @GET("user/address/{email}")
    Call<UserAddressDTO> getCurrentUserAddress(@Path("email") String email);
}
