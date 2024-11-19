package com.rabin.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.transition.Hold;

import java.util.ArrayList;


public class CustomerAdapter extends ArrayAdapter<Order> implements Filterable {
    private static class ViewHolder {
        private TextView tvtext;
        private LottieAnimationView ivdone;

        ViewHolder(){}
    }
    private final LayoutInflater inflater;

    public CustomerAdapter(final Context context, final int textViewResourceId, final ArrayList<Order> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        View itemView = convertView;
        ViewHolder holder = null;
        final Order item = getItem(position);
        if (null == itemView ){
            itemView = this.inflater.inflate(R.layout.listitem_customer, parent, false);

            holder = new ViewHolder();

            holder.tvtext = (TextView) itemView.findViewById(R.id.idfield);
            holder.ivdone = (LottieAnimationView) itemView.findViewById(R.id.ivcomplete);
            itemView.setTag(holder);
        }
        else {
            holder = (ViewHolder) itemView.getTag();
        }
        holder.tvtext.setText(item.toString());
        if (item.getComplete().equals("Yes")){
            holder.ivdone.setAnimation(R.raw.checked_done);
            turnOn(holder.ivdone);
        }
        else{
            holder.ivdone.setAnimation(R.raw.checked_notdone);
            turnOn(holder.ivdone);
        }
        return itemView;
    }
    private void turnOn(LottieAnimationView checkbox) {
        checkbox.setMinAndMaxProgress(0.0f,0.8f);
        checkbox.setSpeed(1);
        checkbox.playAnimation();
    }

}

