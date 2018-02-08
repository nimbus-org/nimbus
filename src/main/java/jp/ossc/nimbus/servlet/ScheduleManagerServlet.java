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
package jp.ossc.nimbus.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ossc.nimbus.beans.ServiceNameEditor;
import jp.ossc.nimbus.beans.StringArrayEditor;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.io.CSVReader;
import jp.ossc.nimbus.service.scheduler2.DefaultSchedule;
import jp.ossc.nimbus.service.scheduler2.Schedule;
import jp.ossc.nimbus.service.scheduler2.ScheduleMaker;
import jp.ossc.nimbus.service.scheduler2.ScheduleManageException;
import jp.ossc.nimbus.service.scheduler2.ScheduleManager;
import jp.ossc.nimbus.service.scheduler2.ScheduleMaster;
import jp.ossc.nimbus.service.scheduler2.Scheduler;
import jp.ossc.nimbus.service.scheduler2.ScheduleDepends;
import jp.ossc.nimbus.service.scheduler2.DefaultScheduleDepends;
import jp.ossc.nimbus.util.converter.BeanJSONConverter;
import jp.ossc.nimbus.util.converter.DateFormatConverter;
import jp.ossc.nimbus.util.converter.StringStreamConverter;

/**
 * �X�P�W���[���Ǘ��T�[�u���b�g�B<p>
 * HTTP�o�R�ł̃X�P�W���[���̊Ǘ����T�|�[�g����Ǘ��R���\�[����񋟂���B<br>
 * ���̃T�[�u���b�g�ɂ́A�ȉ��̏������p�����[�^������B<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th>#</th><th>�p�����[�^��</th><th>�l�̐���</th><th>�f�t�H���g</th></tr>
 *     <tr><td>1</td><td>ScheduleManagerServiceName</td><td>�ΏۂƂ���{@link ScheduleManager �X�P�W���[���Ǘ�}�̃T�[�r�X�����w�肷��B</td><td>�@</td></tr>
 *     <tr><td>2</td><td>SchedulerServiceName</td><td>�ΏۂƂ���{@link Scheduler �X�P�W���[��}�̃T�[�r�X�����w�肷��B</td><td>�@</td></tr>
 *     <tr><td>3</td><td>MakeEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃X�P�W���[���쐬�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>4</td><td>AddEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃X�P�W���[���ǉ������L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>5</td><td>RescheduleEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃X�P�W���[�������ύX�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>6</td><td>RemoveEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃X�P�W���[���폜�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>7</td><td>RemoveAllEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃X�P�W���[���폜�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>8</td><td>ChangeStateEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃X�P�W���[����ԕύX�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>9</td><td>ChangeControlStateEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̐����ԕύX�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>10</td><td>ChangeExecutorKeyEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̎��s�L�[�ύX�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>11</td><td>ChangeRetryEndTimeEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̃��g���C�I�������ύX�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>12</td><td>ChangeMaxDelayTimeEnabled</td><td>{@link ScheduleManager �X�P�W���[���Ǘ�}�ւ̍ő�x�����ԕύX�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>13</td><td>StopEntryEnabled</td><td>{@link Scheduler �X�P�W���[��}�ւ̃X�P�W���[��������~�����L���ɂ��邩�ǂ������w�肷��B</td><td>false</td></tr>
 *     <tr><td>14</td><td>JSONConverterServiceName</td><td>JSON�`���ł̉�����v������ꍇ�Ɏg�p����{@link BeanJSONConverter}�T�[�r�X�̃T�[�r�X�����w�肷��B</td><td>�w�肵�Ȃ��ꍇ�́A�������������B</td></tr>
 *     <tr><td>15</td><td>UnicodeEscape</td><td>JSON�`���ł̉�����v������ꍇ�ɁA�Q�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ������w�肷��B</td><td>true</td></tr>
 * </table>
 * <p>
 * Web�T�[�r�X�́A�N�G���w��ł�GET���N�G�X�g�ɑ΂��āAJSON�Ńf�[�^����������B<br>
 * <table border="1" width="90%">
 *     <tr bgcolor="#cccccc"><th rowspan="2">#</th><th rowspan="2">�A�N�V����</th><th colspan="2">�N�G���p�����[�^</th><th rowspan="2">����JSON�̗�</th></tr>
 *     <tr bgcolor="#cccccc"><th>�p�����[�^��</th><th>�l</th></tr>
 *     <tr><td rowspan="9">1</td><td rowspan="9"><nobr>�X�P�W���[���̌���</nobr></td><td>responseType</td><td>json</td><td rowspan="9">
 *     <code><pre>
 *{
 *    "schedules": [
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": null,
 *            "executeEndTime": "20150616093405684",
 *            "executeStartTime": "20150616093355668",
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group1":"1"},
 *            "id": "1",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group1"],
 *            "masterId": "Schedule1",
 *            "maxDelayTime": 0,
 *            "output": "����I��",
 *            "retry": false,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "state": 4,
 *            "taskName": "Flow1",
 *            "time": "20150616080000000"
 *        },
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": [{"masterId":"Schedule1","ignoreError":false}],
 *            "executeEndTime": "20150616093405876",
 *            "executeStartTime": "20150616093405844",
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group2":"2"},
 *            "id": "2",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group2"],
 *            "masterId": "Schedule2",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": "20150616080300000",
 *            "retryInterval": 10000,
 *            "state": 5,
 *            "taskName": "Flow2",
 *            "time": "20150616080000000"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>schedule</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID</td></tr>
 *     <tr><td>groupId</td><td>�X�P�W���[���O���[�vID</td></tr>
 *     <tr><td>masterId</td><td>�X�P�W���[���}�X�^ID</td></tr>
 *     <tr><td>masterGroupId</td><td>�X�P�W���[���}�X�^�O���[�vID</td></tr>
 *     <tr><td>from</td><td>�X�P�W���[���J�n�����̌������ԊJ�n�����ByyyyMMddHHmmssSSS</td></tr>
 *     <tr><td>to</td><td>�X�P�W���[���J�n�����̌������ԏI�������ByyyyMMddHHmmssSSS</td></tr>
 *     <tr><td>state</td><td>�X�P�W���[����ԁB�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td rowspan="5">2</td><td rowspan="5"><nobr>���s�\�X�P�W���[���̌���</nobr></td><td>responseType</td><td>json</td><td rowspan="5">
 *     <code><pre>
 *{
 *    "schedules": [
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": null,
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group1":"1"},
 *            "id": "1",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group1"],
 *            "masterId": "Schedule1",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "state": 1,
 *            "taskName": "Flow1",
 *            "time": "20150616080000000"
 *        },
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": [{"masterId":"Schedule1","ignoreError":false}],
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group2":"2"},
 *            "id": "2",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group2"],
 *            "masterId": "Schedule2",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": "20150616080300000",
 *            "retryInterval": 10000,
 *            "state": 1,
 *            "taskName": "Flow2",
 *            "time": "20150616080000000"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>executableSchedule</td></tr>
 *     <tr><td>time</td><td>�����ByyyyMMddHHmmssSSS�B�w�肵�Ȃ��ꍇ�́A���ݓ����B</td></tr>
 *     <tr><td>executorType</td><td>���s��ʁB�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>executorKey</td><td>���s�L�[</td></tr>
 *     <tr><td rowspan="3">3</td><td rowspan="3"><nobr>�ˑ����Ă���X�P�W���[���i��s�X�P�W���[���j�̌���</nobr></td><td>responseType</td><td>json</td><td rowspan="3">
 *     <code><pre>
 *{
 *    "schedules": [
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": null,
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group1":"1"},
 *            "id": "17",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group1"],
 *            "masterId": "Schedule1",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "state": 1,
 *            "taskName": "Flow1",
 *            "time": "20150616080000000"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>depends</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID</td></tr>
 *     <tr><td rowspan="3">4</td><td rowspan="3"><nobr>�ˑ�����Ă���X�P�W���[���i�㑱�X�P�W���[���j�̌���</nobr></td><td>responseType</td><td>json</td><td rowspan="3">
 *     <code><pre>
 *{
 *    "schedules": [
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": [{"masterId":"Schedule1","ignoreError":false}],
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group1":"1","Group3":"3"},
 *            "id": "18",
 *            "initialTime": "20150616080000000",
 *            "input": {
 *                "sleep": 100
 *            },
 *            "masterGroupIds": ["Group1","Group3"],
 *            "masterId": "Schedule2",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "state": 1,
 *            "taskName": "Flow2",
 *            "time": "20150616080000000"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>depended</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID</td></tr>
 *     <tr><td rowspan="4">5</td><td rowspan="4"><nobr>�X�P�W���[���}�X�^�̌���</nobr></td><td>responseType</td><td>json</td><td rowspan="4">
 *     <code><pre>
 *{
 *    "scheduleMasters": [
 *        {
 *            "depends": null,
 *            "enabled": true,
 *            "endTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIds": null,
 *            "id": "Schedule1",
 *            "input": null,
 *            "maxDelayTime": 0,
 *            "repeatInterval": 0,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "scheduleType": null,
 *            "startTime": "19700101080000000",
 *            "taskName": "Flow1",
 *            "template": false
 *        },
 *        {
 *            "depends": [{"masterId":"Schedule1","ignoreError":false}],
 *            "enabled": true,
 *            "endTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIds": null,
 *            "id": "Schedule2",
 *            "input": "{\"sleep\":100}",
 *            "maxDelayTime": 0,
 *            "repeatInterval": 0,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "scheduleType": null,
 *            "startTime": "19700101080000000",
 *            "taskName": "Flow2",
 *            "template": false
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>scheduleMaster</td></tr>
 *     <tr><td>masterId</td><td>�X�P�W���[���}�X�^ID</td></tr>
 *     <tr><td>masterGroupId</td><td>�X�P�W���[���}�X�^�O���[�vID</td></tr>
 *     <tr><td rowspan="6">6</td><td rowspan="6"><nobr>�X�P�W���[���̏�ԕύX</nobr></td><td>responseType</td><td>json</td><td rowspan="6"><code><pre>{"result":[{"id": "1", "result": true}]}</pre></code></td></tr>
 *     <tr><td>action</td><td>changeState</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>oldState</td><td>���݂̏�ԁB�w�肵�Ȃ��ꍇ�́A���݂̏�ԂɊւ�炸�V������ԂɕύX����B�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>newState</td><td>�V������ԁB�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>output</td><td>�������ʁB�w�肵�Ȃ��ꍇ�́A�X�V���Ȃ��B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td rowspan="5">7</td><td rowspan="5"><nobr>�X�P�W���[���̐����ԕύX</nobr></td><td>responseType</td><td>json</td><td rowspan="5"><code><pre>{"result":[{"id": "1", "result": true}]}</pre></code></td></tr>
 *     <tr><td>action</td><td>changeControlState</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>oldState</td><td>���݂̏�ԁB�w�肵�Ȃ��ꍇ�́A���݂̏�ԂɊւ�炸�V������ԂɕύX����B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>newState</td><td>�V������ԁB�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td rowspan="5">8</td><td rowspan="5"><nobr>�X�P�W���[�������̕ύX</nobr></td><td>responseType</td><td>json</td><td rowspan="5"><code><pre>{"result":[{"id": "1", "result": true}]}</pre></code></td></tr>
 *     <tr><td>action</td><td>reschedule</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>time</td><td>�����ByyyyMMddHHmmssSSS�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B�w�肵�Ȃ��ꍇ�́A���ݓ����B</td></tr>
 *     <tr><td>output</td><td>�������ʁB�w�肵�Ȃ��ꍇ�́Anull</td></tr>
 *     <tr><td rowspan="9">9</td><td rowspan="9"><nobr>�X�P�W���[���̍폜</nobr></td><td>responseType</td><td>json</td><td rowspan="9"><code><pre>{"result":true}</pre></code></td></tr>
 *     <tr><td>action</td><td>remove</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>groupId</td><td>�X�P�W���[���O���[�vID</td></tr>
 *     <tr><td>masterId</td><td>�X�P�W���[���}�X�^ID</td></tr>
 *     <tr><td>masterGroupId</td><td>�X�P�W���[���}�X�^�O���[�vID</td></tr>
 *     <tr><td>from</td><td>�X�P�W���[���J�n�����̌������ԊJ�n�����ByyyyMMddHHmmssSSS</td></tr>
 *     <tr><td>to</td><td>�X�P�W���[���J�n�����̌������ԏI�������ByyyyMMddHHmmssSSS</td></tr>
 *     <tr><td>state</td><td>�X�P�W���[����ԁB�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td rowspan="4">10</td><td rowspan="4"><nobr>���s�L�[�̕ύX</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>changeExecutorKey</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>executorKey</td><td>���s�L�[</td></tr>
 *     <tr><td rowspan="4">11</td><td rowspan="4"><nobr>���g���C�I�������̕ύX</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>changeRetryEndTime</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>time</td><td>���g���C�I�������ByyyyMMddHHmmssSSS</td></tr>
 *     <tr><td rowspan="4">12</td><td rowspan="4"><nobr>�ő�x�����Ԃ̕ύX</nobr></td><td>responseType</td><td>json</td><td rowspan="4"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>changeMaxDelayTime</td></tr>
 *     <tr><td>id</td><td>�X�P�W���[��ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>time</td><td>�ő�x������[ms]</td></tr>
 *     <tr><td rowspan="13">13</td><td rowspan="13"><nobr>�X�P�W���[���̒ǉ�</nobr></td><td>responseType</td><td>json</td><td rowspan="13">
 *     <code><pre>
 *{
 *    "schedule": {
 *        "checkState": 1,
 *        "controlState": 1,
 *        "depends": null,
 *        "executeEndTime": null,
 *        "executeStartTime": null,
 *        "executorKey": null,
 *        "executorType": null,
 *        "groupIdMap": {"Group1":"1"},
 *        "id": "1",
 *        "initialTime": "20150616080000000",
 *        "input": null,
 *        "masterGroupIds": ["Group1"],
 *        "masterId": "Schedule1",
 *        "maxDelayTime": 0,
 *        "output": null,
 *        "retry": false,
 *        "retryEndTime": null,
 *        "retryInterval": 0,
 *        "state": 1,
 *        "taskName": "Flow1",
 *        "time": "20150616080000000"
 *    }
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>add</td></tr>
 *     <tr><td>masterId</td><td>�X�P�W���[���}�X�^ID</td></tr>
 *     <tr><td>time</td><td>�����ByyyyMMddHHmmssSSS�B�w�肵�Ȃ��ꍇ�́A���ݓ����B</td></tr>
 *     <tr><td>taskName</td><td>�^�X�N��</td></tr>
 *     <tr><td>input</td><td>����</td></tr>
 *     <tr><td>depends</td><td>�ˑ�����X�P�W���[���}�X�^ID�B�����p�����[�^�A�܂��̓J���}��؂�ŕ����w��\�B</td></tr>
 *     <tr><td>executorKey</td><td>���s�L�[�B�w�肵�Ȃ��ꍇnull</td></tr>
 *     <tr><td>executorType</td><td>���s��ʁB�w�肵�Ȃ��ꍇnull</td></tr>
 *     <tr><td>retryInterval</td><td>���g���C�Ԋu[ms]�B�w�肵�Ȃ��ꍇ0</td></tr>
 *     <tr><td>retryEndTime</td><td>���g���C�I�������ByyyyMMddHHmmssSSS�B�w�肵�Ȃ��ꍇnull</td></tr>
 *     <tr><td>maxDelayTime</td><td>�ő�x������[ms]�B�w�肵�Ȃ��ꍇ0</td></tr>
 *     <tr><td rowspan="11">14</td><td rowspan="11"><nobr>�X�P�W���[���}�X�^����̃X�P�W���[���̒ǉ�</nobr></td><td>responseType</td><td>json</td><td rowspan="11">
 *     <code><pre>
 *{
 *    "schedules": [
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": null,
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group1":"1"},
 *            "id": "1",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group1"],
 *            "masterId": "Schedule1",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "state": 1,
 *            "taskName": "Flow1",
 *            "time": "20150616080000000"
 *        },
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": [{"masterId":"Schedule1","ignoreError":false}],
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group2":"2"},
 *            "id": "2",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group2"],
 *            "masterId": "Schedule2",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": "20150616080300000",
 *            "retryInterval": 10000,
 *            "state": 1,
 *            "taskName": "Flow2",
 *            "time": "20150616080000000"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>addFromMaster</td></tr>
 *     <tr><td>masterId</td><td>�X�P�W���[���}�X�^ID</td></tr>
 *     <tr><td>masterGroupId</td><td>�X�P�W���[���}�X�^�O���[�vID</td></tr>
 *     <tr><td>input</td><td>���́B�w�肵�Ȃ��ꍇ�́A�X�P�W���[���}�X�^�ʂ�B</td></tr>
 *     <tr><td>date</td><td>���t�ByyyyMMdd�B�w�肵�Ȃ��ꍇ�́A���ݓ��t�B</td></tr>
 *     <tr><td>startTime</td><td>�J�n�����BHHmmssSSS�B�w�肵�Ȃ��ꍇ�́A�X�P�W���[���}�X�^�ʂ�B</td></tr>
 *     <tr><td>endTime</td><td>�I�������BHHmmssSSS�B�w�肵�Ȃ��ꍇ�A�X�P�W���[���}�X�^�ʂ�B</td></tr>
 *     <tr><td>retryEndTime</td><td>���g���C�I�������BHHmmssSSS�B�w�肵�Ȃ��ꍇ�́A�X�P�W���[���}�X�^�ʂ�B</td></tr>
 *     <tr><td>executorKey</td><td>���s�L�[�B�w�肵�Ȃ��ꍇ�́A�X�P�W���[���}�X�^�ʂ�B</td></tr>
 *     <tr><td rowspan="2">15</td><td rowspan="2"><nobr>�X�P�W���[����ʂ̎擾</nobr></td><td>responseType</td><td>json</td><td rowspan="2"><code><pre>{"scheduleType":["�c�Ɠ�","���j��","����"]}</pre></code></td></tr>
 *     <tr><td>action</td><td>scheduleType</td></tr>
 *     <tr><td rowspan="3">16</td><td rowspan="3"><nobr>�X�P�W���[���쐬</nobr></td><td>responseType</td><td>json</td><td rowspan="3">
 *     <code><pre>
 *{
 *    "schedules": [
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": null,
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group1":"1"},
 *            "id": "1",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group1"],
 *            "masterId": "Schedule1",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": null,
 *            "retryInterval": 0,
 *            "state": 1,
 *            "taskName": "Flow1",
 *            "time": "20150616080000000"
 *        },
 *        {
 *            "checkState": 1,
 *            "controlState": 1,
 *            "depends": [{"masterId":"Schedule1","ignoreError":false}],
 *            "executeEndTime": null,
 *            "executeStartTime": null,
 *            "executorKey": null,
 *            "executorType": null,
 *            "groupIdMap": {"Group2":"2"},
 *            "id": "2",
 *            "initialTime": "20150616080000000",
 *            "input": null,
 *            "masterGroupIds": ["Group2"],
 *            "masterId": "Schedule2",
 *            "maxDelayTime": 0,
 *            "output": null,
 *            "retry": false,
 *            "retryEndTime": "20150616080300000",
 *            "retryInterval": 10000,
 *            "state": 1,
 *            "taskName": "Flow2",
 *            "time": "20150616080000000"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>makeSchedule</td></tr>
 *     <tr><td>date</td><td>���t�ByyyyMMdd�B�w�肵�Ȃ��ꍇ�́A���ݓ��t�B</td></tr>
 *     <tr><td rowspan="6">17</td><td rowspan="6"><nobr>�X�P�W���[���쐬�̔���</nobr></td><td>responseType</td><td>json</td><td rowspan="6">
 *     <code><pre>
 *{
 *    "result": [
 *        {
 *            "result": [
 *                {
 *                    "isMake": true,
 *                    "date": "20150615000000000"
 *                },
 *                {
 *                    "isMake": true,
 *                    "date": "20150616000000000"
 *                }
 *            ],
 *            "masterId": "Schedule1"
 *        },
 *        {
 *            "result": [
 *                {
 *                    "isMake": true,
 *                    "date": "20150615000000000"
 *                },
 *                {
 *                    "isMake": true,
 *                    "date": "20150616000000000"
 *                }
 *            ],
 *            "masterId": "Schedule2"
 *        }
 *    ]
 *}
 *     </pre></code></td></tr>
 *     <tr><td>action</td><td>isMakeSchedule</td></tr>
 *     <tr><td>masterId</td><td>�X�P�W���[���}�X�^ID</td></tr>
 *     <tr><td>date</td><td>���t�ByyyyMMdd�B�w�肵�Ȃ��ꍇ�́A���ݓ��t�B</td></tr>
 *     <tr><td>from</td><td>�J�n���t�ByyyyMMdd�Bto���w�肵�Ă��āAfrom���w�肵�Ȃ��ꍇ�́A���ݓ��t�B</td></tr>
 *     <tr><td>to</td><td>�I�����t�ByyyyMMdd�Bfrom���w�肵�Ă��āAto���w�肵�Ȃ��ꍇ�́A���ݓ��t�B</td></tr>
 *     <tr><td rowspan="2">18</td><td rowspan="2"><nobr>���s��ʂ̎擾</nobr></td><td>responseType</td><td>json</td><td rowspan="2"><code><pre>{"executorType": ["BEANFLOW","COMMAND"]}</pre></code></td></tr>
 *     <tr><td>action</td><td>executorType</td></tr>
 *     <tr><td rowspan="2">19</td><td rowspan="2"><nobr>�X�P�W���[�������̒�~</nobr></td><td>responseType</td><td>json</td><td rowspan="2"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>stopEntry</td></tr>
 *     <tr><td rowspan="2">20</td><td rowspan="2"><nobr>�X�P�W���[�������̊J�n</nobr></td><td>responseType</td><td>json</td><td rowspan="2"><code><pre>{}</pre></code></td></tr>
 *     <tr><td>action</td><td>startEntry</td></tr>
 *     <tr><td rowspan="2">21</td><td rowspan="2"><nobr>�X�P�W���[���������J�n����Ă��邩�̔���</nobr></td><td>responseType</td><td>json</td><td rowspan="2"><code><pre>{"result": true}</pre></code></td></tr>
 *     <tr><td>action</td><td>isStartEntry</td></tr>
 * </table>
 * <p>
 * �ȉ��ɁA�T�[�u���b�g��web.xml��`��������B<br>
 * <pre>
 * &lt;servlet&gt;
 *     &lt;servlet-name&gt;ScheduleManagerServlet&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;jp.ossc.nimbus.servlet.ScheduleManagerServlet&lt;/servlet-class&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;ScheduleManagerServiceName&lt;/param-name&gt;
 *         &lt;param-value&gt;Nimbus#ScheduleManager&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;SchedulerServiceName&lt;/param-name&gt;
 *         &lt;param-value&gt;Nimbus#Scheduler&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;ScheduleManagerServlet&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/schedule-console&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author M.Takata
 */
