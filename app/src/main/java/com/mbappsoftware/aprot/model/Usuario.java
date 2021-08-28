package com.mbappsoftware.aprot.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.net.InterfaceAddress;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String uid;
    private String uidTemporario;
    private String nome;
    private String nomePesquisa;
    private String email;
    private String senha;
    private String celular;
    private String tipoDoFuncionario;
    private String cpf;
    private String fotoComprovante;
    private String iniciouNoApp;
    private int qtdNotas;

    public static final String TIPO_CAMPO = "CAMPO";
    public static final String TIPO_PROJETO = "PROJETO";
    public static final String TIPO_ADM = "ADM";

    public Usuario() {
    }

    public void salvarFirestoreUsuario(Usuario usuario){
        //int numAleatorio = (int)(Math.random() * 100000 ) + 1000;
        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("funcionario")
                .document(getUid())
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void updateQtdNota(){

        Map<String, Object> upUser = new HashMap<>();
        upUser.put("qtdNotas", getQtdNotas());

        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("funcionario")
                .document(getUid())
                .update(upUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void deletarFuncionarioTemporario(){
        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("funcionarioTemporario")
                .document(getUidTemporario())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public int getQtdNotas() {
        return qtdNotas;
    }

    public void setQtdNotas(int qtdNotas) {
        this.qtdNotas = qtdNotas;
    }

    public String getUidTemporario() {
        return uidTemporario;
    }

    public void setUidTemporario(String uidTemporario) {
        this.uidTemporario = uidTemporario;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTipoDoFuncionario() {
        return tipoDoFuncionario;
    }

    public void setTipoDoFuncionario(String tipoDoFuncionario) {
        this.tipoDoFuncionario = tipoDoFuncionario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFotoComprovante() {
        return fotoComprovante;
    }

    public void setFotoComprovante(String fotoComprovante) {
        this.fotoComprovante = fotoComprovante;
    }

    public String getIniciouNoApp() {
        return iniciouNoApp;
    }

    public void setIniciouNoApp(String iniciouNoApp) {
        this.iniciouNoApp = iniciouNoApp;
    }
}
