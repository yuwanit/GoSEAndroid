package com.gose.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * Created by APATTA-PU on 10/3/2558.
 */

@DatabaseTable(tableName = "FavoriteGovernmentOffice")
public class FavoriteGovernmentOffice {
    public static final String COLUMN_NAME_GOVERNMENT_ID = "government_id";
    public static final String COLUMN_NAME_GOVERNMENT_NAME = "government_name";
    public static final String COLUMN_NAME_GOVERNMENT_THAI_NAME = "government_thai_name";

    @DatabaseField(columnName = COLUMN_NAME_GOVERNMENT_ID)
    public int government_id;
    @DatabaseField(columnName = COLUMN_NAME_GOVERNMENT_NAME)
    public String government_name;
    @DatabaseField(columnName = COLUMN_NAME_GOVERNMENT_THAI_NAME)
    public String government_thai_name;

    public FavoriteGovernmentOffice(){}

    public FavoriteGovernmentOffice (int government_id, String government_name, String government_thai_name){
        this.government_id = government_id;
        this.government_name = government_name;
        this.government_thai_name = government_thai_name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("government_id = ").append(government_id);
        sb.append(", ").append("government_name = ").append(government_name);
        sb.append(", ").append("government_thai_name = ").append(government_thai_name);
        return sb.toString();
    }
}
