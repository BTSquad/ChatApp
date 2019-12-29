package com.oblivion.lightchat.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oblivion.lightchat.R;
import com.oblivion.lightchat.View.MasukActivity;
import com.oblivion.lightchat.View.VerifikasiActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesanFragment extends Fragment {

    private Button logOut;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public PesanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_pesan, container, false);

        logOut = v.findViewById(R.id.logout);
        TextView textView = v.findViewById(R.id.text_test);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), MasukActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);



            }
        });


        return v;

    }

}
