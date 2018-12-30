package br.com.inteligenti.lavoutanovov2.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoLoader implements Target {
    private final String name;
    private ImageView imageView;
    private Context context;
    private String directory_type;
    private Bitmap bitmap = null;
    private Wait wait;

    public void setWait(Wait wait) {
        this.wait = wait;
    }

    public PhotoLoader(String name, ImageView imageView) {
        this.name = name;
        this.imageView = imageView;
    }

    public PhotoLoader(String name, Context context, String directory_type) {
        this.name = name;
        this.context = context;
        this.directory_type = directory_type;
    }

    public PhotoLoader(String name, Context context, String directory_type, ImageView imageView) {
        this.name = name;
        this.context = context;
        this.directory_type = directory_type;
        this.imageView = imageView;
    }

    @Override
    public void onPrepareLoad(Drawable arg0) {
    }

    public String getUrl() {
        if (imageView != null) {
            return imageView.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/" + name;
        } else {
            return context.getExternalFilesDir(directory_type).getPath() + "/" + name;
        }
    }

    public boolean fileExist() {
        File file = new File(getUrl());
        return file.exists();
    }

    public File getFile() {
        return new File(getUrl());
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
        //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/" + name);
        try {
            File file = saveFileFormBitmap(bitmap);
            if (imageView!=null) {
                imageView.setImageBitmap(bitmap);
            }
            if (wait != null) {
                List lista = new ArrayList();
                lista.add(file);
                wait.execute(lista);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBitmapFailed(Drawable arg0) {
    }


    public File saveFileFormBitmap(Bitmap bitmap) {

        File file = getFile();
        try {
            if (file.exists()) {
                file.getCanonicalFile().delete();
            }

            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
            ostream.close();
        } catch (Exception e) {
            file = null;
        }
        return file;
    }

    public File saveFileFormUri(Uri uri) {
        File file = new File(uri.getPath());

        File newFile = getFile();

        try {
            //if(file.exists()){
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(newFile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            //}

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFile;
    }
}