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
// パッケージ
// インポート
package jp.ossc.nimbus.service.codemaster.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jp.ossc.nimbus.service.cache.CachedReference;

/**
 * CacheReferenceの管理MAPクラス<p>
 * CacheReferenceの管理MAP
 * ValueはCacheReference以外は無視される
 * @version $Name:  $
 * @author K.Nagai
 * @since 1.0
 */
public class CacheMap extends HashMap {
    
    private static final long serialVersionUID = -7287733115980283710L;
    
    //初期容量(100マスタ登録を想定)
    public final static int   CODEMASTERS_INITIAL_CAPACITY=134;
    //ロードファクタ
    public final static float CODEMASTERS_LOAD_FACTOR     =0.75f;
    /**
     * キャッシュMAP
     */
    public CacheMap() {
        super(CODEMASTERS_INITIAL_CAPACITY,CODEMASTERS_LOAD_FACTOR);
    }

    /* (非 Javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        //リファレンス比較のみ
        if( value == null ){ return false; }
        if( value instanceof CachedReference ){
            return super.containsValue((CachedReference)value);
        }
        return false;
    }

    /* (非 Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map map) {
        final Iterator ite = map.keySet().iterator();
        for( ; ite.hasNext() ; ){
            final Object key = ite.next();
            final Object value = map.get(key);
            if( key == null ){ return; }
            if( key instanceof CachedReference ){
                //そのまま登録
                super.put(key,value);
            } 
        }
    }

    /* (非 Javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    public Object get(Object arg0) {
        final CachedReference ref = (CachedReference) super.get(arg0);
        //実参照化して返却
        if( ref == null ) return null;
        return ref.get();
    }

    /* (非 Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        if( value == null ) return null;
        if( value instanceof CachedReference ){
            return super.put(key,value);
        }
        return null;
    }
}
