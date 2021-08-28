package com.mbappsoftware.aprot.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class CarregadorDeFoto {

    public static Bitmap carrega(String caminhoFoto) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(caminhoFoto);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("fsdfsd", " catch " + e);
        }
        Log.i("fsdfsd", " exif " + caminhoFoto);
        String orientacao = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int codigoOrientacao = Integer.parseInt(orientacao);

        switch (codigoOrientacao) {
            // rotaciona 0 graus no sentido horário case
            case ExifInterface.ORIENTATION_NORMAL:
                return abreFotoERotaciona(caminhoFoto, 0);

            // rotaciona 90 graus no sentido horário case
            case ExifInterface.ORIENTATION_ROTATE_90:
                return abreFotoERotaciona(caminhoFoto, 90);

            // rotaciona 180 graus no sentido horário case
            case ExifInterface.ORIENTATION_ROTATE_180:
                return abreFotoERotaciona(caminhoFoto, 180);

            // rotaciona 270 graus no sentido horário
            case ExifInterface.ORIENTATION_ROTATE_270:
                return abreFotoERotaciona(caminhoFoto, 270);

        }

        return null;


    }

    private static Bitmap abreFotoERotaciona(String caminhoFoto, int angulo) {
        // Abre o bitmap a partir do caminho da foto Bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);

        // Prepara a operação de rotação com o ângulo escolhido
        Matrix matrix = new Matrix(); matrix.postRotate(angulo);

        // Cria um novo bitmap a partir do original já com a rotação aplicada
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
