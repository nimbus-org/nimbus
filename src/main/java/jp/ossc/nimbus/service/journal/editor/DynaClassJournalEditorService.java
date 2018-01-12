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

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link DynaClass}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class DynaClassJournalEditorService extends BlockJournalEditorServiceBase
 implements DynaClassJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 1824967470335975074L;
    
    private static final String NAME_HEADER = "Name : ";
    private static final String DYNA_PROPERTIES_HEADER = "DynaProperties : ";
    
    protected static final String HEADER = "[DynaClass]";
    
    private boolean isOutputName = true;
    private boolean isOutputDynaProperties = true;
    
    public DynaClassJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputName(boolean isOutput){
        isOutputName = isOutput;
    }
    
    public boolean isOutputName(){
        return isOutputName;
    }
    
    public void setOutputDynaProperties(boolean isOutput){
        isOutputDynaProperties = isOutput;
    }
    
    public boolean isOutputDynaProperties(){
        return isOutputDynaProperties;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuffer buf
    ){
        final DynaClass dynaClass = (DynaClass)value;
        boolean isMake = false;
        if(isOutputName()){
            makeNameFormat(finder, key, dynaClass, buf);
            isMake = true;
        }
        
        if(isOutputDynaProperties()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeDynaPropertiesFormat(finder, key, dynaClass, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuffer makeNameFormat(
        EditorFinder finder,
        Object key,
        DynaClass dynaClass,
        StringBuffer buf
    ){
        return buf.append(NAME_HEADER).append(dynaClass.getName());
    }
    
    protected StringBuffer makeDynaPropertiesFormat(
        EditorFinder finder,
        Object key,
        DynaClass dynaClass,
        StringBuffer buf
    ){
        buf.append(DYNA_PROPERTIES_HEADER);
        final DynaProperty[] props = dynaClass.getDynaProperties();
        if(props == null || props.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuffer subBuf = new StringBuffer();
        for(int i = 0, max = props.length; i < max; i++){
            makeObjectFormat(finder, null, props[i], subBuf);
            if(i != max - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}