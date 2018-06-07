package ch.supsi.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class InterpretatoreBlablacar {
    private String info;
    private JsonNode root;
    private ObjectMapper mapper;


    public InterpretatoreBlablacar(String s, List<ViaggioBlaBlaCar> viaggi) throws IOException {
        info = s;

        mapper = new ObjectMapper();
        root = mapper.readTree(info);
        int numeroViaggi = root.path("pager").path("total").asInt();

        prendiViaggi(numeroViaggi, viaggi);
    }

    private void prendiViaggi(int numeroViaggi, List<ViaggioBlaBlaCar> viaggi) {
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

}