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
package jp.ossc.nimbus.core;

/**
 * ファクトリサービスインタフェース。<p>
 * サービスを提供するオブジェクトを生成するファクトリインタフェースである。<p>
 * {@link Service}は、インスタンスを１つだけ{@link ServiceManager}に登録して、複数のオブジェクトから１つのインスタンスが参照されて使われるサービスを定義するためのインタフェースである。<br>
 * それに対して、このインタフェースは、インスタンスを１つだけServiceManagerに登録するところは同じであるが、サービスを行うオブジェクトは、ServiceManagerから取得する時に、{@link #newInstance()}によって毎回生成される別のオブジェクトとなる。従って、サービスを使う側は、それぞれ異なるオブジェクトを使用する事になる。<br>
 * これは、例えば、サービス定義は１つだけで、その実体となるサービスは複数使用したい場合などに用いる。<br>
 * <p>
 * このファクトリによって生成されたオブジェクトは、サービスとして登録されない。従って、そのままでは１度生成されてしまったオブジェクトは、このファクトリから切り離されるため、このファクトリの属性を変更しても、反映されることはない。<br>
 * しかし、場合によっては、このファクトリの属性を変える事で、既に生成されたオブジェクトの属性を変えたい場合もある。このファクトリは、生成したオブジェクトを管理する機能を持ち、{@link #setManagement(boolean)}によってその機能のON/OFFを制御可能である。<br>
 * 但し、注意が必要なのは、setManagement(true)にして、生成したオブジェクトは、このファクトリ内で管理されるため、使う側が使い捨てても、このファクトリサービスが破棄されない限り、ガベージされない。<br>
 * 従って、ファクトリに管理されたオブジェクトを使う側は、使い捨てで使用すべきではない。使い捨てで使用したい場合は、setManagement(false)にして、管理されないオブジェクトとして使用すべきである。<br>
 * 
 * @author M.Takata
 * @see Service
 */
public interface FactoryService{
    
    /**
     * このファクトリが生成するオブジェクトを管理するかどうかを設定する。<p>
     * trueにした場合、その後、このファクトリによって生成されるオブジェクトは、このファクトリの管理化に置かれる。管理されているオブジェクトは、このファクトリの属性の変更を反映される。（どのような属性が管理されるかは、実装に依存する）そのため、このファクトリが参照を保持するので、使う側が参照を捨てても、ガベージされない。このファクトリが、破棄された場合には、管理されているオブジェクトの参照も破棄される。<br>
     * falseにした場合、その後、このファクトリによって生成されるオブジェクトは、このファクトリの管理化に置かれない。管理されていないオブジェクトは、このファクトリの属性の変更を反映されない。そのため、このファクトリが参照を保持することはないので、使う側が参照を捨てると、ガベージの対象になる。<br>
     *
     * @param isManaged 管理する場合はtrue、管理しない場合はfalse
     * @see #isManagement()
     */
    public void setManagement(boolean isManaged);
    
    /**
     * このファクトリによって、この後に生成するオブジェクトが、管理されるかどうかを調べる。<b>
     *
     * @return 管理する場合はtrue、管理しない場合はfalse
     * @see #setManagement(boolean)
     */
    public boolean isManagement();
    
    /**
     * このファクトリが生成するオブジェクトをスレッド単位に生成するかどうかを設定する。<p>
     * デフォルトはfalse。
     *
     * @param isThreadLocal 管理する場合はtrue、管理しない場合はfalse
     * @see #isThreadLocal()
     */
    public void setThreadLocal(boolean isThreadLocal);
    
    /**
     * このファクトリが生成するオブジェクトをスレッド単位に生成するかどうかを調べる。<b>
     *
     * @return スレッド単位に生成する場合はtrue、スレッド単位に生成しない場合はfalse
     * @see #setThreadLocal(boolean)
     */
    public boolean isThreadLocal();
    
    /**
     * このファクトリが生成し管理しているオブジェクトを破棄する。<p>
     *
     * @param service 破棄するサービスオブジェクト
     */
    public void release(Object service);
    
    /**
     * このファクトリが生成し管理しているオブジェクトを全て破棄する。<p>
     */
    public void release();
    
    /**
     * サービスを提供するオブジェクトを生成する。<p>
     * {@link #isManagement()}がtrueの状態で、このメソッドを呼び出すと、生成されるオブジェクトは、このファクトリによって管理され、ファクトリの属性変更が反映される。<br>
     * isManagement()がfalseの状態で、このメソッドを呼び出すと、生成されるオブジェクトは、このファクトリによって管理されないため、ファクトリの属性変更は反映されない。使い捨てのオブジェクトである。<br>
     *
     * @return サービスを提供するオブジェクト
     * @see #setManagement(boolean)
     */
    public Object newInstance();
}