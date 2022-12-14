package com.example.test_task.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_task.R;
import com.example.test_task.model.Contact;
import com.example.test_task.presenter.DeletingListPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListRecyclerAdapter extends RecyclerView.Adapter<ContactListRecyclerAdapter.ContactViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<Contact> contactList;
    private DeletingListPresenter.DeletingListView deletingListView;
    private boolean delete;

    public ContactListRecyclerAdapter(Context context, List<Contact> contactList) {
        layoutInflater = LayoutInflater.from(context);
        this.contactList = contactList;
    }

    public void setDeletingListView(DeletingListPresenter.DeletingListView deletingListView) {
        this.deletingListView = deletingListView;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
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

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.layoutContact)
        LinearLayout layoutContact;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.name)
        TextView name;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.email)
        TextView email;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.phone)
        TextView phone;

        private final Context context;
        private final DeletingListPresenter deletingListPresenter;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            ButterKnife.bind(this, itemView);
            deletingListPresenter = new DeletingListPresenter(context);
            deletingListPresenter.attachDeletingListView(deletingListView);
            if (delete) addListenerForLayoutContact();
        }

        private void addListenerForLayoutContact() {
            layoutContact.setOnClickListener(view ->
                    new AlertDialog.Builder(context)
                            .setMessage("???? ?????????????????????????? ???????????? ?????????????? ???????????????")
                            .setPositiveButton(android.R.string.yes,
                                    (dialog, whichButton) -> {
                                        Contact contact = contactList.get(getAdapterPosition());
                                        deletingListPresenter.deleteContact(contact);
                                    })
                            .setNegativeButton(android.R.string.no, null).show());
        }

    }
}
