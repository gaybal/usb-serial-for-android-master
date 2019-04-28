package com.android.usbport;

import android.content.Context;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by chenyuye on 17/12/26.
 */

public abstract class USBManager {

    private static final String TAG = USBManager.class.getSimpleName();
    private static USBManager mInstance = null;

    public static USBManager getInstance(Context context){
        if(mInstance == null)
            mInstance = new USBManagerImpl(context);
        return mInstance;
    }

    /**
     * 搜索USB设备，USB一对多时使用
     *
     * @return
     *      &emsp;&emsp;搜索到的所有USB设备
     */
    public abstract List<USBDevice> listUsbDevices();
    /**
     * 搜索USB设备，USB一对一时使用
     *
     * @return
     *      &emsp;&emsp;搜索到的所有USB设备
     */
    public abstract List<UsbSerialPort> listUsbPort();
    /**
     * 用默认配置打开USB设备进行通信（波特率：115200、数据位：8、停止位：1、校验位：无）<BR/>
     *
     * @param port  要打开的USB设备
     * @return  是否打开成功</BR>
     *      &emsp;&emsp;true: 打开成功</BR>
     *      &emsp;&emsp;false: 打开失败</BR>
     */
    public abstract boolean openUsbPort(UsbSerialPort port);
    /**
     * 用自定义配置打开USB设备进行通信
     * @param port  要打开的USB设备
     * @param param 使用的参数
     * @return  是否打开成功</BR>
     *      &emsp;&emsp;true: 打开成功</BR>
     *      &emsp;&emsp;false: 打开失败</BR>
     */
    public abstract boolean openUsbPort(UsbSerialPort port, USBParams param);
    /**
     * 销毁USB设备端口（在应用退出前应及时销毁）
     */
    public abstract void destoryPort();
    /**
     * 向USB串口写数据
     *
     * @param data 数据
     * @param timeout 超时时间
     * @return 实际写成功的数据长度（int）
     */
    public abstract int write(byte[] data, int timeout);
    /**
     * 从USB串口读数据
     *
     * @param data 数据
     * @param timeout 超时时间
     * @return 实际读取的数据长度（int）
     */
    public abstract int read(byte[] data, int timeout);

    public abstract void setDTR(boolean value) throws IOException;
    public abstract void setRTS(boolean value) throws IOException;

    @Deprecated
    public abstract void setOnInputListener(SerialInputOutputManager.Listener listener);
    @Deprecated
    public abstract void writeDataToUsb(byte[] data);
}
