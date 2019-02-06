package ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications;

public class Sender {

    public Data notification;
    public Data data;
    public String to;
    public String priority;

    public Sender(Data notification, Data data, String to, String priority) {
        this.data = data;
        this.to = to;
        this.notification = notification;
        this.priority = priority;
    }

}
