package com.android.usbport;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;

/**
 * Created by chenyuye on 17/12/18.
 */

public class USBDevice{

    private static final String TAG = USBDevice.class.getSimpleName();
    private static Context mContext = null;

    private String comName = "";    //端口名称
    private boolean isUsbOpen = false;//端口是否打开
    private UsbSerialPort sPort = null;
    private USBParams params = null;//配置

    private UsbManager mUsbManager = null;
    private final byte[] STATIC = new byte[1];

    public USBDevice(Context context, String comName, UsbSerialPort port) {
        this.comName = comName;
        this.sPort = port;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Override
    public String toString() {
        return "USBDevice: comName="+ comName+ " isUsbOpen="+ isUsbOpen;
    }

    /**
     * 用默认配置打开USB设备进行通信（波特率：9600、数据位：8、停止位：1、校验位：无）<BR/>
     *
     * @return  是否打开成功</BR>
     *      &emsp;&emsp;true: 打开成功</BR>
     *      &emsp;&emsp;false: 打开失败</BR>
     */
    public boolean openUsbPort(){
        if(params == null)
            return openUsbPort(new USBParams());
        else
            return openUsbPort(params);
    }

    /**
     * 用自定义配置打开USB设备进行通信
     * @param param 使用的参数
     * @return  是否打开成功</BR>
     *      &emsp;&emsp;true: 打开成功</BR>
     *      &emsp;&emsp;false: 打开失败</BR>
     */
    public boolean openUsbPort(USBParams param){
        Log.d(TAG, "openUsbPort: param="+ param.toString());
        if (sPort == null || param == null) {//参数判断
            Log.e(TAG, "openUsbPort: param is null");
//            Toast.makeText(mContext, "param is null", Toast.LENGTH_SHORT).show();
            isUsbOpen = false;
            return false;
        }
        if(isUsbOpen) {
            Log.e(TAG, "usb port already opened");
            return false;
        }
        this.params = param;
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
            isUsbOpen = true;
            sPort.write(new byte[]{}, 500);
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
    public void destoryPort(){
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
    protected int write(byte[] data, int timeout){
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

    /**
     * 从USB串口读数据
     *
     * @param data 数据
     * @param timeout 超时时间
     * @return 实际读取的数据长度（int）
     */
    protected int read(byte[] data, int timeout){
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

    public void setDTR(boolean value) throws IOException {
        if(sPort == null) {
            Log.e(TAG, "setDTR: the usb serial port is null");
            return;
        }
        if(sPort != null)
            sPort.setDTR(value);
    }

    public void setRTS(boolean value) throws IOException {
        if(sPort == null) {
            Log.e(TAG, "setRTS: the usb serial port is null");
            return;
        }
        if(sPort != null)
            sPort.setRTS(value);
    }

    /**
     * USB串口是否已打开
     * @return
     */
    public boolean isUsbOpen() {
        return isUsbOpen;
    }

    /**
     * 设置USB串口参数
     * @param params
     */
    public void setParams(USBParams params) {
        this.params = params;
    }

    /**
     * 获取USB端口
     * @return
     */
    public UsbSerialPort getUsbPort() {
        return sPort;
    }

}
