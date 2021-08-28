package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.model.Comprovante;
import com.mbappsoftware.aprot.model.Usuario;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ComprovanteActivity extends AppCompatActivity {

    private Comprovante comprovante;
    private CircleImageView imagem;
    private TextView idProjeto, dia, valor, status, despesa, observacao;
    private ProgressBar progressBarMotorista;
    private StorageReference storage;
    private String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprovante);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (getIntent().getExtras().containsKey("comprovanteList"))) {

            comprovante = (Comprovante) extras.getSerializable("comprovanteList");
            texto = extras.getString("nomeProjeto");
            Log.i("funcks", "FUNCIONARIO > " + comprovante.getDiaDaNota());
        }

        iniciaComponentes();
    }

    private void iniciaComponentes() {

        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comprovante");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagem = findViewById(R.id.compro_fotoMotorista);
        idProjeto = findViewById(R.id.compro_tv_idComprovante);
        valor = findViewById(R.id.compro_tv_valor);
        dia = findViewById(R.id.compro_tv_dia);
        status = findViewById(R.id.compro_tv_status);
        progressBarMotorista = findViewById(R.id.progressBar_fotoComprovante);
        despesa = findViewById(R.id.compro_tv_despesa);
        observacao = findViewById(R.id.compro_tv_observacao);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (comprovante != null) {
            carregaFoto();
        }
    }

    private void carregaFoto() {
        DecimalFormat decimal = new DecimalFormat("0.00");
        String txtValor = decimal.format(comprovante.getValorNota()).replace(",", ".");
        Log.i("Dfdf", " ->  " + txtValor);

        idProjeto.setText(comprovante.getNomeProjeto());
        valor.setText("R$ " + txtValor);
        dia.setText(comprovante.getDiaDaNota());
        status.setText(comprovante.getStatus());
        despesa.setText(comprovante.getTipoComprovante());

        if (comprovante.getObservacao() != null){
            observacao.setText(comprovante.getObservacao());
        }else{
            observacao.setText("--");
        }

        Uri uriFoto = Uri.parse(comprovante.getUrlImagem());

        final StorageReference imagemRef = storage
                .child("funcionario")
                .child("comprovante")
                .child(comprovante.getUrlImagem() ); //UUID.randomUUID().toString()

            imagemRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i("dccdc", "URL 1-> " + uri.toString());
                        }
                    });


        Glide.with(ComprovanteActivity.this).asBitmap().load(uriFoto).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(ComprovanteActivity.this, "ERRO AO CARREGAR FOTO MOTORISTA", Toast.LENGTH_SHORT).show();
                progressBarMotorista.setVisibility(View.GONE);
                imagem.setImageResource(R.drawable.padrao);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                progressBarMotorista.setVisibility(View.GONE);
                return false;
            }
        }).into(imagem);
    }

    public void abrirFotoMotoristaGrande(View view) {

        if (comprovante.getUrlImagem() != null) {
            Intent i = new Intent(ComprovanteActivity.this, ZoomImagemActivity.class);
            i.putExtra("urlComprovanteZoom", comprovante.getUrlImagem());
            i.putExtra("comprovanteList", comprovante);
            i.putExtra("nomeProjeto", texto);
            startActivity(i);
        }
    }

    private void deletarProjeto() {

        if (comprovante.getStatus().equals(Comprovante.STATUS_ANALISE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("DESEJA DELETAR?")
                    .setMessage("Após confirmar, o comprovante sera deletado!")
                    .setCancelable(false)
                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            comprovante.removerFirestoreComprovante();
                            Toast.makeText(ComprovanteActivity.this, "COMPROVANTE REMOVIDO!!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ComprovanteActivity.this, ListaComprovanteActivity.class);
                            i.putExtra("nomeProjeto", texto);

                            StorageReference imagem = storage
                                    .child("funcionario")
                                    .child("comprovante")
                                    .child(comprovante.getUidComprovante() + ".jpeg" );
                            imagem.delete();

                            startActivity(i);

                        }
                    }).setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            Toast.makeText(ComprovanteActivity.this, "COMPROVANTE JÁ FOI ANALISADO!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deleta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_deletar:
                deletarProjeto();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (comprovante.getUrlImagem() != null) {
            Intent i = new Intent(ComprovanteActivity.this, ListaComprovanteActivity.class);
            i.putExtra("nomeProjeto", texto);
            startActivity(i);
        }
        return false;

    }
}