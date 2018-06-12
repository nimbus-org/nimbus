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
package jp.ossc.nimbus.service.test.action;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import jp.ossc.nimbus.beans.PropertyFactory;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.io.RegexFileFilter;
import jp.ossc.nimbus.service.test.ChainTestAction;
import jp.ossc.nimbus.service.test.TestAction;
import jp.ossc.nimbus.service.test.TestActionEstimation;
import jp.ossc.nimbus.service.test.TestContext;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

/**
 * ファイルを圧縮/解凍するアクション。<p>
 * 動作の詳細は、{@link #execute(TestContext, String, Reader)}を参照。<br>
 * 
 * @author M.Ishida
 */
public class FileCompressActionService extends ServiceBase implements TestAction, ChainTestAction.TestActionProcess, TestActionEstimation, FileCompressActionServiceMBean{
    
    private static final long serialVersionUID = 7682175982096275432L;
    protected double expectedCost = Double.NaN;
    
    protected String zipFileExtension = DEFAULT_ZIP_FILE_EXTENTION;
    protected String gzFileExtension = DEFAULT_GZ_FILE_EXTENTION;
    protected String lz4FileExtension = DEFAULT_LZ4_FILE_EXTENTION;
    protected String snappyFileExtension = DEFAULT_SNAPPY_FILE_EXTENTION;
    
    public String getZipFileExtension() {
        return zipFileExtension;
    }

    public void setZipFileExtension(String extension) {
        zipFileExtension = extension;
    }

    public String getGzFileExtension() {
        return gzFileExtension;
    }

    public void setGzFileExtension(String extension) {
        gzFileExtension = extension;
    }

    public String getLz4FileExtension() {
        return lz4FileExtension;
    }

    public void setLz4FileExtension(String extension) {
        lz4FileExtension = extension;
    }

    public String getSnappyFileExtension() {
        return snappyFileExtension;
    }

    public void setSnappyFileExtension(String extension) {
        snappyFileExtension = extension;
    }

    /**
     * リソースの内容を読み込んで、対象ファイルを圧縮/解凍する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * algorithm
     * mode
     * fromFile
     * toFile
     * </pre>
     * algorithmは、圧縮解凍時に使用するアルゴリズムを指定する。<br>
     * modeは、EXTRACT（解凍）かARCHIVE（圧縮）を指定する。<br>
     * fromFileは、圧縮もしくは解凍する対象ファイルを指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param resource リソース
     * @return 取得したプロパティ値
     */
    public Object execute(TestContext context, String actionId, Reader resource) throws Exception{
        return execute(context, actionId, null, resource);
    }
    
