package ch.supsi.gui;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.service.elevation.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.lynden.gmapsfx.shapes.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import netscape.javascript.JSObject;
import org.jsoup.Jsoup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Mappa implements MapComponentInitializedListener,
        ElevationServiceCallback, GeocodingServiceCallback, DirectionsServiceCallback {
    protected GoogleMapView mapComponent;
    protected GoogleMap map;
    protected DirectionsPane directions;

    private MarkerOptions markerOptions2;
    private Marker myMarker2;
    private Nodo arrivo;
    private Nodo partenza;
    private String nomePartenza;
    private String nomeArrivo;
    private DirectionsService directionsService;
    private DirectionsPane directionsPane;
    private List<String> indicazioni = new ArrayList<>();
    private TextArea textArea;

    public Mappa(Nodo arrivoNodo, Nodo partenza, String nomePartenza, String nomeArrivo) {
        arrivo = arrivoNodo;
        this.nomeArrivo = nomeArrivo;
        this.nomePartenza = nomePartenza;
        this.partenza = partenza;
        System.out.println(nomeArrivo);
    }

    public static String getTempo(Nodo n) {
        return calcolaTempoInverso(n.getTempoNodo());
    }

    public void creaMappa() {
        System.setProperty("java.net.useSystemProxies", "true");
        System.out.println("Java version: " + System.getProperty("java.home"));
        mapComponent = new GoogleMapView(Locale.getDefault().getLanguage(), null);
        mapComponent.addMapInializedListener(this);

        BorderPane bp = new BorderPane();
        ToolBar tb = new ToolBar();
    }

    public GoogleMapView getMap() {
        return mapComponent;
    }

    DirectionsRenderer renderer;

    @Override
    public void mapInitialized() {
        LatLong center = new LatLong(arrivo.getCoordinata().getLat(), arrivo.getCoordinata().getLong());
        mapComponent.addMapReadyListener(() -> {
            checkCenter(center);
        });

        MapOptions options = new MapOptions();
        options.center(center)
                .zoom(9)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .mapType(MapTypeIdEnum.ROADMAP);
        map = mapComponent.createMap(options);
        directions = mapComponent.getDirec();

        map.setHeading(123.2);

        /*
        MarkerOptions markerOptions = new MarkerOptions();
        LatLong markerLatLong = new LatLong(47.606189, -122.335842);
        markerOptions.position(markerLatLong)
                .title("My new Marker")
                .icon("mymarker.png")
                .animation(Animation.DROP)
                .visible(true);

        final Marker myMarker = new Marker(markerOptions);

        markerOptions2 = new MarkerOptions();
        LatLong markerLatLong2 = new LatLong(47.906189, -122.335842);
        markerOptions2.position(markerLatLong2)
                .title("My new Marker")
                .visible(true);

        myMarker2 = new Marker(markerOptions2);

        map.addMarker(myMarker);
        map.addMarker(myMarker2);

        InfoWindowOptions infoOptions = new InfoWindowOptions();
        infoOptions.content("<h2>Here's an info window</h2><h3>with some info</h3>")
                .position(center);

        InfoWindow window = new InfoWindow(infoOptions);
        window.open(map, myMarker);


        map.fitBounds(new LatLongBounds(new LatLong(30, 120), center));


        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
        });


        LatLong[] ary = new LatLong[]{markerLatLong, markerLatLong2};
        MVCArray mvc = new MVCArray(ary);

        PolylineOptions polyOpts = new PolylineOptions()
                .path(mvc)
                .strokeColor("red")
                .strokeWeight(2);

        Polyline poly = new Polyline(polyOpts);
        map.addMapShape(poly);
        map.addUIEventHandler(poly, UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
//            System.out.println("You clicked the line at LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
        });

        /*LatLong poly1 = new LatLong(47.429945, -122.84363);
        LatLong poly2 = new LatLong(47.361153, -123.03040);
        LatLong poly3 = new LatLong(47.387193, -123.11554);
        LatLong poly4 = new LatLong(47.585789, -122.96722);
        LatLong[] pAry = new LatLong[]{poly1, poly2, poly3, poly4};
        MVCArray pmvc = new MVCArray(pAry);

        PolygonOptions polygOpts = new PolygonOptions()
                .paths(pmvc)
                .strokeColor("blue")
                .strokeWeight(2)
                .editable(false)
                .fillColor("lightBlue")
                .fillOpacity(0.5);

        Polygon pg = new Polygon(polygOpts);
        map.addMapShape(pg);
        map.addUIEventHandler(pg, UIEventType.click, (JSObject obj) -> {
            //polygOpts.editable(true);
            pg.setEditable(!pg.getEditable());
        });*/

        /*LatLong centreC = new LatLong(47.545481, -121.87384);
        CircleOptions cOpts = new CircleOptions()
                .center(centreC)
                .radius(5000)
                .strokeColor("green")
                .strokeWeight(2)
                .fillColor("orange")
                .fillOpacity(0.3);

        Circle c = new Circle(cOpts);
        map.addMapShape(c);
        map.addUIEventHandler(c, UIEventType.click, (JSObject obj) -> {
            c.setEditable(!c.getEditable());
        });*/

        /*LatLongBounds llb = new LatLongBounds(new LatLong(47.533893, -122.89856), new LatLong(47.580694, -122.80312));
        RectangleOptions rOpts = new RectangleOptions()
                .bounds(llb)
                .strokeColor("black")
                .strokeWeight(2)
                .fillColor("null");

        Rectangle rt = new Rectangle(rOpts);
        map.addMapShape(rt);*/

        /*LatLong arcC = new LatLong(47.227029, -121.81641);
        double startBearing = 0;
        double endBearing = 30;
        double radius = 30000;

        MVCArray path = ArcBuilder.buildArcPoints(arcC, startBearing, endBearing, radius);
        path.push(arcC);

        Polygon arc = new Polygon(new PolygonOptions()
                .paths(path)
                .strokeColor("blue")
                .fillColor("lightBlue")
                .fillOpacity(0.3)
                .strokeWeight(2)
                .editable(false));

        map.addMapShape(arc);
        map.addUIEventHandler(arc, UIEventType.click, (JSObject obj) -> {
            arc.setEditable(!arc.getEditable());
        });*/

        //GeocodingService gs = new GeocodingService();
        /*
        DirectionsService ds = new DirectionsService();
        renderer = new DirectionsRenderer(true, map, directions);

        DirectionsWaypoint[] dw = new DirectionsWaypoint[2];
        dw[0] = new DirectionsWaypoint("São Paulo - SP");
        dw[1] = new DirectionsWaypoint("Juiz de Fora - MG");

        DirectionsRequest dr = new DirectionsRequest(
                "Belo Horizonte - MG",
                "Rio de Janeiro - RJ",
                TravelModes.DRIVING,
                dw);
        ds.getRoute(dr, this, renderer);

        LatLong[] location = new LatLong[1];
        location[0] = new LatLong(-19.744056, -43.958699);
        LocationElevationRequest loc = new LocationElevationRequest(location);
        ElevationService es = new ElevationService();
        es.getElevationForLocations(loc, this);

*/
        directionsService = new DirectionsService();
        directionsPane = new DirectionsPane();
        disegnaPercorso();
        for (int i=indicazioni.size()-1;i>=0;i--){
            textArea.appendText(html2text(indicazioni.get(i))+"\n");
        }

        /*
        DirectionsRequest dd = new DirectionsRequest(
                "Milano,Mi,Italia",
                "Roma,Rm,Italia",
                TravelModes.DRIVING);
        DirectionsService servizzzzio = new DirectionsService();
        servizzzzio.getRoute(dd, this, new DirectionsRenderer(true, mapComponent.getMap(), directionsPane));

        DirectionsRequest dd2 = new DirectionsRequest(
                "Roma,Rm,Italia",
                "Lugano,Svizzera",
                TravelModes.DRIVING);
        servizzzzio.getRoute(dd2, this, new DirectionsRenderer(true, mapComponent.getMap(), directionsPane));

        */

        //disegnare una polyline


        /*
        PolylineOptions opzioniPolyLine = new PolylineOptions()
                .path(getMVCLatLongNodo(arrivo))
                .strokeColor("red")
                .strokeWeight(2);
        Polyline lineaPoligonale = new Polyline(opzioniPolyLine);
        map.addMapShape((MapShape) lineaPoligonale);*/
    }

    private void disegnaPercorso() {
        Nodo temp = arrivo;
        while (temp != partenza) {
            System.out.println(prendiNome(temp));
            System.out.println(prendiNome(temp.getRef().getNodo()));
            DirectionsRequest dir = getDirectionRequest(temp, temp.getRef().getNodo());
            if (temp.getRef().getFreccia().getTipo() == TipoPercorrenza.Trasporti)
                directionsService.getRoute(dir, this, new DirectionsRenderer(false, mapComponent.getMap(), directionsPane, "blue"));
            if (temp.getRef().getFreccia().getTipo() == TipoPercorrenza.BlaBla)
                directionsService.getRoute(dir, this, new DirectionsRenderer(false, mapComponent.getMap(), directionsPane, "red"));
            if (temp.getRef().getFreccia().getTipo() == TipoPercorrenza.Piedi)
                directionsService.getRoute(dir, this, new DirectionsRenderer(false, mapComponent.getMap(), directionsPane, "green"));
            temp = temp.getRef().getNodo();
        }

    }


    private void hideMarker() {
        boolean visible = myMarker2.getVisible();

        myMarker2.setVisible(!visible);
    }

    private void deleteMarker() {
        map.removeMarker(myMarker2);
    }

    private void checkCenter(LatLong center) {
    }

    @Override
    public void elevationsReceived(ElevationResult[] results, ElevationStatus status) {
        if (status.equals(ElevationStatus.OK)) {
            for (ElevationResult e : results) {
                System.out.println(" Elevation on " + e.getLocation().toString() + " is " + e.getElevation());
            }
        }
    }

    @Override
    public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
        if (status.equals(GeocoderStatus.OK)) {
            for (GeocodingResult e : results) {
                System.out.println(e.getVariableName());
                System.out.println("GEOCODE: " + e.getFormattedAddress() + "\n" + e.toString());
            }

        }

    }

    @Override
    public void directionsReceived(DirectionsResult results, DirectionStatus status) {
        if (status.equals(DirectionStatus.OK)) {
            //mapComponent.getMap().showDirectionsPane();
            System.out.println("OK");

            DirectionsResult e = results;
            GeocodingService gs = new GeocodingService();

            System.out.println("SIZE ROUTES: " + e.getRoutes().size() + "\n" + "ORIGIN: " + e.getRoutes().get(0).getLegs().get(0).getStartLocation());
            System.out.println("LEGS SIZE: " + e.getRoutes().get(0).getLegs().size());
            System.out.println("WAYPOINTS " + e.getGeocodedWaypoints().size());
            try {
                System.out.println("Distancia total = " + e.getRoutes().get(0).getLegs().get(0).getDistance().getText());
            } catch (Exception ex) {
                System.out.println("ERRO: " + ex.getMessage());
            }
            System.out.println("LEG(0)");
            System.out.println(e.getRoutes().get(0).getLegs().get(0).getSteps().size());
        }
    }

    public static MVCArray getMVCLatLongNodo(Nodo nodo) {
        List<LatLong> lista = new ArrayList<>();
        if (nodo.getRef() != null) {    //non è il nodo partenza
            if (nodo.getRef().getFreccia().getPercorso() != null) { //non è un viaggio con blablacar
                return nodo.getRef().getFreccia().getPercorso().getMVCArray();
            }
        }
        return null;
    }

    public DirectionsRequest getDirectionRequest(Nodo arr, Nodo part) {
        DirectionsRequest richiesta = null;
        prendiIndicazioniStradali(arr);
        if (arr.getRef() == null)
            return null;
        if (arr.getRef().getFreccia().getTipo() == TipoPercorrenza.Trasporti)
            richiesta = new DirectionsRequest(
                    part.getCoordinata().getLat()+","+part.getCoordinata().getLong(),
                    arr.getCoordinata().getLat()+","+arr.getCoordinata().getLong(),
                    TravelModes.TRANSIT);
        if (arr.getRef().getFreccia().getTipo() == TipoPercorrenza.BlaBla)
            richiesta = new DirectionsRequest(
                    part.getCoordinata().getLat()+","+part.getCoordinata().getLong(),
                    arr.getCoordinata().getLat()+","+arr.getCoordinata().getLong(),
                    TravelModes.DRIVING);
        if (arr.getRef().getFreccia().getTipo() == TipoPercorrenza.Piedi)
            richiesta = new DirectionsRequest(
                    part.getCoordinata().getLat()+","+part.getCoordinata().getLong(),
                    arr.getCoordinata().getLat()+","+arr.getCoordinata().getLong(),
                    TravelModes.WALKING);
        return richiesta;
    }

    private void prendiIndicazioniStradali(Nodo arr) {
        indicazioni.add("------------------------------------------");
        indicazioni.addAll(arr.getRef().getFreccia().getIndicazioni());
        indicazioni.add("------------------------------------------");
    }

    private String prendiNome(Nodo arr) {
        if (arr instanceof NodoGooglePlaces)
            return ((NodoGooglePlaces) arr).getNome();
        if (arr == arrivo)
            return nomeArrivo;
        if (arr == partenza)
            return nomePartenza;
        System.out.println("Errore in prendinome");
        return "";
    }

    public static String calcolaTempoInverso(long tempo) {
        tempo += 7200; //aggiustamento ad ora locale
        long[] t = new long[1];
        tempo -= 1514764800; //tempo passato fino al 2018 in secondi
        t[0] = tempo;
        int anno = calcolaAnno(2018, t);
        int mese = calcolaMese(1, anno, t);
        tempo = t[0];
        int giorno = 1;
        while (tempo > 0) {
            tempo -= 3600 * 24;
            giorno++;
        }
        tempo += 3600 * 24;
        giorno--;
        int ora = 0;
        while (tempo >= 0) {
            tempo -= 3600;
            ora++;
        }
        if (tempo != 0) {
            tempo += 3600;
            ora--;
        }
        int minuti = 0;
        while (tempo > 0) {
            tempo -= 60;
            minuti++;
        }
        String minutiS=""+minuti;
        if(minuti<=9)
            minutiS = "0"+minuti;
        String oraS=""+ora;
        if(ora<=9)
            oraS="0"+ora;
        String meseS=""+mese;
        if(mese<=9)
            meseS="0"+mese;
        String giornoS = ""+giorno;
        if(giorno<=9)
            giornoS="0"+giorno;
        return (giornoS + "/" + meseS + "/" + anno + " " + oraS + ":" + minutiS);
    }

    private static int calcolaAnno(int anno, long[] secondi) {
        long temp = secondi[0];
        temp -= tempoAnno(anno + 1);
        if (temp > 0) {
            secondi[0] = temp;
            return calcolaAnno(anno + 1, secondi);
        } else
            return anno;
    }

    private static int tempoAnno(int anno) {
        if (annoBisestile(anno))
            return 3600 * 24 * 366;
        return 3600 * 24 * 365;
    }

    private static boolean annoBisestile(int anno) {
        if (anno % 4 != 0)
            return false;
        if (anno % 100 != 0)
            return true;
        if (anno % 400 == 0)
            return true;
        return false;
    }

    private static int calcolaMese(int mese, int anno, long[] secondi) {
        long temp = secondi[0];
        temp -= tempoMese(mese, anno);
        if (temp > 0) {
            secondi[0] = temp;
            return calcolaMese(mese + 1, anno, secondi);
        } else
            return mese;
    }

    private static int tempoMese(int mese, int anno) {
        mese -= 1;
        if (mese == 2 && annoBisestile(anno))
            return 3600 * 24 * 29;
        if (mese == 2 && !annoBisestile(anno))
            return 3600 * 24 * 28;
        if (mese == 4 || mese == 6 || mese == 9 || mese == 11)
            return 3600 * 24 * 30;
        return 3600 * 24 * 31;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
