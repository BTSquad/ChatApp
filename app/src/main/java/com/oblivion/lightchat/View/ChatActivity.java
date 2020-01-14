package com.oblivion.lightchat.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oblivion.lightchat.Adapter.ChatAdapter;
import com.oblivion.lightchat.Model.ChatList_model;
import com.oblivion.lightchat.Model.Chat_model;
import com.oblivion.lightchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageView profile_user_chat, back_chat, btn_kirim_chat;
    private EditText pesan_user_edt;
    private TextView view_name_user;
    private String uidReceiver, fragment, namaUser_chat, url_photo;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat_model> chatModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getDataReceiver();
        init();
        readingMessage();

    }

    private void init(){

        profile_user_chat = findViewById(R.id.image_chat);
        back_chat = findViewById(R.id.back_chat);
        view_name_user = findViewById(R.id.username_chat);
        btn_kirim_chat = findViewById(R.id.kirim_chat);
        pesan_user_edt = findViewById(R.id.pesan_user);

        reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recycle_chat);
        LinearLayoutManager layoutManager  = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);



        Picasso.get().load(url_photo).fit().centerCrop().into(profile_user_chat);
        view_name_user.setText(namaUser_chat);

        btn_kirim_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pesan = pesan_user_edt.getText().toString().trim();

                if (pesan.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Pesan masih kosong", Toast.LENGTH_SHORT).show();
                }else {
                    checkChatList();
                }


            }
        });


        back_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment.equals("kontak")){

                    Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                    intent.putExtra("position", 1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });



    }

    private void getDataReceiver(){
        uidReceiver = getIntent().getStringExtra("uidReceiver");
        fragment = getIntent().getStringExtra("fragment");
        namaUser_chat = getIntent().getStringExtra("namauser");
        url_photo = getIntent().getStringExtra("urlPhoto");
    }




    private void checkChatList(){

        final String senderUid = mAuth.getUid();
        Query check = reference.child("chatlist").child(senderUid).orderByChild("receiverUid").equalTo(uidReceiver);

        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){

                    pushChatlist();
                }else {

                     for (DataSnapshot ds : dataSnapshot.getChildren()){

                         ChatList_model model = ds.getValue(ChatList_model.class);

                         pushChat(model.getIdChat(), senderUid);

                     }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void pushChatlist(){

        DatabaseReference pushChatList = reference.child("chatlist");
        final String senderUid = mAuth.getUid();
        final String idChat = pushChatList.push().getKey();

        ChatList_model model = new ChatList_model(uidReceiver, idChat);

        pushChatList.child(senderUid).child(idChat).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pushChat(idChat, senderUid);
            }
        });



    }

    private void pushChat(String idChat, String senderUid) {

        DatabaseReference pushMessage = reference.child("message").child(idChat);

        String idMessage = pushMessage.push().getKey();

        Chat_model chat_model = new Chat_model(senderUid, uidReceiver, pesan_user_edt.getText().toString().trim(), idMessage);

        pushMessage.child(idMessage).setValue(chat_model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                pesan_user_edt.setText("");

            }
        });

        readingMessage();

    }

    private void readingMessage(){

        Query getListChat = reference.child("chatlist").child(mUser.getUid()).orderByChild("receiverUid").equalTo(uidReceiver);
        getListChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                        ChatList_model chatListModel = ds.getValue(ChatList_model.class);

                        getMessage(chatListModel.getIdChat());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getMessage(String idUpload){

        chatModels = new ArrayList<>();
        DatabaseReference ref = reference.child("message").child(idUpload);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Chat_model chat_model = ds.getValue(Chat_model.class);
                    chatModels.add(chat_model);
                }
                chatAdapter = new ChatAdapter(ChatActivity.this, chatModels);
                recyclerView.setAdapter(chatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
