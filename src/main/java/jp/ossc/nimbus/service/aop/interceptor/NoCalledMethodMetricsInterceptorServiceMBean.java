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
package jp.ossc.nimbus.service.aop.interceptor;

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link NoCalledMethodMetricsInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see NoCalledMethodMetricsInterceptorService
 */
public interface NoCalledMethodMetricsInterceptorServiceMBean
 extends ServiceBaseMBean{
    
    /**
     * 統計を取る対象のクラスのクラス修飾子を指定する。<p>
     * ここで指定されたクラス修飾子のクラスが統計を取る対象となる。指定しない場合は、クラス修飾子は限定されない。<br>
     * 修飾子を否定する場合は、各修飾子の前に"!"を付与すること。
     *
     * @param modifiers クラス修飾子文字列
     */
    public void setTargetClassModifiers(String modifiers);
    
    /**
     * 統計を取る対象のクラスのクラス修飾子を取得する。<p>
     *
     * @return クラス修飾子文字列
     */
    public String getTargetClassModifiers();
    
    /**
     * 統計を取る対象のクラス名を指定する。<p>
     * ここで指定されたクラス名のクラスが統計を取る対象となる。指定しない場合は、クラス名は限定されない。また、正規表現を指定する事も可能である。
     *
     * @param name パッケージ名を含む完全修飾クラス名。正規表現も可。
     */
    public void setTargetClassName(String name);
    
    /**
     * 統計を取る対象のクラス名を取得する。<p>
     *
     * @return パッケージ名を含む完全修飾クラス名。正規表現も可。
     */
    public String getTargetClassName();
    
    /**
     * 統計を取る対象のインスタンスのクラス名を指定する。<p>
     * ここで指定されたクラス名のインスタンスが統計を取る対象となる。指定しない場合は、インスタンスは限定されない。
     *
     * @param name パッケージ名を含む完全修飾クラス名
     */
    public void setTargetInstanceClassName(String name);
    
    /**
     * 統計を取る対象のインスタンスのクラス名を取得する。<p>
     *
     * @return パッケージ名を含む完全修飾クラス名。
     */
    public String getTargetInstanceClassName();
    
    /**
     * 統計を取る対象のメソッドのメソッド修飾子を指定する。<p>
     * ここで指定されたメソッド修飾子のメソッドが統計を取る対象となる。指定しない場合は、メソッド修飾子は限定されない。<br>
     * 修飾子を否定する場合は、各修飾子の前に"!"を付与すること。
     *
     * @param modifiers メソッド修飾子文字列
     */
    public void setTargetMethodModifiers(String modifiers);
    
    /**
     * 統計を取る対象のメソッドのメソッド修飾子を取得する。<p>
     *
     * @return メソッド修飾子文字列
     */
    public String getTargetMethodModifiers();
    
    /**
     * 統計を取る対象のメソッド名を指定する。<p>
     * ここで指定されたメソッド名のメソッドが統計を取る対象となる。指定しない場合は、メソッド名は限定されない。また、正規表現を指定する事も可能である。
     *
     * @param name メソッド名。正規表現も可。
     */
    public void setTargetMethodName(String name);
    
    /**
     * 統計を取る対象のメソッド名を取得する。<p>
     *
     * @return メソッド名。正規表現も可。
     */
    public String getTargetMethodName();
    
    /**
     * 統計を取る対象のメソッドの引数の型を表すクラス名を指定する。<p>
     * ここで指定された引数型を持つメソッドが統計を取る対象となる。指定しない場合は、引数型は限定されない。また、正規表現を指定する事も可能である。
     *
     * @param paramTypes メソッドの引数の型を表すクラス名の配列。正規表現も可。
     */
    public void setTargetParameterTypes(String[] paramTypes);
    
    /**
     * 統計を取る対象のメソッドの引数の型を表すクラス名を取得する。<p>
     *
     * @return メソッドの引数の型を表すクラス名の配列。正規表現も可。
     */
    public String[] getTargetParameterTypes();
    
    /**
     * 統計を取る対象のクラスに宣言されているメソッドだけを対象にするかどうかを設定する。<p>
     * デフォルトは、falseで、継承されたメソッドも対象にする
     *
     * @param isDeclaring 統計を取る対象のクラスに宣言されているメソッドだけを対象にする場合、true
     */
    public void setDeclaringMethod(boolean isDeclaring);
    
    /**
     * 統計を取る対象のクラスに宣言されているメソッドだけを対象にするかどうかを判定する。<p>
     *
     * @return trueの場合、統計を取る対象のクラスに宣言されているメソッドだけを対象にする
     */
    public boolean isDeclaringMethod();
    
    /**
     * JVMのクラスパスに加えて、クラスパスを追加する。<p>
     * 相対パスを指定した場合は、実行パスからの相対パスに加えて、このサービスが定義されているサービス定義ファイルからの相対パスによるパス検索を行う。<br>
     *
     * @param paths クラスパスの配列
     */
    public void setClassPaths(String[] paths);
    
    /**
     * JVMのクラスパスに加えたクラスパスを取得する。<p>
     *
     * @return クラスパスの配列
     */
    public String[] getClassPaths();
    
    /**
     * 統計を取る対象のメソッド集合を取得する。<p>
     *
     * @return 統計を取る対象のMethodオブジェクトの集合
     */
    public Set getTargetMethodSet();
    
    /**
     * 統計を取る対象のメソッド一覧文字列を取得する。<p>
     *
     * @return 統計を取る対象のメソッド一覧文字列
     */
    public String getTargetMethodString();
    
    /**
     * 統計を取得した結果、対象のメソッドのうちで呼び出されていなかったメソッド集合を取得する。<p>
     *
     * @return 統計対象のメソッドのうちで呼び出されていなかったメソッド集合
     */
    public Set getNoCalledMethodSet();
    
    /**
     * 統計を取得した結果、対象のメソッドのうちで呼び出されていなかったメソッド一覧文字列を取得する。<p>
     *
     * @return 統計対象のメソッドのうちで呼び出されていなかったメソッド一覧文字列
     */
    public String getNoCalledMethodString();
    
    /**
     * 統計結果をサービスの停止時に標準出力に出力するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isOutput 出力する場合は、true
     */
    public void setOutputSystemOut(boolean isOutput);
    
    /**
     * 統計結果をサービスの停止時に標準出力に出力するかどうかを判定する。<p>
     *
     * @return trueの場合、出力する
     */
    public boolean isOutputSystemOut();
}
