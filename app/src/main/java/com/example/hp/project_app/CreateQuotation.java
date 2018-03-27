package com.example.hp.project_app;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Grasp
 *  @version 1.0 on 09-03-2018.
 */

public class CreateQuotation extends AppCompatActivity {

    private EditText quantity;
    private Button add_client,add_product,preview,date,validdate;
    public TextView client,product,textdate,textvaliddate;

    GlobalVariable globalVariable;

    private int year;
    private int month;
    private int day;

    static final int DATE_PICKER_ID = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        globalVariable = GlobalVariable.getInstance();

        quantity = (EditText) findViewById(R.id.quantity_et);
        client = (TextView) findViewById(R.id.tv_client);
        product = (TextView) findViewById(R.id.tv_product);
        textdate = (TextView) findViewById(R.id.tv_date);
        textvaliddate = (TextView) findViewById(R.id.tv_uptodate);

        add_client = (Button) findViewById(R.id.btn_client);
        add_product = (Button) findViewById(R.id.btn_product);
        preview = (Button) findViewById(R.id.btn_preview);
        date = (Button) findViewById(R.id.btn_date);
        validdate = (Button) findViewById(R.id.btn_validupto);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        textdate.setText(new StringBuilder()
                .append(day).append("-")
                .append(month + 1).append("-")
                .append(year).append(" "));


        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                // Client Details
                if (extras.containsKey("address"))
                {
                    globalVariable.globalClient[0] = extras.getString("address");
                    globalVariable.globalClient[1] = extras.getString("ad1");
                    globalVariable.globalClient[2] = extras.getString("ad2");
                    globalVariable.globalClient[3] = extras.getString("pin");
                    globalVariable.globalClient[4] = extras.getString("state");
                    globalVariable.globalClient[5] = extras.getString("country");
                    globalVariable.globalClient[6] = extras.getString("company");
                    globalVariable.globalClient[7] = extras.getString("name");
                }
                client.setText(globalVariable.globalClient[7]);

                //Product Details
                if (extras.containsKey("pname"))
                {
                    globalVariable.globalProduct[0] = extras.getString("pname");
                    globalVariable.globalProduct[1] = extras.getString("phsn");
                    globalVariable.globalProduct[2] = extras.getString("pgst");
                    globalVariable.globalProduct[3] = extras.getString("pprice");
                }
                product.setText(globalVariable.globalProduct[0]);
            }
        }

        // Client add in database
        add_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreateQuotation.this, SelectClient.class);

                startActivity(intent);
            }

        });

        // Product add in database
        add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreateQuotation.this, SelectProduct.class);

                startActivity(intent);
            }
        });

        // Date add
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog(DATE_PICKER_ID);
            }
        });

        // Valid up to Date
        validdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });


        // Preview add in database
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreateQuotation.this, Invoice.class);

                //customer
                intent.putExtra("name", globalVariable.globalClient[7]);
                intent.putExtra("address",  globalVariable.globalClient[0]);
                intent.putExtra("ad1",  globalVariable.globalClient[1]);
                intent.putExtra("ad2",  globalVariable.globalClient[2]);
                intent.putExtra("pin",  globalVariable.globalClient[3]);
                intent.putExtra("state",  globalVariable.globalClient[4]);
                intent.putExtra("country",  globalVariable.globalClient[5]);
                intent.putExtra("company",  globalVariable.globalClient[6]);

                //product
                intent.putExtra("pname", globalVariable.globalProduct[0]);
                intent.putExtra("phsn", globalVariable.globalProduct[1]);
                intent.putExtra("pgst", globalVariable.globalProduct[2]);
                intent.putExtra("pprice", globalVariable.globalProduct[3]);

                intent.putExtra("proqunt", quantity.getText());
                intent.putExtra("date", textdate.getText());
                intent.putExtra("validdate", textvaliddate.getText());

                startActivity(intent);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            textvaliddate.setText(new StringBuilder()
                    .append(day).append("-")
                    .append(month + 1).append("-")
                    .append(year).append(" "));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.settings), getResources().getString(R.string.action_settings)));
        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

}
