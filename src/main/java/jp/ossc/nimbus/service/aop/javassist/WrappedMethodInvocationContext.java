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
package jp.ossc.nimbus.service.aop.javassist;

import java.lang.reflect.*;
import java.io.*;

import jp.ossc.nimbus.service.aop.*;

/**
 * アスペクトによってラップされたメソッド呼び出しの呼び出し情報を持つ{@link MethodInvocationContext}の実装クラス。<p>
 * 
 * @author M.Takata
 */
public class WrappedMethodInvocationContext
 extends DefaultMethodInvocationContext
 implements MethodInvocationContext, java.io.Serializable{
    
    private static final long serialVersionUID = 1372865160145034983L;
    
    /**
     * アスペクトによってラップされたメソッドのメソッドオブジェクト。<p>
     */
    protected transient Method wrappedTargetMethod;
    
    /**
     * アスペクトによってラップされたメソッド呼び出しの呼び出し情報を生成する。<p>
     *
     * @param target 呼び出し対象のオブジェクト
     * @param method 呼び出し対象のメソッド
     * @param wrappedMethod アスペクトによってラップされたメソッド
     * @param params 呼び出し対象のメソッド引数
     */
    public WrappedMethodInvocationContext(
        Object target,
        Method method,
        Method wrappedMethod,
        Object[] params
    ){
        super(target, method, params);
        wrappedTargetMethod = wrappedMethod;
    }
    
    /**
     * アスペクトによってラップされたメソッドを取得する。<p>
     *
     * @return アスペクトによってラップされたメソッドのメソッドオブジェクト
     */
    public Method getWrappedTargetMethod(){
        return wrappedTargetMethod;
    }
    
    /**
     * インターセプトされた呼び出し対象のメソッドオブジェクトを設定する。<p>
     * 同時に、アスペクトによってラップされたメソッドにも設定する。
     *
     * @param method インターセプトされた呼び出し対象のメソッドオブジェクト
     */
    public void setTargetMethod(Method method){
        super.setTargetMethod(method);
        wrappedTargetMethod = method;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeObject(new SerializableMethod(targetMethod));
        out.writeObject(new SerializableMethod(wrappedTargetMethod));
    }
    
    private void readObject(ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        targetMethod = ((SerializableMethod)in.readObject()).getMethod();
        wrappedTargetMethod = ((SerializableMethod)in.readObject()).getMethod();
    }
}
