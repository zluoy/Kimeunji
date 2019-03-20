package com.eunji.mobile_st_unitas;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoAPI {

    public static final String url = "https://dapi.kakao.com/";
    public static final String Key = "7de3d3c4635ca4328a7c47b30119887e";

//    @Headers("{Authorization: KakaoAK {app_key}")
    @GET("v2/search/image")
    Call<JsonObject> getJson(@Header("Authorization") String key, @Query("query") String query);

}
