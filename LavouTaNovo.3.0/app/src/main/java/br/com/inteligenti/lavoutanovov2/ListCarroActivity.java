package br.com.inteligenti.lavoutanovov2;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.CarroTO;

public class ListCarroActivity extends AppCompatActivity {

    ListView lvLista;
    List<CarroTO> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_carro);

        list = Register.getListCarro();

        lvLista = (ListView) findViewById(R.id.lvLista);
        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //CarroTO item = list.get(i);
                Intent intent = new Intent(ListCarroActivity.this, CadCarroActivity.class);
                intent.putExtra("index_item", String.valueOf(i));
                startActivity(intent);
            }
        });

        CarroAdapter adapter = new CarroAdapter(this, list);
        lvLista.setAdapter(adapter);
    }

    public void adicionar(View view)
    {
        startActivity(new Intent(ListCarroActivity.this, CadCarroActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            list.clear();
            lvLista.setAdapter(null);
            list = Register.getListCarro();
            CarroAdapter adapter = new CarroAdapter(this, list);
            lvLista.setAdapter(adapter);

        }
    }
}
