package com.example.test_task.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.test_task.model.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT *FROM contact")
    List<Contact> findAll();

    @Insert
    void insert(Contact contact);
}
