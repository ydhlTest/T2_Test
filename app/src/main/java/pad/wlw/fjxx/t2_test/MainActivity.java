package pad.wlw.fjxx.t2_test;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pad.wlw.fjxx.t2_test.dao.CarInfo;
import pad.wlw.fjxx.t2_test.dao.CarPeccancy;
import pad.wlw.fjxx.t2_test.dao.DBOpenHelper;
import pad.wlw.fjxx.t2_test.dao.PeccancyType;
import pad.wlw.fjxx.t2_test.dao.UserInfo;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;
    private SQLiteDatabase dbR, dbW;
    private SharedPreferences SP;
    private SharedPreferences.Editor editor;
    private JSONObject objectData;
    private Gson gson;
    private ContentValues values;
    private Map<String, Integer> T1;
    private Map<String, Integer> T2;
    private List<Map<String, String>> T3_localDatas, T3_hasDatas;
    private Map<String, Integer> T4_local, T4_has;
    private List<Integer> T5;
    private List<Map<String, String>> T6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        getData();
    }

    private void getData() {
        if (!SP.getBoolean("CarPeccancy", false)) {
            getCarPeccancy();
        }
        if (!SP.getBoolean("PeccancyType", false)) {
            getPeccancyType();
        }
        if (!SP.getBoolean("UserInfo", false)) {
            getUserInfo();
        }
        if (!SP.getBoolean("CarInfo", false)) {
            getCarInfo();
        }
        isSaved();
    }

    private void getCarInfo() {
        JsonObjectRequest request = new JsonObjectRequest(1, "http://172.168.2.80:9999/transportservice/action" +
                "/GetCarInfo.do", objectData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray array = response.optJSONArray("ROWS_DETAIL");
                List<CarInfo> datas = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    CarInfo bean = gson.fromJson(array.optString(i), CarInfo.class);
                    bean.setType(stringToYearType(bean.getPcardid()));
                    datas.add(bean);
                }
                dbW.beginTransaction();
                for (CarInfo bean : datas) {
                    values = new ContentValues();
                    values.put("carnumber", bean.getCarnumber());
                    values.put("number", bean.getNumber());
                    values.put("cardid", bean.getPcardid());
                    values.put("carbrand", bean.getCarbrand());
                    values.put("buydate", bean.getBuydate());
                    values.put("type", bean.getType());
                    dbW.insert("CarInfo", null, values);
                }
                dbW.setTransactionSuccessful();
                dbW.endTransaction();
                editor.putBoolean("CarInfo", true).commit();
                isSaved();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getCarInfo();
            }
        });
        queue.add(request);
    }

    private int stringToYearType(String in) {
        return Integer.parseInt(String.valueOf(in.charAt(8)));
    }

    private void getUserInfo() {
        JsonObjectRequest request = new JsonObjectRequest(1, "http://172.168.2.80:9999/transportservice/action" +
                "/GetSUserInfo.do", objectData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray array = response.optJSONArray("ROWS_DETAIL");
                List<UserInfo> datas = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    UserInfo bean = gson.fromJson(array.optString(i), UserInfo.class);
                    bean.setType(stringToYearType(bean.getPcardid()));
                    datas.add(bean);
                }
                dbW.beginTransaction();
                for (UserInfo bean : datas) {
                    values = new ContentValues();
                    values.put("username", bean.getUsername());
                    values.put("name", bean.getPname());
                    values.put("cardid", bean.getPcardid());
                    values.put("sex", bean.getPsex());
                    values.put("tel", bean.getPtel());
                    values.put("registerdate", bean.getPregisterdate());
                    values.put("type", bean.getType());
                    dbW.insert("UserInfo", null, values);
                }
                dbW.setTransactionSuccessful();
                dbW.endTransaction();
                editor.putBoolean("UserInfo", true).commit();
                isSaved();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getUserInfo();
            }
        });
        queue.add(request);
    }

    private void getPeccancyType() {
        JsonObjectRequest request = new JsonObjectRequest(1, "http://172.168.2.80:9999/transportservice/action" +
                "/GetPeccancyType.do", objectData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray array = response.optJSONArray("ROWS_DETAIL");
                List<PeccancyType> datas = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    PeccancyType bean = gson.fromJson(array.optString(i), PeccancyType.class);
                    datas.add(bean);
                }
                dbW.beginTransaction();
                for (PeccancyType bean : datas) {
                    values = new ContentValues();
                    values.put("code", bean.getPcode());
                    values.put("money", bean.getPmoney());
                    values.put("score", bean.getPscore());
                    values.put("remarks", bean.getPremarks());
                    dbW.insert("PeccancyType", null, values);
                }
                dbW.setTransactionSuccessful();
                dbW.endTransaction();
                editor.putBoolean("PeccancyType", true).commit();
                isSaved();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getPeccancyType();
            }
        });
        queue.add(request);
    }

    private void isSaved() {
        if (SP.getBoolean("CarPeccancy", false) && SP.getBoolean("PeccancyType", false) && SP.getBoolean(
                "UserInfo", false) && SP.getBoolean("CarInfo", false)) {
            aliense();
        }
    }

    private int stringToType(String in) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = format.parse(in);
            int hour = date.getHours();
            if (hour / 2 == 0 && date.getMinutes() == 0 && date.getSeconds() == 0) {
                return hour / 2 - 1;
            } else {
                return hour / 2;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void getCarPeccancy() {
        JsonObjectRequest request = new JsonObjectRequest(1, "http://172.168.2.80:9999/transportservice/action" +
                "/GetAllCarPeccancy.do", objectData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<CarPeccancy> datas = new ArrayList<>();
                JSONArray array = response.optJSONArray("ROWS_DETAIL");
                for (int i = 0; i < array.length(); i++) {
                    CarPeccancy bean = gson.fromJson(array.optString(i), CarPeccancy.class);
                    bean.setType(stringToType(bean.getPdatetime()));
                    datas.add(bean);
                }
                dbW.beginTransaction();
                for (CarPeccancy bean : datas) {
                    values = new ContentValues();
                    values.put("carnumber", bean.getCarnumber());
                    values.put("code", bean.getPcode());
                    values.put("address", bean.getPaddr());
                    values.put("type", bean.getType());
                    values.put("datetime", bean.getPdatetime());
                    dbW.insert("CarPeccancy", null, values);
                }
                dbW.setTransactionSuccessful();
                dbW.endTransaction();
                editor.putBoolean("CarPeccancy", true).commit();
                isSaved();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getCarPeccancy();
            }
        });
        queue.add(request);
    }

    private void aliense() {
        T1 = new HashMap<>();
        Cursor c1_1 = dbR.rawQuery("select count(*) as c from Carinfo", null);
        int local = 0, has = 0;
        if (c1_1.moveToFirst()) {
            local = c1_1.getInt(0);
        }
        Cursor c1_2 = dbR.rawQuery("select count(*) as c from CarPeccancy", null);
        if (c1_2.moveToFirst()) {
            has = c1_2.getInt(0);
        }
        T1.put("has", has);
        T1.put("local", local);
        //第二张图
        T2 = new HashMap<>();
        int once = 0, more = 0, low = 0, min = 0, high = 0, tmp;
        Cursor c2 = dbR.rawQuery("select count(*) from CarPeccancy group by carnumber", null);
        while (c2.moveToNext()) {
            tmp = c2.getInt(0);
            switch (tmp) {
                case 1:
                    once++;
                    low++;
                    break;
                case 2:
                    more++;
                    low++;
                    break;
                case 3:
                case 4:
                case 5:
                    more++;
                    min++;
                    break;
                default:
                    more++;
                    high++;
                    break;
            }
        }
        T2.put("once", once);
        T2.put("more", more);
        T2.put("low", low);
        T2.put("min", min);
        T2.put("high", high);
        //第三张图
        T3_localDatas = new ArrayList<>();
        T3_hasDatas = new ArrayList<>();
        Cursor c3_local = dbR.rawQuery("select count(*) as c,type from CarInfo group by type order by type desc", null);
        while (c3_local.moveToNext()) {
            Map<String, String> T3_local = new HashMap<>();
            T3_local.put("type", c3_local.getString(c3_local.getColumnIndex("type")));
            T3_local.put("count", c3_local.getString(c3_local.getColumnIndex("c")));
            T3_localDatas.add(T3_local);
        }
        Cursor c3_has = dbR.rawQuery("select count(*) as c,type from CarInfo where carnumber in " +
                "(select carnumber from CarPeccancy group by carnumber) group by type order by type desc", null);
        while (c3_has.moveToNext()) {
            Map<String, String> T3_has = new HashMap<>();
            T3_has.put("type", c3_has.getString(c3_has.getColumnIndex("type")));
            T3_has.put("count", c3_has.getString(c3_has.getColumnIndex("c")));
            T3_hasDatas.add(T3_has);
        }
        //第四张图
        Cursor c4_local = dbR.rawQuery("select count(*)as c,sex from UserInfo group by sex", null);
        T4_local = new HashMap<>();
        while (c4_local.moveToNext()) {
            T4_local.put(c4_local.getString(c4_local.getColumnIndex("sex")),
                    c4_local.getInt(c4_local.getColumnIndex("c")));
        }
        Cursor c4_has = dbR.rawQuery("select count(*)as c,sex from UserInfo where cardid in (select cardid from " +
                        "CarInfo where carnumber in (select carnumber from CarPeccancy)) group by sex",
                null);
        T4_has = new HashMap<>();
        while (c4_has.moveToNext()) {
            T4_has.put(c4_has.getString(c4_has.getColumnIndex("sex")),
                    c4_has.getInt(c4_has.getColumnIndex("c")));
        }
        //第五张图
        Cursor c5 = dbR.rawQuery("select count(*) from CarPeccancy group by type", null);
        T5 = new ArrayList<>();
        while (c5.moveToNext()) {
            T5.add(c5.getInt(0));
        }
        //第六张图
        Cursor c6 = dbR.rawQuery("select count(CarPeccancy.code) as c,PeccancyType.remarks as t from " +
                "PeccancyType," +
                "CarPeccancy where CarPeccancy.code=PeccancyType.code group by CarPeccancy.code ORDER BY c desc " +
                "limit 10", null);
        T6 = new ArrayList<>();
        while (c6.moveToNext()) {
            Map<String, String> data = new HashMap<>();
            data.put("type", c6.getString(c6.getColumnIndex("t")));
            data.put("count", c6.getString(c6.getColumnIndex("c")));
            T6.add(data);
        }
        setView();
    }

    private void setView() {

    }

    private void initData() {
        gson = new Gson();
        objectData = new JSONObject();
        try {
            objectData.put("UserName", "user1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SP = getSharedPreferences("Save", MODE_PRIVATE);
        editor = SP.edit();
        queue = Volley.newRequestQueue(this);
        DBOpenHelper helper = new DBOpenHelper(this, "Traffic.db");
        dbR = helper.getReadableDatabase();
        dbW = helper.getWritableDatabase();
    }

    private void initView() {

    }
}