public class ScheduleManagerServlet extends HttpServlet{
    
    private static final long serialVersionUID = 620443131672065496L;
    
    /**
     * �ΏۂƂ���{@link ScheduleManager �X�P�W���[���Ǘ�}�̃T�[�r�X�����w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_SCHEDULE_MANAGER_SERVICE_NAME = "ScheduleManagerServiceName";
    
    /**
     * �ΏۂƂ���{@link Scheduler �X�P�W���[��}�̃T�[�r�X�����w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_SCHEDULER_SERVICE_NAME = "SchedulerServiceName";
    
    /**
     * �X�P�W���[���쐬�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_MAKE_ENABLED = "MakeEnabled";
    
    /**
     * �X�P�W���[���ǉ������L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_ADD_ENABLED = "AddEnabled";
    
    /**
     * �X�P�W���[�������ύX�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_RESCHEDULE_ENABLED = "RescheduleEnabled";
    
    /**
     * �X�P�W���[���폜�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_REMOVE_ENABLED = "RemoveEnabled";
    
    /**
     * �X�P�W���[���S�폜�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_REMOVE_ALL_ENABLED = "RemoveAllEnabled";
    
    /**
     * �X�P�W���[����ԕύX�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_CHANGE_STATE_ENABLED = "ChangeStateEnabled";
    
    /**
     * �X�P�W���[�������ԕύX�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_CHANGE_CONTROL_STATE_ENABLED = "ChangeControlStateEnabled";
    
    /**
     * ���s�L�[�ύX�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_CHANGE_EXECUTOR_KEY_ENABLED = "ChangeExecutorKeyEnabled";
    
    /**
     * ���g���C�I�������ύX�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_CHANGE_RETRY_END_TIME_ENABLED = "ChangeRetryEndTimeEnabled";
    
    /**
     * �ő�x�����ԕύX�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_CHANGE_MAX_DELAY_TIME_ENABLED = "ChangeMaxDelayTimeEnabled";
    
    /**
     * �X�P�W���[��������~�����L���ɂ��邩�ǂ������w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_STOP_ENTRY_ENABLED = "StopEntryEnabled";
    
    /**
     * JSON�R���o�[�^�̃T�[�r�X�����w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_JSON_CONVERTER_SERVICE_NAME = "JSONConverterServiceName";
    
    /**
     * JSON�������ɂQ�o�C�g���������j�R�[�h�G�X�P�[�v���邩�ǂ����̃t���O���w�肷�邽�߂̏������p�����[�^���B<p>
     */
    protected static final String INIT_PARAM_NAME_UNICODE_ESCAPE = "UnicodeEscape";
    
    private static final int HEADER_OTHER = -1;
    private static final int HEADER_SCHEDULE = 0;
    private static final int HEADER_SCHEDULE_MASTER = 1;
    
    private ScheduleManager scheduleManager;
    private Scheduler scheduler;
    private BeanJSONConverter jsonConverter;
    private StringStreamConverter toStringConverter;
    
