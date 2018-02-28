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
package jp.ossc.nimbus.service.message;

import java.util.*;
import jp.ossc.nimbus.core.*;

/**
 * {@link MessageRecordFactoryService}サービスMBeanインタフェース。<p>
 *
 * @author H.Nakano
 */
public interface MessageRecordFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * {@link MessageRecord}インタフェースの実装クラス名を設定する。<p>
     * デフォルトは、{@link MessageRecordImpl}。<br>
     * 
     * @param className MessageRecordインタフェースの実装クラス名
     */
    public void setMessageRecordClassName(String className);
    
    /**
     * {@link MessageRecord}インタフェースの実装クラス名を取得する。<p>
     * 
     * @return MessageRecordインタフェースの実装クラス名
     */
    public String getMessageRecordClassName();
    
    /**
     * 秘密埋め込みメッセージを秘密文字でマスクするかどうかを設定する。<p>
     * デフォルトは、false。<br>
     * 
     * @param flg 秘密文字でマスクする場合true
     */
    public void setSecretMode(boolean flg);
    
    /**
     * 秘密埋め込みメッセージを秘密文字でマスクするかどうかを判定する。<p>
     * 
     * @return trueの場合、秘密文字でマスクする
     */
    public boolean isSecretMode();
    
    /**
     * 秘密文字を設定する。<p>
     * デフォルトは、nullで、メッセージ定義のままで出力される。<br>
     * 
     * @param secret 秘密文字
     */
    public void setSecretString(String secret);
    
    /**
     * 秘密文字を取得する。<p>
     * 
     * @return 秘密文字
     */
    public String getSecretString();
    
    /**
     * メッセージ定義ファイルの配置ディレクトリを設定する。<p>
     * 指定したディレクトリの直下に、デフォルトロケール用のメッセージ定義ファイルを配置する。<br>
     * 国際化対応する場合は、指定したディレクトリの直下にロケール名のディレクトリを作成して、その配下に対応するメッセージ定義ファイルを配置する。<br>
     *
     * @param dirPaths ディレクトリパス配列
     */
    public void setMessageDirPaths(String[] dirPaths);
    
    /**
     * メッセージ定義ファイルの配置ディレクトリを取得する。<p>
     *
     * @return ディレクトリパス配列
     */
    public String[] getMessageDirPaths();
    
    /**
     * メッセージ定義ファイルのクラスパス内のリソースパスを設定する。<p>
     * クラスパスにメッセージ定義ファイルを配置する。<br>
     * 国際化対応する場合は、拡張子の前にロケール名を付与する。（プロパティファイルと同様）<br>
     * デフォルトで、jp.ossc.nimbus.resource.Nimbusを必ず含む。<br>
     * 
     * @param paths メッセージ定義ファイルのクラスパス内のリソースパス配列
     */
    public void setMessageFiles(String[] paths);
    
    /**
     * メッセージ定義ファイルのクラスパス内のリソースパスを取得する。<p>
     * 
     * @return メッセージ定義ファイルのクラスパス内のリソースパス配列
     */
    public String[] getMessageFiles();
    
    /**
     * メッセージ定義のファイル拡張子を設定する。<p>
     * デフォルトは、"def"。<br>
     * 
     * @param name 拡張子文字列（ex "hogeho")
     */
    public void setExtentionOfMessageFile(String name);
    
    /**
     * メッセージ定義のファイル拡張子を取得する。<p>
     * 
     * @return 拡張子文字列
     */
    public String getExtentionOfMessageFile();
    
    /**
     * 初期読み込みするメッセージ定義ファイルのロケールを設定する。<p>
     * ここで設定しないロケールのメッセージ定義ファイルは、初めてそのロケールのメッセージが必要となった場合にロードされる。
     * 但し、デフォルトロケールは、サービス開始時にロードされる。<br>
     * 
     * @param locales ロケール文字配列
     */
    public void setLocaleStrings(String[] locales);
    
    /**
     * 初期読み込みするメッセージ定義ファイルのロケールを取得する。<p>
     * 
     * @return ロケール文字配列
     */
    public String[] getLocaleStrings();
    
    /**
     * メッセージの上書き定義を許容するかどうかを設定する。<p>
     *
     * @param isAllow 許容する場合、true
     */
    public void setAllowOverrideMessage(boolean isAllow);
    
    /**
     * メッセージの上書き定義を許容するかどうかを判定する。<p>
     *
     * @return trueの場合、許容する
     */
    public boolean isAllowOverrideMessage();
    
    /**
     * Nimbus自身のメッセージファイルを読み込むかどうかを設定する。<p>
     * デフォルトは、trueで読み込む。<br>
     *
     * @param isLoad 読み込む場合は、true
     */
    public void setLoadNimbusMessageFile(boolean isLoad);
    
    /**
     * Nimbus自身のメッセージファイルを読み込むかどうかを判定する。<p>
     *
     * @return trueの場合、読み込む
     */
    public boolean isLoadNimbusMessageFile();
    
    /**
     * 動的にメッセージ定義ファイルの配置ディレクトリを追加する。<p>
     * 同時に複数追加する場合は、;区切りで複数指定することが可能。<br>
     * このメソッドは、サービス開始後に動的にメッセージ定義を追加する場合に使用する。<br>
     * 
     * @param dirPaths  ディレクトリ指定文字列
     * @exception Exception メッセージ定義の動的ロードに失敗した場合
     */
    public void addMessageDirPaths(String dirPaths) throws Exception;
    
    /**
     * 動的にメッセージ定義ファイルのクラスパス内のリソースパスを追加する。<p>
     * 同時に複数追加する場合は、;区切りで複数指定することが可能。<br>
     * このメソッドは、サービス開始後に動的にメッセージ定義を追加する場合に使用する。<br>
     * 
     * @param paths メッセージ定義ファイルのクラスパス内のリソースパス文字列
     * @exception Exception メッセージ定義の動的ロードに失敗した場合
     */
    public void addMessageFiles(String paths) throws Exception;
    
    /**
     * メッセージ一覧を取得する。<p>
     * 
     * @return メッセージ一覧
     */
    public ArrayList getMessgaeList();
    
    /**
     * 使用されたメッセージの一覧を取得する。<p>
     * 
     * @return 使用されたメッセージ一覧
     */
    public ArrayList getUsedMessgaeList();
    
    /**
     * メッセージの使用カウントを初期化する。<p>
     */
    public void initUsedCount();


}
