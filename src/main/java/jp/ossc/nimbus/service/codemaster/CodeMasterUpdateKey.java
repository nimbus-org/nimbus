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
package jp.ossc.nimbus.service.codemaster;

import java.util.*;

/**
 * コードマスタ更新キー。<p>
 *
 * @author M.Takata
 */
public class CodeMasterUpdateKey implements java.io.Serializable{
    
    private static final long serialVersionUID = -4013884085932487915L;
    
    /**
     * 更新タイプ 追加。<p>
     */
    public static final int UPDATE_TYPE_ADD = 1;
    
    /**
     * 更新タイプ 変更。<p>
     */
    public static final int UPDATE_TYPE_UPDATE = 2;
    
    /**
     * 更新タイプ 削除。<p>
     */
    public static final int UPDATE_TYPE_REMOVE = 3;
    
    private Map keyMap;
    private int updateType = UPDATE_TYPE_UPDATE;
    private Object input;
    
    /**
     * 指定されたインデックスのキーを取得する。<p>
     *
     * @param index キーのインデックス
     */
    public Object getKey(int index){
        if(index < 0 || keyMap == null || keyMap.size() <= index){
            return null;
        }
        final Iterator keys = keyMap.values().iterator();
        for(int i = 0; i++ < index; keys.next());
        return keys.next();
    }
    
    /**
     * 指定された名前のキーを取得する。<p>
     *
     * @param name キーの名前
     */
    public Object getKey(String name){
        if(keyMap == null){
            return null;
        }
        return keyMap.get(name);
    }
    
    /**
     * キーの配列を取得する。<p>
     *
     * @return キーの配列
     */
    public Object[] getKeyArray(){
        if(keyMap == null || keyMap.size() == 0){
            return new Object[0];
        }
        return keyMap.values().toArray();
    }
    
    /**
     * キーのリストを取得する。<p>
     *
     * @return キーのリスト
     */
    public List getKeyList(){
        if(keyMap == null || keyMap.size() == 0){
            return new ArrayList();
        }
        return new ArrayList(keyMap.values());
    }
    
    /**
     * キー名とキーのマップを取得する。<p>
     *
     * @return キー名とキーのマップ
     */
    public Map getKeyMap(){
        if(keyMap == null || keyMap.size() == 0){
            return new LinkedHashMap();
        }
        return new LinkedHashMap(keyMap);
    }
    
    /**
     * キーの数を取得する。<p>
     *
     * @return キーの数
     */
    public int getKeySize(){
        return keyMap == null ? 0 : keyMap.size();
    }
    
    /**
     * キーの配列を設定する。<p>
     *
     * @param keys キーの配列
     */
    public void setKeyArray(Object[] keys){
        if(keys == null || keys.length == 0){
            keyMap = null;
        }else{
            if(keyMap == null){
                keyMap = new LinkedHashMap();
            }else{
                keyMap.clear();
            }
            for(int i = 0; i < keys.length; i++){
                addKey(keys[i]);
            }
        }
    }
    
    /**
     * キーのリストを設定する。<p>
     *
     * @param keys キーのリスト
     */
    public void setKeyList(List keys){
        if(keys == null || keys.size() == 0){
            keyMap = null;
        }else{
            if(keyMap == null){
                keyMap = new LinkedHashMap();
            }else{
                keyMap.clear();
            }
            for(int i = 0, imax = keys.size(); i < imax; i++){
                addKey(keys.get(i));
            }
        }
    }
    
    /**
     * キー名とキーのマップを設定する。<p>
     *
     * @param keyMap キー名とキーのマップ
     */
    public void setKeyMap(Map keyMap){
        if(keyMap == null){
            this.keyMap = null;
        }else{
            if(this.keyMap == null){
                this.keyMap = new LinkedHashMap();
            }else{
                keyMap.clear();
            }
            this.keyMap.putAll(keyMap);
        }
    }
    
    /**
     * キーを追加する。<p>
     *
     * @param key キー
     */
    public void addKey(Object key){
        if(keyMap == null){
            keyMap = new LinkedHashMap();
        }
        keyMap.put(key, key);
    }
    
    /**
     * キーを削除する。<p>
     *
     * @param index キーのインデックス
     */
    public void removeKey(int index){
        if(keyMap == null || keyMap.size() <= index){
            return;
        }
        final Iterator keies = keyMap.keySet().iterator();
        for(int i = 0; keies.hasNext() && i++ < index + 1 ; keies.next());
        keies.remove();
    }
    
