package com.mbappsoftware.aprot.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static DatabaseReference database;
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;
    private static FirebaseFirestore firebaseFirestore;

    //retorna a instancia do firebase
    public static FirebaseAuth getFirebaseAutenticacao(){
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;

    }

    //retorna a referencia do database
    public static DatabaseReference getFirebase(){
        if (database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //retorna instancia do firebaseStorage
    public static StorageReference getFirebaseStorage(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

    //retorna instancia do firebaseStorage
    public static FirebaseFirestore getfirebaseFirestore(){
        if (firebaseFirestore == null){
            firebaseFirestore = FirebaseFirestore.getInstance();
        }
        return firebaseFirestore;
    }

}
