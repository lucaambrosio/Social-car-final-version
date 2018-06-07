package ch.supsi.gui;

public class ReferenzaAFreccia {
    private Nodo nodo;
    private TipoPercorrenza tipo;
    private Freccia freccia;

    public ReferenzaAFreccia(Nodo nodo, TipoPercorrenza tipo, Freccia f) {
        this.nodo = nodo;
        this.tipo = tipo;
        freccia = f;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public TipoPercorrenza getTipo() {
        return tipo;
    }

    public Freccia getFreccia() {
        return freccia;
    }
}
