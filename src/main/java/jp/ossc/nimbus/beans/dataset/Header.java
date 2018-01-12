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

import java.io.*;

/**
 * ヘッダー。<p>
 * データセットのヘッダーで、ヘッダー名を持った{@link Record レコード}である。<br>
 * レコードと同様に、複数のプロパティを持つBeanで、スキーマ定義によって、どのようなBeanにするのか（プロパティ名、型など）を動的に決定できる。<br>
 * 
 * @author M.Takata
 */
public class Header extends Record{
    
    private static final long serialVersionUID = -2149254849180957920L;
    
    /**
     * ヘッダー名。<p>
     */
    protected String name;
    
    /**
     * 未定義のヘッダーを生成する。<p>
     */
    public Header(){
    }
    
    /**
     * 未定義のヘッダーを生成する。<p>
     *
     * @param name ヘッダー名
     */
    public Header(String name){
        this.name = name;
    }
    
    /**
     * ヘッダーを生成する。<p>
     *
     * @param name ヘッダー名
     * @param schema スキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public Header(String name, String schema)
     throws PropertySchemaDefineException{
        super(schema);
        this.name = name;
    }
    
    /**
     * ヘッダーを生成する。<p>
     *
     * @param name ヘッダー名
     * @param recordSchema スキーマ文字列から生成されたレコードスキーマ
     * @exception PropertySchemaDefineException プロパティのスキーマ定義に失敗した場合
     */
    public Header(String name, RecordSchema recordSchema){
        super(recordSchema);
        this.name = name;
    }
    
    /**
     * ヘッダー名を取得する。<p>
     *
     * @return ヘッダー名
     */
    public String getName(){
        return name;
    }
    
    /**
     * ヘッダー名を設定する。<p>
     *
     * @param name ヘッダー名
     */
    public void setName(String name){
        this.name = name;
    }
    
    protected void writeSchema(ObjectOutput out) throws IOException{
        super.writeSchema(out);
        out.writeObject(name);
    }
    
    protected void readSchema(ObjectInput in) throws IOException, ClassNotFoundException{
        super.readSchema(in);
        name = (String)in.readObject();
    }
}
