package ch.supsi.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Itinerario {
    private boolean trovato;
    private String da;
    private String a;
    private String jsonString = "";
    private Coordinata daCoord;
    List<Coordinata> listaCoordinate = new ArrayList<>();

    public Itinerario(String da, String a, Coordinata daCoord) {
        trovato=false;
        this.da = da;
        this.a = a;
        this.daCoord = daCoord;
        creaUrl();
        leggiDati();
    }

    private void leggiDati() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root == null) {
            return;
        }
        double lati;
        double longi;
        System.out.println(jsonString);
        listaCoordinate.add(daCoord);
        if(!root.path("routes").has(0)){
            return;
        }
        for (int i = 0; root.path("routes").get(0).path("legs").get(0).path("steps").has(i); i++) {
            lati = root.path("routes").get(0).path("legs").get(0).path("steps").get(i).path("end_location").get("lat").asDouble();
            longi = root.path("routes").get(0).path("legs").get(0).path("steps").get(i).path("end_location").get("lng").asDouble();
            System.out.println("Punto" + i + ":");
            System.out.println(lati);
            System.out.println(longi);
            listaCoordinate.add(new Coordinata(lati, longi));
        }
        for (Coordinata c : listaCoordinate) {
            System.out.println();
            c.scriviCoordinata();
        }
        trovato=true;
    }

    private void creaUrl() {
        String stringaUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        stringaUrl += da;
        stringaUrl += "&destination=";
        stringaUrl += a;
        stringaUrl += "&key=AIzaSyDGrUwa-LTvsqqBWoFMsyvmKH4wyTMAi6Y";
        System.out.println(stringaUrl);
        URL url = null;
        try {
            url = new URL(stringaUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null)
            return;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s;
            while ((s = reader.readLine()) != null)
                jsonString += s;
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
    }

    public List<Coordinata> getListaCoordinate() {
        return listaCoordinate;
    }

    public boolean trattaPresente(){
        return trovato;
    }
}
