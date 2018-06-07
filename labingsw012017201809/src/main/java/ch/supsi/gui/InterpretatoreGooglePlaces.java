package ch.supsi.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InterpretatoreGooglePlaces {
    private static Lock lock = new ReentrantLock(); //public
    private static Set<NodoGooglePlaces> pubblicoSet = new HashSet<>(); //public
    private Nodo partenza, arrivo;
    String stringaUrl;
    URL url;
    String dati = "";
    private JsonNode root;
    private ObjectMapper mapper;
    private double stepLat;
    private double stepLong;
    private double raggioPerc;
    private int numeroCerchi;

    /*public void getInfo(Nodo nodo, int raggio, String token) {
        creaUrl(nodo, raggio, token);
        leggiDati();
    }


    public void creaUrl(Nodo nodo, int raggio, String token) {
        stringaUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?&key=AIzaSyBg-ppSAJThP3AlRPErTL0MdKVx5Z2W6Vw";
        stringaUrl += "&location=" + nodo.getCoordinata().getLat() + "," + nodo.getCoordinata().getLong();
        stringaUrl += "&radius=" + raggio; //raggio in metri
        stringaUrl += "&type=city_hall";
        if (!token.equals(""))
            stringaUrl += "&pagetoken=" + token;
        try {
            url = new URL(stringaUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void leggiDati() {
        BufferedReader reader;
        dati = "";
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s;
            while ((s = reader.readLine()) != null)
                dati += s;
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        mapper = new ObjectMapper();
        try {
            root = mapper.readTree(dati);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("NOO");
        }

    }*/

    /*public double distanza(double lat1, double lon1, double lat2, double lon2) {  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }*/

    public InterpretatoreGooglePlaces(Nodo n1, Nodo n2) {
        partenza = n1;
        arrivo = n2;
        calcolaDistanzaCoordinate();
    }

    private void calcolaDistanzaCoordinate() {
        double latPartenza = partenza.getCoordinata().getLat();
        double lngPartenza = partenza.getCoordinata().getLong();
        double latArrivo = arrivo.getCoordinata().getLat();
        double lngArrivo = arrivo.getCoordinata().getLong();

        double diffLatitudine = latArrivo - latPartenza;
        double diffLongitudine = lngArrivo - lngPartenza;
        //raggioPerc = 2000 + distanza(latPartenza, lngPartenza, latArrivo, lngArrivo) / 10;
        if (raggioPerc < 48000)
            numeroCerchi = 15;
        else
            numeroCerchi = (int) raggioPerc / 3000;    //almeno un cerchio ogni 3000*10 = 30.000 metri

        stepLat = diffLatitudine / (numeroCerchi - 1);
        stepLong = diffLongitudine / (numeroCerchi - 1);
        System.out.println(raggioPerc);
    }

    public Set<NodoGooglePlaces> listaCittà() {
        ArrayList<Thread> threads = new ArrayList<>();

        Nodo temp;
        double newLat;
        double newLong;

        /*
        for (int i = 0; i < numeroCerchi; i++) {
            newLat = i * stepLat + partenza.getCoordinata().getLat();
            newLong = i * stepLong + partenza.getCoordinata().getLong();
            temp = new Nodo(new Coordinata(newLat, newLong));
            set.addAll(prendiSetDaNodo(temp, (int) raggioPerc, ""));
        }*/

        for (int i = 0; i < numeroCerchi; i++) {
            newLat = i * stepLat + partenza.getCoordinata().getLat();
            newLong = i * stepLong + partenza.getCoordinata().getLong();
            temp = new Nodo(new Coordinata(newLat, newLong));
            threads.add( new Thread(new ThreadCercatore(temp, (int) raggioPerc,2)));
        }

        for (int i = 0; i < numeroCerchi; i++) {
            newLat = i * stepLat + partenza.getCoordinata().getLat();
            newLong = i * stepLong + partenza.getCoordinata().getLong();
            temp = new Nodo(new Coordinata(newLat, newLong));
            threads.add( new Thread(new ThreadCercatore(temp, (int) raggioPerc,3)));
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(Thread t: threads)
            t.start();

        for(Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int i = 0;
        for (NodoGooglePlaces n : pubblicoSet) {
            //n.scriviNodoGoogle();
            i++;
        }
        System.out.println(i);

        return pubblicoSet;
    }

    /*public Set<NodoGooglePlaces> prendiSetDaNodo(Nodo nodo, int raggio, String token) {
        Set<NodoGooglePlaces> set = new HashSet<>();
        double lat;
        double lng;
        String id;
        String nome;
        String place_id;
        NodoGooglePlaces città;

        getInfo(nodo, raggio, token);
        System.out.println(stringaUrl);
        System.out.println(dati);

        for (int i = 0; root.path("results").has(i); i++) {
            lat = root.path("results").get(i).path("geometry").path("location").path("lat").asDouble();
            lng = root.path("results").get(i).path("geometry").path("location").path("lng").asDouble();
            id = root.path("results").get(i).path("id").asText();
            nome = root.path("results").get(i).path("name").asText();
            place_id = root.path("results").get(i).path("place_id").asText();
            città = new NodoGooglePlaces(new Coordinata(lat, lng), id, nome, place_id);
            set.add(città);
        }
        //per il next token serve fare un thread.sleep, altrimenti dice che il link non è valido
        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        /*
        if(root.path("next_page_token").asText().equals("")){
            System.out.println("eeh sì, guarda qua: "+root.path("next_page_token").asText());
            return set;
        }
        set.addAll(prendiSetDaNodo(nodo,raggio,root.path("next_page_token").asText()));
        return set;
    }*/

}
