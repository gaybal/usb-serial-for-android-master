/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package test.usb.serialport;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.usbport.ModbusTransfer;
import com.android.usbport.USBManager;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.examples.R;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.hoho.android.usbserial.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private final String TAG = SerialConsoleActivity.class.getSimpleName();

    private Context mContext = SerialConsoleActivity.this;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
    private CheckBox chkDTR;
    private CheckBox chkRTS;

    private EditText wId, wAds, wData;
    private EditText rId, rAds, rLen;
    private Spinner wsId, wsAds, wsData;
    private Spinner rsId, rsAds, rsLen;
    private List<String> lstId = new ArrayList<>();
    private List<String> lstAds = new ArrayList<>();
    private List<String> lstData = new ArrayList<>();

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SerialConsoleActivity.this.updateReceivedData(data);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usbserial_console_activity);
        mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        chkDTR = (CheckBox) findViewById(R.id.checkBoxDTR);
        chkRTS = (CheckBox) findViewById(R.id.checkBoxRTS);

        wId = (EditText) findViewById(R.id.write_id);
        wAds = (EditText) findViewById(R.id.write_address);
        wData = (EditText) findViewById(R.id.write_data);
        rId = (EditText) findViewById(R.id.read_id);
        rAds = (EditText) findViewById(R.id.read_address);
        rLen = (EditText) findViewById(R.id.read_length);
        wId.setText("1");
        wAds.setText("0191");
        wData.setText("0001");
        rId.setText("1");
        rAds.setText("0004");
        rLen.setText("1");
        wsId = (Spinner) findViewById(R.id.write_select_id);
        wsAds = (Spinner) findViewById(R.id.write_select_address);
        wsData = (Spinner) findViewById(R.id.write_select_data);
        rsId = (Spinner) findViewById(R.id.read_select_id);
        rsAds = (Spinner) findViewById(R.id.read_select_address);
        rsLen = (Spinner) findViewById(R.id.read_select_length);

        initSpinnerData();
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lstId);
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lstAds);
        ArrayAdapter adapter3 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lstData);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wsId.setAdapter(adapter1);
        rsId.setAdapter(adapter1);
        wsAds.setAdapter(adapter2);
        rsAds.setAdapter(adapter2);
        wsData.setAdapter(adapter3);
        rsLen.setAdapter(adapter3);

        wsId.setOnItemSelectedListener(this);
        wsAds.setOnItemSelectedListener(this);
        wsData.setOnItemSelectedListener(this);
        rsId.setOnItemSelectedListener(this);
        rsAds.setOnItemSelectedListener(this);
        rsLen.setOnItemSelectedListener(this);

        chkDTR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    USBManager.getInstance(mContext).setDTR(isChecked);
                }catch (Exception x){
                    x.printStackTrace();
                }
            }
        });

        chkRTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    USBManager.getInstance(mContext).setRTS(isChecked);
                }catch (Exception x){
                    x.printStackTrace();
                }
            }
        });

    }

    private void initSpinnerData(){
        lstId.add("1");
        lstId.add("2");
        lstId.add("3");
        lstId.add("4");
        lstAds.add("4");
        lstAds.add("5");
        lstAds.add("6");
        lstAds.add("7");
        lstAds.add("8");
        lstAds.add("9");
        lstAds.add("401");
        lstAds.add("402");
        lstAds.add("403");
        lstAds.add("404");
        lstAds.add("405");
        lstAds.add("406");
        lstAds.add("407");
        lstAds.add("408");
        lstAds.add("409");
        lstAds.add("410");
        lstAds.add("411");
        lstAds.add("412");
        lstAds.add("413");
        lstAds.add("414");
        lstAds.add("415");
        lstAds.add("416");
        lstData.add("0");
        lstData.add("1");
        lstData.add("2");
        lstData.add("3");
        lstData.add("4");
        lstData.add("5");
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemSelected: view.getid="+ view.getId()+ "position="+ i+ " l="+ l);
        switch (adapterView.getId()){
            case R.id.write_select_id:
                wId.setText((String)wsId.getSelectedItem());
                break;
            case R.id.read_select_id:
                rId.setText((String)rsId.getSelectedItem());
                break;
            case R.id.write_select_data:
                int index = wsData.getSelectedItemPosition();
                byte[] data = new byte[2];
                data[0] = (byte)((index >> 8) & 0x000000FF);
                data[1] = (byte)(index & 0x000000FF);
                String hex = StringUtil.byte2HexStr(data);
                wData.setText(hex);
                break;
            case R.id.read_select_length:
                rLen.setText((String)rsLen.getSelectedItem());
                break;
            case R.id.write_select_address:
                int position = wsAds.getSelectedItemPosition();
                if(position > 5){
                    int num = 395 + position;
                    byte[] address = new byte[2];
                    address[0] = (byte)((num >> 8) & 0x000000FF);
                    address[1] = (byte)(num & 0x000000FF);
                    String hexAds = StringUtil.byte2HexStr(address);
                    wAds.setText(hexAds);
                }
                break;
            case R.id.read_select_address:
                if(rsAds.getSelectedItemPosition() < 6){
                    int num = Integer.parseInt((String)rsAds.getSelectedItem());
                    byte[] address = new byte[2];
                    address[0] = (byte)((num >> 8) & 0x000000FF);
                    address[1] = (byte)(num & 0x000000FF);
                    String hexAds = StringUtil.byte2HexStr(address);
                    rAds.setText(hexAds);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void showStatus(TextView theTextView, String theLabel, boolean theValue){
        String msg = theLabel + ": " + (theValue ? "enabled" : "disabled") + "\n";
        theTextView.append(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resume: executed");
        wsAds.setSelection(6);
        wsData.setSelection(1);
        rsLen.setSelection(1);
        USBManager.getInstance(mContext).setOnInputListener(mListener);
    }

    public void sendCmdToPlc(View v){
        byte[] ret = ModbusTransfer.getInstance(mContext).sendCmdToPlc(
                Integer.parseInt(wId.getText().toString().trim()),
                StringUtil.hexStr2Bytes(wAds.getText().toString().trim()),
                StringUtil.hexStr2Bytes(wData.getText().toString().trim()));
        String result = ret==null? "指令发送失败！" : "指令发送成功："+ StringUtil.byte2HexStr(ret);
        mDumpTextView.append(result+ "\n");
//        Toast.makeText(mContext, StringUtil.byte2HexStr(ret), Toast.LENGTH_SHORT).show();
    }

    public void readDataFromPlc(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final byte[] ret = ModbusTransfer.getInstance(mContext).readDataFromPlc(
                        Integer.parseInt(rId.getText().toString().trim()),
                        StringUtil.hexStr2Bytes(rAds.getText().toString().trim()),
                        Integer.parseInt(rLen.getText().toString().trim()),
                        5000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(ret != null) {
                            mDumpTextView.append("成功读取数据：" + StringUtil.byte2HexStr(ret) + "\n");
                            Toast.makeText(mContext, StringUtil.byte2HexStr(ret), Toast.LENGTH_SHORT).show();
                        }else{
                            mDumpTextView.append("读取数据失败！\n");
                        }
                    }
                });
            }
        }).start();
    }
    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + StringUtil.byte2HexStr(data) + "\n\n";
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        mDumpTextView.append(message);
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
//        USBManager.getInstance(mContext).writeDataToUsb(data);
    }

}
