package ch.supsi.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ThreadCercatore implements Runnable {
    Nodo node;
    int ragg;
    String stringaUrl;
    URL url;
    String dati = "";
    private JsonNode root;
    private ObjectMapper mapper;
    private int numero;

    public ThreadCercatore(Nodo node, int ragg, int numero) {
        this.node = node;
        this.ragg = ragg;
        this.numero=numero;
    }

    @Override
    public void run() {
        prendiSetDaNodo(node,ragg,"");
    }

    public void getInfo(Nodo nodo, int raggio, String token) {
        creaUrl(nodo, raggio, token);
        leggiDati();
    }

    public void leggiDati() {
        BufferedReader reader;
        dati = "";
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String s;
            if(reader==null)
                return;
            while ((s = reader.readLine()) != null)
                dati += s;
        } catch (IOException e) {
            System.out.println("IO Exception reader in Thread Cercatore");
            System.out.println("StringaURL= "+stringaUrl);
            System.out.println("URL: "+url);
            e.printStackTrace();
            root=null;
            System.out.println(dati);
            return;
        }
        mapper = new ObjectMapper();
        try {
            root = mapper.readTree(dati);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("NOO");
        }

    }

    public void creaUrl(Nodo nodo, int raggio, String token) {
        stringaUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?&key=AIzaSyBg-ppSAJThP3AlRPErTL0MdKVx5Z2W6Vw";
        stringaUrl += "&location=" + nodo.getCoordinata().getLat() + "," + nodo.getCoordinata().getLong();
        stringaUrl += "&radius=" + raggio; //raggio in metri
        stringaUrl += "&type=administrative_area_level_"+numero;
        if (!token.equals(""))
            stringaUrl += "&pagetoken=" + token;
        try {
            url = new URL(stringaUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Set<NodoGooglePlaces> prendiSetDaNodo(Nodo nodo, int raggio, String token) {
        Set<NodoGooglePlaces> set = new HashSet<>();
        double lat;
        double lng;
        String id;
        String nome;
        String place_id;
        NodoGooglePlaces città;

        getInfo(nodo, raggio, token);
        if(root==null)
            return set;
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
        IlCercatore.lock.lock();
        try
        {
            IlCercatore.pubblicoSet.addAll(set);
        }
        finally {
            IlCercatore.lock.unlock();
        }
        return set;
    }
}
