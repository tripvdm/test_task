package com.example.test_task.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_task.R;
import com.example.test_task.model.Contact;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeletingListRecyclerAdapter extends RecyclerView.Adapter<DeletingListRecyclerAdapter.DeletingContactViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<Contact> contactList;

    public DeletingListRecyclerAdapter(Context context, List<Contact> contactList) {
        layoutInflater = LayoutInflater.from(context);
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public DeletingListRecyclerAdapter.DeletingContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.contact, parent, false);
        return new DeletingListRecyclerAdapter.DeletingContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeletingContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.email.setText(contact.getEmail());
        holder.phone.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public Contact[] getContactDataArray() {
        return contactList.toArray(new Contact[contactList.size()]);
    }

    public static class DeletingContactViewHolder extends RecyclerView.ViewHolder {



        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.name)
        TextView name;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.email)
        TextView email;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.phone)
        TextView phone;

        public DeletingContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

