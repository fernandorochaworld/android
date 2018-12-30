package br.com.inteligenti.lavoutanovov2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.inteligenti.lavoutanovov2.service.Register;
import br.com.inteligenti.lavoutanovov2.to.ServicoTO;

public class ListServicoActivity extends AppCompatActivity {

    ListView lvLista;
    List<ServicoTO> list;
    String tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_servico);
        if (Register.isProfissional()) {

            Intent intent = getIntent();
            tipo = intent.getStringExtra("tipo");
            if (tipo==null || !tipo.equals("REALIZADO")) {
                list = Register.getListServicoProf();
            } else {
                list = Register.getListServicoRealizado();
            }
        } else {
            list = Register.getListServico();
        }

        lvLista = (ListView) findViewById(R.id.lvLista);

        ServicoAdapter adapter = new ServicoAdapter(this, list);
        lvLista.setAdapter(adapter);


        lvLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //ServicoTO item = list.get(i);
                Intent intent = new Intent(ListServicoActivity.this, CadServicoActivity.class);
                intent.putExtra("index_item", String.valueOf(i));
                intent.putExtra("tipo", tipo); // Tipo listagem do Profissional
                startActivity(intent);
            }
        });
    }

    public void adicionar(View view)
    {
        startActivity(new Intent(ListServicoActivity.this, CadServicoActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            list.clear();
            lvLista.setAdapter(null);
            list = Register.getListServico();
            ServicoAdapter adapter = new ServicoAdapter(this, list);
            lvLista.setAdapter(adapter);

        }
    }
}
