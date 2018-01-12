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
package jp.ossc.nimbus.service.test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基底テストリソースクラス。<p>
 * 
 * @author M.Ishida
 */
public class TestResourceBaseImpl extends TestPhaseExecutableImpl implements TestResourceBase, java.io.Serializable {
    
    private static final long serialVersionUID = -3971398998007055451L;
    
    private String title;
    private String description;
    private int errorContinue = CONTINUE_TYPE_DEFAULT;
    private Map actionDescriptionMap;
    private Map actionTitleMap;
    private Map categoryMap;
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public TestResourceBaseImpl() {
        super();
        actionDescriptionMap = new LinkedHashMap();
        actionTitleMap = new LinkedHashMap();
        categoryMap = new LinkedHashMap();
    }
    
    public String getTitle() {
        return title;
    }
    
    /**
     * タイトルを設定する。<p>
     *
     * @param title タイトル
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 説明を設定する。<p>
     *
     * @param description 説明
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getErrorContinue() {
        return errorContinue;
    }
    
    /**
     * エラー時継続種別を設定する。<p>
     *
     * @param errorContinue エラー時継続種別
     * @see #CONTINUE_TYPE_DEFAULT
     * @see #CONTINUE_TYPE_TRUE
     * @see #CONTINUE_TYPE_FALSE
     */
    public void setErrorContinue(boolean errorContinue) {
        if(errorContinue){
            this.errorContinue = CONTINUE_TYPE_TRUE;
        } else {
            this.errorContinue = CONTINUE_TYPE_FALSE;
        }
    }
    
    public String getActionDescription(String actionId) {
        return (String) actionDescriptionMap.get(actionId);
    }
    
    /**
     * テストアクションの説明を設定する。<p>
     *
     * @param actionId アクションID
     * @param desc テストアクションの説明
     */
    public void setActionDescription(String actionId, String desc) {
        actionDescriptionMap.put(actionId, desc);
    }
    
    public String getActionTitle(String actionId) {
        return (String) actionTitleMap.get(actionId);
    }
    
    /**
     * テストアクションのタイトルを設定する。<p>
     *
     * @param actionId アクションID
     * @param title テストアクションのタイトル
     */
    public void setActionTitle(String actionId, String title) {
        actionTitleMap.put(actionId, title);
    }
    
    public Map getCategoryMap(){
        return categoryMap;
    }
    
    /**
     * カテゴリを設定する。<p>
     *
     * @param name カテゴリ名
     * @param value カテゴリ値
     */
    public void setCategory(String name, String value){
        categoryMap.put(name, value);
    }
    
}
