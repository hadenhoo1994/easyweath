package app.easyweather.com.easyweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Haden on 2016/6/19.
 */
public class EasyWeatherOpenHelper extends SQLiteOpenHelper {

    /*
    * province表 建表语句
    * */
    public static final String CREATE_PROVINCE = "create table Province("
            + "id integer primary key autoincrement ,"
            + "province_name text, "
            + "province_code text) ";

    /*
    *city表 建表语句
     */
    public static final String CREATE_CITY = "create table City("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    /*
    *County表 建表语句
     */
    public  static final String CREATE_COUNTY = "create table County("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";


    public EasyWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);//创建provin表
        db.execSQL(CREATE_CITY);//创建city表
        db.execSQL(CREATE_COUNTY);//创建county表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
