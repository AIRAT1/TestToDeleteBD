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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TAG";
    private Button btnAdd, btnRead, btnClear;
    private EditText etName, etEmail;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnRead = (Button)findViewById(R.id.btnRead);
        btnClear = (Button)findViewById(R.id.btnClear);

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);

        dbHelper = new DBHelper(this, "base", null, 1);
    }

    @Override
    public void onClick(View view) {
        ContentValues cv = new ContentValues();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (view.getId()) {
            case R.id.btnAdd:
                Log.d(TAG, "--- Add ---");
                cv.put("name", name);
                cv.put("email", email);
                long insertRow = db.insert("mytable", null, cv);
                Log.d(TAG, "insert row " + insertRow);
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
                        + ", email = " + c.getString(emailColIndex));
                    } while (c.moveToNext());
                }else {
                    Log.d(TAG, "0 rows");
                }
                c.close();
                break;
            case R.id.btnClear:
                Log.d(TAG, "--- Clear ---");
                int clearCount = db.delete("mytable", null, null);
                Log.d(TAG, "deleted row count = " + clearCount);
                db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name= 'mytable'");
//                db.execSQL("delete from sqlite_sequence where name= \'mytable\'");
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
            db.execSQL("DROP IF EXISTS mytable");
            onCreate(db);
        }
    }
}
