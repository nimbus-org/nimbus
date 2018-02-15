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

import jp.ossc.nimbus.beans.*;

/**
 * 共有コンテキストインデックス。<p>
 *
 * @author M.Takata
 */
public class SharedContextIndex implements Externalizable, Cloneable{
    
    protected String name;
    protected TreeMap indexKeyMap = new TreeMap(new ComparableComparator());
    protected Set notNullKeySet = new HashSet();
    protected Set linkedIndex = new HashSet();
    protected BeanTableIndexKeyFactory indexKeyFactory;
    
    public SharedContextIndex(){
    }
    
    public SharedContextIndex(String[] propNames){
        this(null, propNames);
    }
    
    public SharedContextIndex(String name, String[] propNames){
        if(propNames == null || propNames.length == 0){
            new IllegalArgumentException("propNames is empty.");
        }
        this.name = name;
        indexKeyFactory = new DefaultSharedContextIndexKeyFactory(propNames);
    }
    
    public SharedContextIndex(String name, BeanTableIndexKeyFactory keyFactory){
        this.name = name;
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
    
    public synchronized void add(Object key, Object value) throws IndexPropertyAccessException{
        if(value instanceof List){
            List list = (List)value;
            Set keySet = null;
            for(int i = 0, imax = list.size(); i < imax; i++){
                Object element = list.get(i);
                Object indexKey = indexKeyFactory.createIndexKey(element);
                if(keySet != null){
                    if(keySet.contains(indexKey)){
                        continue;
                    }else{
                        keySet.add(indexKey);
                    }
                }
                Set keys = (Set)indexKeyMap.get(indexKey);
                if(keys == null){
                    keys = new HashSet();
                    indexKeyMap.put(indexKey, keys);
                }
                keys.add(key);
                if(indexKey != null){
                    notNullKeySet.add(key);
                }
                if(keySet == null && i != imax - 1){
                    keySet = new HashSet();
                    keySet.add(indexKey);
                }
            }
        }else{
            Object indexKey = indexKeyFactory.createIndexKey(value);
            Set keys = (Set)indexKeyMap.get(indexKey);
            if(keys == null){
                keys = new HashSet();
                indexKeyMap.put(indexKey, keys);
            }
            keys.add(key);
            if(indexKey != null){
                notNullKeySet.add(key);
            }
        }
    }
    
    public synchronized void addAll(Map c) throws IndexPropertyAccessException{
        Iterator itr = c.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry entry = (Map.Entry)itr.next();
            add(entry.getKey(), entry.getValue());
        }
    }
    
    public synchronized void remove(Object key, Object value) throws IndexPropertyAccessException{
        if(value instanceof List){
            List list = (List)value;
            Set keySet = null;
            for(int i = 0, imax = list.size(); i < imax; i++){
                Object element = list.get(i);
                Object indexKey = indexKeyFactory.createIndexKey(element);
                if(keySet != null){
                    if(keySet.contains(indexKey)){
                        continue;
                    }else{
                        keySet.add(indexKey);
                    }
                }
                Set keys = (Set)indexKeyMap.get(indexKey);
                if(keys != null){
                    keys.remove(key);
                    if(keys.size() == 0){
                        indexKeyMap.remove(indexKey);
                    }
                }
                notNullKeySet.remove(key);
                if(keySet == null && i != imax - 1){
                    keySet = new HashSet();
                    keySet.add(indexKey);
                }
            }
        }else{
            Object indexKey = indexKeyFactory.createIndexKey(value);
            Set keys = (Set)indexKeyMap.get(indexKey);
            if(keys != null){
                keys.remove(key);
                if(keys.size() == 0){
                    indexKeyMap.remove(indexKey);
                }
            }
            notNullKeySet.remove(key);
        }
    }
    
    public synchronized void replace(Object key, Object oldValue, Object newValue) throws IndexPropertyAccessException{
        remove(key, oldValue);
        add(key, newValue);
    }
    
    public synchronized void clear(){
        indexKeyMap.clear();
        notNullKeySet.clear();
    }
    
    public Set searchKey(){
        return searchKey(null);
    }
    public synchronized Set searchKey(Set result){
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexKeyMap.values().iterator();
        while(itr.hasNext()){
            Set keys = (Set)itr.next();
            result.add(keys.iterator().next());
        }
        return result;
    }
    
    public Set searchNull(){
        return searchNull(null);
    }
    public synchronized Set searchNull(Set result){
        Set keys = (Set)indexKeyMap.get(null);
        if(result == null){
            result = new HashSet();
        }
        if(keys != null){
            result.addAll(keys);
        }
        return result;
    }
    
