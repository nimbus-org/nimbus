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
package jp.ossc.nimbus.service.soap;

import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.SerializerFactory;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.TypeMappingRegistry;

public class TypeMapping implements javax.xml.rpc.encoding.TypeMapping{
    
    private static final long serialVersionUID = -7471505423593230398L;
    
    private String[] supportedEncodings;
    private Map registeredMap;
    
    public TypeMapping(){
    }
    
    public String[] getSupportedEncodings(){
        return supportedEncodings;
    }
    public void setSupportedEncodings(String[] encodingStyleURIs){
        supportedEncodings = encodingStyleURIs;
    }
    
    public boolean isRegistered(Class javaType, QName xmlType){
        if(registeredMap == null){
            return false;
        }
        return registeredMap.containsKey(new RegisteredKey(javaType, xmlType));
    }
    public void register(
        Class javaType,
        QName xmlType,
        SerializerFactory sf,
        DeserializerFactory dsf
    ){
        if(registeredMap == null){
            registeredMap = new HashMap();
        }
        registeredMap.put(
            new RegisteredKey(javaType, xmlType),
            new RegisteredValue(sf, dsf)
        );
    }
    
    public SerializerFactory getSerializer(Class javaType, QName xmlType){
        if(registeredMap == null){
            return null;
        }
        final RegisteredValue val = (RegisteredValue)registeredMap
            .get(new RegisteredKey(javaType, xmlType));
        return val == null ? null : val.sf;
    }
    
    public DeserializerFactory getDeserializer(
        Class javaType,
        QName xmlType
    ){
        if(registeredMap == null){
            return null;
        }
        final RegisteredValue val = (RegisteredValue)registeredMap
            .get(new RegisteredKey(javaType, xmlType));
        return val == null ? null : val.dsf;
    }
    
    public void removeSerializer(Class javaType, QName xmlType){
        if(registeredMap == null){
            return;
        }
        final RegisteredKey key = new RegisteredKey(javaType, xmlType);
        final RegisteredValue val = (RegisteredValue)registeredMap.get(key);
        if(val == null){
            return;
        }
        val.sf = null;
        if(val.dsf == null){
            registeredMap.remove(key);
        }
    }
    
    public void removeDeserializer(Class javaType, QName xmlType){
        if(registeredMap == null){
            return;
        }
        final RegisteredKey key = new RegisteredKey(javaType, xmlType);
        final RegisteredValue val = (RegisteredValue)registeredMap.get(key);
        if(val == null){
            return;
        }
        val.dsf = null;
        if(val.sf == null){
            registeredMap.remove(key);
        }
    }
    
    public javax.xml.rpc.encoding.TypeMapping cloneTypeMapping(
        TypeMappingRegistry registry,
        String encodingStyleURI
    ){
        javax.xml.rpc.encoding.TypeMapping orgMapping
             = registry.getTypeMapping(encodingStyleURI);
        if(orgMapping == null){
            orgMapping = registry.createTypeMapping();
        }
        if(registeredMap != null){
            final Iterator keys = registeredMap.keySet().iterator();
            while(keys.hasNext()){
                RegisteredKey key = (RegisteredKey)keys.next();
                RegisteredValue val = (RegisteredValue)registeredMap.get(key);
                orgMapping.register(
                    key.javaType,
                    key.xmlType,
                    val.sf,
                    val.dsf
                );
            }
        }
        return orgMapping;
    }
    
    private static class RegisteredKey{
        Class javaType;
        QName xmlType;
        public RegisteredKey(Class javaType, QName xmlType){
            if(javaType == null || xmlType == null){
                throw new IllegalArgumentException("javaType and xmlType should not be null.");
            }
            this.javaType = javaType;
            this.xmlType = xmlType;
        }
        public boolean equals(Object obj){
            if(obj == null){
                return false;
            }
            if(obj == this){
                return true;
            }
            if(obj instanceof RegisteredKey){
                RegisteredKey comp = (RegisteredKey)obj;
                if(javaType.equals(comp.javaType)
                     && xmlType.equals(comp.xmlType)){
                    return true;
                }
            }
            return false;
        }
        public int hashCode(){
            return javaType.hashCode() + xmlType.hashCode();
        }
    }
    
    private static class RegisteredValue{
        SerializerFactory sf;
        DeserializerFactory dsf;
        public RegisteredValue(SerializerFactory sf, DeserializerFactory dsf){
            this.sf = sf;
            this.dsf = dsf;
        }
    }
}
