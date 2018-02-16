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
package jp.ossc.nimbus.service.writer;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import java.util.*;

/**
 * {@link WritableRecordFactoryService}のMBeanインタフェース。<p>
 * 
 * @author Y.Tokuda
 */
public interface WritableRecordFactoryServiceMBean extends ServiceBaseMBean {
    
    /**
     * {@link WritableRecordFactory#createRecord(Object)}で指定されたマップのキーに対するWritableElement実装クラスのマッピングを指定する。<p>
     * このメソッドで指定されなかったキーに対しては、{@link SimpleElement}がマッピングされる。<br>
     * 
     * @param prop キーとWritableElement実装クラスのマッピング
     */
    public void setImplementClasses(Properties prop);
    
    /**
     * {@link WritableRecordFactory#createRecord(Object)}で指定されたマップのキーに対するWritableElement実装クラスのマッピングを取得する。<p>
     * 
     * @return キーとWritableElement実装クラスのマッピング
     */
    public Properties getImplementClasses();
    
    /**
     * {@link WritableRecordFactory#createRecord(Object)}で指定されたマップのキーに対するWritableElement実装サービスのマッピングを指定する。<p>
     * 一度の{@link WritableRecordFactory#createRecord(Object)}で、同一のキーが複数回出現する場合や、マルチスレッドで呼び出す場合は、あるキーに対するWritableElementインスタンスは、その都度生成される必要があるので、{@link jp.ossc.nimbus.core.FactoryService FactoryService}を実装したサービスを使用するか、service要素のinstance属性でfactoryを指定すること。<br>
     * このメソッドで指定されなかったキーに対しては、{@link SimpleElement}がマッピングされる。<br>
     * 
     * @param prop キーとWritableElement実装サービスのマッピング
     */
    public void setImplementServiceNames(Properties prop);
    
    /**
     * {@link WritableRecordFactory#createRecord(Object)}で指定されたマップのキーに対するWritableElement実装サービスのマッピングを取得する。<p>
     * 
     * @return キーとWritableElement実装サービスのマッピング
     */
    public Properties getImplementServiceNames();
    
    /**
     * {@link WritableRecordFactory#createRecord(Object)}で指定されたマップの値のフォーマットを設定する。<p>
     * フォーマットには、{@link WritableRecordFactory#createRecord(Object)}で指定されたマップのキーと、任意の文字列を使用できる。キーは、"%"で囲む。"%"をキーのセパレータ以外の文字として使いたい場合は、"\"を前に付けてエスケープする。"\"をエスケープ文字以外として使いたい場合は、"\"を2回重ねる。<br>
     * <pre>
     *  例：%DATE%,%MESSAGE%
     * </pre>
     *
     * @param fmt フォーマット文字列
     */
    public void setFormat(String fmt);
    
    /**
     * {@link WritableRecordFactory#createRecord(Object)}で指定されたマップの値のフォーマットを取得する。<p>
     *
     * @return フォーマット文字列
     */
    public String getFormat();
}
