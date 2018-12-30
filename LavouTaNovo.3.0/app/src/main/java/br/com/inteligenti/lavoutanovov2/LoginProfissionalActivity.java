package br.com.inteligenti.lavoutanovov2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import br.com.inteligenti.lavoutanovov2.service.ProfissionalCO;
import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.ProfissionalTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginProfissionalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profissional);

        // Botão Voltar
        Button btnLoginProfissionalVoltar = (Button) findViewById(R.id.btnLoginProfissionalVoltar);
        btnLoginProfissionalVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Botão Entrar
        Button btnLoginProfissionalEntrar = (Button) findViewById(R.id.btnLoginProfissionalEntrar);
        btnLoginProfissionalEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entrar();
            }
        });

        // Link Esqueci senha
        TextView linkLoginProfissionalEsqueciSenha = (TextView) findViewById(R.id.linkLoginProfissionalEsqueciSenha);
        linkLoginProfissionalEsqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginProfissionalActivity.this, EsqueciSenhaClienteActivity.class);
                intent.putExtra("tipo", "P"); // P = Profissional

                startActivity(intent);
                finish();
            }
        });
    }

    private void entrar() {
        boolean valido = true;
        String sEmail = ((EditText) findViewById(R.id.etEmail)).getText().toString();
        String sSenha = ((EditText) findViewById(R.id.etSenha)).getText().toString();

        if (sEmail.isEmpty()) {
            ((EditText) findViewById(R.id.etEmail)).setError("O campo email é obrigatório.");
            valido = false;
        }
        if (sSenha.isEmpty()) {
            ((EditText) findViewById(R.id.etSenha)).setError("O campo senha é obrigatório.");
            valido = false;
        }

        if (valido) {
            Retrofit retrofit = ConectorRetrofit.getInstance(LoginProfissionalActivity.this);
            ProfissionalCO profissionalCO = retrofit.create(ProfissionalCO.class);

            Map<String, String> params = new HashMap<String, String>();
            params.put("desc_email", sEmail);
            params.put("desc_senha", sSenha);
            Call<ProfissionalTO> call = profissionalCO.logarProfissional(params);
            call.enqueue(new Callback<ProfissionalTO>() {
                @Override
                public void onResponse(Call<ProfissionalTO> call, Response<ProfissionalTO> response) {
                    String msg = "";
                    try {
                        if (response.body() != null) {
                            ProfissionalTO myProfissional = response.body();
                            //profissionalTO.setId(myProfissional.getId());
                            msg = "Profissional logado com sucesso.";
                            registrarLogin(myProfissional);
                            InicialActivity ini = new InicialActivity();
                            ini.registrarLoginProfissional(myProfissional, LoginProfissionalActivity.this);


                            //String sLogo = InicialActivity.SHARED.getString(InicialActivity.USER_LOGO, "");
                            //if (sLogo.isEmpty()) {
                            //    ProfissionalTO profissionalTO = Register.getProfissionalTO();
                                //ini.registrarLogo(profissionalTO.getDesc_id_facebook(), LoginProfissionalActivity.this);
                            //}
                        } else if (response.errorBody() != null) {
                            msg = response.errorBody().string();
                        }
                    } catch (Exception $e) {
                        msg = "Problema ao efetuar login, tente novamente mais tarde.";
                    }

                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ProfissionalTO> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "ERRO:" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void registrarLogin(ProfissionalTO profissionalTO)
    {
        Register.registrarLoginProfissional(profissionalTO);
        Register.registrarProfissionalTO(profissionalTO);

        /*SharedPreferences.Editor shared = getSharedPreferences(InicialActivity.MEMORIA_INTERNA, 0).edit();
        shared.putString(InicialActivity.USER_NAME, profissionalTO.getDesc_nome());
        shared.putInt(InicialActivity.USER_ID, profissionalTO.getId());
        shared.putString(InicialActivity.USER_ID_FACEBOOK, profissionalTO.getDesc_id_facebook());
        shared.commit();*/
        startActivity(new Intent(LoginProfissionalActivity.this, MainProfissionalActivity.class));
        finish();
    }

}
