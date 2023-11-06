package edu.uga.cs.statequizapp;

public class State {
    private long id;
    private String stateName;
    private String capitalCity;
    private String secondCity;
    private String thirdCity;
    private int statehoodYear;
    private String capitalSince;
    private int sizeRank;

    // Constructors
    public State( String stateName, String capitalCity, String secondCity, String thirdCity, int statehoodYear, String capitalSince, int sizeRank ) {
        this.id = -1;
        this.stateName = stateName;
        this.capitalCity = capitalCity;
        this.secondCity = secondCity;
        this.thirdCity = thirdCity;
        this.statehoodYear = statehoodYear;
        this.capitalSince = capitalSince;
    }

    //getters
    public long getId() {return this.id;}
    public String getStateName() {return this.stateName;}
    public String getCapitalCity() {return this.capitalCity;}
    public String getSecondCity() {return this.secondCity;}
    public String getThirdCity() {return this.thirdCity;}
    public int getStatehoodYear() {return this.statehoodYear;}
    public String getCapitalSince() {return this.capitalSince;}
    public int getSizeRank() {return sizeRank;}

    //setters
    public void setId(long id) {this.id = id;}
    public void setStateName(String stateName) {this.stateName = stateName;}
    public void setCapitalCity(String capitalCity) {this.capitalCity = capitalCity;}
    public void setSecondCity(String secondCity) {this.secondCity = secondCity;}
    public void setThirdCity(String thirdCity) {this.thirdCity = thirdCity;}
    public void setCapitalSince(String capitalSince) {this.capitalSince = capitalSince;}
    public void setSizeRank(int sizeRank) {this.sizeRank = sizeRank;}
    public void setStatehoodYear(int statehoodYear) {this.statehoodYear = statehoodYear;}
}
