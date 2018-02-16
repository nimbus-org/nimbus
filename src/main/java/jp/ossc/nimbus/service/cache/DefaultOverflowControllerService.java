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
package jp.ossc.nimbus.service.cache;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.daemon.*;
import jp.ossc.nimbus.service.queue.*;

/**
 * デフォルトあふれ制御。<p>
 * {@link OverflowValidator}、{@link OverflowAlgorithm}、{@link OverflowAction}の３つを一組にして、あふれ制御を行うOverflowControllerである。<br>
 * また、あふれ制御は、キャッシュの追加処理と同期させる必要はないため、別スレッドであふれ制御を行う事ができるように{@link Queue}サービスを設定できる。<br>
 * 以下に、キャッシュサイズが10を超えると、FIFOであふれ対象となるキャッシュオブジェクトを決定し、キャッシュから削除するあふれ制御サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="OverflowController"
 *                  code="jp.ossc.nimbus.service.cache.DefaultOverflowControllerService"&gt;
 *             &lt;attribute name="OverflowValidatorServiceName"&gt;#CacheSizeOverflowValidator&lt;/attribute&gt;
 *             &lt;attribute name="OverflowAlgorithmServiceName"&gt;#FIFOOverflowAlgorithm&lt;/attribute&gt;
 *             &lt;depends&gt;CacheSizeOverflowValidator&lt;/depends&gt;
 *             &lt;depends&gt;FIFOOverflowAlgorithm&lt;/depends&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="CacheSizeOverflowValidator"
 *                  code="jp.ossc.nimbus.service.cache.CacheSizeOverflowValidatorService"&gt;
 *             &lt;attribute name="MaxSize"&gt;10&lt;/attribute&gt;
 *         &lt;/service&gt;
 *         
 *         &lt;service name="FIFOOverflowAlgorithm"
 *                  code="jp.ossc.nimbus.service.cache.FIFOOverflowAlgorithmService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 *
 * @author M.Takata
 * @see OverflowValidator
 * @see OverflowAlgorithm
 * @see OverflowAction
 * @see Queue
 */
