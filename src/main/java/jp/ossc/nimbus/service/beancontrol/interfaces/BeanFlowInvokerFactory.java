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
package jp.ossc.nimbus.service.beancontrol.interfaces;

import java.util.Set;

/**
 * {@link BeanFlowInvoker}のファクトリインタフェース。<p>
 * 
 * @author H.Nakano
 */
public interface BeanFlowInvokerFactory{
    
    /** BLFLOWファイル拡張子。 */
    public static final String FLOW_FILE_EXTENTION = ".xml";
    
    /**
     * 指定したBeanフローを実行する{@link BeanFlowInvoker}を生成する。<p>
     * 
     * @param key Beanフローキー
     * @return 指定したBeanフローを実行するBeanFlowInvoker
     */
    public BeanFlowInvoker createFlow(String key);
    
    /**
     * 指定したBeanフローを実行する{@link BeanFlowInvoker}を生成する。<p>
     * 
     * @param key Beanフローキー
     * @return 指定したBeanフローを実行するBeanFlowInvoker
     */
    public BeanFlowInvoker createFlow(String key, String caller, boolean isOverwride);
    
    /**
     * このファクトリが管理しているBeanフロー名の集合を取得する。<p>
     *
     * @return Beanフロー名の集合
     */
    public Set getBeanFlowKeySet();
    
    /**
     * 指定されたBeanフローをこのファクトリが管理しているかどうかを判定する。<p>
     *
     * @param key Beanフローキー
     * @return このファクトリが管理している場合true
     */
    public boolean containsFlow(String key);
}
