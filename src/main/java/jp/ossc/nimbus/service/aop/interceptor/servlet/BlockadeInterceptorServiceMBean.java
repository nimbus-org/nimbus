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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link BlockadeInterceptorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see BlockadeInterceptorService
 */
public interface BlockadeInterceptorServiceMBean
 extends ServletFilterInterceptorServiceMBean{
    
    public static final String DEFAULT_PROPERTY_NAME_PATH    = "path";
    public static final String DEFAULT_PROPERTY_NAME_STATE   = "state";
    public static final String DEFAULT_PROPERTY_NAME_MESSAGE = "message";
    
    /**
     * 閉塞状態：開放。<p>
     */
    public static final int BLOCKADE_STATE_OPEN        = 0;
    
    /**
     * 閉塞状態：完全閉塞。<p>
     * 全てのユーザが閉塞されている状態。<br>
     */
    public static final int BLOCKADE_STATE_ALL_CLOSE       = 1;
    
    /**
     * 閉塞状態：部分閉塞。<p>
     * 全てのユーザが部分閉塞されている状態。<br>
     */
    public static final int BLOCKADE_STATE_PART_CLOSE       = 2;
    
    /**
     * 閉塞状態：テスト閉塞。<p>
     * 特権ユーザ以外は閉塞されている状態。<br>
     */
    public static final int BLOCKADE_STATE_TEST_ALL_CLOSE   = 3;
    
    /**
     * 閉塞状態：テスト部分閉塞。<p>
     * 特権ユーザ以外は部分閉塞されている状態。<br>
     */
    public static final int BLOCKADE_STATE_TEST_PART_CLOSE   = 4;
    
    /**
     * 要求オブジェクトをリクエスト属性から取得する時に使用する属性名を設定する。<p>
     * デフォルト値は、{@link StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME}。<br>
     *
     * @param name 属性名
     * @see StreamExchangeInterceptorServiceMBean#DEFAULT_REQUEST_OBJECT_ATTRIBUTE_NAME
     */
    public void setRequestObjectAttributeName(String name);
    
    /**
     * 要求オブジェクトをリクエスト属性から取得する時に使用する属性名を取得する。<p>
     *
     * @return 属性名
     */
    public String getRequestObjectAttributeName();
    
    /**
     * リクエスト属性のユーザを特定するプロパティと、特権ユーザコードマスタのユーザを特定するプロパティのマッピングを設定する。<p>
     * テスト開放をサポートする場合は、設定する。<br>
     *
     * @param mapping リクエスト属性のユーザを特定するプロパティ=特権ユーザコードマスタのユーザを特定するプロパティ
     */
    public void setSpecialUserMapping(Map mapping);
    
    /**
     * リクエスト属性のユーザを特定するプロパティと、特権ユーザコードマスタのユーザを特定するプロパティのマッピングを取得する。<p>
     *
     * @return リクエスト属性のユーザを特定するプロパティ=特権ユーザコードマスタのユーザを特定するプロパティ
     */
    public Map getSpecialUserMapping();
    
    /**
     * HttpSession上のオブジェクトのユーザを特定するプロパティと、特権ユーザコードマスタのユーザを特定するプロパティのマッピングを設定する。<p>
     * テスト開放をサポートする場合は、設定する。<br>
     *
     * @param mapping HttpSession上のオブジェクトのユーザを特定するプロパティ=特権ユーザコードマスタのユーザを特定するプロパティ
     */
    public void setSessionSpecialUserMapping(Map mapping);
    
    /**
     * HttpSession上のオブジェクトのユーザを特定するプロパティと、特権ユーザコードマスタのユーザを特定するプロパティのマッピングを取得する。<p>
     *
     * @return HttpSession上のオブジェクトのユーザを特定するプロパティ=特権ユーザコードマスタのユーザを特定するプロパティ
     */
    public Map getSessionSpecialUserMapping();
    
    /**
     * リクエスト属性の閉塞レコードを特定するプロパティと、閉塞コードマスタの閉塞レコードを特定するプロパティのマッピングを設定する。<p>
     * 閉塞コードマスタを、リクエスト属性から絞り込みたい場合に、設定する。<br>
     *
     * @param mapping リクエスト属性の閉塞レコードを特定するプロパティ=閉塞コードマスタの閉塞レコードを特定するプロパティ
     */
    public void setBlockadeMapping(Map mapping);
    
    /**
     * リクエスト属性の閉塞レコードを特定するプロパティと、閉塞コードマスタの閉塞レコードを特定するプロパティのマッピングを取得する。<p>
     *
     * @return リクエスト属性の閉塞レコードを特定するプロパティ=閉塞コードマスタの閉塞レコードを特定するプロパティ
     */
    public Map getBlockadeMapping();
    
    /**
     * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を設定する。<p>
     * コードマスタの読み取り一貫性を保証したい場合は、この属性の代わりに、{@link #setThreadContextServiceName(ServiceName)}を設定する。<br>
     *
     * @param name CodeMasterFinderサービスのサービス名
     */
    public void setCodeMasterFinderServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を取得する。<p>
     *
     * @return CodeMasterFinderサービスのサービス名
     */
    public ServiceName getCodeMasterFinderServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}サービスのサービス名を設定する。<p>
     * コードマスタの読み取り一貫性を保証したい場合は、{@link #setCodeMasterFinderServiceName(ServiceName)}の代わりにこの属性を設定する。<br>
     *
     * @param name ThreadContextServiceサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.context.ThreadContextService ThreadContextService}サービスのサービス名を取得する。<p>
     *
     * @return ThreadContextServiceサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * 閉塞コードマスタのコードマスタキーを設定する。<p>
     *
     * @param key コードマスタキー
     */
    public void setBlockadeCodeMasterKey(String key);
    
    /**
     * 閉塞コードマスタのコードマスタキーを取得する。<p>
     *
     * @return コードマスタキー
     */
    public String getBlockadeCodeMasterKey();
    
    /**
     * 特権ユーザコードマスタのコードマスタキーを設定する。<p>
     * テスト開放をサポートする場合は、設定する。<br>
     *
     * @param key コードマスタキー
     */
    public void setSpecialUserCodeMasterKey(String key);
    
    /**
     * 特権ユーザコードマスタのコードマスタキーを取得する。<p>
     *
     * @return コードマスタキー
     */
    public String getSpecialUserCodeMasterKey();
    
    /**
     * 閉塞コードマスタのパスを表すプロパティ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_PROPERTY_NAME_PATH}
     *
     * @param name パスを表すプロパティ名
     * @see #DEFAULT_PROPERTY_NAME_PATH
     */
    public void setPathPropertyName(String name);
    
    /**
     * 閉塞コードマスタのパスを表すプロパティ名を取得する。<p>
     *
     * @return パスを表すプロパティ名
     */
    public String getPathPropertyName();
    
    /**
     * 閉塞コードマスタの閉塞状態を表すプロパティ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_PROPERTY_NAME_STATE}
     *
     * @param name 閉塞状態を表すプロパティ名
     * @see #DEFAULT_PROPERTY_NAME_STATE
     */
    public void setStatePropertyName(String name);
    
    /**
     * 閉塞コードマスタの閉塞状態を表すプロパティ名を取得する。<p>
     *
     * @return 閉塞状態を表すプロパティ名
     */
    public String getStatePropertyName();
    
    /**
     * 閉塞コードマスタの閉塞メッセージを表すプロパティ名を設定する。<p>
     * デフォルトは、{@link #DEFAULT_PROPERTY_NAME_MESSAGE}
     *
     * @param name 閉塞メッセージを表すプロパティ名
     * @see #DEFAULT_PROPERTY_NAME_MESSAGE
     */
    public void setMessagePropertyName(String name);
    
    /**
     * 閉塞コードマスタの閉塞メッセージを表すプロパティ名を取得する。<p>
     *
     * @return 閉塞メッセージを表すプロパティ名
     */
    public String getMessagePropertyName();
    
    /**
     * 要求オブジェクトをHttpSessionから取得する時に使用する属性名を設定する。<p>
     *
     * @param attributeName 属性名
     */
    public void setSessionObjectAttributeName(String attributeName);
    
    /**
     * 要求オブジェクトをHttpSessionから取得する時に使用する属性名を取得する。<p>
     * 
     * @return 属性名
     */
    public String getSessionObjectAttributeName();

    /**
     * "閉塞状態：開放"を表すステータス値を取得する。<p>
     * 
     * @return  "閉塞状態：開放"を表すステータス値
     */
    public int getStateOpen();
    
    /**
     * "閉塞状態：開放"を表すステータス値を設定する。<p>
     * 
     * @param state "閉塞状態：開放"を表すステータス値
     */
    public void setStateOpen(int state);
    
    /**
     * "閉塞状態：完全閉塞"を表すステータス値を取得する。<p>
     * 
     * @return  "閉塞状態：完全閉塞"を表すステータス値
     */
    public int getStateAllClose();
    
    /**
     * "閉塞状態：完全閉塞"を表すステータス値を設定する。<p>
     * 
     * @param state "閉塞状態：完全閉塞"を表すステータス値
     */
    public void setStateAllClose(int state);
    
    /**
     * "閉塞状態：部分閉塞"を表すステータス値を取得する。<p>
     * 
     * @return  "閉塞状態：部分閉塞"を表すステータス値
     */
    public int getStatePartClose();
    
    /**
     * "閉塞状態：部分閉塞"を表すステータス値を設定する。<p>
     * 
     * @param state "閉塞状態：部分閉塞"を表すステータス値
     */
    public void setStatePartClose(int state);
    
    /**
     * "閉塞状態：テスト閉塞"を表すステータス値を取得する。<p>
     * 
     * @return  "閉塞状態：テスト閉塞"を表すステータス値
     */
    public int getStateTestAllClose();
    
    /**
     * "閉塞状態：テスト閉塞"を表すステータス値を設定する。<p>
     * 
     * @param state "閉塞状態：テスト閉塞"を表すステータス値
     */
    public void setStateTestAllClose(int state);
    
    /**
     * "閉塞状態：テスト部分閉塞"を表すステータス値を取得する。<p>
     * 
     * @return  "閉塞状態：テスト部分閉塞"を表すステータス値
     */
    public int getStateTestPartClose();
    
    /**
     * "閉塞状態：テスト部分閉塞"を表すステータス値を設定する。<p>
     * 
     * @param state "閉塞状態：テスト部分閉塞"を表すステータス値
     */
    public void setStateTestPartClose(int state);
    
}