    private ServiceName getScheduleManagerServiceName(){
        final ServletConfig config = getServletConfig();
        final String serviceNameStr = config.getInitParameter(INIT_PARAM_NAME_SCHEDULE_MANAGER_SERVICE_NAME);
        if(serviceNameStr == null){
            return null;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        return (ServiceName)editor.getValue();
    }
    
    private ServiceName getSchedulerServiceName(){
        final ServletConfig config = getServletConfig();
        final String serviceNameStr = config.getInitParameter(INIT_PARAM_NAME_SCHEDULER_SERVICE_NAME);
        if(serviceNameStr == null){
            return null;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        return (ServiceName)editor.getValue();
    }
    
    private boolean isMakeEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_MAKE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isAddEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_ADD_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isRescheduleEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_RESCHEDULE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isRemoveEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_REMOVE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isRemoveAllEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_REMOVE_ALL_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isChangeStateEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CHANGE_STATE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isChangeControlStateEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CHANGE_CONTROL_STATE_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isChangeExecutorKeyEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CHANGE_EXECUTOR_KEY_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isChangeRetryEndTimeEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CHANGE_RETRY_END_TIME_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isChangeMaxDelayTimeEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_CHANGE_MAX_DELAY_TIME_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private boolean isStopEntryEnabled(){
        final ServletConfig config = getServletConfig();
        final String isEnabled = config.getInitParameter(INIT_PARAM_NAME_STOP_ENTRY_ENABLED);
        return isEnabled == null ? false : Boolean.valueOf(isEnabled).booleanValue();
    }
    
    private ServiceName getJSONConverterServiceName(){
        final ServletConfig config = getServletConfig();
        final String serviceNameStr = config.getInitParameter(INIT_PARAM_NAME_JSON_CONVERTER_SERVICE_NAME);
        if(serviceNameStr == null){
            return null;
        }
        final ServiceNameEditor editor = new ServiceNameEditor();
        editor.setAsText(serviceNameStr);
        return (ServiceName)editor.getValue();
    }
    
    private boolean isUnicodeEscape(){
        final ServletConfig config = getServletConfig();
        final String isEscape = config.getInitParameter(INIT_PARAM_NAME_UNICODE_ESCAPE);
        return isEscape == null ? true : Boolean.valueOf(isEscape).booleanValue();
    }
    
    /**
     * �T�[�u���b�g�̏��������s���B<p>
     * �T�[�r�X��`�̃��[�h�y�у��[�h�����`�F�b�N���s���B
     *
     * @exception ServletException �T�[�u���b�g�̏������Ɏ��s�����ꍇ
     */
    public synchronized void init() throws ServletException{
        ServiceName jsonConverterServiceName = getJSONConverterServiceName();
        if(jsonConverterServiceName == null){
            jsonConverter = new BeanJSONConverter();
            DateFormatConverter dateFormatConverter = new DateFormatConverter(DateFormatConverter.DATE_TO_STRING, "yyyyMMddHHmmssSSS");
            dateFormatConverter.setNullString(null);
            jsonConverter.setFormatConverter(
                Date.class,
                dateFormatConverter
            );
        }else{
            jsonConverter = (BeanJSONConverter)ServiceManagerFactory.getServiceObject(jsonConverterServiceName);
        }
        jsonConverter.setCharacterEncodingToStream("UTF-8");
        jsonConverter.setUnicodeEscape(isUnicodeEscape());
        toStringConverter = new StringStreamConverter(StringStreamConverter.STREAM_TO_STRING);
        toStringConverter.setCharacterEncodingToObject("UTF-8");
        
        ServiceName scheduleManagerServiceName = getScheduleManagerServiceName();
        if(scheduleManagerServiceName == null){
            throw new ServletException("ScheduleManagerServiceName is null.");
        }else{
            scheduleManager = (ScheduleManager)ServiceManagerFactory.getServiceObject(scheduleManagerServiceName);
        }
        
        ServiceName schedulerServiceName = getSchedulerServiceName();
        if(schedulerServiceName != null){
            scheduler = (Scheduler)ServiceManagerFactory.getServiceObject(schedulerServiceName);
        }
    }
    
    /**
     * POST���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @exception ServletException
     * @exception IOException
     */
    protected void doPost(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        process(req, resp);
    }
    
    /**
     * GET���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @exception ServletException
     * @exception IOException
     */
    protected void doGet(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        process(req, resp);
    }
    
    /**
     * ���N�G�X�g�������s���B<p>
     * �Ǘ��R���\�[���������s���B
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @exception ServletException
     * @exception IOException
     */
    protected void process(
        HttpServletRequest req,
        HttpServletResponse resp
    ) throws ServletException, IOException{
        req.setCharacterEncoding("UTF-8");
        
        if(scheduleManager == null){
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }
        
        final String action = getParameter(req, "action");
        final String responseType = getParameter(req, "responseType");
        if(action == null || action.equals("schedule")){
            processScheduleResponse(req, resp, responseType);
        }else if(action.equals("executableSchedule")){
            processExecutableScheduleResponse(req, resp, responseType);
        }else if(action.equals("depends")){
            processDependsResponse(req, resp, responseType);
        }else if(action.equals("depended")){
            processDependedResponse(req, resp, responseType);
        }else if(action.equals("scheduleMaster")){
            processScheduleMasterResponse(req, resp, responseType);
        }else if(action.equals("changeState")){
            if(!isChangeStateEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processChangeStateResponse(req, resp, responseType);
        }else if(action.equals("changeControlState")){
            if(!isChangeControlStateEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processChangeControlStateResponse(req, resp, responseType);
        }else if(action.equals("reschedule")){
            if(!isRescheduleEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processRescheduleResponse(req, resp, responseType);
        }else if(action.equals("remove")){
            if(!isRemoveEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processRemoveResponse(req, resp, responseType);
        }else if(action.equals("changeExecutorKey")){
            if(!isChangeExecutorKeyEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processChangeExecutorKeyResponse(req, resp, responseType);
        }else if(action.equals("changeRetryEndTime")){
            if(!isChangeRetryEndTimeEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processChangeRetryEndTimeResponse(req, resp, responseType);
        }else if(action.equals("changeMaxDelayTime")){
            if(!isChangeMaxDelayTimeEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processChangeMaxDelayTimeResponse(req, resp, responseType);
        }else if(action.equals("add")){
            if(!isAddEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processAddResponse(req, resp, responseType);
        }else if(action.equals("addFromMaster")){
            if(!isAddEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processAddFromMasterResponse(req, resp, responseType);
        }else if(action.equals("scheduleType")){
            processScheduleTypeResponse(req, resp, responseType);
        }else if(action.equals("makeSchedule")){
            if(!isMakeEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processMakeScheduleResponse(req, resp, responseType);
        }else if(action.equals("isMakeSchedule")){
            processIsMakeScheduleResponse(req, resp, responseType);
        }else if(action.equals("executorType")){
            if(scheduler == null){
                resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            }
            processExecutorTypeResponse(req, resp, responseType);
        }else if(action.equals("stopEntry")){
            if(scheduler == null){
                resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            }
            if(!isStopEntryEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processStopEntryResponse(req, resp, responseType);
        }else if(action.equals("startEntry")){
            if(scheduler == null){
                resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            }
            if(!isStopEntryEnabled()){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            processStartEntryResponse(req, resp, responseType);
        }else if(action.equals("isStartEntry")){
            if(scheduler == null){
                resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            }
            processIsStartEntryResponse(req, resp, responseType);
        }else{
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private String getCurrentPath(HttpServletRequest req){
        return req.getContextPath() + req.getServletPath();
    }
    
    private boolean isNullParameter(HttpServletRequest req, String name){
        String param = req.getParameter(name);
        if(param == null){
            return false;
        }else{
            return "null".equals(param);
        }
    }
    
    private String getParameter(HttpServletRequest req, String name) throws NumberFormatException{
        String param = req.getParameter(name);
        if(param == null || param.length() == 0 || "null".equals(param)){
            return null;
        }
        return param;
    }
    
    private Long getLongParameter(HttpServletRequest req, String name) throws NumberFormatException{
        String param = getParameter(req, name);
        if(param == null){
            return null;
        }
        return Long.valueOf(param);
    }
    
    private long[] getLongParameterValues(HttpServletRequest req, String name) throws NumberFormatException{
        String[] params = getParameterValues(req, name);
        if(params == null){
            return null;
        }
        final long[] result = new long[params.length];
        for(int i = 0; i < params.length; i++){
            result[i] = params[i] == null ? 0 : Long.parseLong(params[i]);
        }
        return result;
    }
    
    private Date getDateParameter(HttpServletRequest req, String name, String format, boolean defaultIsNow) throws ParseException{
        String param = getParameter(req, name);
        if(param == null){
            return defaultIsNow ? new Date() : null;
        }
        return  new SimpleDateFormat(format).parse(param);
    }
    
    private Date[] getDateParameterValues(HttpServletRequest req, String name, String format, boolean defaultIsNow, int length) throws ParseException{
        String[] params = getParameterValues(req, name);
        if(params == null){
            if(defaultIsNow){
                Date[] result = new Date[length];
                Date now = new Date();
                for(int i = 0; i < result.length; i++){
                    result[i] = now;
                }
                return result;
            }else{
                return null;
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date now = new Date();
        Date[] result = new Date[params.length];
        for(int i = 0; i < params.length; i++){
            result[i] = params[i] == null ? (defaultIsNow ? now : null) : dateFormat.parse(params[i]);
        }
        return result;
    }
    
    private int[] getIntParameterValues(HttpServletRequest req, String name) throws NumberFormatException{
        String[] params = getParameterValues(req, name);
        if(params == null){
            return null;
        }
        final int[] result = new int[params.length];
        for(int i = 0; i < params.length; i++){
            result[i] = params[i] == null ? 0 : Integer.parseInt(params[i]);
        }
        return result;
    }
    
    private String[] getParameterValues(HttpServletRequest req, String name){
        return getParameterValues(req, name, true);
    }
    private String[] getParameterValues(HttpServletRequest req, String name, boolean nullReplace){
        String[] params = req.getParameterValues(name);
        if(params == null || params.length == 0){
            return params;
        }
        if(params.length == 1){
            if(params[0].length() == 0){
                return new String[1];
            }
            return CSVReader.toArray(
                params[0],
                ',',
                '\\',
                '"',
                nullReplace ? "null" : null,
                null,
                true,
                false,
                true,
                true
            );
        }else{
            for(int i = 0; i < params.length; i++){
                if((params[i] != null && params[i].length() == 0)
                    || ("null".equals(params[i]) && nullReplace)){
                    params[i] = null;
                }
            }
            return params;
        }
    }
    
    /**
     * ���s�\�X�P�W���[���������N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processExecutableScheduleResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        Date time = null;
        try{
            time = getDateParameter(req, "time", "yyyyMMddHHmmssSSS", true);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'time' is illegal." + e.toString());
            return;
        }
        String[] executorTypes = getParameterValues(req, "executorType");
        String executorKey = getParameter(req, "executorKey");
        
        List schedules = null;
        Exception exception = null;
        try{
            schedules = scheduleManager.findExecutableSchedules(time, executorTypes, executorKey);
        }catch(ScheduleManageException e){
            exception = e;
        }
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedules", schedules);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            if(exception != null){
                buf.append(exception(exception));
            } else {
                buf.append("<html>");
                buf.append(header(HEADER_SCHEDULE));
                buf.append("<body>");
                buf.append(scheduleSearchCondition(getCurrentPath(req), "", "", "", "", null, null, null));
                buf.append(schedules(getCurrentPath(req), schedules));
                buf.append("</body>");
                buf.append("</html>");
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���������N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processScheduleResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String id = getParameter(req, "id");
        String groupId = getParameter(req, "groupId");
        String masterId = getParameter(req, "masterId");
        String masterGroupId = getParameter(req, "masterGroupId");
        Date from = null;
        try{
            from = getDateParameter(req, "from", "yyyyMMddHHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'from' is illegal." + e.toString());
            return;
        }
        Date to = null;
        try{
            to = getDateParameter(req, "to", "yyyyMMddHHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'to' is illegal." + e.toString());
            return;
        }
        int[] states = null;
        try{
            states = getIntParameterValues(req, "state");
        }catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'state' is illegal." + e.toString());
            return;
        }
        Exception exception = null;
        List schedules = null;
        if(id != null && id.length() != 0){
            try{
                Schedule schedule = scheduleManager.findSchedule(id);
                schedules = new ArrayList(1);
                schedules.add(schedule);
            }catch(ScheduleManageException e){
                exception = e;
            }
        }else{
            final String action = getParameter(req, "action");
            if(action == null
                && from == null
                && to == null
                && states == null
                && masterId == null
                && masterGroupId == null
                && groupId == null
            ){
                states = new int[]{Schedule.STATE_INITIAL,Schedule.STATE_RETRY};
            }
            try{
                schedules = scheduleManager.findSchedules(from, to, states, masterId, masterGroupId, groupId);
            }catch(ScheduleManageException e){
                exception = e;
            }
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedules", schedules);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            if(exception != null){
                buf.append(exception(exception));
            } else {
                buf.append("<html>");
                buf.append(header(HEADER_SCHEDULE));
                buf.append("<body>");
                buf.append(scheduleSearchCondition(getCurrentPath(req), id, groupId, masterId, masterGroupId, from, to, states));
                buf.append(schedules(getCurrentPath(req), schedules));
                buf.append("</body>");
                buf.append("</html>");
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �ˑ����Ă���X�P�W���[���i��s�X�P�W���[���j�̌������N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processDependsResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String id = getParameter(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        Exception exception = null;
        List schedules = null;
        try{
            schedules = scheduleManager.findDependsSchedules(id);
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedules", schedules);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            if(exception != null){
                buf.append(exception(exception));
            } else {
                buf.append("<html>");
                buf.append(header(HEADER_OTHER));
                buf.append("<body>");
                buf.append(schedules(getCurrentPath(req), schedules));
                buf.append("</body>");
                buf.append("</html>");
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �ˑ�����Ă���X�P�W���[���i�㑱�X�P�W���[���j�̌������N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processDependedResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String id = getParameter(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        Exception exception = null;
        List schedules = null;
        try{
            schedules = scheduleManager.findDependedSchedules(id);
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedules", schedules);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            if(exception != null){
                buf.append(exception(exception));
            } else {
                buf.append("<html>");
                buf.append(header(HEADER_OTHER));
                buf.append("<body>");
                buf.append(schedules(getCurrentPath(req), schedules));
                buf.append("</body>");
                buf.append("</html>");
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���}�X�^�������N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processScheduleMasterResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String masterId = getParameter(req, "masterId");
        String masterGroupId = getParameter(req, "masterGroupId");
        Exception exception = null;
        List scheduleMasters = null;
        if(masterId != null && masterId.length() != 0){
            try{
                ScheduleMaster scheduleMaster = scheduleManager.findScheduleMaster(masterId);
                if(scheduleMaster != null){
                    scheduleMasters = new ArrayList(1);
                    scheduleMasters.add(scheduleMaster);
                }
            }catch(ScheduleManageException e){
                exception = e;
            }
        }else if(masterGroupId != null && masterGroupId.length() != 0){
            try{
                scheduleMasters = scheduleManager.findScheduleMasters(masterGroupId);
            }catch(ScheduleManageException e){
                exception = e;
            }
        }else{
            try{
                scheduleMasters = scheduleManager.findAllScheduleMasters();
            }catch(ScheduleManageException e){
                exception = e;
            }
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("scheduleMasters", scheduleMasters);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            if(exception != null){
                buf.append(exception(exception));
            } else {
                buf.append("<html>");
                buf.append(header(HEADER_SCHEDULE_MASTER));
                buf.append("<body>");
                buf.append(scheduleMasterSearchCondition(getCurrentPath(req), masterId, masterGroupId));
                buf.append(scheduleMasters(getCurrentPath(req), scheduleMasters));
                buf.append("</body>");
                buf.append("</html>");
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[����ԕύX���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processChangeStateResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        int[] oldState = getIntParameterValues(req, "oldState");
        int[] newState = getIntParameterValues(req, "newState");
        if(newState == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'newState' is null.");
            return;
        }
        String[] output = getParameterValues(req, "output", false);
        Map isChanged = new LinkedHashMap();
        Exception exception = null;
        try{
            if(oldState == null){
                for(int i = 0; i < id.length; i++){
                    if(output == null || output[i] == null){
                        if(scheduleManager.changeState(id[i], newState[i])){
                            isChanged.put(id[i], Boolean.TRUE);
                        }else{
                            isChanged.put(id[i], Boolean.FALSE);
                        }
                    }else{
                        if(scheduleManager.changeState(id[i], newState[i], "null".equals(output[i]) ? null : output[i])){
                            isChanged.put(id[i], Boolean.TRUE);
                        }else{
                            isChanged.put(id[i], Boolean.FALSE);
                        }
                    }
                }
            }else{
                for(int i = 0; i < id.length; i++){
                    if(output == null || output[i] == null){
                        if(scheduleManager.changeState(id[i], oldState[i], newState[i])){
                            isChanged.put(id[i], Boolean.TRUE);
                        }else{
                            isChanged.put(id[i], Boolean.FALSE);
                        }
                    }else{
                        if(scheduleManager.changeState(id[i], oldState[i], newState[i], "null".equals(output[i]) ? null : output[i])){
                            isChanged.put(id[i], Boolean.TRUE);
                        }else{
                            isChanged.put(id[i], Boolean.FALSE);
                        }
                    }
                }
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                List result = new ArrayList();
                Iterator entries = isChanged.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    Map map = new HashMap(2);
                    map.put("id", entry.getKey());
                    map.put("result", entry.getValue());
                    result.add(map);
                }
                jsonMap.put("result", result);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[�������ԕύX���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processChangeControlStateResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        int[] oldState = getIntParameterValues(req, "oldState");
        int[] newState = getIntParameterValues(req, "newState");
        if(newState == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'newState' is null.");
            return;
        }
        Map isChanged = new LinkedHashMap();
        Exception exception = null;
        try{
            if(oldState == null){
                for(int i = 0; i < id.length; i++){
                    if(scheduleManager.changeControlState(id[i], newState[i])){
                        isChanged.put(id[i], Boolean.TRUE);
                    }else{
                        isChanged.put(id[i], Boolean.FALSE);
                    }
                }
            }else{
                for(int i = 0; i < id.length; i++){
                    if(scheduleManager.changeControlState(id[i], oldState[i], newState[i])){
                        isChanged.put(id[i], Boolean.TRUE);
                    }else{
                        isChanged.put(id[i], Boolean.FALSE);
                    }
                }
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                List result = new ArrayList();
                Iterator entries = isChanged.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    Map map = new HashMap(2);
                    map.put("id", entry.getKey());
                    map.put("result", entry.getValue());
                    result.add(map);
                }
                jsonMap.put("result", result);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[�������ύX���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processRescheduleResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        Date[] time = null;
        try{
            time = getDateParameterValues(req, "time", "yyyyMMddHHmmssSSS", true, id.length);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'time' is illegal." + e.toString());
            return;
        }
        String[] output = getParameterValues(req, "output");
        Map isChanged = new LinkedHashMap();
        Exception exception = null;
        try{
            for(int i = 0; i < id.length; i++){
                if(scheduleManager.reschedule(id[i], time[i], output[i])){
                    isChanged.put(id[i], Boolean.TRUE);
                }else{
                    isChanged.put(id[i], Boolean.FALSE);
                }
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                List result = new ArrayList();
                Iterator entries = isChanged.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    Map map = new HashMap(2);
                    map.put("id", entry.getKey());
                    map.put("result", entry.getValue());
                    result.add(map);
                }
                jsonMap.put("result", result);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���폜���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processRemoveResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        String masterId = getParameter(req, "masterId");
        String masterGroupId = getParameter(req, "masterGroupId");
        String groupId = getParameter(req, "groupId");
        Date from = null;
        try{
            from = getDateParameter(req, "from", "yyyyMMddHHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'from' is illegal." + e.toString());
            return;
        }
        Date to = null;
        try{
            to = getDateParameter(req, "to", "yyyyMMddHHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'to' is illegal." + e.toString());
            return;
        }
        int[] states = null;
        try{
            states = getIntParameterValues(req, "state");
        }catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'state' is illegal." + e.toString());
            return;
        }
        boolean isChanged = false;
        Exception exception = null;
        if(id != null){
            try{
                for(int i = 0; i < id.length; i++){
                    isChanged |= scheduleManager.removeSchedule(id[i]);
                }
            }catch(ScheduleManageException e){
                exception = e;
            }
        }else{
            if(!isRemoveAllEnabled()
                && from == null
                && to == null
                && (states == null || states.length == 0)
                && masterId == null
            ){
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
                return;
            }
            
            try{
                isChanged = scheduleManager.removeSchedule(from, to, states, masterId, masterGroupId, groupId);
            }catch(ScheduleManageException e){
                exception = e;
            }
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("result", isChanged ? Boolean.TRUE : Boolean.FALSE);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * ���s�L�[�ύX���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processChangeExecutorKeyResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        String[] executorKey = getParameterValues(req, "executorKey");
        Exception exception = null;
        try{
            for(int i = 0; i < id.length; i++){
                scheduleManager.setExecutorKey(id[i], executorKey[i]);
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * ���g���C�I�������ύX���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processChangeRetryEndTimeResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        Date[] time = null;
        try{
            time = getDateParameterValues(req, "time", "yyyyMMddHHmmssSSS", false, id.length);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'time' is illegal." + e.toString());
            return;
        }
        if(time == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'time' is null.");
            return;
        }
        Exception exception = null;
        try{
            for(int i = 0; i < id.length; i++){
                scheduleManager.setRetryEndTime(id[i], time[i]);
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �ő�x�����ԕύX���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processChangeMaxDelayTimeResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String[] id = getParameterValues(req, "id");
        if(id == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'id' is null.");
            return;
        }
        long[] time = null;
        try{
            time = getLongParameterValues(req, "time");
        }catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'time' is illegal." + e.toString());
            return;
        }
        Exception exception = null;
        try{
            for(int i = 0; i < id.length; i++){
                scheduleManager.setMaxDelayTime(id[i], time[i]);
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���ǉ����N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processAddResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String masterId = getParameter(req, "masterId");
        Date time = null;
        try{
            time = getDateParameter(req, "time", "yyyyMMddHHmmssSSS", true);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'time' is illegal." + e.toString());
            return;
        }
        String taskName = getParameter(req, "taskName");
        String input = getParameter(req, "input");
        String[] depends = getParameterValues(req, "depends");
        ScheduleDepends[] dependsArray = null;
        if(depends != null){
            dependsArray = new ScheduleDepends[depends.length];
            for(int i = 0; i < dependsArray.length; i++){
                dependsArray[i] = new DefaultScheduleDepends(depends[i], false);
            }
        }
        String executorKey = getParameter(req, "executorKey");
        String executorType = getParameter(req, "executorType");
        Long retryInterval = getLongParameter(req, "retryInterval");
        Date retryEndTime = null;
        try{
            retryEndTime = getDateParameter(req, "retryEndTime", "yyyyMMddHHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'retryEndTime' is illegal." + e.toString());
            return;
        }
        Long maxDelayTime = getLongParameter(req, "maxDelayTime");
        Exception exception = null;
        DefaultSchedule schedule = null;
        try{
            schedule = new DefaultSchedule(
                masterId,
                null,
                time,
                taskName,
                input,
                dependsArray,
                null,
                null,
                null,
                executorKey,
                executorType,
                retryInterval == null ? 0l : retryInterval.longValue(),
                retryEndTime,
                maxDelayTime == null ? 0l : maxDelayTime.longValue()
            );
            scheduleManager.addSchedule(schedule);
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedule", schedule);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req));
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���}�X�^����̃X�P�W���[���ǉ����N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processAddFromMasterResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String masterId = getParameter(req, "masterId");
        String masterGroupId = getParameter(req, "masterGroupId");
        if(masterId == null && masterGroupId == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'masterId' and 'masterGroupId' is null.");
            return;
        }
        boolean isNullInput = isNullParameter(req, "input");
        String input = getParameter(req, "input");
        Date date = null;
        try{
            date = getDateParameter(req, "date", "yyyyMMdd", true);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'date' is illegal." + e.toString());
            return;
        }
        Date startTime = null;
        try{
            startTime = getDateParameter(req, "startTime", "HHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'startTime' is illegal." + e.toString());
            return;
        }
        Date endTime = null;
        try{
            endTime = getDateParameter(req, "endTime", "HHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'endTime' is illegal." + e.toString());
            return;
        }
        Date retryEndTime = null;
        try{
            retryEndTime = getDateParameter(req, "retryEndTime", "HHmmssSSS", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'retryEndTime' is illegal." + e.toString());
            return;
        }
        boolean isNullExecutorKey = isNullParameter(req, "executorKey");
        String executorKey = getParameter(req, "executorKey");
        Exception exception = null;
        List schedules = null;
        try{
            List scheduleMasters = null;
            if(masterId != null){
                scheduleMasters = new ArrayList();
                ScheduleMaster scheduleMaster = scheduleManager.findScheduleMaster(masterId);
                if(scheduleMaster == null){
                    exception = new Exception("ScheduleMaster not found. masterId=" + masterId);
                }else{
                    scheduleMasters.add(scheduleMaster);
                }
            }else{
                scheduleMasters = scheduleManager.findScheduleMasters(masterGroupId);
                if(scheduleMasters == null || scheduleMasters.size() == 0){
                    exception = new Exception("ScheduleMaster not found. masterGroupId=" + masterGroupId);
                }
            }
            
            for(int i = 0; i < scheduleMasters.size(); i++){
                ScheduleMaster scheduleMaster = (ScheduleMaster)scheduleMasters.get(i);
                if(isNullInput){
                    scheduleMaster.setInput(null);
                }else if(input != null){
                    scheduleMaster.setInput(input);
                }
                if(startTime != null){
                    scheduleMaster.setStartTime(startTime);
                }
                if(endTime != null){
                    scheduleMaster.setEndTime(endTime);
                }
                if(retryEndTime != null){
                    scheduleMaster.setRetryEndTime(retryEndTime);
                }
                if(isNullExecutorKey){
                    scheduleMaster.setExecutorKey(null);
                }else if(executorKey != null){
                    scheduleMaster.setExecutorKey(executorKey);
                }
                scheduleMaster.setTemplate(false);
            }
            if(scheduleMasters.size() != 0){
                schedules = scheduleManager.makeSchedule(date, scheduleMasters);
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedules", schedules);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req) + "?action=scheduleMaster");
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[����ʎ擾���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processScheduleTypeResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        Exception exception = null;
        Map typeMap = null;
        try{
            typeMap = scheduleManager.getScheduleMakerMap();
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                Object[] types = typeMap == null ? null : typeMap.keySet().toArray();
                if(types != null){
                    Arrays.sort(types);
                }
                jsonMap.put("scheduleType", types);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���쐬���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processMakeScheduleResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        Date date = null;
        try{
            date = getDateParameter(req, "date", "yyyyMMdd", true);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'date' is illegal." + e.toString());
            return;
        }
        Exception exception = null;
        List schedules = null;
        try{
            schedules = scheduleManager.makeSchedule(date);
        }catch(ScheduleManageException e){
            exception = e;
        }

        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                jsonMap.put("schedules", schedules);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            if(exception != null){
                resp.setContentType("text/html;charset=UTF-8");
                buf.append(exception(exception));
            } else {
                resp.sendRedirect(getCurrentPath(req) + "?action=scheduleMaster");
                return;
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[���쐬���胊�N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processIsMakeScheduleResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        String masterId = getParameter(req, "masterId");
        Date date = null;
        try{
            date = getDateParameter(req, "date", "yyyyMMdd", true);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'date' is illegal." + e.toString());
            return;
        }
        Date from = null;
        try{
            from = getDateParameter(req, "from", "yyyyMMdd", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'from' is illegal." + e.toString());
            return;
        }
        Date to = null;
        try{
            to = getDateParameter(req, "to", "yyyyMMdd", false);
        }catch(ParseException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter 'to' is illegal." + e.toString());
            return;
        }
        Exception exception = null;
        boolean isMake = false;
        Map masterIdDayMap = new TreeMap();
        try{
            if(masterId != null){
                ScheduleMaster scheduleMaster = scheduleManager.findScheduleMaster(masterId);
                ScheduleMaker scheduleMaker = scheduleMaster != null ? scheduleManager.getScheduleMaker(scheduleMaster.getScheduleType()) : null;
                if(scheduleMaster == null){
                    exception = new Exception("scheduleMaster not found. masterId=" + masterId);
                }else{
                    Map dayMap = new TreeMap();
                    masterIdDayMap.put(scheduleMaster.getId(), dayMap);
                    if(from == null && to == null){
                        isMake = scheduleMaker.isMakeSchedule(date, scheduleMaster);
                        dayMap.put(date, isMake ? Boolean.TRUE : Boolean.FALSE);
                    }else{
                        if(to == null){
                            to = date;
                        }else if(from == null){
                            from = date;
                        }
                        Calendar current = Calendar.getInstance();
                        current.setTime(from);
                        Calendar toCal = Calendar.getInstance();
                        toCal.setTime(to);
                        while(current.before(toCal) || current.equals(toCal)){
                            Date today = current.getTime();
                            isMake = scheduleMaker.isMakeSchedule(today, scheduleMaster);
                            dayMap.put(today, isMake ? Boolean.TRUE : Boolean.FALSE);
                            current.add(Calendar.DAY_OF_YEAR, 1);
                        }
                    }
                }
            }else{
                List scheduleMasters = scheduleManager.findAllScheduleMasters();
                if(from == null && to == null){
                    for(int i = 0; i < scheduleMasters.size(); i++){
                        ScheduleMaster scheduleMaster = (ScheduleMaster)scheduleMasters.get(i);
                        ScheduleMaker scheduleMaker = scheduleManager.getScheduleMaker(scheduleMaster.getScheduleType());
                        isMake = scheduleMaker.isMakeSchedule(date, scheduleMaster);
                        Map map = (Map)masterIdDayMap.get(scheduleMaster.getId());
                        if(map == null){
                            map = new TreeMap();
                            masterIdDayMap.put(scheduleMaster.getId(), map);
                        }
                        map.put(date, isMake ? Boolean.TRUE : Boolean.FALSE);
                    }
                }else{
                    if(to == null){
                        to = date;
                    }else if(from == null){
                        from = date;
                    }
                    Calendar current = Calendar.getInstance();
                    current.setTime(from);
                    Calendar toCal = Calendar.getInstance();
                    toCal.setTime(to);
                    while(current.before(toCal) || current.equals(toCal)){
                        Date today = current.getTime();
                        for(int i = 0; i < scheduleMasters.size(); i++){
                            ScheduleMaster scheduleMaster = (ScheduleMaster)scheduleMasters.get(i);
                            ScheduleMaker scheduleMaker = scheduleManager.getScheduleMaker(scheduleMaster.getScheduleType());
                            isMake = scheduleMaker.isMakeSchedule(today, scheduleMaster);
                            Map map = (Map)masterIdDayMap.get(scheduleMaster.getId());
                            if(map == null){
                                map = new TreeMap();
                                masterIdDayMap.put(scheduleMaster.getId(), map);
                            }
                            map.put(today, isMake ? Boolean.TRUE : Boolean.FALSE);
                        }
                        current.add(Calendar.DAY_OF_YEAR, 1);
                    }
                }
            }
        }catch(ScheduleManageException e){
            exception = e;
        }
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            if(exception == null){
                List result = new ArrayList();
                Iterator entries = masterIdDayMap.entrySet().iterator();
                while(entries.hasNext()){
                    Map.Entry entry = (Map.Entry)entries.next();
                    Map resultMap = new HashMap(2);
                    resultMap.put("masterId", entry.getKey());
                    List resultList = new ArrayList();
                    Iterator entries2 = ((Map)entry.getValue()).entrySet().iterator();
                    while(entries2.hasNext()){
                        Map.Entry entry2 = (Map.Entry)entries2.next();
                        Map resultMap2 = new HashMap(2);
                        resultMap2.put("date", entry2.getKey());
                        resultMap2.put("isMake", entry2.getValue());
                        resultList.add(resultMap2);
                    }
                    resultMap.put("result", resultList);
                    result.add(resultMap);
                }
                jsonMap.put("result", result);
            }else{
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                pw.flush();
                jsonMap.put("exception", sw.toString());
            }
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.setContentType("text/html;charset=UTF-8");
            if(exception != null){
                buf.append(exception(exception));
            } else {
                buf.append("<html>");
                buf.append(header(HEADER_OTHER));
                buf.append("<body>");
                buf.append(scheduleMasterIsMakeCondition(getCurrentPath(req), masterId, from, to));
                buf.append(scheduleMasterIsMake(getCurrentPath(req), masterIdDayMap));
                buf.append("</body>");
                buf.append("</html>");
            }
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[�����s��ʎ擾���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processExecutorTypeResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        Map typeMap = scheduler.getScheduleExecutors();
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            Object[] types = typeMap == null ? null : typeMap.keySet().toArray();
            if(types != null){
                Arrays.sort(types);
            }
            jsonMap.put("executorType", types);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[��������~���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processStopEntryResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        scheduler.stopEntry();
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.sendRedirect(getCurrentPath(req));
            return;
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[�������J�n���N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processStartEntryResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        scheduler.startEntry();
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.sendRedirect(getCurrentPath(req));
            return;
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * �X�P�W���[�������J�n���胊�N�G�X�g�������s���B<p>
     *
     * @param req HTTP���N�G�X�g
     * @param resp HTTP���X�|���X
     * @param responseType ���X�|���X���
     * @exception ServletException
     * @exception IOException
     */
    protected void processIsStartEntryResponse(
        HttpServletRequest req,
        HttpServletResponse resp,
        String responseType
    ) throws ServletException, IOException{
        boolean isStartEntry = scheduler.isStartEntry();
        
        final StringBuffer buf = new StringBuffer();
        if("json".equals(responseType)){
            resp.setContentType("application/json;charset=UTF-8");
            Map jsonMap = new HashMap();
            jsonMap.put("result", isStartEntry ? Boolean.TRUE : Boolean.FALSE);
            buf.append(
                toStringConverter.convertToObject(jsonConverter.convertToStream(jsonMap))
            );
        }else{
            resp.sendRedirect(getCurrentPath(req));
            return;
        }
        resp.getWriter().println(buf.toString());
    }
    
    /**
     * Exception��������HTML��ԋp����B<p>
     *
     * @param exception ��O
     * @return HTML������
     */
    private String exception(Exception exception){
        final StringBuffer buf = new StringBuffer();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.flush();
        buf.append("<html>");
        buf.append(header(HEADER_OTHER));
        buf.append("<body>");
        buf.append("<pre>");
        buf.append(sw.toString());
        buf.append("</pre>");
        buf.append("</body>");
        buf.append("</html>");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML��Header�v�fHTML�𐶐�����B<p>
     *
     * @param mode 0:�X�P�W���[���A1:�}�X�^�X�P�W���[��
     * @return �X�P�W���[���Ǘ����HTML��Header�v�fHTML
     */
    private String header(int mode){
        final StringBuffer buf = new StringBuffer();
        buf.append("<head>");
        buf.append("<title>Nimbus Schedule</title>");
        buf.append("<style type=\"text/css\">");
        buf.append("th { background-color: #cccccc; }");
        buf.append("table { margin: 3px; }");
        buf.append("</style>");
        buf.append("<script type=\"text/javascript\">");
        if(mode == HEADER_SCHEDULE){
            buf.append("var stateCheckFlg=false;");
            buf.append("var tableCheckFlg=false;");
            if(isChangeStateEnabled()){
                buf.append("function changeStateOne(id, old, newId){");
                buf.append("_changeState(\"changeState\", id, old, document.getElementById(newId).options[document.getElementById(newId).selectedIndex].value);");
                buf.append("}");
                
                buf.append("function changeStateMulti(){");
                buf.append("_changeStateMulti(\"changeState\", \"state_\", \"newState\");");
                buf.append("}");
            }
            if(isChangeControlStateEnabled()){
                buf.append("function changeControlStateOne(id, old, newId){");
                buf.append("_changeState(\"changeControlState\", id, old, document.getElementById(newId).options[document.getElementById(newId).selectedIndex].value);");
                buf.append("}");
                
                buf.append("function changeControlStateMulti(){");
                buf.append("_changeStateMulti(\"changeControlState\", \"controlState_\", \"newControlState\");");
                buf.append("}");
            }
            if(isChangeStateEnabled() || isChangeControlStateEnabled()){
                buf.append("function _changeStateMulti(target, stateId, newId){");
                buf.append("var checks=document.getElementsByName(\"check\");");
                buf.append("var newState=document.getElementById(newId).options[document.getElementById(newId).selectedIndex].value;");
                buf.append("var ids = new Array();");
                buf.append("var oldStates = new Array();");
                buf.append("var newStates = new Array();");
                buf.append("for(var i=0; i<checks.length; i++) {");
                buf.append("if(checks.item(i).checked) {");
                buf.append("ids.push(document.getElementById(\"id_\" + i).value);");
                buf.append("oldStates.push(document.getElementById(stateId + i).value);");
                buf.append("newStates.push(newState);");
                buf.append("}");
                buf.append("}");
                buf.append("if(ids.length == 0){");
                buf.append("return;");
                buf.append("}");
                buf.append("_changeState(target, ids.toString(), oldStates.toString(), newStates.toString());");
                buf.append("}");
                buf.append("function _changeState(target, id, old, _new){");
                buf.append("var f=document.getElementById(target);");
                buf.append("f.id.value=id;");
                buf.append("f.oldState.value=old;");
                buf.append("f.newState.value=_new;");
                buf.append("f.submit();");
                buf.append("}");
            }
            if(isRemoveEnabled()){
                buf.append("function removeOne(id){");
                buf.append("_remove(id);");
                buf.append("}");
                buf.append("function _removeMulti(){");
                buf.append("var checks=document.getElementsByName(\"check\");");
                buf.append("var ids = new Array();");
                buf.append("for(var i=0; i<checks.length; i++) {");
                buf.append("if(checks.item(i).checked) {");
                buf.append("ids.push(document.getElementById(\"id_\" + i).value);");
                buf.append("}");
                buf.append("}");
                buf.append("if(ids.length == 0){");
                buf.append("return;");
                buf.append("}");
                buf.append("_remove(ids.toString());");
                buf.append("}");
                buf.append("function _remove(id){");
                buf.append("var f=document.getElementById(\"removeSchedule\");");
                buf.append("f.id.value=id;");
                buf.append("f.submit();");
                buf.append("}");
            }
            if(isChangeExecutorKeyEnabled()){
                buf.append("function changeExecutorKeyOne(id, newId){");
                buf.append("_changeExecutorKey(id, document.getElementById(newId).value);");
                buf.append("}");
                buf.append("function changeExecutorKeyMulti(){");
                buf.append("var checks=document.getElementsByName(\"check\");");
                buf.append("var executorKey=document.getElementById(\"executorKey\").value;");
                buf.append("var ids = new Array();");
                buf.append("var executorKeys = new Array();");
                buf.append("for(var i=0; i<checks.length; i++) {");
                buf.append("if(checks.item(i).checked) {");
                buf.append("ids.push(document.getElementById(\"id_\" + i).value);");
                buf.append("executorKeys.push(executorKey);");
                buf.append("}");
                buf.append("}");
                buf.append("if(ids.length == 0){");
                buf.append("return;");
                buf.append("}");
                buf.append("_changeExecutorKey(ids.toString(),executorKeys.toString());");
                buf.append("}");
                buf.append("function _changeExecutorKey(id, _new){");
                buf.append("var f=document.getElementById(\"changeExecutorKey\");");
                buf.append("f.id.value=id;");
                buf.append("f.executorKey.value=_new;");
                buf.append("f.submit();");
                buf.append("}");
            }
            if(isRescheduleEnabled()){
                buf.append("function rescheduleOne(id,timeId,outputId){");
                buf.append("var time=document.getElementById(timeId).value;");
                buf.append("var output=document.getElementById(outputId).value;");
                buf.append("_reschedule(id,time,output);");
                buf.append("}");
                buf.append("function _rescheduleMulti(){");
                buf.append("var checks=document.getElementsByName(\"check\");");
                buf.append("var time=document.getElementById(\"time\").value;");
                buf.append("var output=document.getElementById(\"output\").value;");
                buf.append("var ids = new Array();");
                buf.append("var times = new Array();");
                buf.append("var outputs = new Array();");
                buf.append("for(var i=0; i<checks.length; i++) {");
                buf.append("if(checks.item(i).checked) {");
                buf.append("ids.push(document.getElementById(\"id_\" + i).value);");
                buf.append("times.push(time);");
                buf.append("outputs.push(output);");
                buf.append("}");
                buf.append("}");
                buf.append("if(ids.length == 0){");
                buf.append("return;");
                buf.append("}");
                buf.append("_reschedule(ids.toString(),times.toString(),outputs.toString());");
                buf.append("}");
                buf.append("function _reschedule(id,time,output){");
                buf.append("var f=document.getElementById(\"reschedule\");");
                buf.append("f.id.value=id;");
                buf.append("f.time.value=time;");
                buf.append("f.output.value=output;");
                buf.append("f.submit();");
                buf.append("}");
                buf.append("function showDepends(id){");
                buf.append("var f=document.getElementById(\"depends\");");
                buf.append("f.id.value=id;");
                buf.append("f.submit();");
                buf.append("}");
                buf.append("function showDepended(id){");
                buf.append("var f=document.getElementById(\"depended\");");
                buf.append("f.id.value=id;");
                buf.append("f.submit();");
                buf.append("}");
                buf.append("function allCheckState(){");
                buf.append("if(stateCheckFlg){");
                buf.append("stateCheckFlg=false;");
                buf.append("}else{");
                buf.append("stateCheckFlg=true;");
                buf.append("}");
                buf.append("allCheck('state', stateCheckFlg);");
                buf.append("}");
                buf.append("function allCheckTable(){");
                buf.append("if(tableCheckFlg){");
                buf.append("tableCheckFlg=false;");
                buf.append("}else{");
                buf.append("tableCheckFlg=true;");
                buf.append("}");
                buf.append("allCheck('check', tableCheckFlg);");
                buf.append("}");
                buf.append("function allCheck(id, checkFlag){");
                buf.append("var checks=document.getElementsByName(id);");
                buf.append("for(var i=0; i<checks.length; i++) {");
                buf.append("checks.item(i).checked=checkFlag");
                buf.append("}");
                buf.append("}");
            }
        }
        if(mode == HEADER_SCHEDULE_MASTER){
            if(isAddEnabled()){
                buf.append("function addScheduleOne(masterId, inputId, dateId, startTimeId, endTimeId, retryEndTimeId, executorKeyId){");
                buf.append("var f=document.getElementById(\"addFromMaster\");");
                buf.append("f.masterId.value=masterId;");
                buf.append("f.input.value=document.getElementById(inputId).value;");
                buf.append("f.date.value=document.getElementById(dateId).value;");
                buf.append("f.startTime.value=document.getElementById(startTimeId).value;");
                buf.append("f.endTime.value=document.getElementById(endTimeId).value;");
                buf.append("f.retryEndTime.value=document.getElementById(retryEndTimeId).value;");
                buf.append("f.executorKey.value=document.getElementById(executorKeyId).value;");
                buf.append("f.submit();");
                buf.append("}");
            }
        }
        buf.append("</script>");
        buf.append("</head>");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML�̃X�P�W���[��������������HTML�𐶐�����B<p>
     * @param action �T�[�u���b�g�p�X�iForm�^�O��Action������j
     * @param id ���������l�iid�j
     * @param groupId ���������l�igroupId�j
     * @param masterId ���������l�imasterId�j
     * @param masterGroupId ���������l�imasterGroupId�j
     * @param from ���������l�ifrom�j
     * @param to ���������l�ito�j
     * @param states ���������l�istates�j
     * @return �X�P�W���[���Ǘ����HTML�̃X�P�W���[��������������HTML
     */
    private String scheduleSearchCondition(String action, String id, String groupId, String masterId, String masterGroupId, Date from, Date to, int[] states){
        final StringBuffer buf = new StringBuffer();
        buf.append("<form name=\"schedule\" id=\"scheduleSearch\" action=\"" + action + "\" method=\"post\">");
        buf.append("<table border=\"1\">");
        buf.append("<tr>");
        buf.append(th("Id", "left"));
        buf.append(td(text("id", null, format(id), 30)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("GroupId", "left"));
        buf.append(td(text("groupId", null, format(groupId), 30)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("MasterId", "left"));
        buf.append(td(text("masterId", null, format(masterId), 30)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("MasterGroupId", "left"));
        buf.append(td(text("masterGroupId", null, format(masterGroupId), 30)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("From [yyyyMMddHHmmssSSS]", "left"));
        buf.append(td(text("from", null, formatDateTime(from), 30)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("To [yyyyMMddHHmmssSSS]", "left"));
        buf.append(td(text("to", null, formatDateTime(to), 30)));
        buf.append("</tr>");
        buf.append(th("State [ <a href=\"#\" onclick=\"allCheckState();return false;\">all</a> ]", "left"));
        buf.append(td(stateCheckbox(states)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(td(button("ScheduleSearch", "document.getElementById('scheduleSearch').submit();"), null, 2));
        buf.append("</tr>");
        buf.append("</table>");
        buf.append(hidden("action", null, "schedule"));
        buf.append("</form>");
        buf.append("<form name=\"scheduleMaster\" id=\"scheduleMaster\" action=\"" + action + "\" target=\"_blank\" method=\"post\">");
        buf.append("<table border=\"1\">");
        buf.append("<tr>");
        buf.append(th("MasterId", "left"));
        buf.append(td(text("masterId", null, "", 20)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("MasterGroupId", "left"));
        buf.append(td(text("masterGroupId", null, "", 20)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(td(button("ScheduleMasterSearch", "document.getElementById('scheduleMaster').submit();"), null, 2));
        buf.append("</tr>");
        buf.append("</table>");
        buf.append(hidden("action", null, "scheduleMaster"));
        buf.append("</form>");
        if(isChangeStateEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<tr>");
            buf.append(th("State", "left"));
            buf.append(td(stateSelect("newState","newState",-1)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("ChangeState", "javascript:changeStateMulti();"), null, 2));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append("</table>");
        }
        if(isChangeControlStateEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<tr>");
            buf.append(th("ControlState", "left"));
            buf.append(td(controlStateSelect("newControlState","newControlState",-1)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("ChangeControlState", "javascript:changeControlStateMulti();"), null, 2));
            buf.append("</tr>");
            buf.append("</table>");
        }
        if(isChangeExecutorKeyEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<tr>");
            buf.append(th("ExecutorKey", "left"));
            buf.append(td(text("executorKey","executorKey","",20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("ChangeExecutorKey", "javascript:changeExecutorKeyMulti();"), null, 2));
            buf.append("</tr>");
            buf.append("</table>");
        }
        if(isRescheduleEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<tr>");
            buf.append(th("Time", "left"));
            buf.append(td(text("time", "time", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("Output", "left"));
            buf.append(td(textarea("output", "output", "", 50, 3, false)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("Reschedule", "javascript:_rescheduleMulti();"), null, 2));
            buf.append("</tr>");
            buf.append("</table>");
        }
        if(isRemoveEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<tr>");
            buf.append(th("Remove", "left"));
            buf.append(td(button("RemoveSchedules", "javascript:_removeMulti();")));
            buf.append("</tr>");
            buf.append("</table>");
        }
        if(isStopEntryEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<tr>");
            buf.append(th("Entry", "left"));
            if(scheduler.isStartEntry()){
                buf.append(td("StartEntry"));
                buf.append("</tr>");
                buf.append("<tr>");
                buf.append(td(button("StopEntry", "javascript:document.getElementById('stopEntry').submit();"), null, 2));
            } else {
                buf.append(td("StopEntry"));
                buf.append("</tr>");
                buf.append("<tr>");
                buf.append(td(button("StartEntry", "javascript:document.getElementById('startEntry').submit();"), null, 2));
            }
            buf.append("</tr>");
            buf.append("</table><br clear=\"left\">");
        }
        if(isAddEnabled()){
            buf.append("<form name=\"addSchedule\" id=\"addSchedule\" action=\"" + action + "\" method=\"post\">");
            buf.append("<table border=\"1\">");
            buf.append("<tr>");
            buf.append(th("MasterId", "left"));
            buf.append(td(text("masterId", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("GroupId", "left"));
            buf.append(td(text("groupId", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("Time", "left"));
            buf.append(td(text("time", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("TaskName", "left"));
            buf.append(td(text("taskName", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("Input", "left"));
            buf.append(td(textarea("input", "", "", 50, 3, false)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("Depends", "left"));
            buf.append(td(text("depends", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("ExecutorKey", "left"));
            buf.append(td(text("executorKey", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("ExecutorType", "left"));
            buf.append(td(text("executorType", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("RetryInterval", "left"));
            buf.append(td(text("retryInterval", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("RetryEndTime", "left"));
            buf.append(td(text("retryEndTime", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("MaxDelayTime", "left"));
            buf.append(td(text("maxDelayTime", "", "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("AddSchedule", "javascript:document.getElementById('addSchedule').submit();"), null ,2));
            buf.append("</tr>");
            buf.append("</table>");
            buf.append(hidden("action", null, "add"));
            buf.append("</form>");
        }
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML�̃X�P�W���[���f�[�^����HTML�𐶐�����B<p>
     *
     * @param action �T�[�u���b�g�p�X�iForm�^�O��Action������j
     * @param schedules �X�P�W���[���I�u�W�F�N�g�̃��X�g
     * @return �X�P�W���[���Ǘ����HTML�̃X�P�W���[���f�[�^����HTML
     */
    private String schedules(String action, List schedules){
        
        final StringBuffer buf = new StringBuffer();
        buf.append("<table border=\"1\"><tr>");
        buf.append(th("Legend"));
        buf.append("<td bgcolor=\"#ffadad\">FAILED</td>");
        buf.append("<td bgcolor=\"#ffffad\">RUN</td>");
        buf.append("<td bgcolor=\"#ffadff\">TIMEOVER</td>");
        buf.append("<td bgcolor=\"#a9a9a9\">END</td>");
        buf.append("<td bgcolor=\"#add6ff\">DISABLE</td>");
        buf.append("<td bgcolor=\"#ffd6ad\">PAUSE or ABORT</td>");
        buf.append("</tr></table>");
        buf.append("<br>");
        buf.append("<table border=\"1\">");
        buf.append("<tr bgcolor=\"#cccccc\">");
        buf.append(th("<a href=\"#\" onclick=\"allCheckTable();return false;\">#</a>"));
        buf.append(th("Id"));
        buf.append(th("MasterId"));
        buf.append(th("MasterGroupIds"));
        buf.append(th("Time"));
        buf.append(th("TaskName"));
        buf.append(th("Input"));
        buf.append(th("Depends"));
        buf.append(th("Depended"));
        buf.append(th("Output"));
        buf.append(th("InitialTime"));
        buf.append(th("RetryInterval"));
        buf.append(th("RetryEndTime"));
        buf.append(th("Retry"));
        buf.append(th("MaxDelayTime"));
        buf.append(th("State"));
        buf.append(th("ControlState"));
        buf.append(th("CheckState"));
        buf.append(th("ExecutorKey"));
        buf.append(th("ExecutorType"));
        buf.append(th("ExecuteStartTime"));
        buf.append(th("ExecuteEndTime"));
        if(isRescheduleEnabled()){
            buf.append(th("Reschedule"));
        }
        if(isRemoveEnabled()){
            buf.append(th("Remove"));
        }
        buf.append("</tr>");
        int count = 0;
        
        if(schedules != null){
            for(int i = 0; i < schedules.size(); i++){
                Schedule schedule = (Schedule)schedules.get(i);
                int state = schedule.getState();
                int controlState = schedule.getControlState();
                int checkState = schedule.getCheckState();
                String name = null;
                String tr = "<tr>";
                switch(state){
                    case Schedule.STATE_RUN:
                        tr = "<tr bgcolor=\"#ffffad\" title=\"RUN\">";
                        break;
                    case Schedule.STATE_END:
                        tr = "<tr bgcolor=\"#a9a9a9\" title=\"END\">";
                        break;
                    case Schedule.STATE_FAILED:
                        tr = "<tr bgcolor=\"#ffadad\" title=\"FAILED\">";
                        break;
                    case Schedule.STATE_DISABLE:
                        tr = "<tr bgcolor=\"#add6ff\" title=\"DISABLE\">";
                        break;
                    case Schedule.STATE_PAUSE:
                    case Schedule.STATE_ABORT:
                        tr = "<tr bgcolor=\"#ffd6ad\" title=\"PAUSE or ABORT\">";
                        break;
                }
                buf.append(tr);
                String id = schedule.getId();
                buf.append(td(checkbox("check",count, null) + hidden("id", "id_" + count, id)+ hidden("state","state_" + count, String.valueOf(state)) + hidden("controlState","controlState_" + count, String.valueOf(controlState))));
                buf.append(td(format(id)));
                buf.append(td(format(schedule.getMasterId())));
                buf.append(td(format(schedule.getMasterGroupIds())));
                String time = formatDateTime(schedule.getTime());
                buf.append(td(time));
                buf.append(td(format(schedule.getTaskName())));
                buf.append(td(textarea(format(schedule.getInput()), 50, 3)));
                buf.append(td(button("ShowDepends", "javascript:showDepends('" + id + "');")));
                buf.append(td(button("ShowDepended", "javascript:showDepended('" + id + "');")));
                String output = format(schedule.getOutput());
                buf.append(td(textarea(output, 50, 3)));
                buf.append(td(formatDateTime(schedule.getInitialTime())));
                buf.append(td(schedule.getRetryInterval()));
                buf.append(td(formatDateTime(schedule.getRetryEndTime())));
                buf.append(td(schedule.isRetry()));
                buf.append(td(schedule.getMaxDelayTime()));
                final StringBuffer tdBuf = new StringBuffer();
                tdBuf.append(getScheduleStateString(state));
                if(isChangeStateEnabled()){
                    name = "newState_" + count;
                    tdBuf.append("&nbsp:&nbsp" + stateSelect(name, name, state));
                    tdBuf.append(button("ChangeState", "javascript:changeStateOne('" + id + "','" + state + "','" + name + "');"));
                }
                buf.append(td(tdBuf.toString()));
                tdBuf.setLength(0);
                tdBuf.append(getScheduleControlStateString(controlState));
                if(isChangeControlStateEnabled()){
                    name = "newControlState_" + count;
                    tdBuf.append("&nbsp:&nbsp" + controlStateSelect(name, name, controlState));
                    tdBuf.append(button("ChangeControlState", "javascript:changeControlStateOne('" + id + "','" + controlState + "','" + name + "');"));
                }
                buf.append(td(tdBuf.toString()));
                tdBuf.setLength(0);
                buf.append("<td nowrap");
                if(Schedule.CHECK_STATE_TIMEOVER == checkState){
                    buf.append(" bgcolor=\"#ffadff\" title=\"TIMEOVER\"");
                }
                buf.append(">" + getScheduleCheckStateString(checkState) + "</td>");
                
                String executorKey = format(schedule.getExecutorKey());
                tdBuf.append(executorKey);
                if(isChangeExecutorKeyEnabled()){
                    name = "newExecutorKey_" + count;
                    tdBuf.append("&nbsp:&nbsp" + text(name, name, executorKey, 10));
                    tdBuf.append(button("ChangeExecutorKey", "javascript:changeExecutorKeyOne('" + id + "','" + name + "');"));
                }
                buf.append(td(tdBuf.toString()));
                tdBuf.setLength(0);
                buf.append(td(format(schedule.getExecutorType())));
                buf.append(td(formatDateTime(schedule.getExecuteStartTime())));
                buf.append(td(formatDateTime(schedule.getExecuteEndTime())));
                if(isRescheduleEnabled()){
                    String timeName = "time_" + count;
                    tdBuf.append("Time:" + text(timeName, timeName, time, 15) + "&nbsp");
                    String outputName = "output_" + count;
                    tdBuf.append("Output:" + textarea(outputName, outputName, output, 50, 3, false));
                    buf.append(td(tdBuf.toString() + "&nbsp" +  button("Reschedule", "javascript:rescheduleOne('" + id + "','" + timeName + "','" + outputName + "');")));
                }
                if(isRemoveEnabled()){
                    buf.append(td(button("Remove", "javascript:removeOne('" + id + "');")));
                }
                buf.append("</tr>");
                count++;
            }
        }
        buf.append("</table>");
        if(isChangeStateEnabled()){
            buf.append("<form name=\"changeState\" id=\"changeState\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "changeState"));
            buf.append(hidden("oldState", null, ""));
            buf.append(hidden("newState", null, ""));
            buf.append(hidden("id", null, ""));
            buf.append("</form>");
        }
        if(isChangeControlStateEnabled()){
            buf.append("<form name=\"changeControlState\" id=\"changeControlState\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "changeControlState"));
            buf.append(hidden("oldState", null, ""));
            buf.append(hidden("newState", null, ""));
            buf.append(hidden("id", null, ""));
            buf.append("</form>");
        }
        if(isRemoveEnabled()){
            buf.append("<form name=\"removeSchedule\" id=\"removeSchedule\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "remove"));
            buf.append(hidden("id", null, ""));
            buf.append("</form>");
        }
        if(isRescheduleEnabled()){
            buf.append("<form name=\"reschedule\" id=\"reschedule\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "reschedule"));
            buf.append(hidden("id", null, ""));
            buf.append(hidden("time", null, ""));
            buf.append(hidden("output", null, ""));
            buf.append("</form>");
        }
        if(isChangeExecutorKeyEnabled()){
            buf.append("<form name=\"changeExecutorKey\" id=\"changeExecutorKey\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "changeExecutorKey"));
            buf.append(hidden("id", null, ""));
            buf.append(hidden("executorKey", null, ""));
            buf.append("</form>");
        }
        if(isStopEntryEnabled()){
            buf.append("<form name=\"stopEntry\" id=\"stopEntry\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "stopEntry"));
            buf.append("</form>");
            buf.append("<form name=\"startEntry\" id=\"startEntry\" action=\"" + action + "\" method=\"post\"  style=\"display: inline\">");
            buf.append(hidden("action", null, "startEntry"));
            buf.append("</form>");
        }
        buf.append("<form name=\"depends\" id=\"depends\" action=\"" + action + "\" method=\"post\" target=\"_blank\" style=\"display: inline\">");
        buf.append(hidden("action", null, "depends"));
        buf.append(hidden("id", null, ""));
        buf.append("</form>");
        buf.append("<form name=\"depended\" id=\"depended\" action=\"" + action + "\" method=\"post\" target=\"_blank\" style=\"display: inline\">");
        buf.append(hidden("action", null, "depended"));
        buf.append(hidden("id", null, ""));
        buf.append("</form>");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^������������HTML�𐶐�����B<p>
     * @param action �T�[�u���b�g�p�X�iForm�^�O��Action������j
     * @param masterId ���������l�imasterId�j
     * @param masterGroupId ���������l�imasterGroupId�j
     * @return �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^������������HTML
     */
    private String scheduleMasterSearchCondition(String action, String masterId, String masterGroupId){
        final StringBuffer buf = new StringBuffer();
        buf.append("<form name=\"scheduleMaster\" id=\"scheduleMaster\" action=\"" + action + "\" method=\"post\">");
        buf.append("<table border=\"1\">");
        buf.append("<tr>");
        buf.append(th("MasterId", "left"));
        buf.append(td(text("masterId", null, format(masterId), 20)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("MasterGroupId", "left"));
        buf.append(td(text("masterGroupId", null, format(masterGroupId), 20)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(td(button("ScheduleMasterSearch", "document.getElementById('scheduleMaster').submit();"), null, 2));
        buf.append("</tr>");
        buf.append("</table>");
        buf.append(hidden("action", null, "scheduleMaster"));
        buf.append("</form>");
        if(isAddEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<form name=\"addFromMaster\" id=\"addFromMaster\" action=\"" + action + "\" method=\"post\">");
            buf.append("<tr>");
            buf.append(th("MasterId", "left"));
            buf.append(td(text("masterId", null, "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("MasterGroupId", "left"));
            buf.append(td(text("masterGroupId", null, "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("groupId", "left"));
            buf.append(td(text("groupId", null, "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("Input", "left"));
            buf.append(td(textarea("input", "", "", 50, 3, false)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("Date [yyyyMMdd]", "left"));
            buf.append(td(text("date", null, "", 10)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("StartTime [HHmmssSSS]", "left"));
            buf.append(td(text("startTime", null, "", 10)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("EndTime [HHmmssSSS]", "left"));
            buf.append(td(text("endTime", null, "", 10)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("RetryEndTime [HHmmssSSS]", "left"));
            buf.append(td(text("retryEndTime", null, "", 10)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(th("ExecutorKey", "left"));
            buf.append(td(text("executorKey", null, "", 20)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("AddSchedule", "javascript:document.getElementById('addFromMaster').submit();"), null, 2));
            buf.append("</tr>");
            buf.append(hidden("action", null, "addFromMaster"));
            buf.append("</form>");
            buf.append("</table>");
        }
        if(isMakeEnabled()){
            buf.append("<table border=\"1\" align=\"left\">");
            buf.append("<form name=\"makeSchedule\" id=\"makeSchedule\" action=\"" + action + "\" method=\"post\">");
            buf.append("<tr>");
            buf.append(th("Date [yyyyMMdd]", "left"));
            buf.append(td(text("date", null, "", 10)));
            buf.append("</tr>");
            buf.append("<tr>");
            buf.append(td(button("MakeSchedule", "javascript:document.getElementById('makeSchedule').submit();"), null, 2));
            buf.append("</tr>");
            buf.append(hidden("action", null, "makeSchedule"));
            buf.append("</form>");
            buf.append("</table>");
        }
        buf.append("<table border=\"1\" align=\"left\">");
        buf.append("<form name=\"isMakeSchedule\" id=\"isMakeSchedule\" action=\"" + action + "\" target=\"_blank\" method=\"post\">");
        buf.append("<tr>");
        buf.append(th("MasterId", "left"));
        buf.append(td(text("masterId", null, "", 20)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("From [yyyyMMdd]", "left"));
        buf.append(td(text("from", null, "", 10)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("To [yyyyMMdd]", "left"));
        buf.append(td(text("to", null, "", 10)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(td(button("IsMakeSchedule", "javascript:document.getElementById('isMakeSchedule').submit();"), null, 2));
        buf.append("</tr>");
        buf.append(hidden("action", null, "isMakeSchedule"));
        buf.append("</form>");
        buf.append("</table><br clear=\"left\">");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^�f�[�^����HTML�𐶐�����B<p>
     *
     * @param action �T�[�u���b�g�p�X�iForm�^�O��Action������j
     * @param schedules �X�P�W���[���I�u�W�F�N�g�̃��X�g
     * @return �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^�f�[�^����HTML
     */
    private String scheduleMasters(String action, List scheduleMasters){
        final StringBuffer buf = new StringBuffer();
        buf.append("<table border=\"1\">");
        buf.append("<tr bgcolor=\"#cccccc\">");
        buf.append(th("MasterId"));
        buf.append(th("MasterGroupIds"));
        buf.append(th("TaskName"));
        buf.append(th("ScheduleType"));
        buf.append(th("Input"));
        buf.append(th("StartTime"));
        buf.append(th("EndTime"));
        buf.append(th("RepeatInterval"));
        buf.append(th("RetryInterval"));
        buf.append(th("RetryEndTime"));
        buf.append(th("MaxDelayTime"));
        buf.append(th("Enable"));
        buf.append(th("ExecutorKey"));
        buf.append(th("ExecutorType"));
        buf.append(th("Template"));
        buf.append(th("addSchedule"));
        buf.append("</tr>");
        if(scheduleMasters != null){
            int count = 0;
            for(int i = 0; i < scheduleMasters.size(); i++){
                ScheduleMaster scheduleMaster = (ScheduleMaster)scheduleMasters.get(i);
                buf.append("<tr>");
                String masterId = format(scheduleMaster.getId());
                buf.append(td(masterId));
                buf.append(td(format(scheduleMaster.getGroupIds())));
                buf.append(td(format(scheduleMaster.getTaskName())));
                buf.append(td(format(scheduleMaster.getScheduleType())));
                String input = format(scheduleMaster.getInput());
                buf.append(td(textarea(input, 50, 3)));
                String startTime = formatTime(scheduleMaster.getStartTime());
                buf.append(td(startTime));
                String endTime = formatTime(scheduleMaster.getEndTime());
                buf.append(td(endTime));
                buf.append(td(scheduleMaster.getRepeatInterval()));
                buf.append(td(scheduleMaster.getRetryInterval()));
                String retryEndTime = formatTime(scheduleMaster.getRetryEndTime());
                buf.append(td(retryEndTime));
                buf.append(td(scheduleMaster.getMaxDelayTime()));
                buf.append(td(scheduleMaster.isEnabled()));
                String executorKey = format(scheduleMaster.getExecutorKey());
                buf.append(td(executorKey));
                buf.append(td(format(scheduleMaster.getExecutorType())));
                buf.append(td(scheduleMaster.isTemplate()));

                final StringBuffer tdBuf = new StringBuffer();
                if(isAddEnabled()){
                    String dateName = "date_" + count;
                    tdBuf.append("Date[yyyyMMdd]:" + text(dateName, dateName, "", 10) + "&nbsp");
                    String startTimeName = "startTime_" + count;
                    tdBuf.append("StartTime[HHmmssSSS]:" + text(startTimeName, startTimeName, startTime, 10) + "&nbsp");
                    String endTimeName = "endTime_" + count;
                    tdBuf.append("EndTime[HHmmssSSS]:" + text(endTimeName, endTimeName, endTime, 10) + "<br>");
                    String retryEndTimeName = "retryEndTime_" + count;
                    tdBuf.append("RetryEndTime[HHmmssSSS]:" + text(retryEndTimeName, retryEndTimeName, retryEndTime, 10) + "&nbsp");
                    String inputName = "input_" + count;
                    tdBuf.append("Input:" + textarea(inputName, inputName, input, 50, 3, false) + "&nbsp");
                    String executorKeyName = "executorKey_" + count;
                    tdBuf.append("ExecutorKey:" + text(executorKeyName, executorKeyName, executorKey, 10) + "&nbsp");
                    buf.append(td(tdBuf.toString() + "&nbsp" +  button("AddSchedule", "javascript:addScheduleOne('" + masterId + "','" + inputName + "','" + dateName + "','" + startTimeName + "','" + endTimeName + "','" + retryEndTimeName + "','" + executorKeyName + "');")));
                }
                buf.append("</tr>");
                count++;
            }
        }
        buf.append("</table>");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^������������HTML�𐶐�����B<p>
     *
     * @param action �T�[�u���b�g�p�X�iForm�^�O��Action������j
     * @param masterId ���������l�imasterId�j
     * @param masterGroupId ���������l�imasterGroupId�j
     * @return �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^������������HTML
     */
    private String scheduleMasterIsMakeCondition(String action, String masterId, Date from, Date to){
        final StringBuffer buf = new StringBuffer();
        buf.append("<form name=\"isMakeSchedule\" id=\"isMakeSchedule\" action=\"" + action + "\" method=\"post\">");
        buf.append("<table border=\"1\">");
        buf.append("<tr>");
        buf.append(th("MasterId", "left"));
        buf.append(td(text("masterId", null, format(masterId), 20)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("From [yyyyMMdd]", "left"));
        buf.append(td(text("from", null, formatDate(from), 10)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(th("To [yyyyMMdd]", "left"));
        buf.append(td(text("to", null, formatDate(to), 10)));
        buf.append("</tr>");
        buf.append("<tr>");
        buf.append(td(button("IsMakeSchedule", "javascript:document.getElementById('isMakeSchedule').submit();"), null, 2));
        buf.append("</tr>");
        buf.append("</table>");
        buf.append(hidden("action", null, "isMakeSchedule"));
        buf.append("</form>");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^�f�[�^����HTML�𐶐�����B<p>
     *
     * @param action �T�[�u���b�g�p�X�iForm�^�O��Action������j
     * @param schedules �X�P�W���[���I�u�W�F�N�g�̃��X�g
     * @return �X�P�W���[���Ǘ����HTML�̃X�P�W���[���}�X�^�f�[�^����HTML
     */
    private String scheduleMasterIsMake(String action, Map masterIdDayMap){
        final StringBuffer buf = new StringBuffer();
        buf.append("<table border=\"1\">");
        buf.append("<tr bgcolor=\"#cccccc\">");
        buf.append(th("MasterId"));
        Iterator entries = masterIdDayMap.entrySet().iterator();
        while(entries.hasNext()) {
            Map.Entry e = (Map.Entry)entries.next();
            Map map = (Map)e.getValue();
            Iterator keys = map.keySet().iterator();
            while(keys.hasNext()) {
                Date date = (Date)keys.next();
                buf.append(th(formatDate(date)));
            }
            break;
        }
        buf.append("</tr>");
        if(masterIdDayMap != null){
            entries = masterIdDayMap.entrySet().iterator();
            while(entries.hasNext()) {
                Map.Entry e = (Map.Entry)entries.next();
                buf.append("<tr>");
                String masterId = (String)e.getKey();
                buf.append(th(format(masterId)));
                Map map = (Map)e.getValue();
                Iterator values = map.values().iterator();
                while(values.hasNext()) {
                    Boolean isMake = (Boolean)values.next();
                    final StringBuffer tdBuf = new StringBuffer();
                    tdBuf.append(isMake);
                    buf.append(td(tdBuf.toString()));
                }
                buf.append("</tr>");
            }
        }
        buf.append("</table>");
        return buf.toString();
    }
    
    /**
     * th�^�O��HTML������𐶐�����B<p>
     *
     * @param data th�^�O���̕�����
     * @return th�^�O��HTML������
     */
    private String th(String data){
        return th(data, null);
    }
    
    /**
     * th�^�O��HTML������𐶐�����B<p>
     *
     * @param data th�^�O���̕�����
     * @param align th�^�O��align�l
     * @return th�^�O��HTML������
     */
    private String th(String data, String align){
        return th(data, align, 0);
    }
    
    /**
     * th�^�O��HTML������𐶐�����B<p>
     *
     * @param data th�^�O���̕�����
     * @param align th�^�O��align�l
     * @param colspan th�^�O��colspan�l
     * @return th�^�O��HTML������
     */
    private String th(String data, String align, int colspan){
        final StringBuffer buf = new StringBuffer();
        buf.append("<th");
        if(align != null && align.length() != 0){
            buf.append(" align=\"" + align + "\"");
        }
        if(colspan > 1){
            buf.append(" colspan=\"" + colspan + "\"");
        }
        buf.append(">" + data + "</th>");
        return buf.toString();
    }
    
    /**
     * td�^�O��HTML������𐶐�����B<p>
     *
     * @param data td�^�O���̕�����
     * @return td�^�O��HTML������
     */
    private String td(long data){
        return td(String.valueOf(data), "right");
    }
    
    /**
     * td�^�O��HTML������𐶐�����B<p>
     *
     * @param data td�^�O���̕�����
     * @return td�^�O��HTML������
     */
    private String td(boolean data){
        return td(String.valueOf(data), null);
    }
    
    /**
     * td�^�O��HTML������𐶐�����B<p>
     *
     * @param data td�^�O���̕�����
     * @return td�^�O��HTML������
     */
    private String td(String data){
        return td(data, null);
    }
    
    /**
     * td�^�O��HTML������𐶐�����B<p>
     *
     * @param data td�^�O���̕�����
     * @param align td�^�O��align�l
     * @return td�^�O��HTML������
     */
    private String td(String data, String align){
        return td(data, align, 0);
    }
    
    /**
     * td�^�O��HTML������𐶐�����B<p>
     *
     * @param data td�^�O���̕�����
     * @param align td�^�O��align�l
     * @param colspan td�^�O��colspan�l
     * @return td�^�O��HTML������
     */
    private String td(String data, String align, int colspan){
        final StringBuffer buf = new StringBuffer();
        buf.append("<td valign=\"middle\"");
        if(align != null && align.length() != 0){
            buf.append(" align=\"" + align + "\"");
        }
        if(colspan > 1){
            buf.append(" colspan=\"" + colspan + "\"");
        }
        buf.append(" nowrap>" + data + "</td>");
        return buf.toString();
    }
    
    /**
     * input�^�O�itype=text�j��HTML������𐶐�����B<p>
     *
     * @param name input�^�O��name����
     * @param id input�^�O��id����
     * @param value input�^�O��value����
     * @param size input�^�O��size����
     * @return input�^�O�itype=text�j��HTML������
     */
    private String text(String name, String id, String value, int size){
        return input("text", name, id, value, size);
    }
    
    /**
     * input�^�O�itype=hidden�j��HTML������𐶐�����B<p>
     *
     * @param name input�^�O��name����
     * @param id input�^�O��id����
     * @param value input�^�O��value����
     * @return input�^�O�itype=hidden�j��HTML������
     */
    private String hidden(String name, String id, String value){
        return input("hidden", name, id, value, 0);
    }
    
    /**
     * input�^�O��HTML������𐶐�����B<p>
     *
     * @param type input�^�O��type����
     * @param name input�^�O��name����
     * @param id input�^�O��id����
     * @param value input�^�O��value����
     * @param size input�^�O��size����
     * @return input�^�O��HTML������
     */
    private String input(String type, String name, String id, String value, int size){
        final StringBuffer buf = new StringBuffer();
        buf.append("<input type=\"" + type + "\" name=\"" + name + "\" value=\"" + value + "\"");
        if(id != null){
            buf.append(" id=\"" + id + "\"");
        }
        if(size > 0){
            buf.append(" size=\"" + size + "\"");
        }
        buf.append(">");
        return buf.toString();
    }
    
    /**
     * textarea�^�O��HTML������𐶐�����B<p>
     *
     * @param value textarea�^�O�̓��e
     * @param cols textarea�^�O��cols����
     * @param rows textarea�^�O��rows����
     * @return textarea�^�O��HTML������
     */
    private String textarea(String value, int cols, int rows){
        return textarea(null, null, value, cols, rows, true);
    }
    
    /**
     * textarea�^�O��HTML������𐶐�����B<p>
     *
     * @param name textarea�^�O��name����
     * @param id textarea�^�O��id����
     * @param value textarea�^�O�̓��e
     * @param cols textarea�^�O��cols����
     * @param rows textarea�^�O��rows����
     * @param readOnly �ǂݎ���p�ݒ�
     * @return textarea�^�O��HTML������
     */
    private String textarea(String name, String id, String value, int cols, int rows, boolean readOnly){
        final StringBuffer buf = new StringBuffer();
        buf.append("<textarea ");
        if(readOnly){
            buf.append("readonly ");
        }
        if(name != null){
            buf.append("name=\"" + name + "\" ");
        }
        if(id != null){
            buf.append("id=\"" + id + "\" ");
        }
        buf.append("cols=" + cols + " rows=" + rows + " style=\"vertical-align:middle\">");
        buf.append(value);
        buf.append("</textarea>");
        return buf.toString();
    }
    
    /**
     * input(type=button)�^�O��HTML������𐶐�����B<p>
     *
     * @param value input�^�O��value����
     * @param onClick input�^�O��onClick����
     * @return input�^�O�itype=button�j��HTML������
     */
    private String button(String value, String onClick){
        return "<input type=\"button\" value=\"" + value + "\" onclick=\"" + onClick + "\">";
    }
    
    /**
     * �X�P�W���[���̑S�ẴX�e�[�^�X�`�F�b�N�{�b�N�X��HTML�𐶐�����B<p>
     *
     * @param states �I���ς�State
     * @return �X�P�W���[���̑S�ẴX�e�[�^�X�`�F�b�N�{�b�N�X��HTML
     */
    private String stateCheckbox(int[] states){
        final StringBuffer buf = new StringBuffer();
        buf.append(checkbox("state", Schedule.STATE_INITIAL, states) + "<label for=\"state" + Schedule.STATE_INITIAL + "\">" + getScheduleStateString(Schedule.STATE_INITIAL) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_ENTRY, states) + "<label for=\"state" + Schedule.STATE_ENTRY + "\">" + getScheduleStateString(Schedule.STATE_ENTRY) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_RUN, states) + "<label for=\"state" + Schedule.STATE_RUN + "\">" + getScheduleStateString(Schedule.STATE_RUN) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_END, states) + "<label for=\"state" + Schedule.STATE_END + "\">" + getScheduleStateString(Schedule.STATE_END) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_FAILED, states) + "<label for=\"state" + Schedule.STATE_FAILED + "\">" + getScheduleStateString(Schedule.STATE_FAILED) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_PAUSE, states) + "<label for=\"state" + Schedule.STATE_PAUSE + "\">" + getScheduleStateString(Schedule.STATE_PAUSE) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_ABORT, states) + "<label for=\"state" + Schedule.STATE_ABORT + "\">" + getScheduleStateString(Schedule.STATE_ABORT) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_RETRY, states) + "<label for=\"state" + Schedule.STATE_RETRY + "\">" + getScheduleStateString(Schedule.STATE_RETRY) + "</label>&nbsp");
        buf.append(checkbox("state", Schedule.STATE_DISABLE, states) + "<label for=\"state" + Schedule.STATE_DISABLE + "\">" + getScheduleStateString(Schedule.STATE_DISABLE) + "</label>&nbsp");
        return buf.toString();
    }
    
    /**
     * input(type=checkbox)�^�O��HTML������𐶐�����B<p>
     *
     * @param name input�^�O��name����
     * @param value input�^�O��value����
     * @param states �I���ς�State
     * @return input(type=checkbox)�^�O��HTML������
     */
    private String checkbox(String name, int value, int[] states){
        final StringBuffer buf = new StringBuffer();
        buf.append("<input type=\"checkbox\" name=\"" + name + "\" value=\"" + value + "\" id=\"" + name + value + "\"");
        if(states != null){
            for(int i = 0; i < states.length; i++){
                if(value == states[i]){
                    buf.append("checked=\"checked\"");
                }
            }
        }
        buf.append(">");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���̃X�e�[�^�X�Z���N�g�{�b�N�X��HTML�𐶐�����B<p>
     *
     * @param name select�^�O��name����
     * @param id select�^�O��id����
     * @param states �I���ς�State
     * @return �X�P�W���[���̃X�e�[�^�X�Z���N�g�{�b�N�X��HTML
     */
    private String stateSelect(String name, String id, int state){
        final StringBuffer buf = new StringBuffer();
        buf.append("<select name=\"" + name + "\" id=\"" + id + "\" >");
        buf.append(option(Schedule.STATE_INITIAL, state, 0));
        buf.append(option(Schedule.STATE_END, state, 0));
        buf.append(option(Schedule.STATE_FAILED, state, 0));
        buf.append(option(Schedule.STATE_DISABLE, state, 0));
        buf.append("</select>");
        return buf.toString();
    }
    
    /**
     * �X�P�W���[���̃R���g���[���X�e�[�^�X�Z���N�g�{�b�N�X��HTML�𐶐�����B<p>
     *
     * @param name select�^�O��name����
     * @param id select�^�O��id����
     * @param states �I���ς�State
     * @return �X�P�W���[���̃R���g���[���X�e�[�^�X�Z���N�g�{�b�N�X��HTML
     */
    private String controlStateSelect(String name, String id, int state){
        final StringBuffer buf = new StringBuffer();
        buf.append("<select name=\"" + name + "\" id=\"" + id + "\" >");
        buf.append(option(Schedule.CONTROL_STATE_INITIAL, state, 1));
        buf.append(option(Schedule.CONTROL_STATE_PAUSE, state, 1));
        buf.append(option(Schedule.CONTROL_STATE_RESUME, state, 1));
        buf.append(option(Schedule.CONTROL_STATE_ABORT, state, 1));
        buf.append("</select>");
        return buf.toString();
    }
    
    /**
     * option�^�O��HTML������𐶐�����B<p>
     *
     * @param name option�^�O��name����
     * @param state �I���ς�State
     * @param mode 0:�X�e�[�^�X�A1:�R���g���[���X�e�[�^�X
     * @return option�^�O��HTML������
     */
    private String option(int value, int state, int mode){
        final StringBuffer buf = new StringBuffer();
        buf.append("<option value=\"" + value + "\"");
        if(value == state){
            buf.append(" selected");
        }
        buf.append(">");
        if(mode == 0){
            buf.append(getScheduleStateString(value));
        } else {
            buf.append(getScheduleControlStateString(value));
        }
        buf.append("</option>");
        
        return buf.toString();
    }
    
    /**
     * �I�u�W�F�N�g���t�H�[�}�b�g����B<p>
     * @param val �I�u�W�F�N�g
     * @return �t�H�[�}�b�g��̕�����
     */
    private String format(Object val){
        if(val == null){
            return "";
        }
        return val.toString();
    }
    
    /**
     * ��������t�H�[�}�b�g����B<p>
     * @param val ������
     * @return �t�H�[�}�b�g��̕�����
     */
    private String format(String val){
        if(val == null){
            return "";
        }
        return val;
    }
    
    /**
     * ���t���t�H�[�}�b�g����B<p>
     *
     * @param date ���t
     * @return �t�H�[�}�b�g��̕�����
     */
    private String formatDateTime(Date date){
        return format(date, "yyyyMMddHHmmssSSS");
    }
    
    /**
     * ���t���t�H�[�}�b�g����B<p>
     *
     * @param date ���t
     * @return �t�H�[�}�b�g��̕�����
     */
    private String formatTime(Date date){
        return format(date, "HHmmssSSS");
    }
    
    /**
     * ���t���t�H�[�}�b�g����B<p>
     *
     * @param date ���t
     * @return �t�H�[�}�b�g��̕�����
     */
    private String formatDate(Date date){
        return format(date, "yyyyMMdd");
    }
    
    /**
     * ���t���t�H�[�}�b�g����B<p>
     * @param date ���t
     * @return �t�H�[�}�b�g��̕�����
     */
    private String format(Date date, String format){
        if(date == null){
            return "";
        }
        return new SimpleDateFormat(format).format(date);
    }
    
    /**
     * ������z����t�H�[�}�b�g����B<p>
     * @param array �z��
     * @return �t�H�[�}�b�g��̕�����
     */
    private String format(String[] array){
        if(array == null || array.length == 0){
            return "";
        }
        StringArrayEditor editor = new StringArrayEditor();
        editor.setValue(array);
        return editor.getAsText();
    }
    
    /**
     * �X�P�W���[���̃X�e�[�^�X��������擾����B<p>
     *
     * @param state �X�e�[�^�X
     * @return �X�e�[�^�X������
     */
    private String getScheduleStateString(int state){
        switch(state){
        case Schedule.STATE_INITIAL:
            return padding("INITIAL", 7);
        case Schedule.STATE_ENTRY:
            return padding("ENTRY", 7);
        case Schedule.STATE_RUN:
            return padding("RUN", 7);
        case Schedule.STATE_END:
            return padding("END", 7);
        case Schedule.STATE_FAILED:
            return padding("FAILED", 7);
        case Schedule.STATE_PAUSE:
            return padding("PAUSE", 7);
        case Schedule.STATE_ABORT:
            return padding("ABORT", 7);
        case Schedule.STATE_RETRY:
            return padding("RETRY", 7);
        case Schedule.STATE_DISABLE:
            return padding("DISABLE", 7);
        default:
            return padding("UNKNOWN", 7);
        }
    }
    
    /**
     * �X�P�W���[���̃R���g���[���X�e�[�^�X��������擾����B<p>
     *
     * @param state �X�e�[�^�X
     * @return �X�e�[�^�X������
     */
    private String getScheduleControlStateString(int state){
        switch(state){
        case Schedule.CONTROL_STATE_INITIAL:
            return padding("INITIAL", 7);
        case Schedule.CONTROL_STATE_PAUSE:
            return padding("PAUSE", 7);
        case Schedule.CONTROL_STATE_RESUME:
            return padding("RESUME", 7);
        case Schedule.CONTROL_STATE_ABORT:
            return padding("ABORT", 7);
        case Schedule.CONTROL_STATE_FAILED:
            return padding("FAILED", 7);
        default:
            return padding("UNKNOWN", 7);
        }
    }
    
    /**
     * �X�P�W���[���̃`�F�b�N�X�e�[�^�X��������擾����B<p>
     *
     * @param state �X�e�[�^�X
     * @return �X�e�[�^�X������
     */
    private String getScheduleCheckStateString(int state){
        switch(state){
        case Schedule.CHECK_STATE_INITIAL:
            return "INITIAL";
        case Schedule.CHECK_STATE_TIMEOVER:
            return "TIMEOVER";
        default:
            return "UNKNOWN";
        }
    }
    
    /**
     * �w�茅�܂ŃX�y�[�X���߂����������ԋp����<p>
     *
     * @param val �Ώە�����
     * @param length ����
     * @return �X�y�[�X���ߌ�̕�����
     */
    private String padding(String val,int length){
        final StringBuffer buf = new StringBuffer();
        if(val != null){
            buf.append(val);
        }
        while(buf.length() < length){
            buf.append("&nbsp");
        }
        return buf.toString();
    }
}