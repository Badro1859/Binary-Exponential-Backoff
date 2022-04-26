package sample;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Node{

    enum nextState {
        SEND_CTS,
        SEND_ACK,
        SEND_DATA
    }


    public Random rnd;

    private NodeState state;
    public boolean FirstTrans = true;
    public boolean sending = false;
    public int limitTrans = 4;

    public int CW_val = 0;
    public int CW_min = 0;
    public int CW_max = 0;

    public int nbSucc = 0;
    public int nbColl = 0;
    public int NB_msg = 0;

    public int ratio = 20;
    public int DIFS = 3;
    public int SIFS = 2;
    public int EIFS = 4;
    public int TransTime = 4;
    public int NAV = 12;
    public int nTrans = 0;



    AnchorPane container = null;
    ImageView img = null;
    Label l_Log = null;
    Label l_nbSucc = null;
    Label l_nbColl = null;
    Label l_CW = null;

    public Node(int proba, int timeTrans, int sifs, int difs, int eifs, int limit) {
        rnd = new Random();
        ratio = proba;
        DIFS = difs;
        EIFS = eifs;
        SIFS = sifs;
        TransTime = timeTrans;
        limitTrans = limit;


        state = new BaseState(this);
        initComponent();
    }

    private void initComponent() {
        container = new AnchorPane();

        container.setPrefHeight(120.0);
        container.setPrefWidth(84.0);
        container.setStyle("-fx-background-color: #D3FFA0;");

        Font font = new Font("Courier New Bold",14.0);

        l_Log = new Label();
        l_Log.setLayoutY(84.0);
        l_Log.setPrefHeight(16.0);
        l_Log.setPrefWidth(84.0);
        l_Log.setAlignment(Pos.CENTER);
        l_Log.setText("MSG:00");
        l_Log.setFont(font);

        container.getChildren().add(l_Log);

        img = new ImageView();
        img.setFitHeight(60.0);
        img.setFitWidth(90.0);
        img.setLayoutX(13.0);
        img.setLayoutY(20.0);
        img.setPickOnBounds(true);
        img.setPreserveRatio(true);
        img.setImage(new Image(getClass().getResource("pc.png").toExternalForm()));

        container.getChildren().add(img);


        l_nbSucc = new Label();
        l_nbSucc.setLayoutX(42.0);
        l_nbSucc.setPrefHeight(18.0);
        l_nbSucc.setPrefWidth(42.0);
        l_nbSucc.setAlignment(Pos.CENTER);
        l_nbSucc.setStyle("-fx-background-color: #67FF4F;");
        l_nbSucc.setText("00");
        l_nbSucc.setFont(font);

        container.getChildren().add(l_nbSucc);

        l_nbColl = new Label();
        l_nbColl.setPrefHeight(18.0);
        l_nbColl.setPrefWidth(42.0);
        l_nbColl.setAlignment(Pos.CENTER);
        l_nbColl.setFont(font);
        l_nbColl.setStyle("-fx-background-color: #FF7865;");
        l_nbColl.setText("00");

        container.getChildren().add(l_nbColl);

        l_CW = new Label();
        l_CW.setLayoutY(103.0);
        l_CW.setPrefHeight(16.0);
        l_CW.setPrefWidth(84.0);
        l_CW.setAlignment(Pos.CENTER);
        l_CW.setFont(font);
        l_CW.setText("");

        container.getChildren().add(l_CW);
    }

    public void set(boolean mediaOccuped, boolean inColl, boolean onReceive) {
        if (onReceive)
            setState(new SifsState(this,nextState.SEND_CTS));
        else
            state.set(mediaOccuped, inColl);
        updateView();
    }


    private void updateView() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(()->{
                    l_nbColl.setText(String.format("%2d", nbColl));
                    l_nbSucc.setText(String.format("%2d", nbSucc));
                    l_Log.setText(state.getValueForLog());
                    container.setStyle("-fx-background-color:" + state.BACKGROUND + ";");

                    if (state.getClass() == DifsState.class || state.getClass() == EifsState.class)
                        l_CW.setText(String.format("CW:[0-%2d]", CW_max-1));
                    else
                        l_CW.setText("");
                });
            }
        },1, TimeUnit.NANOSECONDS);

    }

    public void setCW_val(){
        CW_val = CW_min + rnd.nextInt(CW_max-1);
    }

    public void setCWRetry() {
        if (nTrans < limitTrans){
            nTrans++;
            CW_max *= 2;
            if (CW_max > 256){
                CW_max = 256;
            }
            setCW_val();
        }
        else {
            setState(new BaseState(this));
        }
    }

    public void setState(NodeState st){
        state = st;
    }
}
