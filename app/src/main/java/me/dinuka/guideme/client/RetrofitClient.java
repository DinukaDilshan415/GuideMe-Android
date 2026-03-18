package me.dinuka.guideme.client;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String ngrokURL = "https://4dfc-2402-d000-a500-3b5f-5c44-e1ba-7253-f9da.ngrok-free.app";
    public static final String BASE_URL = ngrokURL+"/guide_me/";
    /// emulators ==> [...](http://10.0.2.2:8080/api/v1)
    /// physical device ==> open cmd -> ipconfig -> IPv4 Address ==> [...](http://192.168.8.142:8080/api/v1)

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
