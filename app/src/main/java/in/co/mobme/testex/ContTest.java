package in.co.mobme.testex;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import in.co.mobme.testex.content.Mycontent;


/**
 * Created by mobme on 24/7/14.
 */
public class ContTest extends Activity {

    EditText etNameUpdate,etMobUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contentmain);
        etNameUpdate= (EditText) findViewById(R.id.txtNameUpdate);
        etMobUpdate = (EditText) findViewById(R.id.txtMobUpdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    public void onClickAddName(View view) {
        // Add a new student record
        ContentValues values = new ContentValues();
        values.put(Mycontent.NAME,
                ((EditText) findViewById(R.id.txtName)).getText().toString());

        values.put(Mycontent.MOB,
                ((EditText) findViewById(R.id.txtMob)).getText().toString());

        Uri uri = getContentResolver().insert(
                Mycontent.CONTENT_URI, values);

        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }

    public void onClickRetrieveContact(View view) {
        // Retrieve student records

        Uri students = Mycontent.CONTENT_URI;
        Cursor c = managedQuery(students, null, null, null, Mycontent._ID);
        if (c.moveToFirst()) {
            do {
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(Mycontent._ID)) +
                                ", " + c.getString(c.getColumnIndex(Mycontent.NAME)) +
                                ", " + c.getString(c.getColumnIndex(Mycontent.MOB)),
                        Toast.LENGTH_SHORT
                ).show();
            } while (c.moveToNext());
        }
    }

    public void onClickRetrieveByName(View view) {

        String[] proj={Mycontent.NAME,Mycontent.MOB};
        String sel=Mycontent.NAME+" = ?";
        String[] selArgs = new String[]{((EditText) findViewById(R.id.txtNameFilter)).getText().toString()};
        Uri students = Mycontent.CONTENT_URI;
        try {
            Cursor c = getContentResolver().query(students, proj, sel, selArgs, null);
            if (c.moveToFirst()) {
                do {
                    Toast.makeText(this,c.getString(c.getColumnIndex(Mycontent.NAME)) +
                                    ", " + c.getString(c.getColumnIndex(Mycontent.MOB)),
                            Toast.LENGTH_SHORT
                    ).show();
                } while (c.moveToNext());
            }
        }catch (Exception e)
        {
            Log.e("Error",e.toString());
        }
    }
    public void onClickDelById(View view) {

        String sel=Mycontent._ID+" = ?";
        String[] selArgs = new String[]{((EditText) findViewById(R.id.txtIdDel)).getText().toString()};

        Uri students = Mycontent.CONTENT_URI;
        try {
           int count = getContentResolver().delete(students, sel, selArgs);
            if (count!=0)
                Toast.makeText(this,"Deleted Successfully",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"Invalid Contact ID",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Log.e("Error",e.toString());
        }
    }

    public void onClickGetById(View view) {

        String[] proj={Mycontent.NAME,Mycontent.MOB};
        String sel=Mycontent._ID+" = ?";
        String[] selArgs = new String[]{((EditText) findViewById(R.id.txtIdUpdate)).getText().toString()};
        Uri students = Mycontent.CONTENT_URI;
        try {
            Cursor c = getContentResolver().query(students, proj, sel, selArgs, null);
            if (c.moveToFirst()) {
                do {
                    etNameUpdate.setText(c.getString(c.getColumnIndex(Mycontent.NAME)));
                    etMobUpdate.setText(c.getString(c.getColumnIndex(Mycontent.MOB)));
                } while (c.moveToNext());
            }
        }catch (Exception e)
        {
            Log.e("Error",e.toString());
        }
    }
    public void onClickUpdate(View view) {

        String sel = Mycontent._ID + " = ?";
        String[] selArgs = new String[]{((EditText) findViewById(R.id.txtIdUpdate)).getText().toString()};
        ContentValues values = new ContentValues();
        values.put(Mycontent.NAME,
                ((EditText) findViewById(R.id.txtNameUpdate)).getText().toString());

        values.put(Mycontent.MOB,
                ((EditText) findViewById(R.id.txtMobUpdate)).getText().toString());

        Uri students = Mycontent.CONTENT_URI;
        try {
            int count = getContentResolver().update(students,values, sel, selArgs);
            if (count!=0)
                Toast.makeText(this,"Updated Successfully",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"Invalid Contact ID",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
}
