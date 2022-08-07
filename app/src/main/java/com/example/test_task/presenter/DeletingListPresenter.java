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

public class DeletingListPresenter {
    private final AppDatabase database;
    private DeletingListView deletingListView;

    public DeletingListPresenter(final Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "contact").build();
    }

    public void attachDeletingListView(DeletingListView deletingListView) {
        this.deletingListView = deletingListView;
    }

    public void deleteContact(Contact contact) {
        Observable.create((ObservableOnSubscribe<Contact>) emitter -> {
            ContactDao contactDao = database.contactDao();
            contactDao.delete(contact);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> deletingListView.transitionToStaticsFragment())
                .subscribe();
    }

    public interface DeletingListView {
        void transitionToStaticsFragment();
    }
}
