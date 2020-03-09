package com.ela.deviceadvertisingmobile.view;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.ela.deviceadvertisingmobile.ble.tag.TagId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DebugFragment extends Fragment
{
    /** ---------- Arguments ---------- */
    private ImageButton startScan;
    private ImageButton stopScan;
    private ImageButton connect;
    private ScrollView scrollTable, scrollDetail;
    private TableLayout table;
    private TextView tagName;
    private LinearLayout layoutConnection;
    private TextView console;
    private Button sendCmd;
    private EditText txtCmd;

    private final Handler timerHandler = new Handler();
    private final Handler timerHandlerDetail = new Handler();
    private Runnable updater, updaterDetail;
    private final int UPDATE_TIME_MS = 1000;
    private final int UPDATE_DETAIL_TIME_MS = 500;
    private HashMap<String, View> currentList = new HashMap<>();
    private String tagSelected = "";
    private String tagSelectedName = "";
    private boolean connectDisplay = false;

    /** ---------- Public functions -------- */
    public DebugFragment() { }

    /** ---------- Override functions -------- */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_debug, container, false);
        mapUi(v);
        runUpdateUi();
        runUpdateUiDetail();
        initView();
        BleFactory.getInstance().clearFactory();

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
        this.layoutConnection.setVisibility(View.VISIBLE);
    }

    /**
     *   Map the widgets
     * @param v [View]
     */
    private void mapUi(View v)
    {
        this.console = v.findViewById(R.id.output_console);
        this.sendCmd = v.findViewById(R.id.send_command);
        this.sendCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtCmd.getText().toString().equals("")) {
                    DebugText.getInstance().write(">>> Try to send the command : " + txtCmd.getText().toString());
                    BlueScanner.getInstance().sendData(txtCmd.getText().toString());
                }
            }
        });
        this.txtCmd = v.findViewById(R.id.input_command);
        DebugText.getInstance().setInit(getContext(),this.console);

        this.scrollTable = v.findViewById(R.id.scroll_tag_table);
        this.table = v.findViewById(R.id.table_layout);
        this.scrollDetail = v.findViewById(R.id.detail_scroll);
        this.tagName = v.findViewById(R.id.tag_name);

        this.layoutConnection = v.findViewById(R.id.connection_layout);
        this.connect = v.findViewById(R.id.connect_tag);
        this.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectDisplay)
                {
                    connect.setImageDrawable(getResources().getDrawable(R.drawable.blue_connect));
                    connectDisplay = false;
                    layoutConnection.setVisibility(View.VISIBLE);
                    BlueScanner.getInstance().disconnect();
                }
                else
                {
                    DebugText.getInstance().clear();
                    DebugText.getInstance().write(">>> Try to connect to " + tagSelected);
                    connect.setImageDrawable(getResources().getDrawable(R.drawable.blue_connect_s));
                    connectDisplay = true;
                    layoutConnection.setVisibility(View.VISIBLE);
                    BlueScanner.getInstance().connectToTag(getContext(),tagSelected,null);

                }
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
            if (!this.currentList.containsKey(entry.getKey())) {
                newTableRow(entry.getKey(), macList.get(entry.getKey()), entry.getValue().getRssi());
            } else {
                updateTableRow(entry.getKey(), entry.getValue());
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
            if(entry.getKey().equals(this.tagSelectedName))
            {
                this.tagName.setText(entry.getKey());
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