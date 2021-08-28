package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.mbappsoftware.aprot.helper.UsuarioFirebase.getIdUsuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email, senha;
    private FirebaseAuth autenticacao;
    private FirebaseFirestore db;
    private List<Usuario> funcionarioList = new ArrayList<>();
    private String textEmail, textSenha;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = ConfiguracaoFirebase.getfirebaseFirestore();

        iniciarComponentes();
    }

    private void iniciarComponentes(){

        email = findViewById(R.id.login_et_email);
        senha = findViewById(R.id.login_et_senha);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //verificarUsuarioLogado();
        //deslogarUsuario();
    }

    public void validaLogin(View view) {
        textEmail = email.getText().toString().trim();
        textSenha = senha.getText().toString().trim();

        if(!textEmail.isEmpty()){
            if (!textSenha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(textEmail);
                usuario.setSenha(textSenha);
                validarLogin(usuario);

            }else{
                senha.setError("Digite uma senha!!");
            }
        }else{
            email.setError("Digite seu email");
        }
    }

    private void validarLogin(Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "AGUARDE!!", Toast.LENGTH_SHORT).show();

                    FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
                    db.collection("funcionario")
                            .document(getIdUsuario())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()){
                                        Usuario usuario = documentSnapshot.toObject(Usuario.class);

                                        String tipoUsuario = usuario.getTipoDoFuncionario();

                                        if (tipoUsuario.equals(Usuario.TIPO_CAMPO) || tipoUsuario.equals(Usuario.TIPO_PROJETO)){
                                            Toast.makeText(LoginActivity.this, "Bem vindo ", Toast.LENGTH_SHORT).show();
                                            Log.i("tipoUsuario", "tipo do user: " + tipoUsuario );
                                            abrirTelaHome();

                                        }else{
                                            deslogarUsuario();
                                            senha.setText("");
                                            Toast.makeText(LoginActivity.this, "Você não é cadastrado como passageiro, faça o cadastro", Toast.LENGTH_SHORT).show();
                                        }/*else if (tipoUsuario.equals("B"))
                                        USUARIO BLOQUEADO FAZER TEXTO PARA ABRIR NUMERO DA CENTRAL PARA SABER MOTIVO */
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Você não é cadastrado como passageiro, faça o cadastro", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Email e senha nao correspondem a um usuario cadastrado!";
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuario nao esta cadastrado";
                    }catch (Exception e){
                        excecao = " Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    //Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();

                    verificaCadastroTemporario(excecao);
                }
            }
        });
    }

    private void verificaCadastroTemporario(String excecao) {

        db.collection("funcionarioTemporario")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //funcionarioList.clear();
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> listaDocumento = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : listaDocumento) {
                                Usuario funcionarioRec = d.toObject(Usuario.class);

                                if (funcionarioRec.getEmail().equals(textEmail)){
                                    if (textSenha.equals(funcionarioRec.getSenha())){
                                        finalizarCadastro(funcionarioRec);
                                    }else{
                                        senha.setError("Senha incorreta!!");
                                    }
                                }

                                //funcionarioList.add(funcionarioRec);
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, excecao , Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("edada", "erro : " + e );
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.i("edada", "cancelo: ");
            }
        });
    }

    private void finalizarCadastro(Usuario funcionario) {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Aguarde! FINALIZANDO CADASTRO!!")
                .setCancelable(false)
                .build();
        dialog.show();

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                funcionario.getEmail(),
                funcionario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    try {

                        //identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        //usuario.setId(identificadorUsuario);
                        String identificadorUsuario = task.getResult().getUser().getUid();
                        funcionario.setUid(identificadorUsuario);
                        funcionario.setUidTemporario(funcionario.getUidTemporario());
                        funcionario.salvarFirestoreUsuario(funcionario);

                        //DELETAR FUNCIONARIO TEMPORARIO
                        funcionario.deletarFuncionarioTemporario();

                        UsuarioFirebase.atualizaNomeUsuario(funcionario.getNome());

                        //startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                        //i.putExtra("funcionario", funcionario);
                        startActivity(i);
                        finish();

                        dialog.dismiss();

                        Toast.makeText(LoginActivity.this, "Cadastro Finalizado!", Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        e.printStackTrace();
                        dialog.dismiss();
                    }

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um e-mail valido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta ja foi cadastrada";
                    }catch (Exception e){
                        excecao = " Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private void abrirTelaHome(){
        Intent i = new Intent(LoginActivity.this,HomeActivity.class);
        //i.putExtra("funcionario", usuario);
        startActivity(i);
        finish();
        //startActivity(new Intent(this, MercadoPagoActivity.class));
    }

    public void abrirTelaCadastro(View view) {
        startActivity(new Intent(this, CadastroActivity.class));
    }

    public void abrirTelaRecuperarSenha(View view) {
        startActivity(new Intent(this, EsqueceuSenhaActivity.class));
    }

    private void deslogarUsuario(){
        try{
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}