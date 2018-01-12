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
package jp.ossc.nimbus.service.codemaster;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.lang.*;

/**
 * {@link CodeMasterFinder}グルーピングサービス。<p>
 *
 * @author M.Takata
 */
public class CodeMasterFinderGroupService extends ServiceBase
 implements CodeMasterFinder, CodeMasterFinderGroupServiceMBean{

    private static final long serialVersionUID = -5421769179328454354L;

    private ServiceName[] codeMasterFinderServiceNames;
    private CodeMasterFinder[] codeMasterFinderes;
    private String[] notUpdateAllMasterNames;

    // CodeMasterFinderGroupServiceMBeanのJavaDoc
    public void setCodeMasterFinderServiceNames(ServiceName[] names){
        codeMasterFinderServiceNames = names;
    }
    // CodeMasterFinderGroupServiceMBeanのJavaDoc
    public ServiceName[] getCodeMasterFinderServiceNames(){
        return codeMasterFinderServiceNames;
    }

    // CodeMasterFinderGroupServiceMBean のJavaDoc
    public String[] getNotUpdateAllMasterNames() {
        return notUpdateAllMasterNames;
    }

    // CodeMasterFinderGroupServiceMBean のJavaDoc
    public void setNotUpdateAllMasterNames(String[] names) {
        this.notUpdateAllMasterNames = names;
    }

    public void startService() throws Exception{
        if(codeMasterFinderServiceNames == null
            || codeMasterFinderServiceNames.length == 0){
            throw new IllegalArgumentException(
                "CodeMasterFinderServiceNames must be specified."
            );
        }
        codeMasterFinderes = new CodeMasterFinder[
            codeMasterFinderServiceNames.length
        ];
        for(int i = 0; i < codeMasterFinderServiceNames.length; i++){
            codeMasterFinderes[i]
                 = (CodeMasterFinder)ServiceManagerFactory
                    .getServiceObject(codeMasterFinderServiceNames[i]);
        }
    }

    // CodeMasterFinderのJavaDoc
    public Map getCodeMasters() throws ServiceException{
        final Map masters = new HashMap();
        for(int i = 0; i < codeMasterFinderes.length; i++){
            masters.putAll(codeMasterFinderes[i].getCodeMasters());
        }
        return masters;
    }

    // CodeMasterFinderのJavaDoc
    public void updateAllCodeMasters() throws Exception{
        Set codeMasterNameSet = getCodeMasterNameSet();
        if(codeMasterNameSet != null){
            final Collection notUpdateAllMasterNameSet = Arrays.asList(notUpdateAllMasterNames == null ? new String[0] : notUpdateAllMasterNames);
            final Iterator codeMasterNames = codeMasterNameSet.iterator();
            while(codeMasterNames.hasNext()){
                String codeMasterName = (String)codeMasterNames.next();
                if(!notUpdateAllMasterNameSet.contains(codeMasterName)){
                    updateCodeMaster(codeMasterName);
                }
            }
        }
    }

    // CodeMasterFinderのJavaDoc
    public void updateCodeMaster(String key) throws Exception{
        for(int i = 0; i < codeMasterFinderes.length; i++){
            if(codeMasterFinderes[i].getCodeMasterNameSet().contains(key)){
                codeMasterFinderes[i].updateCodeMaster(key);
            }
        }
    }

    // CodeMasterFinderのJavaDoc
    public void updateCodeMaster(String key, Date updateTime) throws Exception{
        for(int i = 0; i < codeMasterFinderes.length; i++){
            if(codeMasterFinderes[i].getCodeMasterNameSet().contains(key)){
                codeMasterFinderes[i].updateCodeMaster(key, updateTime);
            }
        }
    }

    // CodeMasterFinderのJavaDoc
    public void updateCodeMaster(String key, Object input, Date updateTime) throws Exception{
        for(int i = 0; i < codeMasterFinderes.length; i++){
            if(codeMasterFinderes[i].getCodeMasterNameSet().contains(key)){
                codeMasterFinderes[i].updateCodeMaster(key, input, updateTime);
            }
        }
    }

    // CodeMasterFinderのJavaDoc
    public Set getCodeMasterNameSet(){
        final Set result = new HashSet();
        if(codeMasterFinderes == null){
            return result;
        }
        for(int i = 0; i < codeMasterFinderes.length; i++){
            result.addAll(codeMasterFinderes[i].getCodeMasterNameSet());
        }
        return result;
    }
}