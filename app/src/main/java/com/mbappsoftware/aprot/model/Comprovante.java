package com.mbappsoftware.aprot.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Comprovante implements Serializable {

    private String uidComprovante;
    private String uidFuncionario;
    private String uidProjeto;
    private String nomeProjeto;
    private String nomeFuncionario;
    private Double valorNota;
    private String diaDaNota;
    private String status;
    private String urlImagem;
    private String tipoComprovante;
    private String observacao;
    private int qtdNotas;

    public static final String STATUS_ANALISE = "ANALISANDO";
    public static final String STATUS_CONFIRMADO = "CONFIRMADO";
    public static final String STATUS_PAGO = "PAGO";

    public Comprovante() {
    }

    public void salvarFirestoreComprovante(Comprovante comprovante){
        //int numAleatorio = (int)(Math.random() * 100000 ) + 1000;
        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db/*.collection("funcionario")
                .document(getUidFuncionario())*/
                .collection("comprovante")
                .document(getUidComprovante())
                .set(comprovante)
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
        db.collection("comprovante")
                .document(getUidComprovante())
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

    public void removerFirestoreComprovante(){
        FirebaseFirestore db = ConfiguracaoFirebase.getfirebaseFirestore();
        db.collection("comprovante")
                .document(getUidComprovante())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getQtdNotas() {
        return qtdNotas;
    }

    public void setQtdNotas(int qtdNotas) {
        this.qtdNotas = qtdNotas;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public String getTipoComprovante() {
        return tipoComprovante;
    }

    public void setTipoComprovante(String tipoComprovante) {
        this.tipoComprovante = tipoComprovante;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public String getUidProjeto() {
        return uidProjeto;
    }

    public void setUidProjeto(String uidProjeto) {
        this.uidProjeto = uidProjeto;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUidFuncionario() {
        return uidFuncionario;
    }

    public void setUidFuncionario(String uidFuncionario) {
        this.uidFuncionario = uidFuncionario;
    }

    public String getUidComprovante() {
        return uidComprovante;
    }

    public void setUidComprovante(String uidComprovante) {
        this.uidComprovante = uidComprovante;
    }

    public Double getValorNota() {
        return valorNota;
    }

    public void setValorNota(Double valorNota) {
        this.valorNota = valorNota;
    }

    public String getDiaDaNota() {
        return diaDaNota;
    }

    public void setDiaDaNota(String diaDaNota) {
        this.diaDaNota = diaDaNota;
    }
}
