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
package jp.ossc.nimbus.service.scheduler;

import java.util.*;

/**
 * 日付キー。<p>
 * 以下に指定方法を示す。<br>
 * 年指定 : YEAR@2006<br>
 * 月指定 : MONTH@8<br>
 * 日指定 : DAY@10<br>
 * 月末日指定 : DAY@END<br>
 * 日拡張指定 : DAY@任意のキー文字列<br>
 * 週指定 : WEEK@1<br>
 * 月末週指定 : WEEK@END<br>
 * 曜日指定 : WEEK@MONDAY<br>
 * NOT条件：NOT DAY@10<br>
 * OR条件：DAY@10 OR DAY@20<br>
 * AND条件：DAY@10 AND DAY@20<br>
 * 複数条件：(DAY@10 AND DAY@20) OR WEEK@END<br>
 * <p>
 * 日拡張指定を使用する場合は、そのキーを解釈できる{@link DateEvaluator}実装クラスを設定しなければならない。<be>
 *
 * @author M.Takata
 */
public class DateKey{
    
    /**
     * 週指定プレフィクス文字列。<p>
     */
    public static final String WEEK_AT = "WEEK@";
    
    /**
     * 週指定キー文字列 月曜日。<p>
     */
    public static final String MONDAY = "MONDAY";
    
    /**
     * 週指定キー文字列 火曜日。<p>
     */
    public static final String TUESDAY = "TUESDAY";
    
    /**
     * 週指定キー文字列 水曜日。<p>
     */
    public static final String WEDNESDAY = "WEDNESDAY";
    
    /**
     * 週指定キー文字列 木曜日。<p>
     */
    public static final String THURSDAY = "THURSDAY";
    
    /**
     * 週指定キー文字列 金曜日。<p>
     */
    public static final String FRIDAY = "FRIDAY";
    
    /**
     * 週指定キー文字列 土曜日。<p>
     */
    public static final String SATURDAY = "SATURDAY";
    
    /**
     * 週指定キー文字列 日曜日。<p>
     */
    public static final String SUNDAY = "SUNDAY";
    
    /**
     * 年指定プレフィクス文字列。<p>
     */
    public static final String YEAR_AT = "YEAR@";
    
    /**
     * 月指定プレフィクス文字列。<p>
     */
    public static final String MONTH_AT = "MONTH@";
    
    /**
     * 日指定プレフィクス文字列。<p>
     */
    public static final String DAY_AT = "DAY@";
    
    /**
     * 末日及び月末周指定キー文字列。<p>
     */
    public static final String END = "END";
    
    /**
     * AND演算子文字列。<p>
     */
    public static final String AND = "AND";
    
    /**
     * OR演算子文字列。<p>
     */
    public static final String OR = "OR";
    
    /**
     * NOT演算子文字列。<p>
     */
    public static final String NOT = "NOT";
    
    /**
     * 条件オブジェクト。<p>
     */
    protected Condition condition;
    
    /**
     * 日付評価オブジェクト。<p>
     */
    protected DateEvaluator evaluator;
    
    private static final String OPEN = "(";
    private static final String CLOSE = ")";
    private static final String SPACE = " ";
    
    private static final String[] DELIMETERS = new String[]{
        NOT, AND, OR, OPEN, CLOSE, WEEK_AT, YEAR_AT, MONTH_AT, DAY_AT, SPACE
    };
    
    /**
     * 指定された日付キー文字列を解釈する日付キーインスタンスを生成する。<p>
     *
     * @param key 日付キー文字列
     */
    public DateKey(String key){
        condition = parse(key, 0);
    }
    
    /**
     * 指定された日付キー文字列を解釈する日付キーインスタンスを生成する。<p>
     *
     * @param key 日付キー文字列
     * @param evaluator 日付評価
     */
    public DateKey(String key, DateEvaluator evaluator){
        this.evaluator = evaluator;
        condition = parse(key, 0);
    }
    
    /**
     * 日付拡張キーをを評価する{@link DateEvaluator}を設定する。<p>
     *
     * @param evaluator DateEvaluator
     */
    public void setDateEvaluator(DateEvaluator evaluator){
        this.evaluator = evaluator;
    }
    
