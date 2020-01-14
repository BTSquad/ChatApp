package com.oblivion.lightchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oblivion.lightchat.Model.ChatList_model;
import com.oblivion.lightchat.Model.Users_model;
import com.oblivion.lightchat.R;
import com.oblivion.lightchat.View.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PesanAdapter extends RecyclerView.Adapter<PesanAdapter.PesanViewHolder> {


    int userRule;
    Context context;
    List<ChatList_model> chatList_modelList;

    public PesanAdapter(Context context, List<ChatList_model> chatList_modelList) {

        this.context = context;
        this.chatList_modelList = chatList_modelList;

    }

    @NonNull
    @Override
    public PesanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.list_pesan, parent, false);
        return new PesanViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final PesanViewHolder holder, int position) {
        final ChatList_model model = chatList_modelList.get(position);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        Query getUser = reference.orderByChild("userId").equalTo(model.getReceiverUid());

        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        Users_model users_model = ds.getValue(Users_model.class);

                        Picasso.get()
                                .load(users_model.getPhotoUrl())
                                .fit()
                                .centerCrop()
                                .into(holder.imageViewReceiver);

                        holder.namaReceiver.setText(users_model.getNamaUser());
                        holder.pesanAkhir.setText("Hello");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.layoutKlik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChatActivity.class);
                context.startActivity(intent);


            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList_modelList.size();
    }

    public class PesanViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewReceiver;
        private TextView namaReceiver, pesanAkhir;
        private LinearLayout layoutKlik;

        private View mView;

        private PesanViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            imageViewReceiver = mView.findViewById(R.id.image_receiver);
            namaReceiver = mView.findViewById(R.id.nama_receiver);
            pesanAkhir = mView.findViewById(R.id.last_message);
            layoutKlik = mView.findViewById(R.id.klik_pesan);


        }
    }
}
