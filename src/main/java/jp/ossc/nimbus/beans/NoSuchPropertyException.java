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
 * 指定されたプロパティが存在しない事を示す例外。<p>
 *
 * @author M.Takata
 * @see Property
 */
public class NoSuchPropertyException extends Exception implements Serializable{
    
    private static final long serialVersionUID = 5772189868420840538L;
    
    /**
     * エラー対象となったBeanのクラスオブジェクト。<p>
     */
    protected transient Class clazz;
    
    /**
     * エラー対象となったプロパティ名。<p>
     */
    protected final String property;
    
    /**
     * エラー対象となったBeanのクラス、プロパティ名を持った例外を生成する。<p>
     *
     * @param clazz エラー対象となったBeanのクラス
     * @param property エラー対象となったプロパティ名
     */
    public NoSuchPropertyException(Class clazz, String property){
        this(clazz, property, (Throwable)null);
    }
    
    /**
     * エラー対象となったBeanのクラス、プロパティ名、発生元例外を持った例外を生成する。<p>
     *
     * @param clazz エラー対象となったBeanのクラス
     * @param property エラー対象となったプロパティ名
     * @param th 発生元例外
     */
    public NoSuchPropertyException(
        Class clazz,
        String property,
        Throwable th
    ){
        super(
            "No such property. class=" + clazz + ", property=" + property,
            th
        );
        this.clazz = clazz;
        this.property = property;
    }
    
    /**
     * エラー対象となったBeanのクラス、プロパティ名、発生原因メッセージを持った例外を生成する。<p>
     *
     * @param clazz エラー対象となったBeanのクラス
     * @param property エラー対象となったプロパティ名
     * @param cause 発生原因メッセージ
     */
    public NoSuchPropertyException(
        Class clazz,
        String property,
        String cause
    ){
        super(
            "No such property. class=" + clazz + ", property=" + property + " : Cause " + cause
        );
        this.clazz = clazz;
        this.property = property;
    }
    
    /**
     * エラー対象となったBeanのクラスを取得する。<p>
     *
     * @return エラー対象となったBeanのクラス
     */
    public Class getTargetObjectClass(){
        return clazz;
    }
    
    /**
     * エラー対象となったプロパティ名を取得する。<p>
     *
     * @return エラー対象となったプロパティ名
     */
    public String getProperty(){
        return property;
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
