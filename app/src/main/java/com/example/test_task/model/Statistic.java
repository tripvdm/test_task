package com.example.test_task.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Statistic implements Parcelable {
    private int countOfRecords;
    private String dateOfFirstRecord;
    private String dateOfLastRecord;

    public Statistic() {}

    public Statistic(Parcel in) {
        countOfRecords = in.readInt();
        dateOfFirstRecord = in.readString();
        dateOfLastRecord = in.readString();
    }

    public static final Creator<Statistic> CREATOR = new Creator<Statistic>() {
        @Override
        public Statistic createFromParcel(Parcel in) {
            return new Statistic(in);
        }

        @Override
        public Statistic[] newArray(int size) {
            return new Statistic[size];
        }
    };

    public int getCountOfRecords() {
        return countOfRecords;
    }

    public void setCountOfRecords(int countOfRecords) {
        this.countOfRecords = countOfRecords;
    }

    public String getDateOfFirstRecord() {
        return dateOfFirstRecord;
    }

    public void setDateOfFirstRecord(String dateOfFirstRecord) {
        this.dateOfFirstRecord = dateOfFirstRecord;
    }

    public String getDateOfLastRecord() {
        return dateOfLastRecord;
    }

    public void setDateOfLastRecord(String dateOfLastRecord) {
        this.dateOfLastRecord = dateOfLastRecord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(countOfRecords);
        dest.writeString(dateOfFirstRecord);
        dest.writeString(dateOfLastRecord);
    }
}
