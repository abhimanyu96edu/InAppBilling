package com.abhimanyusharma.inappbilling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.abhimanyusharma.inappbilling.util.IabHelper;
import com.abhimanyusharma.inappbilling.util.IabResult;
import com.abhimanyusharma.inappbilling.util.Inventory;
import com.abhimanyusharma.inappbilling.util.Purchase;

public class InAppBillingActivity extends AppCompatActivity {

    private static final String TAG = "InAppBilling";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";
    final String base64EncodedPublicKey = "null";

    private Button clickButton;
    private Button buyButton;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);

        buyButton = (Button)findViewById(R.id.buyButton);
        clickButton = (Button)findViewById(R.id.clickButton);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), InAppBillingAnjlab1.class));
                //finish();
            }
        });

        clickButton.setEnabled(false);

        //String base64EncodedPublicKey = "<place your public key here>";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        Log.d(TAG, "Starting setup.");

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " + result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                               mHelper.enableDebugLogging(true, TAG);
                                           }
                                       }
                                   });
    }

    public void buttonClicked (View view)
    {
        //clickButton.setEnabled(false);
        //buyButton.setEnabled(true);

        Toast.makeText(getApplicationContext(), "PURCHASE COMPLETE", Toast.LENGTH_SHORT).show();
    }

    public void buyClick(View view) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error

                Toast.makeText(getApplicationContext(), "Payment Failed1", Toast.LENGTH_SHORT).show();

                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();
                buyButton.setEnabled(false);
            }

        }
    };
    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure

                Toast.makeText(getApplicationContext(), "Payment Failed2", Toast.LENGTH_SHORT).show();


            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {

                    if (result.isSuccess()) {
                        buyButton.setEnabled(false);
                        clickButton.setEnabled(true);
                    } else {
                        // handle error

                        Toast.makeText(getApplicationContext(), "Payment Failed3", Toast.LENGTH_SHORT).show();

                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}

/*

* http://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial
* https://www.lynda.com/Android-tutorials/Welcome/601784/653939-4.html
* https://github.com/anjlab/android-inapp-billing-v3/blob/master/sample/src/com/anjlab/android/iab/v3/sample2/MainActivity.java
*https://github.com/anjlab/android-inapp-billing-v3
* https://www.youtube.com/results?search_query=anjlab+in+app+billing+tutorial
* https://www.youtube.com/watch?v=vOn44fLdGDU&t=412s
* https://www.youtube.com/watch?v=vpnNEGOF3ck

*/