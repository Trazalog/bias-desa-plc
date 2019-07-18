package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        ArrayList<String> list = this.readFile(this.getArgumentos());

        stage.setTitle("Bioasist Gráfico");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Fechas");
        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setAnimated(false);
        lineChart.setTitle("Gráfica Bioasist, 2010");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Presiones Registradas");

        String[] aux = null;

        for (String s: list) {
            aux =  s.split(" ");
            series1.getData().add(new XYChart.Data(aux[0], Float.parseFloat(aux[1])));
        }




        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series1);

        stage.setScene(scene);

        this.captura(scene.getRoot());

        System.exit(0);
    }

    public void captura(Parent obj){
        String  nombre = "Gráfica Presión ("+this.getFecha()+").png";

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

    public String getArgumentos(){
        Parameters params = getParameters();
        return params.getRaw().get(0);

    }

    public ArrayList<String> readFile(String url){

        ArrayList<String> list = new ArrayList<>();
        try{
        File file =
                new File(url);
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine())
            list.add(sc.nextLine());
        }catch (Exception e){
            System.out.println("Error de Lectura");
        }

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
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh.mm");
        return  formatter.format(date);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
