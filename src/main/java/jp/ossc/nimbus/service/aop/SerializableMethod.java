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
package jp.ossc.nimbus.service.aop;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

/**
 * メソッドオブジェクトを直列化するクラス。<p>
 *
 * @author M.Takata
 */
public class SerializableMethod implements java.io.Serializable{
    
    private static final long serialVersionUID = 1134596120912054986L;
    
    private String declaringClassName;
    private String methodName;
    private List paramTypes;
    private transient Method method;
    
    /**
     * 指定されたメソッドオブジェクトを直列化するインスタンスを生成する。<p>
     *
     * @param method メソッドオブジェクト
     */
    public SerializableMethod(Method method){
        declaringClassName = method.getDeclaringClass().getName();
        methodName = method.getName();
        final Class[] paramTypes = method.getParameterTypes();
        if(paramTypes != null && paramTypes.length != 0){
            this.paramTypes = new ArrayList();
            for(int i = 0; i < paramTypes.length; i++){
                this.paramTypes.add(paramTypes[i].getName());
            }
        }
        this.method = method;
    }
    
    /**
     * メソッドオブジェクトを取得する。<p>
     *
     * @return メソッド
     */
    public Method getMethod(){
        return method;
    }
    
    public String toString(){
        return method.toString();
    }
    
    public boolean equalsSignature(Method method){
        if(!declaringClassName.equals(method.getDeclaringClass().getName())){
            return false;
        }
        if(!methodName.equals(method.getName())){
            return false;
        }
        final Class[] paramTypes = method.getParameterTypes();
        if((this.paramTypes == null && paramTypes.length != 0)
            || (this.paramTypes != null && paramTypes.length == 0)){
            return false;
        }
        if(this.paramTypes != null && paramTypes.length != 0){
            if(this.paramTypes.size() != paramTypes.length){
                return false;
            }
            for(int i = 0; i < paramTypes.length; i++){
                if(!this.paramTypes.get(i).equals(paramTypes[i].getName())){
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof SerializableMethod)){
            return false;
        }
        final SerializableMethod method = (SerializableMethod)obj;
        if(!declaringClassName.equals(method.declaringClassName)){
            return false;
        }
        if(!methodName.equals(method.methodName)){
            return false;
        }
        if((paramTypes == null && method.paramTypes != null)
            || (paramTypes != null && method.paramTypes == null)){
            return false;
        }else if(paramTypes != null && method.paramTypes != null){
            if(!paramTypes.equals(method.paramTypes)){
                return false;
            }
        }
        return true;
    }
    
    public int hashCode(){
        int hashCode = declaringClassName.hashCode()
            + methodName.hashCode();
        if(paramTypes != null){
            hashCode += paramTypes.hashCode();
        }
        return hashCode;
    }
    
    private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        try{
            final Class clazz = Class.forName(
                declaringClassName,
                true,
                Thread.currentThread().getContextClassLoader()
            );
            if(paramTypes == null){
                method = clazz.getDeclaredMethod(
                    methodName,
                    new Class[0]
                );
            }else{
                final List list = new ArrayList();
                for(int i = 0, max = paramTypes.size(); i < max; i++){
                    final String paramType = (String)paramTypes.get(i);
                    Class paramClass = null;
                    if(paramType.equals(Byte.TYPE.getName())){
                        paramClass = Byte.TYPE;
                    }else if(paramType.equals(Character.TYPE.getName())){
                        paramClass = Character.TYPE;
                    }else if(paramType.equals(Short.TYPE.getName())){
                        paramClass = Short.TYPE;
                    }else if(paramType.equals(Integer.TYPE.getName())){
                        paramClass = Integer.TYPE;
                    }else if(paramType.equals(Long.TYPE.getName())){
                        paramClass = Long.TYPE;
                    }else if(paramType.equals(Float.TYPE.getName())){
                        paramClass = Float.TYPE;
                    }else if(paramType.equals(Double.TYPE.getName())){
                        paramClass = Double.TYPE;
                    }else if(paramType.equals(Boolean.TYPE.getName())){
                        paramClass = Boolean.TYPE;
                    }else{
                        paramClass = Class.forName(
                            paramType,
                            true,
                            Thread.currentThread().getContextClassLoader()
                        );
                    }
                    list.add(paramClass);
                }
                method = clazz.getDeclaredMethod(
                    methodName,
                    (Class[])list.toArray(new Class[list.size()])
                );
            }
        }catch(NoSuchMethodException e){
            throw new IOException(e.getMessage());
        }
    }
}
