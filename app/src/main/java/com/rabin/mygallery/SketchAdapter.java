package com.rabin.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SketchAdapter extends ArrayAdapter<Sketch> {

    private static class ViewHolder{
        private TextView tvToString;
        private TextView tvName;
        private ImageView ivSketch;

        ViewHolder(){}
    }

    private final LayoutInflater inflater;

    public SketchAdapter(final Context context, final int textViewResourceId, final ArrayList<Sketch> objects){
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        View itemView = convertView;
        ViewHolder holder = null;
        final Sketch item = getItem(position);
        if (null == itemView){
            itemView = this.inflater.inflate(R.layout.listitem_sketch, parent, false);
            holder = new ViewHolder();

            holder.tvToString = (TextView) itemView.findViewById(R.id.toStringFieldS);
            holder.tvName = (TextView) itemView.findViewById(R.id.name_field_S);
            holder.ivSketch = (ImageView) itemView.findViewById(R.id.ivSketch_S);

            itemView.setTag(holder);
        }
        else {
            holder = (SketchAdapter.ViewHolder) itemView.getTag();
        }

        holder.tvName.setText(item.getNameS());
        holder.tvToString.setText(item.toString());

        String myPic = item.getSketchPic();
        if (! myPic.equals(""))
            holder.ivSketch.setImageBitmap(StringToBitMap(myPic));
        else
            holder.ivSketch.setImageResource(R.drawable.paint);
        return itemView;
    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte = android.util.Base64.decode(encodedString, android.util.Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

}
