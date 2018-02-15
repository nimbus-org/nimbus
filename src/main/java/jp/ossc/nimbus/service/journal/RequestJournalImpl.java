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
import jp.ossc.nimbus.service.journal.editorfinder.*;

/**
 * {@link RequestJournal}実装クラス。<p>
 * 
 * @author   H.Nakano
 */
public class RequestJournalImpl implements RequestJournal, java.io.Serializable{
    
    private static final long serialVersionUID = 687167860749686268L;
    
    /**
     * ジャーナルレコードのリスト。<p>
     */
    protected List mRequestAry;
    
    /**
     * ステップ開始時刻。<p>
     */
    protected Date mStartTime;
    
    /**
     * ステップ終了時刻。<p>
     */
    protected Date mEndTime;
    
    /**
     * ステップのキー。<p>
     */
    protected String mKey;
    
    /**
     * リクエストID。<p>
     */
    protected String mRequestId;
    
    /**
     * 親ステップのJournalRecord。<p>
     */
    protected JournalRecordImpl mStepRoot;
    
    /**
     * 現在のステップのJournalRecord。<p>
     */
    protected JournalRecordImpl mCurrentRoot;
    
    /**
     * ステップかどうかのフラグ。<p>
     * trueの場合ステップ。<br>
     */
    protected boolean mIsStep = false ;
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param isStep ステップの場合true
     */
    public RequestJournalImpl(boolean isStep) {
        super();
        mRequestAry = new ArrayList();
        mStepRoot = null;
        this.mCurrentRoot = null;
        mIsStep = isStep;
    }
    
    /**
     * ステップかどうかを判定する。<p>
     *
     * @return ステップの場合true
     */
    public boolean isStep(){
        return this.mIsStep;
    }
    
    /**
     * ステップのキーを設定する。<p>
     *
     * @param key ステップのキー
     */
    public void setKey(String key){
        mKey=key;
    }
    
    /**
     * リクエストIDを設定する。<p>
     *
     * @param id リクエストID
     */
    public void setRequestId(String id){
        mRequestId=id;
    }
    
    /**
     * ステップの開始時刻を設定する。<p>
     *
     * @param dt ステップの開始時刻
     */
    public void setStartTime(Date dt){
        mStartTime = dt;
    }
    
    /**
     * 現在時刻をステップの開始時刻に設定する。<p>
     */
    public void setStartTime(){
        mStartTime = new Date();
    }
    
    /**
     * 現在時刻をステップの終了時刻に設定する。<p>
     */
    public void setEndTime(){
        mEndTime = new Date();
    }
    
    /**
     * ステップの終了時刻を設定する。<p>
     *
     * @param time ステップの終了時刻
     */
    public void setEndTime(Date time){
        mEndTime = time;
    }
    
    /**
     * ステップの{@link RequestJournal}を設定する。<p>
     * 
     * @param key キー
     * @param finder RequestJournalを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     * @param obj RequestJournalオブジェクト
     * @return RequestJournalを格納した{@link JournalRecord}
     */
    public JournalRecord setParamObj(
        String key,
        EditorFinder finder,
        Object obj
    ){
        JournalRecordImpl rec = new JournalRecordImpl();
        rec.setEditorFinder(finder);
        rec.setKey(key);
        rec.setParamObj(obj);
        this.mRequestAry.add(rec);
        return rec;
    }
    
    /**
     * ステップのジャーナル情報を設定する。<p>
     * 
     * @param key キー
     * @param finder ジャーナル情報を編集する{@link JournalEditor}を検索する{@link EditorFinder}
     * @param obj ジャーナル情報
     * @return ジャーナル情報を格納した{@link JournalRecord}
     */
    public JournalRecord setInfoObj(
        String key,
        EditorFinder finder,
        Object obj
    ){
        JournalRecordImpl rec = new JournalRecordImpl();
        rec.setEditorFinder(finder);
        rec.setKey(key);
        rec.setInfoObj(obj);
        this.mRequestAry.add(rec);
        return rec;
    }
    
    // RequestJournal のJavaDoc
    public String getKey(){
        return this.mKey;
    }
    
    // RequestJournal のJavaDoc
    public String getRequestId(){
        return this.mRequestId;
    }
    
    // RequestJournal のJavaDoc
    public Date getStartTime(){
        return this.mStartTime;
    }
    
    // RequestJournal のJavaDoc
    public Date getEndTime(){
        if(mEndTime == null){
            mEndTime = new Date();
        }
        return this.mEndTime;
    }
    
    // RequestJournal のJavaDoc
    public long getPerformance(){
        if(mEndTime == null){
            mEndTime = new Date();
        }
        return this.mEndTime.getTime() - this.mStartTime.getTime();
    }
    
    // RequestJournal のJavaDoc
    public JournalRecord[] getParamAry(){
        JournalRecord[] ret = new JournalRecord[this.mRequestAry.size()];
        for(int cnt = 0 ;cnt < ret.length; cnt++){
            ret[cnt] = (JournalRecord)this.mRequestAry.get(cnt);
        }
        return ret;
    }
    
    /**
     * 親ステップと現在のステップの{@link JournalRecord}を設定する。<p>
     *
     * @param stepRoot 親ステップ
     * @param curRoot 現在ステップ
     */
    public void setRoot(
        JournalRecordImpl stepRoot,
        JournalRecordImpl curRoot
    ){
        this.mStepRoot = stepRoot;
        this.mCurrentRoot =curRoot;
    }
    
    /**
     * 親ステップの{@link JournalRecord}を取得する。<p>
     *
     * @return 親ステップのJournalRecord
     */
    public JournalRecordImpl getStepRoot(){
        return this.mStepRoot;
    }
    
    /**
     * 現在ステップの{@link JournalRecord}を取得する。<p>
     *
     * @return 現在ステップのJournalRecord
     */
    public JournalRecordImpl getCurRoot(){
        return this.mCurrentRoot;
    }
    
    // RequestJournal のJavaDoc
    public boolean isRoot(){
        return getCurRoot() == null;
    }
    
    // RequestJournal のJavaDoc
    public JournalRecord[] findParamArys(String key){
        List retAry = new ArrayList();
        for(Iterator iterator = this.mRequestAry.iterator(); iterator.hasNext(); ){
            JournalRecord tmp =(JournalRecord)iterator.next();
            if(tmp.getKey().equals(key)){
                retAry.add(tmp);
            }
        }        
        JournalRecord[] ret = new JournalRecord[retAry.size()];
        for(int cnt = 0; cnt < ret.length; cnt++){
            ret[cnt] = (JournalRecord)retAry.get(cnt);
        }
        return ret;
    }
    
    public void clearParam(int from){
        for(int i = from, imax = this.mRequestAry.size(); i < imax; i++){
            this.mRequestAry.remove(from);
        }
    }
}