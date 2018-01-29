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
 * �ċA�IFileList�@�\��t�������t�@�C���B<p>
 *
 * @author H.Nakano
 */
public class RecurciveSearchFile extends File implements Serializable {
    
    private static final long serialVersionUID = 4549749658684567046L;
    
    /**
     * ������ʁF�t�@�C���̂݌�������B<p>
     * �f�t�H���g�l�B<br>
     */
    public static final int SEARCH_TYPE_FILE = 0;
    
    /**
     * ������ʁF�f�B���N�g���̂݌�������B<p>
     */
    public static final int SEARCH_TYPE_DIR = 1;
    
    /**
     * ������ʁF�t�@�C���ƃf�B���N�g���̗�������������B<p>
     */
    public static final int SEARCH_TYPE_ALL = 2;
    
    private static final String REGEX_ESCAPE_ESCAPE = Character.toString((char) 0x00);
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���C���X�^���X�𐶐�����B<p>
     *
     * @param pathname �p�X
     */
    public RecurciveSearchFile(String pathname) {
        super(pathname);
    }
    
    /**
     * �w�肳�ꂽ�t�@�C���̃t�@�C���C���X�^���X�𐶐�����B<p>
     *
     * @param file �t�@�C��
     */
    public RecurciveSearchFile(File file) {
        super(file.getPath());
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���C���X�^���X�𐶐�����B<p>
     *
     * @param parent �e�p�X
     * @param child �q�p�X
     */
    public RecurciveSearchFile(File parent, String child) {
        super(parent, child);
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���C���X�^���X�𐶐�����B<p>
     *
     * @param parent �e�p�X
     * @param child �q�p�X
     */
    public RecurciveSearchFile(String parent, String child) {
        super(parent, child);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C���p�X���擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @return �t�@�C���p�X�z��
     */
    public String[] listAllTree() {
        return listAllTree(SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C���p�X���擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param searchType �������
     * @return �t�@�C���p�X�z��
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
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^
     * @return �t�@�C���p�X�z��
     */
    public String[] listAllTree(FilenameFilter filter) {
        return listAllTree(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^
     * @param searchType �������
     * @return �t�@�C���p�X�z��
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
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^�z��
     * @return �t�@�C���p�X�z��
     */
    public String[] listAllTree(FilenameFilter[] filter) {
        return listAllTree(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^�z��
     * @param searchType �������
     * @return �t�@�C���p�X�z��
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
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @return �t�@�C���z��
     */
    public File[] listAllTreeFiles() {
        return listAllTreeFiles(SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param searchType �������
     * @return �t�@�C���z��
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
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^
     * @return �t�@�C���z��
     */
    public File[] listAllTreeFiles(FilenameFilter filter) {
        return listAllTreeFiles(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^
     * @param searchType �������
     * @return �t�@�C���z��
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
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^�z��
     * @return �t�@�C���z��
     */
    public File[] listAllTreeFiles(FilenameFilter filter[]) {
        return listAllTreeFiles(filter, SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃t�@�C���������f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     * ���̃t�@�C�����f�B���N�g���������Ȃ��ꍇ�ɂ́Anull��Ԃ��B
     *
     * @param filter �t�B���^�z��
     * @param searchType �������
     * @return �t�@�C���z��
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
     * �w�肳�ꂽ�f�B���N�g�����X�g�̊e�f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C���̃��X�g���擾����B<p>
     *
     * @param dirList �f�B���N�g���̃��X�g
     * @param searchType �������
     * @return �t�@�C���̃��X�g
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
     * �w�肳�ꂽ�f�B���N�g�����X�g�̊e�f�B���N�g���z���̃T�u�f�B���N�g�����܂߂��S�Ẵt�@�C�����A�w�肳�ꂽ�t�B���^�Ńt�B���^�����O�������ʂ��擾����B<p>
     *
     * @param dirList �f�B���N�g���̃��X�g
     * @param filter �t�B���^�z��
     * @param searchType �������
     * @return �t�@�C���p�X�z��
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
     * ���̃f�B���N�g���z���ŁA�w�肳�ꂽ���K�\���Ɉ�v����t�@�C���p�X�z����擾����B<p>
     *
     * @param regexPath �p�X�̐��K�\��
     * @return �t�@�C���p�X�z��
     * @see #listAllTreeFiles(String, int)
     */
    public String[] listAllTree(String regexPath) {
        return listAllTree(regexPath, SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃f�B���N�g���z���ŁA�w�肳�ꂽ���K�\���Ɉ�v����t�@�C���p�X�z����擾����B<p>
     *
     * @param regexPath �p�X�̐��K�\��
     * @param searchType �������
     * @return �t�@�C���p�X�z��
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
     * ���̃f�B���N�g���z���ŁA�w�肳�ꂽ���K�\���Ɉ�v����t�@�C���z����擾����B<p>
     *
     * @param regexPath �p�X�̐��K�\��
     * @return �t�@�C���z��
     * @see #listAllTreeFiles(String, int)
     * @see #SEARCH_TYPE_FILE
     * @see #SEARCH_TYPE_DIR
     * @see #SEARCH_TYPE_ALL
     */
    public File[] listAllTreeFiles(String regexPath) {
        return listAllTreeFiles(regexPath, SEARCH_TYPE_FILE);
    }
    
    /**
     * ���̃f�B���N�g���z���ŁA�w�肳�ꂽ���K�\���Ɉ�v����t�@�C���z����擾����B<p>
     * �p�X�̐��K�\���ɂ́A�ʏ�̐��K�\���ɉ�����"**"�Ƃ����w�肪�\�ł���B<br>
     * "**"�Ǝw�肳�ꂽ�ꍇ�A�r���̑S�Ẵf�B���N�g���\�����܂ގ��������B<br>
     * ���������Ƃ��āA���K�\���̃G�X�P�[�v�����ł���"\"�́AWindows OS�̃p�X�Z�p���[�^�ɂ��Ȃ��Ă��邽�߁A���K�\���Ƃ���"\"���w�肵�����ꍇ�́A"\\"�Ǝw�肷�鎖�B<br>
     *
     * @param regexPath �p�X�̐��K�\��
     * @param searchType �������
     * @return �t�@�C���z��
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
     * ���̃t�@�C���ȉ����ċA�I�ɍ폜����B<p>
     *
     * @return �S�č폜�ł����ꍇtrue
     */
    public boolean deleteAllTree() {
        return deleteAllTree(this);
    }
    
    /**
     * ���̃t�@�C���ȉ����ċA�I�ɍ폜����B<p>
     *
     * @param containsOwn ������file���g�������ꍇtrue
     * @return �S�č폜�ł����ꍇtrue
     */
    public boolean deleteAllTree(boolean containsOwn) {
        return deleteAllTree(this, containsOwn);
    }
    
    /**
     * �w�肳�ꂽ�t�@�C���ȉ����ċA�I�ɍ폜����B<p>
     *
     * @param file �폜����t�@�C��
     * @return �S�č폜�ł����ꍇtrue
     */
    public static boolean deleteAllTree(File file) {
        return deleteAllTree(file, true);
    }
    
    /**
     * �w�肳�ꂽ�t�@�C���ȉ����ċA�I�ɍ폜����B<p>
     *
     * @param file �폜����t�@�C��
     * @param containsOwn ������file���g�������ꍇtrue
     * @return �S�č폜�ł����ꍇtrue
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
     * ���̃t�@�C���ȉ����w�肵���t�@�C���ɍċA�I�ɃR�s�[����B<p>
     *
     * @return �S�č쐬�ł����ꍇtrue
     * @throws Exception
     */
    public boolean copyAllTree(File file) throws IOException {
        return copyAllTree(this, file, null);
    }
    
    /**
     * ���̃t�@�C���ȉ����w�肵���t�@�C���ɍċA�I�ɃR�s�[����B<p>
     *
     * @return �S�č쐬�ł����ꍇtrue
     * @throws Exception
     */
    public boolean copyAllTree(File file, FilenameFilter filter) throws IOException {
        return copyAllTree(this, file, filter);
    }

    /**
     * �w�肳�ꂽ�t�@�C���ȉ����ċA�I�ɃR�s�[����B<p>
     *
     * @param fromFile�@�R�s�[���t�@�C��
     * @param toFile�@�R�s�[��t�@�C��
     * @return �S�č폜�ł����ꍇtrue
     * @throws Exception
     */
    public static boolean copyAllTree(File fromFile, File toFile) throws IOException {
        return copyAllTree(fromFile, toFile, null);
    }
    /**
     * �w�肳�ꂽ�t�@�C���ȉ����ċA�I�ɃR�s�[����B<p>
     *
     * @param fromFile�@�R�s�[���t�@�C��
     * @param toFile�@�R�s�[��t�@�C��
     * @return �S�č폜�ł����ꍇtrue
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
     * ���̃t�@�C���ȉ����ċA�I�ɉ��z�}�V�����I�������Ƃ��ɍ폜�����悤�ɗv������B<p>
     */
    public void deleteOnExitAllTree() {
        deleteOnExitAllTree(this);
    }
    
    /**
     * �w�肳�ꂽ�t�@�C���ȉ����ċA�I�ɉ��z�}�V�����I�������Ƃ��ɍ폜�����悤�ɗv������B<p>
     *
     * @param file �t�@�C��
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
