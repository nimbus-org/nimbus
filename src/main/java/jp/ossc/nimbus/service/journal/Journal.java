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
package jp.ossc.nimbus.service.journal;

import java.util.*;
import jp.ossc.nimbus.service.journal.editorfinder.*;

/**
 * ジャーナルインタフェース。<p>
 * 以下のようなジャーナルを取得する。<br>
 * <ul>
 *   <li>アクセス開始時刻</li>
 *   <li>アクセス終了時刻</li>
 *   <li>アクセス時間</li>
 *   <li>アクセス識別子</li>
 *   <li>アクセスによる入出力情報</li>
 * </ul>
 * <p>
 * また、複数階層のシステムで各階層でのジャーナルを出力し、それを一連のジャーナルとして出力する事を実現するために、ジャーナルの入れ子構造をサポートする。<br>
 * 単純な1階層のジャーナル取得は、以下のように行う。<br>
 * <pre>
 *   :
 * Journal journal = (Journal)ServiceManagerFactory.getServiceObject("Journal");
 * try{
 *     journal.startJournal("Request");
 *       :
 *     journal.addInfo("Input", input);
 *       :
 *     Fuga output = hoge.getFuga(input);
 *       :
 *     journal.addInfo("Output", output);
 *       :
 * }finally{
 *     journal.endJournal();
 * }
 * </pre>
 * このコードにより取得されるジャーナルは、"Request"というジャーナルの中に"Input"と"Output"が含まれるという構造になる。<br>
 * また、上記のhoge.getFuga()メソッドの先の下位の階層で、以下のような同様のコードがあった場合、<br>
 * <pre>
 *   :
 * Journal journal = (Journal)ServiceManagerFactory.getServiceObject("Journal");
 * try{
 *     journal.addStartStep("Request2");
 *       :
 *     journal.addInfo("Input2", input);
 *       :
 *     journal.addInfo("Output2", output);
 *       :
 * }finally{
 *     journal.addEndStep();
 * }
 * </pre>
 * この2階層のコードにより取得されるジャーナルは、"Request"というジャーナルの中に"Input"と"Request2"と"Output"が含まれ、更に"Request2"には、"Input2"と"Output2"が含まれるという入れ子構造になる。<br>
 *
 * @author H.Nakano
 */
public interface Journal {
    //レベルを何段階か作る
    //有効なレベル
    
    /**
     * Journal出力レベル DEBUG。<p>
     */
    public static final int JOURNAL_LEVEL_DEBUG = 0;
    
    /**
     * Journal出力レベル INFO。<p>
     */
    public static final int JOURNAL_LEVEL_INFO = 25;
    
    /**
     * Journal出力レベル WARN。<p>
     */
    public static final int JOURNAL_LEVEL_WARN = 50;
    
    /**
     * Journal出力レベル ERROR。<p>
     */
    public static final int JOURNAL_LEVEL_ERROR = 75;
    
    /**
     * Journal出力レベル FATAL。<p>
     */
    public static final int JOURNAL_LEVEL_FATAL = 100;
    
    /**
     * ジャーナルに出力されるリクエストIDを取得する。<p>
     * 
     * @return ジャーナルに出力されるリクエストID
     */
    public String getRequestId();
    
    /**
     * ジャーナルに出力されるリクエストIDを設定する。<p>
     * 
     * @param requestID ジャーナルに出力されるリクエストID
     */
    public void setRequestId(String requestID);
    
    /**
     * ジャーナルの取得を開始する。<p>
     * {@link #startJournal(String, Date, EditorFinder) startJournal(key, null, null)}で呼び出すのと同じである。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @see #startJournal(String, Date, EditorFinder)
     */
    public void startJournal(String key);
    
    /**
     * ジャーナルの取得を開始する。<p>
     * {@link #startJournal(String, Date, EditorFinder) startJournal(key, null, finder)}で呼び出すのと同じである。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     * @see #startJournal(String, Date, EditorFinder)
     */
    public void startJournal(String key, EditorFinder finder);
    
    /**
     * ジャーナルの取得を開始する。<p>
     * {@link #startJournal(String, Date, EditorFinder) startJournal(key, startTime, null)}で呼び出すのと同じである。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @param startTime ジャーナル取得開始時刻
     * @see #startJournal(String, Date, EditorFinder)
     */
    public void startJournal(String key, Date startTime);
    
    /**
     * ジャーナルの取得を開始する。<p>
     * ジャーナルのルートステップを作成する。<br>
     * 既にルートステップが作成されている場合は、その子ステップを作成する。<br>
     * 作成されたステップに追加されたジャーナル情報や子ステップは、それぞれで特に指定がなければ、ここで指定された{@link EditorFinder}で{@link JournalEditor}が検索され、編集される。<br>
     * EditorFinderが指定されていない場合は、親ステップを開始した時に指定されたEditorFinderが適用される。さらにルートステップの場合は、指定がなければ、JournalサービスのデフォルトのEditorFinderが適用される。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @param startTime ジャーナル取得開始時刻
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     */
    public void startJournal(
        String key,
        Date startTime,
        EditorFinder finder
    );
    
    /**
     * ジャーナルの取得を終了する。<p>
     * {@link #endJournal(Date) endJournal(null)}で呼び出すのと同じである。<br>
     *
     * @see #endJournal(Date)
     */
    public void endJournal();
    
