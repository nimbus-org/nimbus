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

/**
 * 簡易カテゴリサービス。<p>
 * 出力先を分類するカテゴリサービスの簡易実装クラス。<br>
 * 指定された出力要素を、設定された{@link WritableRecordFactory}で{@link WritableRecord}に変換して、設定された{@link MessageWriter}に出力を依頼する。<br>
 *
 * @author M.Takata
 */
public class SimpleCategoryService extends ServiceBase
 implements SimpleCategoryServiceMBean{
    
    private static final long serialVersionUID = 1601430582489560068L;
    
    /**
     * このカテゴリが有効かどうかのフラグ。<p>
     * 有効な場合、true
     */
    protected boolean isEnabled = true;
    
    /**
     * このカテゴリの出力先となるMessageWriterのサービス名。<p>
     */
    protected ServiceName writerName;
    
    /**
     * このカテゴリの出力先となるMessageWriterオブジェクト。<p>
     */
    protected MessageWriter writer;
    
    /**
     * このカテゴリの出力フォーマットを決めるWritableRecordFactoryのサービス名。<p>
     */
    protected ServiceName recordFactoryName;
    
    /**
     * このカテゴリの出力フォーマットを決めるWritableRecordFactoryオブジェクト。<p>
     */
    protected WritableRecordFactory recordFactory;
    
    // SimpleCategoryServiceMBeanのJavaDoc
    public void setMessageWriterServiceName(ServiceName name){
        writerName = name;
    }
    
    // SimpleCategoryServiceMBeanのJavaDoc
    public ServiceName getMessageWriterServiceName(){
        return writerName;
    }
    
    // SimpleCategoryServiceMBeanのJavaDoc
    public void setWritableRecordFactoryServiceName(ServiceName name){
        recordFactoryName = name;
    }
    
    // SimpleCategoryServiceMBeanのJavaDoc
    public ServiceName getWritableRecordFactoryServiceName(){
        return recordFactoryName;
    }
    
    /**
     * このカテゴリの出力を行うMessageWriterサービスを設定する。<p>
     *
     * @param writer このカテゴリの出力を行うMessageWriterサービス
     */
    public void setMessageWriterService(MessageWriter writer){
        this.writer = writer;
    }
    
    /**
     * このカテゴリの出力を行うMessageWriterサービスを取得する。<p>
     *
     * @return このカテゴリの出力を行うMessageWriterサービス
     */
    public MessageWriter getMessageWriterService(){
        return this.writer;
    }
    
    /**
     * このカテゴリの出力フォーマットを決めるWritableRecordFactoryサービスを設定する。<p>
     *
     * @param factory このカテゴリの出力フォーマットを決めるWritableRecordFactoryサービス
     */
    public void setWritableRecordFactoryService(WritableRecordFactory factory){
        recordFactory = factory;
    }
    
    /**
     * このカテゴリの出力フォーマットを決めるWritableRecordFactoryサービスを取得する。<p>
     *
     * @return このカテゴリの出力フォーマットを決めるWritableRecordFactoryサービス
     */
    public WritableRecordFactory getWritableRecordFactoryService(){
        return recordFactory;
    }
    
    /**
     * サービスの開始処理を行う。<p>
     *
     * @exception Exception サービスの開始処理に失敗した場合
     */
    public void startService() throws Exception{
        if(writerName != null){
            writer = (MessageWriter)ServiceManagerFactory
                .getServiceObject(writerName);
        }
        if(recordFactoryName != null){
            recordFactory = (WritableRecordFactory)ServiceManagerFactory
                .getServiceObject(recordFactoryName);
        }
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
        writer.write(recordFactory.createRecord(elements));
    }
}
