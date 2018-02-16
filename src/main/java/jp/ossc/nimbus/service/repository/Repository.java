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
package jp.ossc.nimbus.service.repository;

import java.util.*;

/**
 * リポジトリインタフェース。<p>
 * オブジェクトを登録するリポジトリとして必要なインタフェースを定義する。<br>
 *
 * @author M.Takata
 */
public interface Repository{
    
    /**
     * 指定した名前のオブジェクトをリポジトリ内から取得する。<p>
     *
     * @param name 登録名
     * @return 登録されたオブジェクト
     */
    public Object get(String name);
    
    /**
     * 指定した名前のオブジェクトをリポジトリに登録する。<p>
     *
     * @param name 登録名
     * @param obj 登録するオブジェクト
     * @return 登録された場合true
     */
    public boolean register(String name, Object obj);
    
    /**
     * 指定した名前のオブジェクトをリポジトリ内から削除する。<p>
     *
     * @param name 登録名
     * @return 削除された場合true
     */
    public boolean unregister(String name);
    
    /**
     * 指定した名前のオブジェクトがリポジトリ内に登録されているか調べる。<p>
     *
     * @param name 登録名
     * @return 登録されている場合true
     */
    public boolean isRegistered(String name);
    
    /**
     * リポジトリに登録されているオブジェクトの名前の集合を取得する。<p>
     *
     * @return 登録されているオブジェクトの名前の集合
     */
    public Set nameSet();
    
    /**
     * リポジトリに登録されているオブジェクトの集合を取得する。<p>
     *
     * @return 登録されているオブジェクトの集合
     */
    public Set registeredSet();
}