package br.com.inteligenti.lavoutanovov2.service;

import java.util.List;

import br.com.inteligenti.lavoutanovov2.to.CarroTO;
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

/**
 * Created by fernando on 31/12/17.
 */

public interface CarroCO {

    @POST("lavoutanovo/Carro/salvar")
    Call<CarroTO> salvar(@Body CarroTO carroTO);

    @Multipart
    @POST("documento/Documento/upload")
    Call<ResponseBody> uploadLogo(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @GET("seguranca/Carro/getRel")
    Call<List<CarroTO>> getRel(@Query("id_cliente") Integer id_cliente);
}
