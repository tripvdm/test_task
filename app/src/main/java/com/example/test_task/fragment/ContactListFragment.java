package com.example.test_task.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.example.test_task.presenter.DeletingListPresenter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListFragment extends Fragment implements ContactListPresenter.ContactListView, AddContactPresenter.AddContactView {
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

    private ContactListPresenter contactListPresenter;
    private Context context;
    private ContactListRecyclerAdapter contactListRecyclerAdapter;
    private DeletingListPresenter.DeletingListView deletingListView;
    private final boolean delete;

    public ContactListFragment(boolean delete) {
        this.delete = delete;
    }

    public ContactListFragment(DeletingListPresenter.DeletingListView deletingListView, boolean delete) {
        this.deletingListView = deletingListView;
        this.delete = delete;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final View customLayout = getLayoutInflater().inflate(R.layout.alert_dialog, null);
            Contact contact = new Contact();
            AlertDialog alertDialog = builder.setView(customLayout)
                    .setPositiveButton("Добавить", (dialogInterface, i) -> handlePositiveButton(contact)).show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            EditText editName = customLayout.findViewById(R.id.editName);
            editName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    contact.setName(String.valueOf(charSequence));
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });

            EditText editEmail = customLayout.findViewById(R.id.editEmail);
            editEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (checkEmail(String.valueOf(charSequence))) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        contact.setEmail(String.valueOf(charSequence));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });

            EditText editPhone = customLayout.findViewById(R.id.editPhone);
            editPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    contact.setPhone(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handlePositiveButton(Contact contact) {
        AddContactPresenter addContactPresenter = new AddContactPresenter(context);
        addContactPresenter.attachContactFragment(ContactListFragment.this);
        addContactPresenter.addContact(contact);
    }

    private boolean checkEmail(String email) {
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactListPresenter = new ContactListPresenter(context);
        contactListPresenter.attachContactView(this);
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
        contactListRecyclerAdapter.setDelete(delete);
        contactListRecyclerAdapter.setDeletingListView(deletingListView);
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

    @Override
    public void displayContactList(List<Contact> contacts) {
        swipeContactListContainer.setRefreshing(false);
        progressBarContactList.setVisibility(View.GONE);
        createContactListRecyclerAdapter(contacts);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contactListPresenter.findContactList().dispose();
        contactListPresenter.detachContactView();
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
                SwipeRefreshListState swipeRefreshListState = new SwipeRefreshListState();
                contextContentView.setStateContent(swipeRefreshListState);
                contextContentView.displayContent();
            }
        }

    }

    public class SwipeRefreshListState implements StateContent {
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
