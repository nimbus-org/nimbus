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
package jp.ossc.nimbus.service.scheduler;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

import jp.ossc.nimbus.beans.BeanTable;
import jp.ossc.nimbus.core.ServiceBase;
import jp.ossc.nimbus.core.ServiceName;
import jp.ossc.nimbus.core.ServiceManagerFactory;
import jp.ossc.nimbus.service.system.Time;

/**
 * Googleカレンダーイベント日付評価。<p>
 * 以下に指定方法を示す。<br>
 * イベント全体 : EVENT<br>
 * Summary指定 : SUMMARY=任意の文字列<br>
 * EventType指定 : EVENT_TYPE=任意の文字列<br>
 *
 * @author M.Takata
 */
public class GoogleCalendarEventDateEvaluatorService extends ServiceBase implements DateEvaluator, GoogleCalendarEventDateEvaluatorServiceMBean{
    
    private String applicationName;
    private String credentialsFilePath;
    private List scopes;
    private List calendarIds;
    private int maxResults = 0;
    private ServiceName timeServiceName;
    private boolean isTimeMinFromNow = true;
    private int timeMaxDays;
    private String query;
    
    private com.google.api.services.calendar.Calendar calendar;
    private Time time;
    private BeanTable eventList;
    
    public void setApplicationName(String name){
        applicationName = name;
    }
    public String getApplicationName(){
        return applicationName;
    }
    
    public void setCredentialsFilePath(String path){
        credentialsFilePath = path;
    }
    public String getCredentialsFilePath(){
        return credentialsFilePath;
    }
    
    public void setScopes(String[] scopes) throws IllegalArgumentException{
        if(scopes == null || scopes.length == 0){
            throw new IllegalArgumentException("scopes is empty.");
        }
        this.scopes.clear();
        if(scopes != null){
            for(int i = 0; i < scopes.length; i++){
                this.scopes.add(scopes[i]);
            }
        }
    }
    public String[] getScopes(){
        return scopes == null ? null : (String[])scopes.toArray(new String[scopes.size()]);
    }
    
    public void setCalendarIds(String[] ids) throws IllegalArgumentException{
        if(ids == null || ids.length == 0){
            throw new IllegalArgumentException("CalendarIds is empty.");
        }
        calendarIds.clear();
        if(ids != null){
            for(int i = 0; i < ids.length; i++){
                calendarIds.add(ids[i]);
            }
        }
    }
    public String[] getCalendarIds(){
        return calendarIds == null ? null : (String[])calendarIds.toArray(new String[calendarIds.size()]);
    }
    
    public void setMaxResults(int max){
        maxResults = max;
    }
    public int getMaxResults(){
        return maxResults;
    }
    
    public void setTimeMinFromNow(boolean isNow){
        isTimeMinFromNow = isNow;
    }
    public boolean isTimeMinFromNow(){
        return isTimeMinFromNow;
    }
    
    public void setTimeMaxDays(int days){
        timeMaxDays = days;
    }
    public int getTimeMaxDays(){
        return timeMaxDays;
    }
    
    public void setQuery(String query){
        this.query = query;
    }
    public String getQuery(){
        return query;
    }
    
    public void setTimeServiceName(ServiceName name){
        timeServiceName = name;
    }
    public ServiceName getTimeServiceName(){
        return timeServiceName;
    }
    
    public void createService() throws Exception{
        scopes = new ArrayList();
        scopes.add(CalendarScopes.CALENDAR_EVENTS_READONLY);
        calendarIds = new ArrayList();
        calendarIds.add("primary");
    }
    
