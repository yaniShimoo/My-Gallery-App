package com.rabin.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Base64;

public class PaintingAdapter extends ArrayAdapter<Painting> {

    private static class ViewHolder {
        private TextView tvText;
        private TextView tvName;
        private ImageView ivPainting;

        ViewHolder(){}
    }
    private final LayoutInflater inflater;

    public PaintingAdapter(final Context context,
                           final int textViewResourceId,
                           final ArrayList<Painting> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent){
        View itemView = convertView;
        ViewHolder holder = null;
        final Painting item = getItem(position);
        if (null == itemView){
            itemView = this.inflater.inflate(R.layout.listitem_painting, parent, false);

            holder = new ViewHolder();

            holder.tvName = (TextView) itemView.findViewById(R.id.name_field_P);
            holder.tvText = (TextView) itemView.findViewById(R.id.toStringFieldP);
            holder.ivPainting= (ImageView) itemView.findViewById(R.id.ivpainting_p);
            itemView.setTag(holder);
        }
        else {
            holder = (PaintingAdapter.ViewHolder) itemView.getTag();
        }
        holder.tvName.setText(item.getNameP());
        holder.tvText.setText(item.toString());
        String myPic = item.getPaintingPic();
        if (! myPic.equals("")){
            holder.ivPainting.setImageBitmap(StringToBitMap(myPic));
        }
        else {
            holder.ivPainting.setImageResource(R.drawable.paint);
        }
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
