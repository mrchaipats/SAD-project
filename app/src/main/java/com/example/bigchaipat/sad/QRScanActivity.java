package com.example.bigchaipat.sad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class QRScanActivity extends AppCompatActivity {

    TextView tvMenuName;
    TextView tvMenuPrice;
    TextView tvShopId;
    Button scanQRBtn;
    Button purchaseBtn;
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        tvMenuName = findViewById(R.id.menuName);
        tvMenuPrice = findViewById(R.id.menuPrice);
        tvShopId = findViewById(R.id.shopId);
        scanQRBtn = findViewById(R.id.scanQR);
        purchaseBtn = findViewById(R.id.purchase);

        scanQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(QRScanActivity.this).initiateScan();
            }
        });

        purchaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    System.out.println(obj.getString("menuName"));
                    Purchase(view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                try {
                    obj = new JSONObject(result.getContents());
                    tvMenuName.setText(obj.getString("menuName"));
                    tvMenuPrice.setText(obj.getString("menuPrice"));
                    tvShopId.setText(obj.getString("shopId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void Purchase(View view) throws JSONException {
        /*
        PaymentRestClient.getByUrl("https://httpbin.org/getdsds", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println(" ************** Success **************");
            }
        });
        */
        RequestParams rp = new RequestParams();
        rp.add("userId", obj.getString("menuName"));
        rp.add("productIdList", obj.getString("menuPrice"));
        //rp.add("shopId", obj.getString("shopId"));
        PaymentRestClient.postByUrl("http://203.159.47.144/orders", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                System.out.println("*************** Success Post **************");
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    System.out.println(serverResp);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }


}
