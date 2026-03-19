package me.dinuka.guideme.api;

import java.util.HashMap;
import java.util.List;

import me.dinuka.guideme.dto.PartnerDTO;
import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {
    @POST("user/register")
    Call<HashMap<String, Object>> userRegister(@Body UserDTO userDTO);

    @GET("user/{email}")
    Call<UserDTO> getCurrentUser(@Path("email") String email);
}
