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
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

import jp.ossc.nimbus.beans.dataset.Record;
import jp.ossc.nimbus.beans.dataset.RecordList;

/**
 * {@link BeanTable Beanテーブル}の検索ビュー。<p>
 *
 * @author M.Takata
 * @see BeanTable
 */
public class BeanTableView implements Cloneable{
    
    protected static final int OPERATOR_AND   = 1;
    protected static final int OPERATOR_OR    = 2;
    protected static final int OPERATOR_NAND  = 3;
    protected static final int OPERATOR_NOR   = 4;
    protected static final int OPERATOR_XOR   = 5;
    protected static final int OPERATOR_XNOR  = 6;
    protected static final int OPERATOR_IMP   = 7;
    protected static final int OPERATOR_NIMP  = 8;
    protected static final int OPERATOR_CIMP  = 9;
    protected static final int OPERATOR_CNIMP = 10;
    
    protected BeanTableIndexManager indexManager;
    protected Set resultSet;
    protected int operator = OPERATOR_AND;
    
    /**
     * 指定されたインデックス管理を使って検索を行うビューを生成する。<p>
     *
     * @param manager インデックス管理
     */
    public BeanTableView(BeanTableIndexManager manager){
        indexManager = manager;
    }
    
    /**
     * 検索結果のBean集合を取得する。<p>
     *
     * @return 検索結果のBean集合
     */
    public Set getResultSet(){
        return resultSet == null ? indexManager.elements() : resultSet;
    }
    
    /**
     * 検索結果のBeanリストを取得する。<p>
     *
     * @return 検索結果のBeanリスト
     */
    public List getResultList(){
        return new ArrayList(resultSet == null ? indexManager.elements() : resultSet);
    }
    
