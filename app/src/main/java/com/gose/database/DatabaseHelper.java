package com.gose.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by APATTA-PU on 10/3/2558.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getSimpleName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "gose.db";
    private RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> favoriteGovernmentOfficesDao = null;



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Log.i(LOG, "Create Database");

    }
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, FavoriteGovernmentOffice.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.i(LOG, "Create Table");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, FavoriteGovernmentOffice.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public synchronized void close() {
        super.close();
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();

        Log.i(LOG, "Close Database");
    }
    public RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> getfavoriteGovernmentofficesDao() {
        if (favoriteGovernmentOfficesDao == null) {
            favoriteGovernmentOfficesDao = getRuntimeExceptionDao(FavoriteGovernmentOffice.class);
        }
        return favoriteGovernmentOfficesDao;
    }

}

