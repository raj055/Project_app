package com.example.hp.project_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @author Grasp
 *  @version 1.0 on 09-03-2018.
 */

public class Invoice extends AppCompatActivity {

    TextView name,address,company,country;
    TextView item,hsn,gst,price,quantity;
    TextView date,validdate;

    public static int REQUEST_PERMISSIONS = 1;
    ConstraintLayout cl_pdflayout;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice);

        date = (TextView)findViewById(R.id.date_tv);
        validdate = (TextView)findViewById(R.id.validdate_tv);
        quantity = (TextView)findViewById(R.id.tvquantity);

        name = (TextView)findViewById(R.id.textView18);
        address = (TextView)findViewById(R.id.textView19);
        company = (TextView)findViewById(R.id.textView22);
        country = (TextView)findViewById(R.id.textView21);

        item = (TextView)findViewById(R.id.tvproduct);
        hsn = (TextView)findViewById(R.id.tvhsn);
        gst = (TextView)findViewById(R.id.tvgst);
        price = (TextView)findViewById(R.id.tvprice);

        cl_pdflayout = (ConstraintLayout) findViewById(R.id.cl_pdf);

        String str;
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                // Client
                str = extras.getString("name");
                name.setText(str);
                str = extras.getString("address");
                String str1 = extras.getString("ad1");
                String str2 = extras.getString("ad2");
                String str3 = extras.getString("pin");
                String str4 = extras.getString("state");
                address.setText(str + "\n" + str1 + "\n" + str2 + "\n" + str3 +"\n" + str4);
                str = extras.getString("company");
                company.setText(str);
                str = extras.getString("country");
                country.setText(str);

                //Product
                str = extras.getString("pname");
                item.setText(str);
                String str10 = extras.getString("phsn");
                hsn.setText(str10);
                str = extras.getString("pgst");
                gst.setText(str);
                str = extras.getString("pprice");
                price.setText(str);

                str = extras.getString("proqunt");
                quantity.setText(str);
                str = extras.getString("date");
                date.setText(str);
                str = extras.getString("validdate");
                validdate.setText(str);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, menuIconWithText(getResources().getDrawable(R.drawable.address), getResources().getString(R.string.action_address)));
        menu.add(0, 2, 2, menuIconWithText(getResources().getDrawable(R.drawable.edit), getResources().getString(R.string.action_edit)));
        menu.add(0, 3, 3, menuIconWithText(getResources().getDrawable(R.drawable.pdf), getResources().getString(R.string.action_pdf)));
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Toast.makeText(Invoice.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Intent intent = new Intent(Invoice.this, CreateQuotation.class);

                startActivity(intent);

                finish();

                break;
            case 3:
                fn_permission();
                if (boolean_save) {
                    Intent intent1 = new Intent(getApplicationContext(), PDFViewActivity.class);
                    startActivity(intent1);

                }
                if (boolean_permission) {
                    progressDialog = new ProgressDialog(Invoice.this);
                    progressDialog.setMessage("Please wait");
                    bitmap = loadBitmapFromView(cl_pdflayout, cl_pdflayout.getWidth(), cl_pdflayout.getHeight());
                    createPdf();

                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/invoice.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(Invoice.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(Invoice.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(Invoice.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(Invoice.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            boolean_permission = true;

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;

            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Invoice.this, AdminDashboard.class);
        startActivity(intent);

        finish();

    }
}
