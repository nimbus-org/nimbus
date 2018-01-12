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
 * {@link MethodJournalData}オブジェクトをフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class MethodJournalEditorService extends ItemJournalEditorServiceBase
 implements MethodJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = -6371811932300251837L;
    
    private static final String OPEN_BRACKET = "( ";
    private static final String CLOSE_BRACKET = " )";
    private static final String IDENTIFYER_SEPARATOR = ".";
    private static final String TYPE_SEPARATOR = ", ";
    private static final String MESSAGE_SEPARATOR = " : ";
    
    private static final String ITEM_NAME = "Method : ";
    
    private boolean isOutputTarget = false;
    private boolean isOutputOwnerClass = true;
    private boolean isOutputParameterTypes = true;
    private boolean isOutputMessage = true;
    
    public MethodJournalEditorService(){
        super();
        setItemName(ITEM_NAME);
    }
    
    public void setOutputTarget(boolean isOutput){
        isOutputTarget = isOutput;
    }
    
    public boolean isOutputTarget(){
        return isOutputTarget;
    }
    
    public void setOutputOwnerClass(boolean isOutput){
        isOutputOwnerClass = isOutput;
    }
    
    public boolean isOutputOwnerClass(){
        return isOutputOwnerClass;
    }
    
    public void setOutputParameterTypes(boolean isOutput){
        isOutputParameterTypes = isOutput;
    }
    
    public boolean isOutputParameterTypes(){
        return isOutputParameterTypes;
    }
    
    public void setOutputMessage(boolean isOutput){
        isOutputMessage = isOutput;
    }
    
    public boolean isOutputMessage(){
        return isOutputMessage;
    }
    
    protected void processItem(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        final MethodJournalData methodData = (MethodJournalData)value;
        makeMethodJournalFormat(finder, key, methodData, buf);
    }
    
    protected StringBuffer makeMethodJournalFormat(
        EditorFinder finder,
        Object key,
        MethodJournalData data,
        StringBuffer buf
    ){
        if(isOutputMessage()){
            final String message = data.getMessage();
            if(message != null){
                buf.append(message);
                buf.append(MESSAGE_SEPARATOR);
            }
        }
        if(isOutputTarget()){
            makeObjectFormat(finder, null, data.getTarget(), buf);
            buf.append(MESSAGE_SEPARATOR);
        }
        if(isOutputOwnerClass()){
            makeObjectFormat(finder, null, data.getOwnerClass(), buf);
            buf.append(IDENTIFYER_SEPARATOR);
        }
        final String name = data.getName();
        buf.append(name);
        buf.append(OPEN_BRACKET);
        if(isOutputParameterTypes()){
            final Class[] types = data.getParameterTypes();
            if(types != null){
                for(int i = 0; i < types.length; i++){
                    makeObjectFormat(finder, key, types[i], buf);
                    if(i != types.length - 1){
                        buf.append(TYPE_SEPARATOR);
                    }
                }
            }
        }
        buf.append(CLOSE_BRACKET);
        return buf;
    }
}
