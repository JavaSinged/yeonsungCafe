package com.example.shinjiwoong.yeonsungcafe.Shopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_gemcafe;
import com.example.shinjiwoong.yeonsungcafe.Cafe.Init_planet37;

public class ShoppingCartDB extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "shoppingcart.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "cart_info";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "menu_name";
    public static final String COLUMN_TEMP = "menu_temp";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_OPTIONS = "options";

    public ShoppingCartDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_TEMP + " TEXT, " +
                COLUMN_COUNT + " TEXT, " +
                COLUMN_OPTIONS + " TEXT" +
                ")";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * 장바구니 전체 가져오기
     *
     * @return
     **/

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    /**
     * 장바구니 추가
     *
     * @param name    메뉴 이름
     * @param image   이미지
     * @param price   가격
     * @param temp    온도
     * @param count   개수
     * @param options 옵션
     */
    public void addCart(String cafeName, String name, String image, String price, String temp, String count, String options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_IMAGE, image);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_TEMP, temp);
        cv.put(COLUMN_COUNT, count);
        cv.put(COLUMN_OPTIONS, options);

        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1) {
            //실패
            Log.e("error", "추가 실패");
        } else {
            //성공
            try {
                if (cafeName.contains("gemcafe")) {
                    Init_gemcafe.counterFab.increase();
                }else{
                    Init_planet37.counterFab.increase();
                }

            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

    public void deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{id});

        if (result == -1) {
            Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(context, "id: "+id, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateData(String id, String count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        //수정할 값
        cv.put(COLUMN_COUNT, count);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{id});

        if (result == -1) {
            Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("수량이 변경됨 ", count);
//            ((ShoppingCart)ShoppingCart.context).tv_totalPrice.setText(((ShoppingCart)ShoppingCart.context).total);
        }
    }
}
