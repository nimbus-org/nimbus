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

/**
 * {@link GenericsFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see GenericsFactoryService
 */
public interface GenericsFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * 生成するオブジェクトのクラスを設定する。<p>
     *
     * @param clazz 生成するオブジェクトのクラス
     */
    public void setInstantiateClass(Class clazz);
    
    /**
     * 生成するオブジェクトのクラスを取得する。<p>
     *
     * @return 生成するオブジェクトのクラス
     */
    public Class getInstantiateClass();
    
    /**
     * {@link ServiceName}型の属性が設定された場合に、生成するオブジェクトの該当する属性に、該当するサービスを取得して設定するかどうかを設定する。<p>
     *
     * @param flg インジェクションする場合はtrue
     */
    public void setServiceInjection(boolean flg);
    
    /**
     * {@link ServiceName}型の属性が設定された場合に、生成するオブジェクトの該当する属性に、該当するサービスを取得して設定するかどうかを判定する。<p>
     *
     * @return trueの場合は、インジェクションする
     */
    public boolean isServiceInjection();
    
    /**
     * 指定された属性の値を取得する。<p>
     *
     * @param attributeName 属性名
     * @return 指定された属性の値
     */
    public Object getAttribute(String attributeName);
    
    /**
     * 指定されたサービス名属性のサービスを取得する。<p>
     *
     * @param attributeName サービス名属性名
     * @return 指定されたサービス名属性のサービス
     * @exception ServiceNotFoundException 指定されたサービス名属性のサービスが見つからない場合
     */
    public Service getService(String attributeName)
     throws ServiceNotFoundException;
    
    /**
     * 指定されたサービス名属性のサービスオブジェクトを取得する。<p>
     *
     * @param attributeName サービス名属性名
     * @return 指定されたサービス名属性のサービスオブジェクト
     * @exception ServiceNotFoundException 指定されたサービス名属性のサービスオブジェクトが見つからない場合
     */
    public Object getServiceObject(String attributeName)
     throws ServiceNotFoundException;
}