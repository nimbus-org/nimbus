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
package jp.ossc.nimbus.service.journal;

import java.util.*;
import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.service.writer.*;
import jp.ossc.nimbus.service.sequence.*;
import jp.ossc.nimbus.service.queue.*;
import jp.ossc.nimbus.service.queue.Queue;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.lang.*;

/**
 * ジャーナルサービス。<p>
 * 
 * @author H.Nakano
 */
public class ThreadManagedJournalService
 extends ServiceBase
 implements Journal, ThreadManagedJournalServiceMBean, DaemonRunnable, QueueHandler {
    
    private static final long serialVersionUID = 435149061357609295L;
    
    //定数定義
    private static final String C_NOP = ""; //$NON-NLS-1$
    private String mWrKeyName = "JOURNAL";  //$NON-NLS-1$
    
    //メンバー変数
    /** エディターサービスマネージャー名 */
    private ServiceName mEditorFinderName;
    
    /** エディターサービスマネージャー */
    private EditorFinder mEditorFinder;
    
    /** シークエンスサービス名 */
    private ServiceName mSequenceServiceName;
    
    /** シークエンス */
    private Sequence mSequence;
    
    /** リクエストオブジェクトスレッドローカル */
    private ThreadLocal mRequestLocal;
    
    /** カレントステップスレッドローカル */
    private ThreadLocal mCurrentLocal;
    
    /** カレントステップスレッドローカル */
    private ThreadLocal mStepLocal;
    
    /** Queueサービス名 */
    private ServiceName mQueueServiceName;
    
    /** Queue */
    private Queue mQueue;
    
    /**
     * {@link #getQueueServiceName()}がnullの場合、デフォルトの{@link Queue}サービスとして生成する{@link DefaultQueueService}サービス。<p>
     */
    private DefaultQueueService defaultQueue;
    
    /**
     * カテゴリサービス名配列。<p>
     */
    private ServiceName[] categoryNames;
    
    /**
     * カテゴリサービスリスト。<p>
     */
    private List categories;
    
    private int writeDaemonSize = 1;
    
    /** Daemonオブジェクト */
    private Daemon[] mDaemon ;
    private int mJournalLevel;
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。<br>
     * 
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        //スレッドローカル作成
        mRequestLocal = new ThreadLocal();
        mCurrentLocal = new ThreadLocal();
        mStepLocal = new ThreadLocal();
    }
    
    /**
     * EditorFinderを設定する。
     */
    public void setEditorFinder(EditorFinder editorFinder) {
        mEditorFinder = editorFinder;
    }
    
    /**
     * Queueを設定する。
     */
    public void setQueue(Queue queue) {
        mQueue = queue;
    }
    
    /**
     * Sequenceを設定する。
     */
    public void setSequence(Sequence sequence) {
        mSequence = sequence;
    }
    
    /**
     * Categoryを設定する。
     */
    public void setCategories(List categories) {
        this.categories = categories;
    }

    /**
     * サービスの開始処理を行う。<p>
     * 
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        //サービス取得
        mEditorFinder = (EditorFinder)ServiceManagerFactory.getServiceObject(
            mEditorFinderName
        );
        
        if(mSequenceServiceName != null){
            mSequence = (Sequence)ServiceManagerFactory.getServiceObject(
                mSequenceServiceName
            );
        }
        
        if(mQueueServiceName == null){
            if(mQueue == null) {
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
            }
        }else{
            mQueue = (Queue)ServiceManagerFactory
                .getServiceObject(mQueueServiceName);
        }
        
        // カテゴリの登録
        if(categories == null) {
            categories = new ArrayList();
            final ServiceName[] categoryNames = getCategoryServiceNames();
            if(categoryNames != null){
                for(int i = 0; i < categoryNames.length; i++){
                    final ServiceName categoryName = categoryNames[i];
                    final Category category = (Category)ServiceManagerFactory
                        .getServiceObject(categoryName);
                    categories.add(category);
                }
            }
        }
        
        // キュー取得待ちを開始する
        mQueue.accept();
        
        if(mQueue instanceof QueueHandlerContainer){
            ((QueueHandlerContainer)mQueue).setQueueHandler(this);
            ((QueueHandlerContainer)mQueue).start();
        }else{
            mDaemon = new Daemon[writeDaemonSize];
            for(int i = 0; i < writeDaemonSize; i++){
                mDaemon[i] = new Daemon(this);
                mDaemon[i].setName("Nimbus JournalWriterDaemon " + getServiceNameObject() + '[' + (i + 1) + ']');
                mDaemon[i].start();
            }
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * 
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService(){
        
        if(mDaemon != null){
            for(int i = 0; i < mDaemon.length; i++){
                mDaemon[i].stop();
            }
        }
        
        if(mQueue instanceof QueueHandlerContainer){
            ((QueueHandlerContainer)mQueue).stop();
        }
        
        // キュー取得待ちを開放する
        mQueue.release();
        
        // Queueサービスを無名サービスとして生成している場合、
        // そのサービスを停止する
        if(mQueue == getDefaultQueueService()){
            getDefaultQueueService().stop();
            mQueue = null;
        }
        
        categories.clear();
        
        mSequence = null;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * 
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService(){
        mRequestLocal = null;
        mCurrentLocal = null;
        mStepLocal = null;
        
        //サービス取得
        mEditorFinder = null;
        mSequence = null ;
        
        // QueueFactoryサービスを無名サービスとして生成している場合、
        // そのサービスを破棄する
        if(mQueue == getDefaultQueueService()){
            getDefaultQueueService().destroy();
            setDefaultQueueService(null);
        }
        mQueue = null;
        
        mDaemon = null;
        
        categories = null;
    }
    
    // Journal のJavaDoc
    public String getRequestId(){
        if(mCurrentLocal == null){
            return null;
        }
        
        //RootリクエストのリクエストIDを取得1
        JournalRecordImpl jr = (JournalRecordImpl)mCurrentLocal.get();
        if(jr != null){
            RequestJournal rj = (RequestJournal)jr.getObject();
            return rj.getRequestId();
        }
        return null;
    }
    
    // Journal のJavaDoc
    public void setRequestId(String requestID){
        if(mCurrentLocal == null){
            return;
        }
        //RootリクエストのリクエストIDを設定
        JournalRecordImpl jr = (JournalRecordImpl)mCurrentLocal.get();
        if(jr != null){
            RequestJournalImpl rj = (RequestJournalImpl)jr.getObject();
            rj.setRequestId(requestID) ;
        }
    }
    
    // Journal のJavaDoc
    public void startJournal(String key){
        startJournal(key, new Date(), null);
    }
    
    // Journal のJavaDoc
    public void startJournal(
        String key ,
        Date startTime,
        EditorFinder finder
    ){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl jr = (JournalRecordImpl)mRequestLocal.get();
        if(jr == null){
            
            String id = "";
            // 通番サービスが利用できるならば、
            // 通番サービスを利用してインクリメント
            if(mSequence != null){
                id = mSequence.increment();
            }
            //新規リクエストオブジェクトを作成
            RequestJournalImpl rj = new RequestJournalImpl(false);
            rj.setStartTime(startTime);
            rj.setKey(key);
            rj.setRequestId(id);
            rj.setRoot(null,null);
            jr = new JournalRecordImpl();
            if(finder == null){
                jr.setEditorFinder(mEditorFinder);
            }else{
                jr.setEditorFinder(finder);
            }
            if(key == null){
                jr.setKey(C_NOP);
            }else{
                jr.setKey(key);
            }
            jr.setParamObj(rj);
            mRequestLocal.set(jr);
            mCurrentLocal.set(jr);
            mStepLocal.set(jr);
        }else{
            JournalRecordImpl curRec = (JournalRecordImpl)mCurrentLocal.get();
            JournalRecordImpl stepRec = (JournalRecordImpl)mStepLocal.get();
            RequestJournalImpl curStep
                 = (RequestJournalImpl)stepRec.getObject();
            RequestJournalImpl newStep = new RequestJournalImpl(true);
            newStep.setKey(key);
            newStep.setRequestId(curStep.getRequestId());
            newStep.setStartTime(startTime);
            newStep.setRoot(stepRec,curRec);
            if(finder==null){
                finder = mEditorFinder;
            }
            if(key == null){
                key = C_NOP;
            }
            JournalRecord newRec = curStep.setParamObj(key,finder,newStep);
            //カレントステップ変更
            mCurrentLocal.set(newRec);
            mStepLocal.set(newRec);
        }
    }
    
    // Journal のJavaDoc
    public void startJournal(String key, Date startTime){
        startJournal(key, startTime, null);
    }
    
    // Journal のJavaDoc
    public void startJournal(String key, EditorFinder finder){
        startJournal(key, new Date(), finder);
    }
    
    public boolean isStartJournal(){
        return mRequestLocal.get() != null;
    }
    
    // Journal のJavaDoc
    public void endJournal(){
        endJournal(new Date());
    }
    
    /**
     * ジャーナルをキューに書き込む。<p>
     *
     * @param jr ジャーナルレコード
     */
    protected void writeJarnal(JournalRecordImpl jr){
        if(getState() != STARTED){
            return;
        }
        mQueue.push(jr);
    }
    
    // Journal のJavaDoc
    public void endJournal(Date endTime){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl curRec = (JournalRecordImpl)mCurrentLocal.get();
        JournalRecordImpl stepRec = (JournalRecordImpl)mStepLocal.get();
        if((curRec == null) || (stepRec == null)){
            throw new ServiceException(
                "JOURNALSERVICE001",
                "startJournal() and endJournal must be used in a pair."
            );
        }
        //処理中のステップを終了時間登録
        while(true){
            if(stepRec != null && curRec == stepRec){
                break ;
            }
            RequestJournalImpl step = (RequestJournalImpl)stepRec.getObject();
            step.setEndTime(endTime);
            stepRec = step.getStepRoot();
        }
        //リクエストオブジェクトをエディターに渡す。
        RequestJournalImpl curStep = (RequestJournalImpl) curRec.getObject();
        curStep.setEndTime(endTime);
        JournalRecordImpl rootRec = curStep.getCurRoot();
        JournalRecordImpl stepRec1 = curStep.getStepRoot();
        if(rootRec == null){
            writeJarnal(curRec);
            mRequestLocal.set(null);
            mCurrentLocal.set(null);
            mStepLocal.set(null);
        }else{
            mCurrentLocal.set(rootRec);
            mStepLocal.set(stepRec1);
        }
    }
    
    // Journal のJavaDoc
    public void addInfo(String key, Object value,int level){
        //設定されたJournalLevel以下のものは出さない
        if( level < this.getJournalLevel()){
            return;
        }
        addInfo(key,value);
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public int getJournalLevel(){
        return mJournalLevel;
    }
    
    // Journal のJavaDoc
    public void addInfo(String key, Object value){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl stepRec = (JournalRecordImpl)mStepLocal.get();
        if(stepRec != null){
            RequestJournalImpl step = (RequestJournalImpl)stepRec.getObject();
            step.setInfoObj(key,stepRec.getFinder(),value);
        }
    }
    
    // Journal のJavaDoc
    public void addInfo(String key,Object value,EditorFinder finder){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl stepRec = (JournalRecordImpl)mStepLocal.get();
        if(stepRec != null){
            RequestJournalImpl step = (RequestJournalImpl)stepRec.getObject();
            step.setInfoObj(
                key,finder == null ? stepRec.getFinder() : finder,value
            );
        }
    }
    
    // Journal のJavaDoc
    public void addInfo(
        String key,
        Object value,
        EditorFinder finder,
        int level
    ){
        if(getState() != STARTED){
            return;
        }
        //設定されたJournalLevel以下のものは出さない
        if(level < this.getJournalLevel()){
            return;
        }
        addInfo(key,value,finder);
    }
    
    public void removeInfo(int from){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl stepRec = (JournalRecordImpl)mStepLocal.get();
        if(stepRec != null){
            RequestJournalImpl step = (RequestJournalImpl)stepRec.getObject();
            step.clearParam(from);
        }
    }
    
    // Journal のJavaDoc
    public void addStartStep(String key){
        addStartStep(key, new Date(), null);
    }
    
    // Journal のJavaDoc
    public void addStartStep(
        String key,
        EditorFinder finder
    ){
        addStartStep(key, new Date(), finder);
    }
    
    // Journal のJavaDoc
    public void addStartStep(String key, Date startTime){
        addStartStep(key, startTime, null);
    }
    
    // Journal のJavaDoc
    public void addStartStep(
        String key,
        Date startTime,
        EditorFinder finder
    ){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl rootRec = (JournalRecordImpl)mRequestLocal.get();
        if(rootRec != null){
            JournalRecordImpl curRec = (JournalRecordImpl)mCurrentLocal.get();
            JournalRecordImpl stepRec = (JournalRecordImpl)mStepLocal.get();
            RequestJournalImpl curStep
                 = (RequestJournalImpl)curRec.getObject();
            RequestJournalImpl newStep = new RequestJournalImpl(true);
            newStep.setKey(key);
            newStep.setRequestId(curStep.getRequestId());
            newStep.setStartTime(startTime);
            newStep.setRoot(stepRec,curRec);
            if(finder == null){
                finder = mEditorFinder;
            }
            if(key == null){
                key = C_NOP;
            }
            JournalRecord newRec = curStep.setParamObj(key,finder,newStep);
            
            //カレントステップ変更
            mStepLocal.set(newRec);
            mCurrentLocal.set(newRec);
        }
    }
    
    // Journal のJavaDoc
    public void addEndStep(){
        addEndStep(new Date());
    }
    
    // Journal のJavaDoc
    public void addEndStep(Date endTime){
        if(getState() != STARTED){
            return;
        }
        JournalRecordImpl curRec = (JournalRecordImpl)mStepLocal.get();
        if(curRec != null){
            //startJournalで追加したステップは無効
            if(curRec.isStep() == false){
                return ;
            }
            RequestJournalImpl curStep
                 = (RequestJournalImpl)curRec.getObject();
            curStep.setEndTime(endTime);
            //カレントステップをPOP
            JournalRecordImpl root = curStep.getCurRoot();
            if(root != null){
                    mCurrentLocal.set(root);
            }
            root = curStep.getStepRoot();
            if(root != null){
                mStepLocal.set(root);
            }
        }
    }
    
    // DaemonRunnable のJavaDoc
    public boolean onStop(){
        return true;
    }
    
    // DaemonRunnable のJavaDoc
    public boolean onSuspend(){
        return true;
    }
    
    // DaemonRunnable のJavaDoc
    public boolean onResume(){
        return true;
    }
    
    /**
     * キューからジャーナルレコードを取り出す。<p>
     *
     * @param ctrl デーモン制御オブジェクト
     * @return ジャーナルレコード
     */
    public Object provide(DaemonControl ctrl){
        if(mQueue == null){
            return null;
        }
        return mQueue.get(1000);
    }
    
    /**
     * ジャーナルレコードを編集してカテゴリに書き込む。<p>
     * 
     * @param paramObj ジャーナルレコード
     * @param ctrl デーモン制御オブジェクト
     */
    public void consume(Object paramObj, DaemonControl ctrl){
        if(paramObj == null){
            return;
        }
        JournalRecord rj = (JournalRecord)paramObj;
        Object journal = rj.toObject();
        final Map elements = new HashMap();
        
        elements.put(getWritableElementKey(), journal);
        if(categories != null){
            for(int i = 0, imax = categories.size(); i < imax; i++){
                final Category category = (Category)categories.get(i);
                if(category.isEnabled()){
                    try{
                        category.write(elements);
                    }catch(MessageWriteException e){
                        // 無視する
                    }
                }
            }
        }
    }
    
    /**
     * キューから全てのジャーナルレコードを取り出して、消費する。<p>
     */
    public void garbage(){
        // キューがあればキューの残り件数分ログを書き出す
        if(mQueue != null){
            //キューの内容がなくなるまで
            while(mQueue.size() > 0){
                Object obj = mQueue.get(0);
                try{
                    consume(obj, null);
                }catch(Exception e){
                }
            }
        }
    }
    
    // DaemonRunnable のJavaDoc
    public boolean onStart(){
        return true;
    }
    // QueueHandler のJavaDoc
    public void handleDequeuedObject(Object obj) throws Throwable{
        if(obj == null){
            return;
        }
        consume(obj, null);
    }
    
    // QueueHandler のJavaDoc
    public boolean handleError(Object obj, Throwable th) throws Throwable{
        return true;
    }
    
    // QueueHandler のJavaDoc
    public void handleRetryOver(Object obj, Throwable th) throws Throwable{
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public void setEditorFinderName(ServiceName name){
        mEditorFinderName = name;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public ServiceName getEditorFinderName(){
        return mEditorFinderName;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public void setSequenceServiceName(ServiceName name){
        mSequenceServiceName = name;
    }
    
    public ServiceName getSequenceServiceName(){
        return mSequenceServiceName;
    }
    
    // ThreadManagedJournalServiceMBeanのJavaDoc
    public void setCategoryServiceNames(ServiceName[] names){
        categoryNames = names;
    }
    
    // ThreadManagedJournalServiceMBeanのJavaDoc
    public ServiceName[] getCategoryServiceNames(){
        return categoryNames;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public void setQueueServiceName(ServiceName name){
        mQueueServiceName = name;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public ServiceName getQueueServiceName(){
        return mQueueServiceName;
    }
    
    /**
     * Queueサービスが指定されていない場合に使用する{@link DefaultQueueService}を取得する。<p>
     * このDefaultQueueServiceは、無名サービスとして生成される。また、{@link #setQueueServiceName(ServiceName)}でQueueが指定されている場合は、nullを返す場合がある。<br>
     *
     * @return DefaultQueueServiceオブジェクト。生成されていない場合はnullを返す。
     */
    protected DefaultQueueService getDefaultQueueService(){
        return defaultQueue;
    }
    
    /**
     * Queueサービスが指定されていない場合に使用する{@link DefaultQueueService}を設定する。<p>
     *
     * @param queue DefaultQueueServiceオブジェクト
     */
    protected void setDefaultQueueService(DefaultQueueService queue){
        defaultQueue = queue;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public String getWritableElementKey(){
        return mWrKeyName;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public void setWritableElementKey(String string){
        mWrKeyName = string;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public void setJournalLevel(int level){
        mJournalLevel = level;
    }
    
    // ThreadManagedJournalServiceMBean のJavaDoc
    public void setWriteDaemonSize(int size){
        writeDaemonSize = size;
    }
    // ThreadManagedJournalServiceMBean のJavaDoc
    public int getWriteDaemonSize(){
        return writeDaemonSize;
    }
    
    /**
     * 現在のジャーナル出力文字列を取得する。<p>
     * 
     * @param finderServiceName ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}サービスのサービス名
     * @return 現在のジャーナル出力文字列
     */
    public String getCurrentJournalString(ServiceName finderServiceName){
        EditorFinder finder = (EditorFinder)ServiceManagerFactory
            .getServiceObject(finderServiceName);
        return getCurrentJournalString(finder);
    }
    
    // Journal のJavaDoc
    public String getCurrentJournalString(EditorFinder finder){
        JournalRecordImpl curRec = (JournalRecordImpl)mCurrentLocal.get();
        if(curRec == null){
            return "";
        }
        if(mEditorFinder != null){
            finder = mEditorFinder;
        }
        Object journal = finder == null || finder == mEditorFinder ? curRec.toObject() : curRec.toObject(finder);
        return journal == null ? null : journal.toString();
   }
}
