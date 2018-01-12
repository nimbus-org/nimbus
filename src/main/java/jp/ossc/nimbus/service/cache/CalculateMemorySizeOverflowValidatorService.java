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
package jp.ossc.nimbus.service.cache;

import java.util.*;
import java.lang.reflect.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.beans.*;

/**
 * 計算メモリサイズあふれ検証サービス。<p>
 * 以下に、メモリの使用サイズが最大メモリの半分を超えると予想された場合にあふれるあふれ検証サービスのサービス定義例を示す。<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="Shift_JIS"?&gt;
 * 
 * &lt;server&gt;
 *     
 *     &lt;manager name="Sample"&gt;
 *         
 *         &lt;service name="CalculateMemorySizeOverflowValidator"
 *                  code="jp.ossc.nimbus.service.cache.CalculateMemorySizeOverflowValidatorService"/&gt;
 *         
 *     &lt;/manager&gt;
 *     
 * &lt;/server&gt;
 * </pre>
 * {@link MemorySizeOverflowValidatorService}と異なるのは、JVMのヒープサイズであふれ検証するのではなく、キャッシュされたオブジェクトが使用するメモリサイズの理論値であふれ検証するところである。<br>
 * オブジェクトが使用するメモリサイズの理論値の計算方法は、以下の通りである。<br>
 * <ul>
 *   <li>{@link #setMemorySize(String, String)}で指定された理論値がある場合、それを使用する。</li>
 *   <li>クラスに宣言されている全てのインスタンス変数を、その宣言されているクラスの情報を使ってサイズを計算して加算する。これは、フィールド宣言に必要なメモリを計算するだけなので、その変数の値自体のメモリ使用量は計算されない。</li>
 *   <li>オブジェクトに宣言されているgetterメソッドで取得できるprimitive型以外のオブジェクトのサイズを計算して加算する。{@link #setCalculateProperty(boolean)}でこの加算のON/OFFを制御できる。インスタンス変数の値自体のメモリ使用量を加算する。但し、getterが存在しないインスタンス変数の値自体のメモリ使用量は加算されない。</li>
 *   <li>配列型のオブジェクトは、配列長 * 4 + 12バイトとして計算する。但し、インスタンスにアクセスできない場合は、配列長は0と仮定する。また、インスタンスにアクセスできる場合は、各配列要素のオブジェクトのサイズも加算する。</li>
 *   <li>java.util.Collection型やjava.util.Map型のオブジェクトは、インスタンスにアクセスできる場合は各要素のオブジェクトのサイズも加算する。</li>
 * </ul>
 * メモリの理論値は、リフレクションAPIで調べる事ができる範囲までしか、計算されないため、必ずしも実メモリ使用量と一致しない。往々にして、実メモリ使用量よりも小さな値になる。従って、このサービスを使用する場合は、上記の理論値でどこまで実メモリ使用量とのずれが生じ得るかを想定する必要がある。<br>
 *
 * @author M.Takata
 */
