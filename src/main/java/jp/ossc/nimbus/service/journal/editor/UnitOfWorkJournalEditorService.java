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

import jp.ossc.nimbus.ioc.CommandBase;
import jp.ossc.nimbus.ioc.UnitOfWork;
import jp.ossc.nimbus.service.journal.editorfinder.EditorFinder;

/**
 * {@link UnitOfWork}をフォーマットするエディタ。<p>
 * 
 * @author M.Takata
 */
public class UnitOfWorkJournalEditorService
 extends BlockJournalEditorServiceBase
 implements UnitOfWorkJournalEditorServiceMBean, Serializable{
    
    private static final long serialVersionUID = 4874986389010798L;
    
    protected static final String HEADER = "[UnitOfWork]";
    private static final String STATUS_HEADER = "Status : ";
    private static final String UNIT_OF_WORK_SIZE_HEADER = "UnitOfWork size : ";
    private static final String UNIT_OF_WORK_EXECUTE_SIZE_HEADER = "UnitOfWork execute size : ";
    private static final String COMMAND_SIZE_HEADER = "Command size : ";
    private static final String COMMAND_EXECUTE_SIZE_HEADER = "Command execute size : ";
    private static final String EXCEPTION_COUNT_HEADER = "Exception count : ";
    private static final String EXCEPTIONS_HEADER = "Exceptions : ";
    private static final String COMMANDS_HEADER = "Commands : ";
    private static final String STATUS_BEFORE = "Before";
    private static final String STATUS_COMPLETE = "Complete";
    private static final String STATUS_ERROR = "Error";
    private static final String STATUS_UNKNOWN = "Unknown";
    
    private boolean isOutputStatus = true;
    private boolean isOutputUnitOfWorkSize = true;
    private boolean isOutputUnitOfWorkExecuteSize = true;
    private boolean isOutputCommandSize = true;
    private boolean isOutputCommandExecuteSize = true;
    private boolean isOutputExceptionCount = true;
    private boolean isOutputExceptions = true;
    private boolean isOutputCommandBases = true;
    
    public UnitOfWorkJournalEditorService(){
        super();
        setHeader(HEADER);
    }
    
    public void setOutputStatus(boolean isOutput){
        isOutputStatus = isOutput;
    }
    
    public boolean isOutputStatus(){
        return isOutputStatus;
    }
    
    public void setOutputUnitOfWorkSize(boolean isOutput){
        isOutputUnitOfWorkSize = isOutput;
    }
    
    public boolean isOutputUnitOfWorkSize(){
        return isOutputUnitOfWorkSize;
    }
    
    public void setOutputUnitOfWorkExecuteSize(boolean isOutput){
        isOutputUnitOfWorkExecuteSize = isOutput;
    }
    
    public boolean isOutputUnitOfWorkExecuteSize(){
        return isOutputUnitOfWorkExecuteSize;
    }
    
    public void setOutputCommandSize(boolean isOutput){
        isOutputCommandSize = isOutput;
    }
    
    public boolean isOutputCommandSize(){
        return isOutputCommandSize;
    }
    
    public void setOutputCommandExecuteSize(boolean isOutput){
        isOutputCommandExecuteSize = isOutput;
    }
    
    public boolean isOutputCommandExecuteSize(){
        return isOutputCommandExecuteSize;
    }
    
    public void setOutputExceptionCount(boolean isOutput){
        isOutputExceptionCount = isOutput;
    }
    
    public boolean isOutputExceptionCount(){
        return isOutputExceptionCount;
    }
    
    public void setOutputExceptions(boolean isOutput){
        isOutputExceptions = isOutput;
    }
    
    public boolean isOutputExceptions(){
        return isOutputExceptions;
    }
    
    public void setOutputCommandBases(boolean isOutput){
        isOutputCommandBases = isOutput;
    }
    
    public boolean isOutputCommandBases(){
        return isOutputCommandBases;
    }
    
    protected boolean processBlock(
        EditorFinder finder,
        Object key,
        Object value,
        StringBuilder buf
    ){
        final UnitOfWork unitOfWork = (UnitOfWork)value;
        boolean isMake = false;
        if(isOutputStatus()){
            makeStatusFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputUnitOfWorkSize()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeUnitOfWorkSizeFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputUnitOfWorkExecuteSize()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeUnitOfWorkExecuteSizeFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputCommandSize()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCommandSizeFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputCommandExecuteSize()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCommandExecuteSizeFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputExceptionCount()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeExceptionCountFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputExceptions()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeExceptionsFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        if(isOutputCommandBases()){
            if(isMake){
                buf.append(getLineSeparator());
            }
            makeCommandBasesFormat(finder, key, unitOfWork, buf);
            isMake = true;
        }
        return isMake;
    }
    
    protected StringBuilder makeStatusFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        buf.append(STATUS_HEADER);
        switch(unitOfWork.getStatus()){
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
    
    protected StringBuilder makeUnitOfWorkSizeFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        return buf.append(UNIT_OF_WORK_SIZE_HEADER)
            .append(unitOfWork.unitOfWorkSize());
    }
    
    protected StringBuilder makeUnitOfWorkExecuteSizeFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        return buf.append(UNIT_OF_WORK_EXECUTE_SIZE_HEADER)
            .append(unitOfWork.unitOfWorkExecuteSize());
    }
    
    protected StringBuilder makeCommandSizeFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        return buf.append(COMMAND_SIZE_HEADER).append(unitOfWork.commandSize());
    }
    
    protected StringBuilder makeCommandExecuteSizeFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        return buf.append(COMMAND_EXECUTE_SIZE_HEADER)
            .append(unitOfWork.commandExecuteSize());
    }
    
    protected StringBuilder makeExceptionCountFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        return buf.append(EXCEPTION_COUNT_HEADER)
            .append(unitOfWork.getExceptionCount());
    }
    
    protected StringBuilder makeExceptionsFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        buf.append(EXCEPTIONS_HEADER);
        final Throwable[] exceptions = unitOfWork.getExceptions();
        if(exceptions == null || exceptions.length == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0; i < exceptions.length; i++){
            makeObjectFormat(finder, null, exceptions[i], subBuf);
            if(i != exceptions.length - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
    
    protected StringBuilder makeCommandBasesFormat(
        EditorFinder finder,
        Object key,
        UnitOfWork unitOfWork,
        StringBuilder buf
    ){
        buf.append(COMMANDS_HEADER);
        if(unitOfWork.size() == 0){
            buf.append(NULL_STRING);
            return buf;
        }else{
            buf.append(getLineSeparator());
        }
        final StringBuilder subBuf = new StringBuilder();
        for(int i = 0, imax = unitOfWork.size(); i < imax; i++){
            makeObjectFormat(finder, null, unitOfWork.getCommand(i), subBuf);
            if(i != imax - 1){
                subBuf.append(getLineSeparator());
            }
        }
        addIndent(subBuf);
        return buf.append(subBuf);
    }
}