package br.com.inteligenti.lavoutanovov2.service;

import java.util.List;

import br.com.inteligenti.lavoutanovov2.to.PaisTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by fernando on 26/12/17.
 */

public interface PaisClient {

    @GET("seguranca/{user}/getRel")
    Call<List<PaisTO>> reposForUser(@Path("user") String user);

    @POST("lavoutanovo/Cliente/salvar")
    Call<PaisTO> salvar(@Body PaisTO pais);

}
