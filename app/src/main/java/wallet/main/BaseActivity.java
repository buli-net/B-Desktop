package wallet.main;

import android.graphics.Insets;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;

import wallet.ui.TextViewUtils;

import androidx.appcompat.app.AppCompatActivity;

/** Applies system-bar insets to the existing app content on Android 15+. */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPostCreate(Bundle state) {
        super.onPostCreate(state);
        bindDesktopNavigation();
        applySystemBarInsets();
        TextViewUtils.configureLongValueViews(findViewById(android.R.id.content));
    }

    private void bindDesktopNavigation() {
        if (this instanceof MainActivity) return;
        View overview = findViewById(R.id.desktopOverviewTab);
        if (overview == null) return;

        selectNavigationTab();
        overview.setOnClickListener(v -> openDesktopDestination(MainActivity.class));
        findViewById(R.id.desktopSendTab).setOnClickListener(v ->
                openDesktopDestination(wallet.send.SendActivity.class));
        findViewById(R.id.desktopReceiveTab).setOnClickListener(v ->
                openDesktopDestination(wallet.request.RequestCoinsActivity.class));
        findViewById(R.id.desktopTransactionsTab).setOnClickListener(v ->
                openDesktopDestination(MainActivity.class));
        findViewById(R.id.desktopAddressBookTab).setOnClickListener(v -> {
            if (this instanceof wallet.send.SendActivity) {
                View addressBookButton = findViewById(R.id.addressBookButton);
                if (addressBookButton != null) {
                    addressBookButton.performClick();
                    return;
                }
            }
            openDesktopDestination(wallet.contacts.AddressBookActivity.class);
        });
        findViewById(R.id.desktopNetworkTab).setOnClickListener(v ->
                openDesktopDestination(SyncActivity.class));
    }

    private void selectNavigationTab() {
        int selectedId = 0;
        if (this instanceof wallet.send.SendActivity) selectedId = R.id.desktopSendTab;
        else if (this instanceof wallet.request.RequestCoinsActivity) selectedId = R.id.desktopReceiveTab;
        else if (this instanceof wallet.transaction.TransactionDetailActivity) selectedId = R.id.desktopTransactionsTab;
        else if (this instanceof wallet.contacts.AddressBookActivity) selectedId = R.id.desktopAddressBookTab;
        else if (this instanceof SyncActivity) selectedId = R.id.desktopNetworkTab;
        if (selectedId != 0) {
            View selected = findViewById(selectedId);
            if (selected instanceof android.widget.TextView) {
                ((android.widget.TextView) selected).setTypeface(null, android.graphics.Typeface.BOLD);
            }
        }
    }

    private void openDesktopDestination(Class<?> destination) {
        if (destination.isInstance(this)) return;
        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void applySystemBarInsets() {
        if (Build.VERSION.SDK_INT < 35) {
            return;
        }

        final View content = findViewById(android.R.id.content);
        if (content == null) {
            return;
        }

        final int baseLeft = content.getPaddingLeft();
        final int baseTop = content.getPaddingTop();
        final int baseRight = content.getPaddingRight();
        final int baseBottom = content.getPaddingBottom();

        content.setOnApplyWindowInsetsListener((view, insets) -> {
            int left;
            int top;
            int right;
            int bottom;

            if (Build.VERSION.SDK_INT >= 30) {
                Insets systemBars = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                left = systemBars.left;
                top = systemBars.top;
                right = systemBars.right;
                bottom = systemBars.bottom;
            } else {
                left = insets.getSystemWindowInsetLeft();
                top = insets.getSystemWindowInsetTop();
                right = insets.getSystemWindowInsetRight();
                bottom = insets.getSystemWindowInsetBottom();
            }

            view.setPadding(
                    baseLeft + left,
                    baseTop + top,
                    baseRight + right,
                    baseBottom + bottom);
            return insets;
        });
        content.requestApplyInsets();
    }
}
