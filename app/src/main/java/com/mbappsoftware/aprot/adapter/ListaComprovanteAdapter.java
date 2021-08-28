package com.mbappsoftware.aprot.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.model.Comprovante;

import java.text.DecimalFormat;
import java.util.List;

public class ListaComprovanteAdapter extends RecyclerView.Adapter<ListaComprovanteAdapter.MyViewHolder>{

    private List<Comprovante> comprovanteList;

    public ListaComprovanteAdapter(List<Comprovante> comprovanteList) {
        this.comprovanteList = comprovanteList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista_comprovante, parent, false);
        return new ListaComprovanteAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaComprovanteAdapter.MyViewHolder holder, int position) {

        Comprovante comprovante = comprovanteList.get(position);
        DecimalFormat decimal = new DecimalFormat("0.00");
        String txtValor = decimal.format(comprovante.getValorNota()).replace(",", ".");
        holder.data.setText(comprovante.getDiaDaNota());
        holder.valor.setText("R$ " + txtValor);
        holder.despesa.setText(comprovante.getTipoComprovante());
        if (comprovante.getStatus().equals(Comprovante.STATUS_ANALISE)){
            holder.linearLayout.setBackgroundResource(R.color.vermelho);
        }else{
            holder.linearLayout.setBackgroundResource(R.color.verde);
        }
    }

    @Override
    public int getItemCount() {
        return comprovanteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView data, valor, despesa;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            data = itemView.findViewById(R.id.adRequi_tv_data);
            valor = itemView.findViewById(R.id.adRequi_tv_valor);
            despesa = itemView.findViewById(R.id.adRequi_tv_despesa);
            linearLayout = itemView.findViewById(R.id.lista_comrpo_linear);

        }
    }
}
