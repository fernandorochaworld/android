package br.com.inteligenti.lavoutanovov2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.vicmikhailau.maskededittext.MaskedEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.inteligenti.lavoutanovov2.service.ClienteCO;
import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.Documento;
import br.com.inteligenti.lavoutanovov2.service.PaisClient;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.service.Util;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditarPerfilActivity extends AppCompatActivity {

    ImageView imageView;
    Integer REQUEST_CAMERA = 1, REQUEST_GALLERY = 0;
    ClienteCO clienteCO;
    PaisClient paisCO;
    ClienteTO clienteTO;
    String[] aEstado;
    String[] aEstadoNome;

    EditText etNome;
    EditText etEmail;
    MaskedEditText etTelefone;
    EditText etSenha;
    EditText etSenha2;
    MaskedEditText etCpf;
    Spinner  spEstado;
    EditText etCidade;
    EditText etBairro;
    MaskedEditText etCep;
    EditText etRua;
    EditText etNumero;
    EditText etEdificio;
    EditText etAp;
    EditText etComplemento;

    Documento doc;

    String index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        clienteTO = Register.getClienteTO();
        iniciarView();
        iniciarDados();

        Button btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvar();
            }
        });

        Button btnProfileImage = (Button) findViewById(R.id.btnProfileImage);
        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProfileImage();
            }
        });

        Retrofit retrofit = ConectorRetrofit.getInstance(EditarPerfilActivity.this);
        clienteCO = retrofit.create(ClienteCO.class);
    }

    private void iniciarView() {
        imageView = (ImageView) findViewById(R.id.ivProfileImage);

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etSenha = findViewById(R.id.etSenha);
        etSenha2 = findViewById(R.id.etSenha2);
        etCpf = findViewById(R.id.etCpf);

        spEstado = findViewById(R.id.spEstado);
        etCidade = findViewById(R.id.etCidade);
        etBairro = findViewById(R.id.etBairro);
        etRua = findViewById(R.id.etRua);
        etCep = findViewById(R.id.etCep);
        etNumero = findViewById(R.id.etNumero);
        etEdificio = findViewById(R.id.etEdificio);
        etAp = findViewById(R.id.etAp);
        etComplemento = findViewById(R.id.etComplemento);

        aEstado = getResources().getStringArray(R.array.lista_estado);
    }

    private void setValEstado(String sEstado) {
        for(int i = 0; i<aEstado.length; i++) {
            if (aEstado[i].equals(sEstado)) {
                spEstado.setSelection(i);
                break;
            }
        }
    }

    private void setValEstadoByName(String sEstado) {
        if (aEstadoNome == null) {
            aEstadoNome = getResources().getStringArray(R.array.lista_estado_nome);
        }
        for(int i = 0; i<aEstadoNome.length; i++) {
            if (
                Util.unaccent(aEstadoNome[i].toLowerCase()).equals(Util.unaccent(sEstado.toLowerCase()))
                || aEstado[i].equals(sEstado)
            ) {
                spEstado.setSelection(i);
                break;
            }
        }
    }

    private void iniciarDados() {
        Register.iniciarLogo(imageView);

        if (clienteTO != null && clienteTO.getId() != null) {
            etNome.setText(clienteTO.getDesc_nome());
            etEmail.setText(clienteTO.getDesc_email());
            etTelefone.setText(clienteTO.getDesc_telefone());
            etSenha.setText("");
            etSenha2.setText("");

            etCpf.setText(clienteTO.getDesc_cpf());
            setValEstado(clienteTO.getCodgEstado());
            etCidade.setText(clienteTO.getDescCidade());
            etBairro.setText(clienteTO.getDescBairro());
            etRua.setText(clienteTO.getDescRua());
            etCep.setText(clienteTO.getDescCep());
            etNumero.setText(clienteTO.getDescNumero());
            etEdificio.setText(clienteTO.getDescEdificio());
            etAp.setText(clienteTO.getDescAp());
            etComplemento.setText(clienteTO.getDescComplemento());

            if (!etEmail.getText().toString().trim().isEmpty()) {
                etEmail.setEnabled(false);
                //etEmail.setDefaultFocusHighlightEnabled(false);
            }
            if (!etCpf.getText().toString().trim().isEmpty()) {
                etCpf.setEnabled(false);
            }
        }
        Marker mk = MainActivity.mMarkerSelected;
        if (
            etCidade.getText().toString().isEmpty() &&
            mk != null
        ) {
            if (mk.getTag() instanceof Address) {
                getEnderecoFromAddress((Address) mk.getTag());
            } else if (mk.getTag() instanceof String) {
                getEnderecoFromJson((String) mk.getTag());
            }
        }
    }

    private void getEnderecoFromAddress(Address address) {
        setValEstadoByName(address.getAdminArea());
        etCidade.setText(address.getLocality());
    }

    private void getEnderecoFromJson(String jsonStr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray results = jsonObj.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject local = results.getJSONObject(0);
                JSONArray jaAddress = local.getJSONArray("address_components");
                for(int i=0; i<jaAddress.length(); i++) {
                    JSONObject oAddress = jaAddress.getJSONObject(i);
                    String sType = oAddress.getString("types");
                    if (sType.indexOf("\"locality\"") >= 0) {
                        etCidade.setText(oAddress.getString("long_name"));

                    } else if(sType.indexOf("\"administrative_area_level_1\"") >= 0) {
                        setValEstado(oAddress.getString("short_name").toUpperCase());

                    }
                }
            }
        } catch (Exception e) {

        }
    }


    private void loadProfileImage() {
        final CharSequence[] items = {"Camera", "Cancel"}; //, "Gallery"
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfilActivity.this);
        builder.setTitle("Imagem de perfil");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            if (items[i].equals("Camera")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);

            } else if (items[i].equals("Gallery")) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Selecione a Imagem"), REQUEST_GALLERY);

            } else if (items[i].equals("Cancel")) {
                dialogInterface.dismiss();
            }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK) {
            String index = Register.getCurrentDateIndex();
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap)bundle.get("data");
                imageView.setImageBitmap(bitmap);

                doc = new Documento("Perfil"+index+".jpg", bitmap);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                imageView.setImageURI(imageUri);

                doc = new Documento("Perfil"+index+".jpg", imageUri);
            }
            Documento.uploadDoc(doc, null);
        }
    }

    private void preencherTO()
    {
        if (doc != null && doc.getId() != null) {
            clienteTO.setFileFotoPerfil("["+doc.getId()+"]");
        }
        clienteTO.setDesc_email(etEmail.getText().toString().trim());
        clienteTO.setDesc_telefone(etTelefone.getText().toString().trim());
        clienteTO.setDesc_cpf(etCpf.getText().toString().trim());
        if (
            !etSenha.getText().toString().isEmpty() &&
            etSenha.getText().toString().equals(etSenha2.getText().toString())
        ) {
            clienteTO.setDesc_senha(etSenha.getText().toString());
        }
        clienteTO.setCodgEstado(aEstado[spEstado.getSelectedItemPosition()]);
        clienteTO.setDescCidade(etCidade.getText().toString().trim());

        clienteTO.setDesc_cpf(etCpf.getUnMaskedText().toString().trim());
        clienteTO.setDescBairro(etBairro.getText().toString().trim());
        clienteTO.setDescRua(etRua.getText().toString().trim());
        clienteTO.setDescCep(etCep.getText().toString().trim());
        clienteTO.setDescNumero(etNumero.getText().toString().trim());
        clienteTO.setDescEdificio(etEdificio.getText().toString().trim());
        clienteTO.setDescAp(etAp.getText().toString().trim());
        clienteTO.setDescComplemento(etComplemento.getText().toString().trim());
    }

    private boolean validar()
    {
        boolean valido = true;
        String sEmail = clienteTO.getDesc_email();
        Boolean bIsEmail = Util.isEmail(sEmail);
        if (sEmail.isEmpty() || !bIsEmail) {
            if (sEmail.isEmpty()) {
                etEmail.setError("O campo e-mail é obrigatório.");
            } else {
                etEmail.setError("E-mail inválido.");
            }
            valido = false;
        }
        if (clienteTO.getDesc_telefone().isEmpty()) {
            etTelefone.setError("O campo telefone é obrigatório.");
            valido = false;
        }
        if (clienteTO.getDesc_cpf().isEmpty()) {
            etCpf.setError("O campo CPF é obrigatório.");
            valido = false;
        }
        if (!Util.isCPF(etCpf.getUnMaskedText().toString().trim())) {
            etCpf.setError("CPF inválido.");
            valido = false;
        }
        /*if (clienteTO.getDesc_senha().isEmpty()) {
            etSenha.setError("O campo senha é obrigatório.");
            valido = false;
        }*/
        if (clienteTO.getDescCidade().isEmpty()) {
            etCidade.setError("O campo cidade é obrigatório.");
            valido = false;
        }
        if (clienteTO.getDescBairro().isEmpty()) {
            etBairro.setError("O campo bairro é obrigatório.");
            valido = false;
        }
        if (clienteTO.getDescRua().isEmpty()) {
            etRua.setError("O campo rua/avenida é obrigatório.");
            valido = false;
        }
        if (clienteTO.getDescCep().isEmpty()) {
            etCep.setError("O campo CEP é obrigatório.");
            valido = false;
        }
        return valido;
    }

    private void salvar()
    {
        preencherTO();
        boolean valido = validar();
        if (valido) {
            Call<ClienteTO> call = clienteCO.salvar(clienteTO);
            call.enqueue(new Callback<ClienteTO>() {
                @Override
                public void onResponse(Call<ClienteTO> call, Response<ClienteTO> response) {
                    ClienteTO myCliente = response.body();
                    if (myCliente!= null) {
                        //clienteTO.setId(myCliente.getId());
                        Toast.makeText(getBaseContext(), "Perfil editado com sucesso.", Toast.LENGTH_LONG).show();
                        Register.registrarClienteTO(myCliente);
                        if (doc != null) {
                            doc.registraLogo();
                        }
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<ClienteTO> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "Não foi possível fazer a conexão:" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
