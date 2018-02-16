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
package jp.ossc.nimbus.service.scheduler2;

import java.io.Serializable;

/**
 * 集配信リクエスト。<p>
 * 
 * @author M.Takata
 */
public class ConcentrateRequest implements Serializable{
    
    private static final long serialVersionUID = -5534337631451607056L;
    
    /**
     * 集配信種別文字列：収集。<p>
     */
    public static final String PROCESS_TYPE_GET = "GET";
    
    /**
     * 集配信種別文字列：配信。<p>
     */
    public static final String PROCESS_TYPE_PUT = "PUT";
    
    /**
     * 集配信種別文字列：転送。<p>
     */
    public static final String PROCESS_TYPE_FORWARD = "FORWARD";
    
    /**
     * 集配信種別：収集。<p>
     */
    public static final int PROCESS_TYPE_VALUE_GET = 1;
    
    /**
     * 集配信種別：配信。<p>
     */
    public static final int PROCESS_TYPE_VALUE_PUT = 2;
    
    /**
     * 集配信種別：転送。<p>
     */
    public static final int PROCESS_TYPE_VALUE_FORWARD = 3;
    
    private String key;
    private String source;
    private String destination;
    private int processType;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public ConcentrateRequest(){
    }
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param key キー
     * @param type 集配信種別
     * @param src 集配信元情報
     * @param dest 集配信宛先情報
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public ConcentrateRequest(String key, int type, String src, String dest){
        this.key = key;
        source = src;
        destination = dest;
        processType = type;
    }
    
    /**
     * キーを取得する。<p>
     *
     * @return キー
     */
    public String getKey(){
        return key;
    }
    
    /**
     * キーを設定する。<p>
     *
     * @param key キー
     */
    public void setKey(String key){
        this.key = key;
    }
    
    /**
     * 集配信元情報を取得する。<p>
     *
     * @return 集配信元情報
     */
    public String getSource(){
        return source;
    }
    
    /**
     * 集配信元情報を設定する。<p>
     *
     * @param src 集配信元情報
     */
    public void setSource(String src){
        source = src;
    }
    
    /**
     * 集配信宛先情報を取得する。<p>
     *
     * @return 集配信宛先情報
     */
    public String getDestination(){
        return destination;
    }
    
    /**
     * 集配信宛先情報を設定する。<p>
     *
     * @param dest 集配信宛先情報
     */
    public void setDestination(String dest){
        destination = dest;
    }
    
    /**
     * 集配信種別を取得する。<p>
     *
     * @return 集配信種別
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public int getProcessType(){
        return processType;
    }
    
    /**
     * 集配信種別を設定する。<p>
     *
     * @param type 集配信種別
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public void setProcessType(int type){
        processType = type;
    }
    
    /**
     * 集配信種別を集配信種別文字列に変換する。<p>
     *
     * @param type 集配信種別
     * @return 集配信種別文字列
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     * @see #PROCESS_TYPE_GET
     * @see #PROCESS_TYPE_PUT
     * @see #PROCESS_TYPE_FORWARD
     */
    public static String toProcessTypeString(int type){
        switch(type){
        case PROCESS_TYPE_VALUE_GET:
            return PROCESS_TYPE_GET;
        case PROCESS_TYPE_VALUE_PUT:
            return PROCESS_TYPE_PUT;
        case PROCESS_TYPE_VALUE_FORWARD:
            return PROCESS_TYPE_FORWARD;
        default:
            return null;
        }
    }
    
    /**
     * 集配信種別文字列を集配信種別に変換する。<p>
     *
     * @param type 集配信種別文字列
     * @return 集配信種別
     * @see #PROCESS_TYPE_GET
     * @see #PROCESS_TYPE_PUT
     * @see #PROCESS_TYPE_FORWARD
     * @see #PROCESS_TYPE_VALUE_GET
     * @see #PROCESS_TYPE_VALUE_PUT
     * @see #PROCESS_TYPE_VALUE_FORWARD
     */
    public static int toProcessType(String type){
        if(type == null){
            return 0;
        }else if(PROCESS_TYPE_GET.equals(type.toUpperCase())){
            return PROCESS_TYPE_VALUE_GET;
        }else if(PROCESS_TYPE_PUT.equals(type.toUpperCase())){
            return PROCESS_TYPE_VALUE_PUT;
        }else if(PROCESS_TYPE_FORWARD.equals(type.toUpperCase())){
            return PROCESS_TYPE_VALUE_FORWARD;
        }else{
            return 0;
        }
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append("{key=").append(key);
        buf.append(",source=").append(source);
        buf.append(",destination=").append(destination);
        buf.append(",source=").append(source);
        buf.append(",processType=").append(toProcessTypeString(processType)).append('}');
        return buf.toString();
    }
}