package ntt.bps.namberwan.adminkonsultasistatistik.chat;

public class UserModel {

    private String id;
    private String username;
    private String urlPhoto;
    private long lastSeen;
    private boolean isOnline;
    private boolean isTyping;

    public UserModel(){

    }

    public UserModel(String id, String username){
        this.setId(id);
        this.setUsername(username);
    }

    public UserModel(String id, String username, String urlPhoto){
        this.setId(id);
        this.setUsername(username);
        this.setUrlPhoto(urlPhoto);
    }

    public UserModel(String id, String username, String urlPhoto, long lastSeen){
        this.setId(id);
        this.setUsername(username);
        this.setUrlPhoto(urlPhoto);
        this.setLastSeen(lastSeen);
    }

    public UserModel(String id, String username, String urlPhoto, long lastSeen, boolean isOnline, boolean isTyping){
        this.setId(id);
        this.setUsername(username);
        this.setUrlPhoto(urlPhoto);
        this.setLastSeen(lastSeen);
        this.setOnline(isOnline);
        this.setTyping(isTyping);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean getIsTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

}