    /**
     * キー名を持ったキーを追加する。<p>
     *
     * @param name キーの名前
     * @param key キー
     */
    public void addKey(String name, Object key){
        if(keyMap == null){
            keyMap = new LinkedHashMap();
        }
        keyMap.put(name, key);
    }
    
    /**
     * キーを削除する。<p>
     *
     * @param name キーの名前
     */
    public void removeKey(String name){
        if(keyMap == null){
            return;
        }
        keyMap.remove(name);
    }
    
    /**
     * 更新タイプを追加にする。<p>
     */
    public void add(){
        updateType = UPDATE_TYPE_ADD;
    }
    
    /**
     * 更新タイプを変更にする。<p>
     */
    public void update(){
        updateType = UPDATE_TYPE_UPDATE;
    }
    
    /**
     * 更新タイプを削除にする。<p>
     */
    public void remove(){
        updateType = UPDATE_TYPE_REMOVE;
    }
    
    /**
     * 更新タイプが追加かどうか判定する。<p>
     *
     * @return 更新タイプが追加ならtrue
     */
    public boolean isAdd(){
        return updateType == UPDATE_TYPE_ADD;
    }
    
    /**
     * 更新タイプが変更かどうか判定する。<p>
     *
     * @return 更新タイプが変更ならtrue
     */
    public boolean isUpdate(){
        return updateType == UPDATE_TYPE_UPDATE;
    }
    
    /**
     * 更新タイプが削除かどうか判定する。<p>
     *
     * @return 更新タイプが削除ならtrue
     */
    public boolean isRemove(){
        return updateType == UPDATE_TYPE_REMOVE;
    }
    
    /**
     * 更新処理用の入力オブジェクトを設定する。<p>
     *
     * @param in 入力オブジェクト
     */
    public void setInput(Object in){
        input = in;
    }
    
    /**
     * 更新処理用の入力オブジェクトを取得する。<p>
     *
     * @return 入力オブジェクト
     */
    public Object getInput(){
        return input;
    }
    
    /**
     * 更新タイプを設定する。<p>
     *
     * @param type 更新タイプ
     * @see #UPDATE_TYPE_ADD
     * @see #UPDATE_TYPE_UPDATE
     * @see #UPDATE_TYPE_REMOVE
     */
    public void setUpdateType(int type){
        updateType = type;
    }
    
    /**
     * 更新タイプを取得する。<p>
     *
     * @return 更新タイプ
     * @see #UPDATE_TYPE_ADD
     * @see #UPDATE_TYPE_UPDATE
     * @see #UPDATE_TYPE_REMOVE
     */
    public int getUpdateType(){
        return updateType;
    }
    
    /**
     * このオブジェクトの内容をクリアする。<p>
     */
    public void clear(){
        if(keyMap != null){
            keyMap.clear();
        }
        updateType = UPDATE_TYPE_UPDATE;
        input = null;
    }
    
    /**
     * このオブジェクトと他のオブジェクトが等しいかどうかを判定する。<p>
     *
     * @param obj 比較するオブジェクト
     * @return 等しい場合はtrue
     */
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj == this){
            return true;
        }
        if(!(obj instanceof CodeMasterUpdateKey)){
            return false;
        }
        final CodeMasterUpdateKey cmp = (CodeMasterUpdateKey)obj;
        if(keyMap == null){
            return cmp.keyMap == null;
        }else{
            return keyMap.equals(cmp.keyMap);
        }
    }
    
    /**
     * オブジェクトのハッシュコード値を返す。<p>
     *
     * @return ハッシュコード
     */
    public int hashCode(){
        return keyMap == null ? 0 : keyMap.hashCode();
    }
    
    /**
     * オブジェクトの文字列表現を返す。<p>
     *
     * @return 文字列表現
     */
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("keyMap=").append(keyMap);
        buf.append(", updateType=");
        switch(updateType){
        case UPDATE_TYPE_ADD:
            buf.append("ADD");
            break;
        case UPDATE_TYPE_UPDATE:
            buf.append("UPDATE");
            break;
        case UPDATE_TYPE_REMOVE:
            buf.append("REMOVE");
            break;
        }
        buf.append('}');
        return buf.toString();
    }
}