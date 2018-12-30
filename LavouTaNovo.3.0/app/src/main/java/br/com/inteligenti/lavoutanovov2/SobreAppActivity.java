package br.com.inteligenti.lavoutanovov2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;

public class SobreAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_app);
        Calendar calendar = Calendar.getInstance();

        TextView tvDireitos = (TextView) findViewById(R.id.tv_direitos_reservados);
        String sDireitos = tvDireitos.getText().toString();
        sDireitos = sDireitos.replace("{ano}", String.valueOf(calendar.get(Calendar.YEAR)));

        tvDireitos.setText(sDireitos);
    }

}