    /**
     * 検索結果のBean集合を指定したプロパティ名で昇順ソートして取得する。<p>
     *
     * @param propNames ソートするBeanのプロパティ名配列
     * @return 検索結果のソート済みBeanリスト
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public List getResultList(String[] propNames) throws IndexPropertyAccessException{
        return getResultList(propNames, null);
    }
    
    /**
     * 検索結果のBean集合を指定したBeanのプロパティ名で指定されたソート方向にソートして取得する。<p>
     *
     * @param propNames ソートするBeanのプロパティ名配列
     * @param isAsc propNamesで指定したプロパティ名のソート方向を示す配列。trueを指定すると昇順
     * @return 検索結果のソート済みBeanリスト
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public List getResultList(String[] propNames, boolean[] isAsc) throws IndexPropertyAccessException{
        List result = new ArrayList();
        result.addAll(resultSet == null ? indexManager.elements() : resultSet);
        if(result.size() < 2 || propNames == null || propNames.length == 0){
            return result;
        }
        if(Record.class.isAssignableFrom(indexManager.getElementClass())){
            RecordList.sort(result, propNames, isAsc);
        }else{
            Collections.sort(result, new BeanComparator(indexManager.getElementClass(), propNames, isAsc));
        }
        return result;
    }
    
    /**
     * 検索結果のBean集合から指定したBeanのプロパティを重複削除した値の集合を取得する。<p>
     *
     * @param propName プロパティ名
     * @return 指定したBeanのプロパティを重複削除した値の集合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public Set getResultDistinctValueSet(String propName) throws IndexPropertyAccessException{
        return (Set)getResultDistinctValueCollection(propName, false, false);
    }
    
    /**
     * 検索結果のBean集合から指定したBeanのプロパティを重複削除した値のリストを取得する。<p>
     *
     * @param propName プロパティ名
     * @param isAsc propNameで指定したプロパティ名のソート方向を示すフラグ。trueを指定すると昇順
     * @return 指定したBeanのプロパティを重複削除した値のリスト
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public List getResultDistinctValueList(String propName, boolean isAsc) throws IndexPropertyAccessException{
        return (List)getResultDistinctValueCollection(propName, true, isAsc);
    }
    
    protected Collection getResultDistinctValueCollection(String propName, boolean isSort, boolean isAsc) throws IndexPropertyAccessException{
        SimpleProperty prop = new SimpleProperty(propName);
        Set distinctSet = new HashSet();
        try{
            Set elements = resultSet == null ? indexManager.elements() : resultSet;
            Iterator itr = elements.iterator();
            while(itr.hasNext()){
                Object element = itr.next();
                distinctSet.add(prop.getProperty(element));
            }
        }catch(NoSuchPropertyException e){
            throw new IndexPropertyAccessException(
                indexManager.getElementClass(),
                propName,
                e
            );
        }catch(InvocationTargetException e){
            throw new IndexPropertyAccessException(
                indexManager.getElementClass(),
                propName,
                ((InvocationTargetException)e).getTargetException()
            );
        }
        if(isSort){
            List result = new ArrayList();
            result.addAll(distinctSet);
            if(result.size() < 2){
                return result;
            }
            Collections.sort(result, new BeanComparator(isAsc));
            return result;
        }else{
            return distinctSet;
        }
    }
    
    /**
     * 検索結果のBean集合から指定したBeanのプロパティの最大値を取得する。<p>
     *
     * @param propName プロパティ名
     * @return 指定したBeanのプロパティの最大値
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public Object getResultMaxValue(String propName) throws IndexPropertyAccessException{
        List sortedList = getResultList(new String[]{propName}, new boolean[]{false});
        if(sortedList.size() == 0){
            return null;
        }else{
            try{
                return new SimpleProperty(propName).getProperty(sortedList.get(0));
            }catch(NoSuchPropertyException e){
                throw new IndexPropertyAccessException(
                    indexManager.getElementClass(),
                    propName,
                    e
                );
            }catch(InvocationTargetException e){
                throw new IndexPropertyAccessException(
                    indexManager.getElementClass(),
                    propName,
                    ((InvocationTargetException)e).getTargetException()
                );
            }
        }
    }
    
    /**
     * 検索結果のBean集合から指定したBeanのプロパティの最小値を取得する。<p>
     *
     * @param propName プロパティ名
     * @return 指定したBeanのプロパティの最小値
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public Object getResultMinValue(String propName) throws IndexPropertyAccessException{
        List sortedList = getResultList(new String[]{propName}, new boolean[]{true});
        if(sortedList.size() == 0){
            return null;
        }else{
            try{
                return new SimpleProperty(propName).getProperty(sortedList.get(0));
            }catch(NoSuchPropertyException e){
                throw new IndexPropertyAccessException(
                    indexManager.getElementClass(),
                    propName,
                    e
                );
            }catch(InvocationTargetException e){
                throw new IndexPropertyAccessException(
                    indexManager.getElementClass(),
                    propName,
                    ((InvocationTargetException)e).getTargetException()
                );
            }
        }
    }
    
    protected static Comparator sort(Class elementClass, List list, String[] propNames, boolean[] isAsc) throws IndexPropertyAccessException{
        if(list.size() <= 1){
            return Record.class.isAssignableFrom(elementClass)
                ? (Comparator)new RecordList.RecordComparator(propNames, isAsc)
                    : (Comparator)new BeanComparator(elementClass, propNames, isAsc);
        }
        Comparator c = Record.class.isAssignableFrom(elementClass)
            ? (Comparator)new RecordList.RecordComparator(((Record)list.get(0)).getRecordSchema(), propNames, isAsc)
                : (Comparator)new BeanComparator(elementClass, propNames, isAsc);
        Collections.sort(list, c);
        return c;
    }
    
    /**
     * 論理演算状態を論理積（AND）にする。<p>
     * デフォルトの論理演算状態です。<br>
     *
     * @return このビュー
     */
    public BeanTableView and(){
        operator = OPERATOR_AND;
        return this;
    }
    
    /**
     * 論理演算状態を論理和（OR）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView or(){
        operator = OPERATOR_OR;
        return this;
    }
    
    /**
     * 論理演算状態を否定論理積（NAND）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView nand(){
        operator = OPERATOR_NAND;
        return this;
    }
    
    /**
     * 論理演算状態を否定論理和（NOR）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView nor(){
        operator = OPERATOR_NOR;
        return this;
    }
    
    /**
     * 論理演算状態を排他的論理和（XOR）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView xor(){
        operator = OPERATOR_XOR;
        return this;
    }
    
    /**
     * 論理演算状態を排他的否定論理和（XNOR）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView xnor(){
        operator = OPERATOR_XNOR;
        return this;
    }
    
    /**
     * 論理演算状態を論理包含（IMP）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView imp(){
        operator = OPERATOR_IMP;
        return this;
    }
    
    /**
     * 論理演算状態を否定論理包含（NIMP）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView nimp(){
        operator = OPERATOR_NIMP;
        return this;
    }
    
    /**
     * 論理演算状態を逆論理包含（CIMP）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView cimp(){
        operator = OPERATOR_CIMP;
        return this;
    }
    
    /**
     * 論理演算状態を逆否定論理包含（CNIMP）にする。<p>
     *
     * @return このビュー
     */
    public BeanTableView cnimp(){
        operator = OPERATOR_CNIMP;
        return this;
    }
    
