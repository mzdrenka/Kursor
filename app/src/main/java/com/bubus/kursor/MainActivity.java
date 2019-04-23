package com.bubus.kursor;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.UnderlineSpan;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    // Debug log tag.
    private static final String TAG_HTTP_URL_CONNECTION = "HTTP_URL_CONNECTION";

    // Child thread sent message type value to activity main thread Handler.
    private static final int REQUEST_CODE_SHOW_RESPONSE_TEXT = 1;

    // The key of message stored server returned data.
    private static final String RESPONSE_TEXT = "RESPONSE_TEXT";

    // Request method GET. The value must be uppercase.
    private static final String REQUEST_METHOD_GET = "GET";

    //controls
    private Spinner mainCurencySpiner = null;

    //first currency
    private TextView firstCurrencyRate = null;
    private TextView firstCurrencyTotal = null;
    private Spinner firstCurrencySpiner = null;
    private String firstCurrencyTotalString = null;
    private String firstCurrency = null;
    private String firstCurrencyShort = null;

    //secound currency
    private TextView secondCurrencyRate = null;
    private TextView secondCurrencyTotal = null;
    private Spinner secondCurrencySpiner = null;
    private String secondCurrencyTotalString = null;
    private String secondCurrency = null;
    private String secondCurrencyShort = null;

    //third currency
    private TextView thirdCurrencyRate = null;
    private TextView thirdCurrencyTotal = null;
    private Spinner thirdCurrencySpiner = null;
    private String thirdCurrencyTotalString = null;
    private String thirdCurrency = null;
    private String thirdCurrencyShort = null;

    //fourth currency
    private TextView fourthCurrencyRate = null;
    private TextView fourthCurrencyTotal = null;
    private Spinner fourthCurrencySpiner = null;
    private String fourthCurrencyTotalString = null;
    private String fourthCurrency = null;
    private String fourthCurrencyShort = null;

    //fifth currency
    private TextView fifthCurrencyRate = null;
    private TextView fifthCurrencyTotal = null;
    private Spinner fifthCurrencySpiner = null;
    private String fifthCurrencyTotalString = null;
    private String fifthCurrency = null;
    private String fifthhCurrencyShort = null;

    private EditText currencyBaseTotalAmount = null;

    private Handler uiUpdater1 = null;
    private Handler uiUpdater2 = null;
    private Handler uiUpdater3 = null;
    private Handler uiUpdater4 = null;
    private Handler uiUpdater5 = null;

    private String baseCurrency = "";

    final String reqUrl = "http://data.fixer.io/api/latest?access_key=dec9cb0c9c4e729d0aa732ccbeb955ee";
    String baseCurrencySymbol = "&base=";
    String regUrlToSend = "";

    MyDatabase database;

    DecimalFormat df = new DecimalFormat("#.####");
    DecimalFormat df2 = new DecimalFormat("#,###.##");

    NumberFormat doubleFormat = NumberFormat.getInstance(Locale.FRANCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new MyDatabase(this, 1);

        database.deleteAllRates();

        initControls();

        mainCurencySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    baseCurrency = adapterView.getItemAtPosition(i).toString();

                    regUrlToSend = reqUrl + baseCurrencySymbol + baseCurrency;

                    startSendHttpRequestThread(regUrlToSend);

                 }else{
                    database.deleteAllRates();
                    firstCurrencyRate.setText("0");
                    secondCurrencyRate.setText("0");
                    thirdCurrencyRate.setText("0");
                    fourthCurrencyRate.setText("0");
                    fifthCurrencyRate.setText("0");

                    firstCurrencyTotal.setText("0");
                    secondCurrencyTotal.setText("0");
                    thirdCurrencyTotal.setText("0");
                    fourthCurrencyTotal.setText("0");
                    fifthCurrencyTotal.setText("0");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currencyBaseTotalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0){
                    try {
                        String baseAmount = currencyBaseTotalAmount.getText().toString();
                        Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                        double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                        //first currency changes
                        String firstCurrRate = firstCurrencyRate.getText().toString();
                        Number firstCurrRateNumber = doubleFormat.parse(firstCurrRate);
                        double firstCurrDouble = firstCurrRateNumber.doubleValue();
                        double firstCurrencyTotal = baseAmountDouble * firstCurrDouble;
                        firstCurrencyTotalString = df2.format(firstCurrencyTotal);

                        //secound curency changes
                        String secCurrRate = secondCurrencyRate.getText().toString();
                        Number secCurrNumber = doubleFormat.parse(secCurrRate);
                        double secCurrDouble = secCurrNumber.doubleValue();
                        double secondCurrencyTotal = baseAmountDouble * secCurrDouble;
                        secondCurrencyTotalString = df2.format(secondCurrencyTotal);

                        //third curency changes
                        String thrCurrRate = thirdCurrencyRate.getText().toString();
                        Number thrCurrNumber = doubleFormat.parse(thrCurrRate);
                        double thrCurrDouble = thrCurrNumber.doubleValue();
                        double thirdCurrencyTotal = baseAmountDouble * thrCurrDouble;
                        thirdCurrencyTotalString = df2.format(thirdCurrencyTotal);

                        //fourth currency changes
                        String fourCurrRate = fourthCurrencyRate.getText().toString();
                        Number fourCurrNumber = doubleFormat.parse(fourCurrRate);
                        double fourCurrDouble = fourCurrNumber.doubleValue();
                        double fourCurrencyTotal = baseAmountDouble * fourCurrDouble;
                        fourthCurrencyTotalString = df2.format(fourCurrencyTotal);

                        //fifth currency changes
                        String fiftCurrRate = fourthCurrencyRate.getText().toString();
                        Number fiftCurrNumber = doubleFormat.parse(fiftCurrRate);
                        double fiftCurrDouble = fiftCurrNumber.doubleValue();
                        double fiftCurrencyTotal = baseAmountDouble * fiftCurrDouble;
                        fifthCurrencyTotalString = df2.format(fiftCurrencyTotal);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    firstCurrencyTotal.setText(firstCurrencyTotalString);
                    secondCurrencyTotal.setText(secondCurrencyTotalString);
                    thirdCurrencyTotal.setText(thirdCurrencyTotalString);
                    fourthCurrencyTotal.setText(fourthCurrencyTotalString);
                    fifthCurrencyTotal.setText(fifthCurrencyTotalString);

                }else{
                    firstCurrencyTotal.setText("0");
                    secondCurrencyTotal.setText("0");
                    thirdCurrencyTotal.setText("0");
                    fourthCurrencyTotal.setText("0");
                    fifthCurrencyTotal.setText("0");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

               if(s.length() > 0){

               }
            }
        });

        firstCurrencySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    firstCurrencyShort = adapterView.getItemAtPosition(i).toString();

                    StringBuilder builder = new StringBuilder();
                    Cursor cursor = database.getAllRates();
                    while (cursor.moveToNext()){
                        builder.append(cursor.getString(0));
                    }

                    if (builder.toString() != null)
                    {
                        try {
                            JSONObject json = new JSONObject(builder.toString());
                            firstCurrency = json.getString(firstCurrencyShort);
                            String firstCurrencyDouble = df.format(Double.parseDouble(firstCurrency));
                            firstCurrencyRate.setText(firstCurrencyDouble);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                     if(currencyBaseTotalAmount.length() >0){
                         try {
                             String baseAmount = currencyBaseTotalAmount.getText().toString();
                             Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                             double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                             //first currency changes
                             String firstCurrRate = firstCurrencyRate.getText().toString();
                             Number firstCurrRateNumber = doubleFormat.parse(firstCurrRate);
                             double firstCurrDouble = firstCurrRateNumber.doubleValue();
                             double firstCurrencyTotal = baseAmountDouble * firstCurrDouble;
                             firstCurrencyTotalString = df2.format(firstCurrencyTotal);
                         } catch (ParseException e) {
                             e.printStackTrace();
                         } firstCurrencyTotal.setText(firstCurrencyTotalString);
                     }
                    }
                }else {
                    firstCurrencyRate.setText("0");
                    firstCurrencyShort = null;
                    firstCurrencyTotal.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        secondCurrencySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    secondCurrencyShort = adapterView.getItemAtPosition(i).toString();

                    StringBuilder builder = new StringBuilder();
                    Cursor cursor = database.getAllRates();
                    while (cursor.moveToNext()){
                        builder.append(cursor.getString(0));
                    }

                    if (builder.toString() != null) {
                        try {

                            JSONObject json = new JSONObject(builder.toString());
                            secondCurrency = json.getString(secondCurrencyShort);
                            String secondCurrencyDouble = df.format(Double.parseDouble(secondCurrency));
                            secondCurrencyRate.setText(secondCurrencyDouble);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (currencyBaseTotalAmount.length() > 0) {
                            try {
                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                String secCurrRate = secondCurrencyRate.getText().toString();
                                Number secCurrNumber = doubleFormat.parse(secCurrRate);
                                double secCurrDouble = secCurrNumber.doubleValue();
                                double secondCurrencyTotal = baseAmountDouble * secCurrDouble;
                                secondCurrencyTotalString = df2.format(secondCurrencyTotal);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            secondCurrencyTotal.setText(secondCurrencyTotalString);
                        }
                    }
                }else{
                    secondCurrencyRate.setText("0");
                    secondCurrencyShort = null;
                    secondCurrencyTotal.setText("0");
            }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        thirdCurrencySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    thirdCurrencyShort = adapterView.getItemAtPosition(i).toString();
                    StringBuilder builder = new StringBuilder();
                    Cursor cursor = database.getAllRates();
                    while (cursor.moveToNext()){
                        builder.append(cursor.getString(0));
                    }

                    if (builder.toString() != null)
                    {
                        try {
                            JSONObject json = new JSONObject(builder.toString());
                            thirdCurrency = json.getString(thirdCurrencyShort);
                            String thirdCurrencyDouble = df.format(Double.parseDouble(thirdCurrency));
                            thirdCurrencyRate.setText(thirdCurrencyDouble);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(currencyBaseTotalAmount.length() >0){
                            try {
                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();
                                //first currency changes
                                String thirdCurrRate = thirdCurrencyRate.getText().toString();
                                Number thirdCurrRateNumber = doubleFormat.parse(thirdCurrRate);
                                double thirdCurrDouble = thirdCurrRateNumber.doubleValue();
                                double thirdCurrencyTotal = baseAmountDouble * thirdCurrDouble;
                                thirdCurrencyTotalString = df2.format(thirdCurrencyTotal);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            } thirdCurrencyTotal.setText(thirdCurrencyTotalString);
                        }
                    }
                }else {
                    thirdCurrencyRate.setText("0");
                    thirdCurrencyShort = null;
                    thirdCurrencyTotal.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fourthCurrencySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!= 0){
                    fourthCurrencyShort =  adapterView.getItemAtPosition(i).toString();
                    StringBuilder builder = new StringBuilder();
                    Cursor cursor = database.getAllRates();
                    while (cursor.moveToNext()){
                        builder.append(cursor.getString(0));
                    }
                    if (builder.toString() != null) {
                        try {

                            JSONObject json = new JSONObject(builder.toString());
                            fourthCurrency = json.getString(fourthCurrencyShort);
                            String secondCurrencyDouble = df.format(Double.parseDouble(fourthCurrency));
                            fourthCurrencyRate.setText(secondCurrencyDouble);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (currencyBaseTotalAmount.length() > 0) {
                            try {
                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                String fourthCurrRate = fourthCurrencyRate.getText().toString();
                                Number fourthCurrNumber = doubleFormat.parse(fourthCurrRate);
                                double fourthCurrDouble = fourthCurrNumber.doubleValue();
                                double fourthCurrencyTotal = baseAmountDouble * fourthCurrDouble;
                                fourthCurrencyTotalString = df2.format(fourthCurrencyTotal);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            fourthCurrencyTotal.setText(fourthCurrencyTotalString);
                        }
                    }
                }else{
                    fourthCurrencyRate.setText("0");
                    fourthCurrencyShort = null;
                    fourthCurrencyTotal.setText("0");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fifthCurrencySpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!= 0){
                    fifthhCurrencyShort =  adapterView.getItemAtPosition(i).toString();
                    StringBuilder builder = new StringBuilder();
                    Cursor cursor = database.getAllRates();
                    while (cursor.moveToNext()){
                        builder.append(cursor.getString(0));
                    }
                    if (builder.toString() != null) {
                        try {

                            JSONObject json = new JSONObject(builder.toString());
                            fifthCurrency = json.getString(fifthhCurrencyShort);
                            String fifthCurrencyDouble = df.format(Double.parseDouble(fifthCurrency));
                            fifthCurrencyRate.setText(fifthCurrencyDouble);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (currencyBaseTotalAmount.length() > 0) {
                            try {
                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                String fifthCurrRate = fifthCurrencyRate.getText().toString();
                                Number fifthCurrNumber = doubleFormat.parse(fifthCurrRate);
                                double fifthCurrDouble = fifthCurrNumber.doubleValue();
                                double fifthCurrencyTotal = baseAmountDouble * fifthCurrDouble;
                                fifthCurrencyTotalString = df2.format(fifthCurrencyTotal);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            fifthCurrencyTotal.setText(fifthCurrencyTotalString);
                        }
                    }
                }else{
                    fifthCurrencyRate.setText("0");
                    fifthhCurrencyShort = null;
                    fifthCurrencyTotal.setText("0");
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

        if (mainCurencySpiner == null) {
            mainCurencySpiner = (Spinner) findViewById(R.id.main_Curency_Spiner);
        }

        if (currencyBaseTotalAmount == null) {
            currencyBaseTotalAmount = (EditText) findViewById(R.id.currencyBaseTotalAmount);
            char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
            currencyBaseTotalAmount.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));
            currencyBaseTotalAmount.addTextChangedListener(new NumberTextWatcher(currencyBaseTotalAmount));

        }

        if (firstCurrencySpiner == null) {
            firstCurrencySpiner = (Spinner) findViewById(R.id.first_currency_list);
        }

        if (firstCurrencyRate == null) {
            firstCurrencyRate = (TextView) findViewById(R.id.first_currency_rate);
            firstCurrencyRate.setTextIsSelectable(true);
        }

        if (firstCurrencyTotal == null) {
            firstCurrencyTotal = (TextView) findViewById(R.id.firstCurrencyTotal);
            firstCurrencyTotal.setTextIsSelectable(true);
        }

        if (secondCurrencySpiner == null) {
            secondCurrencySpiner = (Spinner) findViewById(R.id.second_currency_list);
        }

        if (secondCurrencyRate == null) {
            secondCurrencyRate = (TextView) findViewById(R.id.second_currency_rate);
            secondCurrencyRate.setTextIsSelectable(true);
        }

        if (secondCurrencyTotal == null) {
            secondCurrencyTotal = (TextView) findViewById(R.id.secoundCurrencyTotal);
            secondCurrencyTotal.setTextIsSelectable(true);
        }

        if (thirdCurrencySpiner == null) {
            thirdCurrencySpiner = (Spinner) findViewById(R.id.third_currency_list);
        }

        if (thirdCurrencyRate == null ) {
            thirdCurrencyRate = (TextView) findViewById(R.id.third_currency_rate);
            thirdCurrencyRate.setTextIsSelectable(true);
        }

        if (thirdCurrencyTotal == null) {
            thirdCurrencyTotal = (TextView) findViewById(R.id.thirdCurrencyTotal);
            thirdCurrencyTotal.setTextIsSelectable(true);
        }

        //fourth

        if (fourthCurrencySpiner == null) {
            fourthCurrencySpiner = (Spinner) findViewById(R.id.fourth_currency_list);
        }

        if (fourthCurrencyRate == null ) {
            fourthCurrencyRate = (TextView) findViewById(R.id.fourth_currency_rate);
            fourthCurrencyRate.setTextIsSelectable(true);
        }

        if (fourthCurrencyTotal == null) {
            fourthCurrencyTotal = (TextView) findViewById(R.id.fourthCurrencyTotal);
            fourthCurrencyTotal.setTextIsSelectable(true);
        }

        //fifth

        if (fifthCurrencySpiner == null) {
            fifthCurrencySpiner = (Spinner) findViewById(R.id.fifth_currency_list);
        }

        if (fifthCurrencyRate == null ) {
            fifthCurrencyRate = (TextView) findViewById(R.id.fifth_currency_rate);
            fifthCurrencyRate.setTextIsSelectable(true);
        }

        if (fifthCurrencyTotal == null) {
            fifthCurrencyTotal = (TextView) findViewById(R.id.fifthCurrencyTotal);
            fifthCurrencyTotal.setTextIsSelectable(true);
        }

        // This handler is used to wait for child thread message to update server response text in TextView.
        uiUpdater1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_CODE_SHOW_RESPONSE_TEXT) {
                    Bundle bundle = msg.getData();

                    if (bundle != null) {

                        String responseText = bundle.getString(RESPONSE_TEXT);
                        if (responseText != null) {
                            try {

                                JSONObject jsonObj = (new JSONObject(responseText)).getJSONObject("rates");

                                database.addRates(jsonObj.toString());

                                    if (jsonObj.getString(firstCurrencyShort) != null) {

                                        firstCurrency = jsonObj.getString(firstCurrencyShort);

                                        String firstCurrencyDouble = df.format(Double.parseDouble(firstCurrency));

                                        firstCurrencyRate.setText(firstCurrencyDouble);

                                        if (currencyBaseTotalAmount.length() > 0) {
                                            try {
                                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();
                                                //first currency changes
                                                String firstCurrRate = firstCurrencyRate.getText().toString();
                                                Number firstCurrRateNumber = doubleFormat.parse(firstCurrRate);
                                                double firstCurrDouble = firstCurrRateNumber.doubleValue();
                                                double firstCurrencyTotal = baseAmountDouble * firstCurrDouble;
                                                firstCurrencyTotalString = df2.format(firstCurrencyTotal);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            firstCurrencyTotal.setText(firstCurrencyTotalString);
                                        } else firstCurrencyTotal.setText("0");
                                    } else {
                                        firstCurrencyRate.setText("0");
                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }}}}}};


        uiUpdater2 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_CODE_SHOW_RESPONSE_TEXT) {
                    Bundle bundle = msg.getData();

                    if (bundle != null) {

                        String responseText = bundle.getString(RESPONSE_TEXT);
                        if (responseText != null) {
                            try {

                                JSONObject jsonObj = (new JSONObject(responseText)).getJSONObject("rates");

                                database.addRates(jsonObj.toString());

                                    if (jsonObj.getString(secondCurrencyShort) != null) {

                                        secondCurrency = jsonObj.getString(secondCurrencyShort);

                                        String secondCurrencyDouble = df.format(Double.parseDouble(secondCurrency));

                                        secondCurrencyRate.setText(secondCurrencyDouble);

                                        if (currencyBaseTotalAmount.length() > 0) {
                                            try {

                                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                                String secCurrRate = secondCurrencyRate.getText().toString();
                                                Number secCurrNumber = doubleFormat.parse(secCurrRate);
                                                double secCurrDouble = secCurrNumber.doubleValue();
                                                double secondCurrencyTotal = baseAmountDouble * secCurrDouble;
                                                secondCurrencyTotalString = df2.format(secondCurrencyTotal);

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            secondCurrencyTotal.setText(secondCurrencyTotalString);
                                        }

                                    } else {
                                        secondCurrencyRate.setText("0");
                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }}}}}};

        uiUpdater3= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_CODE_SHOW_RESPONSE_TEXT) {
                    Bundle bundle = msg.getData();

                    if (bundle != null) {

                        String responseText = bundle.getString(RESPONSE_TEXT);
                        if (responseText != null) {
                            try {

                                JSONObject jsonObj = (new JSONObject(responseText)).getJSONObject("rates");

                                database.addRates(jsonObj.toString());

                                    if (jsonObj.getString(thirdCurrencyShort) != null) {

                                        thirdCurrency = jsonObj.getString(thirdCurrencyShort);

                                        String thirdCurrencyDouble = df.format(Double.parseDouble(thirdCurrency));

                                        thirdCurrencyRate.setText(thirdCurrencyDouble);

                                        if (currencyBaseTotalAmount.length() > 0) {

                                            try {
                                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                                String thirdCurrRate = thirdCurrencyRate.getText().toString();
                                                Number thirdCurrNumber = doubleFormat.parse(thirdCurrRate);
                                                double thirdCurrDouble = thirdCurrNumber.doubleValue();
                                                double thirdCurrencyTotal = baseAmountDouble * thirdCurrDouble;
                                                thirdCurrencyTotalString = df2.format(thirdCurrencyTotal);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            thirdCurrencyTotal.setText(thirdCurrencyTotalString);
                                        } else {
                                            thirdCurrencyTotal.setText("0");
                                        }
                                    }
        } catch (JSONException e) {
            e.printStackTrace();
        }}}}}};


        uiUpdater4 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_CODE_SHOW_RESPONSE_TEXT) {
                    Bundle bundle = msg.getData();

                    if (bundle != null) {

                        String responseText = bundle.getString(RESPONSE_TEXT);
                        if (responseText != null) {
                            try {

                                JSONObject jsonObj = (new JSONObject(responseText)).getJSONObject("rates");

                                database.addRates(jsonObj.toString());

                                    if (jsonObj.getString(fourthCurrencyShort) != null) {

                                        fourthCurrency = jsonObj.getString(fourthCurrencyShort);

                                        String fourthCurrencyDouble = df.format(Double.parseDouble(fourthCurrency));

                                        fourthCurrencyRate.setText(fourthCurrencyDouble);

                                        if (currencyBaseTotalAmount.length() > 0) {

                                            try {
                                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                                String fourthCurrRate = fourthCurrencyRate.getText().toString();
                                                Number fourthCurrNumber = doubleFormat.parse(fourthCurrRate);
                                                double fourthCurrDouble = fourthCurrNumber.doubleValue();
                                                double fourthCurrencyTotal = baseAmountDouble * fourthCurrDouble;
                                                fourthCurrencyTotalString = df2.format(fourthCurrencyTotal);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            fourthCurrencyTotal.setText(thirdCurrencyTotalString);
                                        } else {
                                            fourthCurrencyTotal.setText("0");
                                        }
                                    }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }}}}}};


        uiUpdater5 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REQUEST_CODE_SHOW_RESPONSE_TEXT) {
                Bundle bundle = msg.getData();

                if (bundle != null) {

                    String responseText = bundle.getString(RESPONSE_TEXT);
                    if (responseText != null) {
                        try {

                            JSONObject jsonObj = (new JSONObject(responseText)).getJSONObject("rates");

                            database.addRates(jsonObj.toString());


                                    if (jsonObj.getString(fifthhCurrencyShort) != null) {

                                        fifthCurrency = jsonObj.getString(fifthhCurrencyShort);

                                        String fifthCurrencyDouble = df.format(Double.parseDouble(fifthCurrency));

                                        fifthCurrencyRate.setText(fifthCurrencyDouble);

                                        if (currencyBaseTotalAmount.length() > 0) {

                                            try {
                                                String baseAmount = currencyBaseTotalAmount.getText().toString();
                                                Number baseAmountDoubleNumber =  doubleFormat.parse(baseAmount);
                                                double baseAmountDouble = baseAmountDoubleNumber.doubleValue();

                                                String fifthCurrRate = fifthCurrencyRate.getText().toString();
                                                Number fifthCurrNumber = doubleFormat.parse(fifthCurrRate);
                                                double fifthCurrDouble = fifthCurrNumber.doubleValue();
                                                double fifthCurrencyTotal = baseAmountDouble * fifthCurrDouble;
                                                fifthCurrencyTotalString = df2.format(fifthCurrencyTotal);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            fifthCurrencyTotal.setText(fifthCurrencyTotalString);
                                        } else {
                                            fifthCurrencyTotal.setText("0");
                                        }
                                    }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }}}}}};}


    /* Start a thread to send http request to web server use HttpURLConnection object. */
    private void startSendHttpRequestThread(final String regUrlToSend)
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
                    URL url = new URL(regUrlToSend);

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
                    Message message1 = new Message();
                    Message message2 = new Message();
                    Message message3 = new Message();
                    Message message4 = new Message();
                    Message message5 = new Message();

                    // Set message type.
                    message1.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;
                    message2.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;
                    message3.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;
                    message4.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;
                    message5.what = REQUEST_CODE_SHOW_RESPONSE_TEXT;

                    // Create a bundle object.
                    Bundle bundle = new Bundle();
                    // Put response text in the bundle with the special key.
                    bundle.putString(RESPONSE_TEXT, readTextBuf.toString());
                    // Set bundle data in message.
                    message1.setData(bundle);
                    message2.setData(bundle);
                    message3.setData(bundle);
                    message4.setData(bundle);
                    message5.setData(bundle);
                    // Send message to main thread Handler to process.
                    uiUpdater1.sendMessage(message1);
                    uiUpdater2.sendMessage(message2);
                    uiUpdater3.sendMessage(message3);
                    uiUpdater4.sendMessage(message4);
                    uiUpdater5.sendMessage(message5);

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