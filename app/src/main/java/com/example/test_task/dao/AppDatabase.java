package com.example.test_task.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.test_task.model.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
}
