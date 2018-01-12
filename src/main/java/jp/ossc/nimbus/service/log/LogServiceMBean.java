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
import jp.ossc.nimbus.service.message.*;
import jp.ossc.nimbus.service.queue.*;
import jp.ossc.nimbus.service.context.*;

/**
 * {@link LogService}サービスMBeanインタフェース。<p>
 *
 * @author Y.Tokuda
 */
public interface LogServiceMBean extends ServiceBaseMBean {
    
    /** debugメソッドカテゴリ名 */
    public static final String DEBUG_METHOD_CATEGORY
         = "jp.ossc.nimbus.service.log.DEBUG_METHOD_CATEGORY";
    /** debugメソッドカテゴリの優先度範囲の最小値 */
    public static final int DEBUG_METHOD_CATEGORY_PRIORITY_MIN = -1;
    /** debugメソッドカテゴリの優先度範囲の最大値 */
    public static final int DEBUG_METHOD_CATEGORY_PRIORITY_MAX = -1;
    /** debugメソッドカテゴリのの出力ラベル */
    public static final String DEBUG_METHOD_CATEGORY_LABEL = "DEBUG";
    
    /** システムDEBUGカテゴリ名 */
    public static final String SYSTEM_DEBUG_CATEGORY
         = "jp.ossc.nimbus.service.log.SYSTEM_DEBUG_CATEGORY";
    /** システムDEBUGカテゴリの優先度範囲の最小値 */
    public static final int SYSTEM_DEBUG_CATEGORY_PRIORITY_MIN = 0;
    /** システムDEBUGカテゴリの優先度範囲の最大値 */
    public static final int SYSTEM_DEBUG_CATEGORY_PRIORITY_MAX = 49;
    /** システムDEBUGカテゴリの出力ラベル */
    public static final String SYSTEM_DEBUG_CATEGORY_LABEL = "SYSTEM_DEBUG";
    
    /** システムINFOカテゴリ名 */
    public static final String SYSTEM_INFO_CATEGORY
         = "jp.ossc.nimbus.service.log.SYSTEM_INFO_CATEGORY";
    /** システムINFOカテゴリの優先度範囲の最小値 */
    public static final int SYSTEM_INFO_CATEGORY_PRIORITY_MIN = 50;
    /** システムINFOカテゴリの優先度範囲の最大値 */
    public static final int SYSTEM_INFO_CATEGORY_PRIORITY_MAX = 99;
    /** システムDEBUGカテゴリの出力ラベル */
    public static final String SYSTEM_INFO_CATEGORY_LABEL = "SYSTEM_INFO";
    
    /** システムWARNカテゴリ名 */
    public static final String SYSTEM_WARN_CATEGORY
         = "jp.ossc.nimbus.service.log.SYSTEM_WARN_CATEGORY";
    /** システムWARNカテゴリの優先度範囲の最小値 */
    public static final int SYSTEM_WARN_CATEGORY_PRIORITY_MIN = 100;
    /** システムWARNカテゴリの優先度範囲の最大値 */
    public static final int SYSTEM_WARN_CATEGORY_PRIORITY_MAX = 149;
    /** システムWARNカテゴリの出力ラベル */
    public static final String SYSTEM_WARN_CATEGORY_LABEL = "SYSTEM_WARN";
    
    /** システムERRORカテゴリ名 */
    public static final String SYSTEM_ERROR_CATEGORY
         = "jp.ossc.nimbus.service.log.SYSTEM_ERROR_CATEGORY";
    /** システムERRORカテゴリの優先度範囲の最小値 */
    public static final int SYSTEM_ERROR_CATEGORY_PRIORITY_MIN = 150;
    /** システムERRORカテゴリの優先度範囲の最大値 */
    public static final int SYSTEM_ERROR_CATEGORY_PRIORITY_MAX = 199;
    /** システムERRORカテゴリの出力ラベル */
    public static final String SYSTEM_ERROR_CATEGORY_LABEL = "SYSTEM_ERROR";
    
    /** システムFATALカテゴリ名 */
    public static final String SYSTEM_FATAL_CATEGORY
         = "jp.ossc.nimbus.service.log.SYSTEM_FATAL_CATEGORY";
    /** システムFATALカテゴリの優先度範囲の最小値 */
    public static final int SYSTEM_FATAL_CATEGORY_PRIORITY_MIN = 200;
    /** システムFATALカテゴリの優先度範囲の最大値 */
    public static final int SYSTEM_FATAL_CATEGORY_PRIORITY_MAX = 249;
    /** システムFATALカテゴリの出力ラベル */
    public static final String SYSTEM_FATAL_CATEGORY_LABEL = "SYSTEM_FATAL";
    
    /** カテゴリを表す出力フォーマットのキー */
    public static final String FORMAT_CATEGORY_KEY = "CATEGORY";
    /** コードを表す出力フォーマットのキー */
    public static final String FORMAT_CODE_KEY = "CODE";
    /** 日付を表す出力フォーマットのキー */
    public static final String FORMAT_DATE_KEY = "DATE";
    /** 優先度を表す出力フォーマットのキー */
    public static final String FORMAT_PRIORITY_KEY = "PRIORITY";
    /** メッセージを表す出力フォーマットのキー */
    public static final String FORMAT_MESSAGE_KEY = "MESSAGE";
    
