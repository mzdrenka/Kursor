package com.bubus.kursor;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    // Debug log tag.
    private static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";

    // Child thread sent message type value to activity main thread Handler.
    private static final int REQUEST_CODE_SHOW_RESPONSE_TEXT = 1;

    // The key of message stored server returned data.
    private static final String RESPONSE_TEXT = "RESPONSE_TEXT";

    // Request method GET. The value must be uppercase.
    private static final String REQUEST_METHOD_GET = "GET";

    //PLN
    private static final String PLN_SYMBOL = "PLN";

    // Send http request button.
    private Button requestUrlButtonPLN = null;

    private Spinner currencyList = null;

    private TextView responseTextView = null;

    private TextView responseTextViewCurrencyShort = null;

    private String responseTextCurrencyShort = null;

    private Handler uiUpdater = null;

    private String Currency = "";

    private String CurrencyRate = null;

    private String CurrencyBaseShortName = null;

    final String reqUrl = "http://data.fixer.io/api/latest?access_key=dec9cb0c9c4e729d0aa732ccbeb955ee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();


        currencyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {

                } else {
                    Currency = adapterView.getItemAtPosition(i).toString();
                    startSendHttpRequestThread(reqUrl);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    // Initialize app controls.
    @SuppressLint("HandlerLeak")
    private void initControls() {
        if (currencyList == null) {
            currencyList = (Spinner) findViewById(R.id.currency_list);
        }
        if (responseTextView == null) {
            responseTextView = (TextView) findViewById(R.id.http_url_response_text_view);
        }
        if (responseTextViewCurrencyShort == null){
            responseTextViewCurrencyShort = (TextView) findViewById(R.id.baseCurrencyShortName);
        }

        // This handler is used to wait for child thread message to update server response text in TextView.
        uiUpdater = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_CODE_SHOW_RESPONSE_TEXT) {
                    Bundle bundle = msg.getData();
                    if (bundle != null) {

                        String responseText = bundle.getString(RESPONSE_TEXT);
                        if (responseText != null) {
                            try {

                                JSONObject jsonObj = (new JSONObject(responseText)).getJSONObject("rates");

                                CurrencyRate=jsonObj.getString(Currency);

                                responseTextView.setText(CurrencyRate);

                                JSONObject jsonObjBaseShort = new JSONObject(responseText);

                                CurrencyBaseShortName=(String) jsonObjBaseShort.get("base");

                                responseTextViewCurrencyShort.setText(CurrencyBaseShortName);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                }
    }}};
    }


    /* Start a thread to send http request to web server use HttpURLConnection object. */
    private void startSendHttpRequestThread(final String reqUrl)
    {
        Thread sendHttpRequestThread = new Thread()
        {
            @Override
            public void run() {
                // Maintain http url connection.
                HttpURLConnection httpConn = null;

                // Read text input stream.
                InputStreamReader isReader = null;

                // Read text into buffer.
                BufferedReader bufReader = null;

                // Save server response text.
                StringBuffer readTextBuf = new StringBuffer();

                try {
                    // Create a URL object use page url.
                    URL url = new URL(reqUrl);

                    // Open http connection to web server.
                    httpConn = (HttpURLConnection)url.openConnection();

                    // Set http request method to get.
                    httpConn.setRequestMethod(REQUEST_METHOD_GET);

                    // Set connection timeout and read timeout value.
                    httpConn.setConnectTimeout(10000);
                    httpConn.setReadTimeout(10000);

                    // Get input stream from web url connection.
                    InputStream inputStream = httpConn.getInputStream();

                    // Create input stream reader based on url connection input stream.
                    isReader = new InputStreamReader(inputStream);

                    // Create buffered reader.
                    bufReader = new BufferedReader(isReader);

                    // Read line of text from server response.
                    String line = bufReader.readLine();

                    // Loop while return line is not null.
                    while(line != null)
                    {
                        // Append the text to string buffer.
                        readTextBuf.append(line);

                        // Continue to read text line.
                        line = bufReader.readLine();
                    }

                    // Send message to main thread to update response text in TextView after read all.
                    Message message = new Message();

                    // Set message type.
                    message.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;

                    // Create a bundle object.
                    Bundle bundle = new Bundle();
                    // Put response text in the bundle with the special key.
                    bundle.putString(RESPONSE_TEXT, readTextBuf.toString());
                    // Set bundle data in message.
                    message.setData(bundle);
                    // Send message to main thread Handler to process.
                    uiUpdater.sendMessage(message);
                }catch(MalformedURLException ex)
                {
                    Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                }catch(IOException ex)
                {
                    Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                }finally {
                    try {
                        if (bufReader != null) {
                            bufReader.close();
                            bufReader = null;
                        }

                        if (isReader != null) {
                            isReader.close();
                            isReader = null;
                        }

                        if (httpConn != null) {
                            httpConn.disconnect();
                            httpConn = null;
                        }
                    }catch (IOException ex)
                    {
                        Log.e(TAG_HTTP_URL_CONNECTION, ex.getMessage(), ex);
                    }
                }
            }
        };
        // Start the child thread to request web page.
        sendHttpRequestThread.start();
    }
}