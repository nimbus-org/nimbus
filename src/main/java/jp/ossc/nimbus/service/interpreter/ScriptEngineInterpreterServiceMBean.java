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
package jp.ossc.nimbus.service.interpreter;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link ScriptEngineInterpreterService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface ScriptEngineInterpreterServiceMBean extends ServiceBaseMBean{
    
    /**
     * 指定されたエンジン名で、javax.script.ScriptEngineFactoryを登録する。<p>
     *
     * @param name エンジン名
     * @param clazz javax.script.ScriptEngineFactoryのクラス
     */
    public void setScriptEngineFactoryByEngineName(String name, Class clazz);
    
    /**
     * スクリプトエンジンを特定するための、拡張子を設定する。<p>
     *
     * @param ext 拡張子
     */
    public void setExtension(String ext);
    
    /**
     * スクリプトエンジンを特定するための、拡張子を取得する。<p>
     *
     * @return 拡張子
     */
    public String getExtension();
    
    /**
     * スクリプトエンジンを特定するための、MIMEタイプを設定する。<p>
     *
     * @param type MIMEタイプ
     */
    public void setMimeType(String type);
    
    /**
     * スクリプトエンジンを特定するための、MIMEタイプを取得する。<p>
     *
     * @return MIMEタイプ
     */
    public String getMimeType();
    
    /**
     * スクリプトエンジンを特定するための、エンジン名を設定する。<p>
     *
     * @param name エンジン名
     */
    public void setEngineName(String name);
    
    /**
     * スクリプトエンジンを特定するための、エンジン名を取得する。<p>
     *
     * @return エンジン名
     */
    public String getEngineName();
    
    /**
     * グローバルスコープ変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 値
     */
    public void setGlobalBinding(String name, Object val);
    
    /**
     * グローバルスコープ変数を取得する。<p>
     *
     * @param name 変数名
     * @return 値
     */
    public Object getGlobalBinding(String name);
    
    /**
     * グローバルスコープ変数マップを取得する。<p>
     *
     * @return 変数マップ
     */
    public Map<String, Object> getGlobalBindings();
    
    /**
     * エンジンスコープ変数を設定する。<p>
     *
     * @param name 変数名
     * @param val 値
     */
    public void setEngineBinding(String name, Object val);
    
    /**
     * エンジンスコープ変数を取得する。<p>
     *
     * @param name 変数名
     * @return 値
     */
    public Object getEngineBinding(String name);
    
    /**
     * エンジンスコープ変数マップを取得する。<p>
     *
     * @return 変数マップ
     */
    public Map<String, Object> getEngineBindings();
    
    /**
     * コンパイル毎にスクリプトエンジンを生成するかどうかを設定する。<p>
     * デフォルトは、false。
     *
     * @param isNew コンパイル毎にスクリプトエンジンを生成する場合、true
     */
    public void setNewScriptEngineByEvaluate(boolean isNew);
    
    /**
     * コンパイル毎にスクリプトエンジンを生成するかどうかを判定する。<p>
     *
     * @return trueの場合、コンパイル毎にスクリプトエンジンを生成する
     */
    public boolean isNewScriptEngineByEvaluate();
    
    /**
     * スクリプトをファンクションでラップするかどうかを設定する。<p>
     * デフォルトは、false。
     *
     * @param isWrap スクリプトをファンクションでラップする場合、true
     * @see #setWrapperFunction(String)
     * @see #setStepDelimitor(String)
     */
    public void setWrapByFunction(boolean isWrap);
    
    /**
     * スクリプトをファンクションでラップするかどうかを判定する。<p>
     *
     * @return trueの場合、スクリプトをファンクションでラップする
     */
    public boolean isWrapByFunction();
    
    /**
     * スクリプトをラップする関数テンプレートを設定する。<p>
     * デフォルトは、
     * <pre>
     * function wrapper(){$script\nreturn $lastStep}\nwrapper();
     * </pre>
     * 関数テンプレートに、$lastStepが含まれる場合は、スクリプトから{@link #setStepDelimitor(String)}で指定されたステップのデリミターを使って、最終ステップとそれ以外に分割し、テンプレート中の$scriptと$lastStepに埋め込む。ステップのデリミターを使った、最終ステップの特定は、厳密な言語仕様の解釈をして、見つける訳ではないため、スクリプトの書き方に注意が必要です。<br>
     * $lastStepが含まれない場合は、テンプレート中の$scriptに埋め込む。<br>
     *
     * @param wrapper スクリプトをラップする関数テンプレート
     */
    public void setWrapperFunction(String wrapper);
    
    /**
     * スクリプトをラップする関数テンプレートを取得する。<p>
     *
     * @return スクリプトをラップする関数テンプレート
     */
    public String getWrapperFunction();
    
    /**
     * スクリプトをラップする関数テンプレートの最終ステップを探索する際に使用するステップのデリミターを設定する。<p>
     * デフォルトは、";"。<br>
     *
     * @param delimitor デリミター
     */
    public void setStepDelimitor(String delimitor);
    
    /**
     * スクリプトをラップする関数テンプレートの最終ステップを探索する際に使用するステップのデリミターを取得する。<p>
     *
     * @return デリミター
     */
    public String getStepDelimitor();
    
    /**
     * コンパイルをしないように設定する。<p>
     * デフォルトは、falseで、コンパイル可能な場合は、コンパイルする。
     *
     * @param isNotCompile コンパイルしない場合は、true
     */
    public void setNotCompile(boolean isNotCompile);
    
    /**
     * コンパイルをしないようかどうかを判定する。<p>
     *
     * @return trueの場合は、コンパイルしない
     */
    public boolean isNotCompile();
    
    /**
     * 指定されたスクリプトをインタプリタ実行する。<br>
     *
     * @return 実行結果
     * @exception EvaluateException インタプリタ実行中に例外が発生した場合
     */
    public Object evaluate(String code) throws EvaluateException;
}