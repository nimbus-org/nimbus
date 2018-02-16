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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.Comparator;
import java.util.RandomAccess;
import java.io.Serializable;

/**
 * Beanテーブル。<p>
 * Beanをテーブルのレコードに、Beanのプロパティをカラムに見立て、インデックスを張り、検索を行えるようにしたリストです。<br>
 * <pre>
 * class User{
 *     private String firstName;
 *     private String lastName;
 *     private String sex;
 *     private int age;
 *     public User(){}
 *     public User(String firstName, String lastName, String sex, int age){
 *         this.firstName = firstName;
 *         this.lastName = lastName;
 *         this.sex = sex;
 *         this.age = age;
 *     }
 *     public String getFirstName(){
 *         return firstName;
 *     }
 *     public String getLastName(){
 *         return lastName;
 *     }
 *     public String getSex(){
 *         return sex;
 *     }
 *     public int getAge(){
 *         return age;
 *     }
 * }
 * 
 * BeanTable table = new BeanTable(User.class);
 * 
 * // Userクラスのプロパティ"lastName"と"sex"に対してインデックスを張る
 * table.setIndex("INDEX_LASTNAME_SEX", "lastName", "sex");
 * 
 * // Userクラスのプロパティ"age"に対してインデックスを張る
 * table.setIndex("INDEX_AGE", "age");
 * 
 * // レコードを追加する
 * table.add(new User("kotarou", "fuga", "male", 50));
 * table.add(new User("satoko", "fuga", "female", 49));
 * table.add(new User("tarou", "hoge", "male", 30));
 * table.add(new User("hanako", "hoge", "female", 28));
 * table.add(new User("junko", "hoge", "female", 20));
 * table.add(new User("mika", "hoge", "female", 10));
 * table.add(new User("ichirou, "hoge"", "male", 5));
 * 
 * // 検索用のビューを作成する
 * BeanTableView view = table.createView();
 * 
 * // 検索キーを生成する
 * User key = new User();
 * key.setLastName("hoge");
 * key.setSex("female");
 * key.setAge(20);
 * 
 * // "lastName"と"sex"の合致を条件に検索
 * // 更に"age"が20以上で検索
 * // その結果を"age"と"sex"で降順ソートして取得する
 * // インデックスの指定は、インデックス名またはプロパティ名で指定する
 * List resultSet = view.searchByElement(key, "INDEX_LASTNAME_SEX")
 *                            .searchFromElement(key, true, null, "age")
 *                            .getResultList(new String[]{"age", "sex"}, new boolean{false, false});
 * </pre>
 *
 * @author M.Takata
 */
public class BeanTable implements List, RandomAccess, Serializable, Cloneable{
    
    private static final long serialVersionUID = 619195842574715977L;
    
    protected BeanTableIndexManager indexManager;
    protected final boolean isSynchronized;
    protected List list;
    protected int modCount = 0;
    protected Comparator sortedComparator;
    
    /**
     * 指定されたBeanクラスを格納するテーブルを作成する。<p>
     * 同期化されない。<br>
     *
     * @param elementClass テーブルに格納するレコードとなるBeanのクラスオブジェクト
     */
    public BeanTable(Class elementClass){
        this(elementClass, false);
    }
    
    /**
     * 指定されたBeanクラスを格納するテーブルを作成する。<p>
     *
     * @param elementClass テーブルに格納するレコードとなるBeanのクラスオブジェクト
     * @param isSynchronized 同期化するかどうか。同期化する場合は、true
     */
    public BeanTable(Class elementClass, boolean isSynchronized){
        indexManager = new BeanTableIndexManager(elementClass, isSynchronized);
        this.isSynchronized = isSynchronized;
        list = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
    }
    
    /**
     * 指定されたBeanクラスを格納するテーブルを作成する。<p>
     * 同期化されない。<br>
     *
     * @param elementClass テーブルに格納するレコードとなるBeanのクラスオブジェクト
     * @param c 初期レコードとなるBeanの集合
     */
    public BeanTable(Class elementClass, Collection c){
        this(elementClass, c, false);
    }
    
    /**
     * 指定されたBeanクラスを格納するテーブルを作成する。<p>
     *
     * @param elementClass テーブルに格納するレコードとなるBeanのクラスオブジェクト
     * @param c 初期レコードとなるBeanの集合
     * @param isSynchronized 同期化するかどうか。同期化する場合は、true
     */
    public BeanTable(Class elementClass, Collection c, boolean isSynchronized){
        this(elementClass, isSynchronized);
        addAll(c);
    }
    
    /**
     * 指定されたBeanクラスを格納するテーブルを作成する。<p>
     * 同期化されない。<br>
     *
     * @param elementClass テーブルに格納するレコードとなるBeanのクラスオブジェクト
     * @param initialCapacity 初期容量
     */
    public BeanTable(Class elementClass, int initialCapacity){
        this(elementClass, initialCapacity, false);
    }
    
