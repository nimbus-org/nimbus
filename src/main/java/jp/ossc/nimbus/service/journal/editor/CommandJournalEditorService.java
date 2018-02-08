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

import jp.ossc.nimbus.ioc.Command;
import jp.ossc.nimbus.ioc.CommandBase;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link Command}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class CommandJournalEditorService
 extends BlockJournalEditorServiceBase
 implements CommandJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 5955054609085701115L;
    
    protected static final String HEADER = "[Command]";
    private static final String FLOW_KEY_HEADER = "Flow key : ";
    private static final String INPUT_HEADER = "Input : ";
    private static final String OUTPUT_HEADER = "Output : ";
    private static final String STATUS_HEADER = "Status : ";
    private static final String EXCEPTION_HEADER = "Exception : ";
    
    private static final String STATUS_BEFORE = "Before";
    private static final String STATUS_COMPLETE = "Complete";
    private static final String STATUS_ERROR = "Error";
    private static final String STATUS_UNKNOWN = "Unknown";
    
    private boolean isOutputFlowKey = true;
    private boolean isOutputInput = true;
    private boolean isOutputOutput = true;
    private boolean isOutputStatus = true;
    private boolean isOutputException = true;
    
    public CommandJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputFlowKey(boolean isOutput){
        isOutputFlowKey = isOutput;
    }
    
    public boolean isOutputFlowKey(){
        return isOutputFlowKey;
    }
    
    public void setOutputInput(boolean isOutput){
        isOutputInput = isOutput;
    }
    
    public boolean isOutputInput(){
        return isOutputInput;
    }
    
    public void setOutputOutput(boolean isOutput){
        isOutputOutput = isOutput;
    }
    
    public boolean isOutputOutput(){
        return isOutputOutput;
    }
    
    public void setOutputStatus(boolean isOutput){
        isOutputStatus = isOutput;
    }
    
    public boolean isOutputStatus(){
        return isOutputStatus;
    }
    
    public void setOutputException(boolean isOutput){
        isOutputException = isOutput;
    }
    
    public boolean isOutputException(){
        return isOutputException;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final Command command = (Command)value;
        boolean isMake = false;
        if(isOutputFlowKey()){
            makeFlowKeyFormat(finder, key, command, buf);
            isMake = true;
        }
        if(isOutputInput()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeInputFormat(finder, key, command, buf);
            isMake = true;
        }
        if(isOutputOutput()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeOutputFormat(finder, key, command, buf);
            isMake = true;
        }
        if(isOutputStatus()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeStatusFormat(finder, key, command, buf);
            isMake = true;
        }
        if(isOutputException()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeExceptionFormat(finder, key, command, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeFlowKeyFormat(
        EditorFinder finder,
        Object key,
        Command command,
        StringBuilder buf
    ){
        return buf.append(FLOW_KEY_HEADER)
            .append(command.getFlowKey());
    }
    
    protected StringBuilder makeInputFormat(
        EditorFinder finder,
        Object key,
        Command command,
        StringBuilder buf
    ){
        buf.append(INPUT_HEADER);
        makeObjectFormat(finder, null, command.getInputObject(), buf);
        return buf;
    }
    
    protected StringBuilder makeOutputFormat(
        EditorFinder finder,
        Object key,
        Command command,
        StringBuilder buf
    ){
        buf.append(OUTPUT_HEADER);
        makeObjectFormat(finder, null, command.getOutputObject(), buf);
        return buf;
    }
    
    protected StringBuilder makeStatusFormat(
        EditorFinder finder,
        Object key,
        Command command,
        StringBuilder buf
    ){
        buf.append(STATUS_HEADER);
        switch(command.getStatus()){
        case CommandBase.C_STATUS_BEFORE:
            buf.append(STATUS_BEFORE);
            break;
        case CommandBase.C_STATUS_COMPLETE:
            buf.append(STATUS_COMPLETE);
            break;
        case CommandBase.C_STATUS_ERROR:
            buf.append(STATUS_ERROR);
            break;
        default:
            buf.append(STATUS_UNKNOWN);
            break;
        }
        return buf;
    }
    
    protected StringBuilder makeExceptionFormat(
        EditorFinder finder,
        Object key,
        Command command,
        StringBuilder buf
    ){
        buf.append(EXCEPTION_HEADER);
        makeObjectFormat(finder, null, command.getException(), buf);
        return buf;
    }
}