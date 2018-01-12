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
package jp.ossc.nimbus.service.context;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.reflect.InvocationTargetException;

import jp.ossc.nimbus.beans.Property;
import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.beans.NoSuchPropertyException;
import jp.ossc.nimbus.core.ServiceBase;

/**
 * Javaオブジェクトのハッシュ値を使った{@link SharedContextKeyDistributor}サービス。<p>
 *
 * @author M.Takata
 */
public class MD5HashSharedContextKeyDistributorService extends ServiceBase implements SharedContextKeyDistributor, MD5HashSharedContextKeyDistributorServiceMBean{
    
    private static final long serialVersionUID = 6233853944104905295L;
    
    private Property keyProperty;
    
    public void setKeyProperty(String prop){
        keyProperty = PropertyFactory.createProperty(prop);
    }
    public String getKeyProperty(){
        return keyProperty == null ? null : keyProperty.getPropertyName();
    }
    
    public int selectDataNodeIndex(Object key, int distributedSize) throws SharedContextIllegalDistributeException{
        if(keyProperty != null){
            try{
                key = keyProperty.getProperty(key);
            }catch(NoSuchPropertyException e){
                throw new SharedContextIllegalDistributeException(e);
            }catch(InvocationTargetException e){
                throw new SharedContextIllegalDistributeException(e);
            }
        }
        long hash = key == null ? 0l : key.hashCode();
        try{
            hash = new BigInteger(MessageDigest.getInstance("MD5").digest(java.math.BigInteger.valueOf(hash).toByteArray())).longValue();
        }catch(NoSuchAlgorithmException e){}
        return (int)Math.min(Math.abs(hash / (Long.MIN_VALUE / (long)distributedSize)), distributedSize - 1);
    }
}