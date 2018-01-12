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
package jp.ossc.nimbus.service.scheduler2;

import java.util.Date;

/**
 * スケジュール作成。<p>
 * スケジュールマスタから指定された日付のスケジュールを作成する。<p>
 * スケジュールマスタは、日付の概念を持たないため、指定された日付では必要のないスケジュールである場合がある。<br>
 * また、スケジュールマスタは、繰り返しの概念を持てるため、スケジュールマスタとスケジュールの関係は、1:nになる。<br>
 *
 * @author M.Takata
 */
public interface ScheduleMaker{
    
    /**
     * 指定されたスケジュールマスタからスケジュールを作成する。<p>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return スケジュールの配列
     * @exception ScheduleMakeException スケジュールの作成に失敗した場合
     */
    public Schedule[] makeSchedule(Date date, ScheduleMaster master)
     throws ScheduleMakeException;
     
    /**
     * 指定されたス日付に指定されたスケジュールマスタからスケジュールを作成するかどうかを判定する。<p>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return スケジュールを作成する場合true
     * @exception ScheduleMakeException スケジュールの作成判定に失敗した場合
     */
    public boolean isMakeSchedule(Date date, ScheduleMaster master)
     throws ScheduleMakeException;
}