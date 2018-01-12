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
package jp.ossc.nimbus.util.validator;

import java.util.*;

/**
 * 組み合わせバリデータ。<p>
 * 複数のバリデータを論理演算子で連結して検証する。<br>
 * 
 * @author M.Takata
 */
public class CombinationValidator implements Validator, java.io.Serializable{
    
    private static final long serialVersionUID = 1695449609101701493L;
    
    /**
     * 組み合わせられたバリデータと論理演算子のリスト。<p>
     * ValidatorWithConditionのリスト。<br>
     */
    protected List validators = new ArrayList();
    
    /**
     * 最初のバリデータを追加する。<p>
     *
     * @param validator バリデータ
     * @exception ValidateException 既にバリデータが登録されている場合
     */
    public void add(Validator validator) throws ValidateException{
        if(validators.size() != 0){
            throw new ValidateException("It is not the first validator.");
        }
        final ValidatorWithCondition cond
             = new ValidatorWithCondition(validator);
        validators.add(cond);
    }
    
    /**
     * 最初のバリデータをNOT演算子付きで追加する。<p>
     *
     * @param validator バリデータ
     * @exception ValidateException 既にバリデータが登録されている場合
     */
    public void addNot(Validator validator) throws ValidateException{
        if(validators.size() != 0){
            throw new ValidateException("It is not the first validator.");
        }
        final ValidatorWithCondition cond
             = new ValidatorWithCondition(validator);
        cond.isNot = true;
        validators.add(cond);
    }
    
    /**
     * バリデータをOR演算子付きで連結する。<p>
     *
     * @param validator バリデータ
     */
    public void or(Validator validator){
        final ValidatorWithCondition cond
             = new ValidatorWithCondition(validator);
        cond.isOr = true;
        validators.add(cond);
    }
    
    /**
     * バリデータをAND演算子付きで連結する。<p>
     *
     * @param validator バリデータ
     */
    public void and(Validator validator){
        final ValidatorWithCondition cond
             = new ValidatorWithCondition(validator);
        cond.isAnd = true;
        validators.add(cond);
    }
    
    /**
     * バリデータをOR NOT演算子付きで連結する。<p>
     *
     * @param validator バリデータ
     */
    public void orNot(Validator validator){
        final ValidatorWithCondition cond
             = new ValidatorWithCondition(validator);
        cond.isNot = true;
        cond.isOr = true;
        validators.add(cond);
    }
    
    /**
     * バリデータをAND NOT演算子付きで連結する。<p>
     *
     * @param validator バリデータ
     */
    public void andNot(Validator validator){
        final ValidatorWithCondition cond
             = new ValidatorWithCondition(validator);
        cond.isNot = true;
        cond.isAnd = true;
        validators.add(cond);
    }
    
    /**
     * バリデータを全て削除する。<p>
     */
    public void clear(){
        validators.clear();
    }
    
    /**
     * 指定されたオブジェクトを論理演算子で連結されたバリデータを使って検証する。<p>
     *
     * @param obj 検証対象のオブジェクト
     * @return 検証結果。検証成功の場合true
     * @exception ValidateException 検証に失敗した場合
     */
    public boolean validate(Object obj) throws ValidateException{
        if(validators.size() == 0){
            return true;
        }
        Boolean result = null;
        for(int i = 0, imax = validators.size(); i < imax; i++){
            final ValidatorWithCondition cond
                 = (ValidatorWithCondition)validators.get(i);
            result = cond.validate(result, obj) ? Boolean.TRUE : Boolean.FALSE;
        }
        return result == null ? true : result.booleanValue();
    }
    
    /**
     * 論理演算子付きバリデータ。<p>
     *
     * @author M.Takata
     */
    protected static class ValidatorWithCondition
     implements java.io.Serializable{
        
        private static final long serialVersionUID = 924450733620787066L;
        
        /**
         * NOT演算子付きかどうかのフラグ。<p>
         */
        protected boolean isNot;
        
        /**
         * OR演算子付きかどうかのフラグ。<p>
         */
        protected boolean isOr;
        
        /**
         * AND演算子付きかどうかのフラグ。<p>
         */
        protected boolean isAnd;
        
        /**
         * バリデータ。<p>
         */
        protected Validator validator;
        
        /**
         * インスタンスを生成する。<p>
         *
         * @param validator バリデータ
         */
        public ValidatorWithCondition(Validator validator){
            this.validator = validator;
        }
        
        /**
         * 指定されたオブジェクトをバリデータで検証し、その検証結果とここまでの検証結果の論理演算を行う。<p>
         *
         * @param preResult ここまでの検証結果
         * @param obj 検証対象のオブジェクト
         * @return 検証結果。検証成功の場合true
         * @exception ValidateException 検証に失敗した場合
         */
        public boolean validate(Boolean preResult, Object obj)
         throws ValidateException{
            if(preResult != null){
                if(!preResult.booleanValue() && isAnd){
                    return false;
                }else if(preResult.booleanValue() && isOr){
                    return true;
                }
            }
            boolean result = validator.validate(obj);
            result = isNot ? !result : result;
            if(preResult != null){
                if(isOr){
                    result = result | preResult.booleanValue();
                }else if(isAnd){
                    result = result & preResult.booleanValue();
                }
            }
            return result;
        }
    }
}