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
package jp.ossc.nimbus.servlet;

import java.util.*;
import java.util.regex.*;

import javax.servlet.http.HttpServletRequest;

import jp.ossc.nimbus.core.ServiceBase;

/**
 * BeanFlowのフロー名をリクエストパスとマッピングして選択する。<p>
 *
 * @author M.Takata
 */
public class MappingBeanFlowSelectorService extends DefaultBeanFlowSelectorService
 implements MappingBeanFlowSelectorServiceMBean{
    
    private static final long serialVersionUID = -6425793191507205445L;
    
    protected Properties mapping;
    protected boolean isRegexEnabled;
    protected int regexMatchFlag;
    protected Map regexMapping;
    
    // MappingBeanFlowSelectorServiceMBeanのJavaDoc
    public void setMapping(Properties mapping){
        this.mapping = mapping;
    }
    // MappingBeanFlowSelectorServiceMBeanのJavaDoc
    public Properties getMapping(){
        return mapping;
    }
    
    // MappingBeanFlowSelectorServiceMBeanのJavaDoc
    public void setRegexEnabled(boolean isEnable){
        isRegexEnabled = isEnable;
    }
    // MappingBeanFlowSelectorServiceMBeanのJavaDoc
    public boolean isRegexEnabled(){
        return isRegexEnabled;
    }
    
    // MappingBeanFlowSelectorServiceMBeanのJavaDoc
    public void setRegexMatchFlag(int flag){
        regexMatchFlag = flag;
    }
    // MappingBeanFlowSelectorServiceMBeanのJavaDoc
    public int getRegexMatchFlag(){
        return regexMatchFlag;
    }
    
    public void startService() throws Exception{
        super.startService();
        if(mapping != null && isRegexEnabled){
            final Iterator paths = mapping.keySet().iterator();
            while(paths.hasNext()){
                final String path = (String)paths.next();
                Pattern pattern = Pattern.compile(path, regexMatchFlag);
                if(regexMapping == null){
                    regexMapping = new LinkedHashMap();
                }
                regexMapping.put(pattern, mapping.get(path));
            }
        }
    }
    
    /**
     * BeanFlowのフロー名を選択する。<p>
     *
     * @param req HTTPリクエスト
     * @return BeanFlowのフロー名
     */
    public String selectBeanFlow(HttpServletRequest req){
        
        String path = req.getServletPath();
        if(req.getPathInfo() != null){
            path = path + req.getPathInfo();
        }
        if(path == null){
            return null;
        }
        String flowName = mapping == null ? null : mapping.getProperty(path);
        if(flowName == null && isRegexEnabled
             && regexMapping != null && regexMapping.size() != 0){
            final Iterator patterns = regexMapping.keySet().iterator();
            while(patterns.hasNext()){
                final Pattern pattern = (Pattern)patterns.next();
                if(pattern.matcher(path).matches()){
                    flowName = (String)regexMapping.get(pattern);
                    break;
                }
            }
        }
        return flowName == null ? super.selectBeanFlow(req) : flowName;
    }
}
