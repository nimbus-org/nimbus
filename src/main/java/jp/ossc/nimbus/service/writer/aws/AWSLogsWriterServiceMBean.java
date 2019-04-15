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
package jp.ossc.nimbus.service.writer.aws;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link AWSLogsWriterService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see AWSLogsWriterService
 */
public interface AWSLogsWriterServiceMBean
 extends ServiceBaseMBean, jp.ossc.nimbus.service.writer.MessageWriter{
    
    /**
     * com.amazonaws.services.logs.AWSLogsClientBuilderサービスのサービス名を設定する。<p>
     *
     * @param name AWSLogsClientBuilderサービスのサービス名
     */
    public void setAwsClientBuilderServiceName(ServiceName name);
    
    /**
     * com.amazonaws.services.logs.AWSLogsClientBuilderサービスのサービス名を取得する。<p>
     *
     * @return AWSLogsClientBuilderサービスのサービス名
     */
    public ServiceName getAwsClientBuilderServiceName();
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの実行タイムアウト[ms]を設定する。<p>
     *
     * @param timeout com.amazonaws.AmazonWebServiceRequestの実行タイムアウト[ms]
     */
    public void setSdkClientExecutionTimeout(int timeout);
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの実行タイムアウト[ms]を取得する。<p>
     *
     * @return com.amazonaws.AmazonWebServiceRequestの実行タイムアウト[ms]
     */
    public int getSdkClientExecutionTimeout();
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの要求タイムアウト[ms]を設定する。<p>
     *
     * @param timeout com.amazonaws.AmazonWebServiceRequestの要求タイムアウト[ms]
     */
    public void setSdkRequestTimeout(int timeout);
    
    /**
     * com.amazonaws.AmazonWebServiceRequestの要求タイムアウト[ms]を取得する。<p>
     *
     * @return com.amazonaws.AmazonWebServiceRequestの要求タイムアウト[ms]
     */
    public int getSdkRequestTimeout();
    
    /**
     * サービスの開始時に、ロググループを作成するかどうかを設定する。<p>
     * デフォルトは、falseで作成しない。<br>
     *
     * @param isCreate 作成する場合は、true
     */
    public void setCreateLogGroupOnStart(boolean isCreate);
    
    /**
     * サービスの開始時に、ロググループを作成するかどうかを判定する。<p>
     *
     * @return trueの場合、作成する
     */
    public boolean isCreateLogGroupOnStart();
    
    /**
     * サービスの開始時に、ログストリームを作成するかどうかを設定する。<p>
     * デフォルトは、falseで作成しない。<br>
     *
     * @param isCreate 作成する場合は、true
     */
    public void setCreateLogStreamOnStart(boolean isCreate);
    
    /**
     * サービスの開始時に、ログストリームを作成するかどうかを判定する。<p>
     *
     * @return trueの場合、作成する
     */
    public boolean isCreateLogStreamOnStart();
    
    /**
     * ロググループの名前を設定する。<p>
     *
     * @param name ロググループの名前
     */
    public void setLogGroupName(String name);
    
    /**
     * ロググループの名前を取得する。<p>
     *
     * @return ロググループの名前
     */
    public String getLogGroupName();
    
    /**
     * AWS Key Management ServiceのIDを設定する。<p>
     *
     * @param id ID
     */
    public void setKMSKeyId(String id);
    
    /**
     * AWS Key Management ServiceのIDを取得する。<p>
     *
     * @return ID
     */
    public String getKMSKeyId();
    
    /**
     * ロググループのタグを設定する。<p>
     *
     * @param tags タグのキーと値のマップ
     */
    public void setTags(Properties tags);
    
    /**
     * ロググループのタグを取得する。<p>
     *
     * @return タグのキーと値のマップ
     */
    public Properties getTags();
    
    /**
     * ログストリームの名前を設定する。<p>
     *
     * @param name ログストリームの名前
     */
    public void setLogStreamName(String name);
    
    /**
     * ログストリームの名前を取得する。<p>
     *
     * @return ログストリームの名前
     */
    public String getLogStreamName();
    
    /**
     * バッファリングして出力する際の、バッファ件数を設定する。<p>
     * デフォルトは、0で、バッファしない。<br>
     *
     * @param size バッファ件数
     */
    public void setBufferSize(int size);
    
    /**
     * バッファリングして出力する際の、バッファ件数を取得する。<p>
     *
     * @return バッファ件数
     */
    public int getBufferSize();
    
    /**
     * バッファリングして出力する際の、バッファタイムアウト[ms]を設定する。<p>
     * デフォルトは、0で、タイムアウトせず、指定されたバッファ件数が溜まるまで出力しない。<br>
     *
     * @param timeout バッファタイムアウト[ms]
     */
    public void setBufferTimeout(long timeout);
    
    /**
     * バッファリングして出力する際の、バッファタイムアウト[ms]を取得する。<p>
     *
     * @return バッファタイムアウト[ms]
     */
    public long getBufferTimeout();
}