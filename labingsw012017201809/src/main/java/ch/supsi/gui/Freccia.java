package ch.supsi.gui;

import com.lynden.gmapsfx.javascript.object.MVCArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Freccia {
    private final Nodo nodo;
    private int peso;
    private TipoPercorrenza tipo;
    private Percorso p;
    private List<String> indicazioni = new ArrayList<>();

    public Freccia(Nodo nodo, int peso, TipoPercorrenza tipo) {
        this.nodo = nodo;
        this.peso = peso;
        this.tipo = tipo;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(Nodo nodoAttuale, int ora, int minuti, int giorno, int mese, int anno) {
        if (tipo == TipoPercorrenza.Trasporti)
            peso = Algoritmo.trovaPesoFrecciaTrasporti(nodoAttuale, nodo, ora, minuti, giorno, mese, anno, nodoAttuale.getPesoNodo(),this);
        if (tipo == TipoPercorrenza.BlaBla)
            peso = Algoritmo.trovaPesoFrecciaBlaBla(nodoAttuale, nodo, ora, minuti, giorno, mese, anno, nodoAttuale.getPesoNodo(),this);
        if (tipo == TipoPercorrenza.Piedi)
            peso = Algoritmo.trovaPesoFrecciaPiedi(nodoAttuale, nodo, ora, minuti, giorno, mese, anno, nodoAttuale.getPesoNodo(),this);
        if (peso < 0) {
            System.out.println("Errore\n\n\n\n\n\n");
            peso = Integer.MAX_VALUE/100;
        }
        System.out.println(peso);
    }

    public TipoPercorrenza getTipo() {
        return tipo;
    }

    public void SetPercorso(Percorso p) {
        this.p = p;
    }

    public Percorso getPercorso() {
        return p;
    }

    public void scriviPercorso(){
        p.scriviPercorso();
    }

    public List<String> getIndicazioni() {
        return indicazioni;
    }

    public void setIndicazioni(List<String> indicazioni) {
        this.indicazioni = indicazioni;
    }
}
