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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.usbport.USBManager;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.examples.R;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.List;


public class DataActivity extends Activity {
    TextView tv;
    EditText etRequest;
    Button btnSend,btnSearch;
    private OnListUsbSuccessListener onListUsbSuccessListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        tv = (TextView) findViewById(R.id.tv);
        etRequest = (EditText) findViewById(R.id.et_request);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSearch = (Button) findViewById(R.id.btn_list);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USBManager usbManager = USBManager.getInstance(DataActivity.this);
                usbManager.write(etRequest.getText().toString().getBytes(),1000);
            }
        });
        setOnListUsbSuccessListener(new OnListUsbSuccessListener() {
            @Override
            public void setOnListUsbSuccessListener(boolean b) {
                if (b) {
                    USBManager usbManager = USBManager.getInstance(DataActivity.this);
                    usbManager.setOnInputListener(new SerialInputOutputManager.Listener() {
                        @Override
                        public void onRunError(Exception e) {
                            Toast.makeText(DataActivity.this, "串口连接异常，请重试！", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onNewData(final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String text = new String(data);
                                    tv.append(text);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
    private void refreshDeviceList() {

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                List<UsbSerialPort> usbSerialPorts = USBManager.getInstance(DataActivity.this).listUsbPort();
                return usbSerialPorts;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                if (result!=null && result.size()>0) {
                    boolean ret = USBManager.getInstance(DataActivity.this).openUsbPort(result.get(0));
                    if(ret) {
                        onListUsbSuccessListener.setOnListUsbSuccessListener(true);
                        Toast.makeText(DataActivity.this, "open usb success", Toast.LENGTH_SHORT).show();
                    }else{
                        onListUsbSuccessListener.setOnListUsbSuccessListener(false);
                        Toast.makeText(DataActivity.this, "open usb fail", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute((Void) null); }



    @Override
    protected void onResume() {
        super.onResume();
        refreshDeviceList();

    }
    private void setOnListUsbSuccessListener(OnListUsbSuccessListener onListUsbSuccessListener){
        this.onListUsbSuccessListener = onListUsbSuccessListener;
    };
}
interface OnListUsbSuccessListener{
    void setOnListUsbSuccessListener(boolean b);
}
