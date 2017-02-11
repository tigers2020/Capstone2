package com.androidnerdcolony.idlefactory.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnerdcolony.idlefactory.R;
import com.androidnerdcolony.idlefactory.datalayout.FactoryLine;
import com.androidnerdcolony.idlefactory.firebase.FirebaseUtil;
import com.androidnerdcolony.idlefactory.module.ConvertNumber;
import com.androidnerdcolony.idlefactory.module.DefaultDatabase;
import com.androidnerdcolony.idlefactory.sync.FactoryWork;
import com.androidnerdcolony.idlefactory.ui.adapters.FactoryLineAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CompanyFrontActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SING_IN = 9001;

    public ProgressDialog mProgressDialog;
    @BindView(R.id.factory_list)
    ListView factoryListView;
    @BindView(R.id.balance)
    TextView balanceView;
    @BindView(R.id.idle_cash)
    TextView idleCashView;


    GoogleApiClient mGoogleApiClient;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseDatabase mDatabase;
    DatabaseReference mUserDataRef;
    Context context;

    FactoryLineAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_front);
        ButterKnife.bind(this);
        this.context = this;

        checkGoogleLogin();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SING_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                Timber.d(String.format("google Login Success with: %s", account.getDisplayName()));
                firebaseAuthWithGoogle(account);
            } else {
                updateUI();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(CompanyFrontActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }


        });

    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void checkGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    mUserDataRef = mDatabase.getReference(mUser.getUid());
                } else {
                    signIn();
                }
                updateUI();
            }

        };

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SING_IN);

    }

    private void checkNewPlayer() {
        mUserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DefaultDatabase.SetNewDatabase(context, mUserDataRef);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void updateUI() {
        if (mUser != null) {
            checkNewPlayer();
            DatabaseReference factoryRef = FirebaseUtil.getFactory(context);
            DatabaseReference userStateRef = FirebaseUtil.getUserState(context);
            mAdapter = new FactoryLineAdapter(this, FactoryLine.class, factoryRef);

            factoryListView.setAdapter(mAdapter);

            factoryRef.addChildEventListener(new ChildEventListener() {
                Map<String, Double> idleCash = new TreeMap<>();

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Timber.d("child: " + dataSnapshot.toString());
                    if (dataSnapshot.exists()) {
                        double cash = dataSnapshot.child(getString(R.string.db_idle_cash)).getValue(Double.class);
                        boolean isOpen = dataSnapshot.child(getString(R.string.db_open)).getValue(Boolean.class);
                        if (isOpen) {
                            idleCash.put(dataSnapshot.getKey(), cash);
                            setIdleChashViewText(idleCash);
                        }
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    idleCash.put(dataSnapshot.getKey(), dataSnapshot.child("idleCash").getValue(Double.class));
                    if (idleCash != null) {
                        setIdleChashViewText(idleCash);
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            userStateRef.child("balance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Timber.d("balance Listener" + dataSnapshot.toString());
                    double balance;
                    if (dataSnapshot.exists()) {
                        balance = dataSnapshot.getValue(Double.class);
                        String balanceString = ConvertNumber.numberToString(balance);
                        balanceView.setText(balanceString);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FactoryWork.initialize(context);

        }
    }

    private void setIdleChashViewText(Map<String, Double> idleCashes) {
        double cash = 0;
        for (int i = 0; i < idleCashes.size(); i++) {
            if (idleCashes.containsKey(String.valueOf(i))) {
                cash += idleCashes.get(String.valueOf(i));
            }
        }
        String CashString = ConvertNumber.numberToString(cash);
        idleCashView.setText(CashString);
    }

}
