package com.abhimanyusharma.inappbilling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.abhimanyusharma.inappbilling.util.IabHelper;
import com.abhimanyusharma.inappbilling.util.IabResult;
import com.abhimanyusharma.inappbilling.util.Inventory;
import com.abhimanyusharma.inappbilling.util.Purchase;

public class InAppBillingActivity extends AppCompatActivity {

    private static final String TAG = "InAppBilling";
    IabHelper mHelper;
    //static final String ITEM_SKU = "YOUR PURCHASE ID";
    static final String ITEM_SKU = "com.abhimanyusharma.inappbilling.pro";

    private Button clickButton;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);

        buyButton = (Button)findViewById(R.id.buyButton);
        clickButton = (Button)findViewById(R.id.clickButton);

        clickButton.setEnabled(false);

        //String base64EncodedPublicKey = "<place your public key here>";
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqJUNhWfqOdIW+isT3MlF/xMCyij5ie+P9drmsGZT53AtdT/+jywTeYQ1KZ3KT75xWpO9IXx5BhhrS3rMk8VyiMcPAhXmRcKimt2wuKN0T/6MmRE/rnPmdjFcjAaXCF5mmCWfFSFFfTc9NGwkG4COr8Y+xAumlJUs6hMHF1xFPjjTE1tMn0qAIIt27iu/hdP1LrZX/kTd3bsYKRpfIEhteaYmODWn3R0yR84C/spEebzPFZ05Ym673++Y5cUnI6vpA237EnpnziKlrB7YlhQbsuKR3Bqu0/n2USxi5igQLpyn6kUp6xgBZaL3q/bocuaflkj7kaI1xNDzczKxvDWWqQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " + result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });
    }

    public void buttonClicked (View view)
    {
        clickButton.setEnabled(false);
        buyButton.setEnabled(true);
    }

    public void buyClick(View view) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
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
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {

                    if (result.isSuccess()) {
                        clickButton.setEnabled(true);
                    } else {
                        // handle error
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

* https://www.lynda.com/Android-tutorials/Welcome/601784/653939-4.html
* https://github.com/anjlab/android-inapp-billing-v3/blob/master/sample/src/com/anjlab/android/iab/v3/sample2/MainActivity.java
*https://github.com/anjlab/android-inapp-billing-v3
* https://www.youtube.com/results?search_query=anjlab+in+app+billing+tutorial
* https://www.youtube.com/watch?v=vOn44fLdGDU&t=412s
* https://www.youtube.com/watch?v=vpnNEGOF3ck

*/