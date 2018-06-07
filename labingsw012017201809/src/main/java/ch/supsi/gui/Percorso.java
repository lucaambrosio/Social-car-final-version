package ch.supsi.gui;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;

import java.util.List;

public class Percorso {
    List<Coordinata> lista;

    public Percorso(List<Coordinata> lista) {
        this.lista = lista;
    }

    public List<Coordinata> getLista() {
        return lista;
    }

    public void scriviPercorso() {
        for (Coordinata c : lista) {
            c.scriviCoordinata();
            System.out.println();
        }
    }

    public MVCArray getMVCArray() {
        LatLong[] arr = new LatLong[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            arr[i] = new LatLong(lista.get(i).getLat(), lista.get(i).getLong());
        }
        return new MVCArray(arr);

    }
}
