package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import com.stfalcon.chatkit.commons.models.IUser;

/*
 * Created by troy379 on 04.04.17.
 */
public class User implements IUser {

    private String id;
    private String name;
    private String avatar;
    private String lastSeen;
    private boolean online;

    public User(String id, String name, String avatar, String lastSeen, boolean online) {
        this.id = id;
        this.name = name;
        this.lastSeen = lastSeen;
        this.avatar = avatar;
        this.online = online;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public boolean isOnline() {
        return online;
    }

    public String getLastSeen() {
        return lastSeen;
    }
}