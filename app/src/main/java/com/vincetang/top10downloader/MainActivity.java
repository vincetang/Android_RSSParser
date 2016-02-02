package com.vincetang.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String mFileContents;
    private Button btnParse;
    private ListView listApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnParse = (Button) findViewById(R.id.btnParse);
        listApps = (ListView) findViewById(R.id.xmlListView);

        DownloadData downloadData= new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Add parse activation code
                ParseApplications parseApplications = new ParseApplications(mFileContents);
                parseApplications.process();

                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainActivity.this, R.layout.list_item, parseApplications.getApplications());
                listApps.setAdapter(arrayAdapter);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        //private String mFileContents;

        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLFile(params[0]);
            if (mFileContents == null) {
                Log.d("DownloadData", "Error downloading");
            }

            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("DownloadData", "result is: " + result);
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder tempBuffer = new StringBuilder();

            // Download data from urlPath and catch any exceptions
            try {
                URL url = new URL(urlPath);

                //open URL connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("DownloadData", "The response code is: " + response);

                //Set up input stream from the connection
                InputStream is = connection.getInputStream();

                //A readable stream of bytes from the input stream
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];

                //Start reading data into buffer
                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0)
                        break;

                    // Push data to StringBuilder buffer
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));

                }
                return tempBuffer.toString();

            } catch (IOException e) {
                Log.d("DownloadData", "IO Exception reading data: " + e.getMessage());
                e.printStackTrace();
            } catch(SecurityException e) {
                Log.d("DownloadData", "Security exception reading data: " + e.getMessage());
            }

            return null;
        }
    }
}
