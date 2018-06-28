/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.util.validator;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * IPアドレスバリデータ。<p>
 * 
 * @author M.Takata
 */
public class IPAddressValidator extends AbstractStringValidator
 implements java.io.Serializable{
    
    private static final long serialVersionUID = 3065754957685928501L;
    
    protected String ipCidr;
    protected InetAddress inetAddress;
    protected InetAddress startAddress;
    protected InetAddress endAddress;
    protected int prefixLength;
    
    /**
     * 検証に使用するIP/CIDRを設定する。<p>
     *
     * @param ipCidr IP/CIDR文字列
     */
    public void setIPCIDR(String ipCidr) throws UnknownHostException, IllegalArgumentException{
        int index = ipCidr.indexOf("/");
        if(index != -1){
            String address = ipCidr.substring(0, index);
            String network = ipCidr.substring(index + 1);
            
            inetAddress = InetAddress.getByName(address);
            prefixLength = Integer.parseInt(network);
            
            ByteBuffer maskBuf = null;
            int targetSize = 0;
            if(inetAddress.getAddress().length == 4){
                maskBuf = ByteBuffer.allocate(4).putInt(-1);
                targetSize = 4;
            }else{
                maskBuf = ByteBuffer.allocate(16).putLong(-1L).putLong(-1L);
                targetSize = 16;
            }
            BigInteger mask = (new BigInteger(1, maskBuf.array())).not().shiftRight(prefixLength);
            
            ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
            BigInteger ipVal = new BigInteger(1, buffer.array());
            
            BigInteger startIp = ipVal.and(mask);
            BigInteger endIp = startIp.add(mask.not());
            
            byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
            byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);
            
            startAddress = InetAddress.getByAddress(startIpArr);
            endAddress = InetAddress.getByAddress(endIpArr);
            this.ipCidr = ipCidr;
        }else{
            throw new IllegalArgumentException("Illegal IP/CIDR format : " + ipCidr);
        }
    }
    
    /**
     * 検証に使用するIP/CIDRを取得する。<p>
     *
     * @return IP/CIDR文字列
     */
    public String getIPCIDR(){
        return ipCidr;
    }
    
    private byte[] toBytes(byte[] array, int targetSize){
        byte[] ret = new byte[targetSize];
        for(int i = 1; i <= targetSize && array.length >= i; i++){
            ret[ret.length - i] = array[array.length - i];
        }
        return ret;
    }
    
    protected String toString(Object obj){
        if(obj == null){
            return null;
        }
        if(obj instanceof InetAddress){
            return ((InetAddress)obj).getHostAddress();
        }else if(obj instanceof InetSocketAddress){
            return ((InetSocketAddress)obj).getAddress().getHostAddress();
        }else{
            return super.toString(obj);
        }
    }
    
    /**
     * 指定された文字列が正規表現にマッチするかどうかを検証する。<p>
     *
     * @param str 検証対象の文字列
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    protected boolean validateString(String str) throws ValidateException{
        if(ipCidr != null){
            try{
                InetAddress address = InetAddress.getByName(str);
                BigInteger start = new BigInteger(1, startAddress.getAddress());
                BigInteger end = new BigInteger(1, endAddress.getAddress());
                BigInteger target = new BigInteger(1, address.getAddress());
                
                int st = start.compareTo(target);
                int te = target.compareTo(end);
                
                return (st == -1 || st == 0) && (te == -1 || te == 0);
            }catch(UnknownHostException e){
                throw new ValidateException(e);
            }
        }else{
            throw new ValidateException("IP/CIDR is null.");
        }
    }
}