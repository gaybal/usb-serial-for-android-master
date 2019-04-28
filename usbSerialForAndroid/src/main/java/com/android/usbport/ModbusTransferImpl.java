package com.android.usbport;

import android.content.Context;
import android.util.Log;

import com.hoho.android.usbserial.util.StringUtil;

import java.util.Arrays;


/**
 * Created by chenyuye on 17/12/20.
 */

public class ModbusTransferImpl extends ModbusTransfer{

    private static final String TAG = ModbusTransferImpl.class.getSimpleName();
    private Context mContext ;
    private static final boolean D = true;

    private final byte[] STATIC = new byte[1];
    private final static int WRITE_TIMEOUT = 3*1000;

    public ModbusTransferImpl(Context context){
        mContext = context;
    }

    @Override
    public byte[] sendCmdToPlc(USBDevice device, int desAds, byte[] dataAds, byte[] data){
        synchronized (STATIC){
            Log.d(TAG, "sendCmdToPlc: des="+ desAds+ " dataAds="+ StringUtil.byte2HexStr(dataAds)+
                    " data="+ StringUtil.byte2HexStr(data));
            if(device == null || desAds < 0 || dataAds == null || data == null){
                Log.e(TAG, "sendCmdToPlc: params is error");
                return null;
            }

            int index = 0;
            byte[] param = new byte[1+1+2+data.length+2];

            param[index++] = (byte) desAds;//目标地址
            param[index++] = 6;//写数据指令
            System.arraycopy(dataAds, 0, param, index, dataAds.length);//寄存器地址
            index += 2;
            if(data.length > 0){//数据
                System.arraycopy(data, 0, param, index, data.length);
                index += data.length;
            }
            byte[] crc = crc_16(param, param.length - 2);
            System.arraycopy(crc, 0, param, index, 2);//CRC校验值
            Log.d(TAG, "发送写指令："+ StringUtil.byte2HexStr(param));
            if(!device.isUsbOpen())device.openUsbPort();
            int ret = -1;
            if(!device.isUsbOpen())
                device.openUsbPort();
                ret = device.write(param, 1000);
            if(ret <= 0){
                Log.e(TAG, "readDataFromPlc: 写指令发送不成功！");
                return null;
            }
            //接收同样的命令
            byte[] temp = new byte[8];
            byte[] recv = new byte[param.length];
            long start = System.currentTimeMillis();
            int recvLength = 0;
            while (true){

                long currTime = System.currentTimeMillis();
                if(currTime-start >= WRITE_TIMEOUT) {
                    Log.e(TAG, "sendCmdToPlc: time out");
                    break;
                }
                if(!device.openUsbPort())
                    device.openUsbPort();
                ret = device.read(temp, 300);
                if(ret >= 0){
                    if(ret > recv.length){
                        Log.e(TAG, "sendCmdToPlc: the receive data is too long, please check the read length");
                        return null;
                    }
                    System.arraycopy(temp, 0, recv, recvLength, ret);
                    recvLength += ret;
                    if(D) Log.i(TAG, "sendCmdToPlc: totleLength="+ recvLength+ " param="+ StringUtil.byte2HexStr(recv));
                    if(recvLength == recv.length){//数据接受完毕
                        recvLength = 0;
                        Log.d(TAG, "接收数据: "+ StringUtil.byte2HexStr(recv));
                        crc = crc_16(recv, recv.length-2);
                        if(crc[0]!=param[param.length-2] || crc[1]!=param[param.length-1]){
                            Log.e(TAG, "sendCmdToPlc: crc check is error, real crc="+ StringUtil.byte2HexStr(crc));
                            return null;
                        }
                        if(!Arrays.equals(param, recv)){
                            Log.e(TAG, "sendCmdToPlc: reveive data is error");
                            return null;
                        }
                        return recv;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public byte[] sendCmdToPlc(int desAds, byte[] dataAds, byte[] data){
        synchronized (STATIC){
            Log.d(TAG, "sendCmdToPlc: des="+ desAds+ " dataAds="+ StringUtil.byte2HexStr(dataAds)+
                    " data="+ StringUtil.byte2HexStr(data));
            if(desAds < 0 || dataAds == null || data == null){
                Log.e(TAG, "sendCmdToPlc: params is error");
                return null;
            }
            int index = 0;
            byte[] param = new byte[1+1+2+data.length+2];

            param[index++] = (byte) desAds;
            param[index++] = 6;//写数据指令
            System.arraycopy(dataAds, 0, param, index, dataAds.length);
            index += 2;
            if(data.length > 0){
                System.arraycopy(data, 0, param, index, data.length);
                index += data.length;
            }
            byte[] crc = crc_16(param, param.length - 2);
            System.arraycopy(crc, 0, param, index, 2);
            Log.d(TAG, "发送写指令："+ StringUtil.byte2HexStr(param));
            int ret = USBManager.getInstance(mContext).write(param, 1000);
            if(ret <= 0){
                Log.e(TAG, "sendCmdToPlc: 写指令发送不成功！");
                return null;
            }
            //接收同样的命令
            byte[] temp = new byte[8];
            byte[] recv = new byte[param.length];
            long start = System.currentTimeMillis();
            int recvLength = 0;
            while (true){

                long currTime = System.currentTimeMillis();
                if(currTime-start >= WRITE_TIMEOUT) {
                    Log.e(TAG, "sendCmdToPlc: time out");
                    break;
                }
                ret = USBManager.getInstance(mContext).read(temp, 300);
                if(ret >= 0){
                    if(ret > recv.length){
                        Log.e(TAG, "sendCmdToPlc: the receive data is too long, please check the read length");
                        return null;
                    }
                    System.arraycopy(temp, 0, recv, recvLength, ret);
                    recvLength += ret;
                    if(D) Log.i(TAG, "sendCmdToPlc: totleLength="+ recvLength+ " param="+ StringUtil.byte2HexStr(recv));
                    if(recvLength == recv.length){//数据接受完毕
                        recvLength = 0;
                        Log.d(TAG, "接收数据: "+ StringUtil.byte2HexStr(recv));
                        crc = crc_16(recv, recv.length-2);
                        if(crc[0]!=param[param.length-2] || crc[1]!=param[param.length-1]){
                            Log.e(TAG, "sendCmdToPlc: crc check is error, real crc="+ StringUtil.byte2HexStr(crc));
                            return null;
                        }
                        if(!Arrays.equals(param, recv)){
                            Log.e(TAG, "sendCmdToPlc: reveive data is error");
                            return null;
                        }
                        return recv;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public byte[] readDataFromPlc(int desAds, byte[] dataAds, int dataLength, int timeout){
        synchronized (STATIC){
            Log.d(TAG, "sendCmdToPlc: des="+ desAds+ " dataAds="+ StringUtil.byte2HexStr(dataAds)+
                    " dataLength="+ dataLength);
            if(desAds < 0 || dataAds == null || dataLength < 1){
                Log.e(TAG, "sendCmdToPlc: params is error");
                return null;
            }
            long startTime = System.currentTimeMillis();
            int index = 0;
            byte[] param = new byte[1+1+2+2+2];

            param[index++] = (byte) desAds;
            param[index++] = 3;//读数据指令
            System.arraycopy(dataAds, 0, param, index, dataAds.length);
            index += 2;
            param[index++] = (byte) ((dataLength>>8)&0xFF00);
            param[index++] = (byte) (dataLength&0x00FF);
            byte[] crc = crc_16(param, param.length - 2);
            System.arraycopy(crc, 0, param, index, 2);
            Log.d(TAG, "发送读指令："+ StringUtil.byte2HexStr(param));
            int ret = USBManager.getInstance(mContext).write(param, 1000);
            if(ret <= 0){
                Log.e(TAG, "readDataFromPlc: 读指令发送不成功！");
                return null;
            }
            param = null;
            int totleLength = 0;
            byte[] data = new byte[8];
            param = new byte[16];
            boolean isCheckLength = false;
            while(true){
                long currTime = System.currentTimeMillis();
                if(currTime-startTime >= timeout) {
                    Log.e(TAG, "readDataFromPlc: time out");
                    break;
                }
                ret = USBManager.getInstance(mContext).read(data, 300);
                if(ret >= 0){
                    if(ret > param.length){
                        Log.e(TAG, "readDataFromPlc: the receive data is too long, please check the read length");
                        return null;
                    }
                    System.arraycopy(data, 0, param, totleLength, ret);
                    totleLength += ret;
                    if(D) Log.i(TAG, "readDataFromPlc: totleLength="+ totleLength+ " param="+ StringUtil.byte2HexStr(param));
                    if(!isCheckLength && totleLength >= 3){
                        isCheckLength = true;
                        //读取到数据长度字节
                        byte[] temp = new byte[totleLength];
                        System.arraycopy(param, 0, temp, 0, totleLength);
                        param = new byte[1+1+1+temp[2]+ 2];
                        System.arraycopy(temp, 0, param, 0, totleLength);
                        temp = null;
                        Log.i(TAG, "readDataFromPlc: after check length param="+ StringUtil.byte2HexStr(param));
                    }
                    if(totleLength == param.length){//数据接受完毕
                        totleLength = 0;
                        Log.d(TAG, "接收数据: "+ StringUtil.byte2HexStr(param));
                        crc = crc_16(param, param.length-2);
                        if(crc[0]!=param[param.length-2] || crc[1]!=param[param.length-1]){
                            Log.e(TAG, "readDataFromPlc: crc check is error, real crc="+ StringUtil.byte2HexStr(crc));
                            return null;
                        }
                        if(param[0] != desAds || param[1] != 3){
                            Log.e(TAG, "readDataFromPlc: reveive data is error");
                            return null;
                        }
                        int len = param[2];
                        data = null;
                        data = new byte[len];
                        System.arraycopy(param, 3, data, 0, len);
                        return data;
                    }
                }
            }
            return null;
        }
    }

    @Override
    public byte[] readDataFromPlc(USBDevice device, int desAds, byte[] dataAds,
                                  int dataLength, int timeout){
        synchronized (STATIC){
            Log.d(TAG, "sendCmdToPlc: des="+ desAds+ " dataAds="+ StringUtil.byte2HexStr(dataAds)+
                    " dataLength="+ dataLength);
            if(device == null || desAds < 0 || dataAds == null || dataLength < 1){
                Log.e(TAG, "sendCmdToPlc: params is error");
                return null;
            }
            long startTime = System.currentTimeMillis();
            int index = 0;
            byte[] param = new byte[1+1+2+2+2];

            param[index++] = (byte) desAds;
            param[index++] = 3;//读数据指令
            System.arraycopy(dataAds, 0, param, index, dataAds.length);
            index += 2;
            param[index++] = (byte) ((dataLength>>8)&0xFF00);
            param[index++] = (byte) (dataLength&0x00FF);
            byte[] crc = crc_16(param, param.length - 2);
            System.arraycopy(crc, 0, param, index, 2);
            Log.d(TAG, "发送读指令："+ StringUtil.byte2HexStr(param));
            if(!device.isUsbOpen())device.openUsbPort();
            int ret = -1;
            if(device.isUsbOpen())
                ret = device.write(param, 1000);
            if(ret <= 0){
                Log.e(TAG, "readDataFromPlc: 读指令发送不成功！");
                return null;
            }
            param = null;
            int totleLength = 0;
            byte[] data = new byte[8];
            param = new byte[16];
            boolean isCheckLength = false;
            while(true){
                long currTime = System.currentTimeMillis();
                if(currTime-startTime >= timeout) {
                    Log.e(TAG, "readDataFromPlc: time out");
                    break;
                }
                device.openUsbPort();
                ret = device.read(data, 300);
                if(ret >=0){
                    if(ret > param.length){
                        Log.e(TAG, "readDataFromPlc: the receive data is too long, please check the read length");
                        return null;
                    }
                    System.arraycopy(data, 0, param, totleLength, ret);
                    totleLength += ret;
                    if(D) Log.i(TAG, "readDataFromPlc: totleLength="+ totleLength+ " param="+ StringUtil.byte2HexStr(param));
                    if(!isCheckLength && totleLength >= 3){
                        isCheckLength = true;
                        //读取到数据长度字节
                        byte[] temp = new byte[totleLength];
                        System.arraycopy(param, 0, temp, 0, totleLength);
                        param = new byte[1+1+1+temp[2]+ 2];
                        System.arraycopy(temp, 0, param, 0, totleLength);
                        temp = null;
                        Log.i(TAG, "readDataFromPlc: after check length param="+ StringUtil.byte2HexStr(param));
                    }
                    if(totleLength == param.length){//数据接受完毕
                        totleLength = 0;
                        Log.d(TAG, "接收数据: "+ StringUtil.byte2HexStr(param));
                        crc = crc_16(param, param.length-2);
                        if(crc[0]!=param[param.length-2] || crc[1]!=param[param.length-1]){
                            Log.e(TAG, "readDataFromPlc: crc check is error, real crc="+ StringUtil.byte2HexStr(crc));
                            return null;
                        }
                        if(param[0] != desAds || param[1] != 3){
                            Log.e(TAG, "readDataFromPlc: reveive data is error");
                            return null;
                        }
                        int len = param[2];
                        data = null;
                        data = new byte[len];
                        System.arraycopy(param, 3, data, 0, len);
                        return data;
                    }
                }
            }
            return null;
        }
    }

    /**
     * 计算CRC16校验码
     *
     * @param data 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    private byte[] crc_16(byte[] data, int len) {
        Log.d(TAG, "crc_16: data="+ StringUtil.byte2HexStr(data)+ " length="+ len);
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        byte[] Rcvbuf = new byte[2];

        if(len<=0){
            CRC = 0;
        }else {
            for (i = 0; i < len; i++) {
                CRC ^= ((int) data[i] & 0x000000ff);
                for (j = 0; j < 8; j++) {
                    if ((CRC & 0x00000001) != 0) {
                        CRC >>= 1;
                        CRC ^= POLYNOMIAL;
                    } else {
                        CRC >>= 1;
                    }
                }
            }
        }
        Rcvbuf[0] = (byte)(CRC & 0x00ff);  //高位置
        Rcvbuf[1] = (byte)(((CRC & 0xff00)>>8)&0x00FF);//低位置
        return Rcvbuf;
    }
}
