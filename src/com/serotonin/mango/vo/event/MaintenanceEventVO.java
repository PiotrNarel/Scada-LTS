package com.serotonin.mango.vo.event;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.maintenance.MaintenanceEventRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.timer.CronTimerTrigger;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import com.serotonin.web.taglib.DateFunctions;
import org.scada_lts.mango.service.MaintenanceEventService;
import org.scada_lts.utils.XidUtils;

@JsonRemoteEntity
public class MaintenanceEventVO implements ChangeComparable<MaintenanceEventVO>, JsonSerializable {
    public static final String XID_PREFIX = "ME_";

    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_HOURLY = 2;
    public static final int TYPE_DAILY = 3;
    public static final int TYPE_WEEKLY = 4;
    public static final int TYPE_MONTHLY = 5;
    public static final int TYPE_YEARLY = 6;
    public static final int TYPE_ONCE = 7;
    public static final int TYPE_CRON = 8;

    public static ExportCodes TYPE_CODES = new ExportCodes();
    static {
        TYPE_CODES.addElement(TYPE_MANUAL, "MANUAL", "maintenanceEvents.type.manual");
        TYPE_CODES.addElement(TYPE_HOURLY, "HOURLY", "maintenanceEvents.type.hour");
        TYPE_CODES.addElement(TYPE_DAILY, "DAILY", "maintenanceEvents.type.day");
        TYPE_CODES.addElement(TYPE_WEEKLY, "WEEKLY", "maintenanceEvents.type.week");
        TYPE_CODES.addElement(TYPE_MONTHLY, "MONTHLY", "maintenanceEvents.type.month");
        TYPE_CODES.addElement(TYPE_YEARLY, "YEARLY", "maintenanceEvents.type.year");
        TYPE_CODES.addElement(TYPE_ONCE, "ONCE", "maintenanceEvents.type.once");
        TYPE_CODES.addElement(TYPE_CRON, "CRON", "maintenanceEvents.type.cron");
    }

    private int id = Common.NEW_ID;
    private String xid;
    private int dataSourceId;
    @JsonRemoteProperty
    private String alias;
    private int alarmLevel = AlarmLevels.NONE;
    private int scheduleType = TYPE_MANUAL;
    @JsonRemoteProperty
    private boolean disabled = false;
    @JsonRemoteProperty
    private int activeYear;
    @JsonRemoteProperty
    private int activeMonth;
    @JsonRemoteProperty
    private int activeDay;
    @JsonRemoteProperty
    private int activeHour;
    @JsonRemoteProperty
    private int activeMinute;
    @JsonRemoteProperty
    private int activeSecond;
    @JsonRemoteProperty
    private String activeCron;
    @JsonRemoteProperty
    private int inactiveYear;
    @JsonRemoteProperty
    private int inactiveMonth;
    @JsonRemoteProperty
    private int inactiveDay;
    @JsonRemoteProperty
    private int inactiveHour;
    @JsonRemoteProperty
    private int inactiveMinute;
    @JsonRemoteProperty
    private int inactiveSecond;
    @JsonRemoteProperty
    private String inactiveCron;

    //
    //
    // Convenience data from data source
    //
    private int dataSourceTypeId;
    private String dataSourceName;
    private String dataSourceXid;