    /**
     * 日付拡張キーをを評価する{@link DateEvaluator}を取得する。<p>
     *
     * @return DateEvaluator
     */
    public DateEvaluator getDateEvaluator(){
        return evaluator;
    }
    
    /**
     * 指定された文字列を指定された区切り文字で分割する。<p>
     *
     * @param str 文字列
     * @param delims 区切り文字配列
     * @param tokens 分割したトークンを格納するリスト
     * @return 分割したトークンを格納したリスト
     */
    protected List parseTokens(String str, String[] delims, List tokens){
        int index = -1;
        int delimLength = 0;
        for(int i = 0; i < delims.length; i++){
            int tmp = str.indexOf(delims[i]);
            if(tmp != -1){
                if(index == -1){
                    index = tmp;
                    delimLength = delims[i].length();
                }else if(index > tmp){
                    index = tmp;
                    delimLength = delims[i].length();
                }
            }
        }
        if(index == -1){
            tokens.add(str);
        }else if(index == 0){
            if(str.length() == delimLength){
                tokens.add(str);
            }else{
                tokens.add(str.substring(0, delimLength));
                return parseTokens(str.substring(delimLength), delims, tokens);
            }
        }else{
            tokens.add(str.substring(0, index));
            tokens.add(str.substring(index, index + delimLength));
            if(str.length() > index + delimLength){
                return parseTokens(
                    str.substring(index + delimLength),
                    delims,
                    tokens
                );
            }
        }
        return tokens;
    }
    
    /**
     * 指定された日付キー文字列を、パースして条件に変換する。<p>
     *
     * @param key 日付キー文字列
     * @param index パース中の文字列インデックス
     * @return パースされた条件
     */
    protected Condition parse(String key, int index){
        List tokenList = parseTokens(key, DELIMETERS, new ArrayList());
        Condition cond = null;
        NotCondition notCond = null;
        try{
            Iterator tokens = tokenList.iterator();
            while(tokens.hasNext()){
                String token = (String)tokens.next();
                Condition curCond = null;
                if(NOT.equalsIgnoreCase(token)){
                    if(notCond == null){
                        notCond = new NotCondition();
                    }else{
                        notCond = null;
                    }
                    continue;
                }else if(AND.equalsIgnoreCase(token)){
                    curCond = new AndCondition(cond);
                    cond = null;
                }else if(OR.equalsIgnoreCase(token)){
                    curCond = new OrCondition(cond);
                    cond = null;
                }else if(WEEK_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new WeekCondition(token);
                }else if(YEAR_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new YearCondition(token);
                }else if(MONTH_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new MonthCondition(token);
                }else if(DAY_AT.equalsIgnoreCase(token)){
                    index += token.length();
                    token = (String)tokens.next();
                    curCond = new DayCondition(token);
                }else if(OPEN.equals(token)){
                    final StringBuilder buf = new StringBuilder();
                    int tmpIndex = index;
                    int openCount = 1;
                    while(true){
                        token = (String)tokens.next();
                        if(OPEN.equals(token)){
                            openCount++;
                        }else if(CLOSE.equals(token)){
                            openCount--;
                        }
                        if(openCount == 0){
                            break;
                        }
                        index += token.length();
                        buf.append(token);
                    }
                    curCond = parse(buf.toString(), tmpIndex);
                }else if(CLOSE.equals(token)){
                    throw new IllegalArgumentException(
                        "Invalid potision" + token
                    );
                }else if(SPACE.equals(token)){
                    continue;
                }else{
                    throw new IllegalArgumentException(
                        "Invalid token " + token
                    );
                }
                if(notCond != null){
                    notCond.add(curCond);
                    curCond = notCond;
                    notCond = null;
                }
                if(cond == null){
                    cond = curCond;
                }else{
                    cond.add(curCond);
                }
                index += token.length();
            }
            if(cond == null && notCond != null){
                cond = notCond;
            }
            cond.validate();
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("key=" + key + ", index=" + index, e);
        }catch(NoSuchElementException e){
            throw new IllegalArgumentException("key=" + key + ", index=" + index, e);
        }
        return cond;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("condition=").append(condition);
        buf.append('}');
        return buf.toString();
    }
    
