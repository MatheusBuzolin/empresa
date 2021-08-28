package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Usuario;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    @Override
    protected void onStart() {
        super.onStart();
        abrirTelaHome();
    }

    private void abrirTelaHome(){

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checandoConexao()) {
                    if (verificarUsuarioLogado()){
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();

                    }else{
                        abrirTelaLogin();
                    }

                }else{
                    //abrirTelaHome();
                    abrirTelaLogin();
                    Toast.makeText(SplashActivity.this, "Sem Conexão!", Toast.LENGTH_LONG).show();
                }

            }
        }, 2000);
    }

    public boolean verificarUsuarioLogado(){
        //autenticacao.signOut();
        if (autenticacao.getCurrentUser() != null) {
            Log.i("logggg", " logado");
            return true;
        }else{
            Log.i("logggg", " deslogado");
            return false;
        }
    }

    private void abrirTelaLogin(){
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    public boolean checandoConexao() {
        ConnectivityManager conex = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Network informa = conex.getAllNetworks();  // ConnectivityManager.NetworkCallback informa;
        //NetworkInfo networkInfos = conex.getActiveNetworkInfo();


        Network network = conex.getActiveNetwork();
        NetworkCapabilities capabilities = conex.getNetworkCapabilities(network);
        if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
            Log.i("gggchecandoConexao", "checandoConexao-> CONECTADO WIFI: ");
            return true;
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("gggchecandoConexao", "checandoConexao-> DESCONECTADO : ");
            return false;
        }
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}