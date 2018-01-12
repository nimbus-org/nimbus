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
package jp.ossc.nimbus.service.ejb;

import java.lang.reflect.*;
import javax.naming.*;
import javax.ejb.*;

/**
 * EJBファクトリ。<p>
 * EJBHomeのJNDIに対するlookup、及びEJBHomeからEJBObjectの生成を行う。また、生成したEJBObjectをキャッシュする。<br>
 * EJBLocalHomeのJNDIに対するlookup、及びEJBLocalHomeからEJBLocalObjectの生成を行う。<br>
 *
 * @author  M.Takata
 */
public interface EJBFactory{
    
    /**
     * EJBHomeのcreateメソッドのメソッド名。<p>
     */
    public static final String EJB_CREATE_METHOD_NAME = "create";
    
    /**
     * EJBのJNDI名を指定して、EJBObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBHomeに対して、引数なしのcreateメソッドを呼び出してEJBObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBLocalObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBLocalHomeに対して、引数なしのcreateメソッドを呼び出してEJBLocalObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBHomeに対して、指定した引数のcreateメソッドを呼び出してEJBObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBLocalObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBLocalHomeに対して、指定した引数のcreateメソッドを呼び出してEJBLocalObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBHomeに対して、引数なしのcreateメソッドを呼び出してEJBObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @param homeType EJBHomeのクラスオブジェクト
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Class homeType
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBLocalObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBLocalHomeに対して、引数なしのcreateメソッドを呼び出してEJBLocalObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @param homeType EJBLocalHomeのクラスオブジェクト
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Class homeType
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBHomeに対して、指定した引数のcreateメソッドを呼び出してEJBObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @param homeType EJBHomeのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBLocalObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBLocalHomeに対して、指定した引数のcreateメソッドを呼び出してEJBLocalObjectを取得する。<br>
     *
     * @param name EJBのJNDI名
     * @param homeType EJBLocalHomeのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Class homeType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBHomeに対して、指定した引数のcreateメソッドを呼び出してEJBObjectを取得する。また、取得したEJBObjectを目的のタイプにキャストして返す。<br>
     *
     * @param name EJBのJNDI名
     * @param homeType EJBHomeのクラスオブジェクト
     * @param remoteType EJBObjectのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBObject
     * @exception NamingException EJBHomeのlookupに失敗した場合
     * @exception CreateException EJBHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBObject get(
        String name,
        Class homeType,
        Class remoteType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * EJBのJNDI名を指定して、EJBLocalObjectを取得する。<p>
     * 指定されたJNDI名でlookupしたEJBLocalHomeに対して、指定した引数のcreateメソッドを呼び出してEJBLocalObjectを取得する。また、取得したEJBLocalObjectを目的のタイプにキャストして返す。<br>
     *
     * @param name EJBのJNDI名
     * @param homeType EJBLocalHomeのクラスオブジェクト
     * @param localType EJBLocalObjectのクラスオブジェクト
     * @param paramTypes 引数の型配列
     * @param params 引数の配列
     * @return 指定したJNDI名に対応するEJBLocalObject
     * @exception NamingException EJBLocalHomeのlookupに失敗した場合
     * @exception CreateException EJBLocalHomeのcreateメソッドを呼び出した際に例外が発生した場合
     * @exception NoSuchMethodException EJBLocalHomeのcreateメソッドが見つからない場合
     * @exception IllegalAccessException EJBLocalHomeのcreateメソッドを呼び出した時に、アクセス修飾子によるアクセス権限が不正な場合
     * @exception InvocationTargetException EJBLocalHomeのcreateメソッドを呼び出した時に、呼び出し先で何らかの例外が発生した場合
     */
    public EJBLocalObject getLocal(
        String name,
        Class homeType,
        Class localType,
        Class[] paramTypes,
        Object[] params
    ) throws NamingException, CreateException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException;
    
    /**
     * 指定したJNDI名のEJBのキャッシュを無効化する。<p>
     * 
     * @param name EJBのJNDI名
     */
    public void invalidate(String name);
    
    /**
     * EJBのキャッシュを無効化する。<p>
     */
    public void invalidate();
}
