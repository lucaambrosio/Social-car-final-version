package ch.supsi.gui;

import java.util.Objects;

public class NodoViaggioBlaBlaCar extends Nodo{
    private String nome_città;
    private String indirizzo;
    private String codice_paese;

    public NodoViaggioBlaBlaCar(String nome_città, String indirizzo, Coordinata coordinata, String codice_paese) {
        super(coordinata);
        this.nome_città = nome_città;
        this.indirizzo = indirizzo;
        this.codice_paese = codice_paese;
    }

    public void scriviLuogo() {
        System.out.println("\n"+"nome città: "+nome_città);
        System.out.println("indirizzo: "+indirizzo);
        System.out.println("coordinate : ");
        coordinata.scriviCoordinata();
        System.out.println("\ncodice paese: "+codice_paese+"\n");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodoViaggioBlaBlaCar that = (NodoViaggioBlaBlaCar) o;
        return Objects.equals(nome_città, that.nome_città) &&
                Objects.equals(indirizzo, that.indirizzo) &&
                Objects.equals(codice_paese, that.codice_paese);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nome_città, indirizzo, codice_paese);
    }

    public String getLuogo() {
        String str="";
        str+=("\n"+"nome città: "+nome_città+"\n");
        str+=("indirizzo: "+indirizzo+"\n");
        str+=("coordinate : "+"\n");
        str+=coordinata.getCoordinata();
        str+=("\ncodice paese: "+codice_paese+"\n"+"\n");
        return str;
    }
}
