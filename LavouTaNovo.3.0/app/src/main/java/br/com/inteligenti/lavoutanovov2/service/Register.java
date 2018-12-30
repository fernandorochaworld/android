package br.com.inteligenti.lavoutanovov2.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.com.inteligenti.lavoutanovov2.InicialActivity;
import br.com.inteligenti.lavoutanovov2.to.CarroTO;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import br.com.inteligenti.lavoutanovov2.to.ProfissionalTO;
import br.com.inteligenti.lavoutanovov2.to.ServicoTO;

/**
 * Created by fernando on 09/01/18.
 */
public class Register {

    public static String USER_PROFISSIONAL = "USER_PROFISSIONAL";
    public static String LIST_SERVICO_REALIZADO = "LIST_SERVICO_REALIZADO";
    public static String LIST_SERVICO_TODO = "LIST_SERVICO_TODO";
    public static String LIST_SERVICO      = "LIST_SERVICO";
    public static String LIST_CARRO        = "LIST_CARRO";
    public static String LIST_DOC          = "LIST_DOC";
    public static boolean FLAG_DEBUG        = false;
    public static boolean FLAG_AGUARDANDO_PAGAMENTO = false;

    public static Gson gson = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    public static boolean isConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) InicialActivity.PUBLIC_ACTIVITY.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public static String getIpService() {
        //String ip = (InicialActivity.PUBLIC_ACTIVITY != null || isConnected())?
        //        "192.168.15.9" : "192.168.42.163";
        //return ip;
        return "inteligenti.com.br";
    }

    public static String getUrlService() {
        String ip = getIpService();
        //return "http://"+ip+"/analista2/";
        return "http://"+ip+"/lavoutanovo/";
    }

    public static String getUrlImg(String sFile) {
        return getUrlService() + "repositorio/" + sFile;
    }

    public static boolean hasPermission(Activity activity, String permission) {
        return (
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            )
            ==
            PackageManager.PERMISSION_GRANTED
        );
    }

    public static void getPermission(Activity activity, String[] aPermission) {
        // Check Permissions Now
        ActivityCompat.requestPermissions(
            activity,
            aPermission,
            1
        );
    }

    public static void askPermissions(Activity activity) {
        List<String> listPermission = new ArrayList<String>();

        if (!hasPermission(activity, Manifest.permission.INTERNET)) {
            listPermission.add(Manifest.permission.INTERNET);
        }
        if (!hasPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE)) {
            listPermission.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (!hasPermission(activity, Manifest.permission.CAMERA)) {
            listPermission.add(Manifest.permission.CAMERA);
        }
        if (!hasPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!hasPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!hasPermission(activity, Manifest.permission.LOCATION_HARDWARE)) {
            listPermission.add(Manifest.permission.LOCATION_HARDWARE);
        }
        if (!hasPermission(activity, Manifest.permission.READ_PHONE_STATE)) {
            listPermission.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!hasPermission(activity, Manifest.permission.MEDIA_CONTENT_CONTROL)) {
            listPermission.add(Manifest.permission.MEDIA_CONTENT_CONTROL);
        }
        if (!hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            listPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            listPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (listPermission.size() > 0) {
            String[] aPermission = new String[listPermission.size()];
            listPermission.toArray(aPermission);
            getPermission(activity, aPermission);
        }
    }

    public static void share(String key, String val)
    {
        SharedPreferences.Editor editor = InicialActivity.SHARED.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public static int getNextIdDoc()
    {
        SharedPreferences.Editor editor = InicialActivity.SHARED.edit();
        SharedPreferences shared = InicialActivity.SHARED;
        int numr_doc = shared.getInt(InicialActivity.SEQUENCE_DOC, 1);
        editor.putInt(InicialActivity.SEQUENCE_DOC, ++numr_doc);
        return numr_doc;
    }

    public static String getDoc(Integer id)
    {
        String nome = getListDoc().get(id);
        if (nome != null) {
            return InicialActivity.PUBLIC_ACTIVITY.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/" + nome;
        } else {
            return null;
        }
    }

    public static HashMap<Integer, String> getListDoc()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sJson = shared.getString(LIST_DOC,"");
        if (sJson == "") {
            return new HashMap<Integer, String>();
        } else {
            Type type = new TypeToken<HashMap<Integer, String>>() {}.getType();
            HashMap<Integer, String> list = gson.fromJson(sJson, type);
            return list;
        }
    }

    public static void addDoc(Integer id, String nome_arquivo)
    {
        HashMap<Integer, String> list = getListDoc();
        list.put(id, nome_arquivo);
        setListDoc(list);
    }

    public static void setListDoc(HashMap<Integer, String> list)
    {
        SharedPreferences.Editor editor = InicialActivity.SHARED.edit();

        String sJson = gson.toJson(list);
        editor.putString(LIST_DOC, sJson);
        editor.commit();
    }

    // Percistencia Serviço
    public static List<ServicoTO> getListServico()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sJson = shared.getString(LIST_SERVICO,"");
        if (sJson == "") {
            return new ArrayList<ServicoTO>();
        } else {
            Type type = new TypeToken<List<ServicoTO>>(){}.getType();
            List<ServicoTO> list = gson.fromJson(sJson, type);
            return list;
        }
    }

    public static void editServico(ServicoTO servicoTO, int index)
    {
        List<ServicoTO> list = getListServico();
        list.set(index, servicoTO);
        setListServico(list);
    }

    public static void addServico(ServicoTO servicoTO)
    {
        List<ServicoTO> list = getListServico();
        list.add(servicoTO);
        setListServico(list);
    }

    public static void setListServico(List<ServicoTO> listServico)
    {
        SharedPreferences.Editor editor = InicialActivity.SHARED.edit();

        String sJson = gson.toJson(listServico);
        editor.putString(LIST_SERVICO, sJson);
        editor.commit();
    }

    // Lista Serviços Profissional

    /*public static void registrarServicosProf(String sJsonServicosProf)
    {
        SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
        shared.putString(Register.LIST_SERVICO_TODO, sJsonServicosProf);
        shared.commit();
    }*/

    public static void removerServico(ServicoTO servicoTO)
    {
        List<ServicoTO> list = getListServico();
        //list.remove(servicoTO);
        Iterator<ServicoTO> i = list.iterator();
        while (i.hasNext()) {
            ServicoTO serv = i.next();
            if (serv.getId().equals(servicoTO.getId())) {
                i.remove();
            }
        }
        setListServico(list);
    }

    public static void removerServicoProf(ServicoTO servicoTO)
    {
        List<ServicoTO> list = getListServicoProf();
        //list.remove(servicoTO);
        Iterator<ServicoTO> i = list.iterator();
        while (i.hasNext()) {
            ServicoTO serv = i.next();
            if (serv.getId().equals(servicoTO.getId())) {
                i.remove();
            }
        }
        setListServicoProf(list);
    }

    public static void setListServicoProf(List<ServicoTO> list)
    {
        if (list != null) {
            String sJson = gson.toJson(list);
            SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
            shared.putString(Register.LIST_SERVICO_TODO, sJson);
            shared.commit();
        }
    }

    public static List<ServicoTO> getListServicoProf()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sJson = shared.getString(LIST_SERVICO_TODO,"");
        List<ServicoTO> list = new ArrayList<ServicoTO>();
        if (sJson != "") {
            try {
                Type type = new TypeToken<List<ServicoTO>>(){}.getType();
                list = gson.fromJson(sJson, type);
            }catch (Exception e) {

            }
        }
        return list;
    }

    public static void setListServicoRealizado(List<ServicoTO> list)
    {
        if (list != null) {
            String sJson = gson.toJson(list);
            SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
            shared.putString(Register.LIST_SERVICO_REALIZADO, sJson);
            shared.commit();
        }
    }

    public static List<ServicoTO> getListServicoRealizado()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sJson = shared.getString(LIST_SERVICO_REALIZADO,"");
        List<ServicoTO> list = new ArrayList<ServicoTO>();
        if (sJson != "") {
            try {
                Type type = new TypeToken<List<ServicoTO>>(){}.getType();
                list = gson.fromJson(sJson, type);
            }catch (Exception e) {

            }
        }
        return list;
    }

    // Percistencia Carro
    public static List<CarroTO> getListCarro()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sJson = shared.getString(LIST_CARRO,"");
        if (sJson == "") {
            return new ArrayList<CarroTO>();
        } else {
            Type type = new TypeToken<List<CarroTO>>(){}.getType();
            List<CarroTO> listCarro = gson.fromJson(sJson, type);
            return listCarro;
        }
    }

    public static void editCarro(CarroTO carroTO, int index)
    {
        List<CarroTO> list = getListCarro();
        list.set(index, carroTO);
        setListCarro(list);
    }

    public static void addCarro(CarroTO carroTO)
    {
        List<CarroTO> list = getListCarro();
        list.add(carroTO);
        setListCarro(list);
    }

    public static void setListCarro(List<CarroTO> listCarro)
    {
        SharedPreferences.Editor editor = InicialActivity.SHARED.edit();

        String sJson = gson.toJson(listCarro);
        editor.putString(LIST_CARRO, sJson);
        editor.commit();
    }

    public static String getCurrentDateIndex()
    {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd_HHmmss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    public static void registrarLogin(ClienteTO clienteTO)
    {
        SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
        shared.putString(InicialActivity.USER_TYPE, InicialActivity.USER_TYPE_CLIENTE);
        shared.putString(InicialActivity.USER_NAME, clienteTO.getDesc_nome());
        shared.putString(InicialActivity.USER_EMAIL, clienteTO.getDesc_email());
        shared.putString(InicialActivity.USER_ID_FACEBOOK, clienteTO.getDesc_id_facebook());
        shared.putInt(InicialActivity.USER_ID, clienteTO.getId());
        shared.commit();
    }

    public static void registrarClienteTO(ClienteTO clienteTO)
    {
        String sJson = gson.toJson(clienteTO);

        SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
        shared.putString(InicialActivity.USER_CLIENTE, sJson);
        shared.commit();
    }

    public static ClienteTO getClienteTO()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sCliente = shared.getString(InicialActivity.USER_CLIENTE, "");
        ClienteTO clienteTO = null;
        if (sCliente.isEmpty()) {
            clienteTO = new ClienteTO();
            clienteTO.setId(shared.getInt(InicialActivity.USER_ID, 0));
            clienteTO.setDesc_nome(shared.getString(InicialActivity.USER_NAME, ""));
            clienteTO.setDesc_email(shared.getString(InicialActivity.USER_EMAIL, ""));
            clienteTO.setDesc_id_facebook(shared.getString(InicialActivity.USER_ID_FACEBOOK, ""));
        } else {
            clienteTO = gson.fromJson(sCliente, ClienteTO.class);
        }
        return clienteTO;
    }

    public static boolean isProfissional()
    {
        String type = InicialActivity.SHARED.getString(
            InicialActivity.USER_TYPE, InicialActivity.USER_TYPE_CLIENTE
        );
        return type.equals(InicialActivity.USER_TYPE_PROFISSIONAL);
    }

    public static void registrarLoginProfissional(ProfissionalTO profissionalTO)
    {
        SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
        shared.putString(InicialActivity.USER_TYPE, InicialActivity.USER_TYPE_PROFISSIONAL);
        shared.putString(InicialActivity.USER_NAME, profissionalTO.getNomeProfissional());
        shared.putString(InicialActivity.USER_EMAIL, profissionalTO.getDescEmail());
        shared.putInt(InicialActivity.USER_ID, profissionalTO.getId());
        shared.commit();
    }

    public static void registrarProfissionalTO(ProfissionalTO profissionalTO)
    {
        String sJson = gson.toJson(profissionalTO);

        SharedPreferences.Editor shared = InicialActivity.SHARED.edit();
        shared.putString(Register.USER_PROFISSIONAL, sJson);
        shared.commit();
    }

    public static ProfissionalTO getProfissionalTO()
    {
        SharedPreferences shared = InicialActivity.SHARED;
        String sProfissional = shared.getString(Register.USER_PROFISSIONAL, "");
        ProfissionalTO profissionalTO = null;
        if (sProfissional.isEmpty()) {
            profissionalTO = new ProfissionalTO();
            profissionalTO.setId(shared.getInt(InicialActivity.USER_ID, 0));
            profissionalTO.setNomeProfissional(shared.getString(InicialActivity.USER_NAME, ""));
            profissionalTO.setDescEmail(shared.getString(InicialActivity.USER_EMAIL, ""));
        } else {
            profissionalTO = gson.fromJson(sProfissional, ProfissionalTO.class);
        }
        return profissionalTO;
    }

    public static String getFacebookURL(String id)
    {
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    public static void iniciarLogo(ImageView imgViewLogo)
    {
        String url = InicialActivity.SHARED.getString(InicialActivity.USER_LOGO, "");
        if (url != "") {
            File file = new File(url);
            if (file.exists()) {
                Picasso.with(imgViewLogo.getContext()).load(file).into(imgViewLogo);
            }
        } else {
            ClienteTO clienteTO = Register.getClienteTO();
            if (clienteTO!=null && clienteTO.getDescImgProfile() != null && !clienteTO.getDescImgProfile().isEmpty()) {
                String sNovaUrl = Register.downloadDocument(
                    imgViewLogo.getContext(),
                    clienteTO.getDescImgProfile(),
                    imgViewLogo
                );
                if (sNovaUrl!=null && !sNovaUrl.isEmpty()) {
                    Register.share(InicialActivity.USER_LOGO, sNovaUrl);
                }
            }
        }
    }
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static String downloadDocument(Context activity, String sFileName, ImageView imageView) {
        String url = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/" + sFileName;
        if (!new File(url).exists()) {
            PhotoLoader fl = new PhotoLoader(sFileName, activity, Environment.DIRECTORY_PICTURES, imageView);
            Picasso.with(activity).load(Register.getUrlImg(sFileName)).into(fl);
            return fl.getUrl();
        } else {
            return url;
        }
    }

    public static String getLocalImagePath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
    }

}
