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
package jp.ossc.nimbus.service.test.bug;

import java.util.Date;

import jp.ossc.nimbus.service.test.TestUniqueId;

/**
 * 不具合情報。<p>
 * 不具合情報を保持するためのインターフェイス。<br>
 *
 * @author M.Ishida
 */
public interface BugRecord {
    
    /**
     * 不具合情報のキーとなるIDを取得する。
     * 
     * @return 不具合情報のキーとなるID
     */
    public String getId();
    
    /**
     * 不具合情報のキーとなるIDを設定する。
     * 
     * @param id 不具合情報のキーとなるID
     */
    public void setId(String id);
    
    /**
     * 不具合発生日を取得する。
     * 
     * @return 不具合発生日
     */
    public Date getDate();
    
    /**
     * 不具合発生日を設定する。
     * 
     * @param date 不具合発生日
     */
    public void setDate(Date date);
    
    /**
     * 不具合が発生したテストフレームワークのユニークキーを取得する。
     * 
     * @return テストフレームワークのユニークキー
     */
    public TestUniqueId getTestUniqueId();
    
    /**
     * 不具合が発生したテストフレームワークのユニークキーを設定する。
     * 
     * @param id テストフレームワークのユニークキー
     */
    public void setTestUniqueId(TestUniqueId id);
    
    /**
     * 項目名からBugAttributeを取得する。
     * 
     * @param name 項目名
     * @return BugAttribute
     */
    public <T> BugAttribute<T> getBugAttributes(String name);
    
    /**
     * 登録されているすべてのBugAttributeを取得する。
     * 
     * @return BugAttributeの配列
     */
    public BugAttribute<?>[] getBugAttributes();
    
    /**
     * BugAttributeを登録する。
     * 
     * @param attribute
     */
    public void addBugAttribute(BugAttribute<?> attribute);
    
    /**
     * 不具合情報に設定する項目を保持するためのインターフェイス
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public interface BugAttribute<T> {
        
        /**
         * 項目名を設定する。
         * 
         * @param name 項目名
         */
        public void setName(String name);
        
        /**
         * 項目名を取得する。
         * 
         * @return 項目名
         */
        public String getName();
        
        /**
         * 項目値を設定する。
         * 
         * @param value 項目値
         */
        public void setValue(T value);
        
        /**
         * 項目値を取得する。
         * 
         * @return 項目値
         */
        public T getValue();
        
    }
    
    /**
     * 選択値を持つ、不具合情報に設定する項目を保持するためのインターフェイス
     * 
     * @author m-ishida
     *
     * @param <T> 情報を保持するデータの型
     */
    public interface SelectableBugAttribute<T> extends BugAttribute<T> {
        
        /**
         * 選択可能値を設定する。
         * 
         * @param values 選択可能値
         */
        public void setSelectableValues(T[] values);
        
        /**
         * 選択可能値を取得する。
         * 
         * @return 選択可能値
         */
        public T[] getSelectableValues();
        
    }
    
}
