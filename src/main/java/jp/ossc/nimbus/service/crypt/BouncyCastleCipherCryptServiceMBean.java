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

/**
 * {@link BouncyCastleCipherCryptService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see BouncyCastleCipherCryptService
 */
public interface BouncyCastleCipherCryptServiceMBean extends CipherCryptServiceMBean{
    
    /**
     * 公開鍵のPEM(Privacy-enhanced mail)形式文字列を設定する。<p>
     *
     * @param str 公開鍵の文字列
     */
    public void setPublicKeyStringPEM(String str);
    
    /**
     * 公開鍵のPEM(Privacy-enhanced mail)形式文字列を取得する。<p>
     *
     * @return 公開鍵の文字列
     */
    public String getPublicKeyStringPEM();
    
    /**
     * 公開鍵のPEM(Privacy-enhanced mail)形式ファイルのパスを設定する。<p>
     *
     * @param path 公開鍵のファイルのパス
     */
    public void setPublicKeyFilePEM(String path);
    
    /**
     * 公開鍵のPEM(Privacy-enhanced mail)形式ファイルのパスを取得する。<p>
     *
     * @return 公開鍵のファイルのパス
     */
    public String getPublicKeyFilePEM();
    
    /**
     * 非公開鍵のPEM(Privacy-enhanced mail)形式文字列を設定する。<p>
     *
     * @param str 非公開鍵の文字列
     */
    public void setPrivateKeyStringPEM(String str);
    
    /**
     * 非公開鍵のPEM(Privacy-enhanced mail)形式文字列を取得する。<p>
     *
     * @return 非公開鍵の文字列
     */
    public String getPrivateKeyStringPEM();
    
    /**
     * 非公開鍵のPEM(Privacy-enhanced mail)形式ファイルのパスを設定する。<p>
     *
     * @param path 非公開鍵のファイルのパス
     */
    public void setPrivateKeyFilePEM(String path);
    
    /**
     * 非公開鍵のPEM(Privacy-enhanced mail)形式ファイルのパスを取得する。<p>
     *
     * @return 非公開鍵のファイルのパス
     */
    public String getPrivateKeyFilePEM();
    
    /**
     * 非公開鍵をPEM(Privacy-enhanced mail)形式に変換する。<p>
     *
     * @return PEM形式の非公開鍵
     */
    public String privateKeyToPEM();
    
    /**
     * 公開鍵をPEM(Privacy-enhanced mail)形式に変換する。<p>
     *
     * @return PEM形式の公開鍵
     */
    public String publicKeyToPEM();
}