public class CalculateMemorySizeOverflowValidatorService extends ServiceBase
 implements OverflowValidator, CacheRemoveListener, java.io.Serializable,
            CalculateMemorySizeOverflowValidatorServiceMBean{
    
    private static final long serialVersionUID = 6454857430979865088L;
    
    private static final char KILO_UNIT = 'K';
    private static final char MEGA_UNIT = 'M';
    private static final char GIGA_UNIT = 'G';
    
    private static final long KILO_BYTE = 1024;
    private static final long MEGA_BYTE = KILO_BYTE * KILO_BYTE;
    private static final long GIGA_BYTE = MEGA_BYTE * KILO_BYTE;
    
    
    private String maxMemorySizeStr = "32M";
    private long maxMemorySize = 32 * MEGA_BYTE;
    
    private Map references;
    private Map memorySizeMap;
    private Map tmpMemorySizeMap;
    private long currentUsedMemorySize;
    private boolean isCalculateOnValidate;
    private boolean isCalculateProperty;
    
    {
        final Runtime runtime = Runtime.getRuntime();
        try{
            maxMemorySize = runtime.maxMemory() / 2;
            maxMemorySizeStr = Long.toString(maxMemorySize);
        }catch(NoSuchMethodError err){
        }
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public void setMaxMemorySize(String size)
     throws IllegalArgumentException{
        maxMemorySize = convertMemorySize(size);
        maxMemorySizeStr = size;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public String getMaxMemorySize(){
        return maxMemorySizeStr;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public void setCalculateProperty(boolean isCalculate){
        isCalculateProperty = isCalculate;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public boolean isCalculateProperty(){
        return isCalculateProperty;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public void setCalculateOnValidate(boolean isCalculate){
        isCalculateOnValidate = isCalculate;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public boolean isCalculateOnValidate(){
        return isCalculateOnValidate;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public int size(){
        return references == null ? 0 : references.size();
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public long getCurrentUsedMemorySize(){
        return currentUsedMemorySize;
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public void setMemorySize(String className, String size)
     throws ClassNotFoundException{
        final Class clazz = Class.forName(
            className,
            true,
            NimbusClassLoader.getInstance()
        );
        long val = convertMemorySize(size);
        memorySizeMap.put(clazz, new Long(val));
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public String getMemorySize(String className) throws ClassNotFoundException{
        if(memorySizeMap == null){
            return null;
        }
        final Class clazz = Class.forName(
            className,
            true,
            NimbusClassLoader.getInstance()
        );
        Number number = (Number)memorySizeMap.get(clazz);
        return number == null ? null : String.valueOf(number.longValue());
    }
    
    // CalculateMemorySizeOverflowValidatorServiceMBeanのJavaDoc
    public Map getMemorySizeMap(){
        return memorySizeMap;
    }
    
    /**
     * サービスの生成処理を行う。<p>
     * インスタンス変数の初期化を行う。
     *
     * @exception Exception サービスの生成処理に失敗した場合
     */
    public void createService() throws Exception{
        references = Collections.synchronizedMap(new HashMap());
        memorySizeMap = Collections.synchronizedMap(new HashMap());
        memorySizeMap.put(Byte.TYPE, new Short((short)1));
        memorySizeMap.put(Boolean.TYPE, new Short((short)1));
        memorySizeMap.put(Character.TYPE, new Short((short)2));
        memorySizeMap.put(Short.TYPE, new Short((short)2));
        memorySizeMap.put(Integer.TYPE, new Short((short)4));
        memorySizeMap.put(Float.TYPE, new Short((short)4));
        memorySizeMap.put(Long.TYPE, new Short((short)8));
        memorySizeMap.put(Double.TYPE, new Short((short)8));
        memorySizeMap.put(Class.class, new Short((short)0));
        memorySizeMap.put(Method.class, new Short((short)0));
        memorySizeMap.put(Field.class, new Short((short)0));
        memorySizeMap.put(Constructor.class, new Short((short)0));
        tmpMemorySizeMap = Collections.synchronizedMap(new HashMap());
    }
    
    /**
     * サービスの停止処理を行う。<p>
     *
     * @exception Exception サービスの停止処理に失敗した場合
     */
    public void stopService() throws Exception{
        tmpMemorySizeMap.clear();
    }
    
    /**
     * サービスの破棄処理を行う。<p>
     * インスタンス変数の開放を行う。
     *
     * @exception Exception サービスの破棄処理に失敗した場合
     */
    public void destroyService() throws Exception{
        reset();
        references = null;
    }
    
    private long convertMemorySize(String size)
     throws IllegalArgumentException{
        long value = 0L;
        boolean isValid = true;
        
        if(size == null){
            isValid = false;
        }else{
            final int length = size.length();
            if(length == 0){
                isValid = false;
            }else{
                final char unit = Character.toUpperCase(
                    size.charAt(length - 1)
                );
                String tmpSize = null;
                long unitValue = 0;
                switch(unit){
                case KILO_UNIT:
                    tmpSize = size.substring(0, length - 1);
                    unitValue = KILO_BYTE;
                    break;
                case MEGA_UNIT:
                    tmpSize = size.substring(0, length - 1);
                    unitValue = MEGA_BYTE;
                    break;
                case GIGA_UNIT:
                    tmpSize = size.substring(0, length - 1);
                    unitValue = GIGA_BYTE;
                    break;
                default:
                    tmpSize = size;
                    unitValue = 1;
                }
                try{
                    value = (long)(Double.parseDouble(tmpSize) * (long)unitValue);
                }catch(NumberFormatException e){
                    isValid = false;
                }
            }
        }
        if(value < 0){
            isValid = false;
        }
        if(!isValid){
            throw new IllegalArgumentException("Invalid size : " + size);
        }
        return value;
    }
    
    private long calculateMemorySize(Object obj){
        if(obj == null){
            return 0l;
        }
        final Class clazz = obj.getClass();
        return calculateMemorySize(clazz, obj, true, new ArrayList());
    }
    
    private long roundUp(long val, int base){
        long tmp = val % base;
        return tmp == 0 ? val : (val + base - tmp);
    }
    
    private long calculateMemorySize(Class clazz, Object obj, boolean isRoundUp, List stack){
        if(stack.contains(clazz)){
            return 0l;
        }
        int index = 0;
        try{
            index = stack.size();
            stack.add(clazz);
            Number size = (Number)memorySizeMap.get(clazz);
            if(size != null){
                return size.longValue();
            }
            if(obj == null){
                size = (Number)tmpMemorySizeMap.get(clazz);
                if(size != null){
                    return size.longValue();
                }
            }
            long result = 0l;
            if(clazz.isInterface()){
                // インスタンスの型が分からないので諦める
                result = 8l;
            }else if(clazz.isArray()){
                if(obj == null){
                    // 配列のサイズが分からないので長さ0として諦める
                    result = 12l;
                }else{
                    final int length = Array.getLength(obj);
                    result = length * 4 + 12l;
                    result = roundUp(result, 8);
                    for(int i = 0; i < length; i++){
                        Object element = Array.get(obj, i);
                        if(element != null){
                            result += calculateMemorySize(
                                element.getClass(),
                                element,
                                false,
                                stack
                            );
                        }
                    }
                }
                result = roundUp(result, 8);
            }else if(String.class.equals(clazz)){
                result = calculateMemorySize(clazz, null, true, stack);
                if(obj != null){
                    final String str = (String)obj;
                    result += str.length() * (4l + 2l);
                    result = roundUp(result, 8);
                }
            }else if(Collection.class.isAssignableFrom(clazz)){
                result = calculateMemorySize(clazz, null, true, stack);
                if(obj != null){
                    final Collection col = (Collection)obj;
                    if(col.size() != 0){
                        final Iterator itr = col.iterator();
                        while(itr.hasNext()){
                            Object element = itr.next();
                            if(element != null){
                                result += calculateMemorySize(
                                    element.getClass(),
                                    element,
                                    true,
                                    stack
                                );
                            }
                        }
                    }
                }
            }else if(Map.class.isAssignableFrom(clazz)){
                result = calculateMemorySize(clazz, null, true, stack);
                if(obj != null){
                    final Map map = (Map)obj;
                    if(map.size() != 0){
                        final Iterator entries = map.entrySet().iterator();
                        while(entries.hasNext()){
                            Map.Entry entry = (Map.Entry)entries.next();
                            Object key = entry.getKey();
                            if(key != null){
                                result += calculateMemorySize(
                                    key.getClass(),
                                    key,
                                    true,
                                    stack
                                );
                            }
                            Object value = entry.getValue();
                            if(value != null){
                                result += calculateMemorySize(
                                    value.getClass(),
                                    value,
                                    true,
                                    stack
                                );
                            }
                        }
                    }
                }
            }else{
                Class tmpClass = clazz;
                result += 8;
                do{
                    final Field[] fields = tmpClass.getDeclaredFields();
                    if(fields != null){
                        for(int i = 0; i < fields.length; i++){
                            final Field field = fields[i];
                            if(Modifier.isStatic(field.getModifiers())){
                                continue;
                            }
                            final Class fieldType = field.getType();
                            result += calculateFieldMemorySize(
                                fieldType,
                                true
                            );
                        }
                        if(isRoundUp){
                            result = roundUp(result, 8);
                        }
                    }
                    tmpClass = tmpClass.getSuperclass();
                }while(tmpClass != null);
                if(obj != null && isCalculateProperty){
                    final SimpleProperty[] props
                         = SimpleProperty.getProperties(obj);
                    for(int i = 0; i < props.length; i++){
                        if(!props[i].isReadable(obj)){
                            continue;
                        }
                        Object val = null;
                        Class type = null;
                        try{
                            final Method method = props[i].getReadMethod(obj);
                            if(Modifier.isStatic(method.getModifiers())){
                                continue;
                            }
                            type = props[i].getPropertyType(obj);
                            if(type.isPrimitive()){
                                continue;
                            }
                            val = props[i].getProperty(obj);
                        }catch(InvocationTargetException e){
                        }catch(NoSuchPropertyException e){
                        }
                        if(val != null){
                            result += calculateMemorySize(
                                type,
                                val,
                                true,
                                stack
                            );
                        }
                    }
                }
            }
            if(obj == null){
                tmpMemorySizeMap.put(clazz, new Long(result));
            }
            return result;
        }finally{
            stack.remove(index);
        }
    }
    
    private long calculateFieldMemorySize(Class clazz, boolean isRoundUp){
        if(clazz.isPrimitive()){
            if(Byte.TYPE.equals(clazz) || Boolean.TYPE.equals(clazz)){
                return 1l;
            }else if(Character.TYPE.equals(clazz) || Short.TYPE.equals(clazz)){
                return 2l;
            }else if(Integer.TYPE.equals(clazz) || Float.TYPE.equals(clazz)){
                return 4l;
            }else{
                return 8l;
            }
        }else{
            if(clazz.isArray()){
                // 配列のサイズが分からないので長さ0として諦める
                return 16l;
            }else{
                return 4l;
            }
        }
    }
    
    /**
     * キャッシュ参照を追加する。<p>
     * 引数で渡されたキャッシュ参照を保持する。同時に、{@link CachedReference#addCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void add(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        synchronized(references){
            if(!references.containsKey(ref)){
                Long size = null;
                if(!isCalculateOnValidate){
                    size = new Long(calculateMemorySize(ref.get(this)));
                    if(currentUsedMemorySize < 0){
                        currentUsedMemorySize = 0;
                    }
                    currentUsedMemorySize += size.longValue();
                }
                references.put(ref, size);
                ref.addCacheRemoveListener(this);
            }
        }
    }
    
    /**
     * キャッシュ参照を削除する。<p>
     * 引数で渡されたキャッシュ参照を内部で保持している場合は、破棄する。同時に、{@link CachedReference#removeCacheRemoveListener(CacheRemoveListener)}で、{@link CacheRemoveListener}として自分自身を登録解除する。<br>
     *
     * @param ref キャッシュ参照
     */
    public void remove(CachedReference ref){
        if(references == null || ref == null){
            return;
        }
        synchronized(references){
            if(references.containsKey(ref)){
                final Long size = (Long)references.remove(ref);
                ref.removeCacheRemoveListener(this);
                if(!isCalculateOnValidate && size != null){
                    currentUsedMemorySize -= size.longValue();
                    if(currentUsedMemorySize < 0){
                        currentUsedMemorySize = 0;
                    }
                }
            }
        }
    }
    
    /**
     * ヒープメモリの使用率であふれ検証を行う。<p>
     * 以下の計算式で、あふれ数を計算する。但し、計算結果が負の場合は、0とする。<br>
     * （理論使用メモリ - 最大メモリ）÷（理論使用メモリ÷キャッシュサイズ）
     *
     * @return あふれ検証を行った結果あふれが発生する場合、あふれ数を返す。あふれない場合は、0を返す
     */
    public int validate(){
        if(references == null || references.size() == 0){
            return 0;
        }
        synchronized(references){
            if(getState() != STARTED){
                return 0;
            }
            if(isCalculateOnValidate){
                currentUsedMemorySize = 0;
                final Iterator entries = references.entrySet().iterator();
                while(entries.hasNext()){
                    final Map.Entry entry = (Map.Entry)entries.next();
                    final CachedReference ref
                         = (CachedReference)entry.getKey();
                    final long size = calculateMemorySize(ref.get(this));
                    if(currentUsedMemorySize < 0){
                        currentUsedMemorySize = 0;
                    }
                    currentUsedMemorySize += size;
                }
            }
            
            if(currentUsedMemorySize < maxMemorySize){
                return 0;
            }
            double usedAverage = currentUsedMemorySize / references.size();
            long overflowMemorySize = currentUsedMemorySize - maxMemorySize;
            final double overflowSize = overflowMemorySize / usedAverage;
            return overflowSize > 0.0 ? (int)Math.ceil(overflowSize) : 0;
        }
    }
    
    /**
     * あふれ検証を実行するために保持している情報を初期化する。<p>
     * {@link #add(CachedReference)}で渡されたキャッシュ参照を全て破棄する。<br>
     */
    public void reset(){
        if(references != null){
            references.clear();
        }
        currentUsedMemorySize = 0;
    }
    
    /**
     * キャッシュから削除されたキャッシュ参照の通知を受ける。<p>
     * {@link #remove(CachedReference)}を呼び出す。<br>
     *
     * @param ref キャッシュから削除されたキャッシュ参照
     */
    public void removed(CachedReference ref){
        remove(ref);
    }
}
