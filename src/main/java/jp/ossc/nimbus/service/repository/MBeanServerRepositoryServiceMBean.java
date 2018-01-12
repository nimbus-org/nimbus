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
package jp.ossc.nimbus.service.repository;

import jp.ossc.nimbus.core.*;

/**
 * {@link MBeanServerRepositoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface MBeanServerRepositoryServiceMBean
 extends ServiceBaseMBean, Repository{
    
    /**
     * JMXサーバのドメイン名を設定する。<p>
     * javax.management.MBeanServerFactory.findMBeanServer(String)の引数として使用する。デフォルトは、null。<br>
     *
     * @param domain JMXサーバのドメイン名
     */
    public void setMBeanServerDomain(String domain);
    
    /**
     * JMXサーバのドメイン名を取得する。<p>
     *
     * @return JMXサーバのドメイン名
     */
    public String getMBeanServerDomain();
    
    /**
     * JMXサーバのデフォルトドメイン名を設定する。<p>
     * javax.management.MBeanServer.getDefaultDomain()と比較して、JMXサーバを特定する。デフォルトは、null。<br>
     *
     * @param domain JMXサーバのデフォルトドメイン名
     */
    public void setMBeanServerDefaultDomain(String domain);
    
    /**
     * JMXサーバのデフォルトドメイン名を取得する。<p>
     *
     * @return JMXサーバのデフォルトドメイン名
     */
    public String getMBeanServerDefaultDomain();
    
    /**
     * JMXサーバリストのインデックスを設定する。<p>
     * javax.management.MBeanServerFactory.findMBeanServer(String)の戻り値となるListのインデックスを指定する。デフォルトは、0。<br>
     *
     * @param index JMXサーバリストのインデックス
     */
    public void setMBeanServerIndex(int index);
    
    /**
     * JMXサーバリストのインデックスを取得する。<p>
     *
     * @return JMXサーバリストのインデックス
     */
    public int getMBeanServerIndex();
    
    /**
     * MBeanをJMXサーバに登録する際のドメイン名を設定する。<p>
     * デフォルトは、このサービスが登録されているマネージャ名。<br>
     *
     * @param domain ドメイン名
     */
    public void setObjectNameDomain(String domain);
    
    /**
     * MBeanをJMXサーバに登録する際のドメイン名を取得する。<p>
     *
     * @return ドメイン名
     */
    public String getObjectNameDomain();
    
    /**
     * JMXサーバが見つからない時に、JMXサーバを生成するかどうかを設定する。<p>
     * デフォルトは、falseで生成しない。<br>
     *
     * @param isCreate 生成する場合は、true
     */
    public void setCreateMBeanServer(boolean isCreate);
    
    /**
     * JMXサーバが見つからない時に、JMXサーバを生成するかどうかを判定する。<p>
     *
     * @return trueの場合は、生成する
     */
    public boolean isCreateMBeanServer();
}
