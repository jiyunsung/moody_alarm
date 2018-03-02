package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vivianjiang on 3/1/18.
 */

public class AnimationAdapter extends RecyclerView.Adapter<AnimationAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> urls;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView riv;

        public MyViewHolder(View view) {
            super(view);

            riv = (ImageView) view.findViewById(R.id.horizontal_item_view_image);

        }
    }


    public AnimationAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.urls = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.animation_item, parent, false);

        if (itemView.getLayoutParams ().width == RecyclerView.LayoutParams.MATCH_PARENT)
            itemView.getLayoutParams ().width = RecyclerView.LayoutParams.WRAP_CONTENT;

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Picasso.with(this.context).
                load(urls.get(position)).into(holder.riv);


    }


    @Override
    public int getItemCount() {
        return urls.size();
    }
}
