package me.dinuka.guideme.api;

import java.util.List;

import me.dinuka.guideme.dto.UserDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    @POST("user/register")
    Call<String> userRegister(@Body UserDTO userDTO);
}
