package br.com.inteligenti.lavoutanovov2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.inteligenti.lavoutanovov2.to.ServicoTO;

/**
 * Created by fernando on 11/01/18.
 */

public class ServicoAdapter extends ArrayAdapter<ServicoTO> {

    public ServicoAdapter(Context context, List<ServicoTO> aList) {
        super(context, 0, aList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SimpleDateFormat dfData = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dfHora = new SimpleDateFormat("HH:mm");
        DecimalFormat df = new DecimalFormat( "R$ #,##0.00" );

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_servico, parent, false);
        }

        ServicoTO servicoTO = getItem(position);
        ((TextView) convertView.findViewById(R.id.tv_codg_servico)).setText(servicoTO.getCodg_servico());
        ((TextView) convertView.findViewById(R.id.tv_data_data_servico)).setText(
            dfData.format(servicoTO.getData_data_servico())
        );
        ((TextView) convertView.findViewById(R.id.tv_time_hora_servico)).setText(
            servicoTO.getTime_hora_servico()
        );
        ((TextView) convertView.findViewById(R.id.tv_valr_valor)).setText(
            df.format(servicoTO.getValr_valor())
        );
        ((TextView) convertView.findViewById(R.id.tv_desc_endereco)).setText(servicoTO.getDesc_endereco());

        return convertView;
    }
}
