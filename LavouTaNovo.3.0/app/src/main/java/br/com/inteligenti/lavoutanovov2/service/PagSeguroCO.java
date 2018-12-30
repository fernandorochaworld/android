package br.com.inteligenti.lavoutanovov2.service;

import br.com.inteligenti.lavoutanovov2.to.CreditCardTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by fernando on 31/12/17.
 */

public interface PagSeguroCO {

    @GET("financeiro/PagSeguro/getToken")
    Call<ResponseBody> getToken();

    @POST("financeiro/PagSeguro/pay")
    Call<ResponseBody> pay(@Body CreditCardTO cc);

}
