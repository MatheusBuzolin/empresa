package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Usuario;
import com.santalu.maskedittext.MaskEditText;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText nome, email, senha, confirmaSenha;
    private Spinner spinnerEstado, spinnercidade;
    private MaskEditText celular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        iniciarComponentes();
    }

    private void iniciarComponentes() {
        nome = findViewById(R.id.cadastro_et_nome);
        email = findViewById(R.id.cadastro_et_email);
        senha = findViewById(R.id.cadastro_et_senha);
        confirmaSenha = findViewById(R.id.cadastro_et_confirmaSenha);
        celular = findViewById(R.id.cadastro_et_celular);

    }

    public void btCadastrar(View view) {

        String spCidade = spinnercidade.getSelectedItem().toString().trim();
        String spEstado = spinnerEstado.getSelectedItem().toString().trim();
        String textNome = nome.getText().toString().trim().trim();
        String textEmail = email.getText().toString().trim().trim();
        String textSenha = senha.getText().toString().trim().trim();
        String textConfirmarSenha = confirmaSenha.getText().toString().trim();
        String telefone = celular.getText().toString().trim();
        String textCelular = "";

        if (celular.getRawText() != null){ //para corrigir o erro do usuario nao digitar menos de 10 numeros e tambem para nao salvar vazio
            textCelular = celular.getRawText().toString();
        }

        if (!textNome.isEmpty()){
            if (!textEmail.isEmpty()){
                if (!textSenha.isEmpty() && textSenha.length() > 5){
                    if (textConfirmarSenha.equals(textSenha) ) {
                        if (!telefone.isEmpty() && textCelular.length() == 11) {
                            if (!spEstado.isEmpty() && !spEstado.equals("Estado")) {
                                if (!spCidade.isEmpty() && !spCidade.equals("Cidade")) {

                                    Usuario usuario = new Usuario();
                                    usuario.setNome(textNome);
                                    usuario.setNomePesquisa(textNome.toUpperCase());
                                    usuario.setEmail(textEmail);
                                    usuario.setSenha(textSenha);
                                    usuario.setCelular(textCelular);

                                    cadastrarUsuario(usuario);
                                }else{
                                    Toast.makeText(this, "Escolha sua Cidade", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(this, "Escolha sue Estado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            celular.setError("Digite seu Número");
                        }
                    }else{
                        confirmaSenha.setError("Digite senhas iguais");
                    }
                }else{
                    senha.setError("Digite uma senha maior que 6 numeros ou letras");
                }
            }else{
                email.setError("Digite seu email");
            }
        }else{
            nome.setError("Digite seu nome completo");
        }
    }

    public void cadastrarUsuario(final Usuario usuario){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    try {

                        //identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        //usuario.setId(identificadorUsuario);
                        String identificadorUsuario = task.getResult().getUser().getUid();
                        usuario.setUid(identificadorUsuario);
                        usuario.salvarFirestoreUsuario(usuario);

                        UsuarioFirebase.atualizaNomeUsuario(usuario.getNome());

                        startActivity(new Intent(CadastroActivity.this, HomeActivity.class));
                        finish();
                        Toast.makeText(CadastroActivity.this, "Cadastro efetuado", Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        e.printStackTrace();
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
                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}