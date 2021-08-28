package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.adapter.ListaProjetosAdapter;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.RecyclerItemClickListener;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Projeto;
import com.mbappsoftware.aprot.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference firebaseRef;
    private FirebaseFirestore db;
    private FirebaseAuth autenticacao;

    private Usuario funcionario;
    private List<Projeto> projetoList = new ArrayList<>();
    private ListaProjetosAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerProjetos;
    private RecuperaDadosFunc runRecDadosFunc = new RecuperaDadosFunc();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        db = ConfiguracaoFirebase.getfirebaseFirestore();

        new Thread(runRecDadosFunc).start();
        iniciaComponentes();
    }

    private void iniciaComponentes() {

        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Criar uma viagem");
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerProjetos = findViewById(R.id.home_rv_listProjetos);

        drawerLayout = findViewById(R.id.drawerLayoutId);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigationViewId);
        navigationView.setNavigationItemSelectedListener(this);

        //configurar recyclerview
        recyclerProjetos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProjetos.setHasFixedSize(true);
        adapter = new ListaProjetosAdapter(projetoList);
        recyclerProjetos.setAdapter(adapter);

        recyclerProjetos.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerProjetos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Projeto projeto = projetoList.get(position);

                        if (funcionario != null) {
                            Intent i = new Intent(HomeActivity.this, AddComprovanteActivity.class);
                            i.putExtra("projetoList", projeto);
                            i.putExtra("funcionario", funcionario);
                            startActivity(i);
                        } else {
                            Toast.makeText(HomeActivity.this, "Carregando dados. Tente novamente!", Toast.LENGTH_SHORT).show();
                        }

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void recProjetos() {

        db.collection("projeto")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        projetoList.clear();
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> listaDocumento = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : listaDocumento) {
                                Projeto projetoRec = d.toObject(Projeto.class);

                                projetoList.add(projetoRec);
                            }
                            //Collections.reverse(carroList);
                            adapter.notifyDataSetChanged();

                        }

                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recibos: {
                startActivity(new Intent(this, ListaProjetosActivity.class));
                //startActivity(new Intent(this, ListaComprovanteActivity.class));
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_passageiro_sai:
                deslogarUsuario();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario() {
        try {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checandoConexao() {
        ConnectivityManager conex = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Network informa = conex.getAllNetworks();  // ConnectivityManager.NetworkCallback informa;
        //NetworkInfo networkInfos = conex.getActiveNetworkInfo();


        Network network = conex.getActiveNetwork();
        NetworkCapabilities capabilities = conex.getNetworkCapabilities(network);
        if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
            Log.i("gggchecandoConexao", "checandoConexao-> CONECTADO WIFI: ");
            return true;
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("gggchecandoConexao", "checandoConexao-> DESCONECTADO : ");
            return false;
        }
    }

    class RecuperaDadosFunc implements Runnable {

        @Override
        public void run() {
            if (checandoConexao()) {
                db.collection("funcionario")
                        .document(UsuarioFirebase.getIdUsuario())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    //Toast.makeText(SplashActivity.this, "IF 1", Toast.LENGTH_SHORT).show();

                                    if (document.exists()) {
                                        funcionario = document.toObject(Usuario.class);
                                        recProjetos();
                                    }

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }else{

                runOnUiThread(new Runnable() {//EXIBE DADOS NA TELA ATUAL
                    @Override
                    public void run() {

                        Toast.makeText(HomeActivity.this, "Conexão perdida!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }
}