    /**
     * 指定されたBeanクラスを格納するテーブルを作成する。<p>
     *
     * @param elementClass テーブルに格納するレコードとなるBeanのクラスオブジェクト
     * @param initialCapacity 初期容量
     * @param isSynchronized 同期化するかどうか。同期化する場合は、true
     */
    public BeanTable(Class elementClass, int initialCapacity, boolean isSynchronized){
        indexManager = new BeanTableIndexManager(elementClass, isSynchronized);
        this.isSynchronized = isSynchronized;
        list = isSynchronized ? Collections.synchronizedList(new ArrayList(initialCapacity)) : new ArrayList(initialCapacity);
    }
    
    /**
     * テーブルに格納するレコードとなるBeanのクラスオブジェクトを取得する。<p>
     *
     * @return レコードとなるBeanのクラスオブジェクト
     */
    public Class getElementClass(){
        return indexManager.getElementClass();
    }
    
    /**
     * インデックスを追加する。<p>
     * インデックには、単一のプロパティで構成される単純インデックスと、複数のプロパティで構成される複合インデックスが存在する。<br>
     * 複合インデックスを追加した場合は、自動的にその要素となる単一プロパティの単純インデックスも内部的に生成される。<p>
     * 但し、自動生成された単一インデックスは、インデックス名を持たないため、インデックス名では指定できず、プロパティ名で指定して使用する。<br>
     * インデックスの種類によって、使用できる検索機能が異なる。単純インデックスは、一致検索と範囲検索の両方が可能だが、複合インデックスは、一致検索のみ可能である。<br>
     *
     * @param name インデックス名
     * @param props インデックスを張るBeanのプロパティ名配列
     * @exception NoSuchPropertyException 指定されたプロパティがBeanに存在しない場合
     */
    public void setIndex(String name, String[] props) throws NoSuchPropertyException{
        indexManager.setIndex(name, props);
    }
    
    /**
     * カスタマイズしたインデックスを追加する。<p>
     *
     * @param name インデックス名
     * @param keyFactory インデックスのキーを生成するファクトリ
     * @see #setIndex(String, String[])
     */
    public void setIndex(String name, BeanTableIndexKeyFactory keyFactory){
        indexManager.setIndex(name, keyFactory);
    }
    
    /**
     * インデックスを削除する。<p>
     *
     * @param name インデックス名
     */
    public void removeIndex(String name){
        indexManager.removeIndex(name);
    }
    
    /**
     * インデックスを再解析する。<p>
     */
    public void analyzeIndex(){
        indexManager.clear();
        indexManager.addAll(list);
    }
    
    /**
     * 検索を行うビューを作成する。<p>
     * 
     * @return 検索ビュー
     */
    public BeanTableView createView(){
        return new BeanTableView(indexManager);
    }
    
    /**
     * このテーブル自体を指定したBeanのプロパティ名で昇順ソートする。<p>
     *
     * @param propNames ソートするBeanのプロパティ名配列
     */
    public void sort(String[] propNames){
        sort(propNames, null);
    }
    
    /**
     * このテーブル自体を指定したBeanのプロパティ名で指定されたソート方向にソートする。<p>
     *
     * @param propNames ソートするBeanのプロパティ名配列
     * @param isAsc propNamesで指定したプロパティ名のソート方向を示す配列。trueを指定すると昇順
     */
    public void sort(String[] propNames, boolean[] isAsc){
        sortedComparator = BeanTableView.sort(getElementClass(), list, propNames, isAsc);
    }
    
    /**
     * バイナリサーチアルゴリズムを使用して、指定されたオブジェクトを検索します。<p>
     * リストは、この呼び出しの前に、{@link #sort(String[])}メソッドを使用して、ソートしなければいけません。
     * リストがソートされていない場合、結果は定義されません。
     * 指定されたオブジェクトと等しい要素がリストに複数ある場合、どれが見つかるかは保証されません。
     *
     * @param key 検索されるキー要素
     * @return リスト内に検索キーがある場合は検索キーのインデックス。それ以外の場合は (-(挿入ポイント) - 1)。挿入ポイントは、そのキーがリストに挿入されるポイントとして定義される。つまり、そのキーよりも大きな最初の要素のインデックス。リスト内のすべての要素が指定されたキーよりも小さい場合は size()。これにより、キーが見つかった場合にのみ戻り値が >= 0 になることが保証される。
     */
    public int binarySearch(Object key){
        return Collections.binarySearch(list, key, sortedComparator);
    }
    
    public int size(){
        return list.size();
    }
    
    public boolean isEmpty(){
        return list.isEmpty();
    }
    
    public boolean contains(Object o){
        return list.contains(o);
    }
    
    public Iterator iterator(){
        return new BeanTableIterator();
    }
    
    public ListIterator listIterator(){
        return listIterator(0);
    }
    
