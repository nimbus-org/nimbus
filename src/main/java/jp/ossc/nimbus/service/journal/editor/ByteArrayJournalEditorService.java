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
package jp.ossc.nimbus.service.journal.editor;

import java.io.Serializable;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;
import jp.ossc.nimbus.util.converter.StringConverter;
import jp.ossc.nimbus.util.converter.ConvertException;

/**
 * バイト配列を文字列にフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ByteArrayJournalEditorService
 extends ImmutableJournalEditorServiceBase
 implements ByteArrayJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -5535571335184302651L;
    
    private static final int CONVERT_HEX_VALUE = 0;
    private static final int CONVERT_DECIMAL_VALUE = 1;
    private static final int CONVERT_OCTAL_VALUE = 2;
    private static final int CONVERT_ENCODE_VALUE = 3;
    private static final int CONVERT_LENGTH_VALUE = 4;
    
    private String convertMode = CONVERT_HEX;
    private int convertModeValue;
    private String encode;
    private ServiceName stringConverterServiceName;
    private StringConverter stringConverter;
    
    public void setConvertMode(String mode){
        if(mode != null){
            convertMode = mode;
        }
    }
    public String getConvertMode(){
        return convertMode;
    }
    
    public void setStringConverterServiceName(ServiceName name){
        stringConverterServiceName = name;
    }
    
    public ServiceName getStringConverterServiceName(){
        return stringConverterServiceName;
    }
    
    public void setStringConverter(StringConverter converter){
        stringConverter = converter;
    }
    
    public void startService() throws Exception{
        super.startService();
        if(convertMode.equals(CONVERT_HEX)){
            convertModeValue = CONVERT_HEX_VALUE;
        }else if(convertMode.equals(CONVERT_DECIMAL)){
            convertModeValue = CONVERT_DECIMAL_VALUE;
        }else if(convertMode.equals(CONVERT_OCTAL)){
            convertModeValue = CONVERT_OCTAL_VALUE;
        }else if(convertMode.equals(CONVERT_LENGTH)){
            convertModeValue = CONVERT_LENGTH_VALUE;
        }else{
            new String(new byte[0], convertMode);
            encode = convertMode;
            convertModeValue = CONVERT_ENCODE_VALUE;
            if(stringConverterServiceName != null){
                stringConverter = (StringConverter)ServiceManagerFactory.getServiceObject(stringConverterServiceName);
            }
        }
    }
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final byte[] bytes = (byte[])value;
        switch(convertModeValue){
        case CONVERT_HEX_VALUE:
            makeHexStringFormat(finder, key, bytes, buf);
            break;
        case CONVERT_DECIMAL_VALUE:
            makeDecimalStringFormat(finder, key, bytes, buf);
            break;
        case CONVERT_OCTAL_VALUE:
            makeOctalStringFormat(finder, key, bytes, buf);
            break;
        case CONVERT_LENGTH_VALUE:
            makeLengthStringFormat(finder, key, bytes, buf);
            break;
        default:
            makeEncodeStringFormat(finder, key, bytes, buf);
            break;
        }
        return buf.toString();
    }
    
    protected StringBuilder makeHexStringFormat(
        EditorFinder finder,
        Object key,
        byte[] bytes,
        StringBuilder buf
    ){
        for(int i = 0; i < bytes.length; i++){
            String hex = Integer.toHexString(((int)bytes[i]) & 0x000000FF).toUpperCase();
            if(hex.length() == 1){
                buf.append('0');
            }
            buf.append(hex);
            if(i != bytes.length - 1){
                buf.append(' ');
            }
        }
        return buf;
    }
    
    protected StringBuilder makeDecimalStringFormat(
        EditorFinder finder,
        Object key,
        byte[] bytes,
        StringBuilder buf
    ){
        for(int i = 0; i < bytes.length; i++){
            String decimal = Byte.toString(bytes[i]);
            buf.append(decimal);
            if(i != bytes.length - 1){
                buf.append(' ');
            }
        }
        return buf;
    }
    
    protected StringBuilder makeOctalStringFormat(
        EditorFinder finder,
        Object key,
        byte[] bytes,
        StringBuilder buf
    ){
        for(int i = 0; i < bytes.length; i++){
            String octal = Integer.toOctalString(((int)bytes[i]) & 0x000000FF).toUpperCase();
            if(octal.length() == 1){
                buf.append('0').append('0');
            }else if(octal.length() == 2){
                buf.append('0');
            }
            buf.append(octal);
            if(i != bytes.length - 1){
                buf.append(' ');
            }
        }
        return buf;
    }
    
    protected StringBuilder makeEncodeStringFormat(
        EditorFinder finder,
        Object key,
        byte[] bytes,
        StringBuilder buf
    ){
        try{
            String str = new String(bytes, encode);
            if(stringConverter != null && str != null){
                try{
                    str = stringConverter.convert(str);
                }catch(ConvertException e){
                }
            }
            buf.append(str);
        }catch(java.io.UnsupportedEncodingException e){
            // 起こらない
        }
        return buf;
    }
    
    protected StringBuilder makeLengthStringFormat(
        EditorFinder finder,
        Object key,
        byte[] bytes,
        StringBuilder buf
    ){
        return buf.append(Byte.TYPE.getName()).append('[').append(bytes.length).append(']');
    }
}