    public Set searchNotNull(){
        return searchNotNull(null);
    }
    public synchronized Set searchNotNull(Set result){
        if(result == null){
            result = new HashSet();
        }
        result.addAll(notNullKeySet);
        return result;
    }
    
    public synchronized Object searchByPrimary(Object value) throws IndexPropertyAccessException{
        Object indexKey = indexKeyFactory.createIndexKey(value);
        Set keys = (Set)indexKeyMap.get(indexKey);
        if(keys == null){
            return null;
        }else{
            return keys.iterator().next();
        }
    }
    
    public Set searchBy(Object value) throws IndexPropertyAccessException{
        return searchBy(null, value);
    }
    public synchronized Set searchBy(Set result, Object value) throws IndexPropertyAccessException{
        Object indexKey = indexKeyFactory.createIndexKey(value);
        Set keys = (Set)indexKeyMap.get(indexKey);
        if(result == null){
            result = new HashSet();
        }
        if(keys == null){
            return result;
        }else{
            result.addAll(keys);
            return result;
        }
    }
    
    public Set searchIn(Object[] values) throws IndexPropertyAccessException{
        return searchIn(null, values);
    }
    public synchronized Set searchIn(Set result, Object[] values) throws IndexPropertyAccessException{
        if(result == null){
            result = new HashSet();
        }
        for(int i = 0; i < values.length; i++){
            Object indexKey = indexKeyFactory.createIndexKey(values[i]);
            Set ret = (Set)indexKeyMap.get(indexKey);
            if(ret != null){
                result.addAll(ret);
            }
        }
        return result;
    }
    
    public Set searchByProperty(Object prop){
        return searchByProperty(null, prop);
    }
    public synchronized Set searchByProperty(Set result, Object prop){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Set keys = (Set)indexKeyMap.get(prop);
        if(keys == null){
            return result;
        }
        if(result == null){
            result = new HashSet();
        }
        result.addAll(keys);
        return result;
    }
    
    public Set searchInProperty(Object[] props){
        return searchInProperty(null, props);
    }
    public synchronized Set searchInProperty(Set result, Object[] props){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        for(int i = 0; i < props.length; i++){
            Set keys = (Set)indexKeyMap.get(props[i]);
            if(keys != null){
                result.addAll(keys);
            }
        }
        return result;
    }
    
    public Set searchByProperty(Map props) throws IllegalArgumentException{
        return searchByProperty(null, props);
    }
    public synchronized Set searchByProperty(Set result, Map props) throws IllegalArgumentException{
        Object indexKey = indexKeyFactory.createIndexKeyByProperties(props);
        Set keys = (Set)indexKeyMap.get(indexKey);
        if(keys == null){
            return result;
        }
        if(result == null){
            result = new HashSet();
        }
        result.addAll(keys);
        return result;
    }
    
    public Set searchInProperty(Map[] props) throws IllegalArgumentException{
        return searchInProperty(null, props);
    }
    public synchronized Set searchInProperty(Set result, Map[] props) throws IllegalArgumentException{
        if(result == null){
            result = new HashSet();
        }
        for(int i = 0; i < props.length; i++){
            Map propMap = props[i];
            Object indexKey = indexKeyFactory.createIndexKeyByProperties(propMap);
            Set keys = (Set)indexKeyMap.get(indexKey);
            if(keys != null){
                result.addAll(keys);
            }
        }
        return result;
    }
    
