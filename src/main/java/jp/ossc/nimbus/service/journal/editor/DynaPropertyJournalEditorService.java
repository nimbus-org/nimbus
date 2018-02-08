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

import org.apache.commons.beanutils.DynaProperty;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link DynaProperty}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DynaPropertyJournalEditorService
 extends BlockJournalEditorServiceBase
 implements DynaPropertyJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 7727362798894105781L;
    
    private static final String NAME_HEADER = "Name : ";
    private static final String TYPE_HEADER = "Type : ";
    private static final String IS_INDEXED_HEADER = "Is Indexed : ";
    private static final String IS_MAPPED_HEADER = "Is Mapped : ";
    
    protected static final String HEADER = "[DynaProperty]";
    
    private boolean isOutputName = true;
    private boolean isOutputType = true;
    private boolean isOutputIsIndexed = true;
    private boolean isOutputIsMapped = true;
    
    public DynaPropertyJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputName(boolean isOutput){
        isOutputName = isOutput;
    }
    
    public boolean isOutputName(){
        return isOutputName;
    }
    
    public void setOutputType(boolean isOutput){
        isOutputType = isOutput;
    }
    
    public boolean isOutputType(){
        return isOutputType;
    }
    
    public void setOutputIsIndexed(boolean isOutput){
        isOutputIsIndexed = isOutput;
    }
    
    public boolean isOutputIsIndexed(){
        return isOutputIsIndexed;
    }
    
    public void setOutputIsMapped(boolean isOutput){
        isOutputIsMapped = isOutput;
    }
    
    public boolean isOutputIsMapped(){
        return isOutputIsMapped;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        final DynaProperty prop = (DynaProperty)value;
        boolean isMake = false;
        if(isOutputName()){
            makeNameFormat(finder, key, prop, buf);
            isMake = true;
        }
        
        if(isOutputType()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeTypeFormat(finder, key, prop, buf);
            isMake = true;
        }
        
        if(isOutputIsIndexed()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeIsIndexedFormat(finder, key, prop, buf);
            isMake = true;
        }
        
        if(isOutputIsMapped()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeIsMappedFormat(finder, key, prop, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuffer makeNameFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuffer buf
    ){
        return buf.append(NAME_HEADER).append(prop.getName());
    }
    
    protected StringBuffer makeTypeFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuffer buf
    ){
        buf.append(TYPE_HEADER);
        makeObjectFormat(finder, null, prop.getType(), buf);
        return buf;
    }
    
    protected StringBuffer makeIsIndexedFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuffer buf
    ){
        return buf.append(IS_INDEXED_HEADER).append(prop.isIndexed());
    }
    
    protected StringBuffer makeIsMappedFormat(
        EditorFinder finder,
        Object key,
        DynaProperty prop,
        StringBuffer buf
    ){
        return buf.append(IS_MAPPED_HEADER).append(prop.isMapped());
    }
}