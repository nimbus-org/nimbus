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
package jp.ossc.nimbus.service.converter;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.converter.*;
import jp.ossc.nimbus.service.context.*;
import jp.ossc.nimbus.service.codemaster.*;

/**
 * CodeMasterサービスを使ったBean変換コンバータ。<p>
 * 
 * @author M.Takata
 */
public class CodeMasterConverterService extends ServiceBase
 implements Converter, CodeMasterConverterServiceMBean{
    
    private static final long serialVersionUID = -1293434754491458419L;
    
    protected ServiceName threadContextServiceName;
    protected String threadContextKey = jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey.CODEMASTER;
    protected Context threadContext;
    
    protected ServiceName codeMasterFinderServiceName;
    protected CodeMasterFinder codeMasterFinder;
    
    protected String masterName;
    
    protected ServiceName codeMasterConverterServiceName;
    protected CodeMasterConverter codeMasterConverter;
    
    public void setThreadContextServiceName(ServiceName name){
        threadContextServiceName = name;
    }
    public ServiceName getThreadContextServiceName(){
        return threadContextServiceName;
    }
    
    public void setThreadContextKey(String key){
        threadContextKey = key;
    }
    public String getThreadContextKey(){
        return threadContextKey;
    }
    
    public void setCodeMasterFinderServiceName(ServiceName name){
        codeMasterFinderServiceName = name;
    }
    public ServiceName getCodeMasterFinderServiceName(){
        return codeMasterFinderServiceName;
    }
    
    public void setMasterName(String name){
        masterName = name;
    }
    public String getMasterName(){
        return masterName;
    }
    
    public void setCodeMasterConverterServiceName(ServiceName name){
        codeMasterConverterServiceName = name;
    }
    public ServiceName getCodeMasterConverterServiceName(){
        return codeMasterConverterServiceName;
    }
    
    public void setThreadContext(Context context){
        threadContext = context;
    }
    public Context getThreadContext(){
        return threadContext;
    }
    
    public void setCodeMasterFinder(CodeMasterFinder finder){
        codeMasterFinder = finder;
    }
    public CodeMasterFinder getCodeMasterFinder(){
        return codeMasterFinder;
    }
    
    public void setCodeMasterConverter(CodeMasterConverter converter){
        codeMasterConverter = converter;
    }
    public CodeMasterConverter getCodeMasterConverter(){
        return codeMasterConverter;
    }
    
    public void startService() throws Exception{
        if(threadContextServiceName != null){
            threadContext = (Context)ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
        if(codeMasterFinderServiceName != null){
            codeMasterFinder = (CodeMasterFinder)ServiceManagerFactory.getServiceObject(codeMasterFinderServiceName);
        }
        if(threadContext == null && codeMasterFinder == null){
            throw new IllegalArgumentException("It is necessary to set either of ThreadContext or CodeMasterFinder.");
        }
        if(masterName == null){
            throw new IllegalArgumentException("MasterName must be specified.");
        }
        if(codeMasterConverterServiceName != null){
            codeMasterConverter = (CodeMasterConverter)ServiceManagerFactory.getServiceObject(codeMasterConverterServiceName);
        }
        if(codeMasterConverter == null){
            throw new IllegalArgumentException("CodeMasterConverter must be specified.");
        }
    }
    
    
    /**
     * 指定されたオブジェクトを変換する。<p>
     *
     * @param obj 変換対象のオブジェクト
     * @return 変換後のオブジェクト
     * @exception ConvertException 変換に失敗した場合
     */
    public Object convert(Object obj) throws ConvertException{
        return codeMasterConverter.convert(getCodeMaster(), obj);
    }
    
    protected Object getCodeMaster() throws ConvertException{
        Object master = getCodeMasters().get(masterName);
        if(master == null){
            throw new ConvertException("CodeMaster not found.");
        }
        return master;
    }
    
    protected Map getCodeMasters() throws ConvertException{
        Map masters = null;
        if(threadContext != null){
            masters = (Map)threadContext.get(threadContextKey);
        }else{
            masters = codeMasterFinder.getCodeMasters();
        }
        if(masters == null){
            throw new ConvertException("CodeMasters not found.");
        }
        return masters;
    }
}