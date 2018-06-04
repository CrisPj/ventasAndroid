package pythonteam.com.ventasapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import pythonteam.com.ventasapp.util.SharedPreferencesManager;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottomNav)
    BottomNavigationView btnNavigation;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    OrdersFragment fragment1 = new OrdersFragment();
    CustomersFragment fragment2 = new CustomersFragment();
    ProductsFragment fragment3 = new ProductsFragment();
    MapsFragment fragment4 = new MapsFragment();
    private int currentPage;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return fragment1;
                    case 1:
                        return fragment2;
                    case 2:
                        return fragment3;
                    case 3:
                        return fragment4;

                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }
        });
        btnNavigation.setOnNavigationItemSelectedListener(item -> {
            mViewPager.setCurrentItem(item.getOrder());
            return true;
        });
        disableShiftMode(btnNavigation);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.addOnPageChangeListener(this);
        fab.setOnClickListener(this);
    }

    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

            builder.setMessage("Hecho por el Cristian")
                    .setTitle("About");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (id == R.id.action_route)
        {
            Intent intMap = new Intent(this, MapsActivity.class);
                        intMap.putExtra("routeID", SharedPreferencesManager.read(SharedPreferencesManager.USER_ID, -1));
                        startActivity(intMap);
        }
        else if (id == R.id.action_order)
        {
            Intent i2 = new Intent(getApplicationContext(), CartActivity.class);
                                    startActivity(i2);
        }
        else if (id == R.id.action_logout)
        {
           SharedPreferencesManager.write(SharedPreferencesManager.TOKEN,"");
           Intent i2 = new Intent(getApplicationContext(), LoginActivity.class);
           startActivity(i2);
           finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        fab.hide();
        switch (currentPage) {
            case 0:
                break;
            case 1:
                fab.show();
                break;
            case 2:
                fab.show();
                break;
            case 3:
                fab.show();
            default: // Leaves the FAB hidden on any other tabs
                break;
        }
    }


    @Override
    public void onPageSelected(int position) {
        currentPage = position;
        btnNavigation.getMenu().getItem(position).setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (mViewPager.getCurrentItem()) {
            case 0:
                fragment1.newOrder();
                break;
            case 1:
                fragment2.newCustomer();
                break;
            case 2:
                fragment3.newProduct();
                break;
            case 3:
                fragment4.newRoute();
        }
    }
}
