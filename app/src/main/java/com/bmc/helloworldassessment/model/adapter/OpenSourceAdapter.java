package com.bmc.helloworldassessment.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmc.helloworldassessment.R;
import com.bmc.helloworldassessment.model.Library;

import java.util.List;

public class OpenSourceAdapter extends RecyclerView.Adapter<OpenSourceAdapter.ViewHolder> {
    private List<Library> libraries;
    private int rowLayout;
    private Context context;

    public OpenSourceAdapter(List<Library> libraries, int rowLayout, Context context) {
        this.libraries = libraries;
        this.rowLayout = rowLayout;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Library library = libraries.get(i);
        viewHolder.libraryName.setText(library.name);
        viewHolder.intent = library.intent;
    }

    @Override
    public int getItemCount() {
        return libraries == null ? 0 : libraries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView libraryName;
        public Intent intent;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            libraryName = (TextView) itemView.findViewById(R.id.oss_name);
            libraryName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            context.startActivity(intent);
        }

    }
}
