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
package jp.ossc.nimbus.service.converter;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link BeanFlowConverterService}サービスMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface BeanFlowConverterServiceMBean extends ServiceBaseMBean {
    
    /**
     * 入力Beanのクラス名に対して使用するBeanFlowのマッピングを設定する。<p>
     *
     * @param mapping 入力Beanのクラス名=BeanFlowのキーで構成されるマップ
     */
    public void setClassMapping(Map mapping);
    
    /**
     * 入力Beanのクラス名に対して使用するBeanFlowのマッピングを取得する。<p>
     *
     * @return 入力Beanのクラス名=BeanFlowのキーで構成されるマップ
     */
    public Map getClassMapping();
    
    /**
     * 入力Beanに対する条件毎に使用するBeanFlowのマッピングを設定する。<p>
     * 条件式は、The Apache Jakarta Projectの Commons Jexl(http://jakarta.apache.org/commons/jexl/)を使用する。<br>
     * 入力Bean自体を参照する場合は、"value"という予約語を使用する。<br>
     * 入力Beanのプロパティを参照する場合は、プロパティを表現する文字列を"@"で囲んで指定する。ここで言う、プロパティの概念は、Java Beansのプロパティの概念より広く、{@link jp.ossc.nimbus.beans.PropertyFactory PropertyFactory}の規約に従う。<br>
     *
     * @param conditions 入力Beanに対する条件式=BeanFlowのキーで構成される条件配列
     */
    public void setConditions(String[] conditions);
    
    /**
     * 入力Beanに対する条件毎に使用するBeanFlowのマッピングを取得する。<p>
     *
     * @return 入力Beanに対する条件式=BeanFlowのキーで構成される条件配列
     */
    public String[] getConditions();
    
    /**
     * どの条件にも合致しない場合に使用するBeanFlowのキーを設定する。<p>
     *
     * @param beanFlowKey BeanFlowのキー
     */
    public void setDefaultBeanFlowKey(String beanFlowKey);
    
    /**
     * どの条件にも合致しない場合に使用するBeanFlowのキーを取得する。<p>
     *
     * @return BeanFlowのキー
     */
    public String getDefaultBeanFlowKey();
    
    /**
     * Beanの変換を行うBeanFlowを取得する{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を設定する。<p>
     *
     * @param name BeanFlowInvokerFactoryサービスのサービス名
     */
    public void setBeanFlowInvokerFactoryServiceName(ServiceName name);
    
    /**
     * Beanの変換を行うBeanFlowを取得する{@link jp.ossc.nimbus.service.beancontrol.interfaces.BeanFlowInvokerFactory BeanFlowInvokerFactory}サービスのサービス名を取得する。<p>
     *
     * @return BeanFlowInvokerFactoryサービスのサービス名
     */
    public ServiceName getBeanFlowInvokerFactoryServiceName();
}