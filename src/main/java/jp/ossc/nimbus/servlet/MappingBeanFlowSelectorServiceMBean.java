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
package jp.ossc.nimbus.servlet;

import java.util.Properties;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link MappingBeanFlowSelectorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MappingBeanFlowSelectorService
 */
public interface MappingBeanFlowSelectorServiceMBean extends DefaultBeanFlowSelectorServiceMBean{
    
    /**
     * リクエストパスとアクションフロー名のマッピングを設定する。<p>
     *
     * @param mapping リクエストパスとアクションフロー名のマッピング。リクエストパス=アクションフロー名
     */
    public void setMapping(Properties mapping);
    
    /**
     * リクエストパスとアクションフロー名のマッピングを取得する。<p>
     *
     * @return リクエストパスとアクションフロー名のマッピング
     */
    public Properties getMapping();
    
    /**
     * リクエストパスとアクションフロー名のマッピングにおいて、リクエストパスに正規表現を指定できるようにするかどうかを設定する。<p>
     * デフォルトは、falseで正規表現を使用しない。<br>
     *
     * @param isEnable 正規表現を使用する場合は、true
     */
    public void setRegexEnabled(boolean isEnable);
    
    /**
     * リクエストパスとアクションフロー名のマッピングにおいて、リクエストパスに正規表現を指定できるかどうかを判定する。<p>
     *
     * @return trueの場合、正規表現を使用する
     */
    public boolean isRegexEnabled();
    
    /**
     * 正規表現比較を行う場合に使用するマッチフラグを設定する。<p>
     * 但し、{@link #isRegexEnabled()}がtrueの場合のみ有効である。<br>
     * デフォルトは、0。<br>
     *
     * @param flag マッチフラグ
     * @see java.util.regex.Pattern#CANON_EQ
     * @see java.util.regex.Pattern#CASE_INSENSITIVE
     * @see java.util.regex.Pattern#DOTALL
     * @see java.util.regex.Pattern#MULTILINE
     * @see java.util.regex.Pattern#UNICODE_CASE
     * @see java.util.regex.Pattern#UNIX_LINES
     */
    public void setRegexMatchFlag(int flag);
    
    /**
     * 正規表現比較を行う場合に使用するマッチフラグを取得する。<p>
     *
     * @return マッチフラグ
     */
    public int getRegexMatchFlag();
}
