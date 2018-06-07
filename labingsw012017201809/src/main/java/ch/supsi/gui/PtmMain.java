package ch.supsi.gui;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.DirectionsService;
import javafx.animation.Timeline;
import javafx.application.Application;

import static javafx.application.Application.launch;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import tornadofx.control.DateTimePicker;
import tornadofx.control.Fieldset;
import tornadofx.control.Form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class PtmMain extends Application {
    private Scene principale;

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane pannello = new AnchorPane();
        Scene scenaRicerca = new Scene(pannello);
        principale = scenaRicerca;
        //titolo
        stage.setTitle("TBC");

        settaPannelloRicerca(pannello, stage);

        stage.setScene(scenaRicerca);
        stage.setMinHeight(600);
        stage.setMinWidth(1000);
        stage.show();
    }

    private void settaPannelloRicerca(AnchorPane pannello, Stage stage) {
        TextField textPartenza = new TextField();
        TextField textArrivo = new TextField();
        Button bottoneCerca = new Button("Cerca");
        DatePicker dataPartenza = new DatePicker(LocalDate.now());
        CheckBox trasportiCheck = new CheckBox();
        CheckBox blablaCheck = new CheckBox();
        trasportiCheck.setSelected(true);
        blablaCheck.setSelected(true);
        ImageView imgTrasporti = new ImageView();
        ImageView imgBlaBla = new ImageView();
        imgTrasporti.setImage(new Image("/treno.png"));
        imgBlaBla.setImage(new Image("/BlaBlaCar.png"));
        imgTrasporti.setFitWidth(200);
        imgBlaBla.setFitWidth(400);
        imgTrasporti.setPreserveRatio(true);
        imgBlaBla.setPreserveRatio(true);
        Label errore = new Label();
        RadioButton avanzato = new RadioButton("Avanzato");
        RadioButton semplice = new RadioButton(("Semplice"));
        ToggleGroup algoritmoGroup = new ToggleGroup();
        avanzato.setToggleGroup(algoritmoGroup);
        semplice.setToggleGroup(algoritmoGroup);
        avanzato.setUserData("avanzato");
        semplice.setUserData("semplice");
        semplice.setSelected(true);
        Button clear = new Button("Clear");
        dataPartenza.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArrivo.setText("");
                textPartenza.setText("");
            }
        });

        TextField oraPartenza = new TextField("18");
        TextField minutiPartenza = new TextField("15");
        HBox timePartenza = new HBox(new Label("Ora Partenza "), oraPartenza, new Label(":"), minutiPartenza);


        //onClick
        bottoneCerca.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                errore.setText("");
                AutoCompletamento da = new AutoCompletamento(textPartenza.textProperty());
                AutoCompletamento a = new AutoCompletamento(textArrivo.textProperty());
                textPartenza.setText(formatta(da.nomeCity()));
                textArrivo.setText(formatta(a.nomeCity()));
                if (da.nomeCity().equals(a.nomeCity())) {
                    errore.setText("Errore: arrivo e partenza sono uguali!");
                    return;
                }
                Itinerario i = new Itinerario(da.nomeCity(), a.nomeCity(), da.prendiCoordinate());  //mi servirà per trovare una lista di coordinate
                if (!i.trattaPresente()) {
                    errore.setText("Errore: Percorso non disponibile!");
                    return;
                }
                //ora ho la lista di coordinate presenti in un percorso standard
                //la userò per tracciare dei cerchi di raggio 50km da cui trovare le città
                List<NodoGooglePlaces> listaCity = new ArrayList<>();
                if(!algoritmoGroup.getSelectedToggle().getUserData().equals("semplice")){
                listaCity = trovaCity(i.getListaCoordinate());
                for (NodoGooglePlaces no : listaCity)
                    no.scriviNodoGoogle();}
                Nodo arrivoNodo = new Nodo(a.prendiCoordinate());
                Nodo partenzaNodo = new Nodo(da.prendiCoordinate());
                if (algoritmoGroup.getSelectedToggle().getUserData().equals("semplice"))
                    eseguiAlgoritmo(listaCity, partenzaNodo, arrivoNodo, dataPartenza.getValue().toString(), oraPartenza.getText(), minutiPartenza.getText(), trasportiCheck.isSelected(), blablaCheck.isSelected(), true);
                else
                    eseguiAlgoritmo(listaCity, partenzaNodo, arrivoNodo, dataPartenza.getValue().toString(), oraPartenza.getText(), minutiPartenza.getText(), trasportiCheck.isSelected(), blablaCheck.isSelected(), false);
                Button tornaIndietro = new Button("Torna alla ricerca");

                tornaIndietro.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        stage.setScene(principale);
                    }
                });
                stage.setScene(creaNuovaScena(stage.getScene().getWidth(), stage.getScene().getHeight(), arrivoNodo, partenzaNodo, da.nomeCity(), a.nomeCity(),tornaIndietro));
            }
        });

        //quando si modifica ora
        oraPartenza.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    oraPartenza.setText(newValue.replaceAll("[^\\d]", ""));
                }
                double val = Double.valueOf(newValue);
                if (val >= 24)
                    oraPartenza.setText("23");
                if (val < 0)
                    oraPartenza.setText("0");
            }
        });
        minutiPartenza.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    minutiPartenza.setText(newValue.replaceAll("[^\\d]", ""));
                }
                double val = Double.valueOf(newValue);
                if (val >= 60)
                    minutiPartenza.setText("59");
                if (val < 0)
                    minutiPartenza.setText("0");
            }
        });

        //assemblaggio
        pannello.getChildren().addAll(textArrivo, textPartenza, bottoneCerca, dataPartenza, timePartenza, trasportiCheck, blablaCheck, imgTrasporti, imgBlaBla, errore, avanzato, semplice,clear);
        pannello.setLeftAnchor(textPartenza, 10.0);
        pannello.setTopAnchor(textPartenza, 10.0);

        pannello.setLeftAnchor(textArrivo, 10.0);
        pannello.setTopAnchor(textArrivo, 50.0);

        pannello.setLeftAnchor(bottoneCerca, 10.0);
        pannello.setTopAnchor(bottoneCerca, 90.0);

        pannello.setLeftAnchor(dataPartenza, 600.0);
        pannello.setTopAnchor(dataPartenza, 200.0);

        pannello.setLeftAnchor(timePartenza, 10.0);
        pannello.setTopAnchor(timePartenza, 200.0);

        pannello.setLeftAnchor(trasportiCheck, 10.0);
        pannello.setTopAnchor(trasportiCheck, 350.0);

        pannello.setLeftAnchor(blablaCheck, 450.0);
        pannello.setTopAnchor(blablaCheck, 350.0);

        pannello.setLeftAnchor(imgTrasporti, 80.0);
        pannello.setTopAnchor(imgTrasporti, 250.0);

        pannello.setLeftAnchor(imgBlaBla, 500.0);
        pannello.setTopAnchor(imgBlaBla, 320.0);

        pannello.setLeftAnchor(errore, 250.0);
        pannello.setTopAnchor(errore, 35.0);

        pannello.setLeftAnchor(avanzato, 600.0);
        pannello.setTopAnchor(avanzato, 10.0);

        pannello.setLeftAnchor(semplice, 600.0);
        pannello.setTopAnchor(semplice, 50.0);

        pannello.setLeftAnchor(clear, 143.0);
        pannello.setTopAnchor(clear, 90.0);
    }

    private Scene creaNuovaScena(double width, double height, Nodo arrivoNodo, Nodo partenzaNodo, String nomePartenza, String nomeArrivo, Button tornaIndietro) {
        AnchorPane root = new AnchorPane();
        TextArea text = new TextArea("");
        text.setPrefHeight(400);
        text.setPrefWidth(300);
        text.setEditable(false);
        Mappa mappa = new Mappa(arrivoNodo, partenzaNodo, nomePartenza, nomeArrivo);
        mappa.setTextArea(text);
        mappa.creaMappa();
        Label luogoPartenza = new Label("Luogo partenza: " + nomePartenza);
        Label luogoArrivo = new Label("Luogo arrivo: " + nomeArrivo);
        Label oraPartenza = new Label("Tempo partenza: " + Mappa.getTempo(partenzaNodo));
        Label oraArrivo = new Label("Tempo arrivo: " + Mappa.getTempo(arrivoNodo));


        root.getChildren().addAll(mappa.getMap(), luogoPartenza, luogoArrivo, oraArrivo, oraPartenza, text, tornaIndietro);

        root.setLeftAnchor(luogoPartenza, 10.0);
        root.setTopAnchor(luogoPartenza, 10.0);

        root.setLeftAnchor(oraPartenza, 10.0);
        root.setTopAnchor(oraPartenza, 30.0);

        root.setLeftAnchor(luogoArrivo, 10.0);
        root.setTopAnchor(luogoArrivo, 50.0);

        root.setLeftAnchor(oraArrivo, 10.0);
        root.setTopAnchor(oraArrivo, 70.0);

        root.setLeftAnchor(mappa.getMap(), 400.0);
        root.setTopAnchor(mappa.getMap(), 0.0);

        root.setLeftAnchor(text, 10.0);
        root.setTopAnchor(text, 90.0);

        root.setLeftAnchor(tornaIndietro, 10.0);
        root.setTopAnchor(tornaIndietro, 510.0);

        Scene scene = new Scene(root, width, height);
        return scene;
    }

    private void eseguiAlgoritmo(List<NodoGooglePlaces> listaCity, Nodo partenza, Nodo arrivo, String data, String oraS, String minutiS, boolean tras, boolean bla, boolean semplice) {
        String[] str = data.split("-");
        int anno = Integer.valueOf(str[0]);
        int mese = Integer.valueOf(str[1]);
        int giorno = Integer.valueOf(str[2]);
        int ora = Integer.valueOf(oraS);
        int minuti = Integer.valueOf(minutiS);
        System.out.println(giorno + "/" + mese + "/" + anno + "  " + ora + ":" + minuti);
        Algoritmo al = new Algoritmo(partenza, arrivo, listaCity, giorno, mese, anno, ora, minuti);
        al.preferenze(tras, bla, semplice);
        al.esegui();
    }

    private List<NodoGooglePlaces> trovaCity(List<Coordinata> listaCoordinate) {
        List<NodoGooglePlaces> città = new ArrayList<>();
        IlCercatore cerca = new IlCercatore(listaCoordinate);
        return cerca.getListaCittà();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String formatta(String posto) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < posto.length(); i++) {
            if (posto.charAt(i) != '+')
                str.append(posto.charAt(i));
            else
                str.append(' ');
        }
        return str.toString();                                                                                                    
    }
}