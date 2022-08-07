package com.example.test_task.presenter;

import android.content.Context;

import androidx.room.Room;

import com.example.test_task.dao.AppDatabase;
import com.example.test_task.dao.ContactDao;
import com.example.test_task.model.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactListPresenter {
    private ContactListView contactListView;
    private final AppDatabase database;
    private final List<Contact> contactList = new ArrayList<>();

    public ContactListPresenter(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "contact").build();
    }

    public void attachContactView(ContactListView contactListView) {
        this.contactListView = contactListView;
    }

    public void detachContactView() {
        contactListView = null;
    }

    public Disposable findContactList() {
        contactList.clear();
        int threadCt = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadCt);
        return Observable.create((ObservableOnSubscribe<Contact>) emitter -> {
            ContactDao contactDao = database.contactDao();
            List<Contact> autoList = contactDao.findAll();
            this.contactList.addAll(autoList);
            emitter.onComplete();
        }).subscribeOn(Schedulers.from(executor))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> contactListView.displayContactList(contactList)).subscribe();
    }

    public interface ContactListView {
        void displayContactList(List<Contact> contacts);
    }

}
