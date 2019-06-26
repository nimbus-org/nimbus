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
// パッケージ
package jp.ossc.nimbus.service.crypt;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * 固定の文字列を付加するだけの暗号サービス。<p>
 * 
 * @author A.Kokubu
 */
public class FixCryptService extends ServiceBase
 implements FixCryptServiceMBean, Crypt {
    
    private static final long serialVersionUID = 1117606145962089601L;
    
    private String fixPrefix;
    private byte[] fixPrefixBytes;
    private String fixSuffix;
    private byte[] fixSuffixBytes;
    
    public void setFixPrefix(String prefix){
        fixPrefix = prefix;
        fixPrefixBytes = prefix == null ? null : prefix.getBytes();
    }
    public String getFixPrefix(){
        return fixPrefix;
    }
    
    public void setFixSuffix(String suffix){
        fixSuffix = suffix;
        fixSuffixBytes = suffix == null ? null : suffix.getBytes();
    }
    public String getFixSuffix(){
        return fixSuffix;
    }
    
    /**
     * 暗号化対象の文字列に接頭語及び接尾語を付加して返す。<p>
     * 
     * @param str 暗号化対象文字列
     * @return 暗号化文字列
     */
    public String doEncode(String str) {
        if((fixPrefix == null || fixPrefix.length() == 0)
            && (fixSuffix == null || fixSuffix.length() == 0)){
            return str;
        }
        StringBuilder buf = new StringBuilder();
        if(fixPrefix != null && fixPrefix.length() != 0){
            buf.append(fixPrefix);
        }
        buf.append(str);
        if(fixSuffix != null && fixSuffix.length() != 0){
            buf.append(fixSuffix);
        }
        return buf.toString();
    }
    
    public byte[] doEncodeBytes(byte[] bytes){
        if((fixPrefixBytes == null || fixPrefixBytes.length == 0)
            && (fixSuffixBytes == null || fixSuffixBytes.length == 0)){
            return bytes;
        }
        byte[] result = bytes;
        if(fixPrefixBytes != null && fixPrefixBytes.length != 0){
            byte[] newBytes = new byte[fixPrefixBytes.length + result.length];
            System.arraycopy(fixPrefixBytes, 0, newBytes, 0, fixPrefixBytes.length);
            System.arraycopy(result, 0, newBytes, fixPrefixBytes.length, result.length);
            result = newBytes;
        }
        if(fixSuffixBytes != null && fixSuffixBytes.length != 0){
            byte[] newBytes = new byte[result.length + fixSuffixBytes.length];
            System.arraycopy(result, 0, newBytes, 0, result.length);
            System.arraycopy(fixSuffixBytes, 0, newBytes, result.length, fixSuffixBytes.length);
            result = newBytes;
        }
        return result;
    }
    
    /**
     * 復号化対象の文字列から接頭語及び接尾語を削って返す。<p>
     * 
     * @param str 復号化対象文字列
     * @return 復号化文字列
     */
    public String doDecode(String str){
        if((fixPrefix == null || fixPrefix.length() == 0)
            && (fixSuffix == null || fixSuffix.length() == 0)){
            return str;
        }
        String tmp = str;
        if(fixPrefix != null && fixPrefix.length() != 0
            && tmp.startsWith(fixPrefix)){
            tmp = tmp.substring(fixPrefix.length());
        }
        if(fixSuffix != null && fixSuffix.length() != 0
            && tmp.endsWith(fixSuffix)){
            tmp = tmp.substring(0, tmp.length() - fixSuffix.length());
        }
        return tmp;
    }
    
    public byte[] doDecodeBytes(byte[] bytes){
        if((fixPrefixBytes == null || fixPrefixBytes.length == 0)
            && (fixSuffixBytes == null || fixSuffixBytes.length == 0)){
            return bytes;
        }
        byte[] result = bytes;
        if(fixPrefixBytes != null && fixPrefixBytes.length != 0){
            boolean isMatch = true;
            for(int i = 0; i < fixPrefixBytes.length; i++){
                if(i >= result.length || result[i] != fixPrefixBytes[i]){
                    isMatch = false;
                    break;
                }
            }
            if(isMatch){
                byte[] newBytes = new byte[result.length - fixPrefixBytes.length];
                System.arraycopy(result, fixPrefixBytes.length, newBytes, 0, newBytes.length);
                result = newBytes;
            }
        }
        if(fixSuffixBytes != null && fixSuffixBytes.length != 0){
            boolean isMatch = true;
            for(int i = 0; i < fixSuffixBytes.length; i++){
                if(i >= result.length || result[result.length - 1 - i] != fixSuffixBytes[fixSuffixBytes.length - 1 - i]){
                    isMatch = false;
                    break;
                }
            }
            if(isMatch){
                byte[] newBytes = new byte[result.length - fixSuffixBytes.length];
                System.arraycopy(result, 0, newBytes, 0, newBytes.length);
                result = newBytes;
            }
        }
        return result;
    }
    
    /**
     * ハッシュ対象の文字列をそのまま返す。<p>
     * 
     * @param str ハッシュ対象文字列
     * @return ハッシュ対象文字列
     */
    public String doHash(String str) {
        return str;
    }
    
    /**
     * ハッシュ対象のバイト配列をそのまま返す。<p>
     * 
     * @param bytes ハッシュ対象バイト配列
     * @return ハッシュ対象バイト配列
     */
    public byte[] doHashBytes(byte[] bytes){
        return bytes;
    }
    
    /**
     * 対象の文字列をそのまま返す。<p>
     * 
     * @param str 対象文字列
     * @return 対象文字列
     */
    public String doMac(String str) {
        return str;
    }
    
    /**
     * 対象のバイト配列をそのまま返す。<p>
     * 
     * @param bytes 対象バイト配列
     * @return 対象バイト配列
     */
    public byte[] doMacBytes(byte[] bytes){
        return bytes;
    }
}
