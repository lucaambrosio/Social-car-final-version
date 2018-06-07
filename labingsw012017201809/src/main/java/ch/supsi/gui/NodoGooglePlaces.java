package ch.supsi.gui;

public class NodoGooglePlaces extends Nodo{
    private String id;
    private String nome;
    private String place_id;

    public NodoGooglePlaces(Coordinata coordinata, String id, String nome, String place_id) {
        super(coordinata);
        this.id = id;
        this.nome = nome;
        this.place_id = place_id;
    }

    public NodoGooglePlaces(Nodo nodo, String nome){
        super(nodo.getCoordinata());
        this.nome=nome;
    }

    public void scriviNodoGoogle(){
        coordinata.scriviCoordinata();
        System.out.println("\nid: "+id);
        System.out.println("nome: "+nome);
        System.out.println("place_id: "+place_id);
    }

    public String getNome() {
        return nome;
    }
}
