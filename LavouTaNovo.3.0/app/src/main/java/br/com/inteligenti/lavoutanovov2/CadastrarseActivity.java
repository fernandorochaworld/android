package br.com.inteligenti.lavoutanovov2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.inteligenti.lavoutanovov2.service.ClienteCO;
import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.Documento;
import br.com.inteligenti.lavoutanovov2.service.PaisClient;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.service.Util;
import br.com.inteligenti.lavoutanovov2.service.Wait;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import br.com.inteligenti.lavoutanovov2.to.PaisTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CadastrarseActivity extends AppCompatActivity {

    ImageView imageView;
    Integer REQUEST_CAMERA_HIGH_QUALITY = 2,
            REQUEST_CAMERA = 1,
            REQUEST_GALLERY = 0,
            MY_REQUEST_CODE = 5;
    ClienteCO clienteCO;
    PaisClient paisCO;
    ClienteTO clienteTO;

    EditText etNome;
    EditText etEmail;
    EditText etTelefone;
    EditText etSenha;
    EditText etSenha2;
    MaskedEditText etCpf;

    Button btnCadastrar;
    Button btnProfileImage;
    Documento doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrarse);

        imageView = (ImageView) findViewById(R.id.ivProfileImage);

        etNome = findViewById(R.id.etNome);
        etEmail = findViewById(R.id.etEmail);
        etTelefone = findViewById(R.id.etTelefone);
        etSenha = findViewById(R.id.etSenha);
        etSenha2 = findViewById(R.id.etSenha2);
        etCpf = findViewById(R.id.etCpf);

        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(CadastrarseActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                //finish();
                salvar();
            }
        });

        btnProfileImage = (Button) findViewById(R.id.btnProfileImage);
        btnProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadProfileImage();
            }
        });

        Retrofit retrofit = ConectorRetrofit.getInstance(CadastrarseActivity.this);
        clienteCO = retrofit.create(ClienteCO.class);

        //Retrofit retrofit = builder.build();
        //paisCO = retrofit.create(PaisClient.class);
    }

    Uri imageUri;
    String filename;

    /*public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }*/

    private void loadProfileImage() {
        final CharSequence[] items = {"CameraHD", "Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastrarseActivity.this);
        builder.setTitle("Imagem de perfil");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("CameraHD")) {
                    if (ActivityCompat.checkSelfPermission(CadastrarseActivity.this, android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(CadastrarseActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {

                        try {
                            filename = Register.getLocalImagePath(getBaseContext()) + "/tmp.jpg";
                            File file = new File(filename);

                            //imageUri = Uri.fromFile(file);

                            imageUri = FileProvider.getUriForFile(CadastrarseActivity.this,
                                    "br.com.inteligenti.lavoutanovov2.fileprovider",
                                    file);


                            // start default camera
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(cameraIntent, REQUEST_CAMERA_HIGH_QUALITY);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else if (items[i].equals("Camera")) {
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


    protected File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg", storageDirectory);
        //mImageFileLocation = image.getAbsolutePath();

        return image;

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK) {
            String index = Register.getCurrentDateIndex();
            if (requestCode == REQUEST_CAMERA_HIGH_QUALITY) {
                if(filename == null)
                    return;
                imageView.setImageURI(imageUri);

                doc = new Documento("Perfil"+index+".jpg", imageUri);
            } else if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap)bundle.get("data");
                imageView.setImageBitmap(bitmap);

                doc = new Documento("Perfil"+index+".jpg", bitmap);
            } else if (requestCode == REQUEST_GALLERY) {
                Uri imageUri = data.getData();
                imageView.setImageURI(imageUri);

                doc = new Documento("Perfil"+index+".jpg", imageUri);
            }
        }
    }

    private void bloquearTela(boolean bBloquear)
    {
        etNome.setEnabled(bBloquear);
        etEmail.setEnabled(bBloquear);
        etTelefone.setEnabled(bBloquear);
        etSenha.setEnabled(bBloquear);
        etSenha2.setEnabled(bBloquear);
        etCpf.setEnabled(bBloquear);
        btnProfileImage.setEnabled(bBloquear);
        btnCadastrar.setEnabled(bBloquear);
        btnCadastrar.setText(
            bBloquear?"CADASTRAR":"CADASTRANDO.."
        );
    }

    private void salvar()
    {
        boolean valido = true;
        final ClienteTO clienteTO = new ClienteTO();
        String sNome = etNome.getText().toString().trim();
        Boolean bIsFullName = Util.isFullName(sNome);
        if (sNome.isEmpty() || !bIsFullName) {
            if (sNome.isEmpty()) {
                etNome.setError("O campo nome é obrigatório.");
            } else {
                etNome.setError("Informe o nome completo.");
            }
            valido = false;
        } else {
            clienteTO.setDesc_nome(etNome.getText().toString());
        }
        String sEmail = etEmail.getText().toString().trim();
        Boolean bIsEmail = Util.isEmail(sEmail);

        if (sEmail.isEmpty() || !bIsEmail) {
            if (sEmail.isEmpty()) {
                etEmail.setError("O campo e-mail é obrigatório.");
            } else {
                etEmail.setError("E-mail inválido.");
            }
            valido = false;
        } else {
            clienteTO.setDesc_email(etEmail.getText().toString().trim());
        }
        String sCpf = etCpf.getUnMaskedText().toString().trim();
        if (sCpf.isEmpty() || !Util.isCPF(sCpf)) {
            if (sCpf.isEmpty()) {
                etCpf.setError("O campo CPF é obrigatório.");
            } else {
                etCpf.setError("CPF inválido.");
            }
            valido = false;
        } else {
            clienteTO.setDesc_cpf(sCpf);
        }
        clienteTO.setDesc_telefone(etTelefone.getText().toString());
        if (etSenha.getText().toString().isEmpty()) {
            etSenha.setError("O campo senha é obrigatório.");
            valido = false;
        } else if (!etSenha.getText().toString().equals(etSenha2.getText().toString())) {
            etSenha.setError("Senha não confere.");
            etSenha2.setError("Senha não confere.");
            valido = false;
        } else {
            clienteTO.setDesc_senha(etSenha.getText().toString());
        }
        if (valido) {

            bloquearTela(false);
            Wait wait = new Wait() {
                @Override
                public void execute(List list) {
                    if (list.size()>0) {
                        Documento doc = (Documento)list.get(0);
                        doc.getPath();
                        if (doc != null && doc.getId() != null) {
                            clienteTO.setFileFotoPerfil("[" + doc.getId() + "]");
                            if (doc.getPath() != null && !doc.getPath().isEmpty()) {
                                Register.share(InicialActivity.USER_LOGO, doc.getPath());
                            }
                        }
                    }
                    Call<ClienteTO> call = clienteCO.salvar(clienteTO);
                    call.enqueue(new Callback<ClienteTO>() {
                        @Override
                        public void onResponse(Call<ClienteTO> call, Response<ClienteTO> response) {
                            ClienteTO myCliente = response.body();
                            if (myCliente != null) {
                                //clienteTO.setId(myCliente.getId());
                                Toast.makeText(getBaseContext(), "Cliente cadastrado com sucesso.", Toast.LENGTH_LONG).show();
                                InicialActivity ini = new InicialActivity();
                                ini.registrarLogin(myCliente, CadastrarseActivity.this);
                            } else {
                                bloquearTela(true);
                                String msg = response.errorBody().toString();
                                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ClienteTO> call, Throwable t) {
                            bloquearTela(true);
                            Toast.makeText(getBaseContext(), "Não foi possível fazer a conexão:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };

            Documento.uploadDoc(doc, wait);
        }
    }
}
