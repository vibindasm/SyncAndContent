package in.co.mobme.testex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import in.co.mobme.testex.content.Mycontent;

public class MyActivity extends ActionBarActivity {
    URL url1 = null;
    URL url2 = null;
    TextView tat_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Button bt_download = (Button) findViewById(R.id.at_bt_download);
        tat_tv=(TextView) findViewById(R.id.at_tv);
        String encodedURL1 ="http://tools.ietf.org/rfc/rfc3962.txt";
        String encodedURL2 ="http://tools.ietf.org/rfc/rfc3565.txt";

        try {
            url1 = new URL(encodedURL1);
            url2 = new URL(encodedURL2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        bt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tat_tv.setText("");
                getContentResolver().delete(Mycontent.TEXT_URI, null, null);
                new TestAsynch().execute(url1,url2);
            }
        });

        Button bt_conTest=(Button) findViewById(R.id.bt_conTest);
        bt_conTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.this, ContTest.class);
                startActivity(intent);
            }
        });

    }

   class TestAsynch extends AsyncTask<URL, Integer, String>
    {
        protected void onPreExecute (){
            Log.d("PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(URL...urls) {
            int count = urls.length;
            for (int i = 0; i < count; i++) {
                publishProgress((int) ((i / (float) count) * 100));
                // Escape early if cancel() is called
                String res=downloadFile(urls[i]);
                Log.d("get result from server",res);
                /************************add data to the database*************************************/
            try {
                ContentValues values = new ContentValues();
                values.put(Mycontent.DATA_TEXT, res);
                Uri uri = getContentResolver().insert(
                        Mycontent.TEXT_URI, values);
                Toast.makeText(getBaseContext(),
                        uri.toString(), Toast.LENGTH_LONG).show();
            }catch (Exception e)
            {
                Log.d("Error",e.toString());
            }
                if (isCancelled()) break;
            }
        return "success";

        }

        protected void onProgressUpdate(Integer...a){
            Log.d("progeress update","You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("post execute",""+result);
            Uri textUri = Mycontent.TEXT_URI;
            Cursor c = managedQuery(textUri, null, null, null, Mycontent.TEXT_ID);
            if (c.moveToFirst()) {
                do {
                    tat_tv.setText(tat_tv.getText()+"\n"+
                            c.getString(c.getColumnIndex(Mycontent.TEXT_ID)) +
                                    ", " + c.getString(c.getColumnIndex(Mycontent.DATA_TEXT)));
                } while (c.moveToNext());
            }

        }

        private String downloadFile(URL url) {
            StringBuilder response  = new StringBuilder();
            try {

                HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()), 8192);
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }
            }catch (Exception e)
            {
                Log.e("error",e.toString());
            }
            return response.toString().substring(50,500);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