    protected void operate(Set elements){
        if(elements == null){
            elements = new HashSet(0);
        }
        switch(operator){
        case OPERATOR_OR:
            resultSet.addAll(elements);
            break;
        case OPERATOR_NAND:
            resultSet.retainAll(elements);
            Set all = indexManager.elements();
            all.removeAll(resultSet);
            resultSet = all;
            break;
        case OPERATOR_NOR:
            resultSet.addAll(elements);
            all = indexManager.elements();
            all.removeAll(resultSet);
            resultSet = all;
            break;
        case OPERATOR_XOR:
            Set tmpSet = new HashSet(resultSet);
            tmpSet.retainAll(elements);
            resultSet.addAll(elements);
            resultSet.removeAll(tmpSet);
            break;
        case OPERATOR_XNOR:
            tmpSet = new HashSet(resultSet);
            tmpSet.retainAll(elements);
            resultSet.addAll(elements);
            resultSet.removeAll(tmpSet);
            tmpSet = indexManager.elements();
            tmpSet.removeAll(resultSet);
            resultSet = tmpSet;
            break;
        case OPERATOR_IMP:
            all = indexManager.elements();
            all.removeAll(resultSet);
            all.addAll(elements);
            resultSet = all;
            break;
        case OPERATOR_NIMP:
            resultSet.removeAll(elements);
            break;
        case OPERATOR_CIMP:
            all = indexManager.elements();
            all.removeAll(elements);
            all.addAll(resultSet);
            resultSet = all;
            break;
        case OPERATOR_CNIMP:
            Set targetSet = new HashSet(elements);
            targetSet.removeAll(resultSet);
            resultSet = targetSet;
            break;
        case OPERATOR_AND:
        default:
            resultSet.retainAll(elements);
        }
    }
    
    /**
     * この検索ビューの逆集合をとる。<p>
     * 
     * @return 逆集合をとった結果のこのビュー
     */
    public BeanTableView not(){
        Set all = indexManager.elements();
        all.removeAll(resultSet);
        resultSet = all;
        return this;
    }
    
    /**
     * この検索ビューに指定された検索ビューをAND連結する。<p>
     * 
     * @param view 検索ビュー
     * @return 連結された結果のこのビュー
     */
    public BeanTableView and(BeanTableView view){
        resultSet.retainAll(view.getResultSet());
        return this;
    }
    
