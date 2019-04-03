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
package jp.ossc.nimbus.service.queue;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link AWSSQSQueueService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see AWSSQSQueueService
 */
public interface AWSSQSQueueServiceMBean extends ServiceBaseMBean{
    
    /**
     * AmazonSQSClientBuilderのサービス名を設定する。<p>
     *
     * @param name AmazonSQSClientBuilderのサービス名
     */
    public void setAmazonSQSClientBuilderServiceName(ServiceName name);
    
    /**
     * AmazonSQSClientBuilderのサービス名を取得する。<p>
     *
     * @return AmazonSQSClientBuilderのサービス名
     */
    public ServiceName getAmazonSQSClientBuilderServiceName();
    
    /**
     * キューの名前を設定する。<p>
     * 指定しない場合は、サービス名が採用される。また、FIFOなキューの場合は、".fifo"が末尾に付与される。<br>
     *
     * @param name キューの名前
     */
    public void setQueueName(String name);
    
    /**
     * キューの名前を取得する。<p>
     *
     * @return キューの名前
     */
    public String getQueueName();
    
    /**
     * サービスの開始時に、キューを生成するかどうかを設定する。<p>
     * デフォルトは、falseで生成しない。<br>
     *
     * @param isCreate 生成する場合、true
     */
    public void setCreateQueueOnStart(boolean isCreate);
    
    /**
     * サービスの開始時に、キューを生成するかどうかを判定する。<p>
     *
     * @return trueの場合、生成する
     */
    public boolean isCreateQueueOnStart();
    
    /**
     * サービスの停止時に、キューを削除するかどうかを設定する。<p>
     * デフォルトは、falseで削除しない。<br>
     *
     * @param isDelete 削除する場合、true
     */
    public void setDeleteQueueOnStop(boolean isDelete);
    
    /**
     * サービスの停止時に、キューを削除するかどうかを判定する。<p>
     *
     * @return trueの場合、削除する
     */
    public boolean isDeleteQueueOnStop();
    
    /**
     * キューを生成する場合のキューの属性マップを設定する。<p>
     *
     * @param attributes 属性のマップ
     */
    public void setQueueAttributes(Map attributes);
    
    /**
     * キューを生成する場合のキューの属性マップを取得する。<p>
     *
     * @return 属性のマップ
     */
    public Map getQueueAttributes();
    
    /**
     * キューを生成する場合のキューの属性を設定する。<p>
     *
     * @param name 属性名
     * @param value 属性値
     */
    public void setQueueAttribute(String name, String value);
    
    /**
     * キューに投入するメッセージの属性マップを設定する。<p>
     *
     * @param attributes 属性のマップ
     */
    public void setMessageAttributes(Map attributes);
    
    /**
     * キューに投入するメッセージの属性マップを取得する。<p>
     *
     * @return 属性のマップ
     */
    public Map getMessageAttributes();
    
    /**
     * キューに投入するメッセージの属性を設定する。<p>
     *
     * @param name 属性名
     * @param value 属性値
     */
    public void setMessageAttribute(String name, String value);
    
    /**
     * キューに投入したメッセージの遅延時間[s]を設定する。<p>
     * デフォルトは、0で遅延させない。<br>
     *
     * @param seconds 遅延時間[s]
     */
    public void setDelaySeconds(int seconds);
    
    /**
     * キューに投入したメッセージの遅延時間[s]を取得する。<p>
     *
     * @return 遅延時間[s]
     */
    public int getDelaySeconds();
    
    /**
     * キューに投入したオブジェクトをメッセージのボディとして文字列に変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}のサービス名を設定する。<p>
     * 指定しない場合は、直列化してBASE64エンコードした文字列に変換するConverterが適用される。<br>
     *
     * @param name Converterのサービス名
     */
    public void setMessageBodyFormatConverterServiceName(ServiceName name);
    
    /**
     * キューに投入したオブジェクトをメッセージのボディとして文字列に変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}のサービス名を取得する。<p>
     *
     * @return Converterのサービス名
     */
    public ServiceName getMessageBodyFormatConverterServiceName();
    
    /**
     * キューに投入したメッセージのボディ文字列をオブジェクトに変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}のサービス名を設定する。<p>
     * 指定しない場合は、文字列をBASE64デコードして非直列化してオブジェクトに変換するConverterが適用される。<br>
     *
     * @param name Converterのサービス名
     */
    public void setMessageBodyParseConverterServiceName(ServiceName name);
    
    /**
     * キューに投入したメッセージのボディ文字列をオブジェクトに変換する{@link jp.ossc.nimbus.util.converter.Converter Converter}のサービス名を取得する。<p>
     *
     * @return Converterのサービス名
     */
    public ServiceName getMessageBodyParseConverterServiceName();
    
    /**
     * FIFOのキューの場合に、メッセージの一意性を保証するIDを発行する{@linl jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setMessageDeduplicationIdSequenceServiceName(ServiceName name);
    
    /**
     * FIFOのキューの場合に、メッセージの一意性を保証するIDを発行する{@linl jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getMessageDeduplicationIdSequenceServiceName();
    
    /**
     * FIFOのキューの場合に、メッセージの一意性を保証するIDを、キューに投入したオブジェクトから取得する際のプロパティ名を設定する。<p>
     *
     * @param name キューに投入したオブジェクト上の、メッセージの一意性を保証するIDとなるプロパティのプロパティ名
     */
    public void setPropertyNameOfMessageDeduplicationId(String name);
    
    /**
     * FIFOのキューの場合に、メッセージの一意性を保証するIDを、キューに投入したオブジェクトから取得する際のプロパティ名を取得する。<p>
     *
     * @return キューに投入したオブジェクト上の、メッセージの一意性を保証するIDとなるプロパティのプロパティ名
     */
    public String getPropertyNameOfMessageDeduplicationId();
    
    /**
     * FIFOのキューの場合に、メッセージの順序性を保証するメッセージのグループIDを、キューに投入したオブジェクトから取得する際のプロパティ名を設定する。<p>
     *
     * @param name キューに投入したオブジェクト上の、メッセージの順序性を保証するメッセージのグループIDとなるプロパティのプロパティ名
     */
    public void setPropertyNameOfMessageGroupId(String name);
    
    /**
     * FIFOのキューの場合に、メッセージの順序性を保証するメッセージのグループIDを、キューに投入したオブジェクトから取得する際のプロパティ名を取得する。<p>
     *
     * @return キューに投入したオブジェクト上の、メッセージの順序性を保証するメッセージのグループIDとなるプロパティのプロパティ名
     */
    public String getPropertyNameOfMessageGroupId();
    
    /**
     * FIFOのキューの場合に、メッセージの順序性を保証するメッセージのグループIDを設定する。<p>
     *
     * @param id メッセージの順序性を保証するメッセージのグループID
     */
    public void setMessageGroupId(String id);
    
    /**
     * FIFOのキューの場合に、メッセージの順序性を保証するメッセージのグループIDを取得する。<p>
     *
     * @return メッセージの順序性を保証するメッセージのグループID
     */
    public String getMessageGroupId();
}
