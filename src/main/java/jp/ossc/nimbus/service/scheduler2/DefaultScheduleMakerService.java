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

import java.util.*;

import jp.ossc.nimbus.core.*;


/**
 * デフォルトスケジュール作成サービス。<p>
 *
 * @author M.Takata
 */
public class DefaultScheduleMakerService extends ServiceBase
 implements ScheduleMaker, DefaultScheduleMakerServiceMBean{
    
    private static final long serialVersionUID = -8603198587673383956L;
    
    /**
     * 指定されたスケジュールマスタからスケジュールを作成する。<p>
     * スケジュールマスタの正当性をチェックし、スケジュールマスタに指定された日付を適用し、スケジュールを作成する。<br>
     * スケジュールの作成では、繰り返しのスケジュールマスタの場合は、指定された繰り返し間隔でスケジュール時刻をずらしたスケジュールを、指定された終了時刻まで作成する。<br>
     * 繰り返しのスケジュールマスタでない場合は、１つだけスケジュールを作成する。<br>
     * {@link Schedule}の実装クラスとして{@link DefaultSchedule}を使用する。<br>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return スケジュールの配列
     * @exception ScheduleMakeException スケジュールの作成に失敗した場合
     */
    public Schedule[] makeSchedule(Date date, ScheduleMaster master)
     throws ScheduleMakeException{
        
        if(!master.isEnabled() || master.isTemplate()){
            return new Schedule[0];
        }
        
        if(!isNecessaryMake(date, master)){
            return new Schedule[0];
        }
        
        master.applyDate(date);
        
        checkScheduleMaster(master);
        
        if(master.getEndTime() == null
             || master.getStartTime().equals(master.getEndTime())
             || (master.getRepeatInterval() <= 0
                  && master.getRetryInterval() > 0)
        ){
            return new Schedule[]{
                makeSingleSchedule(master)
            };
        }else{
            return makeRepeatSchedule(master);
        }
    }
     
    public boolean isMakeSchedule(Date date, ScheduleMaster master)
     throws ScheduleMakeException{
        return isNecessaryMake(date, master);
    }
    
    /**
     * スケジュールマスタの正当性をチェックする。<p>
     * <ul>
     *   <li>マスタIDの必須チェック。</li>
     *   <li>タスク名の必須チェック。</li>
     *   <li>開始時刻の必須チェック。</li>
     *   <li>終了時刻が指定されている場合に、開始時刻&lt;=終了時刻となっているかのチェック。</li>
     *   <li>終了時刻が指定されていて、開始時刻&lt;終了時刻となっている場合に、繰り返し間隔&gt;0となっているかのチェック。</li>
     *   <li>リトライ終了時刻が指定されている場合に、開始時刻&lt;=リトライ終了時刻となっているかのチェック。</li>
     *   <li>リトライ間隔&gt;0の場合に、リトライ終了時刻が指定されているかのチェック。</li>
     *   <li>リトライ終了時刻が指定されている場合に、リトライ間隔&lt;=0であるかのチェック。</li>
     *   <li>リトライ終了時刻が指定されていてリトライ間隔&gt;0の場合に、最初のリトライ時刻&lt;=リトライ終了時刻となっているかのチェック。</li>
     * </ul>
     *
     * @param master スケジュールマスタ
     * @exception IllegalScheduleMasterException スケジュールマスタが正しくない場合
     */
    protected void checkScheduleMaster(ScheduleMaster master)
     throws IllegalScheduleMasterException{
        
        if(master.getId() == null){
            throw new IllegalScheduleMasterException("Id is null.");
        }
        if(master.getTaskName() == null){
            throw new IllegalScheduleMasterException("TaskName is null. id=" + master.getId());
        }
        if(master.getStartTime() == null){
            throw new IllegalScheduleMasterException("StartTime is null. id=" + master.getId());
        }
        if(master.getEndTime() != null
            && master.getEndTime().before(master.getStartTime())){
            throw new IllegalScheduleMasterException("EndTime is before StartTime. id=" + master.getId());
        }
        if((master.getEndTime() != null
            && master.getEndTime().after(master.getStartTime()))
            && master.getRepeatInterval() <= 0){
            throw new IllegalScheduleMasterException("RepeatInterval <= 0. id=" + master.getId());
        }
        if(master.getRetryEndTime() != null
            && !master.getRetryEndTime().after(master.getStartTime())){
            throw new IllegalScheduleMasterException("RetryEndTime is before StartTime. id=" + master.getId());
        }
        if(master.getRetryInterval() <= 0 && master.getRetryEndTime() != null){
            throw new IllegalScheduleMasterException("RetryInterval <= 0. id=" + master.getId());
        }
        if(master.getRetryInterval() > 0 && master.getRetryEndTime() != null){
            final Calendar offset = Calendar.getInstance();
            offset.setTime(master.getStartTime());
            final Calendar end = Calendar.getInstance();
            end.setTime(master.getRetryEndTime());
            final Date firstRetryTime = calculateNextDate(
                offset,
                master.getRetryInterval(),
                end
            );
            if(firstRetryTime == null){
                throw new IllegalScheduleMasterException("First RetryTime is after RetryEndTime. id=" + master.getId());
            }
        }
    }
    
    /**
     * この日付で、スケジュールを作成する必要があるかどうかを判定する。<p>
     * この実装では、必ずtrueを返す。<br>
     * 日付に対して、スケジュールの作成有無を判断する必要がある場合は、サブクラスでオーバーライドする事。<br>
     *
     * @param date 作成日
     * @param master スケジュールマスタ
     * @return trueの場合、作る必要がある
     * @exception ScheduleMakeException 判定に失敗した場合
     */
    protected boolean isNecessaryMake(Date date, ScheduleMaster master)
     throws ScheduleMakeException{
        return true;
    }
    
    /**
     * 指定されたスケジュールマスタから、繰り返さないスケジュールを作成する。<p>
     *
     * @param master スケジュールマスタ
     * @return スケジュール
     */
    protected Schedule makeSingleSchedule(ScheduleMaster master)
     throws ScheduleMakeException{
        return new DefaultSchedule(
            master.getId(),
            master.getGroupIds(),
            master.getStartTime(),
            master.getTaskName(),
            master.getInput(),
            master.getDepends(),
            master.getDependsInGroupMap(),
            master.getDependsOnGroup(),
            master.getGroupDependsOnGroupMap(),
            master.getExecutorKey(),
            master.getExecutorType(),
            master.getRetryInterval(),
            master.getRetryEndTime(),
            master.getMaxDelayTime()
        );
    }
    
    /**
     * 指定されたスケジュールマスタから、繰り返しスケジュールを作成する。<p>
     *
     * @param master スケジュールマスタ
     * @return スケジュール配列
     */
    protected Schedule[] makeRepeatSchedule(ScheduleMaster master)
     throws ScheduleMakeException{
        final List result = new ArrayList();
        Date time = master.getStartTime();
        final Calendar offset = Calendar.getInstance();
        offset.setTime(master.getStartTime());
        final Calendar end = Calendar.getInstance();
        end.setTime(master.getEndTime());
        do{
            result.add(
                new DefaultSchedule(
                    master.getId(),
                    master.getGroupIds(),
                    time,
                    master.getTaskName(),
                    master.getInput(),
                    master.getDepends(),
                    master.getDependsInGroupMap(),
                    master.getDependsOnGroup(),
                    master.getGroupDependsOnGroupMap(),
                    master.getExecutorKey(),
                    master.getExecutorType(),
                    master.getRetryInterval(),
                    master.getRetryEndTime(),
                    master.getMaxDelayTime()
                )
            );
        }while((time = calculateNextDate(offset, master.getRepeatInterval(), end)) != null);
        return (Schedule[])result.toArray(new Schedule[result.size()]);
    }
    
    /**
     * 繰り返しスケジュールの次のスケジュール時刻を計算する。<p>
     * 次のスケジュール時刻が終了時刻を過ぎた場合には、nullを返す。<br>
     *
     * @param offset 現在のスケジュール時刻
     * @param interval 繰り返し間隔[ms]
     * @param end スケジュールの終了時刻
     * @return 次のスケジュール時刻
     */
    protected Date calculateNextDate(
        Calendar offset,
        long interval,
        Calendar end
    ){
        if(interval > Integer.MAX_VALUE){
            long offsetInterval = interval;
            int tmpInterval = 0;
            do{
                if(offsetInterval >= Integer.MAX_VALUE){
                    tmpInterval = Integer.MAX_VALUE;
                }else{
                    tmpInterval = (int)offsetInterval;
                }
                offset.add(Calendar.MILLISECOND, tmpInterval);
                offsetInterval -= Integer.MAX_VALUE;
            }while(offsetInterval > 0);
        }else{
            offset.add(Calendar.MILLISECOND, (int)interval);
        }
        if(offset.after(end)){
            return null;
        }else{
            return offset.getTime();
        }
    }
}