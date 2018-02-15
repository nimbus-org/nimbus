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
package jp.ossc.nimbus.service.performance;
// インポート
import java.util.*;
import java.text.SimpleDateFormat;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.lang.*;
import jp.ossc.nimbus.util.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.queue.*;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.service.writer.*;
//
/**
 * スタティスティクス管理クラス。<BR>
 * スタティスティクスの検索、登録を行う。<BR>
 * @author H.Nakano
 * @version 1.00 
 */
public class FileReportPerformanceStatisticsService extends ServiceBase
 implements PerformanceStatistics, FileReportPerformanceStatisticsServiceMBean,
            DaemonRunnable{
    
    private static final long serialVersionUID = 3609558424484833722L;
    
    /** デフォルトインターバル(10分) */
    private static final String C_DEFAULT_INTERVAL = "600";
    
    /** デフォルトインターバル(10分) */
    private static final String C_SEP = "---";
    private static final String C_FORMAT = "yyyy-MM-dd HH-mm-ss";
    private static final SimpleDateFormat formatter
        = new SimpleDateFormat(C_FORMAT);
    
    /** MBeanセッタ保持変数(ソートキー) */
    protected String mSortKey;
    /** MBeanセッタ保持変数(書き出しインターバル秒) */
    protected String mIntervalSec = C_DEFAULT_INTERVAL;
    
    /** MBeanセッタ保持変数（ファイル書き出しレコードファクトリコンポーネント名）*/
    protected ServiceName mWritableRecordFactoryName;
    /** ファイル書き出しレコードファクトリコンポーネント*/
    protected WritableRecordFactoryService mWritableRecFac;
    
    /** MBeanセッタ保持変数（ファイル書き出しコンポーネント名）*/
    protected ServiceName mWriterName;
    /** ファイル書き出しコンポーネント */
    protected MessageWriter mWriter;
    
    /** MBeanセッタ保持変数（キュー部品コンポーネント名）*/
    protected ServiceName mQueueName;
    /** パフォーマンス算出キュー*/
    protected Queue mQueue;
    /**
     * {@link #getQueueServiceName()}がnullの場合、デフォルトの{@link Queue}サービスとして生成する{@link DefaultQueueService}サービス。<p>
     */
    private DefaultQueueService defaultQueue;
    
    /** パフォーマンス更新デーモンスレッドオブジェクト */
    protected Daemon mPerformDaemon;
    /** パフォーマンス出力デーモンスレッドオブジェクト */
    protected Daemon mWriterDaemon;
    
    /** パフォーマンス管理ハッシュ */
    protected Hashtable mHash = null;
    /** パフォーマンスレコードクラス名 */
    protected String mClassName = null;
    /** パフォーマンスレコードクラス */
    protected Class mClsRec = null;
    
    /**
     * コンストラクタ。<BR>
     * HashをインスタンシングしてKeyをセットする。<BR>
     */
    public FileReportPerformanceStatisticsService(){
        super();
    }
    
    /**
     * サービスの生成処理を行う。<p>
     *
     * @exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        mHash = new Hashtable(1024,256);
    }
    
    /**
     * Queueを設定する。
     */
    public void setQueue(Queue queue) {
        mQueue = queue;
    }

    /**
     * WritableRecordFactoryServiceを設定する。
     */
    public void setWritableRecordFactoryService(WritableRecordFactoryService writableRecFac) {
        mWritableRecFac = writableRecFac;
    }

    /**
     * MessageWriterを設定する。
     */
    public void setMessageWriter(MessageWriter writer) {
        mWriter = writer;
    }

    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#startService()
     */
    public void startService() throws Exception{
        if(mClsRec == null){
            throw new ServiceException("PEFORMANCE010","RecordClass is null");
        }
        
        // ファイル書き出しコンポーネント取得
        mWriter = (MessageWriter)ServiceManagerFactory.getServiceObject(
            mWriterName
        );
        
        // ライタブルレコードファクトリコンポーネント取得
        mWritableRecFac = (WritableRecordFactoryService)ServiceManagerFactory
            .getServiceObject(mWritableRecordFactoryName);
        
        
        // Queueサービスの生成または取得
        if(getQueueServiceName() == null){
            if(getDefaultQueueService() == null){
                final DefaultQueueService defaultQueue
                     = new DefaultQueueService();
                defaultQueue.create();
                defaultQueue.start();
                setDefaultQueueService(defaultQueue);
            }else{
                getDefaultQueueService().start();
            }
            mQueue = getDefaultQueueService();
        }else{
            mQueue = (Queue)ServiceManagerFactory
                    .getServiceObject(getQueueServiceName());
        }
        
        // 書き出しインターバルチェック
        if (mIntervalSec == null || "".equals(mIntervalSec)){
            mIntervalSec = C_DEFAULT_INTERVAL;
        }else{
            try{
                Long.parseLong(mIntervalSec);
            }catch(Exception ex){
                throw new ServiceException("PEFORMANCE013","interval setting invalid", ex);
            }
        }
        
        // キュー受付開始
        mQueue.accept();
        
        // パフォーマンス更新デーモンスレッド生成
        mPerformDaemon = new Daemon(this);
        // ファイル出力デーモンスレッド生成
        mWriterDaemon = new Daemon(new WriterDaemonRunnable(this));
        mPerformDaemon.start();
        mWriterDaemon.start();
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#stopService()
     */
    public void stopService() throws Exception{
        clear();
        
        mPerformDaemon.stop();
        mWriterDaemon.stop();
        
        // キュー受付停止
        mQueue.release();
        
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを停止する
        if(mQueue == getDefaultQueueService()){
            getDefaultQueueService().stop();
        }
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.core.ServiceBaseSupport#destroyService()
     */
    public void destroyService() throws Exception{
        mHash = null;
        mClassName = null;
        mClsRec = null;
        
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを破棄する
        if(mQueue == getDefaultQueueService()){
            getDefaultQueueService().destroy();
            setDefaultQueueService(null);
        }
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.performance.PerformanceStatistics#entry(java.lang.String, long)
     */
    public void entry(String key, long msec) {
        if(key == null || key.length() == 0){
            return ;
        }
        final ArrayList enqueueList = new ArrayList(2);
        enqueueList.add(key);
        enqueueList.add(new Long(msec));
        // パフォーマンスエントリをキュー投入する。
        this.mQueue.push(enqueueList);
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.performance.FileReportPerformanceStatisticsServiceMBean#clear()
     */
    public void clear(){
        synchronized(mHash){
            this.mHash.clear();
        }
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.performance.FileReportPerformanceStatisticsServiceMBean#toStringAry(int, boolean)
     */
    public String[] toStringAry(int sortKey, boolean isUpset){
        // ログを取得する
        final ArrayList sortList = new ArrayList();
        final CsvArrayList retAry = new CsvArrayList();
        Hashtable tb = null;
        synchronized(mHash){
            tb = (Hashtable)mHash.clone();
        }
        /** データリストから一項目づつ取り出す。*/
        for(Enumeration enumeration = tb.elements(); enumeration.hasMoreElements(); ){
            final PerformanceRecord item = (PerformanceRecord)enumeration.nextElement();
            if(item != null){
                //キーソートメソッドをコール
                _sortList(sortList, item, sortKey, isUpset);
            }
        }
        retAry.add(C_SEP);
        retAry.add(getNowDate());
        /** キーソートリストから出力文字配列にデータを転記 */
        for(ListIterator iterator = sortList.listIterator(); iterator.hasNext(); ){
            //KEY文字データ取得・CSV分解
            final String sortItem = (String)iterator.next();
            final CsvArrayList keyAry = new CsvArrayList();
            keyAry.split(sortItem, ";");
            //キーでHASHから対象パフォーマンスマネージャを取り出す。
            if(mHash.containsKey(keyAry.getStr(0))){
                final PerformanceRecord item = (PerformanceRecord)mHash.get(keyAry.getStr(0));
                //出力リストにパフォーマンス文字格納
                retAry.add(item.toString());
            }
        }
        /** 出力 */
        final String[] retStrAry = retAry.toStringAry();
        return retStrAry;
    }
    //
    /**
     * 指定のソートキーでソートを行う。<BR>
     * @param sortList - ソート結果格納配列
     * @param item - PerformanceMangerオブジェクト
     * @param sortKey - ソートキー
     * @param isUpset - 昇順、降順の指定
     */
    private void _sortList(ArrayList sortList, PerformanceRecord item, int sortKey, boolean isUpset){
        if(sortList == null || item == null){
            return;
        }
        String cmpKey = "";
        if(sortKey == C_NAME){
            cmpKey = item.getResourceId();
        }else if(sortKey == C_BEST){
            final Long tmpLong = new Long(item.getBestPerformance());
            cmpKey = tmpLong.toString();
        }else if(sortKey == C_WORST){
            final Long tmpLong = new Long(item.getWorstPerformance());
            cmpKey = tmpLong.toString();
        }else if(sortKey == C_AVERAGE){
            final Long tmpLong = new Long(item.getAveragePerformance());
            cmpKey = tmpLong.toString();
        }else if(sortKey == C_COUNT){
            final Long tmpLong = new Long(item.getCallTime());
            cmpKey = tmpLong.toString();
        }
        /** sortデータ文字列を作成する<BR>
         *    resourceId + ";" 比較データ  */
        final String rscId = item.getResourceId() + ";" + cmpKey;
        int entryCnt = 0;
        /** sortListにソートインサートする。 */
        for(ListIterator iterator = sortList.listIterator(); iterator.hasNext(); entryCnt++){
            //リストのコンペア項目を取り出す。
            final String destCmp = (String)iterator.next();
            final CsvArrayList parse = new CsvArrayList();
            parse.split(destCmp, ";");
            //コンペア
            final int ret = cmpKey.compareTo(parse.getStr(1));
            if(isUpset){
                if(ret <= 0){
                    break;
                }
            }else{
                if(ret >= 0){
                    break;
                }
            }
        }
        sortList.add(entryCnt, rscId);
    }
    /* (非 Javadoc)
     * @see jp.ossc.nimbus.service.performance.FileReportPerformanceStatisticsServiceMBean#setRecordClassName(java.lang.String)
     */
    public void setRecordClassName(String className) throws ServiceException{
        this.mClassName = className;
        if(className == null || className.length() == 0){
            this.mClsRec = null;
        }else{
            try{
                mClsRec = Class.forName(
                    className,
                    true,
                    NimbusClassLoader.getInstance()
                );
            }catch(ClassNotFoundException e){
                throw new ServiceException("PEFORMANCE010","ClassNotFoundException classnamse = " + className,e);//MOD 2004-02-09
            }
        }
    }
    /**
     * @return String - パフォーマンスクラス名
     * @see jp.ossc.nimbus.service.performance.FileReportPerformanceStatisticsServiceMBean#getRecordClassName()
     */
    public String getRecordClassName(){
        return this.mClassName;
    }
    /**
     * キュー部品コンポーネント名を設定する.
     * @param name - キュー部品コンポーネント名
     */
    public void setQueueServiceName(ServiceName name){
        this.mQueueName = name;
    }
    /**
     * キュー部品コンポーネント名を取得する。
     * @return ServiceName - キュー部品コンポーネント名
     */
    public ServiceName getQueueServiceName(){
        return this.mQueueName;
    }
    
    /**
     * Queueが指定されていない場合に使用する{@link DefaultQueueService}を取得する。<p>
     * このDefaultQueueServiceは、無名サービスとして生成される。また、{@link #setQueueServiceName(ServiceName)}でQueueが指定されている場合は、nullを返す場合がある。<br>
     *
     * @return DefaultQueueServiceオブジェクト。生成されていない場合はnullを返す。
     */
    protected DefaultQueueService getDefaultQueueService(){
        return defaultQueue;
    }
    
    /**
     * Queueが指定されていない場合に使用する{@link DefaultQueueService}を設定する。<p>
     *
     * @param queue DefaultQueueServiceオブジェクト
     */
    protected void setDefaultQueueService(DefaultQueueService queue){
        defaultQueue = queue;
    }
    
    /**
     * ファイル書き出しコンポーネント名を設定する。
     * @param name - ファイル書き出しコンポーネント名
     */
    public void setWriterServiceName(ServiceName name){
        this.mWriterName = name;
    }
    /**
     * ファイル書き出しコンポーネント名を設定する。
     * @return - ファイル書き出しコンポーネント名
     */
    public ServiceName getWriterServiceName(){
        return this.mWriterName;
    }
    /**
     * ファイル書き出しレコードファクトリコンポーネント名を設定する。
     * @param name - ファイル書き出しレコードファクトリコンポーネント名
     */
    public void setWriteableRecordFactoryServiceName(ServiceName name){
        this.mWritableRecordFactoryName = name;
    }
    /**
     * ファイル書き出しレコードファクトリコンポーネント名を取得する。
     * @return ServiceName - ファイル書き出しレコードファクトリコンポーネント名
     */
    public ServiceName getWriteableRecordFactoryServiceName(){
        return this.mWritableRecordFactoryName;
    }
    /**
     * ファイル書き出しインターバル(秒)を設定する。
     * @param intervalSec - インターバル(秒)
     */
    public void setWritableInterval(String intervalSec){
        this.mIntervalSec = intervalSec;
    }
    /**
     * ファイル書き出しインターバル(秒)を取得する。
     * @return String - インターバル(秒)
     */
    public String getWritableInterval(){
        return this.mIntervalSec;
    }
    /**
     * ソートキーを設定する。
     * @param inSortKey - ソートキー
     */
    public void setSortKey(String inSortKey){
        this.mSortKey = inSortKey;
    }
    /**
     * ソートキーを取得する。
     * @return String - ソートキー
     */
    public String getSortKey(){
        return this.mSortKey;
    }
    /**
     * デーモンスレッドの停止承認を行う。
     * @return true ストップ承認、false ストップ非承認
     */
    public boolean onStop(){
        return true;
    }
    /**
     * デーモンスレッドのサスペンド承認を行う。
     * @return boolean - true:サスペンド承認 false:サスペンド非承認
     */
    public boolean onSuspend(){
        return true;
    }
    /**
     * デーモンスレッドのレジュ－ム承認を行う。
     * @return boolean - true:レジュ－ム承認 false:レジュ－ム非承認
     */
    public boolean onResume(){
        return true;
    }
    /**
     * デーモンスレッドのブロッキング処理を行う。メモリキューからジャーナルオブジェクトを取得する。
     *    @return Object - ジャーナルオブジェクト
     */
    public Object provide(DaemonControl ctrl){
        return this.mQueue.get();
    }
    /**
     * デーモンスレッドの処理を行う。パフォーマンス更新を行う。
     * @param paramObj - ジャーナルオブジェクト
     * @param ctrl - DaemonControl
     */
    public void consume(Object paramObj, DaemonControl ctrl) throws ServiceException{
        if(paramObj == null){
            return;
        }
        // キューからentryデータを取得する。
        final ArrayList entryList = (ArrayList)paramObj;
        final String key = (String)entryList.get(0);
        long msec = ((Long)entryList.get(1)).longValue();
        // パフォーマンス更新
        PerformanceRecordOperator performanceObj = null;
        performanceObj = (PerformanceRecordOperator)mHash.get(key);
        if(performanceObj != null){
            performanceObj.entry(msec);
        }else{
            try{
                performanceObj = (PerformanceRecordOperator)mClsRec.newInstance();
            }catch(InstantiationException e){
                throw new ServiceException("PEFORMANCE001","InstantiationException",e);//MOD 2004-02-09
            }catch(IllegalAccessException e){
                throw new ServiceException("PEFORMANCE001","IllegalAccessException",e); //MOD 2004-02-09
            }
            performanceObj.setResourceId(key);
            performanceObj.entry(msec);
            mHash.put(key,performanceObj);
        }
    }
    /**
     * デーモンスレッドの後処理を行う。メモリキューに残っているジャーナルログを全て出力する。
     */
    public void garbage(){
        if(mQueue == null){
            return;
        }
        //メモリキューの内容がなくなるまで
        while(mQueue.size() > 0){
            Object obj = mQueue.get();
            try{
                consume(obj,mPerformDaemon);
            }catch(Exception e){
            }
        }
    }
    /**
     * デーモンスレッドのスタート承認を行う。
     *    @return true スタート承認、false スタート非承認
     */
    public boolean onStart(){
        return true;
    }
    //
    private static final String getNowDate(){
        return formatter.format(new Date());
    }
}
