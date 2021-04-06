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
 * パスワード生成器。<p>
 *
 * @author M.Takata
 */
public class PasswordGenerator{
    
    /**
     * アルファベット小文字配列。<p>
     */
    public static final char[] LOWERCASE_ALPHABET ={
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
    };
    
    /**
     * アルファベット大文字配列。<p>
     */
    public static final char[] UPPPERCASE_ALPHABET ={
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };
    
    /**
     * 数字配列。<p>
     */
    public static final char[] NUMBER ={
        '0','1','2','3','4','5','6','7','8','9'
    };
    
    /**
     * 記号配列。<p>
     */
    public static final char[] SYMBOL ={
        '!','"','#','$','%','&','\'','(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_','`','{','|','}','~'
    };
    
    private final Random random = new Random();
    private final List elements = new ArrayList();
    
    /**
     * 空のインスタンスを生成する。<p>
     */
    public PasswordGenerator(){
    }
    
    /**
     * 指定したパスワード要素を持つインスタンスを生成する。<p>
     *
     * @param elements パスワード要素の配列
     */
    public PasswordGenerator(PasswordElement[] elements){
        for(int i = 0; i < elements.length; i++){
            addElement(elements[i]);
        }
    }
    
    /**
     * 指定したパスワード要素を設定する。<p>
     *
     * @param elements パスワード要素の配列
     */
    public setElements(PasswordElement[] elements){
        elements.clear();
        for(int i = 0; i < elements.length; i++){
            addElement(elements[i]);
        }
    }
    
    /**
     * 指定したパスワード要素を追加する。<p>
     *
     * @param element パスワード要素
     */
    public void addElement(PasswordElement element){
        elements.add(element);
    }
    
    /**
     * 設定されたパスワード要素をクリアする。<p>
     */
    public void clear(){
        elements.clear();
    }
    
    /**
     * 指定された長さのパスワードを生成する。<p>
     *
     * @param length パスワード長
     * @return パスワード
     */
    public String generate(int length){
        final List result = new ArrayList(length);
        Iterator itr = elements.iterator();
        final List curElements = new ArrayList(elements.size());
        while(itr.hasNext()){
            PasswordElement element = (PasswordElement)itr.next();
            curElements.add(element.clone());
        }
        itr = curElements.iterator();
        while(itr.hasNext() && result.size() < length){
            PasswordElement element = (PasswordElement)itr.next();
            if(element.isNecessary()){
                result.add(Character.valueOf(element.next(random)));
                if(!element.hasNext()){
                    itr.remove();
                }
            }
        }
        while(result.size() < length && curElements.size() > 0){
            PasswordElement element = (PasswordElement)curElements.get(random.nextInt(curElements.size()));
            result.add(Character.valueOf(element.next(random)));
            if(!element.hasNext()){
                curElements.remove(element);
            }
        }
        Collections.shuffle(result, random);
        final StringBuilder buf = new StringBuilder();
        itr = result.iterator();
        while(itr.hasNext()){
            buf.append(itr.next());
        }
        return buf.toString();
    }
    
    /**
     * パスワード要素。<p>
     *
     * @author M.Takata
     */
    public static class PasswordElement implements Cloneable{
        protected final char[] chars;
        protected int minCount = 0;
        protected int maxCount = 0;
        protected int count;
        
        /**
         * 指定された文字をパスワードの要素として持つインスタンスを生成する。<p>
         *
         * @param chars パスワードの要素となる文字配列。
         */
        public PasswordElement(char[] chars){
            this.chars = chars;
        }
        
        /**
         * 指定された文字をパスワードの要素として持ち、最小出現回数と最大出現回数を指定したインスタンスを生成する。<p>
         *
         * @param chars パスワードの要素となる文字配列。
         * @param min 最小出現回数
         * @param max 最大出現回数。何度でも出現して良い場合は、0
         */
        public PasswordElement(char[] chars, int min, int max){
            this(chars);
            setMinCount(min);
            setMaxCount(max);
        }
        
        /**
         * 最小出現回数を設定する。<p>
         *
         * @param min 最小出現回数
         */
        public void setMinCount(int min){
            minCount = min;
        }
        
