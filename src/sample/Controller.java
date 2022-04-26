package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class Controller implements TimerChangeListener{
    private boolean started = false;

    private boolean mediaFull = false;

    int TimeTrans;
    int Sifs;
    int Difs;
    int Eifs;
    int Ratio;
    int Limit;

    public int PROBA = 40;
    List<Node> list = new ArrayList<Node>();



    public Controller() {
        Lookup.getInstance().getService(TimeProvider.class).addTimeChangeListener(this);
    }

    @FXML
    private Pane network;
    public void addNode(MouseEvent event){
        if (!started && event.getY() < 500){

            Node newNode = new Node(Ratio, TimeTrans, Sifs, Difs, Eifs, Limit);
            newNode.container.setLayoutX(event.getX()-40.0);
            newNode.container.setLayoutY(event.getY()-60.0);

            network.getChildren().add(newNode.container);
            list.add(newNode);
        }
    }

    public void clear(){
        ObservableList l = network.getChildren();
        for (int k=0; k<l.size(); k++)
            if (l.get(k).getClass() == AnchorPane.class){
                l.remove(k);
                k--;
            }

    }

    @FXML
    Button startBTN;
    public void start() {
        started = !started;
        startBTN.setText( started? "STOP":"START");
        startBTN.setStyle(started? "-fx-background-color:#FF8670;":"-fx-background-color:#26FFAD;");
    }

    @FXML
    private TextField timeTrans;
    @FXML
    private TextField difs;
    @FXML
    private TextField eifs;
    @FXML
    private TextField ratio;
    @FXML
    private TextField sifs;
    @FXML
    private TextField limit;
    @FXML
    Pane frontPage;
    public void hideFrontPage(){

        TimeTrans = parseInt(timeTrans.getText());
        Sifs = parseInt(sifs.getText());
        Difs = parseInt(difs.getText());
        Eifs = parseInt(eifs.getText());
        Ratio = parseInt(ratio.getText());
        Limit = parseInt(limit.getText());

        frontPage.setVisible(false);
        network.setVisible(true);
    }

    private boolean onSending = false;
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(started) {

            boolean mediaFull = false;
            boolean collision = false;
            int sender = -1;
            int receiver =-1;
            int nbSending = 0;

            for (int k = 0; k < list.size(); k++) {
                if (list.get(k).sending) {
                    mediaFull = true;
                    nbSending++;
                    sender = k;
                    if (nbSending > 1) {
                        collision = true;
                        break;
                    }
                }
            }

            if (nbSending ==1 && !onSending){
                onSending = true;
                receiver = sender;
                while (receiver == sender)
                    receiver = new Random().nextInt(list.size());
            }
            if (nbSending == 0)
                onSending = false;


            for (int k = 0; k < list.size(); k++) {
                if (receiver == k)
                    list.get(k).set(mediaFull, collision, true);
                else
                    list.get(k).set(mediaFull, collision, false);
            }

        }
    }

    DropShadow shadow = new DropShadow();


    @FXML
    private Button startSimulation;
    public void activeShdow1(MouseEvent mouseEvent) {
        startSimulation.setEffect(shadow);
    }


    public void stopShadow1(MouseEvent mouseEvent) {
        startSimulation.setEffect(null);
    }


    public void activeShdow2(MouseEvent mouseEvent) {
        startBTN.setEffect(shadow);
    }

    public void stopShadow2(MouseEvent mouseEvent) {
        startBTN.setEffect(null);
    }

    @FXML
    Button clear;
    public void activeShdow3(MouseEvent mouseEvent) {
        clear.setEffect(shadow);
    }

    public void stopShadow3(MouseEvent mouseEvent) {
        clear.setEffect(null);
    }
}
