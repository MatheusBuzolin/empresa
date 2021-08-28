package com.mbappsoftware.aprot.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.adapter.ListaComprovanteAdapter;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.RecyclerItemClickListener;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Comprovante;

import java.util.ArrayList;
import java.util.List;

public class ListaComprovanteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Comprovante> comprovanteList = new ArrayList<>();
    private ListaComprovanteAdapter adapter;
    private RecyclerView recyclerComprovante;
    private String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_comprovante);

        db = ConfiguracaoFirebase.getfirebaseFirestore();

        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (getIntent().getExtras().containsKey("nomeProjeto"))) {

            //comprovante = (Comprovante) extras.getSerializable("comprovanteList");
            texto = extras.getString("nomeProjeto");

            Log.i("funcks", "FUNCIONARIO > " + texto);
        }

        iniciaComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recProjetos();

    }

    private void recProjetos() {

        db.collection("comprovante")
                .whereEqualTo("uidFuncionario", UsuarioFirebase.getIdUsuario())
                .orderBy("qtdNotas", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        comprovanteList.clear();
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> listaDocumento = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : listaDocumento) {
                                Comprovante comprovanteRec = d.toObject(Comprovante.class);

                                if (comprovanteRec.getNomeProjeto().equals(texto)){
                                    comprovanteList.add(comprovanteRec);
                                }


                            }
                            //Collections.reverse(carroList);

                            adapter.notifyDataSetChanged();


                        }
                    }
                });
    }

    private void iniciaComponentes() {
        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("LISTA DE COMPROVANTES");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerComprovante = findViewById(R.id.listaComprovante_recycler);
        //tvResultado = findViewById(R.id.requisicoes_tv_aguardandoReq);

        //configurar recyclerview
        recyclerComprovante.setLayoutManager(new LinearLayoutManager(this));
        recyclerComprovante.setHasFixedSize(true);
        adapter = new ListaComprovanteAdapter(comprovanteList);
        recyclerComprovante.setAdapter(adapter);

        recyclerComprovante.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerComprovante,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Comprovante comprovante = comprovanteList.get(position);

                        Intent i = new Intent(ListaComprovanteActivity.this, ComprovanteActivity.class);
                        i.putExtra("comprovanteList", comprovante);
                        i.putExtra("nomeProjeto", texto);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }
}