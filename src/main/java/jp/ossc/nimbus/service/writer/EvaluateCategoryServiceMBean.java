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

import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.writer.SimpleCategoryServiceMBean;

/**
 * {@link EvaluateCategoryService}サービスMBeanインタフェース。<p>
 *
 * @author M.Kameda
 */
public interface EvaluateCategoryServiceMBean
 extends SimpleCategoryServiceMBean{
    
    /**
     * カテゴリを使用する際の条件式を設定する。<p>
     * 条件式は、booleanを返す必要がある。<br>
     * 条件式中では、valueで{@link Category#write(Object)}の引数が参照できる。また、その引数のプロパティにアクセスする場合には、@で囲んでプロパティ名を指定することができる。<br>
     * {@link #setThreadContextServiceName(ServiceName)}または、{@link #setCodeMasterFinderServiceName(ServiceName)}で、コードマスタが参照できる設定にしている場合は、masterでコードマスタのマップを参照できる。また、{@link #setCodeMasterName(String)}で、マスタを１つに絞っている場合は、masterで、そのマスタのみを参照できる。<br>
     *
     * @param conditions 条件式の配列
     */
    public void setWritableConditions(String conditions[]);
    
    /**
     * 設定された条件式を取得する。<p>
     * 
     * @return 設定された条件式の配列
     */
    public String[] getWritableConditions();
    
    /**
     * サービスの開始時に、条件式を評価してみるかどうかを設定する。<p>
     * デフォルトでは、trueで評価する。<br>
     * 
     * @param isTest 評価する場合は、true
     */
    public void setTestOnStart(boolean isTest);
    
    /**
     * サービスの開始時に、条件式を評価してみるかどうかを判定する。<p>
     * 
     * @return trueの場合は、評価する
     */
    public boolean isTestOnStart();
    
    /**
     * 評価式を評価する{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を設定する。<p>
     * 設定しない場合は、The Apache Jakarta Projectの<a href="http://jakarta.apache.org/commons/jexl/">Commons Jexl</a>を使用する。<br>
     * 
     * @param name Interpreterサービスのサービス名
     */
    public void setInterpreterServiceName(ServiceName name);
    
    /**
     * 評価式を評価する{@link jp.ossc.nimbus.service.interpreter.Interpreter Interpreter}サービスのサービス名を取得する。<p>
     * 
     * @return Interpreterサービスのサービス名
     */
    public ServiceName getInterpreterServiceName();
    
    /**
     * コードマスタを取得する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を設定する。<p>
     * 
     * @param name Contextサービスのサービス名
     */
    public void setThreadContextServiceName(ServiceName name);
    
    /**
     * コードマスタを取得する{@link jp.ossc.nimbus.service.context.Context Context}サービスのサービス名を取得する。<p>
     * 
     * @return Contextサービスのサービス名
     */
    public ServiceName getThreadContextServiceName();
    
    /**
     * コードマスタを取得する{@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を設定する。<p>
     * 
     * @param name CodeMasterFinderサービスのサービス名
     */
    public void setCodeMasterFinderServiceName(ServiceName name);
    
    /**
     * コードマスタを取得する{@link jp.ossc.nimbus.service.codemaster.CodeMasterFinder CodeMasterFinder}サービスのサービス名を取得する。<p>
     * 
     * @return CodeMasterFinderサービスのサービス名
     */
    public ServiceName getCodeMasterFinderServiceName();
    
    /**
     * コードマスタのマスタ名を設定する。<p>
     *
     * @param name コードマスタのマスタ名
     */
    public void setCodeMasterName(String name);
    
    /**
     * コードマスタのマスタ名を取得する。<p>
     *
     * @return コードマスタのマスタ名
     */
    public String getCodeMasterName();
}
