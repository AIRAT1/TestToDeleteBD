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
    private final static String TAG = "TAG";
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
        etName = (EditText)findViewById(R.id.etName);
        etEmail = (EditText)findViewById(R.id.etEmail);

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        ContentValues cv = new ContentValues();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (view.getId()) {
            case R.id.btnAdd:
                Log.d(TAG, "--- Insert in DB ---");
                cv.put("name", name);
                cv.put("email", email);
                long rowID = db.insert("mytable", null, cv);
                Log.d(TAG, "row inserted " + rowID);

                etName.setText("");
                etName.requestFocus();
                etEmail.setText("");
                break;
            case R.id.btnRead:
                Log.d(TAG, "--- Rows in my table ---");
                Cursor c = db.query("mytable", null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    int idColIndex = c.getColumnIndex("id");
                    int nameColIndex = c.getColumnIndex("name");
                    int emailColIndex = c.getColumnIndex("email");
                    do {
                        Log.d(TAG, "ID = " + c.getInt(idColIndex)
                        + ", name " + c.getString(nameColIndex)
                        + ", email " + c.getString(emailColIndex));
                    }while (c.moveToNext());
                }else {
                    Log.d(TAG, "0 rows");
                }
                c.close();
                break;
            case R.id.btnClear:
                Log.d(TAG, "--- Clear myTable ---");
                int clearCount = db.delete("mytable", null, null);
                Log.d(TAG, "deleted rows count = " + clearCount);
                break;
        }
        db.close();
    }
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE mytable ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "email text"
            + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + "mytable");
            onCreate(db);
        }
    }
}
