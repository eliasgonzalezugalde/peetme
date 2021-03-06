package peetme.app.trunimal.com.peetme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by elias on 23/9/2016.
 */

public class PetViewHolder extends RecyclerView.ViewHolder {

      View mView;

    public PetViewHolder(View itemView) {

        super(itemView);
        mView = itemView;

    }

    public void setTitle(String title) {

        TextView animalTitle = (TextView)mView.findViewById(R.id.animalTitle);
        animalTitle.setText(title);

    }

    public void setDesc(String desc) {

        TextView animalDesc = (TextView)mView.findViewById(R.id.animalDesc);
        animalDesc.setText(desc);

    }

    public void setImage(final Context ctx, final String image) {

        final ImageView animalImage = (ImageView)mView.findViewById(R.id.animalImage);
        //
        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(animalImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).into(animalImage);
            }
        });

    }

}