    /**
     * リソースの内容を読み込んで、対象ファイルを圧縮/解凍する。<p>
     * リソースのフォーマットは、以下。<br>
     * <pre>
     * algorithm
     * mode
     * fromFilePath
     * </pre>
     * propertyは、取得するプロパティ文字列を指定する。プロパティ文字列は、{@link PropertyFactory#createProperty(String)}で解釈される。<br>
     * targetObjectIdは、プロパティの取得対象となるオブジェクトを指定するもので、同一テストケース中に、このTestActionより前に、プロパティの取得対象となるオブジェクトを戻すテストアクションが存在する場合は、そのアクションIDを指定する。また、同一シナリオ中に、このTestActionより前に、プロパティの取得対象となるオブジェクトを戻すテストアクションが存在する場合は、テストケースIDとアクションIDをカンマ区切りで指定する。preResultを使用する場合は、空行を指定する。<br>
     *
     * @param context コンテキスト
     * @param actionId アクションID
     * @param preResult プロパティの取得対象となるオブジェクト
     * @param resource リソース
     * @return 圧縮解凍後のファイルリスト
     */
    public Object execute(TestContext context, String actionId, Object preResult, Reader resource) throws Exception{
        BufferedReader br = new BufferedReader(resource);
        File[] fromFiles = null;
        if(preResult instanceof List) {
            fromFiles = (File[])((List)preResult).toArray(new File[0]);
        } else if(preResult instanceof File[]) {
            fromFiles = (File[])preResult;
        } else if(preResult instanceof File) {
            fromFiles = new File[]{(File)preResult};
        }
        List resultFileList = null;
        try{
            final String algorithm = br.readLine();
            if(algorithm == null) {
                throw new Exception("Unexpected EOF on algorithm");
            }
            if(!ZIP.equals(algorithm) && !GZ.equals(algorithm) && !LZ4.equals(algorithm) && !SNAPPY.equals(algorithm)) {
                throw new Exception("Illegal algorithm : " + algorithm);
            }
            final String mode = br.readLine();
            if(mode == null) {
                throw new Exception("Unexpected EOF on mode");
            } else if(!EXTRACT.equals(mode) && !ARCHIVE.equals(mode)) {
                throw new Exception("Illegal mode : " + mode);
            }
            if(fromFiles == null) {
                final String fromFilePath = br.readLine();
                if(fromFilePath == null) {
                    throw new Exception("Unexpected EOF on fromFilePath");
                }
                File file = new File(fromFilePath);
                if(!file.isAbsolute()) {
                    file = new File(context.getCurrentDirectory(), fromFilePath);
                }
                fromFiles = file.getParentFile().listFiles(new RegexFileFilter(file.getName()));
            }
            if(fromFiles == null) {
                return null;
            }
            for(int i = 0; i < fromFiles.length; i++) {
                File toFile = null;
                if(resultFileList == null) {
                    resultFileList = new ArrayList();
                }
                if(ZIP.equals(algorithm)) {
                    if(EXTRACT.equals(mode)) {
                        if(!fromFiles[i].isFile()) {
                            throw new Exception("FromFile is not file. filePath=" + fromFiles[i].getAbsolutePath());
                        }
                        String toFileName = fromFiles[i].getName();
                        toFile = new File(context.getCurrentDirectory(), toFileName.substring(0, toFileName.lastIndexOf(zipFileExtension)));
                        unZip(fromFiles[i], toFile);
                    } else if(ARCHIVE.equals(mode)) {
                        toFile = new File(context.getCurrentDirectory() , fromFiles[i].getName() + zipFileExtension);
                        doZip(fromFiles[i], toFile);
                    }
                } else {
                    String extension = null;
                    if(GZ.equals(algorithm)) {
                        extension = gzFileExtension;
                    } else if(LZ4.equals(algorithm)) {
                        extension =lz4FileExtension;
                    } else if(SNAPPY.equals(algorithm)) {
                        extension = snappyFileExtension;
                    }
                    if(EXTRACT.equals(mode)) {
                        if(!fromFiles[i].isFile()) {
                            throw new Exception("FromFile is not file. filePath=" + fromFiles[i].getAbsolutePath());
                        }
                        String toFileName = fromFiles[i].getName();
                        toFile = new File(context.getCurrentDirectory(), toFileName.substring(0, toFileName.lastIndexOf(extension)));
                        InputStream is = null;
                        OutputStream os = null;
                        try {
                            if(GZ.equals(algorithm)) {
                                is = new GZIPInputStream(new FileInputStream(fromFiles[i]));
                            } else if(LZ4.equals(algorithm)) {
                                is = new LZ4BlockInputStream(new FileInputStream(fromFiles[i]));
                            } else if(SNAPPY.equals(algorithm)) {
                                is = new SnappyInputStream(new FileInputStream(fromFiles[i]));
                            }
                            os = new FileOutputStream(toFile);
                            transfer(is, os);
                        } finally {
                            if(os != null) {
                                try {
                                    os.close();
                                    os = null;
                                } catch(IOException e) {}
                            }
                            if(is != null) {
                                try {
                                    is.close();
                                    is = null;
                                } catch(IOException e) {}
                            }
                        }
                    } else if(ARCHIVE.equals(mode)) {
                        if(!fromFiles[i].isFile()) {
                            throw new Exception("FromFile is not file. filePath=" + fromFiles[i].getAbsolutePath());
                        }
                        toFile = new File(context.getCurrentDirectory() , fromFiles[i].getName() + extension);
                        InputStream is = null;
                        OutputStream os = null;
                        try {
                            if(GZ.equals(algorithm)) {
                                os = new GZIPOutputStream(new FileOutputStream(toFile));
                            } else if(LZ4.equals(algorithm)) {
                                os = new LZ4BlockOutputStream(new FileOutputStream(toFile));
                            } else if(SNAPPY.equals(algorithm)) {
                                os = new SnappyOutputStream(new FileOutputStream(toFile));
                            }
                            is = new FileInputStream(fromFiles[i]);
                            transfer(is, os);
                        } finally {
                            if(os != null) {
                                try {
                                    os.close();
                                    os = null;
                                } catch(IOException e) {}
                            }
                            if(is != null) {
                                try {
                                    is.close();
                                    is = null;
                                } catch(IOException e) {}
                            }
                        }
                    }
                }
                resultFileList.add(toFile);
            }
        }finally{
            br.close();
            br = null;
        }
        return resultFileList;
    }
    
    private void doZip(File targetDir, File zipFile) throws Exception {
        File[] files = { targetDir };
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            doZip(zos, files, targetDir.getParent());
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (Exception e) {
                }
            }
        }
    }
    
    private void doZip(ZipOutputStream zos, File[] files, String baseDir) throws Exception {
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                if ( baseDir.equals(f.getParent())) {
                    doZip(zos, f.listFiles(), baseDir);
                }
            } else {
                String path = f.getPath().substring(f.getPath().indexOf(baseDir) + baseDir.length() + 1);
                ZipEntry entry = new ZipEntry(path.replace('\\', '/'));
                zos.putNextEntry(entry);
                InputStream is = null;
                try {
                    is = new BufferedInputStream(new FileInputStream(f));
                    transfer(is, zos);
                } finally {
                    is.close();
                }
            }
        }
    }

    private void unZip(File zipFile, File baseDir) throws IOException {
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    new File(baseDir, entry.getName()).mkdirs();
                } else {
                    File file = new File(baseDir, entry.getName());
                    File parent = file.getParentFile();
                    if (parent != null) {
                        parent.mkdirs();
                    }
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        transfer(zis, out);
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                zis.closeEntry();
            }
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private void transfer(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[1024];
        int size = 0;
        while ((size = is.read(buf)) != -1) {
            os.write(buf, 0, size);
        }
        os.flush();
    }
    
    public void setExpectedCost(double cost) {
        expectedCost = cost;
    }
    
    public double getExpectedCost() {
        return expectedCost;
    }
}
