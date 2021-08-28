package com.mbappsoftware.aprot.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.config.ConfiguracaoFirebase;
import com.mbappsoftware.aprot.helper.CarregadorDeFoto;
import com.mbappsoftware.aprot.helper.Permissoes;
import com.mbappsoftware.aprot.helper.UsuarioFirebase;
import com.mbappsoftware.aprot.model.Comprovante;
import com.mbappsoftware.aprot.model.Projeto;
import com.mbappsoftware.aprot.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class AddComprovanteActivity extends AppCompatActivity {

    private static final int SELECAO_GALERIA = 200;
    private static final int SELECAO_CAMERA = 100;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private StorageReference storage;

    private TextInputEditText data, valorNota, observacao;
    private Button galeria, camera;
    private ImageView imagemProjeto;
    private Bitmap bitImagProjet = null;
    private Uri urlImagProjeto;
    private AlertDialog dialog;
    private String strURLimagemSelecionada = "";
    private Projeto projeto;
    private Usuario funcionario;

    private Spinner spinnerTipo;
    private ArrayAdapter<String> spinTipo ;
    private String item;
    private int qtdNota;

    private Conex runConex = new Conex();

    Date date = new Date();
    Calendar dataAtual = Calendar.getInstance();
    private String dia, mes, ano;
    private int i = 0, validaConx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comprovante);

        storage = ConfiguracaoFirebase.getFirebaseStorage();
        validaConx = 1;

        //validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        dia = String.valueOf(dataAtual.get(Calendar.DAY_OF_MONTH));
        mes = String.valueOf(dataAtual.get(Calendar.MONTH) + 1);
        ano = String.valueOf(dataAtual.get(Calendar.YEAR));
        //ano = String.valueOf(dataAtual.get(Calendar.DAY_OF_WEEK_IN_MONTH));

        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (getIntent().getExtras().containsKey("projetoList"))) {

            projeto = (Projeto) extras.getSerializable("projetoList");
            funcionario = (Usuario) extras.getSerializable("funcionario");
            qtdNota = funcionario.getQtdNotas();

            Log.i("funcks", "FUNCIONARIO > " + projeto.getNomeProjeto());
        }

        //evento de click na imagem
        galeria = findViewById(R.id.add_bt_galeria);
        camera = findViewById(R.id.add_bt_camera);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        //evento de click na imagem
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }else{
                    Toast.makeText(AddComprovanteActivity.this, "NULOOO 2 ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iniciaComponentes();

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(runConex).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread(runConex).interrupt();
    }

    private void iniciaComponentes(){

        //configurando toobar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("ADD Comprovante");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = findViewById(R.id.add_et_dia);
        valorNota = findViewById(R.id.add_et_valorNota);
        imagemProjeto = findViewById(R.id.add_prod_imagem);
        spinnerTipo = findViewById(R.id.add_spinnerComprovante);
        observacao = findViewById(R.id.add_et_observacao);

        carregarDadosSpinner();

        SimpleMaskFormatter smfV = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtwV = new MaskTextWatcher(data, smfV);
        data.addTextChangedListener(mtwV);

        String txMes, txDia;

        //mes = "10";
        if (mes.length() == 1){
            txMes = "0" + mes;
            Log.i("fgfsv", " teste1 -> " + txMes + " ->>  " + mes.length());
        }else{
            txMes = mes;
            Log.i("fgfsv", " teste2 -> " + txMes + " ->>  " + mes.length());
        }

        if (dia.length() == 1){
            txDia = "0" + dia;
            Log.i("fgfsv", " teste1 -> " + txDia + " ->>  " + dia.length());
        }else{
            txDia = dia;
            Log.i("fgfsv", " teste2 -> " + txDia + " ->>  " + dia.length());
        }

        data.setText(txDia + txMes + ano);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK ){
            // Bitmap imagem = null;
            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        //Toast.makeText(this, "ENTRO 1 ", Toast.LENGTH_SHORT).show();
                        urlImagProjeto = data.getData();
                        bitImagProjet = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        urlImagProjeto
                                );
                        Log.i("addCom", "URL >  " +  urlImagProjeto.toString());
                        //ivFotoCriminal.setImageBitmap(imaCriminal);
                        //imagemProjeto.setImageURI(urlImagProjeto);

                        break;
                    case SELECAO_CAMERA:
                        //Toast.makeText(this, "ENTRO 2 ", Toast.LENGTH_SHORT).show();
                        bitImagProjet = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitImagProjet.compress(Bitmap.CompressFormat.JPEG, 100,bytes);
                        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitImagProjet, "comprovante", null);
                        urlImagProjeto = Uri.parse(path);
                        Log.i("addCom", "URL >  " +  urlImagProjeto.toString());
                        //bitImagProjet = urlImagProjeto;

                        break;
                }

                if (urlImagProjeto == null) {
                    Toast.makeText(this, "Nulo ", Toast.LENGTH_SHORT).show();
                }

                if (bitImagProjet != null){
                    imagemProjeto.setImageBitmap(bitImagProjet);
                    //urlImagProjeto = bitImagProjet;
                }else{
                    //Toast.makeText(AddImagenPetActivity.this, "sem imagem selecao galeria", Toast.LENGTH_SHORT).show();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            /*PODERIA COLOCAR IMAGEM NULA AQUI
             * ENTRA NA GALERIA MAS NAO SELECIONA NENHUMA IMAGEM*/
            //Toast.makeText(AddImagenPetActivity.this, "sem imagem oba", Toast.LENGTH_SHORT).show();
            //exibirMsgErro(" sem imagem oba");
        }
    }

    public void salvar(View view) {
        String idComprovante = UUID.randomUUID().toString();

        String txtData = data.getText().toString().trim();
        String txtValorNota = valorNota.getText().toString().trim();
        String txtObservacao = observacao.getText().toString();

        if (checandoConexao()) {
            if (urlImagProjeto != null) {
                if (!txtData.isEmpty() && data.length() == 10) {
                    if (!txtValorNota.isEmpty()) {
                        if (!item.isEmpty()) {
                            Double doubValorNota = Double.valueOf(txtValorNota);

                            Comprovante compr = new Comprovante();
                            compr.setUidFuncionario(UsuarioFirebase.getIdUsuario());
                            compr.setUidComprovante(idComprovante);
                            compr.setUidProjeto(projeto.getUidProjeto());
                            compr.setNomeProjeto(projeto.getNomeProjeto());
                            compr.setDiaDaNota(txtData);
                            compr.setValorNota(doubValorNota);
                            compr.setTipoComprovante(item);
                            if (!txtObservacao.isEmpty()) {
                                compr.setObservacao(txtObservacao);
                            } else {
                                compr.setObservacao("-");
                            }
                            compr.setNomeFuncionario(funcionario.getNome());

                            salvarComprovante(compr);
                        } else {
                            Toast.makeText(this, "Categoria necessária!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        data.setError("Data completa!");
                    }
                } else {
                    valorNota.setError("Informe o valor!");
                }
            } else {
                Toast.makeText(this, "Foto necessária!!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Sem Conexão!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarComprovante(Comprovante compr) {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando foto, aguarde!!\n Não feche o app")
                .setCancelable(false)
                .build();
        dialog.show();
        if (urlImagProjeto != null){
            //final String identificadorUsuario = UsuarioFirebase.getIdUsuario();

            //salvando no firebase
            final StorageReference imagemRef = storage
                    .child("funcionario")
                    .child("comprovante")
                    .child(compr.getUidComprovante() + ".jpeg" ); //UUID.randomUUID().toString()

            /*imagemRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i("dccdc", "URL 1-> " + uri.toString());
                        }
                    });*/


            UploadTask uploadTask = imagemRef.putFile(urlImagProjeto);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return imagemRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        strURLimagemSelecionada = downloadUri.toString();

                        qtdNota ++;

                        Log.i("dccdc", "URL 2-> " + strURLimagemSelecionada);
                        Log.i("adwada", "qtdNota-> " + qtdNota);

                        if (strURLimagemSelecionada != null){
                            Usuario u = new Usuario();
                            u.setUid(UsuarioFirebase.getIdUsuario());
                            u.setQtdNotas(qtdNota);
                            u.updateQtdNota();

                            compr.setStatus(Comprovante.STATUS_ANALISE);
                            compr.setUrlImagem(strURLimagemSelecionada);
                            compr.setQtdNotas(qtdNota);
                            compr.salvarFirestoreComprovante(compr);

                        }

                        dialog.dismiss();
                        Toast.makeText(AddComprovanteActivity.this, "salvo com sucesso", Toast.LENGTH_SHORT).show();

                    }else{
                        dialog.dismiss();
                        Toast.makeText(AddComprovanteActivity.this, "Falha ao salvar foto! Tente novamente!", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    dialog.dismiss();
                    Toast.makeText(AddComprovanteActivity.this, "Falha ao salvar foto! Verifique sua conexão!", Toast.LENGTH_LONG).show();
                    Log.i("csdd", " Exception -> " + e);
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    dialog.dismiss();
                    Toast.makeText(AddComprovanteActivity.this, "Falha ao salvar foto! Verifique sua conexão!", Toast.LENGTH_LONG).show();

                }
            });
        }else{
            dialog.dismiss();
        }

    }

    private void carregarDadosSpinner() {

        String[] categoria = getResources().getStringArray(R.array.tipo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item,
                categoria
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = spinnerTipo.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean checandoConexao() {
        ConnectivityManager conex = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Network informa = conex.getAllNetworks();  // ConnectivityManager.NetworkCallback informa;
        //NetworkInfo networkInfos = conex.getActiveNetworkInfo();


        Network network = conex.getActiveNetwork();
        NetworkCapabilities capabilities = conex.getNetworkCapabilities(network);
        if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
            Log.i("csasdad", "checandoConexao-> CONECTADO WIFI: ");
            validaConx = 1;
            return true;
        } else {

            if (dialog != null && validaConx == 1){
                validaConx = 0;
                dialog.dismiss();
                runOnUiThread(new Runnable() {//EXIBE DADOS NA TELA ATUAL
                    @Override
                    public void run() {

                        Toast.makeText(AddComprovanteActivity.this, "Conexão perdida!", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(this, "Sem conexão!", Toast.LENGTH_SHORT).show();
            }
            Log.i("csasdad", "checandoConexao-> DESCONECTADO : ");
            return false;
        }
    }

    class Conex implements Runnable {

        @Override
        public void run() {

            while (i == 0) {
                /*ConnectivityManager conex = (ConnectivityManager) AddComprovanteActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                //Network informa = conex.getAllNetworks();  // ConnectivityManager.NetworkCallback informa;
                //NetworkInfo networkInfos = conex.getActiveNetworkInfo();


                Network network = conex.getActiveNetwork();
                NetworkCapabilities capabilities = conex.getNetworkCapabilities(network);
                if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
                    Log.i("gggchecandoConexao", "checandoConexao-> CONECTADO WIFI: ");

                } else {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Log.i("gggchecandoConexao", "checandoConexao-> DESCONECTADO : ");

                }*/

                checandoConexao();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /*runOnUiThread(new Runnable() {//EXIBE DADOS NA TELA ATUAL
                    @Override
                    public void run() {

                        Toast.makeText(AddComprovanteActivity.this, "Conexão perdida!", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        }
    }
}