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
package jp.ossc.nimbus.core;

import java.util.Set;

/**
 * {@link DefaultServiceLoadersService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultServiceLoadersService
 */
public interface DefaultServiceLoadersServiceMBean extends ServiceBaseMBean{
    
    /**
     * サービス定義ファイルがあるディレクトリのパスを設定する。<p>
     * 指定されたパスは、絶対パス、相対パス、クラスパス、このサービスを読み込んだサービス定義ファイルの存在するパスからの相対パスで評価される。<br>
     *
     * @param path サービス定義ファイルがあるディレクトリのパス
     */
    public void setServiceDir(String path);
    
    /**
     * サービス定義ファイルがあるディレクトリのパスを取得する。<p>
     *
     * @return サービス定義ファイルがあるディレクトリのパス
     */
    public String getServiceDir();
    
    /**
     * サービス定義ファイルがあるディレクトリのサービス定義ファイルを特定するファイル名の正規表現を設定する。<p>
     *
     * @param regex サービス定義ファイル名を特定する正規表現。フィルターの指定方法は、{@link RecurciveSearchFile#listAllTreeFiles(String, int)}を参照。
     */
    public void setServiceFileFilter(String regex);
    
    /**
     * サービス定義ファイルがあるディレクトリのサービス定義ファイルを特定するファイル名の正規表現を取得する。<p>
     *
     * @return サービス定義ファイルを特定するファイル名の正規表現
     */
    public String getServiceFileFilter();
    
    /**
     * ServiceLoaderをサービスとして登録するかどうかを設定する。<p>
     * デフォルトは、falseで登録しない。<br>
     *
     * @param isManage 登録する場合は、true
     */
    public void setManagedServiceLoader(boolean isManage);
    
    /**
     * ServiceLoaderをサービスとして登録するかどうかを判定する。<p>
     *
     * @return trueの場合、登録する
     */
    public boolean isManagedServiceLoader();
    
    /**
     * {@link jp.ossc.nimbus.service.crypt.Crypt Crypt}サービスのサービス名を設定する。<p>
     *
     * @param name Cryptサービスのサービス名
     */
    public void setCryptServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.crypt.Crypt Crypt}サービスのサービス名を取得する。<p>
     *
     * @return Cryptサービスのサービス名
     */
    public ServiceName getCryptServiceName();
    
    /**
     * サービス定義XMLファイルをDTDで検証するかどうかを指定する。<p>
     * デフォルトでは、検証しない。<br>
     *
     * @param validate 検証する場合true。
     */
    public void setValidate(boolean validate);
    
    /**
     * サービス定義XMLファイルをDTDで検証するかどうかを調べる。<p>
     *
     * @return 検証する場合true。
     */
    public boolean isValidate();
    
    /**
     * ここまでにロードしたサービスが全て正常に開始できているかをチェックするかどうかを設定する。<p>
     * この属性をtrueにしておくと、このServiceLoaderの起動完了時に、{@link ServiceManagerFactory#checkLoadManagerCompleted()}を呼び出す。
     *
     * @param isCheck チェックする場合true
     */
    public void setCheckLoadManagerCompleted(boolean isCheck);
    
    /**
     * ここまでにロードしたサービスが全て正常に開始できているかをチェックするかどうかを調べる。<p>
     *
     * @return チェックする場合true
     */
    public boolean isCheckLoadManagerCompleted();
    
    /**
     * 指定したマネージャのサービスが全て正常に開始できているかをチェックするかどうかを設定する。<p>
     * この属性を指定すると、このServiceLoaderの起動完了時に、{@link ServiceManagerFactory#checkLoadManagerCompletedBy(Set)}を呼び出す。
     *
     * @param managerNames チェックするマネージャ名の集合
     */
    public void setCheckLoadManagerCompletedBy(String[] managerNames);
    
    /**
     * サービスが全て正常に開始できているかをチェックするマネージャ名の集合を取得する。<p>
     *
     * @return チェックするマネージャ名の集合
     */
    public String[] getCheckLoadManagerCompletedBy();
    
    /**
     * &lt;manager&gt;要素を表す{@link ServiceManager}インタフェースの実装クラス名を設定する。<p>
     * クラス名は、完全修飾名で設定する。<br>
     *
     * @param className ServiceManagerインタフェースの実装クラス名
     * @see #getServiceManagerClassName()
     * @see ServiceManager
     */
    public void setServiceManagerClassName(String className);
    
    /**
     * &lt;manager&gt;要素を表す{@link ServiceManager}インタフェースの実装クラス名を取得する。<p>
     *
     * @return ServiceManagerインタフェースの実装クラス名
     * @see #setServiceManagerClassName(String)
     * @see ServiceManager
     */
    public String getServiceManagerClassName();
}