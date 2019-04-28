package com.android.usbport;

import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * Created by chenyuye on 17/12/25.
 */

public class USBParams {
    /**
     * 波特率
     */
    public int baudrate = 9600;
    /**
     * 数据位
     */
    public int dataBits = UsbSerialPort.DATABITS_8;
    /**
     * 停止位
     */
    public int stopBits = UsbSerialPort.STOPBITS_1;
    /**
     * 校验位
     */
    public int parity = UsbSerialPort.PARITY_NONE;

    public USBParams(){

    }

    @Override
    public String toString() {
        return "USBParams: baudrate="+ baudrate+ " dataBits="+ dataBits+
                " stopBits="+ stopBits+ " parity="+ parity;
    }
}