public class DefaultOverflowControllerService extends ServiceBase
 implements OverflowController, DaemonRunnable,
            DefaultOverflowControllerServiceMBean{
    
    private static final long serialVersionUID = 304577650295674609L;
    
    /**
     * あふれ検証サービスのサービス名。<p>
     */
    protected ServiceName validatorServiceName;
    
    /**
     * あふれ検証サービス。<p>
     */
    protected OverflowValidator validator;
    
    /**
     * あふれアルゴリズムサービスのサービス名。<p>
     */
    protected ServiceName algorithmServiceName;
    
    /**
     * あふれアルゴリズムサービス。<p>
     */
    protected OverflowAlgorithm algorithm;
    
    /**
     * あふれ動作サービスのサービス名。<p>
     */
    protected ServiceName actionServiceName;
    
    /**
     * あふれ動作サービス。<p>
     */
    protected OverflowAction action;
    
    /**
     * デフォルトのあふれ動作サービス。<p>
     * デフォルトのあふれ動作は、あふれたキャッシュオブジェクトを削除する。<br>
     */
    protected RemoveOverflowActionService defaultAction;
    
    /**
     * あふれ制御の要求を別スレッドで処理するために一旦キューに溜めるためのキューサービスのサービス名。<p>
     */
    protected ServiceName queueServiceName;
    
    /**
     * あふれ制御の要求を別スレッドで処理するために一旦キューに溜めるためのキューサービス。<p>
     */
    protected Queue queue;
    
    /**
     * あふれ制御の要求を別スレッドで処理するためのデーモンオブジェクト。<p>
     */
    protected Daemon daemon;
    
    /**
     * あふれ制御処理中のキャッシュ参照に対する同期制御用のロックオブジェクト。<p>
     */
    protected Object lock = "lock";
    
    /**
     * 定期的にあふれ制御を行う時間間隔[ms]の属性値。<p>
     */
    protected long periodicOverflowIntervalTime;
    
    /**
     * 定期的にあふれ制御を行う時間間隔[ms]。<p>
     */
    protected long periodicOverflowInterval;
    
    /**
     * キャッシュ参照が追加されるたびにあふれ制御を行うかどうかのフラグ。<p>
     * デフォルトは、trueで、追加のたびにあふれ制御を行う。<br>
     */
    protected boolean isOverflowByAdding = true;
    
    /**
     * あふれ制御を行うたびにあふれ検証を行うかどうかのフラグ。<p>
     * デフォルトは、trueで、あふれ制御を行うたびにあふれ検証を行う。<br>
     */
    protected boolean isValidateByOverflow = true;
    
    /**
     * 新規追加されるキャッシュ参照をあふれ対象に加えるかどうかのフラグ。<p>
     * デフォルトは、falseで、新規追加されるキャッシュ参照はあふれ対象に加えない。<br>
     */
    protected boolean isOverflowNewAdding = false;
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setOverflowValidatorServiceName(ServiceName name){
        validatorServiceName = name;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public ServiceName getOverflowValidatorServiceName(){
        return validatorServiceName;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setOverflowAlgorithmServiceName(ServiceName name){
        algorithmServiceName = name;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public ServiceName getOverflowAlgorithmServiceName(){
        return algorithmServiceName;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setOverflowActionServiceName(ServiceName name){
        actionServiceName = name;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public ServiceName getOverflowActionServiceName(){
        return actionServiceName;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setQueueServiceName(ServiceName name){
        queueServiceName = name;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public ServiceName getQueueServiceName(){
        return queueServiceName;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setPeriodicOverflowIntervalTime(long time){
        periodicOverflowIntervalTime = time;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public long getPeriodicOverflowIntervalTime(){
        return periodicOverflowIntervalTime;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setOverflowByAdding(boolean isOverflow){
        isOverflowByAdding = isOverflow;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public boolean isOverflowByAdding(){
        return isOverflowByAdding;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setValidateByOverflow(boolean isValidate){
        isValidateByOverflow = isValidate;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public boolean isValidateByOverflow(){
        return isValidateByOverflow;
    }
    
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public void setOverflowNewAdding(boolean isOverflow){
        isOverflowNewAdding = isOverflow;
    }
    // DefaultOverflowControllerServiceMBeanのJavaDoc
    public boolean isOverflowNewAdding(){
        return isOverflowNewAdding;
    }
    
    /**
     * OverflowActionを設定する。
     */
    public void setOverflowAction(OverflowAction action) {
        this.action = action;
    }
    /**
     * OverflowAlgorithmを設定する。
     */
    public void setOverflowAlgorithm(OverflowAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    /**
     * Queueを設定する。
     */
    public void setQueue(Queue queue) {
        this.queue = queue;
    }
    /**
     * OverflowValidatorを設定する。
     */
    public void setOverflowValidator(OverflowValidator validator) {
        this.validator = validator;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * デーモンを生成する。<br>
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        daemon = new Daemon(this);
        daemon.setName("Nimbus OverflowControllerDaemon " + getServiceNameObject());
    }
    
    /**
     * サービスの開始処理を行う。<p>
     * あふれ検証サービスを取得する。<br>
     * あふれアルゴリズムサービスを取得する。<br>
     * あふれ動作サービスを取得する。<br>
     * あふれ動作サービスに、{@link OverflowAction#setOverflowController(OverflowController)}で自分自身を設定する。<br>
     * キューサービスを取得する。<br>
     * デーモンを開始する。<br>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(validatorServiceName != null){
            validator = (OverflowValidator)ServiceManagerFactory
                .getServiceObject(validatorServiceName);
        }
        if(algorithmServiceName != null){
            algorithm = (OverflowAlgorithm)ServiceManagerFactory
                .getServiceObject(algorithmServiceName);
        }
        if(validator != null && algorithm == null){
            throw new IllegalArgumentException(
                "OverflowAlgorithm must specify when OverflowValidator is specified."
            );
        }
        if(actionServiceName != null){
            action = (OverflowAction)ServiceManagerFactory
                .getServiceObject(actionServiceName);
        }else{
            action = getDefaultOverflowActionService();
        }
        action.setOverflowController(this);
        
        if(queueServiceName != null){
            queue = (Queue)ServiceManagerFactory
                .getServiceObject(queueServiceName);
        }
        if(periodicOverflowIntervalTime > 0){
            periodicOverflowInterval = periodicOverflowIntervalTime;
        }
        if(queue != null || periodicOverflowInterval > 0){
            
            if(queue != null){
                // キュー受付開始
                queue.accept();
            }
            
            // デーモン起動
            daemon.start();
        }
    }
    
    /**
     * サービスの停止処理を行う。<p>
     * デーモンを停止する。<br>
     * あふれ検証サービスの参照を破棄する。<br>
     * あふれアルゴリズムサービスの参照を破棄する。<br>
     * あふれ動作サービスの参照を破棄する。<br>
     * キューサービスの参照を破棄する。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        
        if(queue != null || periodicOverflowInterval > 0){
            
            // デーモン停止
            daemon.stop();
            
            if(queue != null){
                // キュー受付停止
                queue.release();
            }
        }
        
        validator = null;
        algorithm = null;
        if(defaultAction != null && action == defaultAction){
            defaultAction.stop();
        }
        action = null;
        queue = null;
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * デフォルトのあふれ動作サービスを破棄する。<br>
     * デフォルトのキューサービスを破棄する。<br>
     * デーモンを破棄する。<br>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void destroyService() throws Exception{
        if(defaultAction != null){
            defaultAction.destroy();
            defaultAction = null;
        }
        daemon = null;
    }
    
    /**
     * デフォルトのあふれ動作サービスを取得する。<p>
     *
     * @return {@link RemoveOverflowActionService}
     * @exception Exception デフォルトのあふれ動作サービスの生成・起動に失敗した場合
     */
    protected OverflowAction getDefaultOverflowActionService() throws Exception{
        if(defaultAction == null){
            final RemoveOverflowActionService act
                 = new RemoveOverflowActionService();
            act.create();
            act.start();
            defaultAction = act;
        }else if(defaultAction.getState() != STARTED){
            defaultAction.start();
        }
        return defaultAction;
    }
    
    /**
     * あふれ制御を行う。<p>
     * あふれ制御は、別スレッドで行うため、ここでは、処理を行わずにすぐに処理を戻す。<br>
     * 別スレッドで行うあふれ制御は、{@link #consume(Object, DaemonControl)}を参照。<br>
     *
     * @param ref キャッシュに追加されたキャッシュ参照
     */
    public void control(CachedReference ref){
        if(getState() != STARTED){
            return;
        }
        if(queue == null){
            consume(ref, daemon);
        }else{
            queue.push(ref);
        }
    }
    
    /**
     * あふれ制御を行うために保持している情報を初期化する。<p>
     * {@link OverflowValidator#reset()}、{@link OverflowAlgorithm#reset()}、{@link OverflowAction#reset()}を呼び出す。<br>
     */
    public void reset(){
        if(validator != null){
            validator.reset();
        }
        if(algorithm != null){
            algorithm.reset();
        }
        if(action != null){
            action.reset();
        }
    }
    
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
     * @return {@link CachedReference}オブジェクト
     */
    public Object provide(DaemonControl ctrl){
        if(getState() != STARTED){
            return null;
        }
        CachedReference ref = null;
        if(queue == null){
            try{
                ctrl.sleep(periodicOverflowInterval, true);
            }catch(InterruptedException e){
            }
        }else{
            if(periodicOverflowInterval > 0){
                ref = (CachedReference)queue
                    .get(periodicOverflowInterval);
            }else{
                ref = (CachedReference)queue.get();
            }
        }
        return ref;
    }
    
    /**
     * 引数dequeuedで渡されたオブジェクトを消費する。<p>
     * 引数dequeuedで渡されたオブジェクトを{@link CachedReference}にキャストして{@link OverflowValidator}、{@link OverflowAlgorithm}、{@link OverflowAction}を呼び出す。<br>
     * あふれ制御は、{@link #isValidateByOverflow()}がfalseの場合は、以下の順序で行われる。<br>
     * <ol>
     *     <li>１．{@link OverflowValidator#add(CachedReference)}を呼び出す。</li>
     *     <li>２．{@link #isOverflowNewAdding()}がtrueの場合、{@link OverflowAlgorithm#add(CachedReference)}を呼び出す。</li>
     *     <li>３．{@link #isOverflowByAdding()}がtrueの場合、{@link OverflowValidator#validate()}を呼び出し、その戻り値のが1以上の場合、１～３の処理を行う。</li>
     *     <li>３－１．{@link OverflowValidator#validate()}を呼び出し、あふれ数を決定する。</li>
     *     <li>３－２．{@link OverflowAlgorithm#overflow(int)}を呼び出し、あふれ対象のキャッシュ参照を決定する。</li>
     *     <li>３－３．あふれ対象のキャッシュ参照の数の分だけ、{@link OverflowAction#action(OverflowValidator, OverflowAlgorithm, CachedReference)}を繰り返し呼び出し、あふれ処理を行う。</li>
     *     <li>４．{@link #isOverflowNewAdding()}がfalseの場合、{@link OverflowAlgorithm#add(CachedReference)}を呼び出す。</li>
     * </ol>
     * あふれ制御は、{@link #isValidateByOverflow()}がtrueの場合は、以下の順序で行われる。<br>
     * <ol>
     *     <li>１．{@link OverflowValidator#add(CachedReference)}を呼び出す。</li>
     *     <li>２．{@link #isOverflowNewAdding()}がtrueの場合、{@link OverflowAlgorithm#add(CachedReference)}を呼び出す。</li>
     *     <li>３．{@link #isOverflowByAdding()}がtrueの場合、{@link OverflowValidator#validate()}を呼び出し、その戻り値のが1以上の場合、以下の１～３の処理を繰り返す。</li>
     *     <li>３－１．{@link OverflowAlgorithm#overflow()}を呼び出し、あふれ対象のキャッシュ参照を決定する。あふれ対象のキャッシュ参照がnullの場合は、break</li>
     *     <li>３－２．あふれ対象のキャッシュ参照を{@link OverflowAction#action(OverflowValidator, OverflowAlgorithm, CachedReference)}に渡して、あふれ処理を行う。</li>
     *     <li>３－３．{@link OverflowValidator#validate()}を呼び出し、あふれ数を再評価し、0以下となる場合は、break。</li>
     *     <li>４．{@link #isOverflowNewAdding()}がfalseの場合、{@link OverflowAlgorithm#add(CachedReference)}を呼び出す。</li>
     * </ol>
      *
     * @param dequeued キューから取り出されたオブジェクト
     * @param ctrl DaemonControlオブジェクト
     */
    public void consume(Object dequeued, DaemonControl ctrl){
        if(validator == null || getState() != STARTED){
            return;
        }
        CachedReference ref = (CachedReference)dequeued;
        if(ref != null && !ref.isRemoved() && validator != null){
            validator.add(ref);
        }
        if(isOverflowNewAdding && ref != null && !ref.isRemoved() && algorithm != null){
            algorithm.add(ref);
        }
        int overflowSize = 0;
        if(ref == null || isOverflowByAdding){
            overflowSize = validator.validate();
        }
        if(overflowSize > 0){
            if(!isValidateByOverflow){
                synchronized(lock){
                    overflowSize = validator.validate();
                    if(overflowSize > 0){
                        CachedReference[] overflowRefs = null;
                        if(algorithm != null){
                            overflowRefs = algorithm.overflow(overflowSize);
                        }
                        if(overflowRefs != null){
                            for(int i = 0; i < overflowRefs.length; i++){
                                CachedReference overflowRef = overflowRefs[i];
                                if(overflowRef == null){
                                    continue;
                                }else if(!overflowRef.isRemoved()){
                                    if(overflowRef != null && action != null && !overflowRef.isRemoved()){
                                        action.action(validator, algorithm, overflowRef);
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                CachedReference prevOverflowRef = null;
                while(overflowSize > 0){
                    CachedReference overflowRef = null;
                    if(algorithm != null){
                        overflowRef = algorithm.overflow();
                    }
                    if(prevOverflowRef != null && prevOverflowRef == overflowRef){
                        // あふれ動作が行えない場合の無限ループを回避する
                        break;
                    }
                    if(overflowRef == null){
                        break;
                    }else if(!overflowRef.isRemoved()){
                        if(overflowRef != null && action != null && !overflowRef.isRemoved()){
                            action.action(validator, algorithm, overflowRef);
                        }
                    }
                    if(validator != null){
                        overflowSize = validator.validate();
                    }else{
                        overflowSize = 0;
                    }
                    prevOverflowRef = overflowRef;
                }
            }
        }
        if(!isOverflowNewAdding && ref != null && !ref.isRemoved() && algorithm != null){
            algorithm.add(ref);
        }
    }
    
    /**
     * キューの中身を吐き出す。<p>
     */
    public void garbage(){
        if(queue != null){
            while(queue.size() > 0){
                consume(queue.get(0), daemon);
            }
        }
    }
}
