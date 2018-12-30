package br.com.inteligenti.lavoutanovov2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;

import java.util.Observable;
import java.util.Observer;

import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.PagSeguroCO;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.CreditCard;
import br.com.inteligenti.lavoutanovov2.to.CreditCardTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PayActivity extends AppCompatActivity implements Observer {
    public static Retrofit RETROFIT = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        RETROFIT = ConectorRetrofit.getInstance(PayActivity.this);

        final int GET_NEW_CARD = 2;

        Intent intent = new Intent(PayActivity.this, CardEditActivity.class);
        startActivityForResult(intent,GET_NEW_CARD);


        PagSeguroCO pagseguroCO = RETROFIT.create(PagSeguroCO.class);
        Call<ResponseBody> call = pagseguroCO.getToken();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String sSessionId = response.body().string().toString();

                    CreditCard cc = new CreditCard(PayActivity.this);
                    cc.setCardNumber("4111"+"1111"+"1111"+"1111");
                    cc.setMonth("12");
                    cc.setYear("2030");
                    cc.setCvv("123");
                    cc.setSessionId(sSessionId);
                    getPaymentToken( cc );

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PayActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {

            String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            // Your processing goes here.

        }
    }

    private void getPaymentToken( CreditCard creditCard ){
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled( true );
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface( creditCard, "Android" );
        //webView.loadUrl("file:///android_asset/index.html");
        webView.loadUrl(
            Register.getUrlService() + "financeiro/PagSeguro/checkout_android_view"
        );
    }

    @Override
    public void update(Observable observable, Object o) {
        CreditCard creditCard = (CreditCard) observable;

        if( creditCard.getToken() != null ){
            Log.i("Log", "Token: "+creditCard.getToken());

            CreditCardTO cc = new CreditCardTO();
            cc.setCardNumber(creditCard.getCardNumber());
            cc.setCodgBrand(creditCard.getCodgBrand());
            cc.setCodgSenderHash(creditCard.getCodgSenderHash());
            cc.setCvv(creditCard.getCvv());
            cc.setMonth(creditCard.getMonth());
            cc.setName(creditCard.getName());
            cc.setSessionId(creditCard.getSessionId());
            cc.setToken(creditCard.getToken());
            cc.setYear(creditCard.getYear());

            PagSeguroCO pagseguroCO = RETROFIT.create(PagSeguroCO.class);
            Call<ResponseBody> call = pagseguroCO.pay(cc);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String sResult = response.body().string().toString();
                        sResult += "";
                    } catch (Exception e) { }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PayActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
