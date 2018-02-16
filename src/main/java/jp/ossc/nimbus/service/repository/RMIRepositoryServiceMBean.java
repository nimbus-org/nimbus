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
 * {@link RMIRepositoryService}のMBeanインタフェース。<p>
 *
 * @author M.Takata
 */
public interface RMIRepositoryServiceMBean
 extends ServiceBaseMBean, Repository{
    
    /**
     * RMIサーバのホスト名を設定する。<p>
     * 指定しない場合は、ローカルホスト。<br>
     *
     * @param host ホスト名
     */
    public void setHostName(String host);
    
    /**
     * RMIサーバのホスト名を取得する。<p>
     *
     * @return ホスト名
     */
    public String getHostName();
    
    /**
     * RMIサーバのポート番号を設定する。<p>
     * 指定しない場合は、1099。<br>
     *
     * @param port ポート番号
     */
    public void setPort(int port);
    
    /**
     * RMIサーバのポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getPort();
    
    /**
     * ローカルにRMIサーバを作成するかどうかを判定する。<p>
     *
     * @return trueの場合、作成する
     */
    public boolean isCreateRegistry();
    
    /**
     * ローカルにRMIサーバを作成するかどうかを設定する。<p>
     * デフォルトは、falseで作成しない。<br>
     *
     * @param isCreate 作成する場合、true
     */
    public void setCreateRegistry(boolean isCreate);
}