package com.android.usbport;

import android.content.Context;

/**
 * Created by chenyuye on 17/12/26.
 */
public abstract class ModbusTransfer{

    private static ModbusTransfer mInstance = null;

    public static ModbusTransfer getInstance(Context context){
        if(mInstance == null)
            mInstance = new ModbusTransferImpl(context);
        return mInstance;
    }

    /**
     *  主机向从机发送写指令
     * @param desAds    从机号
     * @param dataAds   要写入的数据地址
     * @param data      要写入的数据
     * @return 打包后的数据(调试时使用)
     */
    public abstract byte[] sendCmdToPlc(USBDevice device, int desAds, byte[] dataAds, byte[] data);
    /**
     *  主机向从机发送写指令
     * @param desAds    从机号
     * @param dataAds   要写入的数据地址
     * @param data      要写入的数据
     * @return 打包后的数据(调试时使用)
     */
    public abstract byte[] sendCmdToPlc(int desAds, byte[] dataAds, byte[] data);

    /**
     *  主机从从机中读取数据
     * @param desAds        从机号
     * @param dataAds       要读取的数据地址
     * @param dataLength    要读取的数据长度
     * @param timeout       超时时间ms
     * @return  读取的数据，长度＝dataLength
     */
    public abstract byte[] readDataFromPlc(USBDevice device, int desAds, byte[] dataAds, int dataLength, int timeout);
    /**
     *  主机从从机中读取数据
     * @param desAds        从机号
     * @param dataAds       要读取的数据地址
     * @param dataLength    要读取的数据长度
     * @param timeout       超时时间ms
     * @return  读取的数据，长度＝dataLength
     */
    public abstract byte[] readDataFromPlc(int desAds, byte[] dataAds, int dataLength, int timeout);
}
