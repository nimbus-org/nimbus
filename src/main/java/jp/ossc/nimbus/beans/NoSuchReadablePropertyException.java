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
package jp.ossc.nimbus.beans;

import java.io.*;

import jp.ossc.nimbus.core.*;

/**
 * 指定されたプロパティのGetterが存在しない事を示す例外。<p>
 *
 * @author M.Takata
 * @see SimpleProperty
 */
public class NoSuchReadablePropertyException
 extends NoSuchPropertyException implements Serializable{
    
    private static final long serialVersionUID = -2143568154656653214L;
    
    /**
     * エラー対象となったBeanのクラス、プロパティ名を持った例外を生成する。<p>
     *
     * @param clazz エラー対象となったBeanのクラス
     * @param property エラー対象となったプロパティ名
     */
    public NoSuchReadablePropertyException(Class clazz, String property){
        super(clazz, property);
    }
    
    /**
     * エラー対象となったBeanのクラス、プロパティ名、発生原因メッセージを持った例外を生成する。<p>
     *
     * @param clazz エラー対象となったBeanのクラス
     * @param property エラー対象となったプロパティ名
     * @param cause 発生原因メッセージ
     */
    public NoSuchReadablePropertyException(
        Class clazz,
        String property,
        String cause
    ){
        super(clazz, property, cause);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeObject(clazz.getName());
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        final String className = (String)in.readObject();
        clazz = Class.forName(className, true, NimbusClassLoader.getInstance());
    }
}
 