    /**
     * デフォルトフォーマット。<p>
     * "%DATE%,%PRIORITY%,[%CODE%]%MESSAGE%"
     */
    public static final String DEFAULT_FORMAT
         = '%' + FORMAT_DATE_KEY + "%,%" + FORMAT_PRIORITY_KEY + "%,[%"
             + FORMAT_CODE_KEY + "%]%" + FORMAT_MESSAGE_KEY + '%';
    
    /**
     * 出力するログのカテゴリを定義する{@link LogCategory}サービスの名前を設定する。<p>
     * 
     * @param names LogCategoryサービス名の配列
     */
    public void setCategoryServiceNames(ServiceName[] names);
    
    /**
     * 出力するログのカテゴリを定義する{@link LogCategory}サービスの名前を取得する。<p>
     * 
     * @return LogCategoryサービス名の配列
     */
    public ServiceName[] getCategoryServiceNames();
    
    /**
     * 出力するログのカテゴリを定義する{@link LogCategory}サービスを設定する。<p>
     * 
     * @param categories LogCategoryサービスの配列
     */
    public void setCategoryServices(LogCategory[] categories);
    
    /**
     * 出力するログのカテゴリを定義する{@link LogCategory}サービスを取得する。<p>
     * 
     * @return LogCategoryサービスの配列
     */
    public LogCategory[] getCategoryServices();
    
    /**
     * 出力するログのカテゴリを定義する{@link LogCategory}サービスを追加する。<p>
     * 
     * @param category LogCategoryサービス
     */
    public void addCategoryService(LogCategory category);
    
    /**
     * 指定されたカテゴリ名を持つカテゴリを定義する{@link LogCategory}サービスを取得する。<p>
     * 
     * @param name カテゴリ名
     * @return LogCategoryサービス
     */
    public LogCategory getCategoryService(String name);
    
    /**
     * デフォルトの{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     * デフォルトのカテゴリのMessageWriterサービスが指定されていない場合に使用する。<br>
     *
     * @param name MessageWriterサービス名
     */
    public void setDefaultMessageWriterServiceName(ServiceName name);
    
    /**
     * デフォルトの{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     * デフォルトのカテゴリのMessageWriterサービスが指定されていない場合に使用する。<br>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getDefaultMessageWriterServiceName();
    
    /**
     * デフォルトの{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     * デフォルトのカテゴリのWritableRecordFactoryサービスが指定されていない場合に使用する。<br>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setDefaultWritableRecordFactoryServiceName(ServiceName name);
    
    /**
     * デフォルトの{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     * デフォルトのカテゴリのWritableRecordFactoryサービスが指定されていない場合に使用する。<br>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getDefaultWritableRecordFactoryServiceName();
    
    /**
     * ログ出力に使用するメッセージを管理する{@link MessageRecordFactory}サービス名を設定する。<p>
     * 
     * @param name MessageRecordFactoryサービス名
     */
    public void setMessageRecordFactoryServiceName(ServiceName name);
    
    /**
     * ログ出力に使用するメッセージを管理する{@link MessageRecordFactory}サービスを設定する。<p>
     * 
     * @param message MessageRecordFactoryサービス
     */
    public void setMessageRecordFactoryService(MessageRecordFactory message);
    
    /**
     * ログ出力に使用するメッセージを管理する{@link MessageRecordFactory}サービス名を取得する。<p>
     * 
     * @return MessageRecordFactoryサービス名
     */
    public ServiceName getMessageRecordFactoryServiceName();
    
    /**
     * ログ出力に使用するメッセージを管理する{@link MessageRecordFactory}サービスを取得する。<p>
     * 
     * @return MessageRecordFactoryサービス
     */
    public MessageRecordFactory getMessageRecordFactoryService();
    
    /**
     * 依頼されたログ出力を一旦キューイングする{@link Queue}を生成する{@link Queue}サービス名を設定する。<p>
     * 
     * @param name Queueサービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * 依頼されたログ出力を一旦キューイングする{@link Queue}サービスを設定する。<p>
     * 
     * @param queue Queueサービス名
     */
    public void setQueueService(Queue queue);
    
    /**
     * 依頼されたログ出力を一旦キューイングする{@link Queue}サービス名を取得する。<p>
     * 
     * @return Queueサービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * 依頼されたログ出力を一旦キューイングする{@link Queue}を生成する{@link Queue}サービスを取得する。<p>
     * 
     * @return Queueサービス
     */
    public Queue getQueueService();
    
    /**
     * ログの識別情報を保持する{@link Context}サービス名を設定する。<p>
     * 
     * @param name Contextサービス名
     */
    public void setContextServiceName(ServiceName name);
    
