package br.com.inteligenti.lavoutanovov2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.inteligenti.lavoutanovov2.to.CarroTO;

/**
 * Created by fernando on 11/01/18.
 */

public class CarroAdapter extends ArrayAdapter<CarroTO> {

    public CarroAdapter(Context context, List<CarroTO> aList) {
        super(context, 0, aList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.carro, parent, false);
        }

        CarroTO carroTO = getItem(position);
        ((TextView) convertView.findViewById(R.id.tvDescPlaca)).setText(carroTO.getCodg_placa());
        TextView tvDescMarcaModelo = (TextView) convertView.findViewById(R.id.tvDescMarcaModelo);

        tvDescMarcaModelo.setText(carroTO.getDesc_marca() + " " + carroTO.getDesc_modelo());
        return convertView;
    }
}
