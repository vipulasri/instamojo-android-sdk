package com.instamojo.android.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.instamojo.android.R;
import com.instamojo.android.fragments.BaseFragment;
import com.instamojo.android.fragments.PaymentOptionsFragment;
import com.instamojo.android.fragments.NetBankingFragment;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.helpers.Logger;
import com.instamojo.android.models.Order;

/**
 * Payment Details Activity extends the {@link BaseActivity}. Activity lets user to choose a Payment method
 */
public class PaymentDetailsActivity extends BaseActivity {

    private static final String TAG = PaymentDetailsActivity.class.getSimpleName();
    private Order order;
    private boolean showSearch;
    private SearchView.OnQueryTextListener onQueryTextListener;
    private NetBankingFragment.Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details_instamojo);
        inflateXML();
        loadFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (showSearch) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_payment_options, menu);

            SearchManager searchManager = (SearchManager)
                    getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchMenuItem = menu.findItem(R.id.search);
            SearchView searchView = (SearchView) searchMenuItem.getActionView();
            if (mode == NetBankingFragment.Mode.NetBanking) {
                searchView.setQueryHint(getString(R.string.search_your_bank));
            } else {
                searchView.setQueryHint(getString(R.string.search_your_wallet));
            }

            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            if (onQueryTextListener != null) {
                searchView.setOnQueryTextListener(onQueryTextListener);
            }
        }

        Logger.d(TAG, "Inflated Options Menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start the payment activity with given bundle
     *
     * @param bundle Bundle with either card/netbanking url
     */
    public void startPaymentActivity(Bundle bundle) {
        Logger.d(TAG, "Starting PaymentActivity with given Bundle");
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra(Constants.PAYMENT_BUNDLE, bundle);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            returnResult(RESULT_CANCELED);
        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE) {
            Logger.d(TAG, "Returning back result to caller");
            returnResult(data.getExtras(), resultCode);
        }
    }

    private void inflateXML() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateActionBar();
        Logger.d(TAG, "Inflated XML");
    }

    /**
     * @return The current Order
     */
    public Order getOrder() {
        return order;
    }

    private void loadFragments() {
        Logger.d(TAG, "looking for Order object...");
        order = getIntent().getParcelableExtra(Constants.ORDER);
        if (order == null) {
            Logger.e(TAG, "Object not found. Sending back - Payment Cancelled");
            returnResult(RESULT_CANCELED);
            return;
        }
        Logger.d(TAG, "Found order Object. Starting PaymentOptionsFragment");
        loadFragment(new PaymentOptionsFragment(), false);
    }

    /**
     * Load the given fragment to the support fragment manager
     *
     * @param fragment       Fragment to be added. Must be a subclass of {@link BaseFragment}
     * @param addToBackStack Whether to add this fragment to back stack
     */
    public void loadFragment(BaseFragment fragment, boolean addToBackStack) {
        Logger.d(TAG, "Loading Fragment - " + fragment.getClass().getSimpleName());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.container, fragment);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getFragmentName());
        }
        fragmentTransaction.commit();
        Logger.d(TAG, "Loaded Fragment - " + fragment.getClass().getSimpleName());
    }

    /**
     * Show the search icon in the actionbar
     *
     * @param showSearch        Show the search icon in action action bar
     * @param queryTextListener {@link android.support.v7.widget.SearchView.OnQueryTextListener} to listen for the query string
     */
    public void setShowSearch(boolean showSearch, SearchView.OnQueryTextListener queryTextListener, NetBankingFragment.Mode mode) {
        this.mode = mode;
        this.showSearch = showSearch;
        this.onQueryTextListener = queryTextListener;
        invalidateOptionsMenu();
        Logger.d(TAG, "Invalidating search option for Net banking");
    }
}
