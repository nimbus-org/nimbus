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
package jp.ossc.nimbus.service.keepalive;

/**
 * ラウンドロビンで{@link KeepAliveChecker}を選択する{@link KeepAliveCheckerSelector}実装クラス。<p>
 * {@link #getSelectableCheckers()}で取得できる選択可能なKeepAliveChecker配列から、順次KeepAliveCheckerを選択する。<br>
 *
 * @author M.Takata
 */
public class RoundRobinKeepAliveCheckerSelectorService
 extends AbstractKeepAliveCheckerSelectorService{
    
    private static final long serialVersionUID = -7294639858571881506L;
    
    private volatile int serverIndex;
    
    /**
     * ラウンドロビンで{@link KeepAliveChecker}を取得する。<p>
     * {@link #getSelectableCheckers()}で取得できる選択可能なKeepAliveChecker配列から、順次KeepAliveCheckerを選択する。<br>
     * {@link #getSelectableCheckers()}で取得できる選択可能なKeepAliveChecker配列が長さ0の配列の場合は、nullを返す。<br>
     * 
     * @return KeepAliveCheckerオブジェクト
     */
    public KeepAliveChecker selectChecker(){
        final KeepAliveChecker[] servers = getSelectableCheckers();
        synchronized(this){
            if(servers == null || servers.length == 0){
                return null;
            }else if(servers.length > serverIndex){
                return servers[serverIndex++];
            }else{
                serverIndex = 0;
                return servers[serverIndex++];
            }
        }
    }
}
