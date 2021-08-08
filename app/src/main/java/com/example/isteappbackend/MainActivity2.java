package com.example.isteappbackend;

import android.app.Activity;
import android.os.Bundle;
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
    static MutableLiveData<Boolean> onLogin= new MutableLiveData<>();;
    public static Boolean authenticated;
    private ActivityMain2Binding binding;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    BottomNavigationView bottomNavigationView;
    static Boolean onLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isAuth.setValue(false);
        Observer<Boolean> authObserver= new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && onLogin.getValue()){
                    Log.i("mine","Ready to shift to home");
                    LoginFragment.toHome();
                }
            }
        };
        isAuth.observe(this,authObserver);
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
                    bottomNavigationView.setVisibility(VISIBLE);
                    authenticated=true;
                    isAuth.setValue(true);

                    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                            .build();

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