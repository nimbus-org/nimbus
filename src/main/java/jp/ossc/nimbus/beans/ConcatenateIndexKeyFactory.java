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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * 複数のプロパティを連結してインデックスとする{@link BeanTableIndexKeyFactory}実装クラス。<p>
 *
 * @author M.Takata
 */
public class ConcatenateIndexKeyFactory implements BeanTableIndexKeyFactory, Externalizable{
    
    protected Property[] properties;
    protected Set propertyNames;
    
    public ConcatenateIndexKeyFactory(){}
    
    /**
     * 複数のプロパティを連結してインデックスを生成するインスタンスを生成する。<p>
     *
     * @param elementClass テーブルの要素となるBeanのクラスオブジェクト
     * @param aliasName 連結したプロパティの別名
     * @param propNames 連結するプロパティ名の配列
     */
    public ConcatenateIndexKeyFactory(
        Class elementClass,
        String aliasName,
        String[] propNames
    ) throws NoSuchPropertyException{
        properties = new Property[propNames.length];
        for(int i = 0; i < propNames.length; i++){
            SimpleProperty prop = new SimpleProperty(propNames[i]);
            if(!prop.isReadable(elementClass)){
                throw new NoSuchPropertyException(elementClass, propNames[i]);
            }
            properties[i] = prop;
        }
        propertyNames = new HashSet();
        propertyNames.add(aliasName);
    }
    
    public Set getPropertyNames(){
        return propertyNames;
    }
    
    public Object createIndexKey(Object element) throws IndexPropertyAccessException{
        final StringBuilder indexKey = new StringBuilder();
        String propertyName = null;
        try{
            for(int i = 0; i < properties.length; i++){
                Property property = properties[i];
                propertyName = property.getPropertyName();
                Object prop = property.getProperty(element);
                indexKey.append(prop);
            }
        }catch(NoSuchPropertyException e){
            throw new IndexPropertyAccessException(
                element.getClass(),
                propertyName,
                e
            );
        }catch(InvocationTargetException e){
            throw new IndexPropertyAccessException(
                element.getClass(),
                propertyName,
                ((InvocationTargetException)e).getTargetException()
            );
        }
        return indexKey.toString();
    }
    
    public Object createIndexKeyByProperties(Map keys) throws IllegalArgumentException{
        final String aliasName = (String)propertyNames.iterator().next();
        if(keys.containsKey(aliasName)){
            return keys.get(aliasName);
        }else{
            final StringBuilder indexKey = new StringBuilder();
            for(int i = 0; i < properties.length; i++){
                Property property = properties[i];
                if(keys.containsKey(property.getPropertyName())){
                    indexKey.append(keys.get(property.getPropertyName()));
                }else{
                    throw new IllegalArgumentException("keys are insufficient. keys=" + keys);
                }
            }
            return indexKey.toString();
        }
    }
    public void writeExternal(ObjectOutput out) throws IOException{
        out.writeInt(properties == null ? 0 : properties.length);
        if(properties != null){
            for(int i = 0; i < properties.length; i++){
                Property property = properties[i];
                out.writeObject(property.getPropertyName());
            }
        }
        out.writeObject(propertyNames.iterator().next());
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        final int len = in.readInt();
        if(len > 0){
            properties = new Property[len];
            for(int i = 0; i < len; i++){
                properties[i] = new SimpleProperty((String)in.readObject());
            }
        }
        String aliasName = (String)in.readObject();
        if(aliasName != null){
            propertyNames = new HashSet();
            propertyNames.add(aliasName);
        }
    }
}