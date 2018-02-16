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
package jp.ossc.nimbus.service.beancontrol;

import java.util.*;
import org.w3c.dom.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.beancontrol.interfaces.*;
import jp.ossc.nimbus.service.beancontrol.resource.*;
import java.lang.reflect.InvocationTargetException;
import jp.ossc.nimbus.service.journal.*;

/**
 * 業務フロー実行クラス。<p>
 *
 * @author H.Nakano
 */
public class BeanFlowInvokerAccessImpl 
    implements BeanFlowInvokerAccess {
    //## クラスメンバー変数宣言 ##
    /**    Flow名                                */    
    protected String mFlowName = null;
    /**    メッセージ配列                        */    
    protected ArrayList mAiliasAry = null;
    /**    リソース配列                        */    
    protected HashMap mResourceHash = null;
    /**    JOBステップ                            */    
    protected ArrayList    mJobSteps = null;
    /**    コールバックオブジェクト                */    
    protected BeanFlowInvokerFactoryCallBack mCallBack = null;
    protected String resourcePath;
    protected Journal journal;
    /** XML TAG 定数定義 */
    private static final String C_NAME = "name" ;
    private static final String C_AILIAS = "alias" ;
    private static final String C_RESOURCE = "resource" ;
    private static final String C_FLOW = "flow" ;
    private static final String C_STEP = "step" ;
    private static final String C_KEY = "key" ;
    private static final String C_SERVICE = "service" ;
    private static final String C_TRANCONTROL = "trancontrol" ;
    private static final String C_TRANCLOSE = "tranclose";
    private static final String C_TRUE = "true";
    private static final String C_FALSE = "false";
    private static final String C_RESOURCE_ADDED = "add resource" ;
    
    //
    /**
     *    コンストラクタ<br>
     */
    public BeanFlowInvokerAccessImpl(){
        mJobSteps = new ArrayList() ;
        mAiliasAry = new ArrayList() ;
        mResourceHash = new HashMap() ;
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccess#fillInstance(org.w3c.dom.Element, jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerFactoryCallBack)
     */
    public void fillInstance(Element item,BeanFlowInvokerFactoryCallBack callBack, String encoding)  {
        mCallBack = callBack ;
        //Flow名取得
        mFlowName = item.getAttribute(C_NAME);
        // Ailiasエレメントを取得
        NodeList ailiasList = item.getElementsByTagName(C_AILIAS);
        for(int rCnt=0;rCnt<ailiasList.getLength();rCnt++){
            Element ailiasElement = (Element)ailiasList.item(rCnt);
            mAiliasAry.add(ailiasElement.getAttribute(C_NAME)) ;
        }
        //リソース情報取得
        NodeList resourceList = item.getElementsByTagName(C_RESOURCE);
        for(int rCnt=0;rCnt<resourceList.getLength();rCnt++){
            Element resourceElement = (Element)resourceList.item(rCnt);
            String key = resourceElement.getAttribute(C_NAME) ;
            if(key == null || "".equals(key)){
                throw new InvalidConfigurationException("key is none") ;    
            }
            String findkey = resourceElement.getAttribute(C_KEY) ;
            if(findkey == null ){
                throw new InvalidConfigurationException("findkey is none") ;    
            }
            String service = resourceElement.getAttribute(C_SERVICE) ;
            if(service == null || "".equals(service)){
                throw new InvalidConfigurationException("service is none") ;    
            }
            ServiceNameEditor editor = new ServiceNameEditor();
            editor.setAsText(service) ;
            String tranControl = resourceElement.getAttribute(C_TRANCONTROL) ;
            boolean isTranControl = false ;
            if(tranControl != null && tranControl.equals(C_TRUE)){
                isTranControl = true ;
            }
            String tranClose = resourceElement.getAttribute(C_TRANCLOSE) ;
            boolean isTranClose = true ;
            if(tranClose != null && tranClose.equals(C_FALSE)){
                isTranClose = false ;
            }
            ResourceInfo tmp  = new ResourceInfo() ;
            tmp.setKey(key) ;
            tmp.setFindKey(findkey) ;
            tmp.setServiceName((ServiceName)editor.getValue()) ;
            tmp.setTranControl(isTranControl) ;
            tmp.setTranClose(isTranClose);
            mResourceHash.put(key,tmp) ;
        }
        //Step情報取得
        NodeList stepList = item.getElementsByTagName(C_STEP);
        for(int rCnt=0;rCnt<stepList.getLength();rCnt++){
            Element stepElement = (Element)stepList.item(rCnt);
            JobStep step = new JobStep(this) ; 
            step.fillElement(stepElement,callBack,mJobSteps) ;
            mJobSteps.add(step) ;
        }
        journal = mCallBack.getJournal(this);
    }
    
    public void setResourcePath(String resource){
        resourcePath = resource;
    }
    public String getResourcePath(){
        return resourcePath;
    }
    
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccess#getFlowName()
     */
    public String getFlowName(){
        return this.mFlowName ;
    }    
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.beancontrol.BeanFlowInvokerAccess#getAiliasFlowNames()
     */
    public List getAiliasFlowNames(){
        return this.mAiliasAry ; 
    }
    
    public String[] getOverwrideFlowNames(){
        return null;
    }
    
    public BeanFlowMonitor createMonitor(){
        return new BeanFlowMonitorImpl(mFlowName);
    }
    
    public BeanFlowCoverage getBeanFlowCoverage(){
        throw new UnsupportedOperationException();
    }
    
    public Journal getJournal(){
        return journal;
    }
    
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker#invokeFlow(java.lang.Object)
     */
    public Object invokeFlow(Object input) throws Exception {
        return invokeFlow(input, null);
    }
    
    public Object invokeFlow(Object input, BeanFlowMonitor monitor) throws Exception {
        if(monitor == null){
            monitor = new BeanFlowMonitorImpl();
        }
        // リソース作成
        Object output = null ;
        String newName = new String(this.mFlowName) ;
        ((BeanFlowMonitorImpl)monitor).setFlowName(newName);
        ((BeanFlowMonitorImpl)monitor).setCurrentFlowName(newName);
        ((BeanFlowMonitorImpl)monitor).setStartTime(System.currentTimeMillis());
        if(mCallBack.isManageExecBeanFlow()){
            this.mCallBack.addExcecFlow(monitor) ;
        }
        Journal jnl = journal;
        try{
            if( jnl != null) {
                jnl.startJournal(C_FLOW) ;
                jnl.addInfo(C_NAME, newName) ;
            }
            ResourceManager rm = this.mCallBack.createResourceManager() ;
            HashMap execBeanMap = new HashMap() ;
            for(Iterator ite = mResourceHash.keySet().iterator();ite.hasNext();){
                String key = (String)ite.next();
                ResourceInfo ele = (ResourceInfo)mResourceHash.get(key) ;
                rm.addResource(key,
                                ele.getFindKey(),
                                ele.getServiceName(),
                                ele.isTranControl(),
                                ele.isTranClose());
                if(jnl != null){
                    jnl.addInfo(C_RESOURCE_ADDED,key) ;
                }
            }
            try{
                if (mJobSteps != null){
                    // 登録JOBステップを実行する。
                    for(int i = 0, max = mJobSteps.size(); i < max; i++){
                        // ジョブステップ取得
                        JobStep jobStepObj = (JobStep)mJobSteps.get(i);
                        Object tmp = jobStepObj.invokeStep(input,
                                                            execBeanMap,
                                                            rm,
                                                            monitor);
                        if(tmp != null){
                            output = tmp ;
                        }
                    }
                }
            }catch(InvocationTargetException e){
                Throwable th = e.getTargetException() ;
                endJob(th,rm,monitor,newName) ;
                if(th instanceof Exception){
                    Exception ee = (Exception)th ;
                    throw ee ;
                }else{
                    throw new BeanControlUncheckedException("Target Error occured",th) ;
                }
            }catch(BeanFlowMonitorStopException e){
                endJob(e, rm, monitor, newName);
                throw e;
            }catch(Throwable th){
                endJob(th, rm, monitor, newName);
                throw new BeanControlUncheckedException(
                    "Target Error occured",
                    th
                );
            }
            endJob(null,rm,monitor,newName) ;
        }finally{
            if(jnl != null){
                jnl.endJournal() ;
            }
        }
        return output ;
    }
    
    /**
     *    JOB実行後の後処理。<br>
     * @param e    実行例外
     * @param rm    ResourceManager
     * @param name    BeanFlowキー
     */
    protected void endJob(Throwable e,ResourceManager rm,BeanFlowMonitor monitor,String name){
        ((BeanFlowMonitorImpl)monitor).end();
        try{
            if(e == null){
                rm.commitAllResources() ;
            }else{
                rm.rollbbackAllResources();
            }
        }finally{
            if(mCallBack.isManageExecBeanFlow()){
                this.mCallBack.removeExecFlow(monitor) ;
            }
            rm.terminateResourceManager() ;
        }
    }
    
    public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, boolean isReply, int maxAsynchWait) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    public Object getAsynchReply(Object context, BeanFlowMonitor monitor, long timeout, boolean isCancel) throws BeanFlowAsynchTimeoutException, Exception{
        throw new UnsupportedOperationException();
    }
    
    public Object invokeAsynchFlow(Object obj, BeanFlowMonitor monitor, BeanFlowAsynchInvokeCallback callback, int maxAsynchWait) throws Exception{
        throw new UnsupportedOperationException();
    }
    
    public void end(){
    }
    
    /**
     * リソース情報管理クラス<p>
     * @version $Name:  $
     * @author H.Nakano
     * @since 1.0
     */
    private class ResourceInfo{
        public String key = null ;
        public String findKey = null ;
        public ServiceName serviceName = null ;
        public boolean isTranControl = false ;
        public boolean isTranClose = true;
        /**
         * リソースIDを出力する
         * @return　リソースキー名
         */
        public String getFindKey() {
            return findKey;
        }

        /**
         * トランザクションコントロール対象か出力する。
         * @return boolean
         */
        public boolean isTranControl() {
            return isTranControl;
        }
        
        public boolean isTranClose(){
            return isTranClose;
        }

        /**
         * 指定ファイルにコピーする。
         */
        public String getKey() {
            return key;
        }

        /**
         * 指定ファイルにコピーする。
         */
        public ServiceName getServiceName() {
            return serviceName;
        }

        /**
         * 指定ファイルにコピーする。
         */
        public void setFindKey(String string) {
            findKey = string;
        }

        /**
         * 指定ファイルにコピーする。
         */
        public void setTranControl(boolean b) {
            isTranControl = b;
        }
        
        public void setTranClose(boolean b){
            isTranClose = b;
        }

        /**
         * 指定ファイルにコピーする。
         */
        public void setKey(String string) {
            key = string;
        }

        /**
         * 指定ファイルにコピーする。
         */
        public void setServiceName(ServiceName name) {
            serviceName = name;
        }

    }

}
