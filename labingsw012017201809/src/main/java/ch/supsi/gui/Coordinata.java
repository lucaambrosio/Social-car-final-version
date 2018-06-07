package ch.supsi.gui;

import java.util.Objects;

public class Coordinata {
    private double latitudine;
    private double longitudine;

    public Coordinata(double latitudine, double longitudine) {
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    public void scriviCoordinata() {
        System.out.print("latitudine: "+latitudine+" longitudine: "+longitudine);
    }

    public double getLong() {
        return longitudine;
    }

    public double getLat() {
        return latitudine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinata that = (Coordinata) o;
        return Double.compare(that.latitudine, latitudine) == 0 &&
                Double.compare(that.longitudine, longitudine) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(latitudine, longitudine);
    }

    public String getCoordinata() {
        String str="";
        str+=("latitudine: "+latitudine+" longitudine: "+longitudine+"\n");
        return str;
    }
}
