package com.ela.deviceadvertisingmobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ela.deviceadvertisingmobile.ble.BlueScanner;
import com.ela.deviceadvertisingmobile.view.DebugFragment;
import com.ela.deviceadvertisingmobile.view.DrawerListAdapter;
import com.ela.deviceadvertisingmobile.view.NavItem;
import com.ela.deviceadvertisingmobile.view.sensors.AngleFragment;
import com.ela.deviceadvertisingmobile.view.sensors.HumiditeFragment;
import com.ela.deviceadvertisingmobile.view.sensors.IdFragment;
import com.ela.deviceadvertisingmobile.view.sensors.MagneticFragment;
import com.ela.deviceadvertisingmobile.view.sensors.MovementFragment;
import com.ela.deviceadvertisingmobile.view.sensors.TemperatureFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    /** ---------- Arguments ---------- */
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private boolean DRAWER_OPEN = false;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<>();
    private ImageButton menuBurger;
    private FrameLayout backgroundMain;
    private LinearLayout secondBackgroundMain;
    private RelativeLayout homeLayoutDrawer;
    private Fragment fragment;
    private ImageView idDash, tempDash, humiDash, movDash, magDash, angleDash;

    /** ---------- Override Activity ---------- */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBluetooth();
        checkPermissions();
        mapUi();
        setListener();
        addItemsToNavBar();
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.getFragments().remove(fragmentManager.getFragments().size() -1 );

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    /** ---------- Private functions ---------- */
    /**
     *   Init the bluetooth configuration
     */
    private void initBluetooth()
    {
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
        BlueScanner.getInstance().setBtScanner(this,btScanner);
    }



    /**
     *   set the listener
     */
    private void setListener()
    {
        this.homeLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        // Drawer Item click listeners
        this.mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                view.setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));
            }
        });

        this.menuBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DRAWER_OPEN) {

                    mDrawerLayout.openDrawer(mDrawerPane);
                    DRAWER_OPEN = true;
                }
                else
                {
                    mDrawerLayout.closeDrawer(mDrawerPane);
                    DRAWER_OPEN = false;
                }
            }
        });

        this.idDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                mDrawerList.getChildAt(0).setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));

                fragment = new IdFragment();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerList.setItemChecked(0, true);
                setTitle(mNavItems.get(0).title);
                backgroundMain.setVisibility(View.GONE);
                secondBackgroundMain.setVisibility(View.GONE);
                menuBurger.setVisibility(View.VISIBLE);
            }
        });

        this.tempDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                mDrawerList.getChildAt(1).setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));

                fragment = new TemperatureFragment();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerList.setItemChecked(1, true);
                setTitle(mNavItems.get(1).title);
                backgroundMain.setVisibility(View.GONE);
                secondBackgroundMain.setVisibility(View.GONE);
                menuBurger.setVisibility(View.VISIBLE);
            }
        });

        this.humiDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                mDrawerList.getChildAt(2).setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));

                fragment = new HumiditeFragment();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerList.setItemChecked(2, true);
                setTitle(mNavItems.get(2).title);
                backgroundMain.setVisibility(View.GONE);
                secondBackgroundMain.setVisibility(View.GONE);
                menuBurger.setVisibility(View.VISIBLE);
            }
        });
        this.movDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                mDrawerList.getChildAt(3).setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));

                fragment = new MovementFragment();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerList.setItemChecked(3, true);
                setTitle(mNavItems.get(3).title);
                backgroundMain.setVisibility(View.GONE);
                secondBackgroundMain.setVisibility(View.GONE);
                menuBurger.setVisibility(View.VISIBLE);
            }
        });

        this.angleDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                mDrawerList.getChildAt(5).setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));

                fragment = new AngleFragment();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerList.setItemChecked(5, true);
                setTitle(mNavItems.get(5).title);
                backgroundMain.setVisibility(View.GONE);
                secondBackgroundMain.setVisibility(View.GONE);
                menuBurger.setVisibility(View.VISIBLE);
            }
        });

        this.magDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i < mDrawerList.getAdapter().getCount(); i++)
                {
                    getViewByPosition(i,mDrawerList).setBackgroundColor(getResources().getColor(R.color.elaWhiteColor));
                }
                mDrawerList.getChildAt(4).setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));

                fragment = new MagneticFragment();

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerList.setItemChecked(4, true);
                setTitle(mNavItems.get(4).title);
                backgroundMain.setVisibility(View.GONE);
                secondBackgroundMain.setVisibility(View.GONE);
                menuBurger.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     *   get the view associated to the position
     * @param pos [int] : position of the View wanted
     * @param listView [listView] : List of all the view
     * @return [View] : View selected
     */
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     *   map the widget to the associated object
     */
    private void mapUi()
    {
        // DrawerLayout
        mDrawerLayout = findViewById(R.id.drawerLayout);


        // Populate the Navigtion Drawer with options
        mDrawerPane = findViewById(R.id.drawerPane);
        mDrawerList = findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Menu Burger
        this.menuBurger = findViewById(R.id.btn_menu);
        this.menuBurger.setVisibility(View.GONE);

        // Main Page
        this.backgroundMain = findViewById(R.id.background_main);
        this.secondBackgroundMain = findViewById(R.id.second_background_main);
        this.backgroundMain.setVisibility(View.VISIBLE);
        this.secondBackgroundMain.setVisibility(View.VISIBLE);

        // Go to home page
        this.homeLayoutDrawer = findViewById(R.id.profileBox);

        this.idDash = findViewById(R.id.id_dashboard);
        this.tempDash = findViewById(R.id.temp_dashboard);
        this.humiDash = findViewById(R.id.temp_humi_dashboard);
        this.movDash = findViewById(R.id.movement_dashboard);
        this.magDash = findViewById(R.id.mag_dashboard);
        this.angleDash = findViewById(R.id.angle_dashboard);
    }

    /**
     *   Check the configuration
     */
    private void checkPermissions()
    {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

    /**
     *   Called when a particular item from the navigation drawer is selected.
     * */
    private void selectItemFromDrawer(int position)
    {
        if(position == 0)
        {
            this.fragment = new IdFragment();
        }
        if(position == 1)
        {
            this.fragment = new TemperatureFragment();
        }
        else if(position == 2)
        {
            this.fragment = new HumiditeFragment();
        }
        else if(position == 3)
        {
            this.fragment = new MovementFragment();
        }
        else if(position == 4)
        {
            this.fragment = new MagneticFragment();
        }
        else if(position == 5)
        {
            this.fragment = new AngleFragment();
        }
        else if(position == 6)
        {
            this.fragment = new DebugFragment();
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).title);

        // Close the drawer
        this.mDrawerLayout.closeDrawer(mDrawerPane);

        this.backgroundMain.setVisibility(View.GONE);
        this.secondBackgroundMain.setVisibility(View.GONE);
    }

    /**
     *   Add items to the nav bar
     */
    private void addItemsToNavBar()
    {
        mNavItems.add(new NavItem("Blue ID", "Identification",R.drawable.blue));
        mNavItems.add(new NavItem("Blue T", "Température",R.drawable.temperature_blue));
        mNavItems.add(new NavItem("Blue RHT","Température / Humidité",R.drawable.humidite_blue));
        mNavItems.add(new NavItem("Blue MOV", "Mouvement", R.drawable.mouvement_blue));
        mNavItems.add(new NavItem("Blue MAG", "Magnetic",R.drawable.porte_blue));
        mNavItems.add(new NavItem("Blue ANG", "Angle",R.drawable.angle));
        mNavItems.add(new NavItem("Debug", " ",R.drawable.parameter));
    }
}