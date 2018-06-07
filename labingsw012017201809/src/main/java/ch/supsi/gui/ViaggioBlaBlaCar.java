package ch.supsi.gui;

public class ViaggioBlaBlaCar {
    private String paginaAnnuncio;
    private String dataOraPartenza;
    private NodoViaggioBlaBlaCar luogoPartenza;
    private NodoViaggioBlaBlaCar luogoArrivo;
    private double prezzo;
    private String moneta;
    private String simboloMoneta;
    private double commissione;
    private int postiRimasti;
    private int postiTotali;
    private double durata;
    private double distanza;
    private Auto auto;
    private int ora;
    private int minuti;
    private int giorno;
    private int mese;
    private int anno;
    private long valoreNumericoDataOra;

    public ViaggioBlaBlaCar(String paginaAnnuncio, String dataOraPartenza, NodoViaggioBlaBlaCar luogoPartenza, NodoViaggioBlaBlaCar luogoArrivo, double prezzo, String moneta, String simboloMoneta, double commissione, int postiRimasti, int postiTotali, double durata, double distanza, Auto auto) {
        this.paginaAnnuncio = paginaAnnuncio;
        this.dataOraPartenza = dataOraPartenza;
        this.luogoPartenza = luogoPartenza;
        this.luogoArrivo = luogoArrivo;
        this.prezzo = prezzo;
        this.moneta = moneta;
        this.simboloMoneta = simboloMoneta;
        this.commissione = commissione;
        this.postiRimasti = postiRimasti;
        this.postiTotali = postiTotali;
        this.durata = durata;
        this.distanza = distanza;
        this.auto = auto;
    }

    public void scriviViaggio() {
        System.out.println("Link pagina: " + paginaAnnuncio);
        System.out.println("Data e ora viaggio: " + dataOraPartenza);
        System.out.println("Luogo di partenza:");
        luogoPartenza.scriviLuogo();
        System.out.println("Luogo di arrivo:");
        luogoArrivo.scriviLuogo();
        System.out.println("Prezzo: " + prezzo + " " + moneta + " simbolo moneta:" + simboloMoneta);
        System.out.println("Prezzo Commissione: " + commissione + " " + simboloMoneta);
        System.out.println("Posti rimasti: " + postiRimasti);
        System.out.println("Posti Totali: " + postiTotali);
        System.out.println("Durata: " + durata);
        System.out.println("Distanza: " + distanza);
        if (auto.getID() != 0) {
            System.out.println("Auto: \n");
            auto.scriviAuto();
        }
    }

    public String getViaggio() {
        String str = "";
        str += ("Link pagina: " + paginaAnnuncio + "\n");
        str += ("Data e ora viaggio: " + dataOraPartenza + "\n");
        str += ("Luogo di partenza:" + "\n");
        str += luogoPartenza.getLuogo();
        str += ("Luogo di arrivo:" + "\n");
        str += luogoArrivo.getLuogo();
        str += ("Prezzo: " + prezzo + " " + moneta + " simbolo moneta:" + simboloMoneta + "\n");
        str += ("Prezzo Commissione: " + commissione + " " + simboloMoneta + "\n");
        str += ("Posti rimasti: " + postiRimasti + "\n");
        str += ("Posti Totali: " + postiTotali + "\n");
        str += ("Durata: " + durata + "\n");
        str += ("Distanza: " + distanza + "\n");
        if (auto.getID() != 0) {
            str += ("Auto: \n" + "\n");
            str += auto.getAuto();
        }
        str += "\n---------------------------------------------------\n";
        return str;
    }

    public double getDurata() {
        return durata;
    }

    public String getDataOraPartenza() {
        return dataOraPartenza;
    }

    public void smista() {
        String[] str = dataOraPartenza.split("/");
        giorno = Integer.valueOf(str[0]);
        mese = Integer.valueOf(str[1]);
        str = str[2].split(" ");
        anno = Integer.valueOf(str[0]);
        str = str[1].split(":");
        ora = Integer.valueOf(str[0]);
        minuti = Integer.valueOf((str[1]));
        //System.out.println(ora+":"+minuti+" "+giorno+"/"+mese+"/"+anno);
        valoreNumericoDataOra = FrecciaTrasporti.calcolaTempo(ora,minuti,giorno,mese,anno,0);
    }

    public long getValoreNumericoDataOra() {
        return valoreNumericoDataOra;
    }

    public String getPaginaAnnuncio() {
        return paginaAnnuncio;
    }
}
