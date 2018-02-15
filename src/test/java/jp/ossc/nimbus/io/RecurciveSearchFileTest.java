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
import java.net.*;
import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/09/30 -　H.Nakano
 */
public class RecurciveSearchFileTest extends TestCase {
    private static final File rootDir = new File("target/temp/jp/ossc/nimbus/io/root");
    private static final File subDir1 = new File(rootDir, "sub1");
    private static final File subDir2 = new File(rootDir, "sub2");
    private static final File subSubDir1 = new File(subDir2, "subsub1");
    private static final File subSubDir2 = new File(subDir2, "subsub2");
    private static final File file1 = new File(rootDir, "file1");
    private static final File file2 = new File(subDir1, "file2");
    private static final File file3 = new File(subDir2, "file3");
    private static final File file4 = new File(subSubDir1, "file4");
    
    /**
     * Constructor for RecurciveSearchFileTest.
     * @param arg0
     */
    public RecurciveSearchFileTest(String arg0) {
        super(arg0);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(RecurciveSearchFileTest.class);
    }
    
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite(RecurciveSearchFileTest.class);
        TestSetup wrapper = new TestSetup(suite){
            public void setUp() throws Exception{
                oneTimeSetup();
            }
            public void tearDown() throws Exception{
                oneTimeTeardown();
            }
        };
        return wrapper;
    }
    
    static void oneTimeSetup() throws Exception {
        rootDir.mkdir();
        subDir1.mkdir();
        subDir2.mkdir();
        subSubDir1.mkdir();
        subSubDir2.mkdir();
        file1.createNewFile();
        file2.createNewFile();
        file3.createNewFile();
        file4.createNewFile();
    }
    
    static void oneTimeTeardown() throws Exception {
        file1.delete();
        file2.delete();
        file3.delete();
        file4.delete();
        subSubDir2.delete();
        subSubDir1.delete();
        subDir2.delete();
        subDir1.delete();
        rootDir.delete();
    }
    
    public void testListAllTree1() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree();
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(file1.getAbsolutePath()));
        assertTrue(pathList.contains(file2.getAbsolutePath()));
        assertTrue(pathList.contains(file3.getAbsolutePath()));
        assertTrue(pathList.contains(file4.getAbsolutePath()));
    }
    
    public void testListAllTree2() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir,
            subDir1.getName()
        );
        String[] paths = recFile.listAllTree();
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file2.getAbsolutePath(), paths[0]);
    }
    
    public void testListAllTree3() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            subDir2.getPath(),
            subSubDir2.getName()
        );
        String[] paths = recFile.listAllTree();
        assertNotNull(paths);
        assertEquals(0, paths.length);
    }
    
    public void testListAllTree4() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            file1.getPath()
        );
        String[] paths = recFile.listAllTree();
        assertNull(paths);
    }
    
    public void testListAllTree5() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            RecurciveSearchFile.SEARCH_TYPE_DIR
        );
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subDir2.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir2.getAbsolutePath()));
    }
    
    public void testListAllTree6() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(8, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subDir2.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir2.getAbsolutePath()));
        assertTrue(pathList.contains(file1.getAbsolutePath()));
        assertTrue(pathList.contains(file2.getAbsolutePath()));
        assertTrue(pathList.contains(file3.getAbsolutePath()));
        assertTrue(pathList.contains(file4.getAbsolutePath()));
    }
    
    public void testListAllTree7() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            new FilenameFilter(){
                public boolean accept(File dir, String name){
                    return name.endsWith("1");
                }
            }
        );
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file1.getAbsolutePath(), paths[0]);
    }
    
    public void testListAllTree8() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            new FilenameFilter(){
                public boolean accept(File dir, String name){
                    return name.endsWith("1");
                }
            },
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(3, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir1.getAbsolutePath()));
        assertTrue(pathList.contains(file1.getAbsolutePath()));
    }
    
    public void testListAllTree9() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            new FilenameFilter[]{
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.startsWith("f");
                    }
                },
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.endsWith("1");
                    }
                }
            }
        );
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file1.getAbsolutePath(), paths[0]);
    }
    
    public void testListAllTree10() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            new FilenameFilter[]{
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.startsWith("sub");
                    }
                },
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.endsWith("1");
                    }
                }
            },
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(2, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir1.getAbsolutePath()));
    }
    
    public void testListAllTree11() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(".*");
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file1.getAbsolutePath(), paths[0]);
    }
    
    public void testListAllTree12() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree("**/.*");
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(file1.getAbsolutePath()));
        assertTrue(pathList.contains(file2.getAbsolutePath()));
        assertTrue(pathList.contains(file3.getAbsolutePath()));
        assertTrue(pathList.contains(file4.getAbsolutePath()));
    }
    
    public void testListAllTree13() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree("**/sub1/.*");
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file2.getAbsolutePath(), paths[0]);
    }
    
    public void testListAllTree14() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree("**/sub2/**/.*");
        assertNotNull(paths);
        assertEquals(2, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(file3.getAbsolutePath()));
        assertTrue(pathList.contains(file4.getAbsolutePath()));
    }
    
    public void testListAllTree15() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree("**/.*sub1/.*");
        assertNotNull(paths);
        assertEquals(2, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(file2.getAbsolutePath()));
        assertTrue(pathList.contains(file4.getAbsolutePath()));
    }
    
    public void testListAllTree16() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree("**/.*sub3/.*");
        assertNotNull(paths);
        assertEquals(0, paths.length);
    }
    
    public void testListAllTree17() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            "**/.*",
            RecurciveSearchFile.SEARCH_TYPE_DIR
        );
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subDir2.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir2.getAbsolutePath()));
    }
    
    public void testListAllTree18() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        String[] paths = recFile.listAllTree(
            "**/.*",
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(8, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subDir2.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir1.getAbsolutePath()));
        assertTrue(pathList.contains(subSubDir2.getAbsolutePath()));
        assertTrue(pathList.contains(file1.getAbsolutePath()));
        assertTrue(pathList.contains(file2.getAbsolutePath()));
        assertTrue(pathList.contains(file3.getAbsolutePath()));
        assertTrue(pathList.contains(file4.getAbsolutePath()));
    }
    
    public void testListAllTreeFiles1() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles();
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(file1));
        assertTrue(pathList.contains(file2));
        assertTrue(pathList.contains(file3));
        assertTrue(pathList.contains(file4));
    }
    
    public void testListAllTreeFiles2() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir,
            subDir1.getName()
        );
        File[] paths = recFile.listAllTreeFiles();
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file2, paths[0]);
    }
    
    public void testListAllTreeFiles3() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            subDir2.getPath(),
            subSubDir2.getName()
        );
        File[] paths = recFile.listAllTreeFiles();
        assertNotNull(paths);
        assertEquals(0, paths.length);
    }
    
    public void testListAllTreeFiles4() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            file1.getPath()
        );
        File[] paths = recFile.listAllTreeFiles();
        assertNull(paths);
    }
    
    public void testListAllTreeFiles5() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            RecurciveSearchFile.SEARCH_TYPE_DIR
        );
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1));
        assertTrue(pathList.contains(subDir2));
        assertTrue(pathList.contains(subSubDir1));
        assertTrue(pathList.contains(subSubDir2));
    }
    
    public void testListAllTreeFiles6() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(8, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1));
        assertTrue(pathList.contains(subDir2));
        assertTrue(pathList.contains(subSubDir1));
        assertTrue(pathList.contains(subSubDir2));
        assertTrue(pathList.contains(file1));
        assertTrue(pathList.contains(file2));
        assertTrue(pathList.contains(file3));
        assertTrue(pathList.contains(file4));
    }
    
    public void testListAllTreeFiles7() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            new FilenameFilter(){
                public boolean accept(File dir, String name){
                    return name.endsWith("1");
                }
            }
        );
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file1, paths[0]);
    }
    
    public void testListAllTreeFiles8() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            new FilenameFilter(){
                public boolean accept(File dir, String name){
                    return name.endsWith("1");
                }
            },
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(3, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1));
        assertTrue(pathList.contains(subSubDir1));
        assertTrue(pathList.contains(file1));
    }
    
    public void testListAllTreeFiles9() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            new FilenameFilter[]{
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.startsWith("f");
                    }
                },
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.endsWith("1");
                    }
                }
            }
        );
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file1, paths[0]);
    }
    
    public void testListAllTreeFiles10() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            new FilenameFilter[]{
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.startsWith("sub");
                    }
                },
                new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.endsWith("1");
                    }
                }
            },
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(2, paths.length);
        List pathList = Arrays.asList(paths);
        assertTrue(pathList.contains(subDir1));
        assertTrue(pathList.contains(subSubDir1));
    }
    
    public void testListAllTreeFiles11() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(".*");
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file1, paths[0]);
    }
    
    public void testListAllTreeFiles12() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles("**/.*");
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            pathList.add(paths[i]);
        }
        assertTrue(pathList.contains(file1));
        assertTrue(pathList.contains(file2));
        assertTrue(pathList.contains(file3));
        assertTrue(pathList.contains(file4));
    }
    
    public void testListAllTreeFiles13() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles("**/sub1/.*");
        assertNotNull(paths);
        assertEquals(1, paths.length);
        assertEquals(file2, paths[0]);
    }
    
    public void testListAllTreeFiles14() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles("**/sub2/**/.*");
        assertNotNull(paths);
        assertEquals(2, paths.length);
        List pathList = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            pathList.add(paths[i]);
        }
        assertTrue(pathList.contains(file3));
        assertTrue(pathList.contains(file4));
    }
    
    public void testListAllTreeFiles15() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles("**/.*sub1/.*");
        assertNotNull(paths);
        assertEquals(2, paths.length);
        List pathList = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            pathList.add(paths[i]);
        }
        assertTrue(pathList.contains(file2));
        assertTrue(pathList.contains(file4));
    }
    
    public void testListAllTreeFiles16() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles("**/.*sub3/.*");
        assertNotNull(paths);
        assertEquals(0, paths.length);
    }
    
    public void testListAllTreeFiles17() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            "**/.*",
            RecurciveSearchFile.SEARCH_TYPE_DIR
        );
        assertNotNull(paths);
        assertEquals(4, paths.length);
        List pathList = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            pathList.add(paths[i]);
        }
        assertTrue(pathList.contains(subDir1));
        assertTrue(pathList.contains(subDir2));
        assertTrue(pathList.contains(subSubDir1));
        assertTrue(pathList.contains(subSubDir2));
    }
    
    public void testListAllTreeFiles18() throws Exception{
        final RecurciveSearchFile recFile = new RecurciveSearchFile(
            rootDir.getPath()
        );
        File[] paths = recFile.listAllTreeFiles(
            "**/.*",
            RecurciveSearchFile.SEARCH_TYPE_ALL
        );
        assertNotNull(paths);
        assertEquals(8, paths.length);
        List pathList = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            pathList.add(paths[i]);
        }
        assertTrue(pathList.contains(subDir1));
        assertTrue(pathList.contains(subDir2));
        assertTrue(pathList.contains(subSubDir1));
        assertTrue(pathList.contains(subSubDir2));
        assertTrue(pathList.contains(file1));
        assertTrue(pathList.contains(file2));
        assertTrue(pathList.contains(file3));
        assertTrue(pathList.contains(file4));
    }
}
