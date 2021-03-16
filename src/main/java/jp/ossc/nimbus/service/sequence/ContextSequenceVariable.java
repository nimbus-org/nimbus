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
package jp.ossc.nimbus.service.sequence;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;
import jp.ossc.nimbus.service.context.Context;

/**
 * Context桁数管理クラス。<p>
 * {@link Context}サービスから値を取得して現在値とする。
 * 
 * @author M.Takata
 */
public class ContextSequenceVariable
 implements SequenceVariable, java.io.Serializable{
    
    private static final long serialVersionUID = -1199668787624672114L;
    
    public static final char DELIMITER = '%';
    private static final String EMPTY = "";
    
    private Property key;
    private ServiceName contextServiceName;
    
    /**
     * コンストラクタ。<p>
     *
     * @param key コンテキストキー。%コンテキストキー%で指定する
     * @param context {@link Context}サービスのサービス名
     */
    public ContextSequenceVariable(
        String key,
        ServiceName context
    ) throws IllegalArgumentException{
        this.key = PropertyFactory.createProperty(key.substring(1, key.length() - 1));
        contextServiceName = context;
    }
    
    /**
     * 何もしない。<p>
     *
     * @return 必ずtrue
     */
    public boolean increment(){
        return true;
    }
    
    /**
     * 何もしない。<p>
     */
    public void clear(){
    }
    
    /**
     * 現在値を取得する。<p>
     * {@link Context}サービスから値を取得して現在値とする。
     *
     * @return 現在値
     */
    public String getCurrent(){
        if(contextServiceName == null){
            return EMPTY;
        }
        final Context context = (Context)ServiceManagerFactory
            .getServiceObject(contextServiceName);
        Object val = null;
        try{
            val = key.getProperty(context);
        }catch(NoSuchPropertyException e){
        }catch(java.lang.reflect.InvocationTargetException e){
        }
        return val == null ? EMPTY : val.toString();
    }
}
