package ch.supsi.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Algoritmo {
    private Nodo partenza;
    private Nodo arrivo;
    private boolean trasporti;
    private boolean blabla;
    List<Nodo> listaCity = new ArrayList<>();
    private int giorno;
    private int mese;
    private int anno;
    private int ora;
    private int minuti;
    List<Nodo> listaNodiDijkstra = new ArrayList<>();
    private boolean semplice;
    private List<Thread> threads= new ArrayList<>();

    public Algoritmo(Nodo partenza, Nodo arrivo, List<NodoGooglePlaces> listaCity, int giorno, int mese, int anno, int ora, int minuti) {
        this.partenza = partenza;
        this.arrivo = arrivo;
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
        this.ora = ora;
        this.minuti = minuti;

        for (NodoGooglePlaces n : listaCity)
            this.listaCity.add(n);
        this.listaCity.add(partenza);
        this.listaCity.add(arrivo);
        for (Nodo n : this.listaCity)
            listaNodiDijkstra.add(n);
    }

    public void esegui() {
        if (semplice)
            disponiFrecceSemplice();
        else
            disponiFrecce();
        System.out.println("Frecce\n");
        for (Nodo nodo : listaCity) {
            List<Freccia> l = nodo.getFrecce();
            for (Freccia f : l) {
                if (nodo == partenza)
                    System.out.print("Partenza");
                else if (nodo == arrivo)
                    System.out.print("Arrivo");
                else
                    System.out.print(((NodoGooglePlaces) nodo).getNome());

                System.out.print(" ---- ");
                System.out.print(f.getPeso());
                System.out.print(" ----> ");

                if (f.getNodo() != arrivo)
                    System.out.print(((NodoGooglePlaces) f.getNodo()).getNome());
                else
                    System.out.print("arrivo");
                System.out.println();
            }
        }
        System.out.println("Parte 2");
        partenza.getFrecce().get(0).getNodo().scriviNodo();
        System.out.println("");
        Dikstra();
    }

    private void disponiFrecce() {
        for (int i = 0; i < listaCity.size(); i++) {
            for (int j = 0; j < listaCity.size(); j++) {
                if (siPuoAggiungere(listaCity.get(i), listaCity.get(j))) {
                    if (trasporti)
                        //if (listaCity.get(i) != partenza || listaCity.get(j) != arrivo)
                            listaCity.get(i).aggiungiFreccia(listaCity.get(j), TipoPercorrenza.Trasporti);
                    if (blabla)
                        //if (listaCity.get(i) != partenza || listaCity.get(j) != arrivo)
                            listaCity.get(i).aggiungiFreccia(listaCity.get(j), TipoPercorrenza.BlaBla);
                    //if (listaCity.get(i) != partenza || listaCity.get(j) != arrivo)
                        listaCity.get(i).aggiungiFreccia(listaCity.get(j), TipoPercorrenza.Piedi);
                }
            }
        }
    }

    private void disponiFrecceSemplice() {
        for (int i = 0; i < listaCity.size(); i++) {
            for (int j = 0; j < listaCity.size(); j++) {
                if (siPuoAggiungere(listaCity.get(i), listaCity.get(j))) {
                    if (trasporti)
                        if (listaCity.get(i) == partenza && listaCity.get(j) == arrivo)
                            listaCity.get(i).aggiungiFreccia(listaCity.get(j), TipoPercorrenza.Trasporti);
                    if (blabla)
                        if (listaCity.get(i) == partenza && listaCity.get(j) == arrivo)
                            listaCity.get(i).aggiungiFreccia(listaCity.get(j), TipoPercorrenza.BlaBla);
                    if (listaCity.get(i) == partenza && listaCity.get(j) == arrivo)
                        listaCity.get(i).aggiungiFreccia(listaCity.get(j), TipoPercorrenza.Piedi);
                }
            }
        }
    }

    private boolean siPuoAggiungere(Nodo nodo1, Nodo nodo2) {
        if (nodo1 == arrivo)
            return false;
        if (nodo2 == partenza)
            return false;
        double distanzaNodo1 = IlCercatore.calcolaDistanza(nodo1.getCoordinata(), arrivo.getCoordinata());
        double distanzaNodo2 = IlCercatore.calcolaDistanza(nodo2.getCoordinata(), arrivo.getCoordinata());
        if (distanzaNodo2 < distanzaNodo1) {
            if (nodo1 == partenza && nodo2 == arrivo) {
                System.out.println("Freccia Creata: " + "partenza" + " -> " + "arrivo");
                return true;
            }
            if (nodo1 == partenza) {
                System.out.println("Freccia Creata: " + "partenza" + " -> " + ((NodoGooglePlaces) nodo2).getNome());
                return true;
            }
            if (nodo2 == arrivo) {
                System.out.println("Freccia Creata: " + ((NodoGooglePlaces) nodo1).getNome() + " -> " + "arrivo");
                return true;
            }
            System.out.println("Freccia Creata: " + ((NodoGooglePlaces) nodo1).getNome() + " -> " + ((NodoGooglePlaces) nodo2).getNome());
            return true;
        }
        return false;
    }

    private void Dikstra() {
        partenza.setPesoNodo(0, FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, 0));
        DikstraRicorsione(partenza);
        stampaNodi(listaCity);
    }

    private void stampaNodi(List<Nodo> l) {
        System.out.println("\nStampa Nodi:\n");
        for (Nodo n : l) {
            System.out.print(stampaNodo(n));
            System.out.println(" peso: " + n.getPesoNodo());
        }
    }

    private void DikstraRicorsione(Nodo node) {
        if (node == arrivo)
            return;
        threads = new ArrayList<>();
        trovaPesoFrecceNodo(node);
        for(Thread t: threads)
            t.start();
        for(Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stampaFrecce(node);

        List<Freccia> listaFrecce = node.getFrecce();

        Freccia min = listaFrecce.get(0);
        for (Freccia f : listaFrecce) {
            System.out.println("\nPrima");
            System.out.println("Nodo.peso: " + node.getPesoNodo());
            System.out.println("Freccia.peso: " + f.getPeso());
            System.out.println("f.getPesonodo: " + f.getNodo().getPesoNodo());
            System.out.println("Dopo");
            if (f.getPeso() < min.getPeso())
                min = f;
            if (f.getNodo().getPesoNodo() > f.getPeso() + node.getPesoNodo()) {
                f.getNodo().setPesoNodo(f.getPeso() + node.getPesoNodo(), FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, 0));
                f.getNodo().setRef(new ReferenzaAFreccia(node, f.getTipo(), f));
            }
            System.out.println("Nodo.peso: " + node.getPesoNodo());
            System.out.println("f.getPesonodo: " + f.getNodo().getPesoNodo());
        }


        System.out.println("min : " + min.getPeso() + " città: " + stampaNodo(min.getNodo()));

        stampaNodi(listaNodiDijkstra);
        listaNodiDijkstra.remove(node);
        System.out.println("Dopo rimozione: ");
        stampaNodi(listaNodiDijkstra);
        System.out.println("Stampa del nodo minore");
        System.out.println(nodoMinore());
        System.out.println("Fine stampa");
        DikstraRicorsione(nodoMinore());
    }

    private Nodo nodoMinore() {
        Nodo min = listaNodiDijkstra.get(0);
        for (Nodo n : listaNodiDijkstra) {
            if (n.getPesoNodo() < min.getPesoNodo())
                min = n;
        }
        return min;
    }

    private void trovaPesoFrecceNodo(Nodo node) {
        List<Freccia> listaFrecce = node.getFrecce();
        for (Freccia f : listaFrecce) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    f.setPeso(node, ora, minuti, giorno, mese, anno);
                }
            }));
        }
    }

    private void stampaFrecce(Nodo node) {
        System.out.println("\nFrecce del nodo :" + stampaNodo(node));
        List<Freccia> listaFrecce = node.getFrecce();
        for (Freccia f : listaFrecce) {
            System.out.print(stampaNodo(node));
            System.out.print("--- ");
            System.out.print(f.getPeso());
            System.out.print(" --->");
            System.out.println(stampaNodo(f.getNodo()));
        }
    }

    private String stampaNodo(Nodo node) {
        if (node == partenza)
            return "Partenza";
        if (node == arrivo)
            return "Arrivo";
        return ((NodoGooglePlaces) node).getNome();
    }

    public void preferenze(boolean trasporti, boolean blabla, boolean semplice) {
        this.semplice = semplice;
        this.trasporti = trasporti;
        this.blabla = blabla;
    }

    public static int trovaPesoFrecciaTrasporti(Nodo nodo, Nodo nodo1, int ora, int minuti, int giorno, int mese, int anno, int pesoNodo, Freccia freccia) {
        FrecciaTrasporti f = new FrecciaTrasporti(nodo, nodo1, ora, minuti, giorno, mese, anno, pesoNodo);
        f.esegui();
        f.creaPercorso();
        freccia.SetPercorso(f.getPercorso());
        freccia.getIndicazioni().add("Arrivo ore: " + Mappa.calcolaTempoInverso(FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, (pesoNodo + f.ritornaPesoFreccia()))));
        if (f.getIndicazioni() != null)
            freccia.getIndicazioni().addAll(f.getIndicazioni());
        freccia.getIndicazioni().add("Partenza ore: " + Mappa.calcolaTempoInverso(FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, pesoNodo)));
        freccia.getIndicazioni().add("Uso i trasporti pubblici");
        return f.ritornaPesoFreccia();
    }

    public static int trovaPesoFrecciaPiedi(Nodo nodo, Nodo nodo1, int ora, int minuti, int giorno, int mese, int anno, int pesoNodo, Freccia freccia) {
        FrecciaPiedi f = new FrecciaPiedi(nodo, nodo1, ora, minuti, giorno, mese, anno, pesoNodo);
        f.esegui();
        f.creaPercorso();
        freccia.SetPercorso(f.getPercorso());
        freccia.getIndicazioni().add("Arrivo ore: " + Mappa.calcolaTempoInverso(FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, (pesoNodo + f.ritornaPesoFreccia()))));
        if (f.getIndicazioni() != null)
            freccia.getIndicazioni().addAll(f.getIndicazioni());
        freccia.getIndicazioni().add("Partenza ore: " + Mappa.calcolaTempoInverso(FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, pesoNodo)));
        freccia.getIndicazioni().add("Tragitto a piedi");
        return f.ritornaPesoFreccia();
    }

    public static int trovaPesoFrecciaBlaBla(Nodo nodo, Nodo nodo1, int ora, int minuti, int giorno, int mese, int anno, int pesoNodo, Freccia freccia) {
        FrecciaBlaBla f = new FrecciaBlaBla(nodo, nodo1, ora, minuti, giorno, mese, anno, pesoNodo);
        try {
            f.interpreta();
        } catch (IOException e) {
            e.printStackTrace();
        }
        f.trovaViaggioBreve();
        freccia.getIndicazioni().add("Arrivo ore: " + Mappa.calcolaTempoInverso(FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, (pesoNodo + f.getPesoViaggioPiùBreve()))));
        if (f.getIndicazioni() != null)
            freccia.getIndicazioni().addAll(f.getIndicazioni());
        freccia.getIndicazioni().add("Partenza ore: " + Mappa.calcolaTempoInverso(FrecciaTrasporti.calcolaTempo(ora, minuti, giorno, mese, anno, pesoNodo)));
        freccia.getIndicazioni().add("Uso BlaBlaCar");
        return f.getPesoViaggioPiùBreve();
    }
}
