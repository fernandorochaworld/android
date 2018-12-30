package br.com.inteligenti.lavoutanovov2.service;

import java.util.List;
import java.util.Map;

import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
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

public interface ClienteCO {

    @POST("lavoutanovo/Cliente/salvar")
    Call<ClienteTO> salvar(@Body ClienteTO clienteTO);

    @POST("lavoutanovo/Login/logar")
    Call<ClienteTO> logar(@QueryMap Map<String, String> params);

    @POST("lavoutanovo/Login/logarProfissional")
    Call<ClienteTO> logarProfissional(@QueryMap Map<String, String> params);

    @POST("lavoutanovo/Login/esqueciSenha")
    Call<ClienteTO> esqueciSenha(@QueryMap Map<String, String> params);

    @POST("lavoutanovo/Login/registrarLoginFacebook")
    Call<ClienteTO> registrarLoginFacebook(@Body ClienteTO clienteTO);

    @Multipart
    @POST("documento/Documento/upload")
    Call<ResponseBody> uploadLogo(@Part MultipartBody.Part image, @Part("name") RequestBody name);

    @GET("lavoutanovo/Login/carregarDados")
    Call<ResponseBody> carregarDados(@Query("id_cliente") Integer id_cliente);

    @GET("lavoutanovo/MntCliente/lista_servico")
    Call<List<ServicoTO>> lista_servico(
        @Query("id_cliente") Integer id_cliente,
        @Query("codg_localizacao") String codg_localizacao
    );

}
