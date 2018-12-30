package br.com.inteligenti.lavoutanovov2;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.inteligenti.lavoutanovov2.service.ProfissionalCO;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.ServicoTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainProfissionalActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final int CODE_CAD_SERVICO_ACTIVITY = 1;

    Timer timer;
    SharedPreferences shared;
    ListView lvAddress;
    List<Address> list;
    public List<Marker> listMarker = new ArrayList<Marker>();
    public static ServicoTO servicoTOSelecionado;
    TextView tvDescription;


    // Maps
    public static Location mLastLocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_LOCATION = 0;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private static final String TAG = "";
    private GoogleMap mMap;
    private int markerCount;
    Marker mk = null;

    ProfissionalCO profissionalCO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profissional);
        initView();

        // My init
        shared = InicialActivity.SHARED;
        profissionalCO = InicialActivity.RETROFIT.create(ProfissionalCO.class);
        initProfile();


        markerCount = 0;

        //Check If Google Services Is Available
        if (getServicesAvailable()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        //Create The MapView Fragment
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        */

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        initTimer();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
        timer = null;
    }

    private void initTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if (Register.FLAG_AGUARDANDO_PAGAMENTO) {
                    return;
                }

                //Log.i("tag", "A Kiss every 60 seconds");
                String sLastLocation = "";
                if (mLastLocation != null) {
                    sLastLocation =
                        String.valueOf(mLastLocation.getLatitude()) + "," +
                        String.valueOf(mLastLocation.getLongitude());
                }

                Integer iId = shared.getInt(InicialActivity.USER_ID, 0);
                Call<List<ServicoTO>> call = profissionalCO.check_point(iId, sLastLocation);
                call.enqueue(new Callback<List<ServicoTO>>() {
                    @Override
                    public void onResponse(Call<List<ServicoTO>> call, Response<List<ServicoTO>> response) {
                        try {
                            List<ServicoTO> list = response.body();
                            /*String jsonStr = response.body().string().toString();
                            if (jsonStr != null && jsonStr.trim() != "") {
                                JSONObject jsonObj = new JSONObject(jsonStr);
                                String sJsonServicoProf = jsonObj.getString("lista_servico_prof");

                                Register.registrarServicosProfTO(sJsonServicoProf);
                            }*/
                            Register.setListServicoProf(list);
                            show_points(list);
                        } catch (Exception e) {
                            Log.i("Exception", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ServicoTO>> call, Throwable t) {
                        Toast.makeText(getBaseContext(), "Não foi possível fazer a conexão:" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        },0,60 * 1000);
        // 1000 = 1 segundo
    }

    private void show_points(List<ServicoTO> list) {
        if (mMap != null) {
            for (Marker mk : listMarker) {
                mk.remove();
            }
            listMarker.clear();
            if (list != null)
            for (ServicoTO servicoTO : list) {
                String codgLocalizacao = servicoTO.getCodg_localizacao();
                if (codgLocalizacao != null && !codgLocalizacao.isEmpty()) {
                    String[] latLng = codgLocalizacao.split(",");
                    goToLocation(
                            Double.valueOf(latLng[0]),
                            Double.valueOf(latLng[1]),
                            15,
                            servicoTO
                    );
                }
            }
        }
    }

    private void initProfile() {
        String sNome = shared.getString(InicialActivity.USER_NAME, "");
        String sEmail = shared.getString(InicialActivity.USER_EMAIL, "");
        Integer iId = shared.getInt(InicialActivity.USER_ID, 0);
        String txt = "" + iId + "-" + sNome;

        String url = shared.getString(InicialActivity.USER_LOGO, "");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView etNome = (TextView) hView.findViewById(R.id.etNome);
        TextView etEmail = (TextView) hView.findViewById(R.id.etEmail);
        etNome.setText(sNome);
        etEmail.setText(sEmail);

        ImageView imgViewLogo = (ImageView) hView.findViewById(R.id.imgLogo);

        Register.iniciarLogo(imgViewLogo);
        /*if (url != "") {
            File file = new File(url);
            if (file.exists()) {
                ImageView imgViewLogo = (ImageView) hView.findViewById(R.id.imgLogo);
                Picasso.with(MainActivity.this).load(file).into(imgViewLogo);
            }
        }*/
    }

    public void change_description(View view)
    {
        if (tvDescription != null) {
            int iResource = 0;
            switch (view.getId()) {
                /*case R.id.icon_titan:
                    iResource = R.string.desc_titan;
                    break;
                case R.id.icon_imperial:
                    iResource = R.string.desc_imperial;
                    break;
                case R.id.icon_diamante:
                    iResource = R.string.desc_diamante;
                    break;
                case R.id.icon_ouro:
                    iResource = R.string.desc_ouro;
                    break;
                case R.id.icon_external:
                    iResource = R.string.desc_external;
                    break;
                case R.id.icon_master:
                    iResource = R.string.desc_master;
                    break;*/
            }

            if (iResource>0) {
                tvDescription.setText(getResources().getString(iResource));
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profissional);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profissional);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_servico_atual || id == R.id.nav_list_servico) {

            Intent intent = new Intent(MainProfissionalActivity.this, ListServicoActivity.class);
            intent.putExtra(
                "tipo",
                (id == R.id.nav_servico_atual)? "ATUAL":"REALIZADO"
            ); // P = Profissional

            startActivity(intent);

        } else if (id == R.id.nav_sobre_app) {
            startActivity(new Intent(MainProfissionalActivity.this, SobreAppActivity.class));

        } else if (id == R.id.nav_exit) {
            sair();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profissional);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sair() {
        SharedPreferences.Editor shared = getSharedPreferences(InicialActivity.MEMORIA_INTERNA, 0).edit();
        shared.clear();
        shared.commit();
        LoginManager.getInstance().logOut();
        finish();
    }

    private void goToLocation(double lat, double lng, float zoom, ServicoTO tag) {
        if (mMap != null) {
            LatLng ll = new LatLng(lat, lng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
            mMap.moveCamera(cameraUpdate);

            MarkerOptions markerOptions = new MarkerOptions()
                    .title("O carro está aqui\n")
                    //.snippet(endereco)
                    //.draggable(true)
                    .position(new LatLng(lat, lng));
            Marker mk = mMap.addMarker(markerOptions);
            listMarker.add(mk);
            //getGoogleAddress(mk);

            mk.setTag(tag);
            mk.setSnippet(tag.getDesc_endereco());
        }
    }

    public void getGeocoderAddress(Marker mk) {
        LatLng latLng = mk.getPosition();
        Address address = null;
        String endereco;
        try {
            Geocoder gc = new Geocoder(this);
            List<Address> addresses = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null) {
                address = addresses.get(0);

                // Processa Endereço
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getFeatureName()).append("\n");
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append(" ");
                sb.append(address.getCountryName());
                endereco = sb.toString();

                mk.setSnippet(endereco);
                mk.setTag(address);
            }

        } catch (Exception e) {

        }
    }

    /**
     * GOOGLE MAPS AND MAPS OBJECTS
     */

    // After Creating the Map Set Initial Location
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Uncomment To Show Google Location Blue Pointer
        // mMap.setMyLocationEnabled(true);
        if (mMap != null) {
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker mk) {
                    if (mk.getTag() instanceof ServicoTO) {

                        servicoTOSelecionado = (ServicoTO) mk.getTag();
                        Intent intent = new Intent(MainProfissionalActivity.this, CadServicoActivity.class);
                        startActivityForResult(intent, CODE_CAD_SERVICO_ACTIVITY);
                    }

                }
            });

            show_points(Register.getListServicoProf());
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Add A Map Pointer To The MAp
    public void addMarker(GoogleMap googleMap, double lat, double lon) {

        if (markerCount == 1) {
            animateMarker(mLastLocation, mk);
        } else if (markerCount == 0) {
            //Set Custom BitMap for Pointer
            int height = 80;
            int width = 45;
            //BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.mipmap.icon_car);
            //Bitmap b = bitmapdraw.getBitmap();
            //Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap = googleMap;

            LatLng latlong = new LatLng(lat, lon);
            mk = mMap.addMarker(
                    new MarkerOptions().position(new LatLng(lat, lon))
                            .icon(bitmapDescriptorFromVector(MainProfissionalActivity.this, R.drawable.ic_my_location_black_24dp))
                            .anchor(0.5f, 0.5f)
                    //.icon(BitmapDescriptorFactory.fromBitmap((smallMarker)))
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15));
            //goToLocation(lat, lon, 15);

            //Set Marker Count to 1 after first marker is created
            markerCount = 1;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            //mMap.setMyLocationEnabled(true);
            startLocationUpdates();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (CODE_CAD_SERVICO_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    show_points(Register.getListServicoProf());
                }
                break;
            }
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        if (Register.FLAG_DEBUG) {
            Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
        }
    }


    public boolean getServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {

            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Não foi possível se conectar ao Google.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    /**
     * LOCATION LISTENER EVENTS
     */

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
//        startLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getServicesAvailable();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //Method to display the location on UI
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                if (Register.FLAG_DEBUG) {
                    String loc = "" + latitude + " ," + longitude + " ";
                    Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
                }

                //Add pointer to the map at location
                addMarker(mMap, latitude, longitude);

            } else {

                Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Creating google api client object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    //Creating location request object
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LoginFacebookApp.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LoginFacebookApp.FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(LoginFacebookApp.DISPLACEMENT);
    }


    //Starting the location updates
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    //Stopping location updates
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
        // Displaying the new location on UI
        displayLocation();
    }


    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final MainProfissionalActivity.LatLngInterpolator latLngInterpolator = new MainProfissionalActivity.LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements MainProfissionalActivity.LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

}