    /**
     * 指定された日付が、この日付キーに該当する日付であるかどうかを判定する。<p>
     *
     * @param date 日付
     * @return 指定された日付が、この日付キーに該当する日付である場合はtrue
     * @exception Exception 日付の判定に失敗した場合
     */
    public boolean equalsDate(Date date) throws Exception{
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return condition.equalsDate(cal);
    }
    
    /**
     * 条件。<p>
     *
     * @author M.Takata
     */
    protected interface Condition{
        public boolean equalsDate(Calendar cal) throws Exception;
        public void add(Condition c);
        public void validate();
    }
    
    /**
     * 週指定条件。<p>
     *
     * @author M.Takata
     */
    protected class WeekCondition implements Condition{
        protected int numberOfWeek;
        protected int week;
        protected boolean isEnd;
        public WeekCondition(String key){
            if(END.equals(key)){
                isEnd = true;
            }else if(MONDAY.equalsIgnoreCase(key)){
                week = Calendar.MONDAY;
            }else if(TUESDAY.equalsIgnoreCase(key)){
                week = Calendar.TUESDAY;
            }else if(WEDNESDAY.equalsIgnoreCase(key)){
                week = Calendar.WEDNESDAY;
            }else if(THURSDAY.equalsIgnoreCase(key)){
                week = Calendar.THURSDAY;
            }else if(FRIDAY.equalsIgnoreCase(key)){
                week = Calendar.FRIDAY;
            }else if(SATURDAY.equalsIgnoreCase(key)){
                week = Calendar.SATURDAY;
            }else if(SUNDAY.equalsIgnoreCase(key)){
                week = Calendar.SUNDAY;
            }else{
                try{
                    numberOfWeek = Integer.parseInt(key);
                    if(numberOfWeek < 1 || numberOfWeek > 6){
                        throw new IllegalArgumentException(
                            "expected number from 1 to 6"
                        );
                    }
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException(
                        "expected number or Week key(MONDAY,TUESDAY,...etc.) or END"
                    );
                }
            }
        }
        public boolean equalsDate(Calendar cal){
            if(isEnd){
                return cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
                     == cal.get(Calendar.WEEK_OF_MONTH);
            }else if(numberOfWeek == 0){
                return cal.get(Calendar.DAY_OF_WEEK) == week;
            }else{
                return cal.get(Calendar.WEEK_OF_MONTH) == numberOfWeek;
            }
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(numberOfWeek == 0 && week == 0 && !isEnd){
                throw new IllegalArgumentException(
                    "WEEK@ is null"
                );
            }
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("Week");
            buf.append('{');
            buf.append("numberOfWeek=").append(numberOfWeek);
            buf.append(",week=").append(week);
            buf.append(",isEnd=").append(isEnd);
            buf.append('}');
            return buf.toString();
        }
    }
    
    /**
     * 年指定条件。<p>
     *
     * @author M.Takata
     */
    protected class YearCondition implements Condition{
        protected int year;
        public YearCondition(String key){
            try{
                year = Integer.parseInt(key);
                if(year < 0){
                    throw new IllegalArgumentException(
                        "expected number more than 1"
                    );
                }
            }catch(NumberFormatException e){
                throw new IllegalArgumentException(
                    "expected number"
                );
            }
        }
        public boolean equalsDate(Calendar cal){
            return cal.get(Calendar.YEAR) == year;
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(year == 0){
                throw new IllegalArgumentException(
                    "YEAR@ is null"
                );
            }
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("Year");
            buf.append('{');
            buf.append("year=").append(year);
            buf.append('}');
            return buf.toString();
        }
    }
    
    /**
     * 月指定条件。<p>
     *
     * @author M.Takata
     */
    protected class MonthCondition implements Condition{
        protected int month;
        public MonthCondition(String key){
            try{
                month = Integer.parseInt(key);
                if(month < 0 || month > 12){
                    throw new IllegalArgumentException(
                        "expected number from 1 to 12"
                    );
                }
            }catch(NumberFormatException e){
                throw new IllegalArgumentException(
                    "expected number"
                );
            }
        }
        public boolean equalsDate(Calendar cal){
            return cal.get(Calendar.MONTH) + 1 == month;
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(month == 0){
                throw new IllegalArgumentException(
                    "MONTH@ is null"
                );
            }
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("Month");
            buf.append('{');
            buf.append("month=").append(month);
            buf.append('}');
            return buf.toString();
        }
    }
    
