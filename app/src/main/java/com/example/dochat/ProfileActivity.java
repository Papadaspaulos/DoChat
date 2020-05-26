package com.example.dochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView mProfileTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        firebaseAuth = FirebaseAuth.getInstance();
        mProfileTv= findViewById(R.id.profileTv);



    }
    private  void checkUserStautus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if (user !=null){
            //usuario logeado
            //sacar el email logeado
            mProfileTv.setText(user.getEmail());

        }else{
            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStautus();
        super.onStart();
    }

    //inlate option menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //getm item id
        int id= item.getItemId();
        if ( id==R.id.logout){
            firebaseAuth.signOut();
            checkUserStautus();
        }
        return super.onOptionsItemSelected(item);
    }
}
