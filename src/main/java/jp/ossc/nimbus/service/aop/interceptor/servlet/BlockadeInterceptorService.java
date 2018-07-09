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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.beans.PropertyAccess;
import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.recset.RecordSet;
import jp.ossc.nimbus.recset.RowData;
import jp.ossc.nimbus.service.aop.InterceptorChain;
import jp.ossc.nimbus.service.aop.ServletFilterInvocationContext;
import jp.ossc.nimbus.service.aop.interceptor.servlet.AuthenticateInterceptorService.AuthenticatedInfo;
import jp.ossc.nimbus.service.codemaster.CodeMasterFinder;
import jp.ossc.nimbus.service.context.Context;

/**
 * 閉塞インターセプタ。
 * <p>
 * リクエスト処理をインターセプトして、閉塞コードマスタと特権ユーザコードマスタを参照し、リクエストされたパスが閉塞していないかチェックする。
 * 閉塞している場合は、{@link BlockadeException}をthrowする。<br>
 * 閉塞コードマスタには、パス、閉塞状態、閉塞メッセージを管理する。<br>
 * 閉塞状態には、{@link #BLOCKADE_STATE_OPEN 開放}、{@link #BLOCKADE_STATE_CLOSE 閉塞}、
 * {@link #BLOCKADE_STATE_TEST_OPEN テスト開放}
 * の３つがある。「閉塞」状態の場合は、無条件にBlockadeExceptionをthrowする
 * 。「テスト開放」状態の場合は、特権ユーザコードマスタに存在するユーザからのアクセスのみ許容する。<br>
 * 以下に、閉塞インターセプタのサービス定義例を示す。<br>
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * &lt;server&gt;
 *     &lt;manager name="Sample"&gt;
 *         &lt;service name="BlockadeInterceptor"
 *                  code="jp.ossc.nimbus.service.aop.interceptor.servlet.BlockadeInterceptorService"&gt;
 *             &lt;attribute name="CodeMasterFinderServiceName"&gt;#CodeMasterFinder&lt;/attribute&gt;
 *             &lt;attribute name="BlockadeCodeMasterKey"&gt;BLOCKADE_MST&lt;/attribute&gt;
 *             &lt;attribute name="SpecialUserCodeMasterKey"&gt;SPECIAL_USER_MST&lt;/attribute&gt;
 *             &lt;attribute name="SpecialUserMapping"&gt;Header(Common).id|Header(Login).id=id&lt;/attribute&gt;
 *             &lt;depends&gt;CodeMasterFinder&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;!-- 以下はコードマスタサービス定義 --&gt;
 *         &lt;service name="CodeMasterFinder"
 *                  code="jp.ossc.nimbus.service.codemaster.CodeMasterService"&gt;
 *             &lt;attribute name="MasterNames"&gt;
 *                 BLOCKADE_MST,
 *                 SPECIAL_USER_MST
 *             &lt;/attribute&gt;
 *             &lt;attribute name="BeanFlowInvokerFactoryServiceName"&gt;#BeanFlowInvokerFactory&lt;/attribute&gt;
 *             &lt;attribute name="MessageReceiverServiceName"&gt;#MessageReceiver&lt;/attribute&gt;
 *             &lt;attribute name="Subjects"&gt;CodeMaster&lt;/attribute&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="BeanFlowInvokerFactory"
 *                          code="jp.ossc.nimbus.service.beancontrol.DefaultBeanFlowInvokerFactoryService"&gt;
 *                     &lt;attribute name="DirPaths"&gt;flow&lt;/attribute&gt;
 *                     &lt;attribute name="BeanFlowInvokerAccessClass"&gt;jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccessImpl2&lt;/attribute&gt;
 *                 &lt;/service&gt;
 *             &lt;/depends&gt;
 *             &lt;depends&gt;MessageReceiver&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="MessageReceiver"
 *                  code="jp.ossc.nimbus.service.publish.MessageReceiverService"&gt;
 *             &lt;attribute name="JndiRepositoryServiceName"&gt;JndiRepository&lt;/attribute&gt;
 *             &lt;depends&gt;
 *                 &lt;service name="JndiRepository"
 *                          code="jp.ossc.nimbus.service.repository.JNDIRepositoryService"/&gt;
 *             &lt;/depends&gt;
 *         &lt;/service&gt;
 *     &lt;/manager&gt;
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class BlockadeInterceptorService extends ServletFilterInterceptorService implements BlockadeInterceptorServiceMBean {
    
    private static final long serialVersionUID = -1694588353473833851L;
    
    private String requestObjectAttributeName = StreamExchangeInterceptorServiceMBean.DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME;
    
    private Map specialUserMapping;
    private Map sessionSpecialUserMapping;
    private Map blockadeMapping;
    
    private ServiceName codeMasterFinderServiceName;
    private CodeMasterFinder codeMasterFinder;
    
    private ServiceName threadContextServiceName;
    private Context threadContext;
    
    private String blockadeCodeMasterKey;
    private String specialUserCodeMasterKey;
    
    private String pathPropertyName = DEFAULT_PROPERTY_NAME_PATH;
    private String statePropertyName = DEFAULT_PROPERTY_NAME_STATE;
    private String messagePropertyName = DEFAULT_PROPERTY_NAME_MESSAGE;
    
    private String sessionAuthenticatedInfoAttributeName;
    
    private int stateOpen = BLOCKADE_STATE_OPEN;
    private int stateAllClose = BLOCKADE_STATE_ALL_CLOSE;
    private int statePartClose = BLOCKADE_STATE_PART_CLOSE;
    private int stateTestAllClose = BLOCKADE_STATE_TEST_ALL_CLOSE;
    private int stateTestPartClose = BLOCKADE_STATE_TEST_PART_CLOSE;
    
    private PropertyAccess propertyAccess;
    private Map pathPatternMap;
    
    public void setRequestObjectAttributeName(String name) {
        requestObjectAttributeName = name;
    }
    
    public String getRequestObjectAttributeName() {
        return requestObjectAttributeName;
    }
    
    public void setSpecialUserMapping(Map mapping) {
        specialUserMapping = mapping;
    }
    
    public Map getSpecialUserMapping() {
        return specialUserMapping;
    }
    
    public void setSessionSpecialUserMapping(Map mapping) {
        sessionSpecialUserMapping = mapping;
    }
    
    public Map getSessionSpecialUserMapping() {
        return sessionSpecialUserMapping;
    }
    
    public void setBlockadeMapping(Map mapping) {
        blockadeMapping = mapping;
    }
    
    public Map getBlockadeMapping() {
        return blockadeMapping;
    }
    
    public void setCodeMasterFinderServiceName(ServiceName name) {
        codeMasterFinderServiceName = name;
    }
    
    public ServiceName getCodeMasterFinderServiceName() {
        return codeMasterFinderServiceName;
    }
    
    public void setThreadContextServiceName(ServiceName name) {
        threadContextServiceName = name;
    }
    
    public ServiceName getThreadContextServiceName() {
        return threadContextServiceName;
    }
    
    public void setBlockadeCodeMasterKey(String key) {
        blockadeCodeMasterKey = key;
    }
    
    public String getBlockadeCodeMasterKey() {
        return blockadeCodeMasterKey;
    }
    
    public void setSpecialUserCodeMasterKey(String key) {
        specialUserCodeMasterKey = key;
    }
    
    public String getSpecialUserCodeMasterKey() {
        return specialUserCodeMasterKey;
    }
    
    public void setPathPropertyName(String name) {
        pathPropertyName = name;
    }
    
    public String getPathPropertyName() {
        return pathPropertyName;
    }
    
    public void setStatePropertyName(String name) {
        statePropertyName = name;
    }
    
    public String getStatePropertyName() {
        return statePropertyName;
    }
    
    public void setMessagePropertyName(String name) {
        messagePropertyName = name;
    }
    
    public String getMessagePropertyName() {
        return messagePropertyName;
    }
    
    public String getSessionAuthenticatedInfoAttributeName() {
        return sessionAuthenticatedInfoAttributeName;
    }

    public void setSessionAuthenticatedInfoAttributeName(String attributeName) {
        sessionAuthenticatedInfoAttributeName = attributeName;
    }

    public void setCodeMasterFinder(CodeMasterFinder finder) {
        codeMasterFinder = finder;
    }
    
    public void setThreadContext(Context context) {
        threadContext = context;
    }
    
    public int getStateOpen() {
        return stateOpen;
    }
    
    public void setStateOpen(int state) {
        stateOpen = state;
    }
    
    public int getStateAllClose() {
        return stateAllClose;
    }
    
    public void setStateAllClose(int state) {
        stateAllClose = state;
    }
    
    public int getStatePartClose() {
        return statePartClose;
    }
    
    public void setStatePartClose(int state) {
        statePartClose = state;
    }
    
    public int getStateTestAllClose() {
        return stateTestAllClose;
    }
    
    public void setStateTestAllClose(int state) {
        stateTestAllClose = state;
    }
    
    public int getStateTestPartClose() {
        return stateTestPartClose;
    }
    
    public void setStateTestPartClose(int state) {
        stateTestPartClose = state;
    }
    
    /**
     * サービスの生成処理を行う。
     * <p>
     *
     * @exception Exception サービスの生成に失敗した場合
     */
    public void createService() throws Exception {
        propertyAccess = new PropertyAccess();
        propertyAccess.setIgnoreNullProperty(true);
    }
    
    /**
     * サービスの開始処理を行う。
     * <p>
     *
     * @exception Exception サービスの開始に失敗した場合
     */
    public void startService() throws Exception {
        if (codeMasterFinderServiceName == null && codeMasterFinder == null && threadContextServiceName == null && threadContext == null) {
            throw new IllegalArgumentException("CodeMasterFinder or ThreadContext must be specified.");
        }
        if (blockadeCodeMasterKey == null) {
            throw new IllegalArgumentException("BlockadeCodeMasterKey must be specified.");
        }
        if (specialUserCodeMasterKey != null && (specialUserMapping == null || specialUserMapping.size() == 0)) {
            throw new IllegalArgumentException("SpecialUserMapping must be specified.");
        }
        if (sessionAuthenticatedInfoAttributeName != null && (sessionSpecialUserMapping == null || sessionSpecialUserMapping.size() == 0)) {
            throw new IllegalArgumentException("SessionSpecialUserMapping must be specified.");
        }
        if (codeMasterFinderServiceName != null) {
            codeMasterFinder = (CodeMasterFinder) ServiceManagerFactory.getServiceObject(codeMasterFinderServiceName);
        }
        if (threadContextServiceName != null) {
            threadContext = (Context) ServiceManagerFactory.getServiceObject(threadContextServiceName);
        }
        pathPatternMap = null;
    }
    
    /**
     * サービスの破棄処理を行う。
     * <p>
     *
     * @exception Exception サービスの破棄に失敗した場合
     */
    public void destroyService() throws Exception {
        propertyAccess = null;
    }
    
    /**
     * コードマスタの閉塞マスタ及び特権ユーザマスタをチェックして、閉塞状態の場合は例外をthrowする。
     * <p>
     * サービスが開始されていない場合は、何もせずに次のインターセプタを呼び出す。<br>
     *
     * @param context 呼び出しのコンテキスト情報
     * @param chain 次のインターセプタを呼び出すためのチェーン
     * @return 呼び出し結果の戻り値
     * @exception Throwable 呼び出し先で例外が発生した場合、またはこのインターセプタで任意の例外が発生した場合。但し、
     *                本来呼び出される処理がthrowしないRuntimeException以外の例外をthrowしても
     *                、呼び出し元には伝播されない。
     */
    public Object invokeFilter(ServletFilterInvocationContext context, InterceptorChain chain) throws Throwable {
        if (getState() != STARTED) {
            return chain.invokeNext(context);
        }
        final HttpServletRequest request = (HttpServletRequest) context.getServletRequest();
        String reqPath = request.getServletPath();
        if (request.getPathInfo() != null) {
            reqPath = reqPath + request.getPathInfo();
        }
        Map codeMasters = null;
        if (codeMasterFinder != null) {
            codeMasters = codeMasterFinder.getCodeMasters();
        } else {
            codeMasters = (Map) threadContext.get(ThreadContextKey.CODEMASTER);
        }
        if (codeMasters == null) {
            throw new BlockadeProcessException("CodeMaster is null.");
        }
        Object blockadeCodeMaster = codeMasters.get(blockadeCodeMasterKey);
        if (blockadeCodeMaster == null) {
            throw new BlockadeProcessException("BlockadeCodeMaster is null. key=" + blockadeCodeMasterKey);
        }
        Object specialUserCodeMaster = null;
        if (specialUserCodeMasterKey != null) {
            specialUserCodeMaster = codeMasters.get(specialUserCodeMasterKey);
        }
        boolean isSpecialUser = false;
        String userKey = null;
        if (specialUserCodeMaster != null) {
            boolean isCheckSessionObject = false;
            Object checkTargetObject = null;
            if(sessionAuthenticatedInfoAttributeName != null) {
                HttpSession session = request.getSession(false);
                if(session != null) {
                    checkTargetObject = (AuthenticatedInfo)session.getAttribute(sessionAuthenticatedInfoAttributeName);
                }
            }
            if(checkTargetObject != null) {
                isCheckSessionObject = true;
            } else {
                checkTargetObject = request.getAttribute(requestObjectAttributeName);
                if (checkTargetObject == null) {
                    throw new BlockadeProcessException("CheckTargetObject is not found.");
                }
            }
            if (specialUserCodeMaster instanceof RecordList) {
                RecordList list = (RecordList) specialUserCodeMaster;
                Record primaryKey = list.createRecord();
                applySpecialUserMapping(checkTargetObject, primaryKey, isCheckSessionObject);
                userKey = primaryKey.toString();
                isSpecialUser = list.searchByPrimaryKey(primaryKey) != null;
            } else if (specialUserCodeMaster instanceof RecordSet) {
                RecordSet recset = (RecordSet) specialUserCodeMaster;
                RowData primaryKey = recset.createNewRecord();
                applySpecialUserMapping(checkTargetObject, primaryKey, isCheckSessionObject);
                userKey = primaryKey.getKey();
                isSpecialUser = recset.get(primaryKey) != null;
            } else {
                throw new BlockadeProcessException("Unsupported type of SpecialUserCodeMaster. type=" + specialUserCodeMaster.getClass());
            }
        }
        if (pathPatternMap == null) {
            initPathPatternMap(blockadeCodeMaster);
        }
        Map blockadeFilterMap = null;
        if(blockadeMapping != null){
            Object requestObject = request.getAttribute(requestObjectAttributeName);
            if (requestObject == null) {
                throw new BlockadeProcessException("RequestObject is null.");
            }
            blockadeFilterMap = new HashMap();
            Iterator entries = blockadeMapping.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                try {
                    blockadeFilterMap.put(
                        entry.getValue(),
                        propertyAccess.get(requestObject, (String) entry.getKey())
                    );
                } catch (IllegalArgumentException e) {
                    throw new BlockadeProcessException("BlockadeCodeMaster value '" + entry.getKey() + "' cannot acquire from a request.", e);
                } catch (NoSuchPropertyException e) {
                    throw new BlockadeProcessException("BlockadeCodeMaster value '" + entry.getKey() + "' cannot acquire from a request.", e);
                } catch (InvocationTargetException e) {
                    throw new BlockadeProcessException("BlockadeCodeMaster value '" + entry.getKey() + "' cannot acquire from a request.",
                            e.getTargetException());
                }
            }
        }
        if (blockadeCodeMaster instanceof List) {
            List list = (List) blockadeCodeMaster;
            for (int i = 0, imax = list.size(); i < imax; i++) {
                Object blockade = list.get(i);
                if(blockadeFilterMap != null){
                    if(!isMatchBlockadeMapping(blockadeFilterMap, blockade)){
                        continue;
                    }
                }
                checkBlockade(reqPath, blockade, isSpecialUser, userKey);
            }
        } else if (blockadeCodeMaster instanceof RecordSet) {
            RecordSet recset = (RecordSet) blockadeCodeMaster;
            for (int i = 0, imax = recset.size(); i < imax; i++) {
                Object blockade = recset.get(i);
                if(blockadeFilterMap != null){
                    if(!isMatchBlockadeMapping(blockadeFilterMap, blockade)){
                        continue;
                    }
                }
                checkBlockade(reqPath, blockade, isSpecialUser, userKey);
            }
        } else {
            throw new BlockadeProcessException("Unsupported type of BlockadeCodeMaster. type=" + blockadeCodeMaster.getClass());
        }
        return chain.invokeNext(context);
    }
    
    private boolean isMatchBlockadeMapping(Map blockadeFilterMap, Object blockade) throws BlockadeProcessException {
        boolean isMatch = true;
        Iterator entries = blockadeFilterMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Object requestValue = entry.getValue();
            Object blockadeValue = null;
            try {
                blockadeValue = propertyAccess.get(blockade, (String) entry.getKey());
            } catch (IllegalArgumentException e) {
                throw new BlockadeProcessException("BlockadeCodeMaster value '" + entry.getKey() + "' cannot acquire from a codemaster.", e);
            } catch (NoSuchPropertyException e) {
                throw new BlockadeProcessException("BlockadeCodeMaster value '" + entry.getKey() + "' cannot acquire from a codemaster.", e);
            } catch (InvocationTargetException e) {
                throw new BlockadeProcessException("BlockadeCodeMaster value '" + entry.getKey() + "' cannot acquire from a codemaster.",
                        e.getTargetException());
            }
            if((requestValue == null && blockadeValue != null)
                || (requestValue != null && !requestValue.equals(blockadeValue))
            ){
                isMatch = false;
                break;
            }
        }
        return isMatch;
    }
    
    private Object applySpecialUserMapping(Object requestObject, Object primaryKey, boolean isCheckSessionObject) throws BlockadeProcessException {
        Map mapping = isCheckSessionObject ? sessionSpecialUserMapping : specialUserMapping;
        Iterator entries = mapping.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Object key = null;
            try {
                key = propertyAccess.get(requestObject, (String) entry.getKey());
            } catch (IllegalArgumentException e) {
                throw new BlockadeProcessException("SpecialUserCodeMaster value '" + entry.getKey() + "' cannot acquire from a object.", e);
            } catch (NoSuchPropertyException e) {
                throw new BlockadeProcessException("SpecialUserCodeMaster value '" + entry.getKey() + "' cannot acquire from a object.", e);
            } catch (InvocationTargetException e) {
                throw new BlockadeProcessException("SpecialUserCodeMaster value '" + entry.getKey() + "' cannot acquire from a object.",
                        e.getTargetException());
            }
            try {
                propertyAccess.set(primaryKey, (String) entry.getValue(), key);
            } catch (IllegalArgumentException e) {
                throw new BlockadeProcessException("SpecialUserCodeMaster value '" + entry.getKey() + "' cannot set to a record.", e);
            } catch (NoSuchPropertyException e) {
                throw new BlockadeProcessException("SpecialUserCodeMaster value '" + entry.getKey() + "' cannot set to a record.", e);
            } catch (InvocationTargetException e) {
                throw new BlockadeProcessException("SpecialUserCodeMaster value '" + entry.getKey() + "' cannot set to a record.",
                        e.getTargetException());
            }
        }
        return primaryKey;
    }
    
    private synchronized void initPathPatternMap(Object blockadeCodeMaster) throws BlockadeProcessException {
        if (pathPatternMap != null) {
            return;
        }
        pathPatternMap = new HashMap();
        if (blockadeCodeMaster instanceof List) {
            List list = (List) blockadeCodeMaster;
            for (int i = 0, imax = list.size(); i < imax; i++) {
                initPathPatternMap(pathPatternMap, list.get(i));
            }
        } else if (blockadeCodeMaster instanceof RecordSet) {
            RecordSet recset = (RecordSet) blockadeCodeMaster;
            for (int i = 0, imax = recset.size(); i < imax; i++) {
                initPathPatternMap(pathPatternMap, recset.get(i));
            }
        } else {
            throw new BlockadeProcessException("Unsupported type of BlockadeCodeMaster. type=" + blockadeCodeMaster.getClass());
        }
    }
    
    private synchronized void initPathPatternMap(Map map, Object blockade) throws BlockadeProcessException {
        String path = null;
        try {
            path = (String) propertyAccess.get(blockade, pathPropertyName);
        } catch (ClassCastException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.", e);
        } catch (IllegalArgumentException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.", e);
        } catch (NoSuchPropertyException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.", e);
        } catch (InvocationTargetException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.",
                    e.getTargetException());
        }
        if (path == null) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' is null from a record.");
        }
        initPathPatternMap(map, path);
    }
    
    private synchronized void initPathPatternMap(Map map, String path) throws BlockadeProcessException {
        if (map.get(path) != null) {
            return;
        }
        try {
            map.put(path, Pattern.compile(path));
        } catch (PatternSyntaxException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot compile.", e);
        }
    }
    
    private void checkBlockade(String reqPath, Object blockade, boolean isSpecialUser, String userKey) throws BlockadeException,
            BlockadeProcessException {
        int state = 0;
        try {
            Object stateObject = propertyAccess.get(blockade, statePropertyName);
            if (stateObject == null) {
                throw new BlockadeProcessException("BlockadeCodeMaster value '" + statePropertyName + "' is null from a record.");
            }
            if (stateObject instanceof Number) {
                state = ((Number) stateObject).intValue();
            } else if (stateObject instanceof String) {
                state = Integer.parseInt((String) stateObject);
            } else if (stateObject instanceof Boolean) {
                state = ((Boolean) stateObject).booleanValue() ? 1 : 0;
            }
        } catch (NumberFormatException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + statePropertyName + "' cannot acquire from a record.", e);
        } catch (IllegalArgumentException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + statePropertyName + "' cannot acquire from a record.", e);
        } catch (NoSuchPropertyException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + statePropertyName + "' cannot acquire from a record.", e);
        } catch (InvocationTargetException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + statePropertyName + "' cannot acquire from a record.",
                    e.getTargetException());
        }
        if (state == stateOpen) {
            return;
        }
        String path = null;
        try {
            path = (String) propertyAccess.get(blockade, pathPropertyName);
        } catch (ClassCastException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.", e);
        } catch (IllegalArgumentException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.", e);
        } catch (NoSuchPropertyException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.", e);
        } catch (InvocationTargetException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' cannot acquire from a record.",
                    e.getTargetException());
        }
        if (path == null) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + pathPropertyName + "' is null from a record.");
        }
        boolean isMatch = false;
        if (reqPath.equals(path)) {
            isMatch = true;
        } else {
            Pattern pattern = (Pattern) pathPatternMap.get(path);
            if (pattern == null) {
                initPathPatternMap(pathPatternMap, path);
                pattern = (Pattern) pathPatternMap.get(path);
            }
            isMatch = pattern.matcher(reqPath).matches();
        }
        if (!isMatch) {
            return;
        }
        String message = null;
        try {
            message = (String) propertyAccess.get(blockade, messagePropertyName);
        } catch (ClassCastException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + messagePropertyName + "' cannot acquire from a record.", e);
        } catch (IllegalArgumentException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + messagePropertyName + "' cannot acquire from a record.", e);
        } catch (NoSuchPropertyException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + messagePropertyName + "' cannot acquire from a record.", e);
        } catch (InvocationTargetException e) {
            throw new BlockadeProcessException("BlockadeCodeMaster value '" + messagePropertyName + "' cannot acquire from a record.",
                    e.getTargetException());
        }
        if (state == stateTestAllClose || state == stateTestPartClose) {
            if (isSpecialUser) {
                return;
            }
            if (state == stateTestAllClose) {
                throw message == null ? new BlockadeAllCloseException("Blockade because of not special user. user=" + userKey)
                        : new BlockadeAllCloseException(message);
            } else {
                throw message == null ? new BlockadePartCloseException("Blockade because of not special user. user=" + userKey)
                        : new BlockadePartCloseException(message);
            }
        } else if (state == stateAllClose) {
            throw message == null ? new BlockadeAllCloseException("Blockade.") : new BlockadeAllCloseException(message);
        } else if (state == statePartClose) {
            throw message == null ? new BlockadePartCloseException("Blockade.") : new BlockadePartCloseException(message);
        }
        throw message == null ? new BlockadeAllCloseException("Blockade.") : new BlockadeAllCloseException(message);
    }
}