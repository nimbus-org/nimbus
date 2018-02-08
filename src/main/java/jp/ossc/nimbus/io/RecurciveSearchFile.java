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
package jp.ossc.nimbus.io;

import java.io.*;
import java.util.*;

/**
 * 再帰的FileList機能を付加したファイル。<p>
 *
 * @author H.Nakano
 */
public class RecurciveSearchFile extends File implements Serializable {
    
    private static final long serialVersionUID = 4549749658684567046L;
    
    /**
     * 検索種別：ファイルのみ検索する。<p>
     * デフォルト値。<br>
     */
    public static final int SEARCH_TYPE_FILE = 0;
    
    /**
     * 検索種別：ディレクトリのみ検索する。<p>
     */
    public static final int SEARCH_TYPE_DIR = 1;
    
    /**
     * 検索種別：ファイルとディレクトリの両方を検索する。<p>
     */
    public static final int SEARCH_TYPE_ALL = 2;
    
    private static final String REGEX_ESCAPE_ESCAPE = Character.toString((char) 0x00);
    
    /**
     * 指定されたパスのファイルインスタンスを生成する。<p>
     *
     * @param pathname パス
     */
    public RecurciveSearchFile(String pathname) {
        super(pathname);
    }
    
    /**
     * 指定されたファイルのファイルインスタンスを生成する。<p>
     *
     * @param file ファイル
     */
    public RecurciveSearchFile(File file) {
        super(file.getPath());
    }
    
    /**
     * 指定されたパスのファイルインスタンスを生成する。<p>
     *
     * @param parent 親パス
     * @param child 子パス
     */
    public RecurciveSearchFile(File parent, String child) {
        super(parent, child);
    }
    
