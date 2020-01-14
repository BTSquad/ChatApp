package com.oblivion.lightchat.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oblivion.lightchat.Adapter.PesanAdapter;
import com.oblivion.lightchat.Model.ChatList_model;
import com.oblivion.lightchat.R;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesanFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private PesanAdapter pesanAdapter;
    private List<ChatList_model> listModels;

    public PesanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_pesan, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        recyclerView = v.findViewById(R.id.recycle_chat_list);
        LinearLayoutManager layoutManager  = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        getChatList();
        return v;
    }


    private void getChatList(){

        listModels = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chatlist");

        reference.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data : dataSnapshot.getChildren()){

                        ChatList_model model = data.getValue(ChatList_model.class);
                        listModels.add(model);
                    }

                    pesanAdapter = new PesanAdapter(getContext(), listModels);
                    recyclerView.setAdapter(pesanAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
