package com.oblivion.lightchat.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oblivion.lightchat.Model.Users_model;
import com.oblivion.lightchat.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private int REQUEST_CODE_GALERY = 1111;

    private ImageView imageUser;
    private EditText userName_edt;
    private ProgressBar progressBar;
    private Uri fileUri;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        reference = FirebaseDatabase.getInstance().getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null){

            fileUri = data.getData();
            try {

                InputStream is = getContentResolver().openInputStream(fileUri);

                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageUser.setImageBitmap(bitmap);

            }catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    private void simpanDataUser() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("UsersPhoto");
        String namaFile = getFileName(fileUri);
        final String namaUser = userName_edt.getText().toString().trim();

        try {

            InputStream is = getContentResolver().openInputStream(fileUri);

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] final_image = baos.toByteArray();

            final StorageReference fileToupload = storageReference.child("UsersPhoto").child(namaFile);
            UploadTask uploadTask = fileToupload.putBytes(final_image);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileToupload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            final String url = String.valueOf(uri);

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(namaUser)
                                    .setPhotoUri(Uri.parse(url))
                                    .build();

                            mUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Users_model users_model = new Users_model(
                                                        mUser.getUid(),
                                                        userName_edt.getText().toString().trim(),
                                                        url, mUser.getPhoneNumber());

                                                reference.child(mUser.getUid()).setValue(users_model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        updateUI();
                                                    }
                                                });

                                            }else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(ProfileActivity.this, "Photo Gagal Diubah", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileActivity.this, "Gagal mengupload", Toast.LENGTH_SHORT).show();
                }
            });


        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private void init(){
        progressBar = findViewById(R.id.progressBar_profile);
        progressBar.setVisibility(View.INVISIBLE);

        ImageView back_btn = findViewById(R.id.back);
        ImageView pickPhotoGaleri= findViewById(R.id.edit_PhotoProfile);
        Button simpanUpdateUser = findViewById(R.id.simpan_user);
        Button lewati_btn = findViewById(R.id.lewati_simpanUser);
        imageUser = findViewById(R.id.image_profile_user);
        userName_edt = findViewById(R.id.namaUser_setting);

        userName_edt.setText(mUser.getPhoneNumber());

        lewati_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileUri = Uri.parse("android.resource://com.oblivion.lightchat/mipmap/userdefault");
                try {

                    InputStream is = getContentResolver().openInputStream(fileUri);

//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    imageUser.setImageBitmap(bitmap);

                }catch (Exception e){
                    e.printStackTrace();

                }
                progressBar.setVisibility(View.VISIBLE);
                simpanDataUser();

            }
        });

        pickPhotoGaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"pilih gambar"), REQUEST_CODE_GALERY);

            }
        });

        simpanUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fileUri == null){
                    Toast.makeText(ProfileActivity.this, "Anda belum memilih photo", Toast.LENGTH_LONG).show();
                }else if (userName_edt == null){
                    Toast.makeText(ProfileActivity.this, "Nama masih kosong", Toast.LENGTH_LONG).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    simpanDataUser();
                }

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileActivity.this, MasukActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    private String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri , null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void updateUI(){

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}


