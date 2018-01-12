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
package jp.ossc.nimbus.service.test.stub.http;

import java.io.File;

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.http.proxy.HttpProcessServiceBaseMBean;
import jp.ossc.nimbus.service.test.TestStub;

/**
 * {@link HttpTestStubService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see HttpTestStubService
 */
public interface HttpTestStubServiceMBean extends HttpProcessServiceBaseMBean, TestStub{
    
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
     * HTTPレスポンスに指定するHTTPバージョンを設定する。<p>
     *
     * @param version HTTPバージョン
     */
    public void setHttpVersion(String version);
    
    /**
     * HTTPレスポンスに指定するHTTPバージョンを取得する。<p>
     *
     * @return HTTPバージョン
     */
    public String getHttpVersion();
    
    /**
     * 共通で設定するHTTPレスポンスヘッダを設定する。<p>
     *
     * @param name ヘッダ名
     * @param values ヘッダ値の配列
     */
    public void setHttpHeaders(String name, String[] values);
    
    /**
     * 共通で設定するHTTPレスポンスヘッダを取得する。<p>
     *
     * @param name ヘッダ名
     * @return ヘッダ値の配列
     */
    public String[] getHttpHeaders(String name);
    
    /**
     * 共通で設定するHTTPレスポンスヘッダ文字列を取得する。<p>
     *
     * @return HTTPヘッダ文字列
     */
    public String getHttpHeader();
    
    /**
     * バイナリで指定するレスポンスファイルの拡張子を設定する。<p>
     *
     * @param exts 拡張子の配列
     */
    public void setBinaryFileExtensions(String[] exts);
    
    /**
     * バイナリで指定するレスポンスファイルの拡張子を取得する。<p>
     *
     * @return 拡張子の配列
     */
    public String[] getBinaryFileExtensions();
    
    /**
     * 同じリクエストの繰り返しを許すかどうかを判定する。<p>
     *
     * @return trueの場合、許す
     */
    public boolean isAllowRepeatRequest();
    /**
     * 同じリクエストの繰り返しを許すかどうかを設定する。<p>
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
     * リクエストをファイルに保存するかどうかを判定する。<p>
     *
     * @return trueの場合、保存する
     */
    public boolean isSaveRequestFile();
    /**
     * リクエストをファイルに保存するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isSave 保存する場合、true
     */
    public void setSaveRequestFile(boolean isSave);
    
    /**
     * 読み込んだレスポンスをキャッシュするかどうかを判定する。<p>
     *
     * @return trueの場合、キャッシュする
     */
    public boolean isCacheResponse();
    
    /**
     * 読み込んだレスポンスをキャッシュするかどうかを設定する。<p>
     * デフォルトは、falseで、キャッシュしない。<br>
     *
     * @param isCache キャッシュする場合、true
     */
    public void setCacheResponse(boolean isCache);
}
