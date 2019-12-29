package com.oblivion.lightchat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.oblivion.lightchat.R;

import java.lang.reflect.InvocationTargetException;

public class MasukActivity extends AppCompatActivity {

    private CountryCodePicker cpp;
    private EditText nomorTelp_edt;
    private Button lanjut_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);

        init();

    }


    private void init(){

        ImageView back_btn = findViewById(R.id.back);
        cpp = findViewById(R.id.countryPicker);
        nomorTelp_edt = findViewById(R.id.no_telp);
        lanjut_btn = findViewById(R.id.lanjutkan_nomor);

        lanjut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNumber();
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void getNumber(){
        String num = nomorTelp_edt.getText().toString().trim();

        if (num.equals("")){
            nomorTelp_edt.setError("Nomor tidak boleh kosong");
        }
        else {
            String fullnum ="+" + cpp.getFullNumber() + num;

            if (fullnum.length() < 10){
                nomorTelp_edt.setError("Nomor Tidak Valid");
            }else {

                updateUI(fullnum);

            }

        }
    }

    private void updateUI(String fullnum){

        Intent intent = new Intent(this, VerifikasiActivity.class);
        intent.putExtra("nomortelp", fullnum);
        startActivity(intent);


    }


}
