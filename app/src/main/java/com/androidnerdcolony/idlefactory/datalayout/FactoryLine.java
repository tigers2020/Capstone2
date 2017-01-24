package com.androidnerdcolony.idlefactory.datalayout;

/**
 * Created by tiger on 1/23/2017.
 */

public class FactoryLine {
    private int level;
    private double openCost;
    private double lineCost;
    private double workCapacity;
    private boolean isOpen;
    private boolean isWorking;
    private int configQuality;
    private int configTime;
    private int configValue;
    private double idleCash;
    private int lineNumber;

    public FactoryLine() {
    }

    public FactoryLine(int level, double openCost, double lineCost, double workCapacity, boolean isOpen, boolean isWorking, int configQuality, int configTime, int configValue, double idleCash, int lineNumber) {
        this.level = level;
        this.openCost = openCost;
        this.lineCost = lineCost;
        this.workCapacity = workCapacity;
        this.isOpen = isOpen;
        this.isWorking = isWorking;
        this.configQuality = configQuality;
        this.configTime = configTime;
        this.configValue = configValue;
        this.idleCash = idleCash;
        this.lineNumber = lineNumber;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getLineCost() {
        return lineCost;
    }

    public void setLineCost(double lineCost) {
        this.lineCost = lineCost;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getConfigQuality() {
        return configQuality;
    }

    public void setConfigQuality(int configQuality) {
        this.configQuality = configQuality;
    }

    public int getConfigTime() {
        return configTime;
    }

    public void setConfigTime(int configTime) {
        this.configTime = configTime;
    }

    public int getConfigValue() {
        return configValue;
    }

    public void setConfigValue(int configValue) {
        this.configValue = configValue;
    }

    public double getIdleCash() {
        return idleCash;
    }

    public void setIdleCash(double idleCash) {
        this.idleCash = idleCash;
    }

    public double getWorkCapacity() {
        return workCapacity;
    }

    public void setWorkCapacity(double workCapacity) {
        this.workCapacity = workCapacity;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public double getOpenCost() {
        return openCost;
    }

    public void setOpenCost(double openCost) {
        this.openCost = openCost;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }
}
