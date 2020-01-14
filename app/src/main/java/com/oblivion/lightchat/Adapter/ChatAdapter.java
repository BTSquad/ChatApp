package com.oblivion.lightchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oblivion.lightchat.Model.Chat_model;
import com.oblivion.lightchat.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat_model> chat_models;


    FirebaseUser mUser;


    public ChatAdapter(Context context, List<Chat_model> chat_models){
        this.context = context;
        this.chat_models =  chat_models;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT){

            View v = LayoutInflater.from(context).inflate(R.layout.list_chat_right, parent, false);
            return new ChatViewHolder(v);
        }else {
            View v = LayoutInflater.from(context).inflate(R.layout.list_chat_left, parent, false);
            return new ChatViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        Chat_model model = chat_models.get(position);

        holder.text_pesan_send.setText(model.getPesan());


    }

    @Override
    public int getItemCount() {
        return chat_models.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView text_pesan_send;
        private View mView;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            text_pesan_send = mView.findViewById(R.id.pesan_text);


        }
    }

    @Override
    public int getItemViewType(int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chat_models.get(position).getSender_uid().equals(mUser.getUid())){

            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }

    }
}
