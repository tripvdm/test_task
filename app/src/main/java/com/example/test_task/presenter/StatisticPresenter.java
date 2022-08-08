package com.example.test_task.presenter;

import android.content.Context;

import androidx.room.Room;

import com.example.test_task.dao.AppDatabase;
import com.example.test_task.dao.ContactDao;
import com.example.test_task.model.Contact;
import com.example.test_task.model.Statistic;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StatisticPresenter {
    private final AppDatabase appDatabase;
    private final Statistic statistic;
    private StatisticView statisticView;

    public StatisticPresenter(final Context context) {
        statistic = new Statistic();
        statistic.setCountOfRecords(0);
        statistic.setDateOfFirstRecord("-");
        statistic.setDateOfLastRecord("-");
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "contact").build();
    }

    public void attachStatisticsView(StatisticView statisticView) {
        this.statisticView = statisticView;
    }

    public void detachStatisticsView() {
        statisticView = null;
    }

    public Disposable findOfStatistic() {
        return Observable.create((ObservableOnSubscribe<Contact>) emitter -> {
            ContactDao contactDao = appDatabase.contactDao();
            List<Contact> contacts = contactDao.findAll();
            setFieldsForStatistic(contacts);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> statisticView.displayStatistic(statistic))
                .subscribe();
    }

    private void setFieldsForStatistic(List<Contact> contacts) {
        int records = contacts.size();
        if (records > 0) {
            statistic.setCountOfRecords(records);
            String dateOfFirstRecord = contacts.get(0).getDate();
            statistic.setDateOfFirstRecord(dateOfFirstRecord);
            String dateOfLastRecord = contacts.get(records - 1).getDate();
            statistic.setDateOfLastRecord(dateOfLastRecord);
        }
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public interface StatisticView {
        void displayStatistic(Statistic statistic);
    }
}
