package com.gose.database;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by APATTA-PU on 10/3/2558.
 */
public class FavoriteGovernmentOfficeData {

    public List<FavoriteGovernmentOffice> getFavoriteGovernmentOffice(DatabaseHelper db) throws SQLException {
        RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> favoriteGovernmentOfficesDao = db.getfavoriteGovernmentofficesDao();
        QueryBuilder<FavoriteGovernmentOffice, Integer> favoriteQb = favoriteGovernmentOfficesDao.queryBuilder();
        List<FavoriteGovernmentOffice> favoriteList = favoriteGovernmentOfficesDao.query(favoriteQb.prepare());
        return favoriteList;
    }

    public List<FavoriteGovernmentOffice> getFavoriteGovernmentOfficeByGovernmentId(DatabaseHelper db, String governmentId) throws SQLException {
        RuntimeExceptionDao<FavoriteGovernmentOffice, Integer> favoriteGovernmentOfficesDao = db.getfavoriteGovernmentofficesDao();
        QueryBuilder<FavoriteGovernmentOffice, Integer> favoriteQb = favoriteGovernmentOfficesDao.queryBuilder();
        favoriteQb.where().eq(FavoriteGovernmentOffice.COLUMN_NAME_GOVERNMENT_ID, governmentId);
        List<FavoriteGovernmentOffice> favoriteList = favoriteGovernmentOfficesDao.query(favoriteQb.prepare());
        return favoriteList;
    }
}