    public void startService() throws Exception{
        
        if(applicationName == null){
            applicationName = getServiceNameObject() == null ? getServiceName() : getServiceNameObject().toString();
        }
        
        if(credentialsFilePath == null){
            throw new IllegalArgumentException("CredentialsFilePath is null.");
        }
        
        if(timeServiceName != null){
            time = (Time)ServiceManagerFactory.getServiceObject(timeServiceName);
        }
        
        InputStream is = GoogleCalendarEventDateEvaluatorService.class.getResourceAsStream(credentialsFilePath);
        if(is == null){
            File file = new File(credentialsFilePath);
            if(!file.exists()){
                throw new FileNotFoundException("CredentialsFile not found: " + credentialsFilePath);
            }
            is = new FileInputStream(file);
        }
        
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        
        GoogleCredential tmpCredential = GoogleCredential.fromStream(is);
        
        Credential credential = new GoogleCredential.Builder()
            .setTransport(httpTransport)
            .setJsonFactory(jsonFactory)
            .setServiceAccountId(tmpCredential.getServiceAccountId())
            .setServiceAccountPrivateKey(tmpCredential.getServiceAccountPrivateKey())
            .setServiceAccountScopes(scopes)
            .build();
        
        calendar = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(applicationName)
            .build();
        
        reload();
    }
    
    public void destroyService(){
        scopes = null;
        calendarIds = null;
    }
    
    public void reload() throws Exception{
        final long currentTime = time == null ? System.currentTimeMillis() : time.currentTimeMillis();
        DateTime timeMin = null;
        if(isTimeMinFromNow){
            timeMin = new DateTime(currentTime);
        }
        final DateTime now = new DateTime(currentTime);
        DateTime timeMax = null;
        if(timeMaxDays > 0){
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(currentTime);
            cal.add(Calendar.DAY_OF_YEAR, timeMaxDays);
            timeMax = new DateTime(cal.getTimeInMillis());
        }
        BeanTable newEventList = new BeanTable(Event.class);
        newEventList.setIndex("SUMMARY", new String[]{"summary"});
        newEventList.setIndex("EVENT_TYPE", new String[]{"eventType"});
        
        Iterator ids = calendarIds.iterator();
        while(ids.hasNext()){
            com.google.api.services.calendar.Calendar.Events.List list = calendar.events().list((String)ids.next())
                .setOrderBy("startTime")
                .setSingleEvents(true);
            if(maxResults > 0){
                list = list.setMaxResults(new Integer(maxResults));
            }
            if(timeMin != null){
                list = list.setTimeMin(timeMin);
            }
            if(timeMax != null){
                list = list.setTimeMax(timeMax);
            }
            if(query != null){
                list = list.setQ(query);
            }
            Iterator events = list.execute().getItems().iterator();
            while(events.hasNext()){
                newEventList.add((Event)events.next());
            }
        }
        eventList = newEventList;
    }
    
    public List getEventList(){
        return eventList == null ? null : new ArrayList(eventList);
    }
    
    public boolean equalsDate(String key, Calendar cal) throws Exception{
        if(eventList == null){
            return false;
        }
        Calendar work = Calendar.getInstance();
        if("EVENT".equals(key)){
            Iterator events = eventList.iterator();
            while(events.hasNext()){
                Event event = (Event)events.next();
                work.setTimeInMillis(event.getStart().getDate().getValue());
                if(cal.get(Calendar.YEAR) == work.get(Calendar.YEAR)
                    && cal.get(Calendar.DAY_OF_YEAR) == work.get(Calendar.DAY_OF_YEAR)){
                    return true;
                }else if(cal.get(Calendar.YEAR) < work.get(Calendar.YEAR)
                    || (cal.get(Calendar.YEAR) == work.get(Calendar.YEAR)
                        && cal.get(Calendar.DAY_OF_YEAR) < work.get(Calendar.DAY_OF_YEAR))
                ){
                    break;
                }
            }
        }else{
            Iterator events = null;
            if(key.startsWith("SUMMARY=")){
                events = eventList.createView()
                    .searchBy(key.substring("SUMMARY=".length()), "SUMMARY", null)
                    .getResultList()
                    .iterator();
            }
            if(key.startsWith("EVENT_TYPE=")){
                events = eventList.createView()
                    .searchBy(key.substring("EVENT_TYPE=".length()), "EVENT_TYPE", null)
                    .getResultList()
                    .iterator();
            }
            if(events != null){
                while(events.hasNext()){
                    Event event = (Event)events.next();
                    work.setTimeInMillis(event.getStart().getDate().getValue());
                    if(cal.get(Calendar.YEAR) == work.get(Calendar.YEAR)
                        && cal.get(Calendar.DAY_OF_YEAR) == work.get(Calendar.DAY_OF_YEAR)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}