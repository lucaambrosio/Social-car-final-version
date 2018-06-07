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

public class FrecciaTrasporti {
    Coordinata coord1;
    Coordinata coord2;
    private int ora;
    private int minuti;
    private int giorno;
    private int mese;
    private int anno;
    private int pesoNodo;
    private JsonNode root;
    private ObjectMapper mapper;
    Percorso p;
    private List<String> indicazioni;

    public FrecciaTrasporti(Nodo nodo, Nodo nodo1, int ora, int minuti, int giorno, int mese, int anno, int pesoNodo) {
        coord1 = nodo.getCoordinata();
        coord2 = nodo1.getCoordinata();
        this.ora = ora;
        this.minuti = minuti;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.pesoNodo = pesoNodo;
    }

    public void esegui() {
        creaUrl();
    }

    public int ritornaPesoFreccia(){
        if(root.path("status").asText().equals("OK"))
            return (int)(root.path("routes").get(0).path("legs").get(0).path("arrival_time").path("value").asLong() - calcolaTempo(ora,minuti,giorno,mese,anno,pesoNodo));
        return Integer.MAX_VALUE/10;
    }

    private void creaUrl() {
        String stringaUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        stringaUrl += coord1.getLat() + "," + coord1.getLong();
        stringaUrl += "&destination=";
        stringaUrl += coord2.getLat() + "," + coord2.getLong();
        //stringaUrl += "&key=AIzaSyDGrUwa-LTvsqqBWoFMsyvmKH4wyTMAi6Y";
        stringaUrl += "&key=AIzaSyAgE_8_SwfzeaTuvMn_EeDypjmYL29K_2A";
        stringaUrl += "&mode=transit";
        stringaUrl += "&departure_time=";
        stringaUrl += calcolaTempo(ora,minuti,giorno,mese,anno,pesoNodo);
        System.out.println(stringaUrl);
        System.out.println(calcolaTempo(ora,minuti,giorno,mese,anno,pesoNodo));
        URL url = null;
        String str="";
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
                str += s;
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        mapper = new ObjectMapper();
        try {
            root = mapper.readTree(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long calcolaTempo(int ora, int minuti, int giorno, int mese, int anno, int pesoNodo) {
        long tempo = 1514764800; //tempo passato fino al 2018 in secondi
        tempo += minuti * 60;
        tempo += ora * 3600;
        tempo += (giorno-1) * 3600 * 24;
        tempo += secondiMese(mese, anno, 0);
        tempo += secondiAnno(anno, 0);
        tempo += pesoNodo;
        tempo -= 7200; //aggiustamento ad ora locale
        return tempo;
    }

    private static long secondiAnno(int anno, int secondi) {
        if(anno == 2018)
            return secondi;
        secondi+= tempoAnno(anno);
        return secondiAnno(anno-1,secondi);
    }

    private static int tempoAnno(int anno) {
        if(annoBisestile(anno))
            return 3600 * 24 * 366;
        return 3600 * 24 * 365;
    }

    private static long secondiMese(int mese, int anno, int secondi) {
        if (mese == 1)
            return secondi;
        secondi += tempoMese(mese,anno);
        return secondiMese(mese-1, anno, secondi);
    }

    private static int tempoMese(int mese, int anno) {
        mese -= 1;
        if (mese == 2 && annoBisestile(anno))
            return 3600 * 24 * 29;
        if (mese == 2 && !annoBisestile(anno))
            return 3600 * 24 * 28;
        if (mese == 4 || mese == 6 || mese == 9 || mese == 11)
            return 3600 * 24 * 30;
        return 3600 * 24 * 31;
    }

    private static boolean annoBisestile(int anno) {
        if (anno % 4 != 0)
            return false;
        if (anno % 100 != 0)
            return true;
        if (anno % 400 == 0)
            return true;
        return false;
    }

    public void creaPercorso(){
        int temp=0;
        if(!root.path("status").asText().equals("OK"))
            return;
        List<Coordinata> l = new ArrayList<>();
        l.add(coord1);
        for(int i=0;root.path("routes").get(0).path("legs").get(0).path("steps").has(i);i++){
            double lat=root.path("routes").get(0).path("legs").get(0).path("steps").get(i).path("end_location").path("lat").asDouble();
            double lon=root.path("routes").get(0).path("legs").get(0).path("steps").get(i).path("end_location").path("lng").asDouble();
            Coordinata c = new Coordinata(lat,lon);
            l.add(c);
            temp=i;
        }
        p = new Percorso(l);
        indicazioni=new ArrayList<>();
        System.out.println(temp);
        for(int i=temp;i>=0;i--){
            indicazioni.add(root.path("routes").get(0).path("legs").get(0).path("steps").get(i).path("html_instructions").asText());
        }
        for(String i:indicazioni)
            System.out.println(i);
    }
    public Percorso getPercorso(){
        return p;
    }

    public long ritornaTempo(){
        return calcolaTempo(ora,minuti,giorno,mese,anno,pesoNodo);
    }

    public List<String> getIndicazioni() {
        return indicazioni;
    }
}
