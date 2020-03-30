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
package jp.ossc.nimbus.service.aop.interceptor.servlet;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultOAuth2ScopeResolverService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see DefaultOAuth2ScopeResolverService
 */
public interface DefaultOAuth2ScopeResolverServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * パスの拡張子をトリムするかどうかを設定する。<p>
     * デフォルトは、trueでトリムする。<br>
     *
     * @param isTrim トリムする場合、true
     */
    public void setTrimExtention(boolean isTrim);
    
    /**
     * パスの拡張子をトリムするかどうかを判定する。<p>
     *
     * @return trueの場合、トリムする
     */
    public boolean isTrimExtention();
    
    /**
     * パスからスコープとして取り出す正規表現を設定する。<p>
     *
     * @param regex 正規表現文字列
     */
    public void setPathPattern(String regex);
    
    /**
     * パスからスコープとして取り出す正規表現を取得する。<p>
     *
     * @return 正規表現文字列
     */
    public String getPathPattern();
    
    /**
     * HTTPのメソッドを付与するかどうかを設定する。<p>
     * trueの場合は、パスの後に"$メソッド名"を付与する。<br>
     * デフォルトは、falseで付与しない。<br>
     *
     * @param isAppend 付与する場合、true
     */
    public void setAppendMethod(boolean isAppend);
    
    /**
     * HTTPのメソッドを付与するかどうかを判定する。<p>
     *
     * @return trueの場合、付与する
     */
    public boolean isAppendMethod();
}