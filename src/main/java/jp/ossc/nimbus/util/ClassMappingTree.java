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
package jp.ossc.nimbus.util;

import java.util.*;

/**
 * クラスに対する任意のオブジェクトのマッピングツリー。<p>
 * あるクラスに対応する任意のオブジェクトを、クラスの継承関係のツリーでマッピングして管理する。<br>
 * {@link #add(Class, Object)}でクラスと任意のオブジェクトのマッピングを登録できる。{@link #getValue(Class)}でクラスを指定すると対応するオブジェクトを取得できる。ここで言う対応するオブジェクトとは、指定したクラスに一致するクラスにマッピングされているオブジェクト、または、一致するクラスがない場合は、登録されているクラスの中で最も近い継承関係にあるスーパークラスにマッピングされたオブジェクトを指す。また、ここで言うクラスには、インタフェースも含まれる。<br>
 * 
 * @author M.Takata
 */
public class ClassMappingTree implements java.io.Serializable{
    
    private static final long serialVersionUID = 4471328701464216810L;
    
    private final TreeElement rootElement;
    
    private final Map classMap = new HashMap();
    
    /**
     * nullを{@link java.lang.Object}クラスにマッピングして、マッピングツリーのルートとして、このクラスのインスタンスを生成する。<p>
     */
    public ClassMappingTree(){
        this(null);
    }
    
    /**
     * 指定されたオブジェクトを{@link java.lang.Object}クラスにマッピングして、マッピングツリーのルートとして、このクラスのインスタンスを生成する。<p>
     *
     * @param root java.lang.Objectクラスに対応するオブジェクト
     */
    public ClassMappingTree(Object root){
        rootElement = new TreeElement(java.lang.Object.class, root);
        classMap.put(java.lang.Object.class, rootElement);
    }
    
    /**
     * クラスと任意のオブジェクトのマッピングを登録する。<p>
     *
     * @param clazz クラスオブジェクト
     * @param value 任意のオブジェクト
     */
    public void add(Class clazz, Object value){
        add(clazz, value, false);
    }
    
    /**
     * クラスと任意のオブジェクトのマッピングを登録する。<p>
     *
     * @param clazz クラスオブジェクト
     * @param value 任意のオブジェクト
     * @param replace 既に同じクラスに対してマッピングされたオブジェクトがある場合に、それと置き換える場合は、true。クラスに対してマッピングされたオブジェクトを複数許容して、追加する場合は、false
     */
    public void add(Class clazz, Object value, boolean replace){
        if(classMap.containsKey(clazz)){
            final TreeElement element = (TreeElement)classMap.get(clazz);
            if(replace){
                element.setTarget(value);
            }else{
                element.addTarget(value);
            }
        }else{
            TreeElement element = new TreeElement(clazz, value);
            final TreeElement nearestParent
                 = rootElement.getNearestParentElement(clazz);
            if(nearestParent != null){
                if(nearestParent.hasChild()){
                    final Iterator children
                         = nearestParent.getChildElements().iterator();
                    while(children.hasNext()){
                        final TreeElement child = (TreeElement)children.next();
                        if(child.isChildOf(clazz)){
                            nearestParent.moveChild(element, child);
                        }
                    }
                }
                element = nearestParent.addChild(element);
            }
            classMap.put(clazz, element);
        }
    }
    
    /**
     * 指定されたクラスに対応するオブジェクトを取得する。<p>
     * ここで言う対応するオブジェクトとは、指定したクラスに一致するクラスにマッピングされているオブジェクトを指す。<br>
     * 対応するオブジェクトが複数存在する場合には、最初に登録されたオブジェクトを返す。<br>
     *
     * @param clazz クラスオブジェクト
     * @return 指定されたクラスに対応するオブジェクト
     * @see #getValuesOf(Class)
     */
    public Object getValueOf(Class clazz){
        final Object[] values = getValuesOf(clazz);
        return values != null && values.length > 0 ? values[0] : null;
    }
    
    /**
     * 指定されたクラスに対応するオブジェクト配列を取得する。<p>
     *
     * @param clazz クラスオブジェクト
     * @return 指定されたクラスに対応するオブジェクト配列
     */
    public Object[] getValuesOf(Class clazz){
        final List list = getValueListOf(clazz);
        return list == null ? null : list.toArray();
    }
    
    /**
     * 指定されたクラスに対応するオブジェクトリストを取得する。<p>
     *
     * @param clazz クラスオブジェクト
     * @return 指定されたクラスに対応するオブジェクトリスト
     */
    public List getValueListOf(Class clazz){
        if(classMap.containsKey(clazz)){
            return ((TreeElement)classMap.get(clazz)).getTargets();
        }else{
            return null;
        }
    }
    
    /**
     * 指定されたクラスに対応するオブジェクトを取得する。<p>
     * ここで言う対応するオブジェクトとは、指定したクラスに一致するクラスにマッピングされているオブジェクト、または、一致するクラスがない場合は、登録されているクラスの中で最も近い継承関係にあるスーパークラスにマッピングされたオブジェクトを指す。<br>
     * 対応するオブジェクトが複数存在する場合には、最初に登録されたオブジェクトを返す。<br>
     *
     * @param clazz クラスオブジェクト
     * @return 指定されたクラスに対応するオブジェクト
     * @see #getValues(Class)
     */
    public Object getValue(Class clazz){
        final Object[] values = getValues(clazz);
        return values != null && values.length > 0 ? values[0] : null;
    }
    
