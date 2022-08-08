package com.example.test_task.presenter;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Room;

import com.example.test_task.dao.AppDatabase;
import com.example.test_task.dao.ContactDao;
import com.example.test_task.model.Contact;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddContactPresenter {
    private final AppDatabase database;
    private AddContactView addContactView;

    public AddContactPresenter(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "contact").build();
    }

    public void attachContactFragment(AddContactView addContactView) {
        this.addContactView = addContactView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addContact(Contact contact) {
        Observable.create((ObservableOnSubscribe<Contact>) emitter -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            contact.setDate(localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            ContactDao contactDao = database.contactDao();
            contactDao.insert(contact);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> addContactView.displayResponse())
                .subscribe();
    }

    public interface AddContactView {
        void displayResponse();
    }

}
