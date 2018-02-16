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

package jp.ossc.nimbus.service.writer;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.queue.*;

/**
 * 分散カテゴリサービス。<p>
 * 出力先のI/O性能をカバーするために、出力先を分散するカテゴリ実装クラス。<br>
 *
 * @author M.Takata
 */
public class DistributedCategoryService extends ServiceBase
 implements DistributedCategoryServiceMBean{
    
    private static final long serialVersionUID = 1605519537623731512L;
    
    /**
     * このカテゴリが有効かどうかのフラグ。<p>
     * 有効な場合、true
     */
    protected boolean isEnabled = true;
    
    /**
     * 分散するカテゴリのサービス名配列。<p>
     */
    protected ServiceName[] categoryServiceNames;
    
    /**
     * 分散するカテゴリのサービス配列。<p>
     */
    protected Category[] categories;
    
    /**
     * 分散前に整列させるQueueサービスのサービス名。<p>
     */
    protected ServiceName queueServiceName;
    
    /**
     * 分散前に整列させるQueueサービス。<p>
     */
    protected Queue queue;
    
    /**
     * 分散前に整列させるデフォルトのQueueサービス。<p>
     */
    protected DefaultQueueService defaultQueue;
    
    /**
     * 分散前に整列させるQueueを選択する分散Queueセレクタサービスのサービス名。<p>
     */
    protected ServiceName distributedQueueSelectorServiceName;
    
    /**
     * 分散前に整列させるQueueを選択する分散Queueセレクタ。<p>
     */
    protected DistributedQueueSelector queueSelector;
    
    /**
     * 分散前に整列させるQueueサービスファクトリサービスのサービス名。<p>
     */
    protected ServiceName queueFactoryServiceName;
    
    /**
     * 分散処理を行うデーモンスレッド配列。<p>
     */
    protected Daemon[] daemons;
    
    /**
     * 分散処理スレッドをデーモン化するかどうかのフラグ。<p>
     */
    protected boolean isDaemon = true;
    
    /**
     * 分散処理スレッドのスレッド優先順位。<p>
     */
    protected int threadPriority = -1;
    
    /**
     * 分散処理スレッドで、分散したカテゴリに出力しようとした時に、例外が発生した場合のログメッセージID。<p>
     */
    protected String writeErrorLogMessageId;
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setCategoryServiceNames(ServiceName[] names){
        categoryServiceNames = names;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public ServiceName[] getCategoryServiceNames(){
        return categoryServiceNames;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setQueueFactoryServiceName(ServiceName name){
        queueFactoryServiceName = name;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public ServiceName getQueueFactoryServiceName(){
        return queueFactoryServiceName;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setDistributedQueueSelectorServiceName(ServiceName name){
        distributedQueueSelectorServiceName = name;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public ServiceName getDistributedQueueSelectorServiceName(){
        return distributedQueueSelectorServiceName;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setThreadPriority(int newPriority){
        threadPriority = newPriority;
    }
    // DistributedCategoryServiceMBeanのJavaDoc
    public int getThreadPriority(){
        return threadPriority;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setDaemon(boolean isDaemon){
        this.isDaemon = isDaemon;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public boolean isDaemon(){
        return isDaemon;
    }
    
    // DistributedCategoryServiceMBeanのJavaDoc
    public void setWriteErrorLogMessageId(String id){
        writeErrorLogMessageId = id;
    }
    // DistributedCategoryServiceMBeanのJavaDoc
    public String getWriteErrorLogMessageId(){
        return writeErrorLogMessageId;
    }
    
    /**
     * 分散するカテゴリを設定する。<p>
     *
     * @param categories 分散するカテゴリの配列
     */
    public void setCategories(Category[] categories) {
        this.categories = categories;
    }
    
    /**
     * 分散前に整列させるQueueを設定する。<p>
     *
     * @param container Queue
     */
    public void setQueue(Queue container){
        queue = container;
    }
    
    /**
     * 分散前に整列させるQueueを取得する。<p>
     *
     * @return Queue
     */
    public Queue getQueue(){
        return queue;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(categoryServiceNames != null){
            categories = new Category[categoryServiceNames.length];
            for(int i = 0, max = categoryServiceNames.length; i < max; i++){
                categories[i] = (Category)ServiceManagerFactory
                    .getServiceObject(categoryServiceNames[i]);
            }
        }
        if(categories == null || categories.length == 0){
            throw new IllegalArgumentException("Categories is null.");
        }
        
        Queue[] queues = null;
        if(distributedQueueSelectorServiceName == null){
            
            if(queueServiceName != null){
                queue = (Queue)ServiceManagerFactory
                        .getServiceObject(queueServiceName);
            }else if(queueFactoryServiceName != null){
                queue = (Queue)ServiceManagerFactory
                        .getServiceObject(queueFactoryServiceName);
            }
            if(queue == null){
                defaultQueue = new DefaultQueueService();
                defaultQueue.create();
                defaultQueue.start();
                queue = defaultQueue;
            }
            queue.accept();
        }else{
            queueSelector = (DistributedQueueSelector)ServiceManagerFactory
                    .getServiceObject(distributedQueueSelectorServiceName);
            queues = queueSelector.getQueues();
            if(queues == null || categories.length != queues.length){
                throw new IllegalArgumentException("Categry and Queues is not match.");
            }
        }
        
        // デーモン起動
        final CategoryWriter[] writers = new CategoryWriter[categories.length];
        daemons = new Daemon[categories.length];
        for(int i = 0; i < categories.length; i++){
            writers[i] = new CategoryWriter();
            writers[i].category = categories[i];
            writers[i].queue = queues == null ? queue : queues[i];
            daemons[i] = new Daemon(writers[i]);
            daemons[i].setDaemon(isDaemon);
            daemons[i].setName(getServiceNameObject() + " CategoryWriter" + (i + 1));
            if(threadPriority > 0){
                daemons[i].setPriority(threadPriority);
            }
            daemons[i].start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        // デーモン停止
        for(int i = 0; i < daemons.length; i++){
            daemons[i].stop();
            daemons[i] = null;
        }
        
        // キュー受付停止
        if(queue != null){
            queue.release();
        }
        if(queueSelector != null){
            Queue[] queues = queueSelector.getQueues();
            if(queues != null){
                for(int i = 0; i < queues.length; i++){
                    queues[i].release();
                }
            }
        }
        
        daemons = null;
    }
    
    // CategoryのJavaDoc
    public boolean isEnabled(){
        return isEnabled;
    }
    
    // CategoryのJavaDoc
    public void setEnabled(boolean enable){
        isEnabled = enable;
    }
    
    // CategoryのJavaDoc
    public void write(Object elements) throws MessageWriteException{
        if(!isEnabled()){
            return;
        }
        Queue queue = null;
        if(queueSelector == null){
            queue = this.queue;
        }else{
            queue = queueSelector.selectQueue(elements);
        }
        if(queue != null){
            queue.push(elements);
        }
    }
    
    protected class CategoryWriter implements DaemonRunnable{
        
        protected Queue queue;
        
        protected Category category;
        
        /**
         * デーモンが開始した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onStart() {
            return true;
        }
        
        /**
         * デーモンが停止した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onStop() {
            return true;
        }
        
        /**
         * デーモンが中断した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onSuspend() {
            return true;
        }
        
        /**
         * デーモンが再開した時に呼び出される。<p>
         * 
         * @return 常にtrueを返す
         */
        public boolean onResume() {
            return true;
        }
        
        /**
         * キューから１つ取り出して返す。<p>
         * 
         * @param ctrl DaemonControlオブジェクト
         * @return 入力オブジェクト
         */
        public Object provide(DaemonControl ctrl){
            return queue.get(1000);
        }
        
        /**
         * 引数dequeuedで渡されたオブジェクトを引数にQueueHandlerを呼び出す。<p>
         *
         * @param dequeued キューから取り出されたオブジェクト
         * @param ctrl DaemonControlオブジェクト
         */
        public void consume(Object dequeued, DaemonControl ctrl){
            if(dequeued == null){
                return;
            }
            try{
                category.write(dequeued);
            }catch(MessageWriteException e){
                if(writeErrorLogMessageId != null){
                    getLogger().write(writeErrorLogMessageId, e);
                }
            }
        }
        
        /**
         * キューの中身を吐き出す。<p>
         */
        public void garbage(){
            if(queue != null){
                while(queue != null && queue.size() > 0){
                    consume(queue.get(0), null);
                }
            }
        }
    }
}