    /**
     * 指定されたクラスに対応するオブジェクト配列を取得する。<p>
     *
     * @param clazz クラスオブジェクト
     * @return 指定されたクラスに対応するオブジェクト配列
     */
    public Object[] getValues(Class clazz){
        return getValueList(clazz).toArray();
    }
    
    /**
     * 指定されたクラスに対応するオブジェクトリストを取得する。<p>
     *
     * @param clazz クラスオブジェクト
     * @return 指定されたクラスに対応するオブジェクトリスト
     */
    public List getValueList(Class clazz){
        if(clazz == null){
            return rootElement.getTargets();
        }
        if(classMap.containsKey(clazz)){
            return ((TreeElement)classMap.get(clazz)).getTargets();
        }else{
            final TreeElement nearestParent
                 = rootElement.getNearestParentElement(clazz);
            if(nearestParent == null){
                return rootElement.getTargets();
            }
            return nearestParent.getTargets();
        }
    }
    
    /**
     * 指定されたクラスにマッピングされている指定オブジェクトを削除する。<p>
     *
     * @param clazz クラスオブジェクト
     * @param value 任意のオブジェクト
     */
    public void remove(Class clazz, Object value){
        if(classMap.containsKey(clazz)){
            final TreeElement removeElement
                 = (TreeElement)classMap.get(clazz);
            removeElement.removeTarget(value);
            if(removeElement.getTargets().size() == 0){
                remove(clazz);
            }
        }
    }
    
    /**
     * 指定されたクラスにマッピングされている全てのオブジェクトを削除する。<p>
     *
     * @param clazz クラスオブジェクト
     */
    public void remove(Class clazz){
        if(!Object.class.equals(clazz) && classMap.containsKey(clazz)){
            final TreeElement removedElement
                 = (TreeElement)classMap.remove(clazz);
            if(removedElement.parent != null){
                removedElement.parent.removeChild(removedElement);
            }
        }
    }
    
    /**
     * マッピングされている全てのオブジェクトを削除する。<p>
     */
    public void clear(){
        final Iterator classes = new HashSet(classMap.keySet()).iterator();
        while(classes.hasNext()){
            final Class clazz = (Class)classes.next();
            remove(clazz);
        }
    }
    
    /**
     * 登録されているクラスマッピングの文字列表現を返す。<p>
     *
     * @return クラスマッピングの文字列表現
     */
    public String toString(){
        return super.toString() + classMap;
    }
    
    private static class TreeElement implements java.io.Serializable{
        
        private static final long serialVersionUID = 4875545362697617699L;
        
        TreeElement parent;
        private Map children;
        Class clazz;
        private List targets = new ArrayList();
        public TreeElement(Class clazz, Object target){
            this(null, clazz, target);
        }
        public TreeElement(TreeElement parent, Class clazz, Object target){
            this.parent = parent;
            this.clazz = clazz;
            setTarget(target);
        }
        public TreeElement addChild(TreeElement child){
            if(children == null){
                children = new HashMap();
            }
            if(children.containsKey(child.clazz)){
                final TreeElement element
                     = (TreeElement)children.get(child.clazz);
                element.addTargets(child.getTargets());
                return element;
            }else{
                children.put(child.clazz, child);
                child.parent = this;
                return child;
            }
        }
        public TreeElement getChild(Class clazz){
            if(children == null){
                return null;
            }
            return (TreeElement)children.get(clazz);
        }
        public Collection getChildElements(){
            return children == null ? null : new HashSet(children.values());
        }
        public int childrenNumber(){
            return children == null ? 0 : children.size();
        }
        public void moveChild(TreeElement newParent, TreeElement child){
            children.remove(child.clazz);
            newParent.addChild(child);
        }
        public void removeChild(TreeElement child){
            final TreeElement removedChild
                 = (TreeElement)children.remove(child.clazz);
            if(removedChild != null && removedChild.hasChild()
                && parent != null && parent.children != null){
                parent.children.putAll(removedChild.children);
            }
        }
        public void setTarget(Object target){
            targets.clear();
            if(target != null){
                targets.add(target);
            }
        }
        public void addTarget(Object target){
            if(target != null){
                targets.add(target);
            }
        }
        public void addTargets(List targets){
            targets.addAll(targets);
        }
        public List getTargets(){
            return targets;
        }
        public void removeTarget(Object target){
            targets.remove(target);
        }
        public boolean hasChild(){
            return children != null && children.size() != 0;
        }
        public TreeElement getNearestParentElement(Class clazz){
            if(!isParentOf(clazz)){
                return null;
            }
            if(!hasChild()){
                return this;
            }
            TreeElement nearestElement = null;
            final Iterator elements = children.values().iterator();
            while(elements.hasNext()){
                final TreeElement element = (TreeElement)elements.next();
                nearestElement = element.getNearestParentElement(clazz);
                if(nearestElement != null){
                    return nearestElement;
                }
            }
            return this;
        }
        public boolean isChildOf(Class clazz){
            return clazz.isAssignableFrom(this.clazz);
        }
        public boolean isParentOf(Class clazz){
            return this.clazz.isAssignableFrom(clazz);
        }
        public Object getTarget(){
            return targets.size() == 0 ? null : targets.get(0);
        }
        public boolean equals(Object obj){
            if(obj == null){
                return false;
            }
            if(obj == this){
                return true;
            }
            if(!(obj instanceof TreeElement)){
                return false;
            }
            final TreeElement element = (TreeElement)obj;
            if(clazz == null){
                return element.clazz == null;
            }
            return clazz.equals(element.clazz);
        }
        public int hashCode(){
            return clazz == null ? 0 : clazz.hashCode();
        }
        public String toString(){
            return targets.toString();
        }
    }
}
