package com.rabin.mygallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

public class OnlyCustomerAdapter extends ArrayAdapter<Customer> {
    private static class ViewHolder {
        private TextView tvName;
        private TextView tvtext;
        private ImageView cusIcon;

        ViewHolder(){}
    }
    private final LayoutInflater inflater;

    public OnlyCustomerAdapter(final Context context,
                               final int textViewResourceId,
                               final ArrayList<Customer> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        View itemView = convertView;
        OnlyCustomerAdapter.ViewHolder holder = null;
        final Customer item = getItem(position);
        if (null == itemView ){
            itemView = this.inflater.inflate(R.layout.listitem_only_customers, parent, false);

            holder = new OnlyCustomerAdapter.ViewHolder();

            holder.tvName = (TextView) itemView.findViewById(R.id.nameCus);
            holder.tvtext = (TextView) itemView.findViewById(R.id.toStringCus);
            holder.cusIcon = (ImageView) itemView.findViewById(R.id.cusIcon);
            itemView.setTag(holder);
        }
        else {
            holder = (OnlyCustomerAdapter.ViewHolder) itemView.getTag();
        }
        holder.tvName.setText(item.getNameC());
        holder.tvtext.setText(item.toString());
        holder.cusIcon.setImageResource(R.drawable.kakaha);
        return itemView;
    }
}
