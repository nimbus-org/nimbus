/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2009 The Nimbus2 Project. All rights reserved.
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
 * policies, either expressed or implied, of the Nimbus2 Project.
 */
package jp.ossc.nimbus.service.rest;

import java.util.Map;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link BeanFlowRestServerService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface BeanFlowRestServerServiceMBean extends ServiceBaseMBean{
    
    /**
     * 検証BeanFlowの前置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_VALIDATE_FLOW_PREFIX = "validate";
    
    /**
     * POSTメソッド用BeanFlowの後置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_POST_METHOD_FLOW_POSTFIX = "$POST";
    
    /**
     * GETメソッド用BeanFlowの後置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_GET_METHOD_FLOW_POSTFIX = "$GET";
    
    /**
     * HEADメソッド用BeanFlowの後置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_HEAD_METHOD_FLOW_POSTFIX = "$HEAD";
    
    /**
     * PUTメソッド用BeanFlowの後置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_PUT_METHOD_FLOW_POSTFIX = "$PUT";
    
    /**
     * DELETEメソッド用BeanFlowの後置詞のデフォルト値。<p>
     */
    public static final String DEFAULT_DELETE_METHOD_FLOW_POSTFIX = "$DELETE";
    
    public static final String JOURNAL_KEY_REST_PROCESS = "RestProcess";
    public static final String JOURNAL_KEY_REQUEST_URI = "RequestURI";
    public static final String JOURNAL_KEY_METHOD = "Method";
    public static final String JOURNAL_KEY_RESULT_STATUS = "ResultStatus";
    public static final String JOURNAL_KEY_EXCEPTION = "Exception";
    public static final String JOURNAL_KEY_ACCEPT_HEADER = "Accept";
    public static final String JOURNAL_KEY_ACCEPT_CHARSET_HEADER = "AcceptCharset";
    public static final String JOURNAL_KEY_CONTENT_TYPE_HEADER = "ContentType";
    public static final String JOURNAL_KEY_RESOURCE_PATH = "ResourcePath";
    public static final String JOURNAL_KEY_PATH_PARAMETERS = "PathParamsters";
    public static final String JOURNAL_KEY_REQUEST_PARAMETERS = "RequestParamsters";
    public static final String JOURNAL_KEY_REQUEST_BODY = "RequestBody";
    public static final String JOURNAL_KEY_REQUEST_OBJECT = "RequestObject";
    public static final String JOURNAL_KEY_VALIDATE_FLOW = "ValidateFlow";
    public static final String JOURNAL_KEY_FLOW = "Flow";
    public static final String JOURNAL_KEY_RESPONSE_BODY = "ResponseBody";
    public static final String JOURNAL_KEY_RESPONSE_OBJECT = "ResponseObject";
    
    /**
     * RESTサーバ定義ファイルのパスを設定する。<p>
     * パスは、絶対パス、サービス定義ファイルからの相対パス、クラスパスを指定できる。<br>
     *
     * @param path RESTサーバ定義ファイルのパス
     */
    public void setServerDefinitionPath(String path);
    
    /**
     * RESTサーバ定義ファイルのパスを取得する。<p>
     *
     * @return RESTサーバ定義ファイルのパス
     */
    public String getServerDefinitionPath();
    
    /**
     * RESTサーバ定義ファイルをパースするjavax.xml.parsers.DocumentBuilderFactoryのクラス名を設定する。<p>
     * 指定しない場合は、DocumentBuilderFactory.newInstance()でDocumentBuilderFactoryを生成する。<br>
     *
     * @param name DocumentBuilderFactoryのクラス名
     */
    public void setDocumentBuilderFactoryClassName(String name);
    
    /**
     * RESTサーバ定義ファイルをパースするjavax.xml.parsers.DocumentBuilderFactoryのクラス名を取得する。<p>
     *
     * @return DocumentBuilderFactoryのクラス名
     */
    public String getDocumentBuilderFactoryClassName();
    
    /**
     * RESTサーバ定義ファイルを検証するかどうかを設定する。<p>
     * デフォルトは、falseで検証しない。<br>
     * 
     * @param validate 検証する場合、true
     */
    public void setValidate(boolean validate);
    
    /**
     * RESTサーバ定義ファイルを検証するかどうかを判定する。<p>
     * 
     * @return trueの場合、検証する
     */
    public boolean isValidate();
    
    /**
     * リクエストオブジェクト検証フローのフロー名の前置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_VALIDATE_FLOW_PREFIX}。<br>
     *
     * @param prefix フロー名の前置詞
     */
    public void setValidateFlowPrefix(String prefix);
    
    /**
     * リクエストオブジェクト検証フローのフロー名の前置詞を取得する。<p>
     *
     * @return フロー名の前置詞
     */
    public String getValidateFlowPrefix();
    
    /**
     * POSTメソッド処理フローのフロー名の後置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_POST_METHOD_FLOW_POSTFIX}。<br>
     *
     * @param postfix フロー名の後置詞
     */
    public void setPostMethodFlowPostfix(String postfix);
    
    /**
     * POSTメソッド処理フローのフロー名の後置詞を取得する。<p>
     *
     * @return フロー名の後置詞
     */
    public String getPostMethodFlowPostfix();
    
    /**
     * GETメソッド処理フローのフロー名の後置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_GET_METHOD_FLOW_POSTFIX}。<br>
     *
     * @param postfix フロー名の後置詞
     */
    public void setGetMethodFlowPostfix(String postfix);
    
    /**
     * GETメソッド処理フローのフロー名の後置詞を取得する。<p>
     *
     * @return フロー名の後置詞
     */
    public String getGetMethodFlowPostfix();
    
    /**
     * HEADメソッド処理フローのフロー名の後置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_HEAD_METHOD_FLOW_POSTFIX}。<br>
     *
     * @param postfix フロー名の後置詞
     */
    public void setHeadMethodFlowPostfix(String postfix);
    
    /**
     * HEADメソッド処理フローのフロー名の後置詞を取得する。<p>
     *
     * @return フロー名の後置詞
     */
    public String getHeadMethodFlowPostfix();
    
    /**
     * PUTメソッド処理フローのフロー名の後置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_PUT_METHOD_FLOW_POSTFIX}。<br>
     *
     * @param postfix フロー名の後置詞
     */
    public void setPutMethodFlowPostfix(String postfix);
    
    /**
     * PUTメソッド処理フローのフロー名の後置詞を取得する。<p>
     *
     * @return フロー名の後置詞
     */
    public String getPutMethodFlowPostfix();
    
    /**
     * DELETEメソッド処理フローのフロー名の後置詞を設定する。<p>
     * デフォルトは、{@link #DEFAULT_DELETE_METHOD_FLOW_POSTFIX}。<br>
     *
     * @param postfix フロー名の後置詞
     */
    public void setDeleteMethodFlowPostfix(String postfix);
    
    /**
     * DELETEメソッド処理フローのフロー名の後置詞を取得する。<p>
     *
     * @return フロー名の後置詞
     */
    public String getDeleteMethodFlowPostfix();
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name BeanFlowFactoryサービスのサービス名
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowFactoryサービスのサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を設定する。<p>
     *
     * @param name Journalサービスのサービス名
     */
    public void setJournalServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.Journal Journal}サービスのサービス名を取得する。<p>
     *
     * @return Journalサービスのサービス名
     */
    public ServiceName getJournalServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を設定する。<p>
     *
     * @param name EditorFinderサービスのサービス名
     */
    public void setEditorFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.journal.editorfinder.EditorFinder EditorFinder}サービスのサービス名を取得する。<p>
     *
     * @return EditorFinderサービスのサービス名
     */
    public ServiceName getEditorFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     *
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     *
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     *
     * @param name Contextサービスのサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     *
     * @return Contextサービスのサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * Contextサービスに設定されたリクエストIDのキー名を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.service.aop.interceptor.ThreadContextKey#REQUEST_ID}。<br>
     *
     * @param key リクエストIDのキー名
     * @see #getRequestIdKey()
     */
    public void setRequestIdKey(String key);
    
    /**
     * Contextサービスに設定されたリクエストIDのキー名を取得する。<p>
     *
     * @return リクエストIDのキー名
     * @see #setRequestIdKey(String)
     */
    public String getRequestIdKey();
    
    /**
     * メディアタイプ毎のリクエストオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.BindingStreamConverter BindingStreamConverter}サービスのサービス名のマッピングを設定する。<p>
     *
     * @param mapping メディアタイプとBindingStreamConverterサービスのサービス名のマッピング。メディアタイプ=BindingStreamConverterサービスのサービス名
     */
    public void setRequestConverterServiceNames(Map mapping);
    
    /**
     * メディアタイプ毎のリクエストオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.BindingStreamConverter BindingStreamConverter}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return メディアタイプとBindingStreamConverterサービスのサービス名のマッピング
     */
    public Map getRequestConverterServiceNames();
    
    /**
     * 指定したメディアタイプのリクエストオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.BindingStreamConverter BindingStreamConverter}サービスのサービス名を設定する。<p>
     *
     * @param mediaType メディアタイプ
     * @param name BindingStreamConverterサービスのサービス名
     */
    public void setRequestConverterServiceName(String mediaType, ServiceName name);
    
    /**
     * 指定したメディアタイプのリクエストオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.BindingStreamConverter BindingStreamConverter}サービスのサービス名を取得する。<p>
     *
     * @param mediaType メディアタイプ
     * @return BindingStreamConverterサービスのサービス名
     */
    public ServiceName getRequestConverterServiceName(String mediaType);
    
    /**
     * メディアタイプ毎のレスポンスオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名のマッピングを設定する。<p>
     *
     * @param mapping メディアタイプとStreamConverterサービスのサービス名のマッピング。メディアタイプ=StreamConverterサービスのサービス名
     */
    public void setResponseConverterServiceNames(Map mapping);
    
    /**
     * メディアタイプ毎のレスポンスオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return メディアタイプとStreamConverterサービスのサービス名のマッピング
     */
    public Map getResponseConverterServiceNames();
    
    /**
     * 指定したメディアタイプのレスポンスオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を設定する。<p>
     *
     * @param mediaType メディアタイプ
     * @param name StreamConverterサービスのサービス名
     */
    public void setResponseConverterServiceName(String mediaType, ServiceName name);
    
    /**
     * 指定したメディアタイプのレスポンスオブジェクトの変換を行う{@link jp.ossc.nimbus.util.converter.StreamConverter StreamConverter}サービスのサービス名を取得する。<p>
     *
     * @param mediaType メディアタイプ
     * @return StreamConverterサービスのサービス名
     */
    public ServiceName getResponseConverterServiceName(String mediaType);
    
    /**
     * Accept-Charsetヘッダが指定されていない場合の、デフォルトの文字コードを設定する。<p>
     * デフォルトは、UTF-8。<br>
     *
     * @param encoding 文字コード
     */
    public void setDefaultResponseCharacterEncoding(String encoding);
    
    /**
     * Accept-Charsetヘッダが指定されていない場合の、デフォルトの文字コードを取得する。<p>
     *
     * @return 文字コード
     */
    public String getDefaultResponseCharacterEncoding();
    
    /**
     * ContentLengthの最大値を設定する。<p>
     *
     * @param size ContentLengthの最大値
     */
    public void setRequestSizeThreshold(long size);
    
    /**
     * ContentLengthの最大値を取得する。<p>
     *
     * @return ContentLengthの最大値
     */
    public long getRequestSizeThreshold();
    
    /**
     * RESTサーバ定義ファイルを再読み込みする。<p>
     *
     * @exception Exception 再読み込みに失敗した場合
     */
    public void reload() throws Exception;
}
