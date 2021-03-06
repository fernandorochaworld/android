package br.com.inteligenti.lavoutanovov2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import br.com.inteligenti.lavoutanovov2.service.ClienteCO;
import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EsqueciSenhaClienteActivity extends AppCompatActivity {

    String codg_tipo_usuario = "C";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha_cliente);

        // Botão Voltar
        Button btnLoginClienteVoltar = (Button) findViewById(R.id.btnLoginClienteVoltar);
        btnLoginClienteVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Botão Entrar
        Button btnLoginClienteEntrar = (Button) findViewById(R.id.btnLoginClienteEntrar);
        btnLoginClienteEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrar();
            }
        });


        Intent intent = getIntent();
        String tipo = intent.getStringExtra("tipo");

        if (tipo != null && !tipo.isEmpty()) {
            codg_tipo_usuario = tipo;
        }

    }

    private void entrar() {
        boolean valido = true;
        String sEmail = ((EditText) findViewById(R.id.etEmail)).getText().toString();

        if (sEmail.isEmpty()) {
            ((EditText) findViewById(R.id.etEmail)).setError("O campo email é obrigatório.");
            valido = false;
        }

        if (valido) {
            Retrofit retrofit = ConectorRetrofit.getInstance(EsqueciSenhaClienteActivity.this);
            ClienteCO clienteCO = retrofit.create(ClienteCO.class);

            Map<String, String> params = new HashMap<String, String>();
            params.put("desc_email", sEmail);
            params.put("codg_tipo_usuario", codg_tipo_usuario);
            Call<ClienteTO> call = clienteCO.esqueciSenha(params);
            call.enqueue(new Callback<ClienteTO>() {
                @Override
                public void onResponse(Call<ClienteTO> call, Response<ClienteTO> response) {
                    String msg = "";
                    try {
                        if (response.body() != null) {
                            ClienteTO myCliente = response.body();
                            //clienteTO.setId(myCliente.getId());
                            msg = "Cliente logado com sucesso.";
                            registrarLogin(myCliente);
                        } else if (response.errorBody() != null) {
                            msg = response.errorBody().string();
                        }
                    } catch (Exception $e) {
                        msg = "Problema ao efetuar login, tente novamente mais tarde.";
                    }
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ClienteTO> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "ERRO:" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void registrarLogin(ClienteTO clienteTO)
    {
        Register.registrarLogin(clienteTO);

        /*SharedPreferences.Editor shared = getSharedPreferences(InicialActivity.MEMORIA_INTERNA, 0).edit();
        shared.putString(InicialActivity.USER_NAME, clienteTO.getDesc_nome());
        shared.putInt(InicialActivity.USER_ID, clienteTO.getId());
        shared.putString(InicialActivity.USER_ID_FACEBOOK, clienteTO.getDesc_id_facebook());
        shared.commit();*/
        startActivity(new Intent(EsqueciSenhaClienteActivity.this, MainActivity.class));
        finish();
    }

}
