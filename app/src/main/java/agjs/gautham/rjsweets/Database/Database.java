package agjs.gautham.rjsweets.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import agjs.gautham.rjsweets.Model.SweetOrder;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "Sweets.db";
    private static final int DB_VER= 1 ;

    public Database(Context context) {
        super(context, DB_NAME,null, DB_VER);
    }

    public boolean checkSweetExists(String sweetId, String userPhone){

        boolean flag = false;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM OrderDetail WHERE UserPhone='%s' AND ProductId='%s'",userPhone, sweetId);
        cursor = db.rawQuery(SQLQuery, null);
        flag = cursor.getCount() > 0;
        cursor.close();

        db.close();
        return flag;

    }

    public List<SweetOrder> getCarts(String userPhone){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect ={"UserPhone","ProductName","ProductId","Quantity","Price","Discount","Image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<SweetOrder> result = new ArrayList<>();
        if (c.moveToFirst()){
            do {
                result.add(new SweetOrder(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))
                        ));
            }while (c.moveToNext());
        }
        db.close();
        return result;
    }

    public void addToCart(SweetOrder sweetOrder){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone, ProductId, ProductName, Quantity, Price, Discount, Image) Values('%s','%s','%s','%s','%s','%s','%s');",
                sweetOrder.getUserPhone(),
                sweetOrder.getProductId(),
                sweetOrder.getProductName(),
                sweetOrder.getQuantity(),
                sweetOrder.getPrice(),
                sweetOrder.getDiscount(),
                sweetOrder.getImage());
        db.execSQL(query);
        db.close();
    }

    public void cleanCart(String userPhone){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
        db.close();
    }

    public void removeFromCart(String productId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE ProductId='%s'",productId);
        db.execSQL(query);
        db.close();
    }

    public int getCountCart() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
        db.close();
        return count;
    }

    public void updateCart(SweetOrder sweetOrder) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = '%s' WHERE UserPhone = '%s' AND ProductId= '%s'",sweetOrder.getQuantity(),sweetOrder.getUserPhone(),sweetOrder.getProductId());
        db.execSQL(query);
        db.close();
    }

    public void increaseCart(String userPhone, String sweetId, String quantity) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity = '%s' WHERE UserPhone = '%s' AND ProductId= '%s'",quantity, userPhone, sweetId);
        db.execSQL(query);
        db.close();
    }

}
