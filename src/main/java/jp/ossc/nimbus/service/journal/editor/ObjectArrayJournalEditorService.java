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

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * オブジェクト配列をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ObjectArrayJournalEditorService
 extends ImmutableJournalEditorServiceBase
 implements ObjectArrayJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -2498452790434728627L;
    
    private String startDelimiter = "{";
    private String endDelimiter = "}";
    private String delimiter = ", ";
    private boolean isNoDelimiterWhenSizeZero;
    private String startValueDelimiter = "\"";
    private String endValueDelimiter = "\"";
    
    public void setStartDelimiter(String delim){
        startDelimiter = delim;
    }
    public String getStartDelimiter(){
        return startDelimiter;
    }
    
    public void setEndDelimiter(String delim){
        endDelimiter = delim;
    }
    public String getEndDelimiter(){
        return endDelimiter;
    }
    
    public void setDelimiter(String delim){
        delimiter = delim;
    }
    public String getDelimiter(){
        return delimiter;
    }
    
    public void setStartValueDelimiter(String delim){
        startValueDelimiter = delim;
    }
    public String getStartValueDelimiter(){
        return startValueDelimiter;
    }
    
    public void setEndValueDelimiter(String delim){
        endValueDelimiter = delim;
    }
    public String getEndValueDelimiter(){
        return endValueDelimiter;
    }
    
    public void setNoDelimiterWhenNoArray(boolean isNoDelimiter){
        isNoDelimiterWhenSizeZero = isNoDelimiter;
    }
    public boolean isNoDelimiterWhenNoArray(){
        return isNoDelimiterWhenSizeZero;
    }
    
    protected String toString(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final Object[] objs = (Object[])value;
        if(objs.length > 1 || !isNoDelimiterWhenSizeZero){
            buf.append(startDelimiter);
        }
        for(int i = 0; i < objs.length; i++){
            if(objs[i] != null){
                buf.append(startValueDelimiter);
            }
            makeObjectFormat(
                finder,
                null,
                objs[i],
                buf
            );
            if(objs[i] != null){
                buf.append(endValueDelimiter);
            }
            if(i != objs.length - 1){
                buf.append(delimiter);
            }
        }
        if(objs.length > 1 || !isNoDelimiterWhenSizeZero){
            buf.append(endDelimiter);
        }
        return buf.toString();
    }
}
