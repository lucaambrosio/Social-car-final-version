package ch.supsi.gui;

import javafx.beans.property.StringProperty;

import java.net.URL;

public class CreaUrlBlaBlaCar {

    private URL url;
    private String from;
    private String to;
    private String formato;
    private String valuta;
    private String local;
    private String limite;

    public CreaUrlBlaBlaCar(StringProperty from, StringProperty to) {
        this.from = "&fn=" + from.getValue();
        this.to = "&tn=" + to.getValue();
        formato = "&_format=json";
        valuta = "&cur=EUR";
        local = "&locale=it_IT";
        limite = "&limit=1000000";
    }

    public void createUrl() throws Exception {
        String str = "https://public-api.blablacar.com/api/v2/trips?key=f762695b281c4672abd2df1c40878afb";
        str += from + to + formato + valuta + local + limite;
        System.out.println(str);
        url = new URL(str);
    }

    public URL getURL() {
        return url;
    }
}