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
package jp.ossc.nimbus.service.interpreter;

import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link PolyglotInterpreterService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface PolyglotInterpreterServiceMBean extends ServiceBaseMBean{
    
    /**
     * 言語IDを設定する。<p>
     * デフォルトは、js
     *
     * @param id 言語ID
     */
    public void setLanguageId(String id);
    
    /**
     * 言語IDを取得する。<p>
     *
     * @return 言語ID
     */
    public String getLanguageId();
    
    /**
     * エンジンを共有するかどうかを設定する。<p>
     * デフォルトは、false。<br/>
     * エンジンを共有すると、ソースの共有が可能になるため、コンパイル可能になる。<br/>
     *
     * @param share エンジンを共有する場合は、true
     */
    public void setShareEngine(boolean share);
    
    /**
     * エンジンを共有するかどうかを設定する。<p>
     *
     * @return trueの場合は、エンジンを共有する
     */
    public boolean isShareEngine();
}