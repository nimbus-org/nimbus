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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * {@link BeanTableIndex Beanテーブルインデックス}の管理クラス。<p>
 *
 * @author M.Takata
 */
public class BeanTableIndexManager implements Externalizable, Cloneable{
    
    private static final long serialVersionUID = 5627700360788692960L;
    
    protected Class elementClass;
    protected boolean isSynchronized;
    protected Map nameIndexMap = new HashMap();
    protected Map singleIndexMap = new HashMap();
    protected Map complexIndexMap = new HashMap();
    protected Set valueSet = new HashSet();
    
    public BeanTableIndexManager(){
    }
    
    public BeanTableIndexManager(Class elementClass, boolean isSynchronized){
        if(elementClass == null){
            throw new IllegalArgumentException("elementClass is null.");
        }
        this.elementClass = elementClass;
        this.isSynchronized = isSynchronized;
    }
    
    public Class getElementClass(){
        return elementClass;
    }
    
    public void setIndex(String name, String[] props) throws NoSuchPropertyException{
        BeanTableIndex index = new BeanTableIndex(name, isSynchronized, elementClass, props);
        if(isSynchronized){
            synchronized(this){
                setIndexInternal(name, index, false);
            }
        }else{
            setIndexInternal(name, index, false);
        }
    }
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        BeanTableIndex index = new BeanTableIndex(name, isSynchronized, elementClass, keyFactory);
        try{
            if(isSynchronized){
                synchronized(this){
                    setIndexInternal(name, index, true);
                }
            }else{
                setIndexInternal(name, index, true);
            }
        }catch(NoSuchPropertyException e){
            // 起こらない
        }
    }
    protected void setIndexInternal(String name, BeanTableIndex index, boolean containsDummyProp) throws NoSuchPropertyException{
        Set indexedPropertyNames = index.getIndexedPropertyNames();
        if(indexedPropertyNames.size() == 1){
            String propName = (String)indexedPropertyNames.iterator().next();
            if(singleIndexMap.containsKey(propName)){
                BeanTableIndex singleIndex = (BeanTableIndex)singleIndexMap.get(propName);
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
                throw new IllegalArgumentException("Duplicate index. newIndex=" + name + ", duplicateIndex=" + ((BeanTableIndex)complexIndexMap.get(indexedPropertyNames)).getName());
            }
            complexIndexMap.put(indexedPropertyNames, index);
            Iterator itr = indexedPropertyNames.iterator();
            while(itr.hasNext()){
                String propName = (String)itr.next();
                BeanTableIndex singleIndex = (BeanTableIndex)singleIndexMap.get(propName);
                if(singleIndex == null){
                    try{
                        singleIndex = new BeanTableIndex(isSynchronized, elementClass, new String[]{propName});
                    }catch(NoSuchPropertyException e){
                        if(containsDummyProp){
                            continue;
                        }else{
                            throw e;
                        }
                    }
                    singleIndexMap.put(propName, singleIndex);
                }
                singleIndex.addLinkedIndex(name);
            }
        }
        nameIndexMap.put(name, index);
    }
    
    
    public void removeIndex(String name){
        if(isSynchronized){
            synchronized(this){
                removeIndexInternal(name);
            }
        }else{
            removeIndexInternal(name);
        }
    }
    protected void removeIndexInternal(String name){
        BeanTableIndex index = (BeanTableIndex)nameIndexMap.remove(name);
        if(index == null){
            return;
        }
        Set indexedPropertyNames = index.getIndexedPropertyNames();
        if(indexedPropertyNames.size() == 1){
            String propName = (String)indexedPropertyNames.iterator().next();
            BeanTableIndex singleIndex = (BeanTableIndex)singleIndexMap.get(propName);
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
                BeanTableIndex singleIndex = (BeanTableIndex)singleIndexMap.get(propName);
                singleIndex.removeLinkedIndex(name);
                if(singleIndex.getName() == null
                    && singleIndex.getLinkedIndexSet().size() == 0
                ){
                    singleIndexMap.remove(propName);
                }
            }
        }
    }
    
    public BeanTableIndex getIndex(String name){
        if(isSynchronized){
            synchronized(this){
                return getIndexInternal(name);
            }
        }else{
            return getIndexInternal(name);
        }
    }
    protected BeanTableIndex getIndexInternal(String name){
        return (BeanTableIndex)nameIndexMap.get(name);
    }
    
    public BeanTableIndex getIndexBy(String[] propNames){
        if(isSynchronized){
            synchronized(this){
                return getIndexByInternal(propNames);
            }
        }else{
            return getIndexByInternal(propNames);
        }
    }
    protected BeanTableIndex getIndexByInternal(String[] propNames){
        if(propNames.length == 1){
            return (BeanTableIndex)singleIndexMap.get(propNames[0]);
        }else{
            Set keys = new HashSet(5);
            for(int i = 0; i < propNames.length; i++){
                keys.add(propNames[i]);
            }
            return (BeanTableIndex)complexIndexMap.get(keys);
        }
    }
    
    public BeanTableIndex getIndexBy(Set propNames){
        if(isSynchronized){
            synchronized(this){
                return getIndexByInternal(propNames);
            }
        }else{
            return getIndexByInternal(propNames);
        }
    }
    protected BeanTableIndex getIndexByInternal(Set propNames){
        if(propNames.size() == 1){
            return (BeanTableIndex)singleIndexMap.get(propNames.iterator().next());
        }else{
            return (BeanTableIndex)complexIndexMap.get(propNames);
        }
    }
    
    public boolean add(Object element) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                return addInternal(element);
            }
        }else{
            return addInternal(element);
        }
    }
    protected boolean addInternal(Object element) throws IndexPropertyAccessException{
        if(!valueSet.add(element)){
            return false;
        }
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.add(element);
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.add(element);
        }
        return true;
    }
    
    public void remove(Object element) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                removeInternal(element);
            }
        }else{
            removeInternal(element);
        }
    }
    protected void removeInternal(Object element) throws IndexPropertyAccessException{
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.remove(element);
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.remove(element);
        }
        valueSet.remove(element);
    }
    
    public void replace(Object oldElement, Object newElement) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                replaceInternal(oldElement, newElement);
            }
        }else{
            replaceInternal(oldElement, newElement);
        }
    }
    protected void replaceInternal(Object oldElement, Object newElement) throws IndexPropertyAccessException{
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.replace(oldElement, newElement);
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.replace(oldElement, newElement);
        }
        valueSet.remove(oldElement);
        valueSet.add(newElement);
    }
    
    public boolean addAll(Collection c){
        if(isSynchronized){
            synchronized(this){
                return addAllInternal(c);
            }
        }else{
            return addAllInternal(c);
        }
    }
    protected boolean addAllInternal(Collection c){
        boolean modify = false;
        Iterator itr = c.iterator();
        while(itr.hasNext()){
            Object element = itr.next();
            if(valueSet.add(element)){
                Iterator itr2 = singleIndexMap.values().iterator();
                while(itr2.hasNext()){
                    BeanTableIndex index = (BeanTableIndex)itr2.next();
                    index.add(element);
                }
                itr2 = complexIndexMap.values().iterator();
                while(itr2.hasNext()){
                    BeanTableIndex index = (BeanTableIndex)itr2.next();
                    index.add(element);
                }
                modify |= true;
            }
        }
        return modify;
    }
    
    public void retainAll(Collection c){
        if(isSynchronized){
            synchronized(this){
                retainAllInternal(c);
            }
        }else{
            retainAllInternal(c);
        }
    }
    protected void retainAllInternal(Collection c){
        Iterator elements = valueSet.iterator();
        while(elements.hasNext()){
            Object element = elements.next();
            if(!c.contains(element)){
                Iterator itr = singleIndexMap.values().iterator();
                while(itr.hasNext()){
                    BeanTableIndex index = (BeanTableIndex)itr.next();
                    index.remove(element);
                }
                itr = complexIndexMap.values().iterator();
                while(itr.hasNext()){
                    BeanTableIndex index = (BeanTableIndex)itr.next();
                    index.remove(element);
                }
                elements.remove();
            }
        }
    }
    
    public void clear(){
        if(isSynchronized){
            synchronized(this){
                clearInternal();
            }
        }else{
            clearInternal();
        }
    }
    protected void clearInternal(){
        Iterator itr = singleIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.clear();
        }
        itr = complexIndexMap.values().iterator();
        while(itr.hasNext()){
            BeanTableIndex index = (BeanTableIndex)itr.next();
            index.clear();
        }
        valueSet.clear();
    }
    
    public Set elements(){
        return elements(null);
    }
    public Set elements(Set result){
        if(isSynchronized){
            synchronized(this){
                return elementsInternal(result);
            }
        }else{
            return elementsInternal(result);
        }
    }
    protected Set elementsInternal(Set result){
        if(result == null){
            result = new HashSet();
        }
        result.addAll(valueSet);
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
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
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
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchNotNull(result);
        }
    }
    
    public Set searchKeyElement(String indexName, String[] propNames) throws IndexNotFoundException{
        return searchKeyElement(null, indexName, propNames);
    }
    public Set searchKeyElement(Set result, String indexName, String[] propNames) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchKeyElement(result);
        }
    }
    
    public Object searchByPrimaryElement(
        Object element,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchByPrimaryElement(element);
        }
    }
    
    public Set searchByElement(
        Object element,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchByElement(null, element, indexName, propNames);
    }
    
    public Set searchByElement(
        Set result,
        Object element,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchByElement(element, result);
        }
    }
    
    public Set searchInElement(
        String indexName,
        String[] propNames,
        Object[] elements
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchInElement(null, indexName, propNames, elements);
    }
    
    public Set searchInElement(
        Set result,
        String indexName,
        String[] propNames,
        Object[] elements
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(propNames) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchInElement(result, elements);
        }
    }
    
    public Set searchBy(
        Object value,
        String indexName,
        String propName
    ){
        return searchBy(null, value, indexName, propName);
    }
    
    public Set searchBy(
        Set result,
        Object value,
        String indexName,
        String propName
    ){
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchBy(value, result);
        }
    }
    
    public Set searchIn(
        String indexName,
        String propName,
        Object[] values
    ){
        return searchIn(null, indexName, propName, values);
    }
    
    public Set searchIn(
        Set result,
        String indexName,
        String propName,
        Object[] values
    ){
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchIn(result, values);
        }
    }
    
    public Set searchBy(
        Map keys,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException{
        return searchBy(null, keys, indexName);
    }
    
    public Set searchBy(
        Set result,
        Map keys,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException{
        BeanTableIndex index = indexName == null ? getIndexBy(keys.keySet()) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchBy(keys, result);
        }
    }
    
    public Set searchIn(
        String indexName,
        Map[] keys
    ) throws IndexNotFoundException, IllegalArgumentException{
        return searchIn((Set)null, indexName, keys);
    }
    
    public Set searchIn(
        Set result,
        String indexName,
        Map[] keys
    ) throws IndexNotFoundException, IllegalArgumentException{
        BeanTableIndex index = indexName == null ? getIndexBy(keys[0].keySet()) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchIn(result, keys);
        }
    }
    
    public Set searchFromElement(
        Object from,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchFromElement(null, from, indexName, propName);
    }
    
    public Set searchFromElement(
        Set result,
        Object from,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFromElement(from, result);
        }
    }
    
    public Set searchFrom(
        Object from,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchFrom(null, from, indexName, propName);
    }
    
    public Set searchFrom(
        Set result,
        Object from,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFrom(from, result);
        }
    }
    
    public Set searchToElement(
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchToElement(null, to, indexName, propName);
    }
    
    public Set searchToElement(
        Set result,
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchToElement(to, result);
        }
    }
    
    public Set searchTo(
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchTo(null, to, indexName, propName);
    }
    
    public Set searchTo(
        Set result,
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchTo(to, result);
        }
    }
    
    public Set searchRangeElement(
        Object from,
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchRangeElement(null, from, to, indexName, propName);
    }
    
    public Set searchRangeElement(
        Set result,
        Object from, 
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRangeElement(from, to, result);
        }
    }
    
    public Set searchRange(
        Object from, 
        Object to, 
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchRange(null, from, to, indexName, propName);
    }
    
    public Set searchRange(
        Set result,
        Object from, 
        Object to, 
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRange(from, to, result);
        }
    }
    

    public Set searchFromElement(
        Object from,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchFromElement(null, from, inclusive, indexName, propName);
    }
    
    public Set searchFromElement(
        Set result,
        Object from,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFromElement(from, inclusive, result);
        }
    }
    
    public Set searchFrom(
        Object from,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchFrom(null, from, inclusive, indexName, propName);
    }
    
    public Set searchFrom(
        Set result,
        Object from,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchFrom(from, inclusive, result);
        }
    }
    
    public Set searchToElement(
        Object to,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchToElement(null, to, inclusive, indexName, propName);
    }
    
    public Set searchToElement(
        Set result,
        Object to,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchToElement(to, inclusive, result);
        }
    }
    
    public Set searchTo(
        Object to,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchTo(null, to, inclusive, indexName, propName);
    }
    
    public Set searchTo(
        Set result,
        Object to,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchTo(to, inclusive, result);
        }
    }
    
    public Set searchRangeElement(
        Object from,
        boolean fromInclusive,
        Object to,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        return searchRangeElement(null, from, fromInclusive, to, toInclusive, indexName, propName);
    }
    
    public Set searchRangeElement(
        Set result,
        Object from, 
        boolean fromInclusive,
        Object to,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRangeElement(from, fromInclusive, to, toInclusive, result);
        }
    }
    
    public Set searchRange(
        Object from, 
        boolean fromInclusive,
        Object to, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        return searchRange(null, from, fromInclusive, to, toInclusive, indexName, propName);
    }
    
    public Set searchRange(
        Set result,
        Object from, 
        boolean fromInclusive,
        Object to, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        BeanTableIndex index = indexName == null ? getIndexBy(new String[]{propName}) : getIndex(indexName);
        if(index == null){
            throw new IndexNotFoundException();
        }else{
            return index.searchRange(from, fromInclusive, to, toInclusive, result);
        }
    }

    
    public void writeExternal(ObjectOutput out) throws IOException{
        writeExternal(out, true);
    }
    public void writeExternal(ObjectOutput out, boolean writeValue) throws IOException{
        out.writeObject(elementClass);
        out.writeBoolean(isSynchronized);
        if(isSynchronized){
            synchronized(this){
                out.writeInt(nameIndexMap.size());
                Iterator itr = nameIndexMap.values().iterator();
                while(itr.hasNext()){
                    BeanTableIndex index = (BeanTableIndex)itr.next();
                    index.writeExternal(out, writeValue);
                }
                if(writeValue){
                    out.writeObject(valueSet);
                }
            }
        }else{
            out.writeInt(nameIndexMap.size());
            Iterator itr = nameIndexMap.values().iterator();
            while(itr.hasNext()){
                BeanTableIndex index = (BeanTableIndex)itr.next();
                index.writeExternal(out, writeValue);
            }
            if(writeValue){
                out.writeObject(valueSet);
            }
        }
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readExternal(in, true);
    }
    public void readExternal(ObjectInput in, boolean readValue) throws IOException, ClassNotFoundException{
        elementClass = (Class)in.readObject();
        isSynchronized = in.readBoolean();
        nameIndexMap = new HashMap();
        singleIndexMap = new HashMap();
        complexIndexMap = new HashMap();
        int size = in.readInt();
        try{
            for(int i = 0; i < size; i++){
                BeanTableIndex index = new BeanTableIndex();
                index.readExternal(in, readValue);
                setIndexInternal(index.getName(), index, true);
            }
        }catch(NoSuchPropertyException e){
            // 発生しないはず
        }
        if(readValue){
            valueSet = (HashSet)in.readObject();
        }else{
            valueSet = new HashSet();
        }
    }
    
    public BeanTableIndexManager cloneEmpty(boolean isSynchronized){
        BeanTableIndexManager clone = null;
        try{
            clone = (BeanTableIndexManager)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.isSynchronized = isSynchronized;
        clone.nameIndexMap = new HashMap();
        clone.singleIndexMap = new HashMap();
        clone.complexIndexMap = new HashMap();
        try{
            Iterator itr = nameIndexMap.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                clone.setIndexInternal((String)entry.getKey(), ((BeanTableIndex)entry.getValue()).cloneEmpty(isSynchronized), true);
            }
        }catch(NoSuchPropertyException e){
            // 発生しないはず
        }
        clone.valueSet = new HashSet();
        return clone;
    }
}
