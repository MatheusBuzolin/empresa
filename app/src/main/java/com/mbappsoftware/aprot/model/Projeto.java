package com.mbappsoftware.aprot.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Projeto implements Serializable {

    private String uidProjeto;
    private String nomeProjeto;
    private String nomePesquisaProj;

    public static final String STATUS_ANALISE = "ANALISANDO";
    public static final String STATUS_PAGO = "PAGO";

    public Projeto() {
    }

    public void salvarFirestoreUsuario(Projeto projeto){
        //int numAleatorio = (int)(Math.random() * 100000 ) + 1000;
        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("projeto")
                .document(getUidProjeto())
                .set(projeto)
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

    public void updateCarteira(){

        Map<String, Object> upUser = new HashMap<>();
        //upUser.put("carteira", getCarteira());

        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("projeto")
                .document(getUidProjeto())
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

    public void removerFirestoreMotorista(){
        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("projeto")
                .document(getUidProjeto())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public String getUidProjeto() {
        return uidProjeto;
    }

    public void setUidProjeto(String uidProjeto) {
        this.uidProjeto = uidProjeto;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public String getNomePesquisaProj() {
        return nomePesquisaProj;
    }

    public void setNomePesquisaProj(String nomePesquisaProj) {
        this.nomePesquisaProj = nomePesquisaProj;
    }
}
