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
package jp.ossc.nimbus.service.codemaster;

import jp.ossc.nimbus.lang.ServiceException;
import java.util.*;
/**
 * コードマスター取得インターフェイス
 * @version $Name:  $
 * @author H.Nakano
 * @since 1.0
 */
public interface CodeMasterFinder {
	/**
	 * 現在時点でのコードマスターオブジェクトのマップを返却する
	 * @return	コードマスターの入ったMap
	 * @throws ServiceException
	 */
	public Map getCodeMasters() throws ServiceException;
    
    /**
     * 登録されている全コードマスタを更新する。<p>
     *
     * @exception Exception コードマスタの更新に失敗した場合
     */
    public void updateAllCodeMasters() throws Exception;

    /**
     * コードマスタを更新する。<p>
     *
     * @param key コードマスタ名
     * @exception Exception コードマスタの更新に失敗した場合
     */
    public void updateCodeMaster(String key) throws Exception;
    
    /**
     * コードマスタを更新する。<p>
     *
     * @param key コードマスタ名
     * @param updateTime 更新時刻。nullを指定した場合は、即時更新
     * @exception Exception コードマスタの更新に失敗した場合
     */
    public void updateCodeMaster(String key, Date updateTime) throws Exception;
    
    /**
     * コードマスタを更新する。<p>
     *
     * @param key コードマスタ名
     * @param input 更新に必要な入力。nullも可
     * @param updateTime 更新時刻。nullを指定した場合は、即時更新
     * @exception Exception コードマスタの更新に失敗した場合
     */
    public void updateCodeMaster(String key, Object input, Date updateTime) throws Exception;
    
    /**
     * コードマスタ名の集合を取得する。<p>
     *
     * @return コードマスタ名の集合
     */
    public Set getCodeMasterNameSet();
}
