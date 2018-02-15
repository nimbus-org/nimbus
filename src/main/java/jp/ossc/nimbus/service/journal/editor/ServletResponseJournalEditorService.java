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
import javax.servlet.ServletResponse;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * ServletResponseオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ServletResponseJournalEditorService
 extends BlockJournalEditorServiceBase
 implements ServletResponseJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -540346255624096477L;
    
    private static final String BUFFER_SIZE_HEADER = "Buffer Size : ";
    private static final String CHARACTER_ENCODING_HEADER
         = "Character Encoding : ";
    private static final String CONTENT_TYPE_HEADER = "Content Type : ";
    private static final String LOCALE_HEADER = "Locale : ";
    private static final String IS_COMMITTED_HEADER = "Is Committed : ";
    
    private static final String HEADER = "[ServletResponse]";
    
    private boolean isOutputBufferSize = true;
    private boolean isOutputCharacterEncoding = true;
    private boolean isOutputContentType = true;
    private boolean isOutputLocale = true;
    private boolean isOutputIsCommitted = true;
    
    public ServletResponseJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputBufferSize(boolean isOutput){
        isOutputBufferSize = isOutput;
    }
    
    public boolean isOutputBufferSize(){
        return isOutputBufferSize;
    }
    
    public void setOutputCharacterEncoding(boolean isOutput){
        isOutputCharacterEncoding = isOutput;
    }
    
    public boolean isOutputCharacterEncoding(){
        return isOutputCharacterEncoding;
    }
    
    public void setOutputContentType(boolean isOutput){
        isOutputContentType = isOutput;
    }
    
    public boolean isOutputContentType(){
        return isOutputContentType;
    }
    
    public void setOutputLocale(boolean isOutput){
        isOutputLocale = isOutput;
    }
    
    public boolean isOutputLocale(){
        return isOutputLocale;
    }
    
    public void setOutputIsCommitted(boolean isOutput){
        isOutputIsCommitted = isOutput;
    }
    
    public boolean isOutputIsCommitted(){
        return isOutputIsCommitted;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final ServletResponse response = (ServletResponse)value;
        boolean isMake = false;
        if(isOutputBufferSize()){
            makeBufferSizeFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputCharacterEncoding()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCharacterEncodingFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputContentType()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentTypeFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputLocale()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeLocaleFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputIsCommitted()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeIsCommittedFormat(finder, key, response, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeBufferSizeFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuilder buf
    ){
        return buf.append(BUFFER_SIZE_HEADER)
            .append(response.getBufferSize());
    }
    
    protected StringBuilder makeCharacterEncodingFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuilder buf
    ){
        return buf.append(CHARACTER_ENCODING_HEADER)
            .append(response.getCharacterEncoding());
    }
    
    protected StringBuilder makeContentTypeFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuilder buf
    ){
        try{
            return buf.append(CONTENT_TYPE_HEADER)
                .append(response.getContentType());
        }catch(NoSuchMethodError e){
            return buf;
        }
    }
    
    protected StringBuilder makeLocaleFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuilder buf
    ){
        buf.append(LOCALE_HEADER);
        makeObjectFormat(
            finder,
            null,
            response.getLocale(),
            buf
        );
        return buf;
    }
    
    protected StringBuilder makeIsCommittedFormat(
        EditorFinder finder,
        Object key,
        ServletResponse response,
        StringBuilder buf
    ){
        return buf.append(IS_COMMITTED_HEADER)
            .append(response.isCommitted());
    }
}
