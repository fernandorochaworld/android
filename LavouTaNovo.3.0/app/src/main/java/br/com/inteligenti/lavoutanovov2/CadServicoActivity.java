package br.com.inteligenti.lavoutanovov2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.vicmikhailau.maskededittext.MaskedEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import br.com.inteligenti.lavoutanovov2.service.ConectorRetrofit;
import br.com.inteligenti.lavoutanovov2.service.PagSeguroCO;
import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.service.ServicoCO;
import br.com.inteligenti.lavoutanovov2.service.Util;
import br.com.inteligenti.lavoutanovov2.to.CarroTO;
import br.com.inteligenti.lavoutanovov2.to.ClienteTO;
import br.com.inteligenti.lavoutanovov2.to.CreditCard;
import br.com.inteligenti.lavoutanovov2.to.CreditCardTO;
import br.com.inteligenti.lavoutanovov2.to.ProfissionalTO;
import br.com.inteligenti.lavoutanovov2.to.ServicoTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CadServicoActivity extends AppCompatActivity implements Observer
{

    CreditCard cc = new CreditCard(CadServicoActivity.this);
    CreditCardTO ccTO = new CreditCardTO();
    ServicoTO servicoTO;
    ServicoCO servicoCO;
    EditText etData;
    EditText etHora;
    EditText etEndereco;
    EditText etValor;
    Spinner spServico;
    Spinner spCarro;

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
    Button btnSolicitar;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dfData = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dfHora = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dfDataProducao = new SimpleDateFormat("yyyy-MM-dd");
    double valrServico = 0;
    List<CarroTO> listCarro;
    String[] aCarro;
    String[] aEstado;
    String[] aEstadoNome;
    String[] aServico;

    String index;
    String tipo_consulta = null;
    public static ServicoTO servicoTOSelecionado;
    private static final int CODE_CAD_SERVICO_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_servico);
        Locale.setDefault(new Locale("pt", "BR"));  // mudança global
        Register.FLAG_AGUARDANDO_PAGAMENTO = true;

        calendar.add(Calendar.MINUTE, 30);
        Intent intent = getIntent();
        int codg_servico = intent.getIntExtra("codg_servico", 0);
        String codg_latlng = intent.getStringExtra("codg_latlng");
        String desc_endereco = intent.getStringExtra("desc_endereco");
        ClienteTO clienteTO = Register.getClienteTO();

        tipo_consulta = intent.getStringExtra("tipo");

        index = intent.getStringExtra("index_item");

        iniciarView();

        updateDataHora();
        fillArrayCarro();

        spServico.setSelection(codg_servico);
        spServico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeValrSerValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etEndereco.setText(desc_endereco);

        //selected item will look like a spinner set from XML
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(CadServicoActivity.this, android.R.layout.simple_spinner_item, aCarro);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCarro.setAdapter(spinnerArrayAdapter);
        spCarro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeValrSerValor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        servicoCO = InicialActivity.RETROFIT.create(ServicoCO.class);

        Boolean isProfissional = Register.isProfissional();
        if (index != null) {
            // Serviço Selecionado na listagem Cliente / Profissional

            try {
                if (isProfissional) {

                    if (tipo_consulta==null || !tipo_consulta.equals("REALIZADO")) {
                        servicoTO = Register.getListServicoProf().get(Integer.valueOf(index));
                    } else {
                        servicoTO = Register.getListServicoRealizado().get(Integer.valueOf(index));
                    }
                } else {
                    servicoTO = Register.getListServico().get(Integer.valueOf(index));
                }
            } catch (Exception e) {
                finish();
            }
        } else if (isProfissional && MainProfissionalActivity.servicoTOSelecionado != null) {
            // Serviço Selecionado Profissional

            servicoTO = MainProfissionalActivity.servicoTOSelecionado;
            MainProfissionalActivity.servicoTOSelecionado = null;
        } else if (!isProfissional && MainActivity.servicoTOSelecionado != null) {
            // Serviço Selecionado Cliente / Avaliação

            servicoTO = MainActivity.servicoTOSelecionado;
            //MainActivity.servicoTOSelecionado = null;
            disableView(false);
        } else if (!isProfissional && CadServicoActivity.servicoTOSelecionado != null) {
            // Serviço Selecionado Cliente / Conferencia solicitação

            servicoTO = CadServicoActivity.servicoTOSelecionado;
            CadServicoActivity.servicoTOSelecionado = null;
            disableView(false);
        } else {
            // Serviço Solicitação

            servicoTO = new ServicoTO();
            servicoTO.setId_cliente(clienteTO.getId());
            servicoTO.setCodg_localizacao(codg_latlng);
        }
        iniciarDados();
    }

    private void iniciarView() {
        btnSolicitar = (Button) findViewById(R.id.btnSolicitar);
        etData = (EditText) findViewById(R.id.etData);
        etHora = (EditText) findViewById(R.id.etHora);
        etEndereco = (EditText) findViewById(R.id.etEndereco);
        etValor = (EditText) findViewById(R.id.etValor);
        spServico = (Spinner) findViewById(R.id.spServico);
        spCarro = (Spinner) findViewById(R.id.spCarro);


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
        aServico = getResources().getStringArray(R.array.lista_servico);
    }



    private void iniciarDados()
    {
        Boolean isProfissional = Register.isProfissional();
        if (isProfissional) {
            CardView card = (CardView) findViewById(R.id.card_cliente);
            card.setVisibility(View.VISIBLE);
            CardView card_carro = (CardView) findViewById(R.id.card_carro);
            card_carro.setVisibility(View.VISIBLE);
            Spinner spCarro = (Spinner) findViewById(R.id.spCarro);
            spCarro.setVisibility(View.GONE);

            boolean bExecutando = servicoTO.getCodg_situacao() != null && servicoTO.getCodg_situacao().equals("Solicitado");
            String sLatLong = servicoTO.getCodg_localizacao();
            if (bExecutando && sLatLong != null && !sLatLong.isEmpty() && MainProfissionalActivity.mLastLocation != null) {
                Location location = MainProfissionalActivity.mLastLocation;
                String[] aLatLng = sLatLong.split(",");
                Double distance = Register.distance(
                    Double.parseDouble(aLatLng[0]),
                    location.getLatitude(),
                    Double.parseDouble(aLatLng[1]),
                    location.getLongitude(),
                    0.0d,
                    0.0d
                );
                if (aLatLng.length == 2 && distance < 1000.0d) {
                    CardView card_avaliar = (CardView) findViewById(R.id.card_servico_avaliar);
                    card_avaliar.setVisibility(View.VISIBLE);
                }
            }

            EditText etClienteNome = (EditText) findViewById(R.id.etClienteNome);
            EditText etClienteTelefone = (EditText) findViewById(R.id.etClienteTelefone);

            EditText etCarroMarca = (EditText) findViewById(R.id.etCarroMarca);
            EditText etCarroModelo = (EditText) findViewById(R.id.etCarroModelo);
            EditText etCarroPlaca = (EditText) findViewById(R.id.etCarroPlaca);
            EditText etCarroOutraCor = (EditText) findViewById(R.id.etCarroOutraCor);
            Spinner spCarroCor = (Spinner) findViewById(R.id.spCarroCor);
            spCarroCor.setEnabled(false);
            Spinner spCarroTamanho = (Spinner) findViewById(R.id.spCarroTamanho);
            spCarroTamanho.setEnabled(false);

            if (servicoTO.getCliente() != null) {
                ClienteTO clienteTO = servicoTO.getCliente();
                etClienteNome.setText(clienteTO.getDesc_nome());
                etClienteTelefone.setText(clienteTO.getDesc_telefone());

                // Carregar Imagem
                ImageView imgViewLogo = (ImageView) findViewById(R.id.ivClienteProfileImage);
                if (clienteTO.getDescImgProfile() != null && !clienteTO.getDescImgProfile().isEmpty()) {
                    /*Picasso.with(CadServicoActivity.this)
                        .load(Register.getUrlImg(clienteTO.getDescImgProfile()))
                        //Picasso.with(CadServicoActivity.this).load("http://square.github.io/picasso/static/sample.png")
                        .into(imgViewLogo);
                        */
                    Register.downloadDocument(
                    CadServicoActivity.this,
                        clienteTO.getDescImgProfile(),
                        imgViewLogo
                    );
                }
            }
            if (servicoTO.getCarro() != null) {
                CarroTO carroTO = servicoTO.getCarro();
                etCarroMarca.setText(carroTO.getDesc_marca());
                etCarroModelo.setText(carroTO.getDesc_modelo());
                etCarroOutraCor.setVisibility(
                    (carroTO.getDesc_outra_cor()==null || carroTO.getDesc_outra_cor().isEmpty())?
                    View.GONE:
                    View.VISIBLE
                );
                etCarroOutraCor.setText(carroTO.getDesc_outra_cor());
                etCarroPlaca.setText(carroTO.getCodg_placa());

                String[] aStringCor = getResources().getStringArray(R.array.lista_cor);
                for(int i =0; i<aStringCor.length; i++) {
                    if (aStringCor[i].equals(carroTO.getCodg_cor())) {
                        spCarroCor.setSelection(i);
                        break;
                    }
                }
                String[] aStringTam = getResources().getStringArray(R.array.lista_tamanho_carro);
                if (carroTO.getCodg_tamanho() != "")
                    for(int i =0; i<aStringTam.length; i++) {
                        if (aStringTam[i].charAt(0) == carroTO.getCodg_tamanho().charAt(0)) {
                            spCarroTamanho.setSelection(i);
                            break;
                        }
                    }

                // Carregar Imagem Carro
                ImageView imgViewLogo = (ImageView) findViewById(R.id.ivCarro);
                if (carroTO.getDescImg() != null && !carroTO.getDescImg().isEmpty()) {
                    Picasso.with(CadServicoActivity.this)
                        .load(Register.getUrlImg(carroTO.getDescImg()))
                        //Picasso.with(CadServicoActivity.this).load("http://square.github.io/picasso/static/sample.png")
                        .into(imgViewLogo);
                }
            }
        }

        if (!isProfissional) {
            etEndereco.setVisibility(View.GONE);

            if (servicoTO.getProfissional() != null) {
                ProfissionalTO profissionalTO = servicoTO.getProfissional();

                CardView cardProf = (CardView) findViewById(R.id.card_profissional);
                cardProf.setVisibility(View.VISIBLE);

                ImageView imgViewLogoProf = (ImageView) findViewById(R.id.ivProfissionalProfileImage);

                if (profissionalTO.getDescImgProfile() != null && !profissionalTO.getDescImgProfile().isEmpty()) {
                    Picasso.with(CadServicoActivity.this)
                        .load(Register.getUrlImg(profissionalTO.getDescImgProfile()))
                        //Picasso.with(CadServicoActivity.this).load("http://square.github.io/picasso/static/sample.png")
                        .into(imgViewLogoProf);
                }

                EditText etProfissionalNome = (EditText) findViewById(R.id.etProfissionalNome);

                EditText etProfissionalCarroMarca = (EditText) findViewById(R.id.etProfissionalCarroMarca);
                EditText etProfissionalCarroModelo = (EditText) findViewById(R.id.etProfissionalCarroModelo);
                EditText etProfissionalTipoVeiculo = (EditText) findViewById(R.id.etProfissionalTipoVeiculo);
                EditText etProfissionalCarroPlaca = (EditText) findViewById(R.id.etProfissionalCarroPlaca);
                EditText etProfissionalCarroCor = (EditText) findViewById(R.id.etProfissionalCarroCor);

                etProfissionalNome.setText(profissionalTO.getNomeProfissional());
                etProfissionalTipoVeiculo.setText(profissionalTO.getCodgTipoVeiculo());
                etProfissionalCarroPlaca.setText(profissionalTO.getCodgPlaca());
                etProfissionalCarroMarca.setText(profissionalTO.getDescMarca());
                etProfissionalCarroModelo.setText(profissionalTO.getDescModelo());
                etProfissionalCarroCor.setText(profissionalTO.getCodgCor());
            }

            if (servicoTO.getCodg_situacao() != null && servicoTO.getCodg_situacao().equals("Avaliacao")) {
                CardView card_avaliar = (CardView) findViewById(R.id.card_servico_avaliar_prof);
                card_avaliar.setVisibility(View.VISIBLE);
            }
        }

        if (servicoTO.getId() != null) {

            etEndereco.setText(servicoTO.getDesc_endereco());
            if (servicoTO.getTime_hora_servico() != null && servicoTO.getTime_hora_servico().length() >= 5) {
                etHora.setText(servicoTO.getTime_hora_servico().substring(0, 5));
            }
            if (servicoTO.getData_data_servico() != null) {
                etData.setText(dfData.format(servicoTO.getData_data_servico()));
            }
            etEndereco.setText(servicoTO.getDesc_endereco());
            setValrServico(servicoTO.getCodg_servico());

            setValEstado(servicoTO.getCodgEstado());
            etCidade.setText(servicoTO.getDescCidade());
            etBairro.setText(servicoTO.getDescBairro());
            etRua.setText(servicoTO.getDescRua());
            etCep.setText(servicoTO.getDescCep());
            etNumero.setText(servicoTO.getDescNumero());
            etEdificio.setText(servicoTO.getDescEdificio());
            etAp.setText(servicoTO.getDescAp());
            etComplemento.setText(servicoTO.getDescComplemento());
            disableView(false);

            if (servicoTO.getIdCarro() != null)
                for (CarroTO iCarro :listCarro) {
                    if (servicoTO.getIdCarro().equals(iCarro.getId())) {
                        spCarro.setSelection(listCarro.indexOf(iCarro));
                        break;
                    }
                }


            for(int i=0; i<aServico.length; i++) {
                if (aServico[i].equals(servicoTO.getIdCarro())) {
                    spServico.setSelection(i);
                    break;
                }
            }

            CardView card = (CardView)findViewById(R.id.card_servico_cad);
            card.setVisibility(View.GONE);

            TextView tvTitulo = (TextView) findViewById(R.id.tv_titulo);
            tvTitulo.setText("Serviço");
        } else if (!isProfissional) {

            Marker mk = MainActivity.mMarkerSelected;
            if (mk != null) {
                if (mk.getTag() instanceof Address) {
                    getEnderecoFromAddress((Address) mk.getTag());
                } else if (mk.getTag() instanceof String) {
                    getEnderecoFromJson((String) mk.getTag());
                }
            }
        }
    }

    private void disableView(boolean enabled) {

        Register.FLAG_AGUARDANDO_PAGAMENTO = true; //!enabled;
        btnSolicitar.setText(enabled?"Solicitar":"Processando pagamento...");
        btnSolicitar.setEnabled(enabled);
        etData.setEnabled(enabled);
        etHora.setEnabled(enabled);
        etEndereco.setEnabled(enabled);
        spServico.setEnabled(enabled);
        spCarro.setEnabled(enabled);

        spEstado.setEnabled(enabled);
        etCidade.setEnabled(enabled);
        etBairro.setEnabled(enabled);
        etRua.setEnabled(enabled);
        etCep.setEnabled(enabled);
        etNumero.setEnabled(enabled);
        etEdificio.setEnabled(enabled);
        etAp.setEnabled(enabled);
        etComplemento.setEnabled(enabled);
    }

    private void getEnderecoFromAddress(Address address)
    {
        setValEstadoByName(address.getAdminArea().trim());
        etRua.setText(address.getAddressLine(0));
        etCep.setText(address.getPostalCode());
        etCidade.setText(address.getLocality());
        etBairro.setText(address.getSubLocality());

    }

    private void getEnderecoFromJson(String jsonStr)
    {
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray results = jsonObj.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject local = results.getJSONObject(0);
                JSONArray jaAddress = local.getJSONArray("address_components");
                for(int i=0; i<jaAddress.length(); i++) {
                    JSONObject oAddress = jaAddress.getJSONObject(i);
                    String sType = oAddress.getString("types");
                    if (
                        sType.indexOf("\"locality\"") >= 0 ||
                        sType.indexOf("\"administrative_area_level_2\"") >= 0
                    ) {
                        etCidade.setText(oAddress.getString("long_name"));

                    } else if(sType.indexOf("\"administrative_area_level_1\"") >= 0) {
                        setValEstado(oAddress.getString("short_name").toUpperCase());

                    } else if(sType.indexOf("\"sublocality\"") >= 0) {
                        etBairro.setText(oAddress.getString("long_name"));

                    } else if(sType.indexOf("\"route\"") >= 0) {
                        etRua.setText(oAddress.getString("long_name"));

                    } else if(sType.indexOf("\"street_number\"") >= 0) {
                        etNumero.setText(oAddress.getString("long_name"));

                    } else if(sType.indexOf("\"postal_code\"") >= 0) {
                        etCep.setText(oAddress.getString("long_name"));

                    }
                }
            }
        } catch (Exception e) {

        }
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

    private void setValrServico(String sServico) {
        for(int i = 0; i<aServico.length; i++) {
            if (aServico[i].equals(sServico)) {
                spServico.setSelection(i);
                break;
            }
        }
    }

    public void fillArrayCarro()
    {
        listCarro = Register.getListCarro();
        aCarro = new String[listCarro.size()];
        Iterator<CarroTO> it = listCarro.iterator();
        int i = 0;

        while (it.hasNext()) {
            CarroTO carroTO = it.next();
            String sCar = carroTO.getCodg_placa() + " - " +
                    carroTO.getDesc_marca() + " " +
                    carroTO.getDesc_modelo() + " - " +
                    carroTO.getCodg_cor();
            aCarro[i++] = sCar.toUpperCase();
        }
    }

    private void updateDataHora() {
        etData.setText(dfData.format(calendar.getTime()));
        etHora.setText(dfHora.format(calendar.getTime()));
    }


    public void updateData(View view) {
        new DatePickerDialog(this, d, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDataHora();
        }
    };

    public void updateHora(View view) {
        new TimePickerDialog(this, t, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            updateDataHora();
        }
    };

    public void changeValrSerValor() {
        double valr_valor = 0d;
        CarroTO carroTO;
        if (Register.isProfissional()) {
            carroTO = servicoTO.getCarro();
        } else {
            int iCarro = spCarro.getSelectedItemPosition();
            carroTO = listCarro.get(iCarro);
        }
        boolean bCarroG = carroTO.getCodg_tamanho().equals("G");

        switch (spServico.getSelectedItemPosition()) {
            case 0:
                valr_valor = 30d;
                if (bCarroG) {
                    valr_valor = 40d;
                }
                break;
            case 1:
                valr_valor = 30d;
                if (bCarroG) {
                    valr_valor = 35d;
                }
                break;
            case 2:
                valr_valor = 45d;
                if (bCarroG) {
                    valr_valor = 55d;
                }
                break;
            case 3:
                valr_valor = 55d;
                if (bCarroG) {
                    valr_valor = 65d;
                }
                break;
            case 4:
                valr_valor = 140d;
                if (bCarroG) {
                    valr_valor = 160d;
                }
                break;
            case 5:
                valr_valor = 170d;
                if (bCarroG) {
                    valr_valor = 190d;
                }
                break;
        }
        valrServico = valr_valor;
        DecimalFormat df = new DecimalFormat( "R$ #,##0.00" );
        etValor.setText(df.format(valrServico));
    }

    private void preencherTO()
    {
        servicoTO.setDesc_endereco(etEndereco.getText().toString().trim());
        servicoTO.setDesc_endereco(etEndereco.getText().toString().trim());
        servicoTO.setCodg_servico(
                //listCarro.get(spServico.getSelectedItemPosition()).getId()
                spServico.getSelectedItem().toString()
        );
        servicoTO.setValr_valor(valrServico);
        servicoTO.setData_data_servico(calendar.getTime());
        servicoTO.setTime_hora_servico(dfHora.format(calendar.getTime()));

        servicoTO.setCodgEstado(aEstado[spEstado.getSelectedItemPosition()]);
        servicoTO.setDescCidade(etCidade.getText().toString().trim());
        servicoTO.setDescBairro(etBairro.getText().toString().trim());
        servicoTO.setDescRua(etRua.getText().toString().trim());
        servicoTO.setDescCep(etCep.getText().toString().trim());
        servicoTO.setDescNumero(etNumero.getText().toString().trim());
        servicoTO.setDescEdificio(etEdificio.getText().toString().trim());
        servicoTO.setDescAp(etAp.getText().toString().trim());
        servicoTO.setDescComplemento(etComplemento.getText().toString().trim());

        CarroTO carroTO = listCarro.get(spCarro.getSelectedItemPosition());
        servicoTO.setIdCarro(carroTO.getId());

        if (servicoTO.getId() == null) {
            servicoTO.setCodg_situacao("Solicitado");
            servicoTO.setNumr_estrelas_cliente(1);
            servicoTO.setNumr_estrelas_profissional(1);
        }
    }

    public boolean validar()
    {
        boolean valido = true;
        String msg = "";
        if (servicoTO.getId_cliente() == null) {
            msg += "Cliente não foi informado.\n";
            valido = false;
        }
        if (servicoTO.getCodg_localizacao().trim().isEmpty()) {
            msg += "A localização não foi informada.\n";
            valido = false;
        }
        if (etEndereco.getText().toString().trim().isEmpty()) {
            etEndereco.setError("O campo endereço é obrigatório.");
            valido = false;
        }
        if (etValor.getText().toString().trim().isEmpty()) {
            etEndereco.setError("O campo Valor é obrigatório.");
            valido = false;
        }
        if (spServico.getSelectedItem().toString().isEmpty()) {
            msg += "O campo Serviço é obrigatório.\n";
            valido = false;
        }
        if (!(valrServico > 0)) {
            msg += "O campo Valor do Serviço é obrigatório.\n";
            valido = false;
        }

        Calendar cDataFinal = Calendar.getInstance();
        cDataFinal.add(Calendar.MONTH, 1);

        if (calendar.getTime().before(Calendar.getInstance().getTime())) {
            msg += "A data/hora não deve ser inferior a atual.\n";
            valido = false;
        }
        if (calendar.getTime().after(cDataFinal.getTime())) {
            msg += "A data/hora tem o limite de um mês para solicitação.\n";
            valido = false;
        }

        if (etCidade.getText().toString().isEmpty()) {
            etCidade.setError("O campo cidade é obrigatório.");
            valido = false;
        }
        if (etBairro.getText().toString().isEmpty()) {
            etBairro.setError("O campo bairro é obrigatório.");
            valido = false;
        }
        if (etRua.getText().toString().isEmpty()) {
            etRua.setError("O campo rua/avenida é obrigatório.");
            valido = false;
        }
        if (etCep.getText().toString().isEmpty()) {
            etCep.setError("O campo CEP é obrigatório.");
            valido = false;
        }
        if (!valido) {
            dialogBox(msg, "", false);
        }
        return valido;
    }

    public void salvar(View view)
    {
        boolean valido = validar();

        if (valido) {
            disableView(false);
            pay();
        }
    }

    public void dialogBox(String msg, String titulo, boolean bCancel) {
        if (titulo.isEmpty()) {
            titulo = "Validação do formulário";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }
            );
        if (bCancel) {
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    }
            );
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void dialogBox_ConfirmarRegistro() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
        .setTitle("Confirmação")
        .setMessage("Solicitação Registrada com Sucesso.")
        .setPositiveButton("Ok",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    selecionarServico();
                }
            }
        );

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
        //alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
    }

    private void selecionarServico() {
        if (servicoTOSelecionado != null) {
            Intent intent = new Intent(CadServicoActivity.this, CadServicoActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void pay() {
        final int GET_NEW_CARD = 2;

        Intent intent = new Intent(CadServicoActivity.this, CardEditActivity.class);
        startActivityForResult(intent,GET_NEW_CARD);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {

            String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            String[] aExpiry = expiry.split("/");

            // Your processing goes here.

            cc.setName(cardHolderName);
            cc.setCardNumber(cardNumber);
            cc.setMonth(aExpiry[0]);
            cc.setYear("20"+aExpiry[1]);
            cc.setCvv(cvv);

            PagSeguroCO pagseguroCO = InicialActivity.RETROFIT.create(PagSeguroCO.class);
            Call<ResponseBody> call = pagseguroCO.getToken();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String sSessionId = response.body().string().toString();
                        //cc.setCardNumber("4111"+"1111"+"1111"+"1111");
                        //cc.setMonth("12");
                        //cc.setYear("2030");
                        //cc.setCvv("123");
                        cc.setSessionId(sSessionId);
                        getPaymentToken( cc );
                    } catch (Exception e) {
                        mensagemErroPagamento(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mensagemErroPagamento(t.getMessage());
                }
            });

        } else {
            disableView(true);
        }
    }

    private void mensagemErroPagamento(String msg) {
        Toast.makeText(
            getBaseContext(),
            msg,
            //"Erro ao processar pagamento/solicitação, favor tentar novamente mais tarde.",
            Toast.LENGTH_LONG
        ).show();
        disableView(true);
    }

    private void getPaymentToken( CreditCard creditCard ){
        WebView webView = (WebView) findViewById(R.id.web_view);

        webView.setWebViewClient(
            new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    mensagemErroPagamento(description);
                }

                /*@Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    mensagemErroPagamento(errorResponse.toString());
                }*/
            }
        );


        webView.getSettings().setJavaScriptEnabled( true );
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface( creditCard, "Android" );
        //webView.loadUrl("file:///android_asset/index.html");
        webView.loadUrl(
                Register.getUrlService() + "financeiro/PagSeguro/checkout_android_view"
        );
    }

    @Override
    public void update(Observable observable, Object o) {
        CreditCard creditCard = (CreditCard) observable;

        if( creditCard.getToken() != null ){
            Log.i("Log", "Token: "+creditCard.getToken());

            //CreditCardTO cc = new CreditCardTO();
            ccTO.setCardNumber(creditCard.getCardNumber());
            ccTO.setCodgBrand(creditCard.getCodgBrand());
            ccTO.setCodgSenderHash(creditCard.getCodgSenderHash());
            ccTO.setCvv(creditCard.getCvv());
            ccTO.setMonth(creditCard.getMonth());
            ccTO.setName(creditCard.getName());
            ccTO.setSessionId(creditCard.getSessionId());
            ccTO.setToken(creditCard.getToken());
            ccTO.setYear(creditCard.getYear());

            //Registrar Venda E Pagamento

            /*PagSeguroCO pagseguroCO = InicialActivity.RETROFIT.create(PagSeguroCO.class);
            Call<ResponseBody> call = pagseguroCO.pay(ccTO);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String sResult = response.body().string().toString();
                        sResult += "";
                    } catch (Exception e) { }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CadServicoActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            });*/

            preencherTO();

            servicoTO.setCc(ccTO);
            servicoTO.setCliente(Register.getClienteTO());

            Call<ServicoTO> call2 = servicoCO.registrar(servicoTO);
            call2.enqueue(new Callback<ServicoTO>() {
                @Override
                public void onResponse(Call<ServicoTO> call, Response<ServicoTO> response) {
                    /*try {
                        String s = response.body().string().toString();
                        s += "";
                    } catch (Exception e) {

                    }*/
                    ServicoTO myServico = response.body();
                    if (myServico !=null && myServico.getId() != null) {
                        if (servicoTO.getId() != null) {
                            servicoTO.setId(myServico.getId());
                            //Register.editServico(myServico, Integer.valueOf(index));
                        } else {
                            if (myServico.getCodg_situacao().equals("Cancelado")) {
                                Toast.makeText(getBaseContext(), "Pagamento não autorizado", Toast.LENGTH_LONG).show();
                                disableView(true);
                            } else if (myServico.getCodg_situacao().equals("Indisponibilidade")) {
                                Toast.makeText(getBaseContext(), "Não há profissional disponível no momento. Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                                disableView(true);
                            } else if (myServico.getCodg_situacao().equals("Solicitado")) {
                                //carroTO.setId(myCarro.getId());
                                Register.addServico(myServico);
                                Toast.makeText(getBaseContext(), "Serviço solicitado com sucesso.", Toast.LENGTH_LONG).show();
                                //dialogBox("Serviço solicitado com sucesso.", "Confirmação", false);
                                servicoTOSelecionado = myServico;
                                dialogBox_ConfirmarRegistro();
                                //finish();
                            } else {
                                Toast.makeText(getBaseContext(), myServico.getCodg_situacao() + "Tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                                disableView(true);
                            }
                        }

                    } else {
                        try {
                            mensagemErroPagamento(response.errorBody().string().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ServicoTO> call, Throwable t) {
                    mensagemErroPagamento(t.getMessage());
                }
            });
        }

    }

    private void recusar() {
        EditText etComentario = (EditText) findViewById(R.id.etComentario);
        servicoTO.setDesc_comentario_cliente(etComentario.getText().toString());

        Call<ResponseBody> call2 = servicoCO.recusar_servico(servicoTO);
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = response.body().string().toString();
                    if (s.equals("OK")) {
                        Toast.makeText(getBaseContext(), "Serviço recusado.", Toast.LENGTH_LONG).show();
                        Register.removerServicoProf(servicoTO);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Problema ao recusar serviço, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Problema ao avaliar, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dialogBox_recusarServico() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente Recusar o serviço?")
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                recusar();
                            }
                        }
                )
                .setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        }
                );

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
    }

    public void recusarServico(View view)
    {
        dialogBox_recusarServico();
    }


    private void dialogBox_avaliarCliente() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Confirmar a Finalização do Serviço?")
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finalizarServico();
                            }
                        }
                )
                .setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        }
                );

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //alertDialog.setCanceledOnTouchOutside(false);
        //alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
    }

    private void finalizarServico() {
        EditText etComentario = (EditText) findViewById(R.id.etComentario);
        servicoTO.setDesc_comentario_cliente(etComentario.getText().toString());

        RatingBar ratingBar = (RatingBar) findViewById(R.id.rbNumrStar);
        servicoTO.setNumr_estrelas_cliente( Double.valueOf(ratingBar.getRating()).intValue() );
        if (servicoTO.getNumr_estrelas_cliente() < 1) {
            dialogBox("Deve ser concedida no mínimo uma estrela ao cliente.", "", false);
            return;
        }
        servicoTO.setCodg_situacao("Avaliacao");
        Call<ResponseBody> call2 = servicoCO.avaliar_cliente(servicoTO);
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = response.body().string().toString();
                    if (s.equals("OK")) {
                        Toast.makeText(getBaseContext(), "Avaliação realizada com sucesso.", Toast.LENGTH_LONG).show();
                        Register.removerServicoProf(servicoTO);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);

                        List<ServicoTO> list = Register.getListServicoRealizado();
                        list.add(servicoTO);
                        Register.setListServicoRealizado(list);

                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Problema ao avaliar, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Problema ao avaliar, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void avaliarCliente(View view)
    {
        dialogBox_avaliarCliente();
    }

    public void avaliarProfissional(View view)
    {
        EditText etComentario = (EditText) findViewById(R.id.etComentarioProf);
        servicoTO.setDesc_comentario_profissional(etComentario.getText().toString());

        RatingBar ratingBar = (RatingBar) findViewById(R.id.rbNumrStarProf);
        servicoTO.setNumr_estrelas_profissional( Double.valueOf(ratingBar.getRating()).intValue() );
        if (servicoTO.getNumr_estrelas_profissional() < 1) {
            dialogBox("Deve ser concedida no mínimo uma estrela ao profissional.", "", false);
            return;
        }
        servicoTO.setCodg_situacao("Realizado");
        Call<ResponseBody> call2 = servicoCO.avaliar_profissional(servicoTO);
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String s = response.body().string().toString();
                    if (s.equals("OK")) {
                        Toast.makeText(getBaseContext(), "Avaliação realizada com sucesso.", Toast.LENGTH_LONG).show();
                        Register.removerServico(servicoTO);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Problema ao avaliar, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Problema ao avaliar, tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Register.FLAG_AGUARDANDO_PAGAMENTO = false;

        MainProfissionalActivity.servicoTOSelecionado = null;
        MainActivity.servicoTOSelecionado = null;
        CadServicoActivity.servicoTOSelecionado = null;
    }
}
