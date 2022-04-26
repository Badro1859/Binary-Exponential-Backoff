package sample;

public abstract class NodeState {

    protected final Node node;
    protected String BACKGROUND = "#D3FFA0";
    NodeState(Node nd){
        node = nd;

    }

    public abstract void set(boolean mediaOccuped, boolean inColl);

    public abstract String getValueForLog();

}

class BaseState extends NodeState {

    public BaseState(Node nd){
        super(nd);

        node.FirstTrans = true;
        node.CW_max = 8;
        node.setCW_val();
        node.sending = false;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (mediaOccuped)
            node.setState(new NavState(node, false));
        generateMsg();
        if (node.NB_msg > 0)
            node.setState(new DifsState(node));
    }

    @Override
    public String getValueForLog() {
        return String.format("MSG:%2d", node.NB_msg);
    }

    public void generateMsg(){
        node.NB_msg += (node.rnd.nextInt(100) < node.ratio)? 1: 0;
    }

}

class NavState extends NodeState {
    private int nav;
    private boolean active;

    public NavState(Node nd,boolean inSending){
        super(nd);
        super.BACKGROUND = "#FFCF2B";
        active = inSending;
        nav = node.NAV;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (nav > 0){
            nav--;
        }
        else {
            if (active)
                node.setState(new DifsState(node));
            else
                node.setState(new BaseState(node));
        }


    }

    @Override
    public String getValueForLog() {
        return String.format("NAV:%2d", nav);
    }
}

class DifsState extends NodeState {
    private int DIFS;

    public DifsState(Node nd) {
        super(nd);
        DIFS = node.DIFS;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (mediaOccuped && !inColl)
            node.setState(new NavState(node, true));
        else if (mediaOccuped)
            DIFS = node.DIFS;
        else {
            if (DIFS > 0)
                DIFS--;
            else {
                if (node.FirstTrans){

                    node.setState(new SendRTS(node));
                }
                else {
                    node.setState(new BackoffState(node));
                }
            }
        }

    }

    @Override
    public String getValueForLog() {
        return String.format("DIFS:%2d", DIFS);
    }
}

class BackoffState extends NodeState {

    public BackoffState(Node nd){
        super(nd);

    }
    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (mediaOccuped && !inColl)
            node.setState(new NavState(node, true));
        else if (mediaOccuped)
            node.setState(new DifsState(node));
        else{
            if (node.CW_val > 0)
                node.CW_val--;
            else {
                node.setState(new SendRTS(node));
            }
        }
    }

    @Override
    public String getValueForLog() {
        return String.format("BEB:%2d", node.CW_val);
    }

}

class SifsState extends NodeState {
    private int Time;
    private Node.nextState nextState;
    public SifsState(Node nd, Node.nextState nextState){
        super(nd);
        super.BACKGROUND = "#7797FF";

        this.nextState = nextState;
        Time = node.SIFS;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (inColl) {
            node.setState(new CollisionState(node));
        }
        else {
            if (Time > 0){
                Time--;
            }
            else {
                if (nextState == Node.nextState.SEND_CTS)
                    node.setState(new SendCTS(node));
                else if (nextState == Node.nextState.SEND_ACK)
                    node.setState(new SendACK(node));
                else
                    node.setState(new SendData(node));
            }
        }
    }

    @Override
    public String getValueForLog() {
        return String.format("SIFS:%2d", Time);
    }
}

class SendRTS extends NodeState {
    private int Time;

    public SendRTS(Node nd){
        super(nd);
        super.BACKGROUND = "#88FF42";
        node.sending = true;
        Time = node.SIFS + 2;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (inColl) {
            node.setState(new CollisionState(node));
        }
        else {
            if (Time > 0){
                Time--;
            }
            else {
                node.setState(new SendData(node));
            }
        }
    }

    @Override
    public String getValueForLog() {
        return "Send:RTS";
    }
}

class SendData extends NodeState {
    private int Time;
    private String log;
    public SendData(Node nd){
        super(nd);
        super.BACKGROUND = "#88FF42";
        node.sending = false;

        Time = node.TransTime + node.SIFS;
        log = "Send:DATA";
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (inColl) {
            node.setState(new CollisionState(node));
        }
        else {
            if (Time == node.SIFS)
                log = "Waiting";
            if (Time > 0){
                Time--;
            }
            else {
                node.nbSucc++;
                node.NB_msg--;
                node.setState(new BaseState(node));
            }
        }
    }

    @Override
    public String getValueForLog() {
        return log;
    }
}

class SendCTS extends NodeState {
    private int Time;

    public SendCTS(Node nd){
        super(nd);
        super.BACKGROUND = "#7797FF";
        Time = node.SIFS - 1;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (inColl) {
            node.setState(new CollisionState(node));
        }
        else {
            if (Time > 0){
                Time--;
            }
            else {
                node.setState(new RecvData(node));
            }
        }
    }

    @Override
    public String getValueForLog() {
        return "Send:CTS";
    }
}

class SendACK extends NodeState {
    private int Time;

    public SendACK(Node nd){
        super(nd);
        super.BACKGROUND = "#7797FF";
        Time = 1;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (inColl) {
            node.setState(new CollisionState(node));
        }
        else {
            if (Time > 0){
                Time--;
            }
            else {
                node.setState(new BaseState(node));
            }
        }
    }

    @Override
    public String getValueForLog() {
        return "Send:ACK";
    }
}

class RecvData extends NodeState {
    private int TimeRecv;

    public RecvData(Node nd){
        super(nd);
        super.BACKGROUND = "#7797FF";

        TimeRecv = node.TransTime - node.SIFS - 1;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (inColl) {
            node.setState(new CollisionState(node));
        }
        else {
            if (TimeRecv > 0){
                TimeRecv--;
            }
            else {
                node.setState(new SifsState(node, Node.nextState.SEND_ACK));
            }
        }

    }

    @Override
    public String getValueForLog() {
        return "Recv:DATA";
    }
}

class CollisionState extends NodeState {
    private int timeOfCollision;

    CollisionState(Node nd){
        super(nd);
        super.BACKGROUND = "#FF504A";

        node.nbColl++;
        node.FirstTrans = false;
        node.setCWRetry();
        timeOfCollision = 2;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {

        if (timeOfCollision > 0)
            timeOfCollision--;
        else {
            node.setState(new EifsState(node));
        }

    }

    @Override
    public String getValueForLog() {
        return "Collision";
    }
}

class EifsState extends NodeState {
    private int EIFS;

    public EifsState(Node nd){
        super(nd);

        node.sending = false;
        EIFS = node.EIFS;
    }

    @Override
    public void set(boolean mediaOccuped, boolean inColl) {
        if (mediaOccuped && !inColl)
            node.setState(new NavState(node, true));
        else if (mediaOccuped)
            EIFS = node.EIFS;
        else {
            if (EIFS > 0)
                EIFS--;
            else
                node.setState(new BackoffState(node));
        }
    }

    @Override
    public String getValueForLog() {
        return String.format("EIFS:%2d", EIFS);
    }
}