    /**
     * 日指定条件。<p>
     *
     * @author M.Takata
     */
    protected class DayCondition implements Condition{
        protected int day;
        protected boolean isEnd;
        protected String dateKey;
        public DayCondition(String key){
            if(END.equals(key)){
                isEnd = true;
            }else{
                try{
                    day = Integer.parseInt(key);
                    if(day < 0 || day > 31){
                        throw new IllegalArgumentException(
                            "expected number from 1 to 31"
                        );
                    }
                }catch(NumberFormatException e){
                    if(evaluator == null){
                        throw new IllegalArgumentException(
                            "expected number or END"
                        );
                    }
                    dateKey = key;
                }
            }
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            if(isEnd){
                return cal.getActualMaximum(Calendar.DAY_OF_MONTH) == day;
            }else if(dateKey != null){
                return evaluator.equalsDate(dateKey, cal);
            }else{
                return cal.get(Calendar.DAY_OF_MONTH) == day;
            }
        }
        public void add(Condition c){
            throw new IllegalArgumentException("expected OR or AND.");
        }
        public void validate(){
            if(day == 0 && !isEnd && dateKey == null){
                throw new IllegalArgumentException(
                    "DAY@ is null"
                );
            }
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("Day");
            buf.append('{');
            buf.append("day=").append(day);
            buf.append(",dateKey=").append(dateKey);
            buf.append(",isEnd=").append(isEnd);
            buf.append('}');
            return buf.toString();
        }
    }
    
    /**
     * AND条件。<p>
     *
     * @author M.Takata
     */
    protected class AndCondition implements Condition{
        protected Condition condition1;
        protected Condition condition2;
        public AndCondition(Condition c){
            if(c == null){
                throw new IllegalArgumentException(
                    "expected expresion(WEEK@, YEAR@... etc.)."
                );
            }
            condition1 = c;
        }
        public void add(Condition c){
            if(condition2 != null){
                throw new IllegalArgumentException("expected OR or AND.");
            }
            condition2 = c;
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            return condition1.equalsDate(cal) && condition2.equalsDate(cal);
        }
        public void validate(){
            if(condition1 == null || condition2 == null){
                throw new IllegalArgumentException(
                    "Two operands are necessary for AND"
                );
            }
            condition1.validate();
            condition2.validate();
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("And");
            buf.append('{');
            buf.append("condition1=").append(condition1);
            buf.append(",condition2=").append(condition2);
            buf.append('}');
            return buf.toString();
        }
    }
    
    /**
     * OR条件。<p>
     *
     * @author M.Takata
     */
    protected class OrCondition implements Condition{
        protected Condition condition1;
        protected Condition condition2;
        public OrCondition(Condition c){
            if(c == null){
                throw new IllegalArgumentException(
                    "expected expresion(WEEK@, YEAR@... etc.)."
                );
            }
            condition1 = c;
        }
        public void add(Condition c){
            if(condition2 != null){
                throw new IllegalArgumentException("expected OR or AND.");
            }
            condition2 = c;
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            return condition1.equalsDate(cal) || condition2.equalsDate(cal);
        }
        public void validate(){
            if(condition1 == null || condition2 == null){
                throw new IllegalArgumentException(
                    "Two operands are necessary for OR"
                );
            }
            condition1.validate();
            condition2.validate();
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("Or");
            buf.append('{');
            buf.append("condition1=").append(condition1);
            buf.append(",condition2=").append(condition2);
            buf.append('}');
            return buf.toString();
        }
    }
    
    /**
     * NOT条件。<p>
     *
     * @author M.Takata
     */
    protected class NotCondition implements Condition{
        protected Condition condition;
        public NotCondition(){
        }
        public void add(Condition c){
            condition = c;
        }
        public boolean equalsDate(Calendar cal) throws Exception{
            return !condition.equalsDate(cal);
        }
        public void validate(){
            if(condition == null){
                throw new IllegalArgumentException(
                    "operands are necessary for NOT"
                );
            }
            condition.validate();
        }
        
        public String toString(){
            final StringBuilder buf = new StringBuilder("Not");
            buf.append('{');
            buf.append("condition=").append(condition);
            buf.append('}');
            return buf.toString();
        }
    }
}
