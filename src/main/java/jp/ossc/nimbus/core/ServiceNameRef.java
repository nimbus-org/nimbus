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
package jp.ossc.nimbus.core;

/**
 * {@link Service}を参照するための参照名。<p>
 * 
 * @author M.Takata
 */
public class ServiceNameRef implements java.io.Serializable, Comparable{
    
    private static final long serialVersionUID = 408293095266607083L;

    /**
     * 参照する{@link Service}の実サービス名。<p>
     */
    private final ServiceName realName;
    
    /**
     * {@link Service}の参照名。<p>
     */
    private final String refName;
    
    /**
     * {@link Service}が登録されている{@link ServiceManager}の名前とServiceの名前を指定して、サービスの識別名インスタンスを生成する。<p>
     *
     * @param refName Serviceの参照名
     * @param realName 参照するServiceの実サービス名。
     */
    public ServiceNameRef(String refName, ServiceName realName){
        this.refName = refName;
        this.realName = realName;
    }
    
    /**
     * {@link Service}の参照名を取得する。<p>
     * 
     * @return Serviceの参照名
     */
    public String getReferenceServiceName(){
        return refName;
    }
    
    /**
     * {@link Service}の名前を取得する。<p>
     * 
     * @return Serviceの名前
     */
    public ServiceName getServiceName(){
        return realName;
    }
    
    /**
     * このインスタンスの文字列表現を返す。<p>
     *
     * @return [{@link Service}の参照名]=[Serviceの名前]
     */
    public String toString(){
        StringBuilder buf = new StringBuilder();
        if(refName != null){
            buf.append(refName);
        }
        buf.append('=');
        if(realName != null){
            buf.append(realName);
        }
        return buf.toString();
    }
    
    /**
     * 引数のobjがこのオブジェクトと等しいか調べる。<p>
     * {@link Service}が登録されている{@link ServiceManager}の名前とServiceの名前の両方が等しい場合のみtrueを返す。<br>
     *
     * @param obj 比較対象のオブジェクト
     * @return 等しい場合true
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(obj instanceof ServiceNameRef){
            final ServiceNameRef name = (ServiceNameRef)obj;
            if((refName == null && name.refName != null)
                || (refName != null && name.refName == null)){
                return false;
            }else if(refName != null && name.refName != null
                && !refName.equals(name.refName)){
                return false;
            }
            if((realName == null && name.realName != null)
                || (realName != null && name.realName == null)){
                return false;
            }else if(realName != null && name.realName != null
                && !realName.equals(name.realName)){
                return false;
            }
            return true;
        }
        return false;
    }
    
    /**
     * ハッシュ値を取得する。<p>
     *
     * @return ハッシュ値
     */
    public int hashCode(){
        return (refName != null ? refName.hashCode() : 0)
            + (realName != null ? realName.hashCode() : 0);
    }
    
    /**
     * このオブジェクトと指定されたオブジェクトの順序を比較する。<p>
     *
     * @param obj 比較対照のオブジェクト
     * @return このオブジェクトが指定されたオブジェクトより小さい場合は負の整数、等しい場合はゼロ、大きい場合は正の整数を返す。
     */
    public int compareTo(Object obj){
        if(obj instanceof ServiceNameRef){
            final ServiceNameRef name = (ServiceNameRef)obj;
            if(refName == null){
                if(name.refName != null){
                    return  -1;
                }
            }else{
                if(name.refName == null){
                    return 1;
                }else{
                    final int ret = refName.compareTo(name.refName);
                    if(ret != 0){
                        return ret;
                    }
                }
            }
            if(realName == null){
                if(name.realName != null){
                    return  -1;
                }
            }else{
                if(name.realName == null){
                    return 1;
                }else{
                    return realName.compareTo(name.realName);
                }
            }
        }
        return -1;
    }
}