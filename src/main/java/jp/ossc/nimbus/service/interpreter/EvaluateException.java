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

import java.io.*;

/**
 * コードの評価中に例外が発生した事を示す例外。<p>
 * 
 * @author M.Takata
 */
public class EvaluateException extends RuntimeException implements Externalizable{
    
    private static final long serialVersionUID = 2527297536258573314L;
    
    private String message;
    
    /**
     * コンストラクタ。<p>
     */
    public EvaluateException(){
        super();
    }
    
    /**
     * エラーメッセージを持ったインスタンスを生成するコンストラクタ。<p>
     *
     * @param message エラーメッセージ
     */
    public EvaluateException(String message){
        super();
        this.message = message;
    }
    
    /**
     * この例外の原因となった例外を持ったインスタンスを生成するコンストラクタ。<p>
     *
     * @param cause 原因となった例外
     */
    public EvaluateException(Throwable cause){
        super(cause);
    }
    
    /**
     * エラーメッセージと、この例外の原因となった例外を持ったインスタンスを生成するコンストラクタ。<p>
     *
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     */
    public EvaluateException(String message, Throwable cause){
        super(cause);
        this.message = message;
    }
    
    public String getMessage(){
        return message;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException{
        if(getCause() == null){
            out.writeObject(null);
            out.writeObject(message);
        }else{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            try{
                oos.writeObject(getCause());
                oos.flush();
                
                out.writeObject(getCause());
                out.writeObject(message);
            }catch(NotSerializableException e){
                out.writeObject(null);
                out.writeObject(message == null ? getCause().toString() : message + " : " + getCause().toString());
            }finally{
                oos.close();
            }
        }
        out.writeObject(getStackTrace());
    }
    
    public void readExternal(ObjectInput in)
     throws IOException, ClassNotFoundException{
        initCause((Throwable)in.readObject());
        message = (String)in.readObject();
        setStackTrace((StackTraceElement[])in.readObject());
    }
}