    /**
     * ジャーナルの取得を終了する。<p>
     * カレントのステップを終了する。<br>
     * また、カレントのステップがルートステップの場合には、ジャーナル出力を行い、ステップをクリアする。<br>
     * 
     * @param endTime ジャーナル取得終了時刻
     */
    public void endJournal(Date endTime);
    
    /**
     * カレントのステップにジャーナル情報を追加する。<p>
     * {@link #addInfo(String, Object, EditorFinder) addInfo(key, value, null)}で呼び出すのと同じである。<br>
     * 
     * @param key ステップに追加するジャーナル情報のキー
     * @param value 追加するジャーナル情報
     * @see #addInfo(String, Object, EditorFinder)
     */
    public void addInfo(String key, Object value);
    
    /**
     * カレントのステップにジャーナル情報を追加する。<p>
     * 追加されたジャーナル情報は、指定された{@link EditorFinder}で{@link JournalEditor}が検索され、編集される。<br>
     * EditorFinderが指定されていない場合は、カレントのステップを開始した時に指定されたEditorFinderが適用される。<br>
     * 
     * @param key ステップに追加するジャーナル情報のキー
     * @param value 追加するジャーナル情報
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     */
    public void addInfo(
        String key,
        Object value,
        EditorFinder finder
    );
    
    /**
     * カレントのステップにジャーナル情報を追加する。<p>
     * 出力レベル制御の後に、{@link #addInfo(String, Object, EditorFinder) addInfo(key, value, null)}を呼び出す。<br>
     * 
     * @param key ステップに追加するジャーナル情報のキー
     * @param value 追加するジャーナル情報
     * @param level 出力レベル
     * @see #addInfo(String, Object, EditorFinder)
     */
    public void addInfo(String key, Object value, int level);
    
    /**
     * カレントのステップにジャーナル情報を追加する。<p>
     * 出力レベル制御の後に、{@link #addInfo(String, Object, EditorFinder) addInfo(key, value, finder)}を呼び出す。<br>
     * 
     * @param key ステップに追加するジャーナル情報のキー
     * @param value 追加するジャーナル情報
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     * @param level 出力レベル
     * @see #addInfo(String, Object, EditorFinder)
     */
    public void addInfo(
        String key,
        Object value,
        EditorFinder finder,
        int level
    );
    
    /**
     * カレントのステップに追加されたジャーナル情報のうち、指定されたインデックス以降を削除する。<p>
     *
     * @param from インデックス
     */
    public void removeInfo(int from);
    
    /**
     * 子ステップのジャーナルの取得を開始する。<p>
     * {@link #addStartStep(String, Date, EditorFinder) addStartStep(key, null, null)}で呼び出すのと同じである。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @see #addStartStep(String, Date, EditorFinder)
     */
    public void addStartStep(String key);
    
    /**
     * 子ステップのジャーナルの取得を開始する。<p>
     * {@link #addStartStep(String, Date, EditorFinder) addStartStep(key, null, finder)}で呼び出すのと同じである。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     * @see #addStartStep(String, Date, EditorFinder)
     */
    public void addStartStep(String key, EditorFinder finder);
    
    /**
     * 子ステップのジャーナルの取得を開始する。<p>
     * {@link #addStartStep(String, Date, EditorFinder) addStartStep(key, startTime, null)}で呼び出すのと同じである。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @param startTime ジャーナル取得開始時刻
     * @see #addStartStep(String, Date, EditorFinder)
     */
    public void addStartStep(String key, Date startTime);
    
    /**
     * 子ステップのジャーナルの取得を開始する。<p>
     * ジャーナルの子ステップを作成する。<br>
     * ルートステップが作成されていない場合は、何もしない。<br>
     * 作成されたステップに追加されたジャーナル情報や子ステップは、それぞれで特に指定がなければ、ここで指定された{@link EditorFinder}で{@link JournalEditor}が検索され、編集される。<br>
     * EditorFinderが指定されていない場合は、親ステップを開始した時に指定されたEditorFinderが適用される。<br>
     * 
     * @param key 取得するジャーナル情報のキー
     * @param startTime ジャーナル取得開始時刻
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     */
    public void addStartStep(
        String key,
        Date startTime,
        EditorFinder finder
    );
    
    /**
     * 子ステップのジャーナルの取得を終了する。<p>
     * {@link #addEndStep(Date) addEndStep(null)}で呼び出すのと同じである。<br>
     *
     * @see #addEndStep(Date)
     */
    public void addEndStep();
    
    /**
     * 子ステップのジャーナルの取得を終了する。<p>
     * カレントのステップを終了する。<br>
     * 
     * @param endTime ジャーナル取得終了時刻
     */
    public void addEndStep(Date endTime);
    
    /**
     * 現在のジャーナル出力文字列を取得する。<p>
     * 
     * @param finder ジャーナルを編集する{@link JournalEditor}を検索する{@link EditorFinder}
     * @return 現在のジャーナル出力文字列
     */
    public String getCurrentJournalString(EditorFinder finder);
    
    /**
     * ジャーナルが開始されているかどうかを判定する。<p>
     *
     * @return ジャーナルが開始されている場合は、true
     */
    public boolean isStartJournal();
}
