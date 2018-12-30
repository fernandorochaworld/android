package br.com.inteligenti.lavoutanovov2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vicmikhailau.maskededittext.MaskedEditText;

import br.com.inteligenti.lavoutanovov2.service.CarroCO;
import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.Documento;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.CarroTO;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CadCarroActivity extends AppCompatActivity {

    ImageView imageView;
    Integer REQUEST_CAMERA = 1, REQUEST_GALLERY = 0;
    String[] aStringCor;

    MaskedEditText etPlaca;
    EditText etMarca;
    EditText etModelo;
    Spinner spTamanho;
    Spinner spCor;
    EditText etOutraCor;

    CarroTO carroTO;
    CarroCO carroCO;
    Documento doc;

    String index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadcarro);
        aStringCor = getResources().getStringArray(R.array.lista_cor);
        Intent intent = getIntent();
        index = intent.getStringExtra("index_item");

        imageView = (ImageView) findViewById(R.id.ivCarro);

        etPlaca = (MaskedEditText) findViewById(R.id.etPlaca);
        etMarca  = (EditText) findViewById(R.id.etMarca);
        etModelo = (EditText) findViewById(R.id.etModelo);
        spTamanho = (Spinner) findViewById(R.id.spTamanho);
        spCor     = (Spinner) findViewById(R.id.spCor);
        etOutraCor = (EditText) findViewById(R.id.etOutraCor);

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

        spCor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == aStringCor.length -1) {
                    etOutraCor.setVisibility(View.VISIBLE);
                } else {
                    etOutraCor.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ClienteTO clienteTO = Register.getClienteTO();

        carroCO = InicialActivity.RETROFIT.create(CarroCO.class);

        if (index != null) {
            carroTO = Register.getListCarro().get(Integer.valueOf(index));
            fillForm();
        } else {
            carroTO = new CarroTO();
            carroTO.setId_cliente(clienteTO.getId());
        }
    }

    private void fillForm() {
        String[] aStringTam = getResources().getStringArray(R.array.lista_tamanho_carro);

        etPlaca.setText(carroTO.getCodg_placa());
        etMarca.setText(carroTO.getDesc_marca());
        etModelo.setText(carroTO.getDesc_modelo());
        etOutraCor.setText(carroTO.getDesc_outra_cor());
        for(int i =0; i<aStringCor.length; i++) {
            if (aStringCor[i].equals(carroTO.getCodg_cor())) {
                spCor.setSelection(i);
                break;
            }
        }
        if (carroTO.getCodg_tamanho() != "")
        for(int i =0; i<aStringTam.length; i++) {
            if (aStringTam[i].charAt(0) == carroTO.getCodg_tamanho().charAt(0)) {
                spTamanho.setSelection(i);
                break;
            }
        }

        String idDoc = carroTO.getFile_foto();
        if (idDoc!=null) {
            idDoc = idDoc.replace("[", "").replace(",", "").replace("]", "").replace("\"", "");
        }
        if (idDoc != null && idDoc !="") {
            String url = Register.getDoc(Integer.parseInt(idDoc));
            if (url!=null) {
                //Picasso.with(CadCarroActivity.this).load(url).into(imageView);
                Bitmap bitmap = BitmapFactory.decodeFile(url);

                doc = new Documento(url, bitmap);
                doc.setId(Integer.parseInt(idDoc));
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private void loadProfileImage() {
        final CharSequence[] items = {"Camera", "Cancel"}; //, "Gallery"
        AlertDialog.Builder builder = new AlertDialog.Builder(CadCarroActivity.this);
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

                doc = new Documento("Carro_"+index+".jpg", bitmap);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                imageView.setImageURI(imageUri);

                doc = new Documento("Carro_gallery_"+index+".jpg", imageUri);
            }
            if (doc != null) {
                Documento.uploadDoc(doc, null);
            }
        }
    }

    private void salvar()
    {
        boolean valido = true;
        if (doc != null && doc.getId() != null) {
            carroTO.setFile_foto("["+doc.getId()+"]");
        } else {
            //Toast.makeText(getBaseContext(), "A foto é obrigatória.", Toast.LENGTH_LONG).show();
            //valido = false;
        }

        if (etPlaca.getText().toString().trim().isEmpty()) {
            etPlaca.setError("O campo placa é obrigatório.");
            valido = false;
        } else {
            carroTO.setCodg_placa(etPlaca.getText().toString().trim());
        }
        if (etMarca.getText().toString().trim().isEmpty()) {
            etMarca.setError("O campo Marca é obrigatório.");
            valido = false;
        } else {
            carroTO.setDesc_marca(etMarca.getText().toString().trim());
        }
        if (etModelo.getText().toString().trim().isEmpty()) {
            etModelo.setError("O campo Modelo é obrigatório.");
            valido = false;
        } else {
            carroTO.setDesc_modelo(etModelo.getText().toString().trim());
        }
        if (spTamanho.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(this, "O campo Tamanho do Carro é obrigatório.", Toast.LENGTH_LONG).show();
            valido = false;
        } else {
            carroTO.setCodg_tamanho(spTamanho.getSelectedItem().toString().substring(0,1));
        }
        if (spCor.getSelectedItem().toString().isEmpty()) {
            Toast.makeText(this, "O campo Cor é obrigatório.", Toast.LENGTH_LONG).show();
            valido = false;
        } else {
            carroTO.setCodg_cor(spCor.getSelectedItem().toString());
        }
        if (
            (spCor.getSelectedItemPosition() == aStringCor.length -1)
            && etOutraCor.getText().toString().trim().isEmpty()
            ) {
            etOutraCor.setError("O campo Outra Cor é obrigatório.");
            valido = false;
        } else {
            carroTO.setDesc_outra_cor(etOutraCor.getText().toString().trim());
        }
        if (valido) {
            Call<CarroTO> call = carroCO.salvar(carroTO);
            call.enqueue(new Callback<CarroTO>() {
                @Override
                public void onResponse(Call<CarroTO> call, Response<CarroTO> response) {
                    CarroTO myCarro = response.body();
                    if (myCarro != null) {
                        if (carroTO.getId() != null) {
                            Register.editCarro(myCarro, Integer.valueOf(index));
                        } else {
                            //carroTO.setId(myCarro.getId());
                            Register.addCarro(myCarro);
                        }
                        Toast.makeText(CadCarroActivity.this, "Carro inserido com sucesso.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<CarroTO> call, Throwable t) {
                    Toast.makeText(getBaseContext(), "Não foi possível fazer a conexão:" + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
