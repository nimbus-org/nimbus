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
package jp.ossc.nimbus.service.jndi;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link DummyJndiFinderService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DummyJndiFinderService
 */
public interface DummyJndiFinderServiceMBean extends ServiceBaseMBean{
    
    public void setJndiMapping(String jndiName, Object obj);
    
    public void setJndiMappingServiceName(String jndiName, ServiceName name);
    
    public void setJndiFinderServiceName(ServiceName name);
    public ServiceName getJndiFinderServiceName();
    
    /**
     * lookup時に使用するJNDIプレフィックスを設定する。<p>
     * デフォルトは、空文字。<br>
     *
     * @param prefix JNDIプレフィックス
     */
    public void setPrefix(String prefix);
    
    /**
     * lookup時に使用するJNDIプレフィックスを取得する。<p>
     *
     * @return JNDIプレフィックス
     */
    public String getPrefix();
    
    /**
     * キャッシュしたリモートオブジェクトを全てクリアする。<p>
     */
    public void clearCache();
    
    /**
     * 指定したJNDI名のしたリモートオブジェクトのキャッシュをクリアする。<p>
     * 
     * @param jndiName キャッシュから削除するリモートオブジェクトのJNDI名
     */
    public void clearCache(String jndiName);
}
