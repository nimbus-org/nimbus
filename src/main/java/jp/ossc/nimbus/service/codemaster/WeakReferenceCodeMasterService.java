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
// パッケージ
// インポート
package jp.ossc.nimbus.service.codemaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.lang.ServiceException;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvoker;
import jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory;
import jp.ossc.nimbus.service.cache.Cache;
import jp.ossc.nimbus.service.cache.CachedReference;
import jp.ossc.nimbus.service.codemaster.map.CacheMap;
import jp.ossc.nimbus.core.ServiceBase;

/**
 * 弱参照を用いたコードマスタクラス<p>
 * キャッシュサービスを使用して弱参照を用いたコードマスタの管理を行う
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class WeakReferenceCodeMasterService extends ServiceBase implements
        WeakReferenceCodeMasterServiceMBean, CodeMasterFinder {

    private static final long serialVersionUID = 3626251129819845012L;

    /** 検索更新日付キー */
    private static final String FIND_DATE_KEY = "date";
    /** マスターデータオブジェクトキー */
    private static final String MASTER_DATA_KEY = "data";

    /**コードマスタ名。この名前がBeanFlowのキーとして使用される。*/
    private String[] mMasterNames= null ;

    private String[] notUpdateAllMasterNames;

    /**マスター格納用Hash*/
    protected HashMap mMaster= null;
    /**ロガー名*/
    private ServiceName mLoggerServiceName = null ;
    /**BeanFlowInvokerファクトリ名*/
    private ServiceName mBFInvokerFactoryName = null;
    /**BeanFlowInvokerファクトリ*/
    private BeanFlowInvokerFactory mBFInvokerFactory = null;
    /**Cacheサービス名*/
    private ServiceName mCacheServiceName;
    /**Cacheサービス実体*/
    private Cache mCache;

    //セッター/ゲッター
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#setMasterNames(java.lang.String[])
     */
    public void setMasterNames(String[] names) {
        mMasterNames = names;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#setLoggerServiceName(jp.ossc.nimbus.core.ServiceName)
     */
    public void setLoggerServiceName(ServiceName name) {
        mLoggerServiceName = name;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#getMasterNames()
     */
    public String[] getMasterNames() {
        return mMasterNames;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#setLoggerServiceName()
     */
    public ServiceName getLoggerServiceName() {
        return mLoggerServiceName;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#setBeanFlowInvokerFactoryName(jp.ossc.nimbus.core.ServiceName)
     */
    public void setBeanFlowInvokerFactoryName(ServiceName name) {
        mBFInvokerFactoryName = name;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#getBeanFlowInvokerFactoryName()
     */
    public ServiceName getBeanFlowInvokerFactoryName() {
        return mBFInvokerFactoryName;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#setCacheServiceName(jp.ossc.nimbus.core.ServiceName)
     */
    public void setCacheServiceName(ServiceName name) {
        mCacheServiceName = name ;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.WeakReferenceCodeMasterServiceMBean#getCacheServiceName()
     */
    public ServiceName getCacheServiceName() {
        return mCacheServiceName;
    }

    // CodeMasterServiceMBean のJavaDoc
    public String[] getNotUpdateAllMasterNames() {
        return notUpdateAllMasterNames;
    }

    // CodeMasterServiceMBean のJavaDoc
    public void setNotUpdateAllMasterNames(String[] names) {
        this.notUpdateAllMasterNames = names;
    }

    /**
     * BeanFlowInvokerFactoryを設定する。
     */
    public void setBeanFlowInvokerFactory(BeanFlowInvokerFactory invokerFactory) {
        mBFInvokerFactory = invokerFactory;
    }

    /**
     * Cacheを設定する。
     */
    public void setCache(Cache cache) {
        mCache = cache;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
     */
    public void startService() throws Exception {
        if( mMasterNames == null ){
            throw new IllegalArgumentException("Attribute : MasterNames is null");
        }
        mMaster = new HashMap() ;

        if( mCacheServiceName != null) {
            mCache = (Cache)ServiceManagerFactory.getServiceObject(mCacheServiceName);
        } else if(mCache == null) {
            throw new IllegalArgumentException("Attribute : CacheServiceName or Cache is null");
        }

        if(mBFInvokerFactoryName != null) {
            mBFInvokerFactory = (BeanFlowInvokerFactory)ServiceManagerFactory.getServiceObject(mBFInvokerFactoryName);
        } else if(mBFInvokerFactory == null) {
            throw new IllegalArgumentException("Attribute : BeanFlowInvokerFactoryName or BeanFlowInvokerFactory is null");
        }

        try {
            initMasterHash();
        } catch ( ServiceException e) {
            throw e;
        }
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
     */
    public void stopService()  {
        this.mBFInvokerFactory = null;
        this.mBFInvokerFactoryName = null;
        if( this.mCache != null ) {
            this.mCache.clear();
        }
        this.mCache = null;
        this.mCacheServiceName = null;
        this.mMasterNames = null;
        if( this.mMaster != null ){
            this.mMaster.clear();
        }
        mMaster = null;
    }

    /**
     * マスタ管理テーブルを初期化する
     * @throws ServiceException
     */
    private void initMasterHash () throws ServiceException {
        for( int cnt = 0 ; cnt < mMasterNames.length ; cnt++ ){
            final String bfname = mMasterNames[cnt];
            final BeanFlowInvoker invoker = mBFInvokerFactory.createFlow(bfname);
            if( invoker == null ){
                //BeanFlowInvokerFactoryは無効キーでNULLを返します
                throw new ServiceException("WeakReferenceCodeMasterService001","Cannot specify Invoker with key ->"+bfname);
            }
            //時系列管理マスタテーブルを作成
            TimeManageMaster tmgr = new TimeManageMaster() ;
            tmgr.setMasterName(bfname) ;
            //マスタに登録
            this.mMaster.put(bfname,tmgr) ;
            Object outMaster = null;
            try {
                //BeanFlowを実行する
                outMaster = invoker.invokeFlow(null);
            } catch ( Exception e ){
                throw new ServiceException("WeakReferenceCodeMasterService002","Exception occured in Invoker with key ->"+bfname,e);
            }
            if( outMaster != null ){
                //コードマスタを登録する(内部で弱参照に変換される)
                tmgr.addMaster(new Date(),outMaster) ;
            }
        }
    }
       /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.codemaster.CodeMasterFinder#getCodeMasters()
     */
    public Map getCodeMasters()  {
        Map map = new CacheMap() ;
        Date nowDate = new Date();
        Set keys = this.mMaster.keySet();
        Iterator ite = keys.iterator() ;
        //全コードマスターをキーMapに格納
        while (ite.hasNext()){
            String key = (String)ite.next() ;
            TimeManageMaster tmp = null ;
            synchronized(this.mMaster){
                tmp = (TimeManageMaster)this.mMaster.get(key) ;
            }
            //現在時刻でマスターを検索
            Object mst = tmp.getMaster(nowDate) ;
            //弱参照のまま登録する
            map.put(key,mst) ;
        }
        return map;
    }

    /**
     * 全マスター更新・現在時刻
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void codeMasterRefresh(){
        //現在時刻取得
        codeMasterRefresh(new Date());
    }
    /**
     * 全マスター更新・指定時刻
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void codeMasterRefresh(Date date){
        codeMasterRefresh(mMasterNames,date);
    }
    /**
     * 指定マスター更新・現在時刻
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void codeMasterRefresh(String flowName){
        //現在時刻取得
        codeMasterRefresh(flowName,new Date());
    }
    /**
     * 指定マスター更新・指定時刻
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void codeMasterRefresh(String flowName,Date date){
        final String[] flows = { flowName };
        codeMasterRefresh(flows,date);
    }

    /**
     * マスター更新
     * Message は MapMessageとし、<br>
     * nameとvalueの組み合わせは、<br>
     * "key" (String)  | [マスター名] (String)<br>
     * "date" (String) | [データ有効日時](long)<br>
     * で設定すること<br>
     * 指定した日付以降の日付が既に設定されていれば、該当するマスタデータを無効にする
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    private void codeMasterRefresh(String[] flowNames ,Date date) {
        for ( int i = 0 ; i < flowNames.length ; i ++ ){
            String bfname = flowNames[i] ;
            final BeanFlowInvoker invoker = mBFInvokerFactory.createFlow(bfname);
            if( invoker == null ){
                //BeanFlowInvokerFactoryは無効キーでNULLを返す
                throw new ServiceException("WeakReferenceCodeMasterService004","Cannot specify Invoker with key ->"+bfname);
            }
            TimeManageMaster tmgr = (TimeManageMaster) this.mMaster.get(bfname);
            //無かった場合新しく登録を行う
            if( tmgr == null ){
                //時系列管理マスタテーブルを作成
                tmgr = new TimeManageMaster() ;
                tmgr.setMasterName(bfname) ;
                //マスタに登録
                synchronized( mMaster ) {
                    this.mMaster.put(bfname,tmgr) ;
                }
            }
            Object outMaster = null;
            try {
                //BeanFlowを実行する
                outMaster = invoker.invokeFlow(null);
            } catch ( Exception e ){
                throw new ServiceException("WeakReferenceCodeMasterService005","Exception occured in Invoker with key ->"+bfname,e);
            }
            if( outMaster == null ){
                throw new ServiceException("WeakReferenceCodeMasterService006","Return codemaster is null : key ->"+bfname);
            }
            final TimeManageMaster newTm = tmgr.cloneOwn() ;
            //マスタを登録(内部でキャッシュ参照に変換される)
            newTm.addMaster(date,outMaster) ;
            //現在時刻で不要なマスタを削除
            newTm.clear() ;
            synchronized(this.mMaster){
                this.mMaster.put(bfname,newTm) ;
            }
        }
    }

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

    public void updateCodeMaster(String key) throws Exception{
        updateCodeMaster(key, new Date());
    }

    public void updateCodeMaster(String key, Date updateTime) throws Exception{
        updateCodeMaster(key, null, updateTime);
    }

    public void updateCodeMaster(String key, Object input, Date updateTime){
        String bfname = key ;
        Date date = updateTime;
        final BeanFlowInvoker invoker = mBFInvokerFactory.createFlow(bfname);
        if( invoker == null ){
            //BeanFlowInvokerFactoryは無効キーでNULLを返す
            throw new ServiceException("WeakReferenceCodeMasterService004","Cannot specify Invoker with key ->"+bfname);
        }
        TimeManageMaster tmgr = (TimeManageMaster) this.mMaster.get(bfname);
        //無かった場合新しく登録を行う
        if( tmgr == null ){
            //時系列管理マスタテーブルを作成
            tmgr = new TimeManageMaster() ;
            tmgr.setMasterName(bfname) ;
            //マスタに登録
            synchronized( mMaster ) {
                this.mMaster.put(bfname,tmgr) ;
            }
        }
        Object outMaster = null;
        try {
            //BeanFlowを実行する
            outMaster = invoker.invokeFlow(input);
        } catch ( Exception e ){
            throw new ServiceException("WeakReferenceCodeMasterService005","Exception occured in Invoker with key ->"+bfname,e);
        }
        if( outMaster == null ){
            throw new ServiceException("WeakReferenceCodeMasterService006","Return codemaster is null : key ->"+bfname);
        }
        final TimeManageMaster newTm = tmgr.cloneOwn() ;
        if(date == null){
            date = new Date();
        }
        //マスタを登録(内部でキャッシュ参照に変換される)
        newTm.addMaster(date,outMaster) ;
        //現在時刻で不要なマスタを削除
        newTm.clear() ;
        synchronized(this.mMaster){
            this.mMaster.put(bfname,newTm) ;
        }
    }

    // CodeMasterFinderのJavaDoc
    public Set getCodeMasterNameSet(){
        return mMaster == null
             ? new HashSet() : new HashSet(mMaster.keySet());
    }

    /**
     * マスターBeanの時刻での管理を行うクラス<p>
     * @version $Name:  $
     * @author H.Nakano
     * @since 1.0
     */
    private class TimeManageMaster{
        private String mFlowKey = null ;
        private ArrayList mTimeList = null ;
        /**
         * コンストラクター
         */
        public TimeManageMaster(){
            mTimeList = new ArrayList() ;
        }
        /**
         * マスター名設定
         * @param name
         */
        public void setMasterName(String name){
            mFlowKey = name ;
        }
        /**
         * マスター名取得
         * @return
         */
        public String getMasterName(){
            return mFlowKey ;
        }
        /**
         * マスターデータ追加
         * @param time
         * @param master
         */
        public void addMaster(Date time ,Object master){            //登録を消去
            HashMap rec = new HashMap() ;
            //キャッシュ参照に変換
            CachedReference wref = mCache.add(master);
            //キャッシュ参照を登録
            rec.put(MASTER_DATA_KEY,wref) ;
            rec.put(FIND_DATE_KEY,time) ;
            boolean instFlg = false ;
            for(int cnt= mTimeList.size()-1; cnt > -1 ;cnt--){
                Map map = (Map)mTimeList.get(cnt) ;
                Date tmpTime = (Date)map.get(FIND_DATE_KEY) ;
                if(tmpTime.before(time)){
                    //時刻が前のものを発見
                    if(cnt== mTimeList.size()-1){
                        mTimeList.add(rec) ;
                    }else{
                        mTimeList.add(cnt+1,rec) ;
                    }
                    instFlg = true ;
                    break ;
                } else if( tmpTime.equals(time) ){
                    //同時刻の場合、置き換える
                    mTimeList.set(cnt,rec);
                    instFlg = true ;
                    break ;
                }
            }
            if(!instFlg){
                //実は最も早い時刻だった
                if(mTimeList.size()==0){
                    mTimeList.add(rec) ;
                }else{
                    mTimeList.add(0,rec) ;
                }
            }
        }
        /**
         * 指定時刻でのマスター(CachedReferenceとなる)取得
         * @param time
         * @return
         */
        public Object getMaster(Date time){
            Object ret = null ;
            for(int cnt= mTimeList.size()-1; cnt > -1 ;cnt--){
                Map map = (Map)mTimeList.get(cnt) ;
                Date tmpTime = (Date)map.get(FIND_DATE_KEY) ;
                if(tmpTime.before(time)){
                    ret= map.get(MASTER_DATA_KEY) ;
                    break ;
                }
            }
            return ret ;
        }
        /**
         * 現在時刻で不必要なマスターを破棄
         */
        public void clear(){
            Date now = new Date() ;
            for(int cnt= mTimeList.size()-1; cnt >= 0 ;cnt--){
                Map map = (Map)mTimeList.get(cnt) ;
                Date tmpTime = (Date)map.get(FIND_DATE_KEY) ;
                if(tmpTime.before(now)){
                    if(cnt>0){
                        for(int rcnt = cnt-1;rcnt>=0;rcnt--){
                            HashMap masterMap = (HashMap) mTimeList.get(rcnt);
                            //キャッシュ参照を消去
                            CachedReference ref = (CachedReference) masterMap.get(MASTER_DATA_KEY);
                            mCache.remove(ref);
                            //登録を消去
                            mTimeList.remove(rcnt) ;
                        }
                        break;
                    }
                }
            }
        }
        /**
         * クローン
         * @return
         */
        public TimeManageMaster cloneOwn(){
            TimeManageMaster ret = new TimeManageMaster() ;
            ret.setMasterName(this.getMasterName()) ;
            for(int cnt= 0;cnt<mTimeList.size();cnt++){
                ret.mTimeList.add(this.mTimeList.get(cnt));
            }
            return ret ;
        }
    }

}