    public ListIterator listIterator(int index){
        return new BeanTableListIterator(index);
    }
    
    public List subList(int fromIndex, int toIndex){
        return Collections.unmodifiableList(list.subList(fromIndex, toIndex));
    }
    
    public Object[] toArray(){
        return list.toArray();
    }
    
    public Object[] toArray(Object[] a){
        return list.toArray(a);
    }
    
    public Object get(int index){
        return list.get(index);
    }
    
    public int indexOf(Object o){
        return list.indexOf(o);
    }
    
    public int lastIndexOf(Object o){
        return list.lastIndexOf(o);
    }
    
    public boolean containsAll(Collection c){
        return list.containsAll(c);
    }
    
    public boolean add(Object element){
        if(element == null){
            return false;
        }
        indexManager.add(element);
        boolean isAdd = list.add(element);
        if(isAdd){
            modCount++;
        }
        return isAdd;
    }
    
    public void add(int index, Object element){
        if(element == null){
            return;
        }
        indexManager.add(element);
        list.add(index, element);
        modCount++;
    }
    
    public Object set(int index, Object element){
        Object old = list.set(index, element);
        indexManager.replace(old, element);
        modCount++;
        return old;
    }
    
    public boolean addAll(Collection c){
        if(list.addAll(c)){
            indexManager.addAll(c);
            modCount++;
            return true;
        }else{
            return false;
        }
    }
    
    public boolean addAll(int index, Collection c){
        if(list.addAll(index, c)){
            indexManager.addAll(c);
            modCount++;
            return true;
        }else{
            return false;
        }
    }
    
    public boolean remove(Object o){
        boolean isRemoved = list.remove(o);
        if(isRemoved){
            indexManager.remove(o);
            modCount++;
        }
        return isRemoved;
    }
    
    public Object remove(int index){
        Object ret = list.remove(index);
        indexManager.remove(ret);
        modCount++;
        return ret;
    }
    
    public boolean removeAll(Collection c){
        if(list.removeAll(c)){
            Iterator itr = c.iterator();
            while(itr.hasNext()){
                Object element = itr.next();
                indexManager.remove(element);
            }
            modCount++;
            return true;
        }else{
            return false;
        }
    }
    
    public boolean retainAll(Collection c){
        if(list.retainAll(c)){
            indexManager.retainAll(c);
            modCount++;
            return true;
        }else{
            return false;
        }
    }
    
    public void clear(){
        indexManager.clear();
        list.clear();
        modCount++;
    }
    
    public Object clone(){
        BeanTable clone = null;
        try{
            clone = (BeanTable)super.clone();
        }catch(CloneNotSupportedException e){
        }
        clone.list = isSynchronized ? Collections.synchronizedList(new ArrayList()) : new ArrayList();
        clone.indexManager = new BeanTableIndexManager(getElementClass(), isSynchronized);
        clone.addAll(this);
        return clone;
    }
    
    protected class BeanTableIterator implements Iterator, Serializable{
        
        private static final long serialVersionUID = -7202550703286618072L;
        
        protected int cursor = 0;
        protected int lastRet = -1;
        protected int expectedModCount = modCount;
        
        public boolean hasNext(){
            return cursor != size();
        }
        
        public Object next(){
            checkForComodification();
            try{
                Object next = get(cursor);
                lastRet = cursor++;
                return next;
            }catch(IndexOutOfBoundsException e){
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        public void remove(){
            if(lastRet == -1){
                throw new IllegalStateException();
            }
            checkForComodification();
            
            try{
                BeanTable.this.remove(lastRet);
                if(lastRet < cursor){
                    cursor--;
                }
                lastRet = -1;
                expectedModCount++;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
        
        final void checkForComodification(){
            if(modCount != expectedModCount){
                throw new ConcurrentModificationException();
            }
        }
    }
    
    protected class BeanTableListIterator extends BeanTableIterator implements ListIterator{
        
        private static final long serialVersionUID = 8909520626834440479L;
        
        public BeanTableListIterator(int index){
            cursor = index;
        }
        
        public boolean hasPrevious(){
            return cursor != 0;
        }
        
        public Object previous(){
            checkForComodification();
            try{
                int i = cursor - 1;
                Object previous = get(i);
                lastRet = cursor = i;
                return previous;
            }catch(IndexOutOfBoundsException e){
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        public int nextIndex(){
            return cursor;
        }
        
        public int previousIndex(){
            return cursor - 1;
        }
        
        public void set(Object o){
            if(lastRet == -1){
                throw new IllegalStateException();
            }
            checkForComodification();
            
            try{
                BeanTable.this.set(lastRet, o);
                expectedModCount++;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
        
        public void add(Object o){
            checkForComodification();
            
            try{
                BeanTable.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount++;
            }catch(IndexOutOfBoundsException e){
                throw new ConcurrentModificationException();
            }
        }
    }
}