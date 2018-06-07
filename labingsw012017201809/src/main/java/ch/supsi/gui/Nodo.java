package ch.supsi.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Nodo {
    protected Coordinata coordinata;
    protected List<Freccia> frecce = new ArrayList<>();
    protected int pesoNodo;
    protected long orarioNodo;
    protected ReferenzaAFreccia ref = null;

    public Nodo(Coordinata coordinata) {
        this.coordinata = coordinata;
        pesoNodo = Integer.MAX_VALUE / 10;
    }

    public Coordinata getCoordinata() {
        return coordinata;
    }

    public void scriviNodo() {
        coordinata.scriviCoordinata();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nodo nodo = (Nodo) o;
        return Objects.equals(coordinata, nodo.coordinata);
    }

    @Override
    public int hashCode() {

        return Objects.hash(coordinata);
    }

    public void aggiungiFreccia(Nodo nodo, TipoPercorrenza tipo) {
        int peso = -1;
        frecce.add(new Freccia(nodo, peso, tipo));
    }

    public List<Freccia> getFrecce() {
        return frecce;
    }

    public void setPesoNodo(int pesoNodo, long tempo) {
        this.pesoNodo = pesoNodo;
        this.orarioNodo = tempo + pesoNodo;
    }

    public int getPesoNodo() {
        return pesoNodo;
    }

    public long getTempoNodo(){
        return orarioNodo;
    }

    public void setRef(ReferenzaAFreccia ref) {
        this.ref = ref;
    }

    public ReferenzaAFreccia getRef() {
        return ref;
    }
}
