package com.mbappsoftware.aprot.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;

public class Conexao {

    public boolean checandoConexao(Context context) {
        ConnectivityManager conex = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
