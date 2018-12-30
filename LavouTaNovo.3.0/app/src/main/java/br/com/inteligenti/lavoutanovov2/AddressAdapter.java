package br.com.inteligenti.lavoutanovov2;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fernando on 11/01/18.
 */

public class AddressAdapter extends ArrayAdapter<Address> {

    public AddressAdapter(Context context, List<Address> aList) {
        super(context, 0, aList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.address, parent, false);
        }

        Address address = getItem(position);
        TextView tvDescLocal = (TextView) convertView.findViewById(R.id.tvDescLocal);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append("\n");
        }
        sb.append(address.getFeatureName()).append("\n");
        sb.append(address.getLocality()).append("\n");
        sb.append(address.getPostalCode()).append(" ");
        sb.append(address.getCountryName());
        String endereco = sb.toString();


        tvDescLocal.setText(endereco);

        return convertView;
    }
}