    /**
     * 指定されたパスのファイルインスタンスを生成する。<p>
     *
     * @param parent 親パス
     * @param child 子パス
     */
    public RecurciveSearchFile(String parent, String child) {
        super(parent, child);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルパスを取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @return ファイルパス配列
     */
    public String[] listAllTree() {
        return listAllTree(SEARCH_TYPE_FILE);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルパスを取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param searchType 検索種別
     * @return ファイルパス配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public String[] listAllTree(int searchType) {
        if (!isDirectory()) {
            return null;
        }
        final List dirList = new ArrayList();
        dirList.add(this);
        final List fileList = recurciveSerach(dirList, searchType);
        final String[] ret = new String[fileList.size()];
        for (int cnt = 0; cnt < ret.length; cnt++) {
            File tmp = (File) fileList.get(cnt);
            ret[cnt] = tmp.getAbsolutePath();
        }
        return ret;
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ
     * @return ファイルパス配列
     */
    public String[] listAllTree(FilenameFilter filter) {
        return listAllTree(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ
     * @param searchType 検索種別
     * @return ファイルパス配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public String[] listAllTree(FilenameFilter filter, int searchType) {
        if (!isDirectory()) {
            return null;
        }
        final FilenameFilter[] filters = new FilenameFilter[1];
        filters[0] = filter;
        return listAllTree(filters, searchType);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ配列
     * @return ファイルパス配列
     */
    public String[] listAllTree(FilenameFilter[] filter) {
        return listAllTree(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ配列
     * @param searchType 検索種別
     * @return ファイルパス配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public String[] listAllTree(FilenameFilter[] filter, int searchType) {
        if (!isDirectory()) {
            return null;
        }
        final List dirList = new ArrayList();
        dirList.add(this);
        final List fileList = filteringRecurciveSerach(dirList, filter, searchType);
        String[] ret = new String[fileList.size()];
        for (int cnt = 0; cnt < ret.length; cnt++) {
            final File tmp = (File) fileList.get(cnt);
            ret[cnt] = tmp.getAbsolutePath();
        }
        return ret;
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @return ファイル配列
     */
    public File[] listAllTreeFiles() {
        return listAllTreeFiles(SEARCH_TYPE_FILE);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param searchType 検索種別
     * @return ファイル配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public File[] listAllTreeFiles(int searchType) {
        if (!isDirectory()) {
            return null;
        }
        final List dirList = new ArrayList();
        dirList.add(this);
        final List fileList = recurciveSerach(dirList, searchType);
        final File[] ret = new File[fileList.size()];
        for (int cnt = 0; cnt < ret.length; cnt++) {
            ret[cnt] = (File) fileList.get(cnt);
        }
        return ret;
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ
     * @return ファイル配列
     */
    public File[] listAllTreeFiles(FilenameFilter filter) {
        return listAllTreeFiles(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ
     * @param searchType 検索種別
     * @return ファイル配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public File[] listAllTreeFiles(FilenameFilter filter, int searchType) {
        if (!isDirectory()) {
            return null;
        }
        final List dirList = new ArrayList();
        dirList.add(this);
        final FilenameFilter[] filters = new FilenameFilter[1];
        filters[0] = filter;
        final List fileList = filteringRecurciveSerach(dirList, filters, searchType);
        final File[] ret = new File[fileList.size()];
        for (int cnt = 0; cnt < ret.length; cnt++) {
            ret[cnt] = (File) fileList.get(cnt);
        }
        return ret;
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ配列
     * @return ファイル配列
     */
    public File[] listAllTreeFiles(FilenameFilter filter[]) {
        return listAllTreeFiles(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * このファイルが示すディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     * このファイルがディレクトリを示さない場合には、nullを返す。
     *
     * @param filter フィルタ配列
     * @param searchType 検索種別
     * @return ファイル配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public File[] listAllTreeFiles(FilenameFilter filter[], int searchType) {
        if (!isDirectory()) {
            return null;
        }
        final List dirList = new ArrayList();
        dirList.add(this);
        final List fileList = filteringRecurciveSerach(dirList, filter, searchType);
        File[] ret = new File[fileList.size()];
        for (int cnt = 0; cnt < ret.length; cnt++) {
            ret[cnt] = (File) fileList.get(cnt);
        }
        return ret;
    }
    
    /**
     * 指定されたディレクトリリストの各ディレクトリ配下のサブディレクトリを含めた全てのファイルのリストを取得する。<p>
     *
     * @param dirList ディレクトリのリスト
     * @param searchType 検索種別
     * @return ファイルのリスト
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    protected List recurciveSerach(List dirList, int searchType) {
        final List fileList = new ArrayList();
        while (dirList.size() > 0) {
            File dir = (File) dirList.remove(0);
            File[] list = dir.listFiles();
            if (list != null) {
                for (int cnt = 0; cnt < list.length; cnt++) {
                    File tmp = list[cnt];
                    final boolean isDir = tmp.isDirectory();
                    final boolean isFile = tmp.isFile();
                    if (!isDir && !isFile) {
                        continue;
                    }
                    if (isDir) {
                        dirList.add(tmp);
                    }
                    switch (searchType) {
                    case SEARCH_TYPE_FILE:
                        if (isDir) {
                            continue;
                        }
                        break;
                    case SEARCH_TYPE_DIR:
                        if (isFile) {
                            continue;
                        }
                        break;
                    case SEARCH_TYPE_ALL:
                    default:
                    }
                    fileList.add(tmp);
                }
            }
        }
        return fileList;
    }
    
    /**
     * 指定されたディレクトリリストの各ディレクトリ配下のサブディレクトリを含めた全てのファイルを、指定されたフィルタでフィルタリングした結果を取得する。<p>
     *
     * @param dirList ディレクトリのリスト
     * @param filter フィルタ配列
     * @param searchType 検索種別
     * @return ファイルパス配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    protected List filteringRecurciveSerach(
        List dirList,
        FilenameFilter[] filter,
        int searchType
    ){
        final List fileList = new ArrayList();
        while (dirList.size() > 0) {
            File dir = (File) dirList.remove(0);
            File[] list = dir.listFiles();
            for (int cnt = 0; cnt < list.length; cnt++) {
                File tmp = list[cnt];
                final boolean isDir = tmp.isDirectory();
                final boolean isFile = tmp.isFile();
                if (!isDir && !isFile) {
                    continue;
                }
                if (isDir) {
                    dirList.add(tmp);
                }
                switch (searchType) {
                case SEARCH_TYPE_FILE:
                    if (isDir) {
                        continue;
                    }
                    break;
                case SEARCH_TYPE_DIR:
                    if (isFile) {
                        continue;
                    }
                    break;
                case SEARCH_TYPE_ALL:
                default:
                }
                boolean check = true;
                for (int fcnt = 0; fcnt < filter.length; fcnt++) {
                    if (!filter[fcnt].accept(tmp.getParentFile(), tmp.getName())) {
                        check = false;
                        break;
                    }
                }
                if (check) {
                    fileList.add(tmp);
                }
            }
        }
        return fileList;
    }
    
    /**
     * このディレクトリ配下で、指定された正規表現に一致するファイルパス配列を取得する。<p>
     *
     * @param regexPath パスの正規表現
     * @return ファイルパス配列
     * @see #listAllTreeFiles(String, int)
     */
    public String[] listAllTree(String regexPath) {
        return listAllTree(regexPath, SEARCH_TYPE_FILE);
    }
    
    /**
     * このディレクトリ配下で、指定された正規表現に一致するファイルパス配列を取得する。<p>
     *
     * @param regexPath パスの正規表現
     * @param searchType 検索種別
     * @return ファイルパス配列
     * @see #listAllTreeFiles(String, int)
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public String[] listAllTree(String regexPath, int searchType) {
        final File[] files = listAllTreeFiles(regexPath, searchType);
        String[] result = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            result[i] = files[i].getAbsolutePath();
        }
        return result;
    }
    
    /**
     * このディレクトリ配下で、指定された正規表現に一致するファイル配列を取得する。<p>
     *
     * @param regexPath パスの正規表現
     * @return ファイル配列
     * @see #listAllTreeFiles(String, int)
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public File[] listAllTreeFiles(String regexPath) {
        return listAllTreeFiles(regexPath, SEARCH_TYPE_FILE);
    }
    
    /**
     * このディレクトリ配下で、指定された正規表現に一致するファイル配列を取得する。<p>
     * パスの正規表現には、通常の正規表現に加えて"**"という指定が可能である。<br>
     * "**"と指定された場合、途中の全てのディレクトリ構造を含む事を示す。<br>
     * 制限事項として、正規表現のエスケープ文字である"\"は、Windows OSのパスセパレータにもなっているため、正規表現として"\"を指定したい場合は、"\\"と指定する事。<br>
     *
     * @param regexPath パスの正規表現
     * @param searchType 検索種別
     * @return ファイル配列
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public File[] listAllTreeFiles(String regexPath, int searchType) {
        regexPath = regexPath.replaceAll("\\\\\\\\", REGEX_ESCAPE_ESCAPE);
        final List result = filteringRecurciveSerachByRegEx(
            getPath().length() == 0
                 ? new File(regexPath) : new File(this, regexPath),
            searchType,
            new ArrayList()
        );
        return (File[]) result.toArray(new File[result.size()]);
    }
    
    private List filteringRecurciveSerachByRegEx(
        File file,
        int searchType,
        List result
    ){
        if (file.exists()) {
            switch (searchType) {
            case SEARCH_TYPE_FILE:
                if (file.isDirectory()) {
                    return result;
                }
                break;
            case SEARCH_TYPE_DIR:
                if (file.isFile()) {
                    return result;
                }
                break;
            case SEARCH_TYPE_ALL:
            default:
            }
            result.add(file);
            return result;
        }
        List pathList = new ArrayList();
        File f = file;
        String name = null;
        do {
            name = f.getName();
            f = f.getParentFile();
            pathList.add(0, name.length() == 0 ? "/" : name);
        } while (f != null);
        
        File allDir = null;
        for (int i = 0, imax = pathList.size(); i < imax; i++) {
            name = (String) pathList.get(i);
            f = new File(f, name);
            if ("**".equals(name)) {
                if (allDir == null) {
                    allDir = f.getParentFile();
                    if (allDir == null) {
                        allDir = new File(".");
                    }
                }
                if (i == imax - 1) {
                    name = ".*";
                } else {
                    continue;
                }
            }
            if (allDir != null) {
                RecurciveSearchFile rootDir
                     = new RecurciveSearchFile(allDir.getPath());
                if (i == imax - 1) {
                    File[] files = rootDir.listAllTreeFiles(
                        new RegexFileFilter(
                            name.replaceAll(REGEX_ESCAPE_ESCAPE, "\\\\")
                        ),
                        searchType
                    );
                    if (files != null) {
                        for (int j = 0; j < files.length; j++) {
                            result.add(files[j]);
                        }
                    }
                } else {
                    File[] dirs = rootDir.listAllTreeFiles(
                        new RegexFileFilter(
                            name.replaceAll(REGEX_ESCAPE_ESCAPE, "\\\\")
                        ),
                        RecurciveSearchFile.SEARCH_TYPE_DIR
                    );
                    if (dirs != null) {
                        final StringBuilder buf = new StringBuilder();
                        for (int j = i + 1; j < imax; j++) {
                            buf.append((String) pathList.get(j));
                            if (j != imax - 1) {
                                buf.append('/');
                            }
                        }
                        final String path = buf.toString();
                        for (int j = 0; j < dirs.length; j++) {
                            dirs[j] = new File(dirs[j], path);
                            result = filteringRecurciveSerachByRegEx(
                                dirs[j],
                                searchType,
                                result
                            );
                        }
                    }
                    break;
                }
            } else if (!f.exists()) {
                File rootDir = f.getParentFile();
                if (rootDir == null) {
                    rootDir = new File(".");
                }
                if (i == imax - 1) {
                    File[] files = rootDir.listFiles(
                        new RegexFileFilter(
                            name.replaceAll(REGEX_ESCAPE_ESCAPE, "\\\\")
                        )
                    );
                    if (files != null) {
                        for (int j = 0; j < files.length; j++) {
                            boolean isDir = files[j].isDirectory();
                            boolean isFile = files[j].isFile();
                            if (!isDir && !isFile) {
                                continue;
                            }
                            switch (searchType) {
                            case SEARCH_TYPE_FILE:
                                if (isDir) {
                                    continue;
                                }
                                break;
                            case SEARCH_TYPE_DIR:
                                if (isFile) {
                                    continue;
                                }
                                break;
                            case SEARCH_TYPE_ALL:
                            default:
                            }
                            result.add(files[j]);
                        }
                    }
                } else {
                    File[] dirs = rootDir.listFiles(
                        new RegexFileFilter(
                            name.replaceAll(REGEX_ESCAPE_ESCAPE, "\\\\")
                        )
                    );
                    if (dirs != null) {
                        final StringBuilder buf = new StringBuilder();
                        for (int j = i + 1; j < imax; j++) {
                            buf.append((String) pathList.get(j));
                            if (j != imax - 1) {
                                buf.append('/');
                            }
                        }
                        final String path = buf.toString();
                        for (int j = 0; j < dirs.length; j++) {
                            if (!dirs[j].isDirectory()) {
                                continue;
                            }
                            dirs[j] = new File(dirs[j], path);
                            result = filteringRecurciveSerachByRegEx(
                                dirs[j],
                                searchType,
                                result
                            );
                        }
                    }
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * このファイル以下を再帰的に削除する。<p>
     *
     * @return 全て削除できた場合true
     */
    public boolean deleteAllTree() {
        return deleteAllTree(this);
    }
    
    /**
     * このファイル以下を再帰的に削除する。<p>
     *
     * @param containsOwn 引数のfile自身も消す場合true
     * @return 全て削除できた場合true
     */
    public boolean deleteAllTree(boolean containsOwn) {
        return deleteAllTree(this, containsOwn);
    }
    
    /**
     * 指定されたファイル以下を再帰的に削除する。<p>
     *
     * @param file 削除するファイル
     * @return 全て削除できた場合true
     */
    public static boolean deleteAllTree(File file) {
        return deleteAllTree(file, true);
    }
    
    /**
     * 指定されたファイル以下を再帰的に削除する。<p>
     *
     * @param file 削除するファイル
     * @param containsOwn 引数のfile自身も消す場合true
     * @return 全て削除できた場合true
     */
    public static boolean deleteAllTree(File file, boolean containsOwn) {
        if (!file.exists()) {
            return true;
        }
        boolean result = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                result &= deleteAllTree(files[i]);
            }
            if(containsOwn){
                result &= file.delete();
            }
        } else if (file.isFile()) {
            if(containsOwn){
                result &= file.delete();
            }
        }
        return result;
    }
    
    /**
     * このファイル以下を指定したファイルに再帰的にコピーする。<p>
     *
     * @return 全て作成できた場合true
     * @throws Exception
     */
    public boolean copyAllTree(File file) throws IOException {
        return copyAllTree(this, file, null);
    }
    
    /**
     * このファイル以下を指定したファイルに再帰的にコピーする。<p>
     *
     * @return 全て作成できた場合true
     * @throws Exception
     */
    public boolean copyAllTree(File file, FilenameFilter filter) throws IOException {
        return copyAllTree(this, file, filter);
    }

    /**
     * 指定されたファイル以下を再帰的にコピーする。<p>
     *
     * @param fromFile　コピー元ファイル
     * @param toFile　コピー先ファイル
     * @return 全て削除できた場合true
     * @throws Exception
     */
    public static boolean copyAllTree(File fromFile, File toFile) throws IOException {
        return copyAllTree(fromFile, toFile, null);
    }
    /**
     * 指定されたファイル以下を再帰的にコピーする。<p>
     *
     * @param fromFile　コピー元ファイル
     * @param toFile　コピー先ファイル
     * @return 全て削除できた場合true
     * @throws Exception
     */
    public static boolean copyAllTree(File fromFile, File toFile, FilenameFilter filter) throws IOException {
        if (!fromFile.exists()) {
            return true;
        }
        boolean result = true;
        
        File[] files = null;
        if(filter != null){
        	files = fromFile.listFiles(filter);
        }else{
        	files = fromFile.listFiles();
        }
        if (files == null) {
            return false;
        }
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            if (file.isDirectory()) {
                result &= createDirectory(toFile, file, filter);
            } else if (file.isFile()) {
                File copyFile = new File(toFile.getAbsolutePath() + "/" + file.getName());
                dataCopy(file, copyFile);
            }
        }
        
        return result;
    }
    
    private static boolean createDirectory(File copyDir, File dir, FilenameFilter filter) throws IOException {
        File newDir = new File(copyDir.getAbsolutePath() + "/" + dir.getName());
        newDir.mkdir();
        boolean result = true;
        File[] files = null;
        if(filter != null){
        	files = dir.listFiles(filter);
        }else{
        	files = dir.listFiles();
        }
        for (int index = 0; index < files.length; index++) {
            File file = files[index];
            if (file.isDirectory()) {
                result &= createDirectory(newDir, file, filter);
            } else if (file.isFile()) {
                File copyFile = new File(newDir.getAbsolutePath() + "/" + file.getName());
                dataCopy(file, copyFile);
            }
        }
        
        return result;
    }
    
    public static void dataCopy(File fromFile, File toFile) throws IOException {
        FileInputStream fis = new FileInputStream(fromFile);
        FileOutputStream fos = new FileOutputStream(toFile);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } finally {
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }
    
    /**
     * このファイル以下を再帰的に仮想マシンが終了したときに削除されるように要求する。<p>
     */
    public void deleteOnExitAllTree() {
        deleteOnExitAllTree(this);
    }
    
    /**
     * 指定されたファイル以下を再帰的に仮想マシンが終了したときに削除されるように要求する。<p>
     *
     * @param file ファイル
     */
    public static void deleteOnExitAllTree(File file) {
        file.deleteOnExit();
        if(!file.exists()){
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteOnExitAllTree(files[i]);
            }
        }
    }
}
