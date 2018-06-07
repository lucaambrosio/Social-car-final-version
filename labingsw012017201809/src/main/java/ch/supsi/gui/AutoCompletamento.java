package ch.supsi.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.StringProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class AutoCompletamento {
    Coordinata coordinata;
    String[] nome;
    JsonNode json;
    boolean riuscito = false;

    public AutoCompletamento(StringProperty stringProperty) {
        String posto = stringProperty.getValue().toLowerCase();
        posto.trim();
        posto = splitta(posto);
        System.out.println(posto);
        trovaCity(posto);
        if(riuscito)
            trovaCoordinate();
        else
            coordinata = null;
    }

    private String splitta(String posto) {
        String[] postoTemp = posto.split(" ");
        posto="";
        for(int i=0;i<postoTemp.length;i++){
            if(postoTemp[i].equals(""))
                continue;
            if(i>0 && !posto.equals(""))
                posto+="+";
            posto+=postoTemp[i];
        }
        return posto;
    }

    private void trovaCity(String posto) {
        String stringUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
        stringUrl += posto;
        //stringUrl += "&types=geocode&language=it&key=AIzaSyBg-ppSAJThP3AlRPErTL0MdKVx5Z2W6Vw";
        stringUrl += "&types=geocode&language=it&key=AIzaSyAgE_8_SwfzeaTuvMn_EeDypjmYL29K_2A";
        elaboraUrl(stringUrl);
        String temp = null;
        try {
            temp = json.path("predictions").get(0).path("description").asText();
        }
        catch(Exception e){
            System.out.println("Ellole 3");
        }
        if(temp!= null){
            nome = temp.split(",");
            riuscito = true;
        }
        else
            nome=null;
    }

    private void trovaCoordinate() {
        String stringUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        for(int i=0;i<nome.length;i++){
            if(i>0)
                stringUrl+=",";
            stringUrl+=splitta((nome[i].trim()));
        }
        //stringUrl += "&key=AIzaSyCsVOg9RMHnZ09J_Qhe6f4tjeG3bn4Pvnk";
        //stringUrl += "&key=AIzaSyBg-ppSAJThP3AlRPErTL0MdKVx5Z2W6Vw";
        stringUrl += "&key=AIzaSyAgE_8_SwfzeaTuvMn_EeDypjmYL29K_2A";
        elaboraUrl(stringUrl);
        //System.out.println(stringUrl);
        double lati=1000;
        double longi=1000;
        try {
            lati = json.path("results").get(0).path("geometry").path("location").path("lat").asDouble();
            longi = json.path("results").get(0).path("geometry").path("location").path("lng").asDouble();
        }
        catch(Exception e){
            System.out.println("Ellole");
        }
        coordinata = new Coordinata(lati,longi);
    }

    private void elaboraUrl(String stringUrl){
        try {
            URL url = new URL(stringUrl);
            BufferedReader reader;
            String dati = "";
            try {
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String s;
                while ((s = reader.readLine()) != null)
                    dati += s;
            } catch (IOException e) {
                System.out.println("IO Exceptionx");
                System.out.println(stringUrl);
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(dati);
                json = root;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("NOO");
            }
        } catch (MalformedURLException e) {
            System.out.println("Ellole 2");
            e.printStackTrace();
        }
    }

    Coordinata prendiCoordinate() {
        return coordinata;
    }

    String nomeCity() {
        String str="";
        if (nome == null) return "non riconosciuto...";
        else{
            for(int i=0;i<nome.length;i++){
                if(i>0)
                    str+=",";
                str+=splitta((nome[i].trim()));
            }
            return str;
        }
    }
}
