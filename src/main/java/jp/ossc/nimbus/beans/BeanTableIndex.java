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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.Serializable;

import jp.ossc.nimbus.beans.dataset.Record;

/**
 * Beanテーブルインデックス。<p>
 *
 * @author M.Takata
 */
public class BeanTableIndex implements Externalizable, Cloneable{
    
    private static final long serialVersionUID = -1133271083255739920L;
    
    protected String name;
    protected Class elementClass;
    protected boolean isSynchronized;
    protected TreeMap indexValueMap = new TreeMap(new ComparableComparator());
    protected Set nullValueSet = new HashSet();
    protected Set linkedIndex = new HashSet();
    protected BeanTableIndexKeyFactory indexKeyFactory;
    
    public BeanTableIndex(){
    }
    
    public BeanTableIndex(boolean isSynchronized, Class elementClass, String[] propNames) throws NoSuchPropertyException{
        this(null, isSynchronized, elementClass, propNames);
    }
    
    public BeanTableIndex(String name, boolean isSynchronized, Class elementClass, String[] propNames) throws NoSuchPropertyException{
        if(propNames == null || propNames.length == 0){
            new IllegalArgumentException("propNames is empty.");
        }
        this.name = name;
        this.isSynchronized = isSynchronized;
        this.elementClass = elementClass;
        indexKeyFactory = new DefaultBeanTableIndexKeyFactory(elementClass, propNames);
    }
    
    public BeanTableIndex(String name, boolean isSynchronized, Class elementClass, BeanTableIndexKeyFactory keyFactory){
        this.name = name;
        this.isSynchronized = isSynchronized;
        this.elementClass = elementClass;
        indexKeyFactory = keyFactory;
    }
    
    public String getName(){
        return name;
    }
    protected void setName(String name){
        this.name = name;
    }
    
    public Set getIndexedPropertyNames(){
        return indexKeyFactory.getPropertyNames();
    }
    
    public void addLinkedIndex(String indexName){
        linkedIndex.add(indexName);
    }
    public void removeLinkedIndex(String indexName){
        linkedIndex.remove(indexName);
    }
    public Set getLinkedIndexSet(){
        return linkedIndex;
    }
    
