package com.instamojo.mojosdk.fragments;

/**
 * JuspaySafe Brwoser Fragment extending {@link in.juspay.godel.ui.JuspayBrowserFragment}.
 *
 * @author vedhavyas
 * @version 1.0
 * @since 14/03/16
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instamojo.mojosdk.activities.PaymentActivity;
import com.instamojo.mojosdk.network.JavaScriptInterface;

import in.juspay.godel.ui.JuspayBrowserFragment;

public class JusPaySafeBrowser extends JuspayBrowserFragment {

    public static final String URL = "url";
    public static final String MERCHANT_ID = "merchantId";
    public static final String ORDER_ID = "orderId";
    public static final String POST_DATA = "postData";

    /**
     * Creates new instance of Fragment.
     */
    public JusPaySafeBrowser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadWebView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void loadWebView() {
        setupJuspayBackButtonCallbackInterface(new JuspayBackButtonCallback() {
            @Override
            public void transactionCancelled() {
                ((PaymentActivity) getActivity()).returnResult(Activity.RESULT_CANCELED);
            }
        });
        getWebView().addJavascriptInterface(new JavaScriptInterface(getActivity()), "AndroidScriptInterface");
        getWebView().getSettings().setJavaScriptEnabled(true);
    }
}