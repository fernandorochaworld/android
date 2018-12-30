package br.com.inteligenti.lavoutanovov2.service;

import java.util.List;
import java.util.Map;

import br.com.inteligenti.lavoutanovov2.to.ProfissionalTO;
import br.com.inteligenti.lavoutanovov2.to.ServicoTO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by fernando on 31/12/17.
 */

public interface ProfissionalCO {


    @POST("lavoutanovo/Login/logarProfissional")
    Call<ProfissionalTO> logarProfissional(@QueryMap Map<String, String> params);

    @POST("lavoutanovo/Login/esqueciSenha")
    Call<ProfissionalTO> esqueciSenha(@QueryMap Map<String, String> params);

    @Multipart
    @POST("documento/Documento/upload")
    Call<ResponseBody> uploadLogo(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @GET("lavoutanovo/Login/carregarDadosProfissional")
    Call<ResponseBody> carregarDados(@Query("id_profissional") Integer id_profissional);

    @GET("lavoutanovo/MntProfissional/check_point")
    Call<List<ServicoTO>> check_point(
        @Query("id_profissional") Integer id_profissional,
        @Query("codg_localizacao") String codg_localizacao
    );

}
