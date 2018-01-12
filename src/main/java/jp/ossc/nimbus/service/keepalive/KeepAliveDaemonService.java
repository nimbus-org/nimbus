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
package jp.ossc.nimbus.service.keepalive;

import java.util.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.util.*;

/**
 * KeepAliveChecker監視サービス。<p>
 * 複数サーバを監視して、生きているサーバを優先度順およびラウンドロビン順に生存確認する。<br>
 * 
 * @author H.Nakano
 * @version  1.00 作成: 2003/10/08 - H.Nakano
 */
public class KeepAliveDaemonService extends ServiceBase
 implements KeepAliveDaemonServiceMBean, DaemonRunnable {
    
    private static final long serialVersionUID = -1630984556813685757L;
    
    private static final String C_NAME = "Server name: " ; //$NON-NLS-1$
    private static final String C_STATUS = "status : " ; //$NON-NLS-1$
    private static final String C_RUNNING = "running" ; //$NON-NLS-1$
    private static final String C_STOP ="stop" ; //$NON-NLS-1$
    private static final String C_ALIVEKEY = "KEEPALIVE001" ; //$NON-NLS-1$
    private static final String C_DEADKEY = "KEEPALIVE002" ; //$NON-NLS-1$
    
    //## クラス変数宣言 ##
    /** 送信サーバ稼動状態一覧 */
    protected Hashtable mServerTbl = new Hashtable();
    /** 同期用オブジェクト */
    protected Boolean mSyncObj = new Boolean(true);
    /** サーバチェッカーリスト */
    protected List mCheckerList = new ArrayList();
    /** チェックインターバル */
    protected volatile long mInterval;
    /** デーモンクラスインスタンス */
    protected Daemon mDaemon;
    /** ラウンドロビンカウンター */
    protected volatile int mRoundRobin;
    protected String aliveLogMessageId = C_ALIVEKEY;
    protected String deadLogMessageId = C_DEADKEY;
    protected boolean isOutputAliveLogMessage = true;
    protected boolean isOutputDeadLogMessage = true;
    
    public void createService() throws Exception{
        mDaemon = new Daemon(this);
        mDaemon.setName(getServiceName());
    }
    
    public  void startService() throws Exception{
        mDaemon.start();
    }
    
    public  void stopService() throws Exception{
        mDaemon.stop();
    }
    
    public  void destroyService() throws Exception{
        mDaemon = null;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public void setChekerServiceNames(ServiceName[] serviceNames){
        synchronized(mSyncObj){
            mCheckerList.clear();
            for(int cnt = 0; cnt < serviceNames.length; cnt++){
                mCheckerList.add(serviceNames[cnt]);
            }
        }
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public ServiceName[] getChekerServiceNames(){
        return (ServiceName[])mCheckerList.toArray(new ServiceName[mCheckerList.size()]);
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public void setIntervalTimeMillis(long miliseconds){
        mInterval = miliseconds ;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public long getIntervalTimeMillis(){
        return mInterval;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public String[] getStatusString(){
        CsvArrayList ret = new CsvArrayList();
        Map keepAlive = getKeepAliveMap();
        for(Iterator keys = keepAlive.keySet().iterator();keys.hasNext();){
            String serverId = (String)keys.next();
            String servername = C_NAME + serverId;
            String status = C_STATUS;
            if(Boolean.TRUE.equals((Boolean)keepAlive.get(serverId))){
                status = status + C_RUNNING;
            }else{
                status = status + C_STOP;
            }
            ret.add(servername);
            ret.add(status);
        }
        return ret.toStringAry();
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public void setAliveLogMessageId(String id){
        aliveLogMessageId = id;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public String getAliveLogMessageId(){
        return aliveLogMessageId;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public void setDeadLogMessageId(String id){
        deadLogMessageId = id;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public String getDeadLogMessageId(){
        return deadLogMessageId;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public void setOutputAliveLogMessage(boolean isOutput){
        isOutputAliveLogMessage = isOutput;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public boolean isOutputAliveLogMessage(){
        return isOutputAliveLogMessage;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public void setOutputDeadLogMessage(boolean isOutput){
        isOutputDeadLogMessage = isOutput;
    }
    
    // KeepAliveDaemonServiceMBeanのJavaDoc
    public boolean isOutputDeadLogMessage(){
        return isOutputDeadLogMessage;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onStart(){
        
        for(Iterator iterator = mCheckerList.iterator();iterator.hasNext();){
            ServiceName name = (ServiceName)iterator.next();
            KeepAliveChecker kac =(KeepAliveChecker)ServiceManagerFactory.getServiceObject(name);
            //コンニチハっていってみる
            boolean bret = kac.isAlive();
            //結果をアップデートしとく
            updateTblStructure(name, bret);
        }
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onStop(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onSuspend(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public boolean onResume(){
        return true;
    }
    
    // DaemonRunnableのJavaDoc
    public Object provide(DaemonControl ctrl) throws Exception{
        Thread.sleep(mInterval);
        return null;
    }
    
    // DaemonRunnableのJavaDoc
    public void consume(Object paramObj, DaemonControl ctrl) throws Exception{
        onStart();
    }
    
    // DaemonRunnableのJavaDoc
    public void garbage(){
    }
    
    //
    /**
     * 送信サーバ一覧を更新する。<p>
     * 引数で指定された送信サーバの稼動状態を送信サーバ一覧に更新する。
     *
     * @param msid サーバ名
     * @param keepAlive 稼動状態
     */
    protected void updateTblStructure(Object msid, boolean keepAlive){
        synchronized(mSyncObj){
            Boolean bret = (Boolean)mServerTbl.get(msid);
            if(bret != null){
                if(!bret.booleanValue() && keepAlive){
                    //復活！！
                    if(isOutputAliveLogMessage){
                        getLogger().write(aliveLogMessageId, msid);
                    }
                }
                if(bret.booleanValue() && !keepAlive){
                    //停止
                    if(isOutputDeadLogMessage){
                        getLogger().write(deadLogMessageId, msid);
                    }
                }
            }
            mServerTbl.put(msid, new Boolean(keepAlive));
        }
    }
    
    // QueryKeepAliveのJavaDoc
    public Map getKeepAliveMap(){
        synchronized(mSyncObj){
            return (Map)mServerTbl.clone();
        }
    }
    
    // QueryKeepAliveのJavaDoc
    public void updateTbl(Object msid, boolean keepAlive) {
        synchronized(mSyncObj) {
            Boolean bret = (Boolean)mServerTbl.get(msid);
            if(bret != null){
                updateTblStructure(msid,keepAlive);
            }
        }
    }
    
    // QueryKeepAliveのJavaDoc
    public List getPriolityAry(){
        CsvArrayList ret = new CsvArrayList();
        synchronized(mSyncObj){
            for(ListIterator iterator = mCheckerList.listIterator();iterator.hasNext();){
                ServiceName name = (ServiceName)iterator.next();
                Boolean  bret = (Boolean)this.mServerTbl.get(name);
                if(bret.booleanValue()){
                    ret.add(name);
                }
            }
        }
        return ret;
    }
    
    // QueryKeepAliveのJavaDoc
    public List getPriolityAry(Set available){
        synchronized(mSyncObj){
            List ret = getPriolityAry();
            for(ListIterator iterator = ret.listIterator();iterator.hasNext();){
                Object name = (Object)iterator.next();
                if(!available.contains(name)){
                    ret.remove(name);
                }
            }
            return ret;
        }
    }
    
    // QueryKeepAliveのJavaDoc
    public List getRoundrobinAry() {
        CsvArrayList ret = new CsvArrayList() ;
        synchronized(mSyncObj) {
            for(int cnt = mRoundRobin; cnt < mCheckerList.size(); cnt++){
                ServiceName name = (ServiceName)mCheckerList.get(cnt);
                Boolean  bret = (Boolean)this.mServerTbl.get(name);
                if(bret.booleanValue()){
                    ret.add(name);
                }
            }
            for(int cnt = 0; cnt < mRoundRobin; cnt++){
                ServiceName name = (ServiceName)mCheckerList.get(cnt);
                Boolean  bret = (Boolean)this.mServerTbl.get(name) ;
                if(bret.booleanValue()){
                    ret.add(name) ;
                }
            }
            mRoundRobin++;
            if(mCheckerList.size() <= mRoundRobin){
                mRoundRobin = 0;
            }
        }
        return ret;
    }
    
    // QueryKeepAliveのJavaDoc
    public List getRoundrobinAry(Set available) {
        synchronized(mSyncObj) {
            List ret = getRoundrobinAry();
            for(ListIterator iterator = ret.listIterator();iterator.hasNext();){
                Object name = iterator.next();
                if(!available.contains(name)){
                    ret.remove(name);
                }
            }
            return ret;
        }
    }
}
