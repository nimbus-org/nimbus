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
package jp.ossc.nimbus.service.naming;

import jp.ossc.nimbus.core.*;

/**
 * {@link ServiceNamingService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ServiceNamingService
 */
public interface ServiceNamingServiceMBean extends Service, Naming{
    
    /**
     * {@link jp.ossc.nimbus.core.Service Service}の検索パスを設定する。<p>
     * ここで指定する文字列配列には、{@link jp.ossc.nimbus.core.ServiceManager ServiceManager}を表す&lt;manager&gt;要素の名前配列を指定する。自分自身が登録されているServiceManagerからServiceの取得を試みた後、指定した名前配列の順で、該当するServiceManagerからServiceの取得を試みる。自分自身が登録されているServiceManagerよりも優先して検索したいServiceManagerがある場合は、{@link #setBootServicePath(String[])}で設定する必要がある。<br>
     *
     * @param path &lt;manager&gt;要素の名前配列
     * @see #getServicePath()
     * @see #setBootServicePath(String[])
     */
    public void setServicePath(String[] path);
    
    /**
     * {@link jp.ossc.nimbus.core.Service Service}の検索パスを取得する。<p>
     * 
     * @return Serviceの検索パスとなる&lt;manager&gt;要素の名前配列
     * @see #setServicePath(String[])
     */
    public String[] getServicePath();
    
    /**
     * {@link jp.ossc.nimbus.core.Service Service}のブート検索パスを設定する。<p>
     * ここで指定する文字列配列には、{@link jp.ossc.nimbus.core.ServiceManager ServiceManager}を表す&lt;manager&gt;要素の名前配列を指定する。指定した名前配列の順で、該当するServiceManagerからServiceの取得を試みる。その後、自分自身が登録されているServiceManagerからServiceの取得を試みる。自分自身が登録されているServiceManagerの後に検索したいServiceManagerがある場合は、{@link #setServicePath(String[])}で設定する必要がある。<br>
     *
     * @param path &lt;manager&gt;要素の名前配列
     * @see #getBootServicePath()
     * @see #setServicePath(String[])
     */
    public void setBootServicePath(String[] path);
    
    /**
     * {@link jp.ossc.nimbus.core.Service Service}のブート検索パスを取得する。<p>
     *
     * @return Serviceの検索パスとなる&lt;manager&gt;要素の名前配列
     * @see #setBootServicePath(String[])
     */
    public String[] getBootServicePath();
    
    /**
     * {@link jp.ossc.nimbus.core.Service Service}を参照名で参照する{@link ServiceNameRef}の配列を設定する。<p>
     * ServiceNameRefを&lt;attribute&gt;要素で指定するには、以下のフォーマットで指定する。<br>
     * <pre>
     * [参照名]=[&lt;manager&gt;要素のname属性の値]#[&lt;service&gt;要素のname属性の値]
     * </pre>
     * '='の右側の文字列は、ServiceNameの指定方法に準拠する。<br>
     * 更に、配列の指定は、改行して上記の指定を複数行う。<br>
     * この指定によって、{@link #find(String)}に参照名を指定する事で、対応するServiceを取得する事ができる。<br>
     *
     * @param refs ServiceNameRefの配列
     * @see #getServiceNameReferences()
     */
    public void setServiceNameReferences(ServiceNameRef[] refs);
    
    /**
     * {@link jp.ossc.nimbus.core.Service Service}を参照名で参照する{@link ServiceNameRef}の配列を取得する。<p>
     *
     * @return ServiceNameRefの配列
     * @see #setServiceNameReferences(ServiceNameRef[])
     */
    public ServiceNameRef[] getServiceNameReferences();
}