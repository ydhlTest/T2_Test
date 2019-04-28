package pad.wlw.fjxx.t2_test.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table CarPeccancy(id integer primary key autoincrement,carnumber text not null,code " +
                "text not null,datetime text not null,address text not null,type integer)");
        db.execSQL("create table PeccancyType(id integer primary key autoincrement,remarks text not null,code " +
                "text not null,money text not null,score text not null)");
        db.execSQL("create table CarInfo(id integer primary key autoincrement,carnumber text not null,number " +
                "text not null,cardid text not null,carbrand text not null,buydate text not null,type integer)");
        db.execSQL("create table UserInfo(id integer primary key autoincrement,username text not null,name text " +
                "not null,cardid text not null,sex text not null,tel text not null,registerdate text not null," +
                "type integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
