package com.android.usbport;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenyuye on 17/12/18.
 */

public class USBManagerImpl extends USBManager{

    private static final String TAG = USBManagerImpl.class.getSimpleName();
    private static Context mContext = null;

    private static USBManagerImpl mInstance = null;
    private boolean isUsbOpen = false;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private UsbManager mUsbManager = null;
    private static UsbSerialPort sPort = null;

    private SerialInputOutputManager mSerialIoManager;
    private SerialInputOutputManager.Listener mListener;

    private final byte[] STATIC = new byte[1];

    public USBManagerImpl(Context context){
        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    /**
     * 搜索USB设备, USB一对多时使用
     *
     * @return
     *      &emsp;&emsp;搜索到的所有USB设备
     */
    @Override
    public List<USBDevice> listUsbDevices(){
        List<UsbSerialDriver> drivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        if(drivers.size() > 0){
            UsbDevice device = drivers.get(0).getDevice();
            if(!mUsbManager.hasPermission(device)){
                String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
                PendingIntent i = PendingIntent.getBroadcast(mContext, 0,
                        new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device, i);
            }
        }
        final List<USBDevice> result = new ArrayList<>();
        int index = 1;
        for (final UsbSerialDriver driver : drivers) {
            List<USBDevice> list = new ArrayList<>();
            final List<UsbSerialPort> ports = driver.getPorts();
            for (UsbSerialPort port : ports) {
                list.add(new USBDevice(mContext, "COM"+(index++), port));
            }
            Log.d(TAG, String.format("usb serial port %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
            result.addAll(list);
            list = null;
        }
        drivers = null;
        return result;
    }

    /**
     * 搜索USB设备，USB一对一时使用
     *
     * @return
     *      &emsp;&emsp;搜索到的所有USB设备
     */
    @Override
    public List<UsbSerialPort> listUsbPort(){
        List<UsbSerialDriver> drivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        if(drivers.size() > 0){
            UsbDevice device = drivers.get(0).getDevice();
            if(!mUsbManager.hasPermission(device)){
                String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
                PendingIntent i = PendingIntent.getBroadcast(mContext, 0,
                        new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(device, i);
            }
        }
        final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            Log.d(TAG, String.format("usb serial port %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
            result.addAll(ports);
        }
        drivers = null;
        return result;
    }

    /**
     * 用默认配置打开USB设备进行通信（波特率：115200、数据位：8、停止位：1、校验位：无）<BR/>
     *
     * @param port  要打开的USB设备
     * @return  是否打开成功</BR>
     *      &emsp;&emsp;true: 打开成功</BR>
     *      &emsp;&emsp;false: 打开失败</BR>
     */
    @Override
    public boolean openUsbPort(UsbSerialPort port){
        return openUsbPort(port, new USBParams());
    }

    /**
     * 用自定义配置打开USB设备进行通信
     * @param port  要打开的USB设备
     * @param param 使用的参数
     * @return  是否打开成功</BR>
     *      &emsp;&emsp;true: 打开成功</BR>
     *      &emsp;&emsp;false: 打开失败</BR>
     */
    @Override
    public boolean openUsbPort(UsbSerialPort port, USBParams param){
        Log.d(TAG, "openUsbPort: port="+ port);
        sPort = port;
        if (sPort == null && param == null) {//参数判断
            Log.e(TAG, "openUsbPort: param is null");
//            Toast.makeText(mContext, "param is null", Toast.LENGTH_SHORT).show();
            isUsbOpen = false;
            return false;
        }
        //打开设备，获得USB连接
        UsbDeviceConnection connection = mUsbManager.openDevice(sPort.getDriver().getDevice());
        if (connection == null) {
            Log.e(TAG, "openUsbPort: Opening device failed");
            Toast.makeText(mContext, "Opening device failed", Toast.LENGTH_SHORT).show();
            isUsbOpen = false;
            return false;
        }
        try {//打开USB端口
            sPort.open(connection);
            //设置端口参数
            sPort.setParameters(param.baudrate, param.dataBits, param.stopBits, param.parity);
            //状态显示
            Log.i(TAG, "openUsbPort: CD  - Carrier Detect="+ sPort.getCD());
            Log.i(TAG, "openUsbPort: CTS - Clear To Send="+ sPort.getCTS());
            Log.i(TAG, "openUsbPort: DSR - Data Set Ready="+ sPort.getDSR());
            Log.i(TAG, "openUsbPort: DTR - Data Terminal Ready="+ sPort.getDTR());
            Log.i(TAG, "openUsbPort: DSR - Data Set Ready="+ sPort.getDSR());
            Log.i(TAG, "openUsbPort: RI  - Ring Indicator="+ sPort.getRI());
            Log.i(TAG, "openUsbPort: RTS - Request To Send="+ sPort.getRTS());
            sPort.write(new byte[]{}, 500);
            isUsbOpen = true;
        } catch (IOException e) {
            Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
            try {
                sPort.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }finally {
                sPort = null;
                isUsbOpen = false;
                return false;
            }
        }
        return true;
    }

    /**
     * 销毁USB设备端口（在应用退出前应及时销毁）
     */
    @Override
    public void destoryPort(){
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
                isUsbOpen = false;
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
    }

    /**
     * 向USB串口写数据
     *
     * @param data 数据
     * @param timeout 超时时间
     * @return 实际写成功的数据长度（int）
     */
    @Override
    public int write(byte[] data, int timeout){
        synchronized (STATIC){
            if(sPort == null) {
                Log.e(TAG, "write: the usb serial port is null");
                return -1;
            }
            if(!isUsbOpen){
                Log.e(TAG, "write: please open the usb port first!");
                return -2;
            }
            try {
                int ret = sPort.write(data, timeout);
                return ret;
            } catch (IOException e) {
                e.printStackTrace();
                return -3;
            }
        }
    }
    @Override
    public void writeDataToUsb(byte[] data){
        Log.d(TAG, "writeDataToUsb: data="+ HexDump.toHexString(data));
        if(!isUsbOpen){
            Log.e(TAG, "writeDataToUsb: please open the usb port first!");
            return;
        }
        if(mSerialIoManager == null){
            mSerialIoManager.writeAsync(data);
        }else{
            Log.e(TAG, "writeDataToUsb: Please set the input listener first!");
        }
    }
    /**
     * 从USB串口读数据
     *
     * @param data 数据
     * @param timeout 超时时间
     * @return 实际读取的数据长度（int）
     */
    @Override
    public int read(byte[] data, int timeout){
        synchronized (STATIC){
            if(sPort == null) {
                Log.e(TAG, "read: the usb serial port is null");
                return -1;
            }
            if(!isUsbOpen){
                Log.e(TAG, "read: please open the usb port first!");
                return -2;
            }
            try {
                int ret = sPort.read(data, timeout);
                return ret;
            } catch (IOException e) {
                e.printStackTrace();
                return -3;
            }
        }
    }

    @Override
    public void setDTR(boolean value) throws IOException {
        if(sPort == null) {
            Log.e(TAG, "setDTR: the usb serial port is null");
            return;
        }
        if(sPort != null)
            sPort.setDTR(value);
    }

    @Override
    public void setRTS(boolean value) throws IOException {
        if(sPort == null) {
            Log.e(TAG, "setRTS: the usb serial port is null");
            return;
        }
        if(sPort != null)
            sPort.setRTS(value);
    }




    @Override
    public void setOnInputListener(SerialInputOutputManager.Listener listener){
        mListener = listener;
        onDeviceStateChange();
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }
}
