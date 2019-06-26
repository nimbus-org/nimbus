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
package jp.ossc.nimbus.service.crypt;

import java.io.*;

/**
 * 暗号化。<p>
 *
 * @author A.Kokubu
 */
public interface Crypt {
    
    /**
     * 文字列を暗号化する。<p>
     * 
     * @param str 暗号化対象文字列
     * @return 暗号化文字列
     */
    public String doEncode(String str);
    
    /**
     * 文字列を復号化する。<p>
     * 
     * @param str 復号化対象文字列
     * @return 復号化文字列
     */
    public String doDecode(String str) throws Exception;
    
    /**
     * バイト配列を暗号化する。<p>
     * 
     * @param bytes 暗号化対象バイト配列
     * @return 暗号化バイト配列
     */
    public byte[] doEncodeBytes(byte[] bytes);
    
    /**
     * バイト配列を復号化する。<p>
     * 
     * @param bytes 復号化対象バイト配列
     * @return 復号化バイト配列
     */
    public byte[] doDecodeBytes(byte[] bytes) throws Exception;
    
    /**
     * ファイルを暗号化する。<p>
     * 
     * @param inFilePath 暗号化対象のファイルのパス
     * @param outFilePath 暗号化後のファイルのパス
     */
    public void doEncodeFile(String inFilePath, String outFilePath) throws Exception;
    
    /**
     * ファイルを暗号化する。<p>
     * 
     * @param is 暗号化対象の入力ストリーム
     * @param os 暗号化後の出力ストリーム
     */
    public void doEncodeStream(InputStream is, OutputStream os) throws Exception;
    
    /**
     * ファイルを復号化する。<p>
     * 
     * @param inFilePath 復号化対象のファイルのパス
     * @param outFilePath 復号化後のファイルのパス
     */
    public void doDecodeFile(String inFilePath, String outFilePath) throws Exception;
    
    /**
     * ファイルを復号化する。<p>
     * 
     * @param is 復号化対象の入力ストリーム
     * @param os 復号化後の出力ストリーム
     */
    public void doDecodeStream(InputStream is, OutputStream os) throws Exception;
    
    /**
     * 文字列をハッシュする。<p>
     * 
     * @param str ハッシュ対象文字列
     * @return ハッシュ文字列
     */
    public String doHash(String str);
    
    /**
     * バイト配列をハッシュする。<p>
     * 
     * @param bytes ハッシュ対象バイト配列
     * @return ハッシュバイト配列
     */
    public byte[] doHashBytes(byte[] bytes);
    
    /**
     * 文字列のメッセージ認証コードを取得する。<p>
     * 
     * @param str 対象文字列
     * @return メッセージ認証コード文字列
     */
    public String doMac(String str);
    
    /**
     * バイト配列のメッセージ認証コードを取得する。<p>
     * 
     * @param bytes 対象バイト配列
     * @return メッセージ認証コードバイト配列
     */
    public byte[] doMacBytes(byte[] bytes);
}
