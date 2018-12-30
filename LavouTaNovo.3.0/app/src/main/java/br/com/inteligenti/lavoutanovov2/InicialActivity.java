package br.com.inteligenti.lavoutanovov2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.inteligenti.lavoutanovov2.service.ClienteCO;
import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.Documento;
import br.com.inteligenti.lavoutanovov2.service.PhotoLoader;
import br.com.inteligenti.lavoutanovov2.service.ProfissionalCO;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.service.Wait;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import br.com.inteligenti.lavoutanovov2.to.ProfissionalTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InicialActivity extends AppCompatActivity {

    public static final String MEMORIA_INTERNA  = "LAVOUTANOVO_MEMORIA";
    public static final String USER_ID          = "USER_ID";
    public static final String USER_TYPE        = "USER_TYPE";
    public static final String USER_TYPE_CLIENTE      = "CLIENTE";
    public static final String USER_TYPE_PROFISSIONAL = "PROFISSIONAL";
    public static final String USER_NAME        = "USER_NAME";
    public static final String USER_EMAIL       = "USER_EMAIL";
    public static final String USER_LOGO        = "USER_LOGO";
    public static final String USER_ID_FACEBOOK = "USER_ID_FACEBOOK";
    public static final String USER_CLIENTE     = "USER_CLIENTE";
    public static final String SEQUENCE_DOC     = "SEQUENCE_DOC";
    public static SharedPreferences SHARED      = null;
    public static Retrofit RETROFIT = null;
    public static Activity PUBLIC_ACTIVITY = null;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dfDataProducao = new SimpleDateFormat("yyyy-MM-dd");
    String dataLimit = "2018-03-01";


    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        Locale.setDefault(new Locale("pt", "BR"));  // mudança global
        String sDataAtual = dfDataProducao.format(calendar.getTime());
        /*if (sDataAtual.compareTo(dataLimit) > 0) {
            Toast.makeText(InicialActivity.this, "Excessão não tratada pelo Sistema.", Toast.LENGTH_LONG);
            finish();
            return;
        }*/
        Register.askPermissions(InicialActivity.this);

        // My init
        PUBLIC_ACTIVITY = InicialActivity.this;
        RETROFIT = ConectorRetrofit.getInstance(InicialActivity.this);

        SharedPreferences shared = getSharedPreferences(InicialActivity.MEMORIA_INTERNA, 0);
        SHARED = shared;
        Integer id = shared.getInt(InicialActivity.USER_ID, 0);
        if (id != null && id > 0) {
            String type = shared.getString(InicialActivity.USER_TYPE, "");
            Class<?> oClass = MainProfissionalActivity.class;
            if (!type.equals(InicialActivity.USER_TYPE_PROFISSIONAL)) {
                oClass = MainActivity.class;
            }
            startActivity(new Intent(InicialActivity.this, oClass));
            finish();
            return;
        }

        // Cadastrar-se
        Button btnCadastrarse = (Button) findViewById(R.id.btnCadastrarse);
        btnCadastrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(InicialActivity.this, CadastrarseActivity.class));
            }
        });

        // Cliente Login
        Button btnLoginCliente = (Button) findViewById(R.id.btnLoginCliente);
        btnLoginCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(InicialActivity.this, LoginClienteActivity.class));
            }
        });
        // Cliente Profissional
        Button btnLoginProfissional = (Button) findViewById(R.id.btnLoginProfissional);
        btnLoginProfissional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(InicialActivity.this, LoginProfissionalActivity.class));
            }
        });

        // Btn Fake
        Button btnFacebook = (Button) findViewById(R.id.btnFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        // Iniciar Botão Facebook

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions("email");
        //loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        /*LoginManager.getInstance().logInWithReadPermissions(
            this, Arrays.asList("email", "public_profile", "user_birthday", "user_friends", "user_location")
        );*/
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String accessToken = loginResult.getAccessToken().getToken();

                // save accessToken to SharedPreference
                //prefUtil.saveAccessToken(accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {
                                // Getting FB User Data
                                Bundle facebookData = getFacebookData(jsonObject);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,gender,last_name,link,locale,name,timezone,updated_time,verified,age_range,friends,birthday");
                //parameters.putString("fields", "id,first_name,last_name,email,gender,user_location,user_friends,user_birthday, birthday");
                //id,email,first_name,gender,last_name,link,locale,name,timezone,updated_time,verified,age_range,friends,birthday
                request.setParameters(parameters);
                request.executeAsync();

                //Toast.makeText(LoginActivity.this, "Sucess.", Toast.LENGTH_LONG).show();
            }

            private Bundle getFacebookData(JSONObject object) {
                Bundle bundle = new Bundle();

                try {
                    String id = object.getString("id");

                    ClienteTO clienteTO = new ClienteTO();
                    try {
                        clienteTO.setDesc_email(object.getString("email"));
                    } catch(Exception e) {}

                    clienteTO.setDesc_nome(
                        object.getString("name")
                    );
                    clienteTO.setDesc_id_facebook(id);

                    String email = object.getString("first_name");

                    bundle.putString("idFacebook", id);
                    if (object.has("first_name"))
                        bundle.putString("first_name", object.getString("first_name"));
                    if (object.has("last_name"))
                        bundle.putString("last_name", object.getString("last_name"));
                    if (object.has("email"))
                        bundle.putString("email", object.getString("email"));
                    if (object.has("gender"))
                        bundle.putString("gender", object.getString("gender"));

                    registrarLoginFacebook(clienteTO);

                    //Toast.makeText(InicialActivity.this, "Ok="+email, Toast.LENGTH_LONG).show();
                    /*prefUtil.saveFacebookUserInfo(object.getString("first_name"),
                            object.getString("last_name"),object.getString("email"),
                            object.getString("gender"), profile_pic.toString());
                            */
                } catch (Exception e) {
                    //Log.d(TAG, "BUNDLE Exception : "+e.toString());
                    Toast.makeText(InicialActivity.this, "ERRO="+e.getMessage(), Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                }

                return bundle;
            }

            @Override
            public void onCancel() {
                Toast.makeText(InicialActivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(InicialActivity.this, "Error.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registrarLoginFacebook(ClienteTO clienteTO)
    {
        Retrofit retrofit = ConectorRetrofit.getInstance(InicialActivity.this);
        ClienteCO clienteCO = retrofit.create(ClienteCO.class);

        Call<ClienteTO> call = clienteCO.registrarLoginFacebook(clienteTO);
        call.enqueue(new Callback<ClienteTO>() {
            @Override
            public void onResponse(Call<ClienteTO> call, Response<ClienteTO> response) {
                String msg = "";
                try {
                    if (response.body() != null) {
                        ClienteTO myCliente = response.body();
                        //clienteTO.setId(myCliente.getId());
                        msg = "Cliente logado com sucesso.";
                        registrarLogin(myCliente, InicialActivity.this);
                    } else if (response.errorBody() != null) {
                        msg = response.errorBody().string();
                        LoginManager.getInstance().logOut();
                    }
                } catch (Exception $e) {
                    msg = "Problema ao efetuar login, tente novamente mais tarde.";
                    LoginManager.getInstance().logOut();
                }

                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ClienteTO> call, Throwable t) {
                Toast.makeText(getBaseContext(), "ERRO:" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void registrarLogo(final ClienteTO clienteTO, final Activity activity)
    {
        if (clienteTO.getFileFotoPerfil() == null || clienteTO.getFileFotoPerfil().isEmpty()) {
            String urlProfileFace = Register.getFacebookURL(clienteTO.getDesc_id_facebook());
            PhotoLoader fl = new PhotoLoader("logo.png", activity, Environment.DIRECTORY_PICTURES);
            Picasso.with(activity).load(urlProfileFace).into(fl);
            String path = fl.getUrl();

            SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
            shared.putString(InicialActivity.USER_LOGO, path);
            shared.commit();

            File file = fl.getFile();
            Uri imageUri = FileProvider.getUriForFile(this,
                    "br.com.inteligenti.lavoutanovov2.fileprovider",
                    file);

            String index = Register.getCurrentDateIndex();
            Documento doc = new Documento("Perfil" + index + ".jpg", imageUri);

            Wait wait = new Wait() {
                @Override
                public void execute(List list) {
                    try {
                        if (list != null && list.size()>0) {
                            Documento doc = (Documento) list.get(0);

                            if (doc.getId() != null) {
                                clienteTO.setFileFotoPerfil("[" + doc.getId() + "]");

                                ClienteCO clienteCO = RETROFIT.create(ClienteCO.class);
                                Call<ClienteTO> req = clienteCO.salvar(clienteTO);
                                req.enqueue(new Callback<ClienteTO>() {

                                    @Override
                                    public void onResponse(Call<ClienteTO> call, Response<ClienteTO> response) {
                                        int i = 1;
                                    }

                                    @Override
                                    public void onFailure(Call<ClienteTO> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            };

            Documento.uploadDoc(doc, wait);
        }
        /*
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");


        //Retrofit retrofit = ConectorRetrofit.getInstance(this);
        ClienteCO clienteCO = RETROFIT.create(ClienteCO.class);

        Call<okhttp3.ResponseBody> req = clienteCO.uploadLogo(body, name);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Do Something
                String txt = null;
                try {
                    txt = response.body().string().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(activity, txt, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        */
    }

    public void registrarLogin(ClienteTO clienteTO, Activity activity)
    {
        int atual_user = SHARED.getInt(InicialActivity.USER_ID, 0);
        SharedPreferences.Editor shared = SHARED.edit();
        shared.putString(InicialActivity.USER_TYPE, InicialActivity.USER_TYPE_CLIENTE);
        shared.putString(InicialActivity.USER_NAME, clienteTO.getDesc_nome());
        shared.putString(InicialActivity.USER_EMAIL, clienteTO.getDesc_email());
        shared.putString(InicialActivity.USER_ID_FACEBOOK, clienteTO.getDesc_id_facebook());
        shared.putInt(InicialActivity.USER_ID, clienteTO.getId());
        shared.commit();
        if (atual_user != clienteTO.getId() && clienteTO.getId() != null && clienteTO.getId() > 0) {
            iniciarDados(clienteTO.getId());
        }

        String sLogo = InicialActivity.SHARED.getString(InicialActivity.USER_LOGO, "");
        if (sLogo.isEmpty() && clienteTO.getDesc_id_facebook() != null && !clienteTO.getDesc_id_facebook().isEmpty()) {
            registrarLogo(clienteTO, InicialActivity.this);
        }

        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }

    public void registrarLoginProfissional(ProfissionalTO profissionalTO, Activity activity)
    {
        int atual_user = SHARED.getInt(InicialActivity.USER_ID, 0);
        SharedPreferences.Editor shared = SHARED.edit();
        shared.putString(InicialActivity.USER_TYPE, InicialActivity.USER_TYPE_PROFISSIONAL);
        shared.putString(InicialActivity.USER_NAME, profissionalTO.getNomeProfissional());
        shared.putString(InicialActivity.USER_EMAIL, profissionalTO.getDescEmail());
        shared.putInt(InicialActivity.USER_ID, profissionalTO.getId());
        shared.commit();
        if (atual_user != profissionalTO.getId() && profissionalTO.getId() != null && profissionalTO.getId() > 0) {
            iniciarDadosProfissional(profissionalTO.getId());
        }
        registrarLogoProf(activity);
        activity.startActivity(new Intent(activity, MainProfissionalActivity.class));
        activity.finish();
    }

    private void iniciarDadosProfissional(Integer id) {
        ProfissionalCO profissionalCO = RETROFIT.create(ProfissionalCO.class);
        Call<ResponseBody> call = profissionalCO.carregarDados(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonStr = response.body().string().toString();

                    if (jsonStr != null && jsonStr.trim() != "") {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        String sPerfil  = jsonObj.getString("perfil");
                        //String sCarro   = jsonObj.getString("lista_carro");
                        String sServicoProf = jsonObj.getString("lista_servico_prof");
                        //String sDoc     = jsonObj.getString("lista_doc");
                        //sDoc = sDoc.replace("]", "").replace("[", "");

                        SharedPreferences.Editor editor = SHARED.edit();
                        editor.putString(Register.USER_PROFISSIONAL, sPerfil);
                        //editor.putString(Register.LIST_CARRO, sCarro);
                        editor.putString(Register.LIST_SERVICO_TODO, sServicoProf);
                        //editor.putString(Register.LIST_DOC, sDoc);
                        editor.commit();
                    }
                } catch (Exception e) {
                    Log.i("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Não foi possível fazer a conexão:" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void registrarLogoProf(final Activity activity) {
        SharedPreferences shared = InicialActivity.SHARED;
        String logo = shared.getString(InicialActivity.USER_LOGO, "");
        ProfissionalTO prof = Register.getProfissionalTO();

        if (logo.isEmpty() && prof.getDescImgProfile()!=null && !prof.getDescImgProfile().isEmpty() ) {

            PhotoLoader fl = new PhotoLoader(prof.getDescImgProfile(), activity, Environment.DIRECTORY_PICTURES);
            Picasso.with(activity).load(Register.getUrlImg(prof.getDescImgProfile())).into(fl);
            String path = fl.getUrl();

            SharedPreferences.Editor editor = InicialActivity.SHARED.edit();
            editor.putString(InicialActivity.USER_LOGO, path);
            editor.commit();
        }
    }

    private void iniciarDados(Integer id) {
        ClienteCO clienteCO = RETROFIT.create(ClienteCO.class);
        Call<ResponseBody> call = clienteCO.carregarDados(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonStr = response.body().string().toString();

                    if (jsonStr != null && jsonStr.trim() != "") {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        String sPerfil  = jsonObj.getString("perfil");
                        String sCarro   = jsonObj.getString("lista_carro");
                        String sServico = jsonObj.getString("lista_servico");
                        String sDoc     = jsonObj.getString("lista_doc");
                        //sDoc = sDoc.replace("]", "").replace("[", "");

                        SharedPreferences.Editor editor = SHARED.edit();
                        editor.putString(InicialActivity.USER_CLIENTE, sPerfil);
                        editor.putString(Register.LIST_CARRO, sCarro);
                        editor.putString(Register.LIST_SERVICO, sServico);
                        editor.putString(Register.LIST_DOC, sDoc);
                        editor.commit();
                    }
                } catch (Exception e) {
                    Log.i("Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Não foi possível fazer a conexão:" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
