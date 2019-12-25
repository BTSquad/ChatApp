package com.oblivion.lightchat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oblivion.lightchat.R;

import java.security.PrivilegedAction;
import java.util.concurrent.TimeUnit;

public class VerifikasiActivity extends AppCompatActivity {

    private String verifID;
    private Button verifikasi_btn, kirim_ulang_btn;
    private ImageView back_btn;
    private FirebaseAuth mAuth;
    private EditText verifkode_edt;
    String nomor;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        nomor = getIntent().getStringExtra("nomortelp");

        init();
        kirimKodeVerifikasi();


    }

    private void init(){

        verifikasi_btn = findViewById(R.id.btn_verifikasi);
        kirim_ulang_btn = findViewById(R.id.btn_verifUlang);
        back_btn = findViewById(R.id.back);
        verifkode_edt = findViewById(R.id.verfikasi_kode);

        mAuth = FirebaseAuth.getInstance();


        verifikasi_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String kodeUserInput = verifkode_edt.getText().toString().trim();

                if (kodeUserInput.isEmpty()){
                    verifkode_edt.setError("Anda Belum Memasukkan kode");
                }else if (kodeUserInput.length() < 6 ){
                    verifkode_edt.setError("Format Kode Salah");
                }else {
                      verifikasiKode(kodeUserInput);

                }

            }
        });

        kirim_ulang_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              resendVerificationCode(nomor, resendToken);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(VerifikasiActivity.this, MasukActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }
        });

    }

    private void callbackVerif(){

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verifID = s;
                resendToken = forceResendingToken;

                verifikasi_btn.setEnabled(true);
                kirim_ulang_btn.setEnabled(true);

                Toast.makeText(VerifikasiActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signWithCredentialPhone(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(VerifikasiActivity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }else if (e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(VerifikasiActivity.this, "SMS Quota execeded", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(VerifikasiActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                }



            }
        };

    }

    private void kirimKodeVerifikasi(){

        callbackVerif();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                nomor,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }




    private void verifikasiKode(String kode){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifID, kode);
        signWithCredentialPhone(credential);
    }

    private void signWithCredentialPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    verifikasi_btn.setEnabled(false);
                    kirim_ulang_btn.setEnabled(false);

                    FirebaseUser user = task.getResult().getUser();
                    assert user != null;
                    cekUserId(user);


                }else {

                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){

                    }

                    Toast.makeText(VerifikasiActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }  
            }
        });
    }




    private void resendVerificationCode(String nomor, PhoneAuthProvider.ForceResendingToken token) {

        callbackVerif();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                nomor,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private void cekUserId(FirebaseUser user) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query getUser = reference.orderByChild("userId").equalTo(user.getUid());

        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    updateUI(true);

                }else {
                    updateUI(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void updateUI(Boolean dataUser){

        if (dataUser){
            Intent intent = new Intent(VerifikasiActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }else {
            Intent intent = new Intent(VerifikasiActivity.this, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }


    }

}
