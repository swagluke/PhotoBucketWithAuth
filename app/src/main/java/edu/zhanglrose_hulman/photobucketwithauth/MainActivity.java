package edu.zhanglrose_hulman.photobucketwithauth;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, PhotoListFragment.Callback {

    //private static final int RC_SIGN_IN = 1;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    OnCompleteListener mOnCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        initializeListeners();
    }

    private void initializeListeners() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Log.d(Constants.TAG, "User: " + user);
                //Log.d(Constants.TAG, "User: " + user.getUid());
                if (user != null) {
                    switchToPhotoListFragment(user.getUid());
                } else {
                    switchToLoginFragment();
                }
            }
        };
        mOnCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    showLoginError("Login failed");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onLogin(String email, String password) {
        //DONE: Log user in with username & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mOnCompleteListener);
    }

    @Override
    public void log_out() {
        switchToLoginFragment();
    }


    // MARK: Provided Helper Methods
    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, new LoginFragment(), "Login");
        ft.commit();
    }

    private void switchToPhotoListFragment(String uid) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment photolistFragment = PhotoListFragment.newInstance(uid);
        ft.replace(R.id.fragment, photolistFragment, "PhotoLists");
        ft.commit();
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    @Override
    public void onDisplay(Photo weatherpic) {
        showImageFromUrl(weatherpic);
    }

    private void showImageFromUrl(Photo photo) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PhotoDetailFragment photodetailFragment = PhotoDetailFragment.newInstance(photo);
        ft.replace(R.id.fragment, photodetailFragment, "PhotoDetail");
        ft.addToBackStack("PhotoDetail");
        ft.commit();
    }
}
