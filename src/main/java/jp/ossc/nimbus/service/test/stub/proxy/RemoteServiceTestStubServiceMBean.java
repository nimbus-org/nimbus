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
package jp.ossc.nimbus.service.test.stub.proxy;

import java.io.File;
import java.util.Map;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.service.test.TestStub;

/**
 * {@link RemoteServiceTestStubService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see RemoteServiceTestStubService
 */
public interface RemoteServiceTestStubServiceMBean extends ServiceBaseMBean, TestStub{
    
    /**
     * スタブIDを設定する。<p>
     *
     * @param id スタブID
     */
    public void setId(String id);
    
    /**
     * スタブIDを取得する。<p>
     *
     * @return スタブID
     */
    public String getId();
    
    /**
     * リソースファイルの文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setFileEncoding(String encoding);
    
    /**
     * リソースファイルの文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getFileEncoding();
    
    /**
     * {@link jp.ossc.nimbus.service.test.StubResourceManager StubResourceManager}サービスのサービス名を設定する。<p>
     *
     * @param name StubResourceManagerサービスのサービス名
     */
    public void setStubResourceManagerServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.test.StubResourceManager StubResourceManager}サービスのサービス名を取得する。<p>
     *
     * @return StubResourceManagerサービスのサービス名
     */
    public ServiceName getStubResourceManagerServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     *
     * @param name Interpreterサービスのサービス名
     */
    public void setInterpreterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     *
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getInterpreterServiceName();
    
    /**
     * StubResourceManagerからダウンロードしたリソースファイルを配置するディレクトリを設定する。<p>
     * デフォルトは、サービス定義ファイルの場所にスタブIDでディレクトリを配置する。<br>
     *
     * @param dir ディレクトリ
     */
    public void setResourceDirectory(File dir);
    
    /**
     * StubResourceManagerからダウンロードしたリソースファイルを配置するディレクトリを取得する。<p>
     *
     * @return ディレクトリ
     */
    public File getResourceDirectory();
    
    /**
     * 同じ呼び出しの繰り返しを許すかどうかを判定する。<p>
     *
     * @return trueの場合、許す
     */
    public boolean isAllowRepeatCall();
    /**
     * 同じ呼び出しの繰り返しを許すかどうかを設定する。<p>
     *
     * @param isAllow 許す場合、true
     */
    public void setAllowRepeatRequest(boolean isAllow);
    
    /**
     * マルチスレッド処理を安全に行うかどうかを判定する。<p>
     *
     * @return trueの場合、安全に行う
     */
    public boolean isSafeMultithread();
    /**
     * マルチスレッド処理を安全に行うかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isSafe 安全に行う場合、true
     */
    public void setSafeMultithread(boolean isSafe);
    
    /**
     * 呼び出し内容をファイルに保存するかどうかを判定する。<p>
     *
     * @return trueの場合、保存する
     */
    public boolean isSaveCallFile();
    /**
     * 呼び出し内容をファイルに保存するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isSave 保存する場合、true
     */
    public void setSaveRequestFile(boolean isSave);
    
    /**
     * 読み込んだ戻り値内容をキャッシュするかどうかを判定する。<p>
     *
     * @return trueの場合、キャッシュする
     */
    public boolean isCacheReturn();
    
    /**
     * 読み込んだ戻り値内容をキャッシュするかどうかを設定する。<p>
     * デフォルトは、falseで、キャッシュしない。<br>
     *
     * @param isCache キャッシュする場合、true
     */
    public void setCacheReturn(boolean isCache);
    
    /**
     * 呼び出しの引数をファイルに出力する際に使用する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名を設定する。<p>
     *
     * @param className 引数の型
     * @param name Converterサービスのサービス名
     */
    public void setArgumentsConverterServiceName(String className, ServiceName name);
    
    /**
     * 呼び出しの引数をファイルに出力する際に使用する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名を取得する。<p>
     *
     * @param className 引数の型
     * @return Converterサービスのサービス名
     */
    public ServiceName getArgumentsConverterServiceName(String className);
    
    /**
     * 呼び出しの引数の型とそれをファイルに出力する際に使用する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return 引数の型とConverterサービスのサービス名のマッピング
     */
    public Map getArgumentsConverterServiceNames();
    
    /**
     * 呼び出しの戻り値をファイルから読み込む際に使用する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名を設定する。<p>
     *
     * @param className 戻り値の型
     * @param name Converterサービスのサービス名
     */
    public void setReturnConverterServiceName(String className, ServiceName name);
    
    /**
     * 呼び出しの戻り値をファイルから読み込む際に使用する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名を取得する。<p>
     *
     * @param className 戻り値の型
     * @return Converterサービスのサービス名
     */
    public ServiceName getReturnConverterServiceName(String className);
    
    /**
     * 呼び出しの戻り値の型とそれをファイルから読み込む際に使用する{@link jp.ossc.nimbus.util.converter.Converter Converter}サービスのサービス名のマッピングを取得する。<p>
     *
     * @return 戻り値の型とConverterサービスのサービス名のマッピング
     */
    public Map getReturnConverterServiceNames();
}