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
package jp.ossc.nimbus.service.trade;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultTradeSimulatorFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DefaultTradeSimulatorFactoryService
 */
public interface DefaultTradeSimulatorFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * 空売りの売買サイン判定を行うかどうかを設定する。<p>
     * 
     * @param isShort 空売りの場合、true
     */
    public void setShortSelling(boolean isShort);
    
    /**
     * 空売りの売買サイン判定を行うかどうかを判定する。<p>
     * 
     * @return trueの場合、空売り
     */
    public boolean isShortSelling();
    
    /**
     * 取引開始のサインが発生して取引を開始するまでの間を設定する。<p>
     * デフォルトは、0で、サイン発生時に取引を開始する。<br>
     *
     * @param margin 取引を開始するまでの間となる時系列要素の本数
     */
    public void setTradeStartMargin(int margin);
    
    /**
     * 取引開始のサインが発生して取引を開始するまでの間を取得する。<p>
     *
     * @return 取引を開始するまでの間となる時系列要素の本数
     */
    public int getTradeStartMargin();
    
    /**
     * 取引終了のサインが発生して取引を終了するまでの間を設定する。<p>
     * デフォルトは、0で、サイン発生時に取引を終了する。<br>
     *
     * @param margin 取引を終了するまでの間となる時系列要素の本数
     */
    public void setTradeEndMargin(int margin);
    
    /**
     * 取引終了のサインが発生して取引を終了するまでの間を取得する。<p>
     *
     * @return 取引を終了するまでの間となる時系列要素の本数
     */
    public int getTradeEndMargin();
}
