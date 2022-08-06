package com.example.test_task.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.test_task.R;
import com.example.test_task.adapter.ContactListRecyclerAdapter;
import com.example.test_task.model.Contact;
import com.example.test_task.presenter.AddContactPresenter;
import com.example.test_task.presenter.ContactListPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListFragment extends Fragment implements ContactListPresenter.ContactListView, AddContactPresenter.AddContactView {
    private static final String TAG = ContactListFragment.class.getSimpleName();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.swipeContactListContainer)
    SwipeRefreshLayout swipeContactListContainer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.contactRecyclerView)
    RecyclerView contactListRecyclerView;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.addContact)
    FloatingActionButton addContact;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.progressBarList)
    ProgressBar progressBarContactList;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.emptyList)
    TextView emptyContactList;

    private ContactListPresenter contactListPresenter;
    private Context context;
    private ContactListRecyclerAdapter contactListRecyclerAdapter;
    private final boolean deleting;

    public ContactListFragment(boolean deleting) {
        this.deleting = deleting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint({"NewApi", "UseRequireInsteadOfGet"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        ButterKnife.bind(this, view);
        context = getContext();
        createContactListRecyclerAdapter(new ArrayList<>());
        swipeContactListContainer.setOnRefreshListener(() -> {
            if (progressBarContactList.getVisibility() == View.VISIBLE) {
                swipeContactListContainer.setRefreshing(false);
            } else {
                contactListPresenter.findContactList();
            }
        });
        addContact.setOnClickListener(v -> {
            AlertDialog.Builder builder
                    = new AlertDialog.Builder(context);
            builder.setTitle("Добавление контакта");
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            builder.setView(customLayout);
            builder.setPositiveButton("Добавить",
                    (dialog, which) -> {
                        EditText editName = customLayout.findViewById(R.id.editName);
                        EditText editEmail = customLayout.findViewById(R.id.editEmail);
                        EditText editPhone = customLayout.findViewById(R.id.editPhone);
                        Contact contact = new Contact();
                        contact.setName(editName.getText().toString());
                        contact.setEmail(editEmail.getText().toString());
                        contact.setPhone(editPhone.getText().toString());
                        AddContactPresenter addContactPresenter = new AddContactPresenter(context);
                        addContactPresenter.attachContactFragment(this);
                        addContactPresenter.addContact(contact);
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactListPresenter = new ContactListPresenter(getContext());
        contactListPresenter.attachContactFragment(this);
        if (savedInstanceState == null) {
            progressBarContactList.setVisibility(View.VISIBLE);
            contactListPresenter.findContactList();
        } else {
            ContextContentView contextContentView = new ContextContentView();
            StateContent progressBarState = new ContactListState(contextContentView, savedInstanceState);
            progressBarState.displayContent();
        }
    }

    private void createContactListRecyclerAdapter(List<Contact> contactList) {
        contactListRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        contactListRecyclerAdapter = new ContactListRecyclerAdapter(context, contactList);
        contactListRecyclerAdapter.setDeleting(deleting);
        contactListRecyclerView.setAdapter(contactListRecyclerAdapter);
        contactListRecyclerView.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (progressBarContactList.getVisibility() == View.VISIBLE) {
            outState.putBoolean("loadingProgressBar", true);
        } else if (swipeContactListContainer.isRefreshing()) {
            outState.putBoolean("loadingSwipeRefreshLayout", true);
        } else {
            outState.putParcelableArray("contactArray", contactListRecyclerAdapter.getContactDataArray());
        }
        contactListPresenter.findContactList().dispose();
    }

    /*TODO check emptyList*/
    @Override
    public void displayContactList(List<Contact> contacts) {
        if (contacts.isEmpty()) {
            emptyContactList();
        } else {
            emptyContactList.setVisibility(View.GONE);
            swipeContactListContainer.setRefreshing(false);
            progressBarContactList.setVisibility(View.GONE);
            createContactListRecyclerAdapter(contacts);
        }
    }

    private void emptyContactList() {
        emptyContactList.setVisibility(View.VISIBLE);
        swipeContactListContainer.setRefreshing(false);
        progressBarContactList.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contactListPresenter.findContactList().dispose();
    }

    @Override
    public void displayResponse() {
        Toast.makeText(context, "Данные успешно добавлены", Toast.LENGTH_SHORT).show();
    }
    public interface StateContent {
        void displayContent();
    }

    public class ContactListState implements StateContent {
        private final ContextContentView contextContentView;
        private final Bundle savedInstanceState;

        public ContactListState(final ContextContentView contextContentView, final Bundle savedInstanceState) {
            this.contextContentView = contextContentView;
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        public void displayContent() {
            Contact[] contactArray = (Contact[]) savedInstanceState.get("contactArray");
            if (contactArray != null) {
                displayContactList(Arrays.asList(contactArray));
            } else {
                StateContent progressBarStateContent = new ProgressBarState(contextContentView, savedInstanceState);
                contextContentView.setStateContent(progressBarStateContent);
                contextContentView.displayContent();
            }
        }
    }

    public class ProgressBarState implements StateContent {
        private final ContextContentView contextContentView;
        private final Bundle savedInstanceState;

        public ProgressBarState(final ContextContentView contextContentView, final Bundle savedInstanceState) {
            this.contextContentView = contextContentView;
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        public void displayContent() {
            Boolean loadingProgressBar = (Boolean) savedInstanceState.get("loadingProgressBar");
            if (loadingProgressBar != null && loadingProgressBar) {
                progressBarContactList.setVisibility(View.VISIBLE);
            } else {
                SwipeRefreshListState swipeRefreshListState = new SwipeRefreshListState(savedInstanceState);
                contextContentView.setStateContent(swipeRefreshListState);
                contextContentView.displayContent();
            }
        }

    }

    public class SwipeRefreshListState implements StateContent {
        private final Bundle savedInstanceState;

        public SwipeRefreshListState(final Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        public void displayContent() {
            swipeContactListContainer.setRefreshing(true);
        }
    }

    public static class ContextContentView {
        private StateContent stateContent;

        public void setStateContent(final StateContent stateContent) {
            this.stateContent = stateContent;
        }

        public void displayContent() {
            stateContent.displayContent();
        }
    }

}
