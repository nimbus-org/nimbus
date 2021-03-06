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

import java.io.*;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * 何もしない暗号化サービス。<p>
 *
 * @author A.Kokubu
 */
public class NullCryptService extends ServiceBase
 implements NullCryptServiceMBean, Crypt {
    
    private static final long serialVersionUID = -4920171488896939493L;
    
    /**
     * 暗号化対象の文字列をそのまま返す。<p>
     * 
     * @param str 暗号化対象文字列
     * @return 暗号化文字列
     */
    public String doEncode(String str) {
        return str;
    }
    
    /**
     * 復号化対象の文字列をそのまま返す。<p>
     * 
     * @param str 復号化対象文字列
     * @return 復号化文字列
     */
    public String doDecode(String str) {
        return str;
    }
    
    public byte[] doEncodeBytes(byte[] bytes){
        return bytes;
    }
    
    public byte[] doDecodeBytes(byte[] bytes){
        return bytes;
    }
    
    public void doEncodeFile(String inFilePath, String outFilePath) throws IOException{
        FileInputStream fis = new FileInputStream(inFilePath);
        FileOutputStream fos = new FileOutputStream(outFilePath);
        try{
            doEncodeStream(fis, fos);
        }finally{
            fis.close();
            fos.close();
        }
    }
    
    public void doEncodeStream(InputStream is, OutputStream os) throws IOException{
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) > 0){
            baos.write(bytes, 0, length);
        }
        bytes = doEncodeBytes(baos.toByteArray());
        os.write(bytes, 0, bytes.length);
    }
    
    public void doDecodeFile(String inFilePath, String outFilePath) throws Exception{
        FileInputStream fis = new FileInputStream(inFilePath);
        FileOutputStream fos = new FileOutputStream(outFilePath);
        try{
            doDecodeStream(fis, fos);
        }finally{
            fis.close();
            fos.close();
        }
    }
    
    public void doDecodeStream(InputStream is, OutputStream os) throws Exception{
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) > 0){
            baos.write(bytes, 0, length);
        }
        bytes = doDecodeBytes(baos.toByteArray());
        os.write(bytes, 0, bytes.length);
    }
    
    public String doHash(String str) {
        return "";
    }
    public byte[] doHashBytes(byte[] bytes){
        return "".getBytes();
    }
    public byte[] doHashFile(String filePath) throws IOException{
        return "".getBytes();
    }
    public byte[] doHashStream(InputStream is) throws IOException{
        return "".getBytes();
    }
    
    public String doMac(String str) {
        return "";
    }
    public byte[] doMacBytes(byte[] bytes){
        return "".getBytes();
    }
    public byte[] doMacFile(String filePath) throws IOException{
        return "".getBytes();
    }
    public byte[] doMacStream(InputStream is) throws IOException{
        return "".getBytes();
    }
    
    public String doSign(String str){
        return "";
    }
    public byte[] doSignBytes(byte[] bytes){
        return "".getBytes();
    }
    public byte[] doSignFile(String filePath) throws IOException{
        return "".getBytes();
    }
    public byte[] doSignStream(InputStream is) throws IOException{
        return "".getBytes();
    }
    public boolean doVerify(String str, String sign){
        return true;
    }
    public boolean doVerifyBytes(byte[] bytes, byte[] sign){
        return true;
    }
    public boolean doVerifyFile(String filePath, byte[] sign) throws IOException{
        return true;
    }
    public boolean doVerifyStream(InputStream is, byte[] sign) throws IOException{
        return true;
    }
}
