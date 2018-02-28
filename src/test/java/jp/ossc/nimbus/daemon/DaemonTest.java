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
package jp.ossc.nimbus.daemon;

import junit.framework.TestCase;

//
/**
 * 
 * @author   NRI. Hirotaka Nakano
 * @version  1.00 作成: 2003/09/30 -　H.Nakano
 */
public class DaemonTest extends TestCase {

	/**
	 * Constructor for DaemonTest.
	 * @param arg0
	 */
	public DaemonTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DaemonTest.class);
	}


	public void testSuspend() throws InterruptedException {
		TestRunnable r = new TestRunnable() ;
		Daemon d = new Daemon(r) ;
		d.start() ;
		Thread.sleep(5000);
		System.out.println("１回suspend") ;
		d.suspend() ;
		Thread.sleep(5000);
		System.out.println("１回resume") ;
		d.resume() ;
		Thread.sleep(5000);
		System.out.println("１回とめるよ") ;
		d.stop() ;
		System.out.println("2回うごかすよ") ;
		d.start() ;
		Thread.sleep(5000);
		System.out.println("１回suspend") ;
		d.suspend() ;
		Thread.sleep(5000);
		System.out.println("２回とめるよ") ;
		d.stop() ;		
	}
	public void testStop() {
		TestRunnable1 r = new TestRunnable1() ;
		Daemon d = new Daemon(r) ;
		d.start() ;
		d.stopWait() ;
		d.start() ;
		d.stopWait() ;
		d.start() ;
		d.stop() ;
	}
}
