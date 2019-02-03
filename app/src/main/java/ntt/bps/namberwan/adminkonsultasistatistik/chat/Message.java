package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

public class Message implements IMessage {

    private String idSender;
    private String message;
    private Date createdAt;
    private User user;

    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }

    public Message(String idSender, User user, String message, Date createdAt) {
        this.idSender = idSender;
        this.message = message;
        this.user = user;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return this.idSender;
    }

    @Override
    public String getText() {
        return this.message;
    }

    @Override
    public IUser getUser() {
        return this.user;
    }

    @Override
    public Date getCreatedAt() {
        return this.createdAt;
    }

}
