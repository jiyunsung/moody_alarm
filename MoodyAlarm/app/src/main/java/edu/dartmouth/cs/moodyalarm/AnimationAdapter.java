package edu.dartmouth.cs.moodyalarm;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private ArrayList<SpotifyPlaylist> playlists;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView riv;


        public MyViewHolder(View view) {
            super(view);

            riv = (ImageView) view.findViewById(R.id.horizontal_item_view_image);


        }
    }


    public AnimationAdapter(Context context, ArrayList<SpotifyPlaylist> data) {
        this.context = context;
        this.playlists = data;
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

        SpotifyPlaylist playlist = this.playlists.get(position);

        Picasso.with(this.context).
                load(playlist.getImageUrl()).into(holder.riv);

        holder.riv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d("animation adapter,", "on click");
                Intent intent = new Intent(context, PlaylistDisplay.class);
                intent.putExtra("pos", position+1);
                intent.putExtra("id", "vivjiang");
                context.startActivity(intent);




            }
        });

        // Sets a long click listener for the ImageView using an anonymous listener object that
// implements the OnLongClickListener interface
        holder.riv.setOnLongClickListener(new View.OnLongClickListener() {

            // Defines the one method for the interface, which is called when the View is long-clicked
            public boolean onLongClick(View v) {
                SpotifyPlaylist playlist= playlists.get(position);
                // Create a new ClipData.
                // This is done in two steps to provide clarity. The convenience method
                // ClipData.newPlainText() can create a plain text ClipData in one step.

                // Create a new ClipData.Item from the ImageView object's tag

                Intent intent = new Intent();
                intent.putExtra("playlist", playlist);

                ClipData.Item item = new ClipData.Item(intent);

                // Create a new ClipData using the tag as a label, the plain text MIME type, and
                // the already-created item. This will create a new ClipDescription object within the
                // ClipData, and set its MIME type entry to "text/plain"
                ClipData dragData = new ClipData((CharSequence)v.getTag(),new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},item);

                // Instantiates the drag shadow builder.

                View.DragShadowBuilder myShadow = new MyDragShadowBuilder(holder.riv, playlist.getImageUrl());

                // Starts the drag

                v.startDrag(dragData,  // the data to be dragged
                        myShadow,  // the drag shadow builder
                        v,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );
                v.setVisibility(View.INVISIBLE);
                return true;

            }
        });


    }


    private class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private ImageView shadow;

        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v, String url) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            shadow = (ImageView) v;

            // Creates a draggable image that will fill the Canvas provided by the system.
            Picasso.with(context).
                    load(url).into(shadow);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            // Defines local variables
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth();

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight();

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.getLayoutParams().height= height;
            shadow.getLayoutParams().width= width;



            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            // Draws the ColorDrawable in the Canvas passed in from the system.
            shadow.draw(canvas);
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
