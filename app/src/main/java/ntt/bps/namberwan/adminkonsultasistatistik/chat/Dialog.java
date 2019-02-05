package ntt.bps.namberwan.adminkonsultasistatistik.chat;

public class Dialog {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(long messageSent) {
        this.messageSent = messageSent;
    }

    public Dialog(String id, String idSender, String username, String urlPhoto, String lastMessage, long messageSent) {

        this.id = id;
        this.idSender = idSender;
        this.username = username;
        this.urlPhoto = urlPhoto;
        this.lastMessage = lastMessage;
        this.messageSent = messageSent;
    }

    public Dialog() {

    }

    private String idSender;
    private String username;
    private String urlPhoto;
    private String lastMessage;
    private long messageSent;


}
