package com.oblivion.lightchat.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oblivion.lightchat.Adapter.KontakAdapter;
import com.oblivion.lightchat.Model.Kontak_model;
import com.oblivion.lightchat.Model.Users_model;
import com.oblivion.lightchat.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class KontakFragment extends Fragment {

    private int REQUEST_CODE_CONT = 1111;

    private RecyclerView recyclerView;
    private KontakAdapter kontakAdapter;
    private List<Users_model> list;
    private DatabaseReference reference;
    private long data;



    public KontakFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kontak, container, false);

        recyclerView = v.findViewById(R.id.recycle_cont);
        reference = FirebaseDatabase.getInstance().getReference("Users");

        LinearLayoutManager layoutManager  = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        reqPermissionCont();

        FloatingActionButton fab = v.findViewById(R.id.refresh_cont);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected(getContext())){
                    getContact();
                }else {
                    buildDialog(getActivity()).show();
                }

            }
        });
        return v;
    }

    private void getContact(){
        kontakAdapter = new KontakAdapter(getContext(), getContacts());
        recyclerView.setAdapter(kontakAdapter);
    }

    private void reqPermissionCont(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED){
          requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_CODE_CONT);

        }else {
            loadData();
        }

    }

    private List<Users_model> getContacts(){

         list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
      //  "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC"
        cursor.moveToFirst();


        while (cursor.moveToNext()){

            String nomorKontak = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-, ]", "");

            reference.orderByChild("noTelp").equalTo(nomorKontak).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()){

                         for (DataSnapshot ds : dataSnapshot.getChildren()){

                             Users_model model = ds.getValue(Users_model.class);

//                             Kontak_model kontak_model = new Kontak_model(model.getNamaUser(), model.getNoTelp());
                             list.add(model);
                             saveData();
                             recyclerView.setAdapter(kontakAdapter);

                         }
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });

        }

        return list;
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("saveDataPhone", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("task",json);
        editor.apply();
    }

    private void loadData(){

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("saveDataPhone", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task", null);
        Type type = new TypeToken<List<Users_model>>(){}.getType();
        list = gson.fromJson(json, type);

        if (json != null){

            kontakAdapter = new KontakAdapter(getContext(), list);
            recyclerView.setAdapter(kontakAdapter);

        }
        else {

            getContact();

        }

    }


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Anda Sedang Offline");
        builder.setMessage("Tidak ada jaringan yang terhubung, Anda tidak bisa melihat postingan terbaru");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_CONT){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                loadData();

            }else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
