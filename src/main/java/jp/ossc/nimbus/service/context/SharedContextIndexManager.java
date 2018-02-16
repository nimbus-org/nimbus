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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

import jp.ossc.nimbus.beans.BeanTableIndexKeyFactory;
import jp.ossc.nimbus.beans.IndexPropertyAccessException;
import jp.ossc.nimbus.beans.IndexNotFoundException;

/**
 * {@link SharedContextIndex 共有コンテキストインデックス}の管理クラス。<p>
 *
 * @author M.Takata
 */
public class SharedContextIndexManager implements Externalizable, Cloneable{
    
    protected Map nameIndexMap = new HashMap();
    protected Map singleIndexMap = new HashMap();
    protected Map complexIndexMap = new HashMap();
    protected Set keySet = new HashSet();
    
    public SharedContextIndexManager(){
    }
    
    public void setIndex(String name, String[] props){
        SharedContextIndex index = new SharedContextIndex(name, props);
        setIndexInternal(name, index, false);
    }
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        SharedContextIndex index = new SharedContextIndex(name, keyFactory);
        setIndexInternal(name, index, true);
    }
    protected void setIndexInternal(String name, SharedContextIndex index, boolean containsDummyProp){
        Set indexedPropertyNames = index.getIndexedPropertyNames();
        if(indexedPropertyNames.size() == 1){
            String propName = (String)indexedPropertyNames.iterator().next();
            if(singleIndexMap.containsKey(propName)){
                SharedContextIndex singleIndex = (SharedContextIndex)singleIndexMap.get(propName);
                if(singleIndex.getName() == null){
                    singleIndex.setName(name);
                    index = singleIndex;
                }else{
                    throw new IllegalArgumentException("Duplicate index. newIndex=" + name + ", duplicateIndex=" + singleIndex.getName());
                }
            }else{
                singleIndexMap.put(propName, index);
            }
        }else{
            if(complexIndexMap.containsKey(indexedPropertyNames)){
                throw new IllegalArgumentException("Duplicate index. newIndex=" + name + ", duplicateIndex=" + ((SharedContextIndex)complexIndexMap.get(indexedPropertyNames)).getName());
            }
            complexIndexMap.put(indexedPropertyNames, index);
            Iterator itr = indexedPropertyNames.iterator();
            while(itr.hasNext()){
                String propName = (String)itr.next();
                SharedContextIndex singleIndex = (SharedContextIndex)singleIndexMap.get(propName);
                if(singleIndex == null){
                    singleIndex = new SharedContextIndex(new String[]{propName});
                    singleIndexMap.put(propName, singleIndex);
                }
                singleIndex.addLinkedIndex(name);
            }
        }
        nameIndexMap.put(name, index);
    }
    
    public void removeIndex(String name){
        SharedContextIndex index = (SharedContextIndex)nameIndexMap.remove(name);
        if(index == null){
            return;
        }
        Set indexedPropertyNames = index.getIndexedPropertyNames();
        if(indexedPropertyNames.size() == 1){
            String propName = (String)indexedPropertyNames.iterator().next();
            SharedContextIndex singleIndex = (SharedContextIndex)singleIndexMap.get(propName);
            if(singleIndex.getLinkedIndexSet().size() == 0){
                singleIndexMap.remove(propName);
            }else{
                singleIndex.setName(null);
            }
        }else{
            complexIndexMap.remove(indexedPropertyNames);
            Iterator itr = indexedPropertyNames.iterator();
            while(itr.hasNext()){
                String propName = (String)itr.next();
                SharedContextIndex singleIndex = (SharedContextIndex)singleIndexMap.get(propName);
                singleIndex.removeLinkedIndex(name);
                if(singleIndex.getName() == null
                    && singleIndex.getLinkedIndexSet().size() == 0
                ){
                    singleIndexMap.remove(propName);
                }
            }
        }
    }
    
    public SharedContextIndex getIndex(String name){
        return (SharedContextIndex)nameIndexMap.get(name);
    }
    
    public SharedContextIndex getIndexBy(String[] propNames){
        if(propNames.length == 1){
            return (SharedContextIndex)singleIndexMap.get(propNames[0]);
        }else{
            Set keys = new HashSet(5);
            for(int i = 0; i < propNames.length; i++){
                keys.add(propNames[i]);
            }
            return (SharedContextIndex)complexIndexMap.get(keys);
        }
    }
    
    public SharedContextIndex getIndexBy(Set propNames){
        if(propNames.size() == 1){
            return (SharedContextIndex)singleIndexMap.get(propNames.iterator().next());
        }else{
            return (SharedContextIndex)complexIndexMap.get(propNames);
        }
    }
    
    public boolean hasIndex(String name){
        return hasIndex(name, (String)null);
    }
    
    public boolean hasIndex(String name, String propName){
        return hasIndex(name, propName == null ? null : new String[]{propName});
    }
    
    public boolean hasIndex(String name, String[] propNames){
        if(getIndex(name) != null){
            return true;
        }
        return propNames == null ? false : getIndexBy(propNames) != null;
    }
    
    public boolean hasIndex(){
        return !nameIndexMap.isEmpty();
    }
    
    public synchronized boolean add(Object key, Object value) throws IndexPropertyAccessException{
        if(!keySet.add(key)){
            return false;
        }
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.add(key, value);
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.add(key, value);
        }
        return true;
    }
    
    public synchronized void remove(Object key, Object value) throws IndexPropertyAccessException{
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.remove(key, value);
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.remove(key, value);
        }
        keySet.remove(key);
    }
    
    public synchronized void replace(Object key, Object oldValue, Object newValue) throws IndexPropertyAccessException{
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.replace(key, oldValue, newValue);
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.replace(key, oldValue, newValue);
        }
    }
    
    public synchronized boolean addAll(Map c){
        boolean modify = false;
        Iterator entries = c.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry entry = (Map.Entry)entries.next();
            if(keySet.add(entry.getKey())){
                Iterator itr = singleIndexMap.values().iterator();
                while(itr.hasNext()){
                    SharedContextIndex index = (SharedContextIndex)itr.next();
                    index.add(entry.getKey(), entry.getValue());
                }
                itr = complexIndexMap.values().iterator();
                while(itr.hasNext()){
                    SharedContextIndex index = (SharedContextIndex)itr.next();
                    index.add(entry.getKey(), entry.getValue());
                }
                modify |= true;
            }
        }
        return modify;
    }
    
    public synchronized void retainAll(Map c){
        Iterator keys = keySet.iterator();
        while(keys.hasNext()){
            Object key = keys.next();
            if(!c.containsKey(key)){
                Iterator itr = singleIndexMap.values().iterator();
                while(itr.hasNext()){
                    SharedContextIndex index = (SharedContextIndex)itr.next();
                    index.remove(key, c.get(key));
                }
                itr = complexIndexMap.values().iterator();
                while(itr.hasNext()){
                    SharedContextIndex index = (SharedContextIndex)itr.next();
                    index.remove(key, c.get(key));
                }
                keys.remove();
            }
        }
    }
    
    public synchronized void clear(){
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.clear();
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            SharedContextIndex index = (SharedContextIndex)itr.next();
            index.clear();
        }
        keySet.clear();
    }
    
    public Set keySet(){
        return keySet(null);
    }
    public synchronized Set keySet(Set result){
        if(result == null){
            result = new HashSet();
        }
        result.addAll(keySet);
        return result;
    }
    
    public Set searchNull(String indexName, String propName) throws IndexNotFoundException{
        return searchNull(null, indexName, propName);
    }
    
    public Set searchNull(
        Set result,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchNull(result);
        }
    }
    
    public Set searchNotNull(String indexName, String propName) throws IndexNotFoundException{
        return searchNotNull(null, indexName, propName);
    }
    
    public Set searchNotNull(
        Set result,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchNotNull(result);
        }
    }
    
    public Set searchKey(String indexName, String[] propNames) throws IndexNotFoundException{
        return searchKey(null, indexName, propNames);
    }
    public Set searchKey(Set result, String indexName, String[] propNames) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchKey(result);
        }
    }
    
    public Object searchByPrimary(
        Object value,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchByPrimary(value);
        }
    }
    
    public Set searchBy(
        Object value,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchBy(null, value, indexName, propNames);
    }
    
    public Set searchBy(
        Set result,
        Object value,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchBy(result, value);
        }
    }
    
    public Set searchIn(
        String indexName,
        String[] propNames,
        Object[] values
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchIn(null, indexName, propNames, values);
    }
    
    public Set searchIn(
        Set result,
        String indexName,
        String[] propNames,
        Object[] values
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchIn(result, values);
        }
    }
    
    public Set searchByProperty(
        Object prop,
        String indexName,
        String propName
    ){
        return searchByProperty(null, prop, indexName, propName);
    }
    
    public Set searchByProperty(
        Set result,
        Object prop,
        String indexName,
        String propName
    ){
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchByProperty(result, prop);
        }
    }
    
    public Set searchInProperty(
        String indexName,
        String propName,
        Object[] props
    ){
        return searchInProperty(null, indexName, propName, props);
    }
    
    public Set searchInProperty(
        Set result,
        String indexName,
        String propName,
        Object[] props
    ){
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchInProperty(result, props);
        }
    }
    
    public Set searchByProperty(
        Map props,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException{
        return searchByProperty(null, props, indexName);
    }
    
    public Set searchByProperty(
        Set result,
        Map props,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException{
        SharedContextIndex index = indexName == null ? getIndexBy(props.keySet()) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchByProperty(result, props);
        }
    }
    
    public Set searchInProperty(
        String indexName,
        Map[] props
    ) throws IndexNotFoundException, IllegalArgumentException{
        return searchInProperty((Set)null, indexName, props);
    }
    
    public Set searchInProperty(
        Set result,
        String indexName,
        Map[] props
    ) throws IndexNotFoundException, IllegalArgumentException{
        SharedContextIndex index = indexName == null ? getIndexBy(props[0].keySet()) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchInProperty(result, props);
        }
    }
    
    public Set searchFrom(
        Object fromValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchFrom(null, fromValue, indexName, propName);
    }
    
    public Set searchFrom(
        Set result,
        Object fromValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFrom(result, fromValue);
        }
    }
    
    public Set searchFromProperty(
        Object fromProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchFromProperty(null, fromProp, indexName, propName);
    }
    
    public Set searchFromProperty(
        Set result,
        Object fromProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFromProperty(result, fromProp);
        }
    }
    
    public Set searchTo(
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchTo(null, toValue, indexName, propName);
    }
    
    public Set searchTo(
        Set result,
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchTo(result, toValue);
        }
    }
    
    public Set searchToProperty(
        Object toProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchToProperty(null, toProp, indexName, propName);
    }
    
    public Set searchToProperty(
        Set result,
        Object toProp,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchToProperty(result, toProp);
        }
    }
    
    public Set searchRange(
        Object fromValue,
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchRange(null, fromValue, toValue, indexName, propName);
    }
    
    public Set searchRange(
        Set result,
        Object fromValue, 
        Object toValue,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRange(result, fromValue, toValue);
        }
    }
    
    public Set searchRangeProperty(
        Object fromProp, 
        Object toProp, 
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchRangeProperty(null, fromProp, toProp, indexName, propName);
    }
    
    public Set searchRangeProperty(
        Set result,
        Object fromProp,
        Object toProp, 
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRangeProperty(result, fromProp, toProp);
        }
    }
    

    public Set searchFrom(
        Object fromValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchFrom(null, fromValue, inclusive, indexName, propName);
    }
    
    public Set searchFrom(
        Set result,
        Object fromValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFrom(result, fromValue, inclusive);
        }
    }
    
    public Set searchFromProperty(
        Object fromProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchFromProperty(null, fromProp, inclusive, indexName, propName);
    }
    
    public Set searchFromProperty(
        Set result,
        Object fromProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFromProperty(result, fromProp, inclusive);
        }
    }
    
    public Set searchTo(
        Object toValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchTo(null, toValue, inclusive, indexName, propName);
    }
    
    public Set searchTo(
        Set result,
        Object toValue,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchTo(result, toValue, inclusive);
        }
    }
    
    public Set searchToProperty(
        Object toProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchToProperty(null, toProp, inclusive, indexName, propName);
    }
    
    public Set searchToProperty(
        Set result,
        Object toProp,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchToProperty(result, toProp, inclusive);
        }
    }
    
    public Set searchRange(
        Object fromValue,
        boolean fromInclusive,
        Object toValue,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchRange(null, fromValue, fromInclusive, toValue, toInclusive, indexName, propName);
    }
    
    public Set searchRange(
        Set result,
        Object fromValue, 
        boolean fromInclusive,
        Object toValue,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRange(result, fromValue, fromInclusive, toValue, toInclusive);
        }
    }
    
    public Set searchRangeProperty(
        Object fromProp, 
        boolean fromInclusive,
        Object toProp, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchRangeProperty(null, fromProp, fromInclusive, toProp, toInclusive, indexName, propName);
    }
    
    public Set searchRangeProperty(
        Set result,
        Object fromProp,
        boolean fromInclusive,
        Object toProp, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        SharedContextIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRangeProperty(result, fromProp, fromInclusive, toProp, toInclusive);
        }
    }

    
    public void writeExternal(ObjectOutput out) throws IOException{
        writeExternal(out, true);
    }
    public void writeExternal(ObjectOutput out, boolean writeValue) throws IOException{
        synchronized(this){
            out.writeInt(nameIndexMap.size());
            Iterator itr = nameIndexMap.values().iterator();
            while(itr.hasNext()){
                SharedContextIndex index = (SharedContextIndex)itr.next();
                index.writeExternal(out, writeValue);
            }
            out.writeInt(singleIndexMap.size());
            itr = singleIndexMap.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                out.writeObject(entry.getKey());
                ((SharedContextIndex)entry.getValue()).writeExternal(out, writeValue);
            }
            out.writeInt(complexIndexMap.size());
            itr = complexIndexMap.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                out.writeObject(entry.getKey());
                ((SharedContextIndex)entry.getValue()).writeExternal(out, writeValue);
            }
            if(writeValue){
                out.writeObject(keySet);
            }
        }
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readExternal(in, true);
    }
    public void readExternal(ObjectInput in, boolean readValue) throws IOException, ClassNotFoundException{
        nameIndexMap = new HashMap();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            SharedContextIndex index = new SharedContextIndex();
            index.readExternal(in, readValue);
            nameIndexMap.put(index.getName(), index);
        }
        singleIndexMap = new HashMap();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            SharedContextIndex index = new SharedContextIndex();
            String propName = (String)in.readObject();
            index.readExternal(in, readValue);
            singleIndexMap.put(propName, index);
        }
        complexIndexMap = new HashMap();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            SharedContextIndex index = new SharedContextIndex();
            Set propNames = (Set)in.readObject();
            index.readExternal(in, readValue);
            complexIndexMap.put(propNames, index);
        }
        if(readValue){
            keySet = (HashSet)in.readObject();
        }else{
            keySet = new HashSet();
        }
    }
    
    public SharedContextIndexManager cloneEmpty(){
        SharedContextIndexManager clone = null;
        try{
            clone = (SharedContextIndexManager)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.nameIndexMap = new HashMap();
        clone.singleIndexMap = new HashMap();
        clone.complexIndexMap = new HashMap();
        Iterator itr = nameIndexMap.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            clone.setIndexInternal((String)entry.getKey(), ((SharedContextIndex)entry.getValue()).cloneEmpty(), true);
        }
        clone.keySet = new HashSet();
        return clone;
    }
    
    public SharedContextIndexManager clone(Map c){
        SharedContextIndexManager clone = cloneEmpty();
        clone.addAll(c);
        return clone;
    }
    
    public void replaceIndex(String name, Map c){
        SharedContextIndex index = getIndex(name);
        if(index == null){
            return;
        }
        Set linkedNames = index.getLinkedIndexSet();
        if(linkedNames.size() != 0){
            Iterator itr = linkedNames.iterator();
            while(itr.hasNext()){
                String linkedName = (String)itr.next();
                SharedContextIndex linkedIndex = (SharedContextIndex)complexIndexMap.get(linkedName);
                synchronized(linkedIndex){
                    linkedIndex.clear();
                    linkedIndex.addAll(c);
                }
            }
        }
        synchronized(index){
            index.clear();
            index.addAll(c);
        }
    }
    
    public SharedContextIndex createTemporaryIndex(
        Set resultSet,
        String indexName,
        String propName
    )throws IndexNotFoundException{
        return createTemporaryIndex(resultSet, indexName, new String[]{propName});
    }
    public SharedContextIndex createTemporaryIndex(
        Set resultSet,
        String indexName,
        String[] propNames
    )throws IndexNotFoundException{
        SharedContextIndex index = null;
        if(indexName != null){
            index = getIndex(indexName);
        }
        if(index == null && propNames != null && propNames.length != 0){
            index = getIndexBy(propNames);
        }
        if(index == null){
            throw new IndexNotFoundException();
        }
        index = index.clone(resultSet);
        return index;
    }
}
