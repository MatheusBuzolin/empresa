package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mbappsoftware.aprot.R;

public class EsqueceuSenhaActivity extends AppCompatActivity {

    private EditText txRecuEmial;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueceu_senha);

        firebaseAuth = FirebaseAuth.getInstance();

        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Esqueceu Senha? ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txRecuEmial = findViewById(R.id.et_login_emailRecup);
    }

    public void recuSenha(View view){
        String stringEmail = txRecuEmial.getText().toString().trim();

        if(!stringEmail.isEmpty()){
            firebaseAuth
                    .sendPasswordResetEmail(stringEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                txRecuEmial.setText("");
                                Toast.makeText(EsqueceuSenhaActivity.this, "Email enviado!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EsqueceuSenhaActivity.this, "Falha ao enviar email!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }else{
            Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
        }
    }
}