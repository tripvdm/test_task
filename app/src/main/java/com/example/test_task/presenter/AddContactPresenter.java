package com.example.test_task.presenter;

import android.content.Context;

import androidx.room.Room;

import com.example.test_task.dao.AppDatabase;
import com.example.test_task.dao.ContactDao;
import com.example.test_task.model.Contact;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddContactPresenter {
    private static final String TAG = AddContactPresenter.class.getSimpleName();

    private final AppDatabase database;
    private AddContactView addContactView;

    public AddContactPresenter(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "contact").build();
    }

    public void addContact(Contact contact) {
        Observable.create((ObservableOnSubscribe<Contact>) emitter -> {
            ContactDao contactDao = database.contactDao();
            contactDao.insert(contact);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> addContactView.displayResponse())
                .subscribe();
    }

    public void attachContactFragment(AddContactView addContactView) {
        this.addContactView = addContactView;
    }

    public interface AddContactView {
        void displayResponse();
    }

}
