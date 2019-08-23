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
package jp.ossc.nimbus.util.converter;

import java.io.*;
import java.security.*;

/**
 * メッセージダイジェスト文字列コンバータ。<p>
 * メッセージ・ダイジェスト・アルゴリズムで、任意サイズのデータを固定長のハッシュ値に変換する。<br>
 *
 * @author M.Takata
 */
public class MessageDigestStringConverter extends StringStreamConverter implements StringConverter, Serializable{
    
    private static final long serialVersionUID = -6580612870754304286l;
    
    /**
     * デフォルトの文字エンコーディング。<p>
     */
    public static final String DEFAULT_ENCODING = "ISO_8859-1";
    
    /**
     * デフォルトのハッシュアルゴリズム名。<p>
     */
    public static final String DEFAULT_HASH_ALGORITHM = "MD5";
    
    protected Provider messageDigestProvider;
    protected String messageDigestProviderName;
    protected String characterEncoding = DEFAULT_ENCODING;
    protected String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
    
    /**
     * 変換時に使用する文字エンコーディングを設定する。<p>
     * デフォルトは、{@link #DEFAULT_ENCODING}。<br>
     * 
     * @param encoding 文字エンコーディング
     */
    public void setCharacterEncoding(String encoding){
        characterEncoding = encoding;
    }
    
    /**
     * 変換時に使用する文字エンコーディングを取得する。<p>
     * 
     * @return 文字エンコーディング
     */
    public String getCharacterEncoding(){
        return characterEncoding;
    }
    
    /**
     * ハッシュアルゴリズムを設定する。<p>
     * デフォルトは、{@link #DEFAULT_HASH_ALGORITHM}。<br>
     *
     * @param algorithm ハッシュアルゴリズム
     */
    public void setHashAlgorithm(String algorithm){
        hashAlgorithm = algorithm;
    }
    
    /**
     * ハッシュアルゴリズムを取得する。<p>
     *
     * @return ハッシュアルゴリズム
     */
    public String getHashAlgorithm(){
        return hashAlgorithm;
    }
    
    /**
     * メッセージダイジェストのプロバイダを設定する。<p>
     * デフォルトは、インストールされているプロバイダの中から最優先のプロバイダが自動選択される。<br>
     *
     * @param p プロバイダ
     */
    public void setMessageDigestProvider(Provider p){
        messageDigestProvider = p;
    }
    
    /**
     * メッセージダイジェストのプロバイダを取得する。<p>
     *
     * @return プロバイダ
     */
    public Provider getMessageDigestProvider(){
        return messageDigestProvider;
    }
    
    /**
     * メッセージダイジェストのプロバイダ名を設定する。<p>
     * デフォルトは、インストールされているプロバイダの中から最優先のプロバイダが自動選択される。<br>
     *
     * @param name プロバイダ名
     */
    public void setMessageDigestProviderName(String name){
        messageDigestProviderName = name;
    }
    
    /**
     * メッセージダイジェストのプロバイダ名を取得する。<p>
     *
     * @return プロバイダ名
     */
    public String getMessageDigestProviderName(){
        return messageDigestProviderName;
    }
    
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }
        if(obj instanceof InputStream){
            return convertToObject((InputStream)obj);
        }else if(obj instanceof byte[]){
            return convertToObject(new ByteArrayInputStream((byte[])obj));
        }else{
            return convert(obj.toString());
        }
    }
    
    public String convert(String str) throws ConvertException{
        if(str == null){
            return null;
        }
        try{
            return toHexString(
                createMessageDigest().digest(
                    str.getBytes(characterEncoding)
                )
            );
        }catch(UnsupportedEncodingException e){
            throw new ConvertException(e);
        }
    }
    
    protected MessageDigest createMessageDigest() throws ConvertException{
        MessageDigest messageDigest = null;
        try{
            if(messageDigestProvider != null){
                messageDigest = MessageDigest.getInstance(
                    hashAlgorithm,
                    messageDigestProvider
                );
            }else if(messageDigestProviderName != null){
                messageDigest = MessageDigest.getInstance(
                    hashAlgorithm,
                    messageDigestProviderName
                );
            }else{
                messageDigest = MessageDigest.getInstance(hashAlgorithm);
            }
        }catch(NoSuchAlgorithmException e){
            throw new ConvertException(e);
        }catch(NoSuchProviderException e){
            throw new ConvertException(e);
        }
        return messageDigest;
    }
    
    protected byte[] convertToByteArray(Object obj) throws ConvertException{
        try{
            return obj == null ? null : createMessageDigest().digest(obj.toString().getBytes(characterEncoding));
        }catch(UnsupportedEncodingException e){
            throw new ConvertException(e);
        }
    }
    
    public Object convertToObject(InputStream is) throws ConvertException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length = 0;
        try{
            while((length = is.read(bytes, 0, bytes.length)) > 0){
                baos.write(bytes, 0, length);
            }
        }catch(IOException e){
            throw new ConvertException(e);
        }
        
        return toHexString(createMessageDigest().digest(baos.toByteArray()));
    }
    
    /**
     * 指定されたバイト配列を16進数の文字列に変換する。<p>
     *
     * @param bytes バイト配列
     * @return 16進数文字列
     */
    protected static String toHexString(byte[] bytes){
        final StringBuilder buf = new StringBuilder();
        for(int i = 0, max = bytes.length; i < max; i++){
            int intValue = bytes[i];
            intValue &= 0x000000FF;
            final String str = Integer.toHexString(intValue).toUpperCase();
            if(str.length() == 1){
                buf.append('0');
            }
            buf.append(str);
        }
        return buf.toString();
    }
}
