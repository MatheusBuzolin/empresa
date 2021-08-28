package com.mbappsoftware.aprot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mbappsoftware.aprot.R;
import com.mbappsoftware.aprot.model.Projeto;

import java.util.List;

public class ListaProjetosAdapter extends RecyclerView.Adapter<ListaProjetosAdapter.MyViewHolder>{

    private List<Projeto> projetoList;


    public ListaProjetosAdapter(List<Projeto> projetoList) {
        this.projetoList = projetoList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista_projeto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Projeto projeto = projetoList.get(position);
        holder.nome.setText(projeto.getNomeProjeto());
    }

    @Override
    public int getItemCount() {
        return projetoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.adRequi_tv_nomeProjeto);
        }
    }
}
