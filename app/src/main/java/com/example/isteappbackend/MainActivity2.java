package com.example.isteappbackend;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.isteappbackend.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.isteappbackend.databinding.ActivityMain2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity2 extends AppCompatActivity {

    MutableLiveData<Boolean> isAuth= new MutableLiveData<>();;
    static MutableLiveData<Boolean> doneLoading= new MutableLiveData<>();
    public static Boolean authenticated;
    private ActivityMain2Binding binding;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    BottomNavigationView bottomNavigationView;
    static Boolean onLoading, onLogin;
    Observer<Boolean> authObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isAuth.setValue(false);
        doneLoading.setValue(false);
        onLogin=false;
        authObserver= new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && doneLoading.getValue() && onLoading){
                    bottomNavigationView.setVisibility(VISIBLE);
                    LoadingFrag.toHome();
                }else if(!aBoolean && doneLoading.getValue() && onLoading){
                    //TODO:Make the transitions smoother
                    LoadingFrag.toLogin();
                }
                else if(aBoolean && onLogin){
                    bottomNavigationView.setVisibility(VISIBLE);
                    LoginFragment.toHome();
                }
            }
        };
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                isAuth.observe(MainActivity2.this,authObserver);
            }
        }, 1050);
        FragmentContainerView nav=findViewById(R.id.nav_host_fragment_activity_main2);
         bottomNavigationView = findViewById(R.id.nav_view);
        firebaseAuth=FirebaseAuth.getInstance();
        FragmentManager fragmentManager=getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main2, LoadingFrag.class,null)
//                .addToBackStack("loading")
//                .commit();
        NavController navController = Navigation.findNavController(MainActivity2.this, R.id.nav_host_fragment_activity_main2);

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    authenticated=true;
                    isAuth.setValue(true);

                    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                            .build();
//                    NavController navController = Navigation.findNavController(MainActivity2.this, R.id.nav_host_fragment_activity_main2);

                    NavigationUI.setupWithNavController(binding.navView, navController);
                }
                else{
                    authenticated=false;
                    Log.i("mine","not logged in");
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
        bottomNavigationView.setVisibility(GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}