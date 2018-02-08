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
 * JournalServletResponseWrapperオブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class ServletResponseWrapperJournalEditorService
 extends ServletResponseJournalEditorService
 implements ServletResponseWrapperJournalEditorServiceMBean,
            Serializable{
    
    private static final long serialVersionUID = 6136120907874155531L;
    
    private static final String CONTENT_LENGTH_HEADER = "Content Length : ";
    private static final String CONTENT_HEADER = "Content : ";
    
    private static final String HEADER = "[ServletResponse]";
    
    private boolean isOutputContentLength = true;
    private boolean isOutputContent = true;
    
    public ServletResponseWrapperJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputContentLength(boolean isOutput){
        isOutputContentLength = isOutput;
    }
    
    public boolean isOutputContentLength(){
        return isOutputContentLength;
    }
    
    public void setOutputContent(boolean isOutput){
        isOutputContent = isOutput;
    }
    
    public boolean isOutputContent(){
        return isOutputContent;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        boolean isMake = super.processBlock(finder, key, value, buf);
        final JournalServletResponseWrapper response
             = (JournalServletResponseWrapper)value;
        if(isOutputContentLength()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentLengthFormat(finder, key, response, buf);
            isMake = true;
        }
        
        if(isOutputContent()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeContentFormat(finder, key, response, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuffer makeContentLengthFormat(
        EditorFinder finder,
        Object key,
        JournalServletResponseWrapper response,
        StringBuffer buf
    ){
        return buf.append(CONTENT_LENGTH_HEADER)
            .append(response.getContentLength());
    }
    
    protected StringBuffer makeContentFormat(
        EditorFinder finder,
        Object key,
        JournalServletResponseWrapper response,
        StringBuffer buf
    ){
        return buf.append(CONTENT_HEADER)
            .append(response.getContent());
    }
}
