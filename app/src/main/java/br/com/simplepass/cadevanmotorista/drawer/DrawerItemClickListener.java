package br.com.simplepass.cadevanmotorista.drawer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.activity.LoginActivity;
import br.com.simplepass.cadevanmotorista.activity.SharedPathsActivity;
import br.com.simplepass.cadevanmotorista.location.Locator;
import br.com.simplepass.cadevanmotorista.utils.Constants;


/**
 * Listener of the Drawer Menu
 */
public class DrawerItemClickListener implements NavigationView.OnNavigationItemSelectedListener{
    private Activity mActivity;
    private DrawerLayout mDrawerLayout;

    public DrawerItemClickListener(Activity activity, DrawerLayout drawerLayout){
        mActivity = activity;
        mDrawerLayout = drawerLayout;
    }


    /**
     * Method called after a click in the drawer menu
     * @param item clicked item
     * @return return the success of the method
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();

        switch (item.getItemId()){
            case R.id.drawer_item_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getString(R.string.share_message));
                shareIntent.setType("text/plain");
                mActivity.startActivity(shareIntent);
                return true;
            case R.id.drawer_item_shared_paths:
                Intent intent = new Intent(mActivity, SharedPathsActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            mActivity);

                    mActivity.startActivity(intent, activityOptions.toBundle());
                } else {
                    mActivity.startActivity(intent);
                }
                return true;
            case R.id.drawer_item_leave:
                Locator.getInstance().stop();

                Intent intent2 = new Intent(mActivity, LoginActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //ToDo: Consertar isso
                    AccountManager.get(mActivity).removeAccount(
                            new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE),
                            mActivity,
                            null,
                            null);

                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                            mActivity);

                    mActivity.startActivity(intent2, activityOptions.toBundle());
                } else {
                    AccountManager.get(mActivity).removeAccount(
                            new Account(Constants.ARG_ACCOUNT_NAME, Constants.ACCOUNT_TYPE),
                            null,
                            null);
                    mActivity.startActivity(intent2);
                }
                return true;
            default:
                return true;
        }
    }


}
