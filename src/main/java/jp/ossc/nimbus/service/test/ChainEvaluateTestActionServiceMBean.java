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
package jp.ossc.nimbus.service.test;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.service.test.ChainTestAction.TestActionProcess;
import jp.ossc.nimbus.service.test.ChainEvaluateTestAction.EvaluateTestActionProcess;

/**
 * {@link ChainEvaluateTestActionService}のMBeanインタフェース<p>
 * 
 * @author M.Ishida
 * @see ChainEvaluateTestActionService
 */
public interface ChainEvaluateTestActionServiceMBean extends ServiceBaseMBean {
    
    /**
     * 連鎖させるテストアクションサービスのサービス名の配列を取得する。<p>
     *
     * @return テストアクションサービスのサービス名の配列
     */
    public ServiceName[] getActionServiceNames();
    
    /**
     * 連鎖させるテストアクションサービスのサービス名の配列を設定する。<p>
     * 連鎖可能なテストアクションは、以下。<br>
     * <ul>
     *     <li>{@link TestAction}</li>
     *     <li>{@link EvaluateTestAction}</li>
     *     <li>{@link TestActionProcess}</li>
     *     <li>{@link EvaluateTestActionProcess}</li>
     * </ul>
     *
     * @param names テストアクションサービスのサービス名の配列
     */
    public void setActionServiceNames(ServiceName[] names);
    
    /**
     * 連鎖の最後の評価テストアクションサービスのサービス名を取得する。<p>
     *
     * @return 評価テストアクションサービスのサービス名
     */
    public ServiceName getEndEvaluateTestActionServiceName();
    
    /**
     * 連鎖の最後の評価テストアクションサービスのサービス名を設定する。<p>
     * 設定可能な評価テストアクションは、以下。<br>
     * <ul>
     *     <li>{@link EvaluateTestAction}</li>
     *     <li>{@link EvaluateTestActionProcess}</li>
     * </ul>
     *
     * @param name 評価テストアクションサービスのサービス名
     */
    public void setEndEvaluateTestActionServiceName(ServiceName name);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     * 
     * @return 連鎖されたテストアクションの想定コストの総和
     */
    public double getExpectedCost();
}
