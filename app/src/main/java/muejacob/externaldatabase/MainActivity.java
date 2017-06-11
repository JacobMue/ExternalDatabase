package muejacob.externaldatabase;

import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG="Firebase Errors";

    EditText edtEmail,edtPassword;
    Button btnMain,btnLogout;
    TextView txtResult,txtAccount;
    CardView cdContainer;

    private boolean checkAccount=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        txtResult=(TextView)findViewById(R.id.txtResult);
        txtAccount= (TextView) findViewById(R.id.txtAccount);
        cdContainer=(CardView) findViewById(R.id.cdContainer);
        btnMain=(Button)findViewById(R.id.btnClick);
        btnLogout=(Button)findViewById(R.id.btnLogout);
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheckAccount()) {
                    createAccount(edtEmail.getText().toString(), edtPassword.getText().toString());
                }else{
                    signIn(edtEmail.getText().toString(), edtPassword.getText().toString());
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    logoutLayout();
                    getCurrentUser();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    loginLayout();
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        txtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCheckAccount()) {
                    loginLayout();
                }else{
                    registerLayout();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                loginLayout();
            }
        });


    }

    private void loginLayout(){
        txtAccount.setText("Not yet a member? Sign up.");
        btnLogout.setVisibility(View.GONE);
        txtResult.setVisibility(View.GONE);
        cdContainer.setVisibility(View.VISIBLE);
        setCheckAccount(false);
        btnMain.setText("LOGIN");
        edtEmail.setText("");
        edtPassword.setText("");
    }

    private void registerLayout(){
        txtAccount.setText("Already a member? Sign in.");
        btnLogout.setVisibility(View.GONE);
        txtResult.setVisibility(View.GONE);
        cdContainer.setVisibility(View.VISIBLE);
        setCheckAccount(true);
        btnMain.setText("SIGN UP");
        edtEmail.setText("");
        edtPassword.setText("");
    }

    private void logoutLayout(){
        txtAccount.setText("");
        btnLogout.setVisibility(View.VISIBLE);
        txtResult.setVisibility(View.VISIBLE);
        cdContainer.setVisibility(View.GONE);
        edtEmail.setText("");
        edtPassword.setText("");
    }

    private void createAccount(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        loginLayout();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Failed to sign up.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void signIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Failed to sign in.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void getCurrentUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            txtResult.setText("Logged in as:\n\nEmail Address : "+user.getEmail()+"\n\nUser ID : "+user.getUid());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean isCheckAccount() {
        return checkAccount;
    }

    public void setCheckAccount(boolean checkAccount) {
        this.checkAccount = checkAccount;
    }
}
