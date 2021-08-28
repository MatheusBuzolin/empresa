package com.mbappsoftware.aprot.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;

public class UsuarioFirebase {

    private static String tipoUsuario;

    public static String getIdUsuario(){
        return getUsuarioAtual().getUid();
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario =  ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();

    }

    public static boolean atualizaNomeUsuario(String nome){
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (!task.isSuccessful()){
                        Log.i("Perfil", "Erro ao atualizar o nome do perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