    public Set searchFrom(Object fromValue) throws IndexPropertyAccessException{
        return searchFrom(null, fromValue);
    }
    public Set searchFrom(Set result, Object fromValue) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(fromValue);
        return searchFromProperty(result, fromProp);
    }
    
    public Set searchFromProperty(Object fromProp){
        return searchFromProperty(null, fromProp);
    }
    public synchronized Set searchFromProperty(Set result, Object fromProp){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexKeyMap.tailMap(fromProp).values().iterator();
        while(itr.hasNext()){
            Set keys = (Set)itr.next();
            result.addAll(keys);
        }
        return result;
    }
    
    public Set searchTo(Object toValue) throws IndexPropertyAccessException{
        return searchTo(null, toValue);
    }
    public Set searchTo(Set result, Object toValue) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object toProp = indexKeyFactory.createIndexKey(toValue);
        return searchToProperty(result, toProp);
    }
    
    public Set searchToProperty(Object toProp){
        return searchToProperty(null, toProp);
    }
    public synchronized Set searchToProperty(Set result, Object toProp){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexKeyMap.headMap(toProp).values().iterator();
        while(itr.hasNext()){
            Set keys = (Set)itr.next();
            result.addAll(keys);
        }
        return result;
    }
    
    public Set searchRange(Object fromValue, Object toValue) throws IndexPropertyAccessException{
        return searchRange(null, fromValue, toValue);
    }
    public Set searchRange(Set result, Object fromValue, Object toValue) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(fromValue);
        Object toProp = indexKeyFactory.createIndexKey(toValue);
        return searchRangeProperty(result, fromProp, toProp);
    }
    
    public Set searchRangeProperty(Object fromProp, Object toProp){
        return searchRangeProperty(null, fromProp, toProp);
    }
    public synchronized Set searchRangeProperty(Set result, Object fromProp, Object toProp){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        if(fromProp == null){
            return searchToProperty(result, toProp);
        }else if(toProp == null){
            return searchFromProperty(result, fromProp);
        }else{
            Iterator itr = indexKeyMap.subMap(fromProp, toProp).values().iterator();
            while(itr.hasNext()){
                Set keys = (Set)itr.next();
                result.addAll(keys);
            }
            return result;
        }
    }
    

    public Set searchFrom(Object fromValue, boolean inclusive) throws IndexPropertyAccessException{
        return searchFrom(null, fromValue, inclusive);
    }
    public Set searchFrom(Set result, Object fromValue, boolean inclusive) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(fromValue);
        return searchFromProperty(result, fromProp, inclusive);
    }
    
    public Set searchFromProperty(Object fromProp, boolean inclusive){
        return searchFromProperty(null, fromProp, inclusive);
    }
    public synchronized Set searchFromProperty(Set result, Object fromProp, boolean inclusive){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexKeyMap.tailMap(fromProp, inclusive).values().iterator();
        while(itr.hasNext()){
            Set keys = (Set)itr.next();
            result.addAll(keys);
        }
        return result;
    }
    
    public Set searchTo(Object toValue, boolean inclusive) throws IndexPropertyAccessException{
        return searchTo(null, toValue, inclusive);
    }
    public Set searchTo(Set result, Object toValue, boolean inclusive) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object toProp = indexKeyFactory.createIndexKey(toValue);
        return searchToProperty(result, toProp, inclusive);
    }
    
    public Set searchToProperty(Object toProp, boolean inclusive){
        return searchToProperty(null, toProp, inclusive);
    }
    public synchronized Set searchToProperty(Set result, Object toProp, boolean inclusive){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        Iterator itr = indexKeyMap.headMap(toProp, inclusive).values().iterator();
        while(itr.hasNext()){
            Set keys = (Set)itr.next();
            result.addAll(keys);
        }
        return result;
    }
    
    public Set searchRange(Object fromValue, boolean fromInclusive, Object toValue, boolean toInclusive) throws IndexPropertyAccessException{
        return searchRange(null, fromValue, fromInclusive, toValue, toInclusive);
    }
    public Set searchRange(Set result, Object fromValue, boolean fromInclusive, Object toValue, boolean toInclusive) throws IndexPropertyAccessException{
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        Object fromProp = indexKeyFactory.createIndexKey(fromValue);
        Object toProp = indexKeyFactory.createIndexKey(toValue);
        return searchRangeProperty(result, fromProp, fromInclusive, toProp, toInclusive);
    }
    
    public Set searchRangeProperty(Object fromProp, boolean fromInclusive, Object toProp, boolean toInclusive){
        return searchRangeProperty(null, fromProp, fromInclusive, toProp, toInclusive);
    }
    public synchronized Set searchRangeProperty(Set result, Object fromProp, boolean fromInclusive, Object toProp, boolean toInclusive){
        final int indexKeySize = indexKeyFactory.getPropertyNames().size();
        if(indexKeySize != 1){
            throw new UnsupportedOperationException("This method is not supported, beacause this index is complex key index.");
        }
        if(result == null){
            result = new HashSet();
        }
        if(fromProp == null){
            return searchToProperty(result, toProp, toInclusive);
        }else if(toProp == null){
            return searchFromProperty(result, fromProp, fromInclusive);
        }else{
            Iterator itr = indexKeyMap.subMap(fromProp, fromInclusive, toProp, toInclusive).values().iterator();
            while(itr.hasNext()){
                Set keys = (Set)itr.next();
                result.addAll(keys);
            }
            return result;
        }
    }

    
    public void writeExternal(ObjectOutput out) throws IOException{
        writeExternal(out, true);
    }
    public void writeExternal(ObjectOutput out, boolean writeValue) throws IOException{
        out.writeObject(name);
        out.writeObject(indexKeyFactory);
        synchronized(this){
            out.writeObject(linkedIndex);
            if(writeValue){
                out.writeObject(indexKeyMap);
                out.writeObject(notNullKeySet);
            }
        }
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        readExternal(in, true);
    }
    public void readExternal(ObjectInput in, boolean readValue) throws IOException, ClassNotFoundException{
        name = (String)in.readObject();
        indexKeyFactory = (BeanTableIndexKeyFactory)in.readObject();
        linkedIndex = (Set)in.readObject();
        if(readValue){
            indexKeyMap = (TreeMap)in.readObject();
            notNullKeySet = (Set)in.readObject();
        }
    }
    
    public SharedContextIndex cloneEmpty(){
        SharedContextIndex clone = null;
        try{
            clone = (SharedContextIndex)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.indexKeyMap = new TreeMap(new ComparableComparator());
        clone.notNullKeySet = new HashSet();
        clone.linkedIndex = new HashSet();
        return clone;
    }
    
    public SharedContextIndex clone(Set keySet){
        SharedContextIndex clone = null;
        try{
            clone = (SharedContextIndex)super.clone();
        }catch(CloneNotSupportedException e){
            return null;
        }
        clone.indexKeyMap = new TreeMap(new ComparableComparator());
        clone.notNullKeySet = new HashSet();
        clone.linkedIndex = new HashSet();
        synchronized(this){
            Iterator itr = indexKeyMap.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                Set set = (Set)entry.getValue();
                Set newSet = null;
                Iterator keys = keySet.iterator();
                while(keys.hasNext()){
                    Object key = keys.next();
                    if(set.contains(key)){
                        if(newSet == null){
                            newSet = new HashSet();
                        }
                        newSet.add(key);
                    }
                }
                if(newSet != null){
                    clone.indexKeyMap.put(entry.getKey(), newSet);
                }
            }
            Iterator keys = keySet.iterator();
            while(keys.hasNext()){
                Object key = keys.next();
                if(notNullKeySet.contains(key)){
                    clone.notNullKeySet.add(key);
                }
            }
        }
        return clone;
    }
    
    protected static class DefaultSharedContextIndexKeyFactory implements BeanTableIndexKeyFactory, Externalizable{
        protected List indexedProperties = new ArrayList();
        protected Set indexedPropertyNames = new HashSet();
        
        public DefaultSharedContextIndexKeyFactory(){}
        
        public DefaultSharedContextIndexKeyFactory(String[] propNames){
            for(int i = 0; i < propNames.length; i++){
                SimpleProperty prop = new SimpleProperty(propNames[i]);
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
        
        public Object createIndexKey(Object value) throws IndexPropertyAccessException{
            final int indexKeySize = indexedProperties.size();
            if(indexKeySize == 1){
                try{
                    return ((Property)indexedProperties.get(0)).getProperty(value);
                }catch(NoSuchPropertyException e){
                    throw new IndexPropertyAccessException(
                        value == null ? null : value.getClass(),
                        ((Property)indexedProperties.get(0)).getPropertyName(),
                        e
                    );
                }catch(InvocationTargetException e){
                    throw new IndexPropertyAccessException(
                        value == null ? null : value.getClass(),
                        ((Property)indexedProperties.get(0)).getPropertyName(),
                        ((InvocationTargetException)e).getTargetException()
                    );
                }
            }else{
                ComplexKey indexKey = new ComplexKey(indexKeySize);
                for(int i = 0; i < indexKeySize; i++){
                    SimpleProperty prop = (SimpleProperty)indexedProperties.get(i);
                    try{
                        indexKey.set(i, prop.getProperty(value));
                    }catch(NoSuchPropertyException e){
                        throw new IndexPropertyAccessException(
                            value == null ? null : value.getClass(),
                            ((Property)indexedProperties.get(0)).getPropertyName(),
                            e
                        );
                    }catch(InvocationTargetException e){
                        throw new IndexPropertyAccessException(
                            value == null ? null : value.getClass(),
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
            out.writeInt(indexedProperties.size());
            for(int i = 0; i < indexedProperties.size(); i++){
                SimpleProperty prop = (SimpleProperty)indexedProperties.get(i);
                out.writeObject(prop.getPropertyName());
            }
        }
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
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
        
        private static final long serialVersionUID = 6961807867939374647L;
        
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