    /**
     * この検索ビューに指定された検索ビューをOR連結する。<p>
     * 
     * @param view 検索ビュー
     * @return 連結された結果のこのビュー
     */
    public BeanTableView or(BeanTableView view){
        resultSet.addAll(view.getResultSet());
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの否定論理積（NAND）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView nand(BeanTableView view){
        resultSet.retainAll(view.getResultSet());
        Set all = indexManager.elements();
        all.removeAll(resultSet);
        resultSet = all;
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの否定論理和（NOR）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView nor(BeanTableView view){
        resultSet.addAll(view.getResultSet());
        Set all = indexManager.elements();
        all.removeAll(resultSet);
        resultSet = all;
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの排他的論理和（XOR）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView xor(BeanTableView view){
        Set andSet = new HashSet(resultSet);
        andSet.retainAll(view.getResultSet());
        resultSet.addAll(view.getResultSet());
        resultSet.removeAll(andSet);
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの否定排他的論理和（XNOR）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView xnor(BeanTableView view){
        Set tmpSet = new HashSet(resultSet);
        tmpSet.retainAll(view.getResultSet());
        resultSet.addAll(view.getResultSet());
        resultSet.removeAll(tmpSet);
        tmpSet = indexManager.elements();
        tmpSet.removeAll(resultSet);
        resultSet = tmpSet;
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの論理包含（IMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView imp(BeanTableView view){
        Set all = indexManager.elements();
        all.removeAll(resultSet);
        all.addAll(view.getResultSet());
        resultSet = all;
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの否定論理包含（NIMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView nimp(BeanTableView view){
        resultSet.removeAll(view.getResultSet());
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの逆論理包含（CIMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView cimp(BeanTableView view){
        Set all = indexManager.elements();
        all.removeAll(view.getResultSet());
        all.addAll(resultSet);
        resultSet = all;
        return this;
    }
    
    /**
     * この検索ビューと指定された検索ビューの否定逆論理包含（CNIMP）を行う。<p>
     *
     * @param view 検索ビュー
     * @return 結果となるこのビュー
     */
    public BeanTableView cnimp(BeanTableView view){
        Set targetSet = new HashSet(view.getResultSet());
        targetSet.removeAll(resultSet);
        resultSet = targetSet;
        return this;
    }
    
    /**
     * 指定されたインデックスまたはプロパティ集合に対するインデックスのキー要素の集合を検索する。<p>
     * キー検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     */
    public BeanTableView searchKeyElement(String indexName, String[] propNames) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchKeyElement(resultSet, indexName, propNames);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchKeyElement(indexName, propNames));
        }
        return this;
    }
    
    /**
     * 特定のプロパティがnullとなるBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     */
    public BeanTableView searchNull(String indexName, String propName) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchNull(new HashSet(), indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchNull(indexName, propName));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが非nullとなるBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     */
    public BeanTableView searchNotNull(String indexName, String propName) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchNotNull(new HashSet(), indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchNotNull(indexName, propName));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティと一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param element 検索キーとなるBean
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public BeanTableView searchByElement(
        Object element,
        String indexName,
        String[] propNames
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchByElement(new HashSet(), element, indexName, propNames);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchByElement(element, indexName, propNames));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した複数のBeanの該当するプロパティと一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param indexName インデックス名
     * @param propNames プロパティ名配列
     * @param elements 検索キーとなるBean配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public BeanTableView searchInElement(
        String indexName,
        String[] propNames,
        Object[] elements
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchInElement(resultSet, indexName, propNames, elements);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchInElement(indexName, propNames, elements));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param value 検索キーとなる値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     */
    public BeanTableView searchBy(
        Object value,
        String indexName,
        String propName
    ){
        if(resultSet == null){
            resultSet = indexManager.searchBy(new HashSet(), value, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchBy(value, indexName, propName));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した複数の値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @param values 検索キーとなる値配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、または複合インデックスの場合
     */
    public BeanTableView searchIn(
        String indexName,
        String propName,
        Object[] values
    ){
        if(resultSet == null){
            resultSet = indexManager.searchIn(resultSet, indexName, propName, values);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchIn(indexName, propName, values));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param keys 検索キーとなるプロパティ名と値のマッピング
     * @param indexName インデックス名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IllegalArgumentException 指定されたインデックスが指定されたプロパティに関連しない場合
     */
    public BeanTableView searchBy(
        Map keys,
        String indexName
    ) throws IndexNotFoundException, IllegalArgumentException{
        if(resultSet == null){
            resultSet = indexManager.searchBy(new HashSet(), keys, indexName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchBy(keys, indexName));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した複数の値と一致するBean集合を検索する。<p>
     * 一致検索の一種であり、単純インデックスと複合インデックスに対して有効。<br>
     *
     * @param indexName インデックス名
     * @param keys 検索キーとなるプロパティ名と値のマッピングの配列
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しない場合
     * @exception IllegalArgumentException 指定されたインデックスが指定されたプロパティに関連しない場合
     */
    public BeanTableView searchIn(
        String indexName,
        Map[] keys
    ) throws IndexNotFoundException, IllegalArgumentException{
        if(resultSet == null){
            resultSet = indexManager.searchIn(resultSet, indexName, keys);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            operate(indexManager.searchIn(indexName, keys));
        }
        return this;
    }
    
    protected BeanTableIndex createTmporaryIndex(
        String indexName,
        String[] propNames
    )throws IndexNotFoundException{
        BeanTableIndex index = null;
        if(indexName != null){
            index = indexManager.getIndex(indexName);
        }
        if(index == null && propNames != null && propNames.length != 0){
            index = indexManager.getIndexBy(propNames);
        }
        if(index == null){
            throw new IndexNotFoundException();
        }
        index = index.cloneEmpty(false);
        Iterator itr = resultSet.iterator();
        while(itr.hasNext()){
            Object element = itr.next();
            index.add(element);
        }
        return index;
    }
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public BeanTableView searchFromElement(
        Object from,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchFromElement(resultSet, from, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchFromElement(from));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 閾値を持つBean
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */

    public BeanTableView searchFromElement(
        Object from,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchFromElement(resultSet, from, inclusive, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchFromElement(from, inclusive));
        }
        return this;
    }

    
    /**
     * 特定のプロパティが指定した値より大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     */
    public BeanTableView searchFrom(
        Object from,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchFrom(resultSet, from, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchFrom(from));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した値より大きいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 閾値
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     */

    public BeanTableView searchFrom(
        Object from,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchFrom(resultSet, from, inclusive, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchFrom(from, inclusive));
        }
        return this;
    }

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param to 閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public BeanTableView searchToElement(
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchToElement(resultSet, to, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchToElement(to));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティより小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param to 閾値を持つBean
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */

    public BeanTableView searchToElement(
        Object to,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchToElement(resultSet, to, inclusive, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchToElement(to, inclusive));
        }
        return this;
    }

    
    /**
     * 特定のプロパティが指定した値より小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param to 閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     */
    public BeanTableView searchTo(
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchTo(resultSet, to, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchTo(to));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した値より小さいBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param to 閾値
     * @param inclusive 検索結果に閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     */

    public BeanTableView searchTo(
        Object to,
        boolean inclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchTo(resultSet, to, inclusive, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchTo(to, inclusive));
        }
        return this;
    }

    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティの範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 範囲の最小閾値を持つBean
     * @param to 範囲の最大閾値を持つBean
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */
    public BeanTableView searchRangeElement(
        Object from,
        Object to,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchRangeElement(resultSet, from, to, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchRangeElement(from, to));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定したBeanの該当するプロパティの範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 範囲の最小閾値を持つBean
     * @param fromInclusive 検索結果に最小閾値を含むかどうか。含む場合はtrue
     * @param to 範囲の最大閾値を持つBean
     * @param toInclusive 検索結果に最大閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     * @exception IndexPropertyAccessException 指定されたプロパティの取得で例外が発生した場合
     */

    public BeanTableView searchRangeElement(
        Object from,
        boolean fromInclusive,
        Object to,
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException, IndexPropertyAccessException{
        if(resultSet == null){
            resultSet = indexManager.searchRangeElement(resultSet, from, fromInclusive, to, toInclusive, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchRangeElement(from, fromInclusive, to, toInclusive));
        }
        return this;
    }

    
    /**
     * 特定のプロパティが指定した値の範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 範囲の最小閾値
     * @param to 範囲の最大閾値
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     */
    public BeanTableView searchRange(
        Object from, 
        Object to, 
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchRange(resultSet, from, to, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchRange(from, to));
        }
        return this;
    }
    
    /**
     * 特定のプロパティが指定した値の範囲内となるBean集合を検索する。<p>
     * 範囲検索の一種であり、単純インデックスに対してのみ有効。<br>
     *
     * @param from 範囲の最小閾値
     * @param fromInclusive 検索結果に最小閾値を含むかどうか。含む場合はtrue
     * @param to 範囲の最大閾値
     * @param toInclusive 検索結果に最大閾値を含むかどうか。含む場合はtrue
     * @param indexName インデックス名
     * @param propName プロパティ名
     * @return 検索結果のこのビュー
     * @exception IndexNotFoundException 該当するインデックスが存在しないか、複合インデックスの場合
     */

    public BeanTableView searchRange(
        Object from, 
        boolean fromInclusive,
        Object to, 
        boolean toInclusive,
        String indexName,
        String propName
    ) throws IndexNotFoundException{
        if(resultSet == null){
            resultSet = indexManager.searchRange(resultSet, from, fromInclusive, to, toInclusive, indexName, propName);
            if(resultSet == null){
                resultSet = new HashSet();
            }
        }else{
            BeanTableIndex index = createTmporaryIndex(indexName, new String[]{propName});
            operate(index.searchRange(from, fromInclusive, to, toInclusive));
        }
        return this;
    }

    
    /**
     * このビューの複製を作る。<p>
     * 複製の論理演算状態は、デフォルト値となる。<br>
     *
     * @return このビューの複製
     */
    public Object clone(){
        BeanTableView clone = null;
        try{
            clone = (BeanTableView)super.clone();
        }catch(CloneNotSupportedException e){
        }
        if(resultSet != null){
            clone.resultSet = new HashSet(resultSet);
        }
        operator = OPERATOR_AND;
        return clone;
    }
    
    protected static class BeanComparator implements Comparator, Externalizable{
        
        protected SimpleProperty[] properties;
        protected boolean[] isAsc;
        protected Class elementClass;
        
        public BeanComparator(){
        }
        
        public BeanComparator(boolean isAsc){
            this.isAsc = new boolean[]{isAsc};
        }
        
        public BeanComparator(Class elementClass, String[] propNames){
            this(elementClass, propNames, null);
        }
        
        public BeanComparator(Class elementClass, String[] propNames, boolean[] isAsc){
            if(propNames == null || propNames.length == 0){
                throw new IllegalArgumentException("Property name array is empty.");
            }
            if(isAsc != null && propNames.length != isAsc.length){
                throw new IllegalArgumentException("Length of property name array and sort flag array is unmatch.");
            }
            this.elementClass = elementClass;
            this.properties = new SimpleProperty[propNames.length];
            for(int i = 0; i < propNames.length; i++){
                properties[i] = new SimpleProperty(propNames[i]);
                if(!properties[i].isReadable(elementClass)){
                    throw new IllegalArgumentException("No such readable property. property=" + propNames[i]);
                }
            }
            this.isAsc = isAsc;
        }
        
        public int compare(Object bean1, Object bean2){
            if(elementClass == null){
                if(bean1 != null && bean2 == null){
                    return isAsc[0] ? 1 : -1;
                }
                if(bean1 == null && bean2 != null){
                    return isAsc[0] ? -1 : 1;
                }
                if(bean1 != null && bean2 != null){
                    int comp = 0;
                    if(bean1 instanceof Comparable){
                        comp = ((Comparable)bean1).compareTo(bean2);
                    }else{
                        comp = bean1.hashCode() - bean2.hashCode();
                    }
                    if(comp != 0){
                        return isAsc[0] ? comp : -1 * comp;
                    }
                }
            }else{
                if(bean1 == null && bean2 == null){
                    return 0;
                }
                if(bean1 != null && bean2 == null){
                    return 1;
                }
                if(bean1 == null && bean2 != null){
                    return -1;
                }
                for(int i = 0; i < properties.length; i++){
                    Object val1 = null;
                    try{
                        val1 = properties[i].getProperty(bean1);
                    }catch(NoSuchPropertyException e){
                        throw new IndexPropertyAccessException(
                            elementClass,
                            properties[i].getPropertyName(),
                            e
                        );
                    }catch(InvocationTargetException e){
                        throw new IndexPropertyAccessException(
                            elementClass,
                            properties[i].getPropertyName(),
                            ((InvocationTargetException)e).getTargetException()
                        );
                    }
                    Object val2 = null;
                    try{
                        val2 = properties[i].getProperty(bean2);
                    }catch(NoSuchPropertyException e){
                    }catch(InvocationTargetException e){
                    }
                    if(val1 != null && val2 == null){
                        return (isAsc == null || isAsc[i]) ? 1 : -1;
                    }
                    if(val1 == null && val2 != null){
                        return (isAsc == null || isAsc[i]) ? -1 : 1;
                    }
                    if(val1 != null && val2 != null){
                        int comp = 0;
                        if(val1 instanceof Comparable){
                            comp = ((Comparable)val1).compareTo(val2);
                        }else{
                            comp = val1.hashCode() - val2.hashCode();
                        }
                        if(comp != 0){
                            return (isAsc == null || isAsc[i]) ? comp : -1 * comp;
                        }
                    }
                }
            }
            return 0;
        }
        
        public void writeExternal(ObjectOutput out) throws IOException{
            if(properties == null || properties.length == 0){
                out.writeInt(0);
            }else{
                out.writeInt(properties.length);
                for(int i = 0; i < properties.length; i++){
                    SimpleProperty prop = properties[i];
                    out.writeObject(prop.getPropertyName());
                }
            }
            if(isAsc == null || isAsc.length == 0){
                out.writeInt(0);
            }else{
                out.writeInt(isAsc.length);
                for(int i = 0; i < isAsc.length; i++){
                    boolean flg = isAsc[i];
                    out.writeBoolean(flg);
                }
            }
            out.writeObject(elementClass);
        }
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
            int len = in.readInt();
            if(len > 0){
                properties = new SimpleProperty[len];
                for(int i = 0; i < len; i++){
                    properties[i] = new SimpleProperty((String)in.readObject());
                }
            }
            len = in.readInt();
            if(len > 0){
                isAsc = new boolean[len];
                for(int i = 0; i < len; i++){
                    isAsc[i] = in.readBoolean();
                }
            }
            elementClass = (Class)in.readObject();
        }
    }
}
