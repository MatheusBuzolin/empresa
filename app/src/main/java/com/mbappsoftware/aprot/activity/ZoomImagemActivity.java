package com.mbappsoftware.aprot.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.model.Comprovante;

public class ZoomImagemActivity extends AppCompatActivity {

    private String texto, strFotoMotoristaInfo, urlFoto;
    private ImageView iVFoto;
    private Comprovante comprovante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_imagem);

        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("COMPROVANTE");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iVFoto = findViewById(R.id.pv_image);

        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (getIntent().getExtras().containsKey("comprovanteList"))) {

            comprovante = (Comprovante) extras.getSerializable("comprovanteList");
            urlFoto = extras.getString("urlComprovanteZoom");
            texto = extras.getString("nomeProjeto");
            carregarFotoString(urlFoto);

            Log.i("funcks", "FUNCIONARIO > " + comprovante.getDiaDaNota());
        }
    }

        private void carregarFotoString(String foto) {
        Uri uri = Uri.parse(foto);

        Glide.with(ZoomImagemActivity.this).asBitmap().load(uri).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(ZoomImagemActivity.this, "ERRO AO CARREGAR FOTO MOTORISTA", Toast.LENGTH_SHORT).show();
                //progressBarCriminal.setVisibility(View.GONE);
                iVFoto.setImageResource(R.drawable.padrao);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                //progressBarCriminal.setVisibility(View.GONE);
                return false;
            }
        }).into(iVFoto);
    }

    private void carregarFotoUri(Uri uri) {


        Glide.with(ZoomImagemActivity.this).asBitmap().load(uri).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                Toast.makeText(ZoomImagemActivity.this, "ERRO AO CARREGAR FOTO MOTORISTA", Toast.LENGTH_SHORT).show();
                //progressBarCriminal.setVisibility(View.GONE);
                iVFoto.setImageResource(R.drawable.padrao);

                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                //progressBarCriminal.setVisibility(View.GONE);

                return false;
            }
        }).into(iVFoto);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (comprovante.getUrlImagem() != null) {
            Intent i = new Intent(ZoomImagemActivity.this, ComprovanteActivity.class);
            i.putExtra("comprovanteList", comprovante);
            i.putExtra("nomeProjeto", texto);
            startActivity(i);
        }
        return false;

    }
}