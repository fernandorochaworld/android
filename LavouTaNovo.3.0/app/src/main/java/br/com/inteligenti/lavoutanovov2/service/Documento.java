package br.com.inteligenti.lavoutanovov2.service;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.inteligenti.lavoutanovov2.InicialActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fernando on 10/01/18.
 */

public class Documento {

    private String name;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap bitmap;
    private Integer id;
    private String path;

    public Documento(String name, Bitmap bitmap) {
        this.name = name;
        this.bitmap = bitmap;
    }

    public Documento(String name, Uri uri) {
        this.name = name;
        this.imageUri = uri;
    }

    public static void uploadDoc(Documento doc, final Wait wait_out) {
        File file = null;
        PhotoLoader fl = null;
        final Documento documento = doc;

        Wait wait = new Wait() {
            @Override
            public void execute(List list) {
                if (wait_out != null && (list == null || list.size() < 1)) {
                    // Não salvou o arquivo
                    wait_out.execute(new ArrayList());
                    return;
                }
                File file = (File) list.get(0);

                if (file != null && file.exists()) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");


                    ClienteCO clienteCO = InicialActivity.RETROFIT.create(ClienteCO.class);
                    Call<okhttp3.ResponseBody> req = clienteCO.uploadLogo(body, name);
                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            // Do Something
                            String txt = null;
                            List lista = new ArrayList();
                            try {
                                txt = response.body().string().toString();
                                JSONObject object = (JSONObject) new JSONTokener(txt).nextValue();
                                documento.setId(object.getInt("id"));
                                if (documento.getId() != null) {
                                    // Salvou o Arquivo
                                    Register.addDoc(documento.getId(), documento.getName());
                                }
                                lista.add(documento);
                                //documento.getBitmap();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (wait_out!=null) {
                                wait_out.execute(lista);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();

                            if (wait_out!=null) {
                                wait_out.execute(null);
                            }
                        }
                    });
                }
            }
        };


        if (doc != null && doc.getBitmap() != null) {
            fl = new PhotoLoader(doc.getName(), InicialActivity.PUBLIC_ACTIVITY, Environment.DIRECTORY_PICTURES);
            file = fl.saveFileFormBitmap(doc.getBitmap());

            List lista = new ArrayList();
            lista.add(file);
            wait.execute(lista);
            doc.setPath(fl.getUrl());
        } else if(doc != null && doc.getName() != null) {
            fl = new PhotoLoader(doc.getName(), InicialActivity.PUBLIC_ACTIVITY, Environment.DIRECTORY_PICTURES);
            fl.setWait(wait);
            Picasso.with(InicialActivity.PUBLIC_ACTIVITY).load(doc.getImageUri()).into(fl);
            doc.setPath(fl.getUrl());
            //file = fl.getFile();
        } else {
            wait.execute(null);
        }
    }

    public void registraLogo() {
        if (path != null) {
            SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
            shared.putString(InicialActivity.USER_LOGO, path);
            shared.commit();
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
