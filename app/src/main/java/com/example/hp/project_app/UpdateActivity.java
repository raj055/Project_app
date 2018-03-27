package com.example.hp.project_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

/**
 * @author Grasp
 *  @version 1.0 on 09-03-2018.
 */

public class UpdateActivity extends AppCompatActivity {

    String HttpURL = "http://pcddata-001-site1.1tempurl.com/updateclientdetails.php";
    ProgressDialog progressDialog;
    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    EditText ClientName, ClientAddress,ClientAddressline1,ClientAddressline2,ClientMobileno,ClientState,
            ClientCountry, ClientEmail, ClientCompany,ClientPin, ClientDesignation;
    Button UpdateStudent;
    String ClientIdHolder,ClientNameHolder,ClientAddressHolder,ClientAddressline1Holder,ClientAddressline2Holder,
            ClientMobilenoHolder,ClientStateHolder,ClientCountryHolder, ClientEmailHolder,ClientPinoHolder,
            ClientComapnyHolder, ClientDesignationHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ClientName = (EditText)findViewById(R.id.editName);
        ClientAddress = (EditText)findViewById(R.id.editAddress);
        ClientAddressline1 = (EditText)findViewById(R.id.editAddressline1);
        ClientAddressline2 = (EditText)findViewById(R.id.editAddressline2);
        ClientMobileno = (EditText)findViewById(R.id.editMobileno);
        ClientState = (EditText)findViewById(R.id.editState);
        ClientCountry = (EditText)findViewById(R.id.editCountry);
        ClientEmail = (EditText)findViewById(R.id.editEmail);
        ClientPin= (EditText)findViewById(R.id.editPin);
        ClientCompany = (EditText)findViewById(R.id.editCompany);
        ClientDesignation = (EditText)findViewById(R.id.editDesignation);

        UpdateStudent = (Button)findViewById(R.id.UpdateButton);

        // Receive Client ID, Name , Address , Email, etc.. Send by previous ShowSingleRecordActivity.
        ClientIdHolder = getIntent().getStringExtra("id");
        ClientNameHolder = getIntent().getStringExtra("name");
        ClientAddressHolder = getIntent().getStringExtra("address");
        ClientAddressline1Holder = getIntent().getStringExtra("addressline1");
        ClientAddressline2Holder = getIntent().getStringExtra("addressline2");
        ClientMobilenoHolder = getIntent().getStringExtra("mobileno");
        ClientStateHolder = getIntent().getStringExtra("state");
        ClientCountryHolder = getIntent().getStringExtra("country");
        ClientPinoHolder = getIntent().getStringExtra("pin");
        ClientComapnyHolder = getIntent().getStringExtra("company");
        ClientEmailHolder = getIntent().getStringExtra("email_id");
        ClientDesignationHolder = getIntent().getStringExtra("designation");

        // Setting Received Student Name, Phone Number, Class into EditText.
        ClientName.setText(ClientNameHolder);
        ClientAddress.setText(ClientAddressHolder);
        ClientAddressline1.setText(ClientAddressline1Holder);
        ClientAddressline2.setText(ClientAddressline2Holder);
        ClientMobileno.setText(ClientMobilenoHolder);
        ClientState.setText(ClientStateHolder);
        ClientCountry.setText(ClientCountryHolder);
        ClientEmail.setText(ClientEmailHolder);
        ClientPin.setText(ClientPinoHolder);
        ClientCompany.setText(ClientComapnyHolder);
        ClientDesignation.setText(ClientDesignationHolder);

        // Adding click listener to update button .
        UpdateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Getting data from EditText after button click.
                GetDataFromEditText();

                ClientRecordUpdate (ClientIdHolder,ClientNameHolder,ClientAddressHolder,ClientAddressline1Holder,
                        ClientAddressline2Holder, ClientMobilenoHolder,ClientStateHolder, ClientCountryHolder,
                        ClientEmailHolder,ClientPinoHolder, ClientComapnyHolder, ClientDesignationHolder);

                Intent intent = new Intent(UpdateActivity.this,ClientDetailsActivity.class);
                startActivity(intent);
            }
        });

    }

    // Method to get existing data from EditText.
    public void GetDataFromEditText(){

        ClientNameHolder = ClientName.getText().toString();
        ClientAddressHolder = ClientAddress.getText().toString();
        ClientAddressline1Holder = ClientAddressline1.getText().toString();
        ClientAddressline2Holder = ClientAddressline2.getText().toString();
        ClientMobilenoHolder = ClientMobileno.getText().toString();
        ClientStateHolder = ClientState.getText().toString();
        ClientCountryHolder = ClientCountry.getText().toString();
        ClientEmailHolder = ClientEmail.getText().toString();
        ClientPinoHolder = ClientPin.getText().toString();
        ClientComapnyHolder = ClientCompany.getText().toString();
        ClientDesignationHolder = ClientDesignation.getText().toString();

    }

    // Method to Update Student Record.
    public void ClientRecordUpdate( String ClientIdHolder, String ClientNameHolder, String ClientAddressHolder, String ClientAddressline1Holder,
                                    String ClientAddressline2Holder, String ClientMobilenoHolder,
                                    String ClientStateHolder, String ClientCountryHolder, String ClientEmailHolder,
                                   String ClientPinoHolder, String ClientComapnyHolder, String ClientDesignationHolder){

        class ClientRecordUpdateClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(UpdateActivity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(UpdateActivity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("id",params[0]);

                hashMap.put("name",params[1]);

                hashMap.put("address",params[2]);

                hashMap.put("addressline1",params[3]);

                hashMap.put("addressline2",params[4]);

                hashMap.put("mobileno",params[5]);

                hashMap.put("state",params[6]);

                hashMap.put("country",params[7]);

                hashMap.put("company",params[8]);

                hashMap.put("pin",params[9]);

                hashMap.put("email_id",params[10]);

                hashMap.put("designation",params[11]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        ClientRecordUpdateClass clientRecordUpdateClass = new ClientRecordUpdateClass();

        clientRecordUpdateClass.execute(ClientIdHolder,ClientNameHolder, ClientAddressHolder, ClientAddressline1Holder,
                                         ClientAddressline2Holder, ClientMobilenoHolder,
                                         ClientStateHolder, ClientCountryHolder, ClientEmailHolder,
                                         ClientPinoHolder, ClientComapnyHolder, ClientDesignationHolder);
    }
}
