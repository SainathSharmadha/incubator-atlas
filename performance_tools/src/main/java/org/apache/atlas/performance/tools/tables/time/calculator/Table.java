package org.apache.atlas.performance.tools.tables.time.calculator;

public class Table {

    int tableno;
    String guid, tableName, startTime, endTime;

    public int getTableno() {
        return tableno;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setTableno(int tableno) {
        this.tableno = tableno;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