    public boolean isNew() {
        return id == Common.NEW_ID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public int getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(int scheduleType) {
        this.scheduleType = scheduleType;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getActiveYear() {
        return activeYear;
    }

    public void setActiveYear(int activeYear) {
        this.activeYear = activeYear;
    }

    public int getActiveMonth() {
        return activeMonth;
    }

    public void setActiveMonth(int activeMonth) {
        this.activeMonth = activeMonth;
    }

    public int getActiveDay() {
        return activeDay;
    }

    public void setActiveDay(int activeDay) {
        this.activeDay = activeDay;
    }

    public int getActiveHour() {
        return activeHour;
    }

    public void setActiveHour(int activeHour) {
        this.activeHour = activeHour;
    }

    public int getActiveMinute() {
        return activeMinute;
    }

    public void setActiveMinute(int activeMinute) {
        this.activeMinute = activeMinute;
    }

    public int getActiveSecond() {
        return activeSecond;
    }

    public void setActiveSecond(int activeSecond) {
        this.activeSecond = activeSecond;
    }

    public String getActiveCron() {
        return activeCron;
    }

    public void setActiveCron(String activeCron) {
        this.activeCron = activeCron;
    }

    public int getInactiveYear() {
        return inactiveYear;
    }

    public void setInactiveYear(int inactiveYear) {
        this.inactiveYear = inactiveYear;
    }

    public int getInactiveMonth() {
        return inactiveMonth;
    }

    public void setInactiveMonth(int inactiveMonth) {
        this.inactiveMonth = inactiveMonth;
    }

    public int getInactiveDay() {
        return inactiveDay;
    }

    public void setInactiveDay(int inactiveDay) {
        this.inactiveDay = inactiveDay;
    }

    public int getInactiveHour() {
        return inactiveHour;
    }

    public void setInactiveHour(int inactiveHour) {
        this.inactiveHour = inactiveHour;
    }

    public int getInactiveMinute() {
        return inactiveMinute;
    }

    public void setInactiveMinute(int inactiveMinute) {
        this.inactiveMinute = inactiveMinute;
    }

    public int getInactiveSecond() {
        return inactiveSecond;
    }

    public void setInactiveSecond(int inactiveSecond) {
        this.inactiveSecond = inactiveSecond;
    }

    public String getInactiveCron() {
        return inactiveCron;
    }

    public void setInactiveCron(String inactiveCron) {
        this.inactiveCron = inactiveCron;
    }

    public int getDataSourceTypeId() {
        return dataSourceTypeId;
    }

    public void setDataSourceTypeId(int dataSourceTypeId) {
        this.dataSourceTypeId = dataSourceTypeId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceXid() {
        return dataSourceXid;
    }

    public void setDataSourceXid(String dataSourceXid) {
        this.dataSourceXid = dataSourceXid;
    }

    public EventTypeVO getEventType() {
        return new EventTypeVO(EventType.EventSources.MAINTENANCE, id, 0, getDescription(), alarmLevel);
    }

    public LocalizableMessage getDescription() {
        LocalizableMessage message;

        if (!StringUtils.isEmpty(alias))
            message = new LocalizableMessage("common.default", alias);
        else if (scheduleType == TYPE_MANUAL)
            message = new LocalizableMessage("maintenanceEvents.schedule.manual", dataSourceName);
        else if (scheduleType == TYPE_ONCE) {
            message = new LocalizableMessage("maintenanceEvents.schedule.onceUntil", dataSourceName,
                    DateFunctions.getTime(new DateTime(activeYear, activeMonth, activeDay, activeHour, activeMinute,
                            activeSecond, 0).getMillis()), DateFunctions.getTime(new DateTime(inactiveYear,
                            inactiveMonth, inactiveDay, inactiveHour, inactiveMinute, inactiveSecond, 0).getMillis()));
        }
        else if (scheduleType == TYPE_HOURLY) {
            String activeTime = StringUtils.pad(Integer.toString(activeMinute), '0', 2) + ":"
                    + StringUtils.pad(Integer.toString(activeSecond), '0', 2);
            message = new LocalizableMessage("maintenanceEvents.schedule.hoursUntil", dataSourceName, activeTime,
                    StringUtils.pad(Integer.toString(inactiveMinute), '0', 2) + ":"
                            + StringUtils.pad(Integer.toString(inactiveSecond), '0', 2));
        }
        else if (scheduleType == TYPE_DAILY)
            message = new LocalizableMessage("maintenanceEvents.schedule.dailyUntil", dataSourceName, activeTime(),
                    inactiveTime());
        else if (scheduleType == TYPE_WEEKLY)
            message = new LocalizableMessage("maintenanceEvents.schedule.weeklyUntil", dataSourceName, weekday(true),
                    activeTime(), weekday(false), inactiveTime());
        else if (scheduleType == TYPE_MONTHLY)
            message = new LocalizableMessage("maintenanceEvents.schedule.monthlyUntil", dataSourceName, monthday(true),
                    activeTime(), monthday(false), inactiveTime());
        else if (scheduleType == TYPE_YEARLY)
            message = new LocalizableMessage("maintenanceEvents.schedule.yearlyUntil", dataSourceName, monthday(true),
                    month(true), activeTime(), monthday(false), month(false), inactiveTime());
        else if (scheduleType == TYPE_CRON)
            message = new LocalizableMessage("maintenanceEvents.schedule.cronUntil", dataSourceName, activeCron,
                    inactiveCron);
        else
            throw new ShouldNeverHappenException("Unknown schedule type: " + scheduleType);

        return message;
    }

    private LocalizableMessage getTypeMessage() {
        switch (scheduleType) {
        case TYPE_MANUAL:
            return new LocalizableMessage("maintenanceEvents.type.manual");
        case TYPE_HOURLY:
            return new LocalizableMessage("maintenanceEvents.type.hour");
        case TYPE_DAILY:
            return new LocalizableMessage("maintenanceEvents.type.day");
        case TYPE_WEEKLY:
            return new LocalizableMessage("maintenanceEvents.type.week");
        case TYPE_MONTHLY:
            return new LocalizableMessage("maintenanceEvents.type.month");
        case TYPE_YEARLY:
            return new LocalizableMessage("maintenanceEvents.type.year");
        case TYPE_ONCE:
            return new LocalizableMessage("maintenanceEvents.type.once");
        case TYPE_CRON:
            return new LocalizableMessage("maintenanceEvents.type.cron");
        }
        return null;
    }

    private String activeTime() {
        return StringUtils.pad(Integer.toString(activeHour), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(activeMinute), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(activeSecond), '0', 2);
    }

    private String inactiveTime() {
        return StringUtils.pad(Integer.toString(inactiveHour), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(inactiveMinute), '0', 2) + ":"
                + StringUtils.pad(Integer.toString(inactiveSecond), '0', 2);
    }

    private static final String[] weekdays = { "", "common.day.mon", "common.day.tue", "common.day.wed",
            "common.day.thu", "common.day.fri", "common.day.sat", "common.day.sun" };

    private LocalizableMessage weekday(boolean active) {
        int day = activeDay;
        if (!active)
            day = inactiveDay;
        return new LocalizableMessage(weekdays[day]);
    }

    private LocalizableMessage monthday(boolean active) {
        int day = activeDay;

        if (!active)
            day = inactiveDay;

        if (day == -3)
            return new LocalizableMessage("common.day.thirdLast");
        if (day == -2)
            return new LocalizableMessage("common.day.secondLastLast");
        if (day == -1)
            return new LocalizableMessage("common.day.last");
        if (day != 11 && day % 10 == 1)
            return new LocalizableMessage("common.counting.st", Integer.toString(day));
        if (day != 12 && day % 10 == 2)
            return new LocalizableMessage("common.counting.nd", Integer.toString(day));
        if (day != 13 && day % 10 == 3)
            return new LocalizableMessage("common.counting.rd", Integer.toString(day));
        return new LocalizableMessage("common.counting.th", Integer.toString(day));
    }

    private static final String[] months = { "", "common.month.jan", "common.month.feb", "common.month.mar",
            "common.month.apr", "common.month.may", "common.month.jun", "common.month.jul", "common.month.aug",
            "common.month.sep", "common.month.oct", "common.month.nov", "common.month.dec" };

    private LocalizableMessage month(boolean active) {
        int month = activeMonth;
        if (!active)
            month = inactiveMonth;
        return new LocalizableMessage(months[month]);
    }

    @Override
    public String getTypeKey() {
        return "event.audit.maintenanceEvent";
    }

    public void validate(DwrResponseI18n response) {

        MaintenanceEventService maintenanceEventService = new MaintenanceEventService();
        XidUtils.validateXid(response, maintenanceEventService::isXidUnique, xid, id);

        if (StringUtils.isLengthGreaterThan(alias, 50))
            response.addContextualMessage("alias", "maintenanceEvents.validate.aliasTooLong");

        if (dataSourceId <= 0)
            response.addContextualMessage("dataSourceId", "validate.invalidValue");

        // Check that cron patterns are ok.
        if (scheduleType == TYPE_CRON) {
            try {
                new CronTimerTrigger(activeCron);
            }
            catch (Exception e) {
                response.addContextualMessage("activeCron", "maintenanceEvents.validate.activeCron", e.getMessage());
            }

            try {
                new CronTimerTrigger(inactiveCron);
            }
            catch (Exception e) {
                response.addContextualMessage("inactiveCron", "maintenanceEvents.validate.inactiveCron", e.getMessage());
            }
        }

        // Test that the triggers can be created.
        MaintenanceEventRT rt = new MaintenanceEventRT(this);
        try {
            rt.createTrigger(true);
        }
        catch (RuntimeException e) {
            response.addContextualMessage("activeCron", "maintenanceEvents.validate.activeTrigger", e.getMessage());
        }

        try {
            rt.createTrigger(false);
        }
        catch (RuntimeException e) {
            response.addContextualMessage("inactiveCron", "maintenanceEvents.validate.inactiveTrigger", e.getMessage());
        }

        // If the event is once, make sure the active time is earlier than the inactive time.
        if (scheduleType == TYPE_ONCE) {
            try {
                DateTime adt = new DateTime(activeYear, activeMonth, activeDay, activeHour, activeMinute, activeSecond, 0);
                DateTime idt = new DateTime(inactiveYear, inactiveMonth, inactiveDay, inactiveHour, inactiveMinute,
                        inactiveSecond, 0);
                if (idt.getMillis() <= adt.getMillis())
                    response.addContextualMessage("scheduleType", "maintenanceEvents.validate.invalidRtn");
            } catch (Exception ex) {
                response.addContextualMessage("scheduleType", "maintenanceEvents.validate.invalidRtn");
            }
        }
        validateHour(response, activeHour, "activeHour");
        validateMinute(response, activeMinute, "activeMinute");
        validateSecond(response, activeSecond, "activeSecond");
        validateMonth(response, activeMonth, "activeMonth");
        validateYear(response, activeYear, "activeYear");
        validateDay(response, activeYear, activeMonth, activeDay, "activeDay");

        validateHour(response, inactiveHour, "inactiveHour");
        validateMinute(response, inactiveMinute, "inactiveMinute");
        validateSecond(response, inactiveSecond, "inactiveSecond");
        validateMonth(response, inactiveMonth, "inactiveMonth");
        validateYear(response, inactiveYear, "inactiveYear");
        validateDay(response, inactiveYear, inactiveMonth, inactiveDay, "inactiveDay");
    }

    @Override
    public void addProperties(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "common.xid", xid);
        AuditEventType.addPropertyMessage(list, "maintenanceEvents.dataSource", dataSourceId);
        AuditEventType.addPropertyMessage(list, "maintenanceEvents.alias", alias);
        AuditEventType.addPropertyMessage(list, "common.alarmLevel", AlarmLevels.getAlarmLevelMessage(alarmLevel));
        AuditEventType.addPropertyMessage(list, "maintenanceEvents.type", getTypeMessage());
        AuditEventType.addPropertyMessage(list, "common.disabled", disabled);
        AuditEventType.addPropertyMessage(list, "common.configuration", getDescription());
    }

    @Override
    public void addPropertyChanges(List<LocalizableMessage> list, MaintenanceEventVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid", from.xid, xid);
        AuditEventType.maybeAddPropertyChangeMessage(list, "maintenanceEvents.dataSource", from.dataSourceId,
                dataSourceId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "maintenanceEvents.alias", from.alias, alias);
        AuditEventType.maybeAddAlarmLevelChangeMessage(list, "common.alarmLevel", from.alarmLevel, alarmLevel);
        if (from.scheduleType != scheduleType)
            AuditEventType.addPropertyChangeMessage(list, "maintenanceEvents.type", from.getTypeMessage(),
                    getTypeMessage());
        AuditEventType.maybeAddPropertyChangeMessage(list, "common.disabled", from.disabled, disabled);
        if (from.activeYear != activeYear || from.activeMonth != activeMonth || from.activeDay != activeDay
                || from.activeHour != activeHour || from.activeMinute != activeMinute
                || from.activeSecond != activeSecond || from.activeCron != activeCron
                || from.inactiveYear != inactiveYear || from.inactiveMonth != inactiveMonth
                || from.inactiveDay != inactiveDay || from.inactiveHour != inactiveHour
                || from.inactiveMinute != inactiveMinute || from.inactiveSecond != inactiveSecond
                || from.inactiveCron != inactiveCron)
            AuditEventType.maybeAddPropertyChangeMessage(list, "common.configuration", from.getDescription(),
                    getDescription());
    }

    //
    //
    // Serialization
    //
    public void jsonSerialize(Map<String, Object> map) {
        map.put("xid", xid);
        map.put("dataSourceXid", dataSourceXid);
        map.put("alarmLevel", AlarmLevels.CODES.getCode(alarmLevel));
        map.put("scheduleType", TYPE_CODES.getCode(scheduleType));
    }

    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        String text = json.getString("dataSourceXid");
        if (text != null) {
            DataSourceVO<?> ds = new DataSourceDao().getDataSource(text);
            if (ds == null)
                throw new LocalizableJsonException("emport.error.maintenanceEvent.invalid", "dataSourceXid", text);
            dataSourceId = ds.getId();
        }

        text = json.getString("alarmLevel");
        if (text != null) {
            alarmLevel = AlarmLevels.CODES.getId(text);
            if (!AlarmLevels.CODES.isValidId(alarmLevel))
                throw new LocalizableJsonException("emport.error.maintenanceEvent.invalid", "alarmLevel", text,
                        AlarmLevels.CODES.getCodeList());
        }

        text = json.getString("scheduleType");
        if (text != null) {
            scheduleType = TYPE_CODES.getId(text);
            if (!TYPE_CODES.isValidId(scheduleType))
                throw new LocalizableJsonException("emport.error.maintenanceEvent.invalid", "scheduleType", text,
                        TYPE_CODES.getCodeList());
        }
    }

    private void validateYear(DwrResponseI18n response, int year, String contextKey) {
        if (year < 1) {
            response.addContextualMessage(contextKey, "validate.invalidValue");
        }
    }

    private void validateMonth(DwrResponseI18n response, int month, String contextKey) {
        if (month < 1 || month > 12) {
            response.addContextualMessage(contextKey, "validate.invalidValue");
        }
    }

    private void validateDay(DwrResponseI18n response, int year, int month, int day, String contextKey) {
        if(scheduleType == TYPE_MONTHLY || scheduleType == TYPE_YEARLY) {
               if (day != -1 && day != -2 && day != -3) {
                   doValidateDay(response, year, month, day, contextKey);
               }
        } else if(scheduleType == TYPE_WEEKLY) {
            if (day < 1 || day > 7) {
                response.addContextualMessage(contextKey, "validate.invalidValue");
            }
        } else if(scheduleType == TYPE_ONCE) {
            doValidateDay(response, year, month, day, contextKey);
        }
    }

    private void doValidateDay(DwrResponseI18n response, int year, int month, int day, String contextKey) {
        YearMonth date = YearMonth.of(year, month);
        if(!date.isValidDay(day)) {
            response.addContextualMessage(contextKey, "validate.invalidValue");
        }
    }

    private void validateHour(DwrResponseI18n response, int hour, String contextKey) {
        if (hour < 0 || hour > 23) {
            response.addContextualMessage(contextKey, "validate.invalidValue");
        }
    }

    private void validateMinute(DwrResponseI18n response, int minute, String contextKey) {
        if (minute < 0 || minute > 59) {
            response.addContextualMessage(contextKey, "validate.invalidValue");
        }
    }

    private void validateSecond(DwrResponseI18n response, int second, String contextKey) {
        if (second < 0 || second > 59) {
            response.addContextualMessage(contextKey, "validate.invalidValue");
        }
    }
}
