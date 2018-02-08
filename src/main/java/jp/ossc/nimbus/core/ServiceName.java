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
 * {@link Service}を識別するための名前。<p>
 * 
 * @author M.Takata
 */
public class ServiceName implements java.io.Serializable, Comparable{
    
    private static final long serialVersionUID = 3004514157528131335L;
    
    /**
     * {@link Service}が登録されている{@link ServiceManager}の名前。<p>
     */
    private String managerName;
    
    /**
     * {@link Service}の名前。<p>
     */
    private String serviceName;
    
    public ServiceName(){}
    
    /**
     * {@link Service}の名前を指定して、サービスの識別名インスタンスを生成する。<p>
     * Serviceが登録されている{@link ServiceManager}の名前は、{@link ServiceManager#DEFAULT_NAME}となる。<br>
     *
     * @param service Serviceの名前
     */
    public ServiceName(String service){
        this(ServiceManager.DEFAULT_NAME, service);
    }
    
    /**
     * {@link Service}が登録されている{@link ServiceManager}の名前とServiceの名前を指定して、サービスの識別名インスタンスを生成する。<p>
     *
     * @param manager Serviceが登録されているServiceManagerの名前
     * @param service Serviceの名前
     */
    public ServiceName(String manager, String service){
        managerName = manager;
        serviceName = service;
    }
    
    /**
     * {@link Service}が登録されている{@link ServiceManager}の名前を取得する。<p>
     * 
     * @return {@link Service}が登録されている{@link ServiceManager}の名前
     */
    public String getServiceManagerName(){
        return managerName;
    }
    
    /**
     * {@link Service}の名前を取得する。<p>
     * 
     * @return {@link Service}の名前
     */
    public String getServiceName(){
        return serviceName;
    }
    
    /**
     * このインスタンスの文字列表現を返す。<p>
     *
     * @return [{@link ServiceManager}の名前]#[{@link Service}の名前]
     */
    public String toString(){
        StringBuffer buf = new StringBuffer();
        if(managerName != null){
            buf.append(managerName);
        }
        if(managerName != null || serviceName != null){
            buf.append('#');
        }
        if(serviceName != null){
            buf.append(serviceName);
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
        if(obj instanceof ServiceName){
            final ServiceName name = (ServiceName)obj;
            if((managerName == null && name.managerName != null)
                || (managerName != null && name.managerName == null)){
                return false;
            }else if(managerName != null && name.managerName != null
                && !managerName.equals(name.managerName)){
                return false;
            }
            if((serviceName == null && name.serviceName != null)
                || (serviceName != null && name.serviceName == null)){
                return false;
            }else if(serviceName != null && name.serviceName != null
                && !serviceName.equals(name.serviceName)){
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
        return (managerName != null ? managerName.hashCode() : 0)
            + (serviceName != null ? serviceName.hashCode() : 0);
    }
    
    /**
     * このオブジェクトと指定されたオブジェクトの順序を比較する。<p>
     *
     * @param obj 比較対照のオブジェクト
     * @return このオブジェクトが指定されたオブジェクトより小さい場合は負の整数、等しい場合はゼロ、大きい場合は正の整数を返す。
     */
    public int compareTo(Object obj){
        if(obj instanceof ServiceName){
            final ServiceName name = (ServiceName)obj;
            if(managerName == null){
                if(name.managerName != null){
                    return  -1;
                }
            }else{
                if(name.managerName == null){
                    return 1;
                }else{
                    final int ret = managerName.compareTo(name.managerName);
                    if(ret != 0){
                        return ret;
                    }
                }
            }
            if(serviceName == null){
                if(name.serviceName != null){
                    return  -1;
                }
            }else{
                if(name.serviceName == null){
                    return 1;
                }else{
                    return serviceName.compareTo(name.serviceName);
                }
            }
        }
        return 0;
    }
}