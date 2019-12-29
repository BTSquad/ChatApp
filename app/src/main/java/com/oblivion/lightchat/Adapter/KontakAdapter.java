package com.oblivion.lightchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oblivion.lightchat.Model.Users_model;
import com.oblivion.lightchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KontakAdapter extends RecyclerView.Adapter<KontakAdapter.Viewholder> {

    private Context context;
    private List<Users_model> usersModels;

    public KontakAdapter(Context context, List<Users_model> usersModels){
        this.context = context;
        this.usersModels = usersModels;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.list_kontak, parent, false);
        return new Viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Users_model model = usersModels.get(position);

        holder.nama_kontak.setText(model.getNamaUser());
        holder.num_kontak.setText(model.getNoTelp());
        Picasso.get().load(model.getPhotoUrl()).fit().centerCrop().into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return usersModels.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView nama_kontak, num_kontak;
        private ImageView imageView;

        private View mView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            nama_kontak = mView.findViewById(R.id.nama_kontak);
            num_kontak = mView.findViewById(R.id.nomor_kontak);
            imageView = mView.findViewById(R.id.kontak_image);


        }
    }
}
