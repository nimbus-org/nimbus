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
package jp.ossc.nimbus.service.soap;

import java.net.URL;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link JaxRpcServiceFactoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface JaxRpcServiceFactoryServiceMBean extends ServiceBaseMBean {
    
    /**
     * WSDL URLを取得する。<p>
     * 
     * @return WSDL URL
     */
    public URL getWsdlURL();
    
    /**
     * WSDL URLを設定する。<p>
     * 
     * @param url WSDLのURL
     */
    public void setWsdlURL(URL url);
    
    /**
     * WSDLファイルのパスを取得する。<p>
     * 
     * @return WSDLファイルのパス
     */
    public String getWsdlPath();
    
    /**
     * WSDLファイルのパスを設定する。<p>
     * 
     * @param path WSDLファイルのパス
     */
    public void setWsdlPath(String path);
    
    /**
     * JAX-RPCサービス名を取得する。
     * 
     * @return JAX-RPCサービス名
     */
    public String getJaxRpcServiceName();

    /**
     * JAX-RPCサービス名を設定する。<p>
     * 
     * @param jaxRpcServiceName JAX-RPCサービス名
     */
    public void setJaxRpcServiceName(String jaxRpcServiceName);
    
    /**
     * ネームスペース名を取得する。<p>
     * 
     * @return ネームスペース名
     */
    public String getNameSpace();
    
    /**
     * ネームスペースを設定する。<p>
     * 
     * @param nameSpace ネームスペース
     */
    public void setNameSpace(String nameSpace);
    
    /**
     * JAX-RPCサービスに登録する型マッピングを設定する。<p>
     *
     * @param encodingStyleURI エンコードを特定するURI
     * @param mapping 型マッピング
     */
    public void setTypeMapping(String encodingStyleURI, TypeMapping mapping);
    
    /**
     * 指定したURIで特定されるJAX-RPCサービスに登録する型マッピングを取得する。<p>
     *
     * @param encodingStyleURI エンコードを特定するURI
     * @return 型マッピング
     */
    public TypeMapping getTypeMapping(String encodingStyleURI);
    
    public void setServiceFactoryClassName(String name);
    public String getServiceFactoryClassName();
}
