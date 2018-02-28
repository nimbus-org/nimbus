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
package jp.ossc.nimbus.service.semaphore;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultSemaphoreServiceFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultSemaphoreServiceFactoryService
 */
public interface DefaultSemaphoreServiceFactoryServiceMBean
 extends FactoryServiceBaseMBean{
    
    /**
     * セマフォ実装クラス名を設定する。<p>
     *
     * @param name セマフォ実装クラス名
     */
    public void setSemaphoreClassName(String name);
    
    /**
     * セマフォ実装クラス名を取得する。<p>
     *
     * @return セマフォ実装クラス名
     */
    public String getSemaphoreClassName();
    
    /**
     * セマフォのリソース総数を返す。<p>
     *
     * @return リソース総数
     */
    public int getResourceCapacity();
    
    /**
     * セマフォのリソース総数を設定する。<p>
     *
     * @param capa リソース総数
     */
    public void setResourceCapacity(int capa);
    
    /**
     * セマフォ獲得待ちスレッドを開放し、セマフォ獲得待ちを受け付けないようにする。<p>
     */
    public void release();
    
    /**
     * セマフォ獲得待ちの受付を開始する。<p>
     * {@link #release()}呼出し後に、セマフォ獲得待ちを受け付けるようにする。
     */
    public void accept();
}