    /**
     * ログの識別情報を保持する{@link Context}サービスを設定する。<p>
     * 
     * @param context Contextサービス
     */
    public void setContextService(Context context);
    
    /**
     * ログの識別情報を保持する{@link Context}サービス名を取得する。<p>
     * 
     * @return Contextサービス名
     */
    public ServiceName getContextServiceName();
    
    /**
     * ログの識別情報を保持する{@link Context}サービスを取得する。<p>
     * 
     * @return Contextサービス
     */
    public Context getContextService();
    
    /**
     * {@link Context}サービスから取得するフォーマット情報のキー名を設定する。<p>
     * 
     * @param keys Contextサービスから取得するフォーマット情報のキー名配列
     */
    public void setContextFormatKeys(String[] keys);
    
    /**
     * {@link Context}サービスから取得するフォーマット情報のキー名を追加する。<p>
     * 
     * @param key Contextサービスから取得するフォーマット情報のキー名
     */
    public void addContextFormatKey(String key);
    
    /**
     * {@link Context}サービスから取得するフォーマット情報のキー名を削除する。<p>
     * 
     * @param key Contextサービスから取得するフォーマット情報のキー名
     */
    public void removeContextFormatKey(String key);
    
    /**
     * {@link Context}サービスから取得するフォーマット情報のキー名を全て削除する。<p>
     */
    public void clearContextFormatKeys();
    
    /**
     * {@link Context}サービスから取得するフォーマット情報のキー名を取得する。<p>
     * 
     * @return Contextサービスから取得するフォーマット情報のキー名
     */
    public String[] getContextFormatKeys();
    
    /**
     * {@link Logger#debug(Object)}メソッドのログを出力するかどうかを設定する。<p>
     * 
     * @param isEnabled 出力する場合 true
     */
    public void setDebugEnabled(boolean isEnabled);
    
    /**
     * {@link Logger#debug(Object)}メソッドのログを出力するかどうかを調べる。<p>
     * 
     * @return 出力する場合 true
     */
    public boolean isDebugEnabled();
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setSystemDebugEnabled(boolean isEnabled);
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isSystemDebugEnabled();
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setSystemInfoEnabled(boolean isEnabled);
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isSystemInfoEnabled();
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setSystemWarnEnabled(boolean isEnabled);
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isSystemWarnEnabled();
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setSystemErrorEnabled(boolean isEnabled);
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isSystemErrorEnabled();
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力を行うかどうかを設定する。<p>
     *
     * @param isEnabled 出力する場合 true
     */
    public void setSystemFatalEnabled(boolean isEnabled);
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力を行うかどうかを調べる。<p>
     *
     * @return 出力する場合 true
     */
    public boolean isSystemFatalEnabled();
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setDebugMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getDebugMessageWriterServiceName();
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setSystemDebugMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getSystemDebugMessageWriterServiceName();
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setSystemInfoMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getSystemInfoMessageWriterServiceName();
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setSystemWarnMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getSystemWarnMessageWriterServiceName();
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setSystemErrorMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getSystemErrorMessageWriterServiceName();
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を設定する。<p>
     *
     * @param name MessageWriterサービス名
     */
    public void setSystemFatalMessageWriterServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力を行う{@link jp.ossc.nimbus.service.writer.MessageWriter MessageWriter}サービス名を取得する。<p>
     *
     * @return MessageWriterサービス名
     */
    public ServiceName getSystemFatalMessageWriterServiceName();
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setDebugWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link #DEBUG_METHOD_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getDebugWritableRecordFactoryServiceName();
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setSystemDebugWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link #SYSTEM_DEBUG_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getSystemDebugWritableRecordFactoryServiceName();
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setSystemInfoWritableRecordFactoryServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_INFO_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getSystemInfoWritableRecordFactoryServiceName();
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setSystemWarnWritableRecordFactoryServiceName(ServiceName name);
    
    /**
     * {@link #SYSTEM_WARN_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getSystemWarnWritableRecordFactoryServiceName();
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setSystemErrorWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link #SYSTEM_ERROR_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getSystemErrorWritableRecordFactoryServiceName();
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を設定する。<p>
     *
     * @param name WritableRecordFactoryサービス名
     */
    public void setSystemFatalWritableRecordFactoryServiceName(
        ServiceName name
    );
    
    /**
     * {@link #SYSTEM_FATAL_CATEGORY}カテゴリのログ出力フォーマットを行う{@link jp.ossc.nimbus.service.writer.WritableRecordFactory WritableRecordFactory}サービス名を取得する。<p>
     *
     * @return WritableRecordFactoryサービス名
     */
    public ServiceName getSystemFatalWritableRecordFactoryServiceName();
    
    /**
     * ログ出力スレッドをデーモンスレッドにするかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isDaemon デーモンスレッドにする場合true
     */
    public void setDaemon(boolean isDaemon);
    
    /**
     * ログ出力スレッドがデーモンスレッドかどうかを判定する。<p>
     *
     * @return trueの場合は、デーモンスレッド
     */
    public boolean isDaemon();
}
