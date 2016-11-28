package peetme.app.trunimal.com.peetme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by elias on 23/9/2016.
 */

public class MyPetViewHolder extends RecyclerView.ViewHolder {

    View mView;
    Button editBtn;
    Button deleteBtn;

    public MyPetViewHolder(View itemView) {

        super(itemView);
        mView = itemView;

        editBtn = (Button) mView.findViewById(R.id.editBtn);
        deleteBtn = (Button) mView.findViewById(R.id.deleteBtn);

    }

    public void setTitle(String title) {

        TextView animalTitle = (TextView) mView.findViewById(R.id.animalTitle);
        animalTitle.setText(title);

    }

    public void setDesc(String desc) {

        TextView animalDesc = (TextView) mView.findViewById(R.id.animalDesc);
        animalDesc.setText(desc);

    }

    public void setImage(final Context ctx, final String image) {

        final ImageView animalImage = (ImageView) mView.findViewById(R.id.animalImage);
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
