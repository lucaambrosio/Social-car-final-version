package ch.supsi.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FrecciaBlaBla {
    private JsonNode root;
    private ObjectMapper mapper;
    private List<ViaggioBlaBlaCar> viaggi = new ArrayList<>();
    private URL url;
    private Nodo partenza;
    private Nodo arrivo;
    private int ora;
    private int minuti;
    private int giorno;
    private int mese;
    private int anno;
    private int numeroViaggi = -1;
    private long pesoViaggioPiùBreve = Integer.MAX_VALUE / 100;
    private int pesoNodo;
    private ViaggioBlaBlaCar min;
    private List<String> indicazioni;

    public FrecciaBlaBla(Nodo nodo, Nodo nodo1, int ora, int minuti, int giorno, int mese, int anno, int pesoNodo) {
        partenza = nodo;
        arrivo = nodo1;
        this.ora = ora;
        this.minuti = minuti;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.pesoNodo = pesoNodo;
        creaUrl();
    }

    private void creaUrl() {
        String from;
        String to;
        String formato;
        String valuta;
        String local;
        String limite;
        from = "&fc=" + partenza.getCoordinata().getLat() + "," + partenza.getCoordinata().getLong();
        to = "&tc=" + arrivo.getCoordinata().getLat() + "," + arrivo.getCoordinata().getLong();
        formato = "&_format=json";
        valuta = "&cur=EUR";
        local = "&locale=it_IT";
        limite = "&limit=1000000";
        String str = "https://public-api.blablacar.com/api/v2/trips?key=f762695b281c4672abd2df1c40878afb";
        //String str = "https://public-api.blablacar.com/api/v2/trips?key=AIzaSyAgE_8_SwfzeaTuvMn_EeDypjmYL29K_2A";
        str += from + to + formato + valuta + local + limite;
        System.out.println(str);
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void interpreta() throws IOException {
        mapper = new ObjectMapper();
        root = mapper.readTree(url);
        numeroViaggi = root.path("pager").path("total").asInt();
        prendiViaggi();
    }

    private void prendiViaggi() {
        if (numeroViaggi == -1) {
            System.out.println("Errore n. viaggi");
            return;
        }
        String paginaAnnuncio;
        String dataOraPartenza;
        NodoViaggioBlaBlaCar luogoPartenza;
        NodoViaggioBlaBlaCar luogoArrivo;
        double prezzo;
        String moneta;
        String simboloMoneta;
        double commissione;
        int postiRimasti;
        int postiTotali;
        double durata;
        double distanza;
        Auto auto;

        for (int i = 0; i < numeroViaggi; i++) {
            paginaAnnuncio = root.path("trips").get(i).path("links").path("_front").asText();
            dataOraPartenza = root.path("trips").get(i).path("departure_date").asText();
            luogoPartenza = TrovaLuogo(true, i);
            luogoArrivo = TrovaLuogo(false, i);
            prezzo = root.path("trips").get(i).path("price").path("value").asDouble();
            moneta = root.path("trips").get(i).path("price").path("currency").asText();
            simboloMoneta = root.path("trips").get(i).path("price").path("symbol").asText();
            commissione = root.path("trips").get(i).path("commission").path("value").asDouble();
            postiRimasti = root.path("trips").get(i).path("seats_left").asInt();
            postiTotali = root.path("trips").get(i).path("seats").asInt();
            durata = root.path("trips").get(i).path("duration").path("value").asInt();
            distanza = root.path("trips").get(i).path("distance").path("value").asInt();
            auto = TrovaAuto(i);
            viaggi.add(new ViaggioBlaBlaCar(paginaAnnuncio, dataOraPartenza, luogoPartenza, luogoArrivo, prezzo, moneta, simboloMoneta, commissione, postiRimasti, postiTotali, durata, distanza, auto));
        }
    }

    private Auto TrovaAuto(int posArr) {
        int id;
        String marca;
        String modello;
        String comfort;
        int stelleComfort;

        id = root.path("trips").get(posArr).path("car").path("id").asInt();
        marca = root.path("trips").get(posArr).path("car").path("make").asText();
        modello = root.path("trips").get(posArr).path("car").path("model").asText();
        comfort = root.path("trips").get(posArr).path("car").path("comfort").asText();
        stelleComfort = root.path("trips").get(posArr).path("car").path("comfort_nb_star").asInt();
        //se l'id = 0 l'auto non è stata impostata
        return new Auto(id, marca, modello, comfort, stelleComfort);
    }

    private NodoViaggioBlaBlaCar TrovaLuogo(boolean partenza, int posArray) {

        String partenzaArrivo;
        if (partenza)
            partenzaArrivo = "departure_place";
        else
            partenzaArrivo = "arrival_place";

        String nome_città;
        String indirizzo;
        Coordinata coordinata;
        String codice_paese;

        nome_città = root.path("trips").get(posArray).path(partenzaArrivo).path("city_name").asText();
        indirizzo = root.path("trips").get(posArray).path(partenzaArrivo).path("address").asText();
        double lat = root.path("trips").get(posArray).path(partenzaArrivo).path("latitude").asDouble();
        double lon = root.path("trips").get(posArray).path(partenzaArrivo).path("longitude").asDouble();
        coordinata = new Coordinata(lat, lon);
        codice_paese = root.path("trips").get(posArray).path(partenzaArrivo).path("country_code").asText();

        return new NodoViaggioBlaBlaCar(nome_città, indirizzo, coordinata, codice_paese);
    }

    public int getPesoViaggioPiùBreve() {
        return (int) pesoViaggioPiùBreve;
    }

    public void trovaViaggioBreve() {
        long tempoAlNodo = FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, pesoNodo);
        System.out.println(numeroViaggi);
        if (numeroViaggi > 0) {
            int i = 0;
            while (min == null) {
                if (numeroViaggi == i) {
                    System.out.println("min==i");
                    return;
                }
                viaggi.get(i).smista();
                System.out.println("Val numerico data/ora: " + viaggi.get(i).getValoreNumericoDataOra());
                System.out.println("Val numerico tempo al nodo: " + tempoAlNodo);
                if (viaggi.get(i).getValoreNumericoDataOra() >= tempoAlNodo) {
                    System.out.println("nuovo minimo");
                    min = viaggi.get(i);
                    break;
                }
                i++;
            }
        } else
            return;
        for (ViaggioBlaBlaCar v : viaggi) {
            v.smista();
            if (v.getValoreNumericoDataOra() < tempoAlNodo) {
                System.out.println("Parte troppo presto");
                continue;
            }
            if (v.getValoreNumericoDataOra() + v.getDurata() < min.getValoreNumericoDataOra() + min.getDurata()) {
                System.out.println("Trovato nuovo min!" + (min.getValoreNumericoDataOra() + (int) min.getDurata() - tempoAlNodo));
                min = v;
            }
            //System.out.println(v.getValoreNumericoDataOra());
        }
        if (min != null)
            pesoViaggioPiùBreve = min.getValoreNumericoDataOra() + (int) min.getDurata() - tempoAlNodo;
        System.out.println(pesoViaggioPiùBreve);
        //per indicazioni
        indicazioni = new ArrayList<>();
        if (min != null) {
            String viaggio = min.getPaginaAnnuncio();
            indicazioni.add("Viaggio BlaBlaCar:\n" + viaggio);
            System.out.println(viaggio);
        }
    }

    public List<String> getIndicazioni() {
        return indicazioni;
    }
}
