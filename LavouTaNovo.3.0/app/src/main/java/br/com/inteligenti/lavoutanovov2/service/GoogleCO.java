package br.com.inteligenti.lavoutanovov2.service;

/**
 * Created by fernando on 12/01/18.
 */

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleCO {

    @GET("json")
    Call<ResponseBody> getEndereco(@Query(value="latlng", encoded=false) String latlng);

}
