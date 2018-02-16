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
package jp.ossc.nimbus.service.log;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultCommonsLogFactoryService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DefaultCommonsLogFactoryServiceMBean extends LogServiceMBean{
    
    public static final String CATEGORY_COMMONS_TRACE
     = "jp.ossc.nimbus.service.log.DefaultCommonsLogService.CATEGORY_COMMONS_TRACE";
    public static final String CATEGORY_COMMONS_DEBUG
     = "jp.ossc.nimbus.service.log.DefaultCommonsLogService.CATEGORY_COMMONS_DEBUG";
    public static final String CATEGORY_COMMONS_INFO
     = "jp.ossc.nimbus.service.log.DefaultCommonsLogService.CATEGORY_COMMONS_INFO";
    public static final String CATEGORY_COMMONS_WARN
     = "jp.ossc.nimbus.service.log.DefaultCommonsLogService.CATEGORY_COMMONS_WARN";
    public static final String CATEGORY_COMMONS_ERROR
     = "jp.ossc.nimbus.service.log.DefaultCommonsLogService.CATEGORY_COMMONS_ERROR";
    public static final String CATEGORY_COMMONS_FATAL
     = "jp.ossc.nimbus.service.log.DefaultCommonsLogService.CATEGORY_COMMONS_FATAL";
    
    public static final int PRIORITY_COMMONS_TRACE_MIN = 0;
    public static final int PRIORITY_COMMONS_TRACE_MAX = 9;
    public static final int PRIORITY_COMMONS_TRACE = 5;
    
    public static final int PRIORITY_COMMONS_DEBUG_MIN = 10;
    public static final int PRIORITY_COMMONS_DEBUG_MAX = 19;
    public static final int PRIORITY_COMMONS_DEBUG = 15;
    
    public static final int PRIORITY_COMMONS_INFO_MIN = 20;
    public static final int PRIORITY_COMMONS_INFO_MAX = 29;
    public static final int PRIORITY_COMMONS_INFO = 25;
    
    public static final int PRIORITY_COMMONS_WARN_MIN = 30;
    public static final int PRIORITY_COMMONS_WARN_MAX = 39;
    public static final int PRIORITY_COMMONS_WARN = 35;
    
    public static final int PRIORITY_COMMONS_ERROR_MIN = 40;
    public static final int PRIORITY_COMMONS_ERROR_MAX = 49;
    public static final int PRIORITY_COMMONS_ERROR = 45;
    
    public static final int PRIORITY_COMMONS_FATAL_MIN = 50;
    public static final int PRIORITY_COMMONS_FATAL_MAX = 59;
    public static final int PRIORITY_COMMONS_FATAL = 55;
    
    public static final String LABEL_COMMONS_TRACE = "TRACE";
    public static final String LABEL_COMMONS_DEBUG = "DEBUG";
    public static final String LABEL_COMMONS_INFO = "INFO";
    public static final String LABEL_COMMONS_WARN = "WARN";
    public static final String LABEL_COMMONS_ERROR = "ERROR";
    public static final String LABEL_COMMONS_FATAL = "FATAL";
    
    /**
     * ログサービスのクライアントを識別するキーを表す出力フォーマットのキー。<p> 
     */
    public static final String FORMAT_CLIENT_KEY = "CLIENT";
    
    /**
     * デフォルトフォーマット。<p>
     * "%DATE%,%CLIENT%,%PRIORITY%,%MESSAGE%"
     */
    public static final String DEFAULT_FORMAT
         = '%' + FORMAT_DATE_KEY + "%,%" + FORMAT_CLIENT_KEY + "%,%"
          + FORMAT_PRIORITY_KEY + "%,%" + FORMAT_MESSAGE_KEY + '%';
    
    /**
     * 指定されたログクライアントからのログだけを出力するように設定する。<p>
     *
     * @param clients {@link org.apache.commons.logging.LogFactory#getLog(String)}の引数の文字列、または{@link org.apache.commons.logging.LogFactory#getLog(Class)}の引数のクラスのパッケージ名を除いたクラス名
     */
    public void setEnabledClients(String[] clients);
    
    /**
     * 有効なログクライアントのキー名配列を取得する。<p>
     *
     * @return 有効なログクライアントのキー名配列
     */
    public String[] getEnabledClients();
    
    /**
     * {@link org.apache.commons.logging.Log#trace(Object)}のログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setCommonsTraceEnabled(boolean isEnabled);
    
    /**
     * {@link org.apache.commons.logging.Log#trace(Object)}のログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isCommonsTraceEnabled();
    
    /**
     * {@link org.apache.commons.logging.Log#debug(Object)}のログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setCommonsDebugEnabled(boolean isEnabled);
    
    /**
     * {@link org.apache.commons.logging.Log#debug(Object)}のログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isCommonsDebugEnabled();
    
    /**
     * {@link org.apache.commons.logging.Log#info(Object)}のログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setCommonsInfoEnabled(boolean isEnabled);
    
    /**
     * {@link org.apache.commons.logging.Log#info(Object)}のログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isCommonsInfoEnabled();
    
    /**
     * {@link org.apache.commons.logging.Log#warn(Object)}のログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setCommonsWarnEnabled(boolean isEnabled);
    
    /**
     * {@link org.apache.commons.logging.Log#warn(Object)}のログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isCommonsWarnEnabled();
    
    /**
     * {@link org.apache.commons.logging.Log#error(Object)}のログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setCommonsErrorEnabled(boolean isEnabled);
    
    /**
     * {@link org.apache.commons.logging.Log#error(Object)}のログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isCommonsErrorEnabled();
    
    /**
     * {@link org.apache.commons.logging.Log#fatal(Object)}のログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setCommonsFatalEnabled(boolean isEnabled);
    
    /**
     * {@link org.apache.commons.logging.Log#fatal(Object)}のログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isCommonsFatalEnabled();
    
    /**
     * {@link org.apache.commons.logging.Log#trace(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setCommonsTraceMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link org.apache.commons.logging.Log#trace(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getCommonsTraceMessageWriterServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#debug(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setCommonsDebugMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link org.apache.commons.logging.Log#debug(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getCommonsDebugMessageWriterServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#info(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setCommonsInfoMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link org.apache.commons.logging.Log#info(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getCommonsInfoMessageWriterServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#warn(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setCommonsWarnMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link org.apache.commons.logging.Log#warn(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getCommonsWarnMessageWriterServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#error(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setCommonsErrorMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link org.apache.commons.logging.Log#error(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getCommonsErrorMessageWriterServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#fatal(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setCommonsFatalMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link org.apache.commons.logging.Log#fatal(Object)}のログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getCommonsFatalMessageWriterServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#trace(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setCommonsTraceWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link org.apache.commons.logging.Log#trace(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getCommonsTraceWritableRecordFactoryServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#debug(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setCommonsDebugWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link org.apache.commons.logging.Log#debug(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getCommonsDebugWritableRecordFactoryServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#info(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setCommonsInfoWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link org.apache.commons.logging.Log#info(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getCommonsInfoWritableRecordFactoryServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#warn(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setCommonsWarnWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link org.apache.commons.logging.Log#warn(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getCommonsWarnWritableRecordFactoryServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#error(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setCommonsErrorWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link org.apache.commons.logging.Log#error(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getCommonsErrorWritableRecordFactoryServiceName();
    
    /**
     * {@link org.apache.commons.logging.Log#fatal(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setCommonsFatalWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link org.apache.commons.logging.Log#fatal(Object)}のログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getCommonsFatalWritableRecordFactoryServiceName();
}
