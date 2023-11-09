package edu.uga.cs.statequizapp;

public class State {

    private long id;
    private String name;
    private String capitalCity;
    private String secondCity;
    private String thirdCity;
    private int statehood;
    private int capitalSince;
    private int sizeRank;

    public State(String name, String capitalCity, String secondCity, String thirdCity,
                 int statehood, int capitalSince, int sizeRank) {
        this.name = name;
        this.capitalCity = capitalCity;
        this.secondCity = secondCity;
        this.thirdCity = thirdCity;
        this.statehood = statehood;
        this.capitalSince = capitalSince;
        this.sizeRank = sizeRank;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapitalCity() {
        return capitalCity;
    }

    public void setCapitalCity(String capitalCity) {
        this.capitalCity = capitalCity;
    }

    public String getSecondCity() {
        return secondCity;
    }

    public void setSecondCity(String secondCity) {
        this.secondCity = secondCity;
    }

    public String getThirdCity() {
        return thirdCity;
    }

    public void setThirdCity(String thirdCity) {
        this.thirdCity = thirdCity;
    }

    public int getStatehood() {
        return statehood;
    }

    public void setStatehood(int statehood) {
        this.statehood = statehood;
    }

    public int getCapitalSince() {
        return capitalSince;
    }

    public void setCapitalSince(int capitalSince) {
        this.capitalSince = capitalSince;
    }

    public int getSizeRank() {
        return sizeRank;
    }

    public void setSizeRank(int sizeRank) {
        this.sizeRank = sizeRank;
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capitalCity='" + capitalCity + '\'' +
                ", secondCity='" + secondCity + '\'' +
                ", thirdCity='" + thirdCity + '\'' +
                ", statehood=" + statehood +
                ", capitalSince=" + capitalSince +
                ", sizeRank=" + sizeRank +
                '}';
    }
}
