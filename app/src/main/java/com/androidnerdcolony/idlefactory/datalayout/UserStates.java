package com.androidnerdcolony.idlefactory.datalayout;

/**
 * Created by tiger on 1/23/2017.
 */

public class UserStates{
    private double balance;
    private double prestige;
    private int level;
    private double exp;

    public UserStates() {
    }

    public UserStates(double balance, double prestige, int level, double exp) {
        this.balance = balance;
        this.prestige = prestige;
        this.level = level;
        this.exp = exp;
    }

    public double getBalance() {
        return balance;
    }


    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getPrestige() {
        return prestige;
    }

    public void setPrestige(double prestige) {
        this.prestige = prestige;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }
}
