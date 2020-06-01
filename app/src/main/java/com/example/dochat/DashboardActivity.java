package com.example.dochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


         actionBar = getSupportActionBar();
         actionBar.setTitle("Perfil");
        firebaseAuth = FirebaseAuth.getInstance();
        //mProfileTv= findViewById(R.id.profileTv);
        //Botton navigation view
        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);




    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //itemclick
                    switch (menuItem.getItemId()){
                        case R.id.nav_paciente:
                            AddFragment fragment1 = new AddFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1,"");
                            ft1.commit();

                            return true;
                        case R.id.nav_perfil:

                            ProfileFragment fragment2 = new ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2,"");
                            ft2.commit();

                            return true;
                        case R.id.nav_usuarios:

                            UsersFragment fragment3 = new UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3,"");
                            ft3.commit();

                            return true;

                        case R.id.nav_chat:

                            ChatFragment fragment4 = new ChatFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4,"");
                            ft4.commit();

                            return true;

                    }
                    return false;
                }
            };

    private  void checkUserStautus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if (user !=null){
            //usuario logeado
            //sacar el email logeado
            //mProfileTv.setText(user.getEmail());

        }else{
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
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
