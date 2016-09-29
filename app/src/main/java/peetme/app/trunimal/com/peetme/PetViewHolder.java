package peetme.app.trunimal.com.peetme;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by elias on 23/9/2016.
 */

public class PetViewHolder extends RecyclerView.ViewHolder {

    TextView txtPet;
    TextView txtCategory;

    public PetViewHolder(View itemView) {
        super(itemView);
        txtPet = (TextView)itemView.findViewById(R.id.messageTxt);
        txtCategory = (TextView)itemView.findViewById(R.id.messageTxt);
    }

    //revisar estos campos

    public void setName(String name) {
        TextView field = (TextView) itemView.findViewById(android.R.id.text1);
        field.setText(name);
    }

    public void setText(String text) {
        TextView field = (TextView) itemView.findViewById(android.R.id.text2);
        field.setText(text);
    }
}