        /**
         * 最大出現回数を設定する。<p>
         *
         * @param max 最大出現回数。何度でも出現して良い場合は、0
         */
        public void setMaxCount(int max){
            maxCount = max;
        }
        
        /**
         * まだ出現しても良いかを判定する。<p>
         *
         * @return まだ出現しても良い場合は、true
         */
        public boolean hasNext(){
            return maxCount == 0 || count < maxCount;
        }
        
        /**
         * まだ出現すべきかを判定する。<p>
         *
         * @return まだ出現すべきか場合は、true
         */
        public boolean isNecessary(){
            return minCount > count;
        }
        
        /**
         * 次の文字を返す。<p>
         *
         * @param random 乱数
         * @return 次の文字
         */
        public char next(Random random){
            count++;
            return chars[random.nextInt(chars.length)];
        }
        
        /**
         * 複製を生成する。<p>
         *
         * @return 複製
         */
        public Object clone(){
            PasswordElement clone = null;
            try{
                clone = (PasswordElement)super.clone();
            }catch(CloneNotSupportedException e){
                return null;
            }
            clone.count = 0;
            return clone;
        }
    }
    
    /**
     * アルファベット小文字のパスワード要素。<p>
     *
     * @author M.Takata
     */
    public static class LowercaseAlphabetElement extends PasswordElement{
        
        /**
         * アルファベット小文字をパスワードの要素として持つインスタンスを生成する。<p>
         */
        public LowercaseAlphabetElement(){
            super(LOWERCASE_ALPHABET);
        }
        
        /**
         * アルファベット小文字をパスワードの要素として持ち、最小出現回数と最大出現回数を指定したインスタンスを生成する。<p>
         *
         * @param min 最小出現回数
         * @param max 最大出現回数。何度でも出現して良い場合は、0
         */
        public LowercaseAlphabetElement(int min, int max){
            super(LOWERCASE_ALPHABET, min, max);
        }
    }
    
    /**
     * アルファベット大文字のパスワード要素。<p>
     *
     * @author M.Takata
     */
    public static class UppercaseAlphabetElement extends PasswordElement{
        
        /**
         * アルファベット大文字をパスワードの要素として持つインスタンスを生成する。<p>
         */
        public UppercaseAlphabetElement(){
            super(UPPPERCASE_ALPHABET);
        }
        
        /**
         * アルファベット大文字をパスワードの要素として持ち、最小出現回数と最大出現回数を指定したインスタンスを生成する。<p>
         *
         * @param min 最小出現回数
         * @param max 最大出現回数。何度でも出現して良い場合は、0
         */
        public UppercaseAlphabetElement(int min, int max){
            super(UPPPERCASE_ALPHABET, min, max);
        }
    }
    
    /**
     * 数字のパスワード要素。<p>
     *
     * @author M.Takata
     */
    public static class NumberElement extends PasswordElement{
        
        /**
         * 数字をパスワードの要素として持つインスタンスを生成する。<p>
         */
        public NumberElement(){
            super(NUMBER);
        }
        
        /**
         * 数字をパスワードの要素として持ち、最小出現回数と最大出現回数を指定したインスタンスを生成する。<p>
         *
         * @param min 最小出現回数
         * @param max 最大出現回数。何度でも出現して良い場合は、0
         */
        public NumberElement(int min, int max){
            super(NUMBER, min, max);
        }
    }
    
    /**
     * 記号のパスワード要素。<p>
     *
     * @author M.Takata
     */
    public static class SymbolElement extends PasswordElement{
        
        /**
         * 記号をパスワードの要素として持つインスタンスを生成する。<p>
         */
        public SymbolElement(){
            super(SYMBOL);
        }
        
        /**
         * 記号をパスワードの要素として持ち、最小出現回数と最大出現回数を指定したインスタンスを生成する。<p>
         *
         * @param min 最小出現回数
         * @param max 最大出現回数。何度でも出現して良い場合は、0
         */
        public SymbolElement(int min, int max){
            super(SYMBOL, min, max);
        }
    }
}