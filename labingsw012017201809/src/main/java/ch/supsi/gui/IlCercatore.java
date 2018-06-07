package ch.supsi.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IlCercatore {
    private List<Coordinata> listaC;
    private List<NodoGooglePlaces> listaCittà;
    public static Set<NodoGooglePlaces> pubblicoSet = new HashSet<>();
    public static Lock lock = new ReentrantLock();
    private ArrayList<Thread> threads = new ArrayList<>();

    public IlCercatore(List<Coordinata> listaCoordinate) {
        listaC = listaCoordinate;
        listaCittà = new ArrayList<>();
        trovaCittà();
    }

    private void trovaCittà() {
        Coordinata ultimoNodoConRichiesta = null;
        double distanza = 0;
        for (int i = 0; i < listaC.size(); i++) {
            if (ultimoNodoConRichiesta == null) {
                System.out.println("\nNULL");
                ultimoNodoConRichiesta = listaC.get(i);
                threads.add(new Thread(new ThreadCercatore(new Nodo(ultimoNodoConRichiesta), 10000, 3)));
                threads.add(new Thread(new ThreadCercatore(new Nodo(ultimoNodoConRichiesta), 25000, 3)));
                continue;
            }
            distanza = calcolaDistanza(ultimoNodoConRichiesta, listaC.get(i));
            System.out.println(distanza);
            if (distanza >= 30000) {
                System.out.println(">=30000");
                lineaRetta(ultimoNodoConRichiesta, listaC.get(i), distanza);
                ultimoNodoConRichiesta = listaC.get(i);
                continue;
            }
            System.out.println("<30000");
            if (distanza > 5000) {
                System.out.println(">5000");
                ultimoNodoConRichiesta = listaC.get(i);
                threads.add(new Thread(new ThreadCercatore(new Nodo(ultimoNodoConRichiesta), 25000, 3)));
            }
        }

        for (Thread th : threads)
            th.start();

        for (Thread th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(NodoGooglePlaces n : pubblicoSet)
            listaCittà.add(n);
    }

    private void lineaRetta(Coordinata partenza, Coordinata arrivo, double dist) {
        int numeroCerchi = 1 + (int) dist / 15000;
        System.out.println("numero cerchi = " + numeroCerchi);

        double latPartenza = partenza.getLat();
        double lngPartenza = partenza.getLong();
        double latArrivo = arrivo.getLat();
        double lngArrivo = arrivo.getLong();
        double diffLatitudine = latArrivo - latPartenza;
        double diffLongitudine = lngArrivo - lngPartenza;

        double stepLat = diffLatitudine / (numeroCerchi - 1);
        double stepLong = diffLongitudine / (numeroCerchi - 1);

        double newLat;
        double newLong;
        Nodo temp;

        for (int i = 1; i < numeroCerchi; i++) {
            newLat = i * stepLat + partenza.getLat();
            newLong = i * stepLong + partenza.getLong();
            temp = new Nodo(new Coordinata(newLat, newLong));
            threads.add(new Thread(new ThreadCercatore(temp, (int) 25000, 3)));
        }
        //i=1 per saltare il nodo di partenza, già analizzato in precedenza;
    }

    public static double calcolaDistanza(Coordinata ultimoNodoConRichiesta, Coordinata coordinata) {
        double lat1 = ultimoNodoConRichiesta.getLat();
        double lon1 = ultimoNodoConRichiesta.getLong();
        double lat2 = coordinata.getLat();
        double lon2 = coordinata.getLong();
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d * 1000; // meters
    }

    public List<NodoGooglePlaces> getListaCittà() {
        return listaCittà;
    }
}
