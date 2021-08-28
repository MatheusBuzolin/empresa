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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.adapter.ListaComprovanteAdapter;
import com.mbappsoftware.aprot.adapter.ListaProjetosAdapter;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.RecyclerItemClickListener;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Comprovante;
import com.mbappsoftware.aprot.model.Projeto;

import java.util.ArrayList;
import java.util.List;

public class ListaProjetosActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private List<Projeto> projetoList = new ArrayList<>();
    private ListaProjetosAdapter adapter;
    private RecyclerView recyclerComprovante;

    private CarregaComprovante runCarregaComprovante = new CarregaComprovante();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_projetos);
        db = ConfiguracaoFirebase.getfirebaseFirestore();

        iniciaComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //recProjetos();
        new Thread(runCarregaComprovante).start();

    }

    class CarregaComprovante implements Runnable {
        @Override
        public void run() {

            db.collection("comprovante")
                    .whereEqualTo("uidFuncionario", UsuarioFirebase.getIdUsuario())
                    .orderBy("qtdNotas", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Comprovante> comprovanteList = new ArrayList<>();

                            if (!queryDocumentSnapshots.isEmpty()) {

                                List<DocumentSnapshot> listaDocumento = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot d : listaDocumento) {
                                    Comprovante comprovanteRec = d.toObject(Comprovante.class);

                                    comprovanteList.add(comprovanteRec);
                                }

                                exibeObra(comprovanteList);
                            }else{
                                exibeObra(null);
                            }
                        }
                    });
        }
    }

    private void exibeObra(List<Comprovante> comprovanteList) {

        Comprovante compr = new Comprovante();
        List<String> nomeProjetoList = new ArrayList<>();

        if (comprovanteList != null){

            for (int i = 0; i < comprovanteList.size(); i++){
                compr = comprovanteList.get(i);
                //stComprovante = compr.getNomeProjeto();

                if (!nomeProjetoList.contains(comprovanteList.get(i).getNomeProjeto())) {
                    nomeProjetoList.add(compr.getNomeProjeto());
                    Log.i("fdfd", " - > " + i+"A" + " ---> ENTRO -> " + nomeProjetoList.toString());
                }else{
                    Log.i("fdfd", " - > " + i+"b" + " ---> SAIU -> " + nomeProjetoList.toString());
                }
            }

            exibeLista(nomeProjetoList);
            Log.i("fdfd", "FINALIZADO -> " + nomeProjetoList.toString());

        }else{
            Toast.makeText(this, "Sem comprovantes!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void exibeLista(List<String> nomeProjetoList) {

        ArrayAdapter<String> adap = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomeProjetoList);
        listView.setAdapter(adap);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String texto = "";
                TextView tv = (TextView) view;
                texto = tv.getText().toString();

                Intent i = new Intent(ListaProjetosActivity.this,ListaComprovanteActivity.class);
                i.putExtra("nomeProjeto", texto);
                startActivity(i);
            }
        });
    }

    private void iniciaComponentes() {
        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("CENTRO DE CUSTO");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.projeto_lista);
        //recyclerComprovante = findViewById(R.id.listaComprovante_recycler);
        //tvResultado = findViewById(R.id.requisicoes_tv_aguardandoReq);



        //configurar recyclerview
        /*recyclerComprovante.setLayoutManager(new LinearLayoutManager(this));
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
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));*/
    }
}