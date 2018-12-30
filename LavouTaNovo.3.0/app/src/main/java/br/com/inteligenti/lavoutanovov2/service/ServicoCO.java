package br.com.inteligenti.lavoutanovov2.service;

import java.util.List;

import br.com.inteligenti.lavoutanovov2.to.ServicoTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by fernando on 31/12/17.
 */
public interface ServicoCO {

    //@POST("lavoutanovo/Servico/salvar")
    //Call<ServicoTO> salvar(@Body ServicoTO servicoTO);

    @POST("lavoutanovo/MntServico/registrar")
    Call<ServicoTO> registrar(@Body ServicoTO servicoTO);

    @POST("lavoutanovo/Login/teste_param")
    Call<ResponseBody> teste_param(@Body ServicoTO servicoTO);

    @POST("lavoutanovo/MntServico/recusar_servico")
    Call<ResponseBody> recusar_servico(@Body ServicoTO servicoTO);

    @POST("lavoutanovo/MntServico/avaliar_cliente")
    Call<ResponseBody> avaliar_cliente(@Body ServicoTO servicoTO);

    @POST("lavoutanovo/MntServico/avaliar_profissional")
    Call<ResponseBody> avaliar_profissional(@Body ServicoTO servicoTO);

}
