package br.com.inteligenti.lavoutanovov2.service;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fernando on 30/12/17.
 */

public class ConectorRetrofit {

    public static Retrofit getInstance(Activity activity)
    {
        //String ip = (activity != null || isConnected(activity))?
        //        "192.168.15.4" : "192.168.42.163";

        String url = Register.getUrlService();

        /*Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
                */
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();

        Gson gson = Register.gson;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson));

        return builder.build();
    }

    public static Retrofit getInstanceGoogleMaps(Activity activity)
    {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create(gson));

        return builder.build();
    }
}
