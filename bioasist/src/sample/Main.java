package sample;

import javafx.application.Application;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main extends Application {

    String dir =  "C:\\DatosPLCS7-1200\\";

    @Override
    public void start(Stage primaryStage) throws Exception{


        String file = this.getArgumentos(0);

        if (file == null){
            System.out.println("Sin Argumentos");
            System.console().readLine();
            System.exit(0);
        }

        String fr= this.readFile(dir + file);

        ArrayList<String> data =this.filtrarDatos(fr);

        NumberAxis numberAxis = new NumberAxis();
        DateAxis dateAxis = new DateAxis();

        LineChart<Date, Number> lineChart = new LineChart<>(dateAxis, numberAxis);


        lineChart.setTitle(file);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.setStyle("CHART_COLOR_1: #ff0000 ; CHART_COLOR_2: #0000FF ;");

        numberAxis.setLabel("Temperatura y Presión");
        dateAxis.setLabel("Hora");

        ObservableList<XYChart.Series<Date, Number>> series = FXCollections.observableArrayList();

        final XYChart.Series<Date, Number> series1 = new XYChart.Series<>();
        ObservableList<XYChart.Data<Date, Number>> series1Data = FXCollections.observableArrayList();

        final XYChart.Series<Date, Number> series2 = new XYChart.Series<>();
        ObservableList<XYChart.Data<Date, Number>> series2Data = FXCollections.observableArrayList();

        int hora;
        int min;
        int ant = -1;
        int dia = 1;
        for (String s:data) {
            System.out.println(s);

            hora =  Integer.parseInt(getHora(s).split(":")[0]);
            min =   Integer.parseInt(getHora(s).split(":")[1]);

            if(hora<ant) {dia++; System.out.println("salto");}

            XYChart.Data a = new XYChart.Data<Date, Number>(new GregorianCalendar(2020,5,dia,hora,min).getTime(), Float.parseFloat(s.split("-")[1].replace(',','.')));
            XYChart.Data b = new XYChart.Data<Date, Number>(new GregorianCalendar(2020, 5, dia ,hora, min).getTime(), Float.parseFloat(s.split("-")[2].replace(',','.')));
            series1Data.add(a);
            series2Data.add(b);

            if(data.indexOf(s) == data.size()-1) a.setNode(createDataNode(a.XValueProperty(),hora + ":" +min));
            ant = hora;
        }


        series1.setName("Gráfica de Presiones");
        series1.setData(series1Data);

        series2.setName("Gráfica Temperatura");
        series2.setData(series2Data);

        series.add(series1);
        series.add(series2);

        lineChart.setData(series);


        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene  = new Scene(lineChart,bounds.getWidth(),bounds.getHeight());

        this.captura(scene.getRoot());

        //primaryStage.show();
        System.exit(0);
    }

    public String getHora(String s){
        String aux[] = s.split("-");
        return aux[0];

    }

    public void captura(Parent obj){

        String  nombre = dir +"\\"+this.getArgumentos(0).split("txt")[0]+"png";

        System.out.println(nombre);

        WritableImage image = obj.snapshot(new SnapshotParameters(), null);

        // TODO: probably use a file chooser here
        File file = new File(nombre);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            this.openImg(nombre);
        } catch (IOException e) {
            // TODO: handle exception here
        }
    }

    public String getArgumentos(int index){
        Parameters params = getParameters();
        return params.getRaw().get(index);

    }

    public String readFile(String url){

        String content = "";

        try
        {
            content = new String ( Files.readAllBytes( Paths.get(url)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return content;
    }

    public ArrayList<String> filtrarDatos(String content){

        String S[] =  content.split("\n");

        ArrayList<String> list = new ArrayList<>();

        Boolean ban = false;
        for (String s:S) {
            if(s.contains("Presi")){
                ban=true;
                continue;
            }

            if(ban && !s.contains("-")){
                String aux[] = s.split("\t");
                list.add(aux[0] + "-" +aux[1] + "-" + aux[2]);
            }else{
                ban = false;
            }
        }

        System.out.println("Cantidad de Lineas Leídas: " + list.size());
        return list;
    }

    public void openImg(String img){
        File f = new File(img);
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFecha(){
        Date date = Calendar.getInstance().getTime();

        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh.mm.ss");
        return  formatter.format(date);
    }

    private static Node createDataNode(ObjectExpression<String> value, String hora) {
        Label label = new Label();
        label.textProperty().bind(value.asString(hora));

        Pane pane = new Pane(label);
        pane.setShape(new Circle(6.0));
        pane.setScaleShape(false);

        label.translateYProperty().bind(label.heightProperty().divide(-1.5));
        label.translateXProperty().bind(label.heightProperty().divide(-1.5));

        return pane;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
