package com.ela.deviceadvertisingmobile.view.sensors;

import android.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ela.deviceadvertisingmobile.R;
import com.ela.deviceadvertisingmobile.ble.BleFactory;
import com.ela.deviceadvertisingmobile.ble.BlueScanner;
import com.ela.deviceadvertisingmobile.ble.tag.Tag;
import com.ela.deviceadvertisingmobile.ble.tag.TagMagnetic;
import com.ela.deviceadvertisingmobile.ble.tag.TagMovement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class MagneticFragment extends Fragment
{
    /** ---------- Arguments ---------- */
    private ImageButton startScan;
    private ImageButton stopScan;
    private ImageButton connect,disconnet;
    private ScrollView scrollTable, scrollDetail;
    private TableLayout table;
    private TextView tagName, tagConnectName, tagStateConnection, tagBatValue;
    private LinearLayout layoutConnection;
    private View viewLimit, viewEnd, circleState;
    private ImageButton sendLedOn, sendLedOff, sendBuzzOn, sendBuzzOff, sendBatt;
    private CircularProgressIndicator circularProgress;
    private GradientDrawable circleBackground;

    private final Handler timerHandler = new Handler();
    private final Handler timerHandlerDetail = new Handler();
    private Runnable updater, updaterDetail;
    private final int UPDATE_TIME_MS = 1000;
    private final int UPDATE_DETAIL_TIME_MS = 500;
    private HashMap<String, View> currentList = new HashMap<>();
    private String tagSelected = "";
    private String tagSelectedName = "";
    private boolean connectDisplay = false;
    private boolean scrollIsUp = true;

    /** ---------- Public functions -------- */
    public MagneticFragment() { }

    /** ---------- Override functions -------- */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_magnetic, container, false);
        BleFactory.getInstance().clearFactory();
        mapUi(v);
        runUpdateUi();
        runUpdateUiDetail();
        initView();
        BleFactory.getInstance().clearFactory();

        circleBackground = (GradientDrawable)circleState.getBackground();
        circleBackground.setColor(getResources().getColor(R.color.elaRedColor));
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.timerHandler.removeCallbacks(updater);
        this.timerHandlerDetail.removeCallbacks(updaterDetail);
        BlueScanner.getInstance().disconnect();
    }

    /** ---------- Private functions -------- */
    /**
     *   Init the view
     */
    private void initView()
    {
        this.scrollDetail.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.scrollTable.setLayoutParams(params);
        this.stopScan.setImageDrawable(getResources().getDrawable(R.drawable.stop_s));
        this.layoutConnection.setVisibility(View.GONE);
        this.viewLimit.setVisibility(View.GONE);
        this.viewEnd.setVisibility(View.GONE);
    }

    /**
     *   Map the widgets
     * @param v [View]
     */
    private void mapUi(View v)
    {
        this.circleState = v.findViewById(R.id.circle_state);
        this.circularProgress = v.findViewById(R.id.circular_progress);
        this.circularProgress.setMaxProgress(100);
        this.circularProgress.setAnimationEnabled(false);
        this.scrollTable = v.findViewById(R.id.scroll_tag_table);
        this.scrollDetail = v.findViewById(R.id.detail_scroll);
        this.table = v.findViewById(R.id.table_layout);
        this.tagName = v.findViewById(R.id.tag_name);

        this.tagConnectName = v.findViewById(R.id.tag_connect_name);
        this.tagStateConnection = v.findViewById(R.id.state_connection);
        this.layoutConnection = v.findViewById(R.id.connection_layout);
        this.viewLimit = v.findViewById(R.id.view_limit);
        this.viewEnd = v.findViewById(R.id.view_end);
        this.connect = v.findViewById(R.id.connect_tag);
        this.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connectDisplay)
                {
                    connect.setImageDrawable(getResources().getDrawable(R.drawable.connection));
                    disconnet.setImageDrawable(getResources().getDrawable(R.drawable.connection));
                    layoutConnection.setVisibility(View.GONE);
                    viewLimit.setVisibility(View.GONE);
                    viewEnd.setVisibility(View.GONE);
                    connectDisplay = false;
                    BlueScanner.getInstance().disconnect();
                }
                else
                {

                    connect.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
                    disconnet.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
                    layoutConnection.setVisibility(View.VISIBLE);
                    viewLimit.setVisibility(View.VISIBLE);
                    viewEnd.setVisibility(View.VISIBLE);
                    connectDisplay = true;
                    BlueScanner.getInstance().connectToTag(getContext(),tagSelected,tagStateConnection);

                }
                scrollDetail.post(new Runnable() {
                    @Override
                    public void run() {
                        if (scrollIsUp)
                        {
                            scrollDetail.smoothScrollTo(0, 1200);
                            scrollIsUp = false;

                        } else {
                            scrollDetail.smoothScrollTo(0, 0);
                            scrollIsUp = true;
                        }
                    }
                });
            }
        });
        this.disconnet = v.findViewById(R.id.tag_connect_disconnect);
        this.disconnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectDisplay)
                {
                    disconnet.setImageDrawable(getResources().getDrawable(R.drawable.connection));
                    connect.setImageDrawable(getResources().getDrawable(R.drawable.connection));
                    layoutConnection.setVisibility(View.GONE);
                    viewLimit.setVisibility(View.GONE);
                    viewEnd.setVisibility(View.GONE);
                    connectDisplay = false;
                    BlueScanner.getInstance().disconnect();
                }
                else
                {
                    connect.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
                    disconnet.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
                    layoutConnection.setVisibility(View.VISIBLE);
                    viewLimit.setVisibility(View.VISIBLE);
                    viewEnd.setVisibility(View.VISIBLE);
                    connectDisplay = true;

                }
                scrollDetail.post(new Runnable() {
                    @Override
                    public void run() {
                        if (scrollIsUp)
                        {
                            scrollDetail.smoothScrollTo(0, 1200);
                            scrollIsUp = false;

                        } else {
                            scrollDetail.smoothScrollTo(0, 0);
                            scrollIsUp = true;
                        }
                    }
                });
            }
        });

        this.startScan = v.findViewById(R.id.start_scan);
        this.startScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getContext(), "Scan Start", Toast.LENGTH_SHORT).show();
                startScan.setImageDrawable(getResources().getDrawable(R.drawable.start_s));
                stopScan.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                BlueScanner.getInstance().startScanning();
            }
        });
        this.stopScan = v.findViewById(R.id.stop_scan);
        this.stopScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getContext(), "Scan Stop", Toast.LENGTH_SHORT).show();
                startScan.setImageDrawable(getResources().getDrawable(R.drawable.start));
                stopScan.setImageDrawable(getResources().getDrawable(R.drawable.stop_s));
                BlueScanner.getInstance().stopScanning();
            }
        });

        this.sendLedOn = v.findViewById(R.id.connect_send_led_on);
        this.sendLedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueScanner.getInstance().sendData("LED_ON");
            }
        });
        this.sendLedOff = v.findViewById(R.id.connect_send_led_off);
        this.sendLedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueScanner.getInstance().sendData("LED_OFF");
            }
        });
        this.sendBuzzOn = v.findViewById(R.id.connect_send_buzz_on);
        this.sendBuzzOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueScanner.getInstance().sendData("BUZZ_ON");
            }
        });
        this.sendBuzzOff = v.findViewById(R.id.connect_send_buzz_on);
        this.sendBuzzOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueScanner.getInstance().sendData("BUZZ_OFF");
            }
        });

        this.tagBatValue = v.findViewById(R.id.result_bat_voltage);
        this.sendBatt = v.findViewById(R.id.connect_send_bat_voltage);
        this.sendBatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BlueScanner.getInstance().sendData("GET_BATT_VOLTAGE")) {
                    try {
                        Thread.sleep(200);
                        getBatteryLevel(BlueScanner.getInstance().readRx());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                else
                    return;
            }
        });
    }

    /**
     *   Get the bat level of the tag
     * @param rawData [String]
     */
    private void getBatteryLevel(String rawData)
    {
        if(rawData.length() > 43) {
            String batt_1 = hexToAscii(rawData.substring(66, 68));
            String batt_2 = hexToAscii(rawData.substring(69, 71));
            String batt_3 = hexToAscii(rawData.substring(72, 74));
            String batt_4 = hexToAscii(rawData.substring(75, 77));
            String batt = batt_1.concat(",").concat(batt_2).concat(batt_3).concat(batt_4);
            this.tagBatValue.setText(batt);
        }
    }

    /**
     *   Convert hex character to ascii string
     * @param hexStr [String]
     * @return
     */
    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    /**
     *   Update the Ui
     * @param tagList [HashMap<String, Tag>] : tag list from the factory
     */
    private void updateUi(LinkedHashMap<String, Tag> tagList, LinkedHashMap<String, String> macList)
    {
        if(tagList.size() == 0) { return; }
        for(Map.Entry<String, Tag> entry : tagList.entrySet())
        {
            if(entry.getValue() instanceof TagMagnetic) {
                if (!this.currentList.containsKey(entry.getKey())) {
                    newTableRow(entry.getKey(), macList.get(entry.getKey()), entry.getValue().getRssi());
                } else {
                    updateTableRow(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     *   Update the rssi value
     * @param name [String] : name of the tag
     * @param tag [Tag] : rssi to update
     */
    private void updateTableRow(String name, Tag tag)
    {
        for(Map.Entry<String, View> entry : this.currentList.entrySet())
        {
            if(entry.getValue().getClass().equals(TableRow.class))
            {
                TableRow tableRow = (TableRow) entry.getValue();
                TextView textView = (TextView) tableRow.getChildAt(0);

                if(textView.getText().toString().equals(name))
                {
                    TextView rssiView = (TextView) tableRow.getChildAt(1);
                    rssiView.setText(String.valueOf(tag.getRssi()));
                    entry.getValue().invalidate();
                    entry.getValue().requestLayout();
                }
            }
        }
    }

    /**
     *   Add a new table row to the table layout with all the tags
     * @param mac [String] : Name display in the row
     * @param rssi [int] : value display
     */
    private void newTableRow(final String name, final String mac, int rssi)
    {
        TableRow tableRow = new TableRow(getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setPadding(10,10,10,10);

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new TableRow.LayoutParams(0));
        textView.setTextColor(getResources().getColor(R.color.elaDarkBlueColor));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView.setText(name);

        tableRow.addView(textView);

        TextView textView2 = new TextView(getContext());
        textView2.setLayoutParams(new TableRow.LayoutParams(1));
        textView2.setTextColor(getResources().getColor(R.color.elaDarkBlueColor));
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        textView2.setText(String.valueOf(rssi));
        textView2.setGravity(Gravity.CENTER);

        tableRow.addView(textView2);

        this.table.addView(tableRow);

        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, TypedValue.COMPLEX_UNIT_DIP));
        view.setBackgroundColor(getResources().getColor(R.color.elaDarkBlueColor));

        this.table.addView(view);

        tableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Map.Entry<String, View> entry : currentList.entrySet())
                {
                    entry.getValue().setBackgroundColor(getResources().getColor(R.color.background));
                }
                v.setBackgroundColor(getResources().getColor(R.color.elaLightGrayColor));
                tagSelected = mac;
                tagSelectedName = name;

                scrollDetail.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,290);
                scrollTable.setLayoutParams(params);
            }
        });
        this.currentList.put(name, tableRow);
    }

    /**
     *   Update the Ui detail for
     * @param tagList
     */
    public void updateUiDetail(LinkedHashMap<String, Tag> tagList)
    {
        if(tagList.size() == 0) { return; }

        for(Map.Entry<String, Tag> entry : tagList.entrySet())
        {
            if(entry.getKey().equals(this.tagSelectedName) &&
                    (entry.getValue() instanceof TagMagnetic))
            {
                TagMagnetic tag = (TagMagnetic) entry.getValue();
                this.tagName.setText(entry.getKey());
                this.tagConnectName.setText(entry.getKey());
                if(tag.getState())
                    this.circleBackground.setColor(getResources().getColor(R.color.elaRedColor));
                else
                    this.circleBackground.setColor(getResources().getColor(R.color.elaLightBlueColorCircle));

                int counter = tag.getCounter();
                while(counter > 100)
                {
                    counter = counter - 100;
                }

                this.circularProgress.setCurrentProgress(counter);
            }
        }
    }


    /**
     *   Runnable task to update the ui
     */
    void runUpdateUi() {
        this.updater = new Runnable() {
            @Override
            public void run() {
                timerHandler.postDelayed(updater,UPDATE_TIME_MS);
                updateUi(BleFactory.getInstance().getTagList(),BleFactory.getInstance().getMacTagList());
            }
        };
        this.timerHandler.post(this.updater);
    }

    /**
     *   Runnable task to update the ui graph and detail
     */
    void runUpdateUiDetail() {
        this.updaterDetail = new Runnable() {
            @Override
            public void run() {
                timerHandlerDetail.postDelayed(updaterDetail,UPDATE_DETAIL_TIME_MS);
                updateUiDetail(BleFactory.getInstance().getTagList());
                BleFactory.getInstance().clearCurrentTag();

            }
        };
        this.timerHandlerDetail.post(this.updaterDetail);
    }
}