    public void add(Object element) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                addInternal(element);
            }
        }else{
            addInternal(element);
        }
    }
    protected void addInternal(Object element) throws IndexPropertyAccessException{
        Object indexKey = indexKeyFactory.createIndexKey(element);
        if(indexKey == null){
            nullValueSet.add(element);
        }else{
            Set elements = (Set)indexValueMap.get(indexKey);
            if(elements == null){
                elements = new HashSet();
                indexValueMap.put(indexKey, elements);
            }
            elements.add(element);
        }
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
        Object indexKey = indexKeyFactory.createIndexKey(element);
        if(indexKey == null){
            nullValueSet.remove(element);
        }else{
            Set elements = (Set)indexValueMap.get(indexKey);
            if(elements != null){
                elements.remove(element);
                if(elements.size() == 0){
                    indexValueMap.remove(indexKey);
                }
            }
        }
    }
    
    public void replace(Object oldElement, Object newElement) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                removeInternal(oldElement);
                addInternal(newElement);
            }
        }else{
            removeInternal(oldElement);
            addInternal(newElement);
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
        indexValueMap.clear();
        nullValueSet.clear();
    }
    
    public Set searchKeyElement(){
        return searchKeyElement(null);
    }
    public Set searchKeyElement(Set result){
        if(isSynchronized){
            synchronized(this){
                return searchKeyElementInternal(result);
            }
        }else{
            return searchKeyElementInternal(result);
        }
    }
    protected Set searchKeyElementInternal(Set result){
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexValueMap.values().iterator();
        while(itr.hasNext()){
            Set elements = (Set)itr.next();
            result.add(elements.iterator().next());
        }
        return result;
    }
    
    public Set searchNull(){
        return searchNull(null);
    }
    public Set searchNull(Set result){
        if(isSynchronized){
            synchronized(this){
                return searchNullInternal(result);
            }
        }else{
            return searchNullInternal(result);
        }
    }
    protected Set searchNullInternal(Set result){
        if(result == null){
            if(!isSynchronized){
                return nullValueSet;
            }else{
                result = new HashSet();
            }
        }
        result.addAll(nullValueSet);
        return result;
    }
    
    public Set searchNotNull(){
        return searchNotNull(null);
    }
    public Set searchNotNull(Set result){
        if(isSynchronized){
            synchronized(this){
                return searchNotNullInternal(result);
            }
        }else{
            return searchNotNullInternal(result);
        }
    }
    protected Set searchNotNullInternal(Set result){
        if(indexValueMap.size() != 0){
            Iterator itr = indexValueMap.values().iterator();
            while(itr.hasNext()){
                Set elements = (Set)itr.next();
                if(result == null){
                    result = new HashSet();
                }
                result.addAll(elements);
            }
        }
        return result;
    }
    
    public Object searchByPrimaryElement(Object element) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                return searchByPrimaryElementInternal(element);
            }
        }else{
            return searchByPrimaryElementInternal(element);
        }
    }
    protected Object searchByPrimaryElementInternal(Object element) throws IndexPropertyAccessException{
        Object indexKey = indexKeyFactory.createIndexKey(element);
        Set elements = (Set)indexValueMap.get(indexKey);
        if(elements == null){
            return null;
        }else{
            return elements.iterator().next();
        }
    }
    
    public Set searchByElement(Object element) throws IndexPropertyAccessException{
        return searchByElement(element, null);
    }
    public Set searchByElement(Object element, Set result) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                return searchByElementInternal(element, result);
            }
        }else{
            return searchByElementInternal(element, result);
        }
    }
    protected Set searchByElementInternal(Object element, Set result) throws IndexPropertyAccessException{
        Object indexKey = indexKeyFactory.createIndexKey(element);
        Set elements = (Set)indexValueMap.get(indexKey);
        if(result == null){
            if(!isSynchronized){
                return elements;
            }else{
                result = new HashSet();
            }
        }
        if(elements == null){
            return result;
        }else{
            result.addAll(elements);
            return result;
        }
    }
    
    public Set searchInElement(Object[] elements) throws IndexPropertyAccessException{
        return searchInElement(null, elements);
    }
    public Set searchInElement(Set result, Object[] elements) throws IndexPropertyAccessException{
        if(isSynchronized){
            synchronized(this){
                return searchInElementInternal(result, elements);
            }
        }else{
            return searchInElementInternal(result, elements);
        }
    }
    protected Set searchInElementInternal(Set result, Object[] elements) throws IndexPropertyAccessException{
        if(result == null){
            result = new HashSet();
        }
        for(int i = 0; i < elements.length; i++){
            Object element = elements[i];
            Object indexKey = indexKeyFactory.createIndexKey(element);
            Set ret = (Set)indexValueMap.get(indexKey);
            if(ret != null){
                result.addAll(ret);
            }
        }
        return result;
    }
    
    public Set searchBy(Object value){
        return searchBy(value, null);
    }
    public Set searchBy(Object value, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchByInternal(value, result);
            }
        }else{
            return searchByInternal(value, result);
        }
    }
    protected Set searchByInternal(Object value, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Set elements = (Set)indexValueMap.get(value);
        if(elements == null){
            return result;
        }
        if(result == null){
            if(!isSynchronized){
                return elements;
            }else{
                result = new HashSet();
            }
        }
        result.addAll(elements);
        return result;
    }
    
    public Set searchIn(Object[] values){
        return searchIn(null, values);
    }
    public Set searchIn(Set result, Object[] values){
        if(isSynchronized){
            synchronized(this){
                return searchInInternal(result, values);
            }
        }else{
            return searchInInternal(result, values);
        }
    }
    protected Set searchInInternal(Set result, Object[] values){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        for(int i = 0; i < values.length; i++){
            Set elements = (Set)indexValueMap.get(values[i]);
            if(elements != null){
                result.addAll(elements);
            }
        }
        return result;
    }
    
    public Set searchBy(Map keys) throws IllegalArgumentException{
        return searchBy(keys, null);
    }
    public Set searchBy(Map keys, Set result) throws IllegalArgumentException{
        if(isSynchronized){
            synchronized(this){
                return searchByInternal(keys, result);
            }
        }else{
            return searchByInternal(keys, result);
        }
    }
    protected Set searchByInternal(Map keys, Set result) throws IllegalArgumentException{
        Object indexKey = indexKeyFactory.createIndexKeyByProperties(keys);
        Set elements = (Set)indexValueMap.get(indexKey);
        if(elements == null){
            return result;
        }
        if(result == null){
            if(!isSynchronized){
                return elements;
            }else{
                result = new HashSet();
            }
        }
        result.addAll(elements);
        return result;
    }
    
    public Set searchIn(Map[] keys) throws IllegalArgumentException{
        return searchIn(null, keys);
    }
    public Set searchIn(Set result, Map[] keys) throws IllegalArgumentException{
        if(isSynchronized){
            synchronized(this){
                return searchInInternal(result, keys);
            }
        }else{
            return searchInInternal(result, keys);
        }
    }
    protected Set searchInInternal(Set result, Map[] keys) throws IllegalArgumentException{
        if(result == null){
            result = new HashSet();
        }
        for(int i = 0; i < keys.length; i++){
            Object indexKey = indexKeyFactory.createIndexKeyByProperties(keys[i]);
            Set elements = (Set)indexValueMap.get(indexKey);
            if(elements != null){
                result.addAll(elements);
            }
        }
        return result;
    }
    
    public Set searchFromElement(Object from) throws IndexPropertyAccessException{
        return searchFromElement(from, null);
    }
    public Set searchFromElement(Object from, Set result) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(from);
        return searchFrom(fromProp, result);
    }
    
    public Set searchFrom(Object from){
        return searchFrom(from, null);
    }
    public Set searchFrom(Object from, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchFromInternal(from, result);
            }
        }else{
            return searchFromInternal(from, result);
        }
    }
    protected Set searchFromInternal(Object from, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexValueMap.tailMap(from).values().iterator();
        while(itr.hasNext()){
            Set values = (Set)itr.next();
            result.addAll(values);
        }
        return result;
    }
    
    public Set searchToElement(Object to) throws IndexPropertyAccessException{
        return searchToElement(to, null);
    }
    public Set searchToElement(Object to, Set result) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object toProp = indexKeyFactory.createIndexKey(to);
        return searchTo(toProp, result);
    }
    
    public Set searchTo(Object to){
        return searchTo(to, null);
    }
    public Set searchTo(Object to, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchToInternal(to, result);
            }
        }else{
            return searchToInternal(to, result);
        }
    }
    protected Set searchToInternal(Object to, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexValueMap.headMap(to).values().iterator();
        while(itr.hasNext()){
            Set values = (Set)itr.next();
            result.addAll(values);
        }
        return result;
    }
    
    public Set searchRangeElement(Object from, Object to) throws IndexPropertyAccessException{
        return searchRangeElement(from, to, null);
    }
    public Set searchRangeElement(Object from, Object to, Set result) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(from);
        Object toProp = indexKeyFactory.createIndexKey(to);
        return searchRange(fromProp, toProp, result);
    }
    
    public Set searchRange(Object from, Object to){
        return searchRange(from, to, null);
    }
    public Set searchRange(Object from, Object to, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchRangeInternal(from, to, result);
            }
        }else{
            return searchRangeInternal(from, to, result);
        }
    }
    protected Set searchRangeInternal(Object from, Object to, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        if(from == null){
            return searchTo(to, result);
        }else if(to == null){
            return searchFrom(from, result);
        }else{
            Iterator itr = indexValueMap.subMap(from, to).values().iterator();
            while(itr.hasNext()){
                Set values = (Set)itr.next();
                result.addAll(values);
            }
            return result;
        }
    }
    

    public Set searchFromElement(Object from,  boolean inclusive) throws IndexPropertyAccessException{
        return searchFromElement(from, inclusive, null);
    }
    public Set searchFromElement(Object from,  boolean inclusive, Set result) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(from);
        return searchFrom(fromProp, inclusive, result);
    }
    
    public Set searchFrom(Object from,  boolean inclusive){
        return searchFrom(from, inclusive, null);
    }
    public Set searchFrom(Object from,  boolean inclusive, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchFromInternal(from, inclusive, result);
            }
        }else{
            return searchFromInternal(from, inclusive, result);
        }
    }
    protected Set searchFromInternal(Object from,  boolean inclusive, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexValueMap.tailMap(from, inclusive).values().iterator();
        while(itr.hasNext()){
            Set values = (Set)itr.next();
            result.addAll(values);
        }
        return result;
    }
    
    public Set searchToElement(Object to,  boolean inclusive) throws IndexPropertyAccessException{
        return searchToElement(to, inclusive, null);
    }
    public Set searchToElement(Object to,  boolean inclusive, Set result) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object toProp = indexKeyFactory.createIndexKey(to);
        return searchTo(toProp, inclusive, result);
    }
    
    public Set searchTo(Object to,  boolean inclusive){
        return searchTo(to, inclusive, null);
    }
    public Set searchTo(Object to,  boolean inclusive, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchToInternal(to, inclusive, result);
            }
        }else{
            return searchToInternal(to, inclusive, result);
        }
    }
    protected Set searchToInternal(Object to,  boolean inclusive, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexValueMap.headMap(to, inclusive).values().iterator();
        while(itr.hasNext()){
            Set values = (Set)itr.next();
            result.addAll(values);
        }
        return result;
    }
    
    public Set searchRangeElement(Object from,  boolean fromInclusive, Object to,  boolean toInclusive) throws IndexPropertyAccessException{
        return searchRangeElement(from, fromInclusive, to, toInclusive, null);
    }
    public Set searchRangeElement(Object from,  boolean fromInclusive, Object to,  boolean toInclusive, Set result) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(from);
        Object toProp = indexKeyFactory.createIndexKey(to);
        return searchRange(fromProp, fromInclusive, toProp, toInclusive, result);
    }
    
    public Set searchRange(Object from,  boolean fromInclusive, Object to,  boolean toInclusive){
        return searchRange(from, fromInclusive, to, toInclusive, null);
    }
    public Set searchRange(Object from,  boolean fromInclusive, Object to,  boolean toInclusive, Set result){
        if(isSynchronized){
            synchronized(this){
                return searchRangeInternal(from, fromInclusive, to, toInclusive, result);
            }
        }else{
            return searchRangeInternal(from, fromInclusive, to, toInclusive, result);
        }
    }
    protected Set searchRangeInternal(Object from,  boolean fromInclusive, Object to,  boolean toInclusive, Set result){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        if(from == null){
            return searchTo(to, toInclusive, result);
        }else if(to == null){
            return searchFrom(from, fromInclusive, result);
        }else{
            Iterator itr = indexValueMap.subMap(from, fromInclusive, to, toInclusive).values().iterator();
            while(itr.hasNext()){
                Set values = (Set)itr.next();
                result.addAll(values);
            }
            return result;
        }
    }

    
    public void writeExternal(ObjectOutput out) throws IOException{
        writeExternal(out, true);
    }
    public void writeExternal(ObjectOutput out, boolean writeValue) throws IOException{
        out.writeObject(name);
        out.writeObject(elementClass);
        out.writeBoolean(isSynchronized);
        out.writeObject(indexKeyFactory);
        if(isSynchronized){
            synchronized(this){
                out.writeObject(linkedIndex);
                if(writeValue){
                    out.writeObject(indexValueMap);
                    out.writeObject(nullValueSet);
                }
            }
        }else{
            out.writeObject(linkedIndex);
            if(writeValue){
                out.writeObject(indexValueMap);
                out.writeObject(nullValueSet);
            }
        }
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readExternal(in, true);
    }
    public void readExternal(ObjectInput in, boolean readValue) throws IOException, ClassNotFoundException{
        name = (String)in.readObject();
        elementClass = (Class)in.readObject();
        isSynchronized = in.readBoolean();
        indexKeyFactory = (BeanTableIndexKeyFactory)in.readObject();
        linkedIndex = (Set)in.readObject();
        if(readValue){
            indexValueMap = (TreeMap)in.readObject();
            nullValueSet = (Set)in.readObject();
        }
    }
    
    public BeanTableIndex cloneEmpty(boolean isSynchronized){
        BeanTableIndex clone = null;
        try{
            clone = (BeanTableIndex)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.isSynchronized = isSynchronized;
        clone.indexValueMap = new TreeMap(new ComparableComparator());
        clone.nullValueSet = new HashSet();
        clone.linkedIndex = new HashSet();
        return clone;
    }
    
    protected static class DefaultBeanTableIndexKeyFactory implements BeanTableIndexKeyFactory, Externalizable{
        protected List indexedProperties = new ArrayList();
        protected Set indexedPropertyNames = new HashSet();
        protected Class elementClass;
        
        public DefaultBeanTableIndexKeyFactory(){}
        
        public DefaultBeanTableIndexKeyFactory(Class elementClass, String[] propNames) throws NoSuchPropertyException{
            this.elementClass = elementClass;
            for(int i = 0; i < propNames.length; i++){
                String propName = propNames[i];
                SimpleProperty prop = new SimpleProperty(propName);
                if(!Record.class.isAssignableFrom(elementClass) && !prop.isReadable(elementClass)){
                    throw new NoSuchPropertyException(elementClass, propName);
                }
                if(indexedPropertyNames.contains(prop.getPropertyName())){
                    continue;
                }
                indexedPropertyNames.add(prop.getPropertyName());
                indexedProperties.add(prop);
            }
            ((ArrayList)indexedProperties).trimToSize();
        }
        
        public Set getPropertyNames(){
            return indexedPropertyNames;
        }
        
        public Object createIndexKey(Object element) throws IndexPropertyAccessException{
            final int indexKeySize = indexedProperties.size();
            if(indexKeySize == 1){
                try{
                    return ((Property)indexedProperties.get(0)).getProperty(element);
                }catch(NoSuchPropertyException e){
                    throw new IndexPropertyAccessException(
                        elementClass,
                        ((Property)indexedProperties.get(0)).getPropertyName(),
                        e
                    );
                }catch(InvocationTargetException e){
                    throw new IndexPropertyAccessException(
                        elementClass,
                        ((Property)indexedProperties.get(0)).getPropertyName(),
                        ((InvocationTargetException)e).getTargetException()
                    );
                }
            }else{
                ComplexKey indexKey = new ComplexKey(indexKeySize);
                for(int i = 0; i < indexKeySize; i++){
                    SimpleProperty prop = (SimpleProperty)indexedProperties.get(i);
                    try{
                        indexKey.set(i, prop.getProperty(element));
                    }catch(NoSuchPropertyException e){
                        throw new IndexPropertyAccessException(
                            elementClass,
                            ((Property)indexedProperties.get(0)).getPropertyName(),
                            e
                        );
                    }catch(InvocationTargetException e){
                        throw new IndexPropertyAccessException(
                            elementClass,
                            ((Property)indexedProperties.get(0)).getPropertyName(),
                            ((InvocationTargetException)e).getTargetException()
                        );
                    }
                }
                return indexKey;
            }
        }
        
        public Object createIndexKeyByProperties(Map keys) throws IllegalArgumentException{
            if(!keys.keySet().containsAll(indexedPropertyNames)){
                throw new IllegalArgumentException("keys are insufficient. keys=" + keys + ", indexedProperties=" + indexedPropertyNames);
            }
            final int indexKeySize = indexedPropertyNames.size();
            if(indexKeySize == 1){
                return keys.get(indexedPropertyNames.iterator().next());
            }else{
                ComplexKey indexKey = new ComplexKey(indexKeySize);
                for(int i = 0; i < indexKeySize; i++){
                    indexKey.set(i, keys.get(((Property)indexedProperties.get(i)).getPropertyName()));
                }
                return indexKey;
            }
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            out.writeObject(elementClass == null ? null : elementClass.getName());
            out.writeInt(indexedProperties.size());
            for(int i = 0; i < indexedProperties.size(); i++){
                SimpleProperty prop = (SimpleProperty)indexedProperties.get(i);
                out.writeObject(prop.getPropertyName());
            }
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            String className = (String)in.readObject();
            if(className != null){
                elementClass = jp.ossc.nimbus.core.Utility.convertStringToClass(className, false);
            }
            indexedProperties = new ArrayList();
            indexedPropertyNames = new HashSet();
            final int size = in.readInt();
            for(int i = 0; i < size; i++){
                final String propName = (String)in.readObject();
                indexedProperties.add(new SimpleProperty(propName));
                indexedPropertyNames.add(propName);
            }
            ((ArrayList)indexedProperties).trimToSize();
        }
    }
    
    protected static class ComparableComparator implements Comparator, Serializable{
        
        private static final long serialVersionUID = 1186653106412122081L;
        
        public int compare(Object o1, Object o2){
            Comparable c1 = (Comparable)o1;
            Comparable c2 = (Comparable)o2;
            if(c1 != null && c2 != null){
                return c1.compareTo(c2);
            }else if(c1 == null && c2 != null){
                return -1;
            }else if(c1 != null && c2 == null){
                return 1;
            }else{
                return 0;
            }
        }
    }
    
    public static class ComplexKey implements Comparable, Externalizable{
        
        private Comparable[] keys;
        private int hashCode;
        
        public ComplexKey(){
        }
        
        public ComplexKey(int size){
            keys = new Comparable[size];
        }
        public void set(int index, Object key){
            keys[index] = (Comparable)key;
            hashCode += (key == null ? 0 : key.hashCode());
        }
        public int hashCode(){
            return hashCode;
        }
        public boolean equals(Object obj){
            if(obj == this){
                return true;
            }
            if(obj == null || !(obj instanceof ComplexKey)){
                return false;
            }
            ComplexKey cmp = (ComplexKey)obj;
            for(int i = 0; i < keys.length; i++){
                if(keys[i] == null){
                    if(cmp.keys[i] != null){
                        return false;
                    }
                }else{
                    if(!keys[i].equals(cmp.keys[i])){
                        return false;
                    }
                }
            }
            return true;
        }
        public int compareTo(Object obj){
            ComplexKey cmp = (ComplexKey)obj;
            for(int i = 0; i < keys.length; i++){
                if(keys[i] == null){
                    if(cmp.keys[i] != null){
                        return -1;
                    }
                }else if(cmp.keys[i] == null){
                    return 1;
                }else{
                    final int ret = keys[i].compareTo(cmp.keys[i]);
                    if(ret != 0){
                        return ret;
                    }
                }
            }
            return 0;
        }
        public void writeExternal(ObjectOutput out) throws IOException{
            out.writeObject(keys);
        }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            Object[] tmpKeys = (Object[])in.readObject();
            if(tmpKeys != null){
                keys = new Comparable[tmpKeys.length];
                for(int i = 0; i < tmpKeys.length; i++){
                    set(i, tmpKeys[i]);
                }
            }
        }
    }
}
