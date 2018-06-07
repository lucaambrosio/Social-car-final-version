package ch.supsi.gui;

public class Auto {
    private int id;
    private String marca;
    private String modello;
    private String comfort;
    private int stelleComfort;

    public Auto(int id, String marca, String modello, String comfort, int stelleComfort) {
        this.id = id;
        this.marca = marca;
        this.modello = modello;
        this.comfort = comfort;
        this.stelleComfort = stelleComfort;
    }

    public void scriviAuto() {
        System.out.println("ID: "+id);
        System.out.println("Marca e modello: "+marca+" "+modello);
        System.out.println("Livello di comfort: "+comfort+" "+stelleComfort+" stelle");
    }

    public int getID() {
        return id;
    }

    public String getAuto() {
        String str="";
        str+=("ID: "+id+"\n");
        str+=("Marca e modello: "+marca+" "+modello+"\n");
        str+=("Livello di comfort: "+comfort+" "+stelleComfort+" stelle"+"\n");
        return str;
    }
}
