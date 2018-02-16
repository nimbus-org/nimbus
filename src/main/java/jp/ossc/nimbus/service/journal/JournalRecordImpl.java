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

import java.io.*;

import jp.ossc.nimbus.service.journal.editorfinder.*;
import jp.ossc.nimbus.core.*;

/**
 * {@link JournalRecord}実装クラス。<p>
 * 
 * @author H.Nakano
 */
public class JournalRecordImpl implements JournalRecord, java.io.Serializable{
    
    private static final long serialVersionUID = 4377038814532613910L;
    
    /** キー */
    private String mKey ;
    /** エディタータイプ */
    private transient EditorFinder mFinder ;
    private ServiceName mFinderName ;
    private Object mEditObj ;
    /** 登録オブジェクト*/
    private transient Object mParamObj ;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public JournalRecordImpl(){
        super();
        this.mKey = null;
        this.mFinder = null;
        this.mEditObj = null;
        this.mParamObj = null;
    }
    
    // JournalRecord のJavaDoc
    public String getKey(){
        return mKey;
    }
    
    /**
     * ジャーナル情報のキーを設定する。<p>
     * 
     * @param key ジャーナル情報のキー
     * @see #getKey()
     */
    public void setKey(String key){
        mKey = key;
    }
    
    /**
     * ジャーナル情報を編集する{@link JournalEditor}を設定する。<p>
     * 
     * @param finder ジャーナル情報を編集するJournalEditor
     * @see #getFinder()
     */
    public void setEditorFinder(EditorFinder finder){
        mFinder = finder ;
        if(mFinder instanceof ServiceBase){
            mFinderName = ((ServiceBase)mFinder).getServiceNameObject();
        }else if(mFinder instanceof Service){
            final Service service = (Service)mFinder;
            mFinderName = new ServiceName(
                service.getServiceManagerName(),
                service.getServiceName()
            );
        }
    }
    
    /**
     * {@link RequestJournal}を設定する。<p>
     *
     * @param obj RequestJournal
     */
    public void setParamObj(Object obj){
        mParamObj = obj ;
    }
    
    /**
     * ジャーナル情報を設定する。<p>
     *
     * @param obj ジャーナル情報
     */
    public void setInfoObj(Object obj){
        mParamObj = obj;
        JournalEditor editor = getJournalEditor();
        this.mEditObj = editor.toObject(mFinder,mKey,obj) ;
    }
    
    // JournalRecord のJavaDoc
    public Object toObject(){
        if(this.mEditObj == null){
            JournalEditor editor = getJournalEditor() ;
            mEditObj = editor.toObject(mFinder,mKey,mParamObj) ;
        }
        return mEditObj ;
    }
    
    // JournalRecord のJavaDoc
    public Object toObject( EditorFinder finder ){
        Object ret = null;
        if(finder != null && mEditObj == null){
            final JournalEditor editor = finder.findEditor(mKey, mParamObj);
            ret = editor.toObject(finder,mKey,mParamObj) ;
        }
        return ret;
    }
    
    /**
     * ジャーナル情報を取得する。<p>
     *
     * @return ジャーナル情報
     */
    public Object getObject(){
        return mParamObj ;
    }
    
    /**
     * ジャーナル情報を編集する{@link JournalEditor}を検索する{@link EditorFinder}を取得する。<p>
     * 
     * @return ジャーナル情報を編集するJournalEditorを検索するEditorFinder
     */
    public EditorFinder getFinder(){
        return mFinder ;
    }
    
    // JournalRecord のJavaDoc
    public JournalEditor getJournalEditor(){
        
        return mFinder == null ? null : mFinder.findEditor(mKey, mParamObj) ;
    }
    
    // JournalRecord のJavaDoc
    public boolean isStep(){
        return (mParamObj != null && mParamObj instanceof RequestJournal);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        if(mFinderName == null && mFinder != null){
            out.writeObject(mFinder);
        }
        if(mParamObj != null && mParamObj instanceof RequestJournal){
            out.writeObject(mParamObj);
        }else{
            out.writeObject(null);
        }
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        if(mFinderName == null){
            mFinder = (EditorFinder)in.readObject();
        }else{
            try{
                mFinder = (EditorFinder)ServiceManagerFactory
                    .getServiceObject(mFinderName);
            }catch(ServiceNotFoundException e){
            }
        }
        mParamObj = in.readObject();
    }
}
