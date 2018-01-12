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
package jp.ossc.nimbus.beans.dataset;

/**
 * プロパティスキーマ定義例外。<p>
 * 
 * @author M.Takata
 */
public class PropertySchemaDefineException extends DataSetException{
    
    private static final long serialVersionUID = 1552119964516882240L;
    
    /**
     * スキーマ文字列。<p>
     */
    protected String schema;
    
    /**
     * 空の例外を生成する。<p>
     *
     * @param schema 定義しようとしたスキーマ文字列
     */
    public PropertySchemaDefineException(String schema){
        this.schema = schema;
    }
    
    /**
     * メッセージを持った例外を生成する。<p>
     *
     * @param schema 定義しようとしたスキーマ文字列
     * @param message メッセージ
     */
    public PropertySchemaDefineException(String schema, String message){
        super(message);
        this.schema = schema;
    }
    
    /**
     * メッセージと原因となった例外を持った例外を生成する。<p>
     *
     * @param schema 定義しようとしたスキーマ文字列
     * @param message メッセージ
     * @param cause 原因となった例外
     */
    public PropertySchemaDefineException(
        String schema,
        String message,
        Throwable cause
    ){
        super(message, cause);
        this.schema = schema;
    }
    
    /**
     * 原因となった例外を持った例外を生成する。<p>
     *
     * @param schema 定義しようとしたスキーマ文字列
     * @param cause 原因となった例外
     */
    public PropertySchemaDefineException(String schema, Throwable cause){
        super(cause);
        this.schema = schema;
    }
    
    /**
     * メッセージを取得する。<p>
     *
     * @return メッセージ
     */
    public String getMessage(){
        return schema + ':' + message;
    }
    
    /**
     * スキーマ文字列を取得する。<p>
     *
     * @return スキーマ文字列
     */
    public String getSchema(){
        return schema;
    }
}
