package de.android.testtodeletebd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TestDbActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TAG";
    private Button btnAdd, btnRead, btnClear, btnUpdate, btnDelete;
    private EditText etName, etEmail, etID;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnRead = (Button)findViewById(R.id.btnRead);
        btnClear = (Button)findViewById(R.id.btnClear);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnDelete = (Button)findViewById(R.id.btnDelete);

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etID = (EditText)findViewById(R.id.etID);

        dbHelper = new DBHelper(this, "myDB", null, 1);
    }

    @Override
    public void onClick(View view) {
        ContentValues cv = new ContentValues();

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String id = etID.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (view.getId()) {
            case R.id.btnAdd:
                Log.d(TAG, "--- Add ---");
                cv.put("name", name);
                cv.put("email", email);
                long addRow = db.insert("mytable", null, cv);
                Log.d(TAG, "add row " + addRow);

                etName.setText("");
                etName.requestFocus();
                etEmail.setText("");
                break;
            case R.id.btnRead:
                Log.d(TAG, "--- Read ---");
                Cursor c = db.query("mytable", null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    int idColIndex = c.getColumnIndex("id");
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");
                    do {
                        Log.d(TAG, "ID = " + c.getInt(idColIndex)
                        + ", name = " + c.getString(nameColIndex)
                        + ", name = " + c.getString(emailColIndex));
                    }while (c.moveToNext());
                }else {
                    Log.d(TAG, "0 rows");
                }
                c.close();
                break;
            case R.id.btnClear:
                Log.d(TAG, "--- Clear ---");
                int clearRow = db.delete("mytable", null, null);
                Log.d(TAG, "clearRow " + clearRow);
                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name= 'mytable'");
//                db.execSQL("delete from sqlite_sequence where name= \'mytable\'");
                break;
            case R.id.btnUpdate:
                Log.d(TAG, "--- Update ---");
                cv.put("name", name);
                cv.put("email", email);
                int updateRow = db.update("mytable", cv, "id = ?", new String[]{id});
                Log.d(TAG, "update row " + updateRow);

                etName.setText("");
                etName.requestFocus();
                etEmail.setText("");
                etID.setText("");
                break;
            case R.id.btnDelete:
                Log.d(TAG, "--- Delete ---");
                int deleteRow = db.delete("mytable", "id = " + id, null);
                Log.d(TAG, "delete row " + deleteRow);
                etID.setText("");
                break;
            default:
                break;
        }
        db.close();
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate DB");
            db.execSQL("CREATE TABLE mytable ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "email text"
            + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS mytable");
            onCreate(db);
        }
    }
}
