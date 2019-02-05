package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntt.bps.namberwan.adminkonsultasistatistik.R;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.Client;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.Data;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.MyResponse;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.Sender;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements MessageInput.InputListener, MessageInput.TypingListener {

    public static final String ID_USER_SENDER = "id sender";
    public static final String ID_ADMIN_RECEIVER = "id receiver";
    public static final String USERNAME_RECEIVER = "username receiver";
    public static final String URL_PHOTO_RECEIVER = "url photo receiver";


    private MessagesListAdapter<Message> adapter;

    private User sender;
    private User receiver;

    private String idSender;
    private String idChat;
    private String idReceiver;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;

    private UserModel userModel;

    private APIService apiService;

    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setupFirebase();

        createUserModel();

        updateUserInformation(userModel.getId(), userModel.getUsername(),
                userModel.getUrlPhoto(), userModel.getLastSeen(), true, false);

        setupReceiverInformation();

        setupChatRoom();

        initView();

    }

    private void createUserModel() {
        String idUser = firebaseAuth.getCurrentUser().getUid();
        String username = "";
        if (firebaseAuth.getCurrentUser().getDisplayName() != null){
            username = firebaseAuth.getCurrentUser().getDisplayName();
        }
        String urlPhoto = "";
        if (firebaseAuth.getCurrentUser().getPhotoUrl() != null){
            urlPhoto = firebaseAuth.getCurrentUser().getPhotoUrl().toString();
        }
        long lastSeen = System.currentTimeMillis();

        userModel = new UserModel(idUser, username, urlPhoto, lastSeen);
    }

    private void updateUserInformation(String idUser, String username, String urlPhoto, long lastSeen, boolean isOnline, boolean isTyping) {

        String key = reference.child(idUser).getKey();

        Map<String, Object> childUpdates = ChatUtils.updateUserInformation(key, username, urlPhoto, lastSeen, isOnline, isTyping);

        reference.updateChildren(childUpdates);
    }

    private void setupReceiverInformation() {

        idReceiver = getIntent().getStringExtra(ID_ADMIN_RECEIVER);
        String usernameReceiver = getIntent().getStringExtra(USERNAME_RECEIVER);
        String photoReceiver = getIntent().getStringExtra(URL_PHOTO_RECEIVER);

        UserModel receiverUM = new UserModel(idReceiver, usernameReceiver, photoReceiver);

        receiver = new User(receiverUM.getId(), receiverUM.getUsername(), receiverUM.getUrlPhoto(), "", true);

        setTitle(usernameReceiver);

        if (getSupportActionBar()!=null){

            DatabaseReference reference = firebaseDatabase.getReference("Users").child(receiver.getId());
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UserModel temp = dataSnapshot.getValue(UserModel.class);
                    String status = ChatUtils.getStatusString(ChatActivity.this, temp);
                    if (getSupportActionBar()!=null){
                        getSupportActionBar().setSubtitle(status);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UserModel temp = dataSnapshot.getValue(UserModel.class);
                    String status = ChatUtils.getStatusString(ChatActivity.this, temp);
                    if (getSupportActionBar()!=null){
                        getSupportActionBar().setSubtitle(status);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setupFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    private void setupChatRoom() {

        idSender = getIntent().getStringExtra(ID_USER_SENDER);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String urlAvatar = "";

        if (firebaseUser.getPhotoUrl()!=null){
            urlAvatar = firebaseUser.getPhotoUrl().toString();
        }

        sender = new User(idSender, firebaseUser.getDisplayName(), urlAvatar, "",true);

        idChat = createIdChat(idSender, idReceiver);

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private String createIdChat(String idSender, String idReceiver) {
        ArrayList<String> list = new ArrayList<>();
        list.add(idSender);
        list.add(idReceiver);
        Collections.sort(list);
        return list.get(0) + list.get(1);
    }

    private void initView() {

        MessageInput messageInput = findViewById(R.id.input);
        messageInput.setInputListener(this);
        messageInput.setTypingListener(this);

        MessagesList messagesList = findViewById(R.id.messages_list);

        //initial setup adapter
        adapter = new MessagesListAdapter<>(idSender, null);
        messagesList.setAdapter(adapter);

        DateFormatter.Formatter formatter = new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return getString(R.string.date_header_today);
                } else if (DateFormatter.isYesterday(date)) {
                    return getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
        };

        adapter.setDateHeadersFormatter(formatter);

        displayMessage(idSender, idReceiver);
    }

    private void displayMessage(final String idSender, final String idReceiver) {

        //display history
        final List<Message> messages = new ArrayList<>();
        final DatabaseReference reference = firebaseDatabase.getReference("Chats").child(idChat);
        reference.keepSynced(true);
        reference.orderByChild("createdAt");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MessageModel temp = snapshot.getValue(MessageModel.class);
                    if (temp.getSender().equals(idSender) && temp.getReceiver().equals(idReceiver)
                            || temp.getSender().equals(idReceiver) && temp.getReceiver().equals(idSender)){
                        User user = new User(temp.getSender(), "", "", "", false);
                        Message message = new Message(temp.getSender(), user, temp.getMessage(), new Date(temp.getCreatedAt()));
                        messages.add(message);
                    }
                }

                //adapter = new MessagesListAdapter<>(idSender, null);
                adapter.clear();
                adapter.addToEnd(messages, true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(CharSequence input) {

        DatabaseReference reference = firebaseDatabase.getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender.getId());
        hashMap.put("receiver", receiver.getId());
        hashMap.put("message", input.toString());
        long now = System.currentTimeMillis();
        hashMap.put("createdAt", now);

        reference.child("Chats").child(idChat).push().setValue(hashMap);

        Message message = new Message(idSender, sender, input.toString());
        adapter.addToStart(message, true);

        if (notify) {
            sendNotification(idReceiver, userModel.getUsername(), input.toString());
        }
        notify = false;

        makeChatList(input.toString(), now);
    }

    private void makeChatList(String input, long now) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(userModel.getId());

        Map<String, Object> update = new HashMap<>();
        update.put("username", receiver.getName());
        if (receiver.getAvatar() != null){
            update.put("urlPhoto", receiver.getAvatar());
        } else {
            update.put("urlPhoto", "");
        }

        update.put("lastMessage", input);

        update.put("messageSent", now);

        update.put("idSender", userModel.getId());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + idReceiver + "/", update);

        reference.updateChildren(childUpdates);
    }

    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(idSender, userModel.getUsername(), userModel.getUrlPhoto(), R.drawable.ic_bps_launcher, message, username + " (Admin BPS)", idReceiver);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<MyResponse> call, Response<MyResponse> response) {

                        }

                        @Override
                        public void onFailure(@NonNull Call<MyResponse> call, Throwable t) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        if (!input.equals("")){
            notify = true;
            sendMessage(input);
        } else {
            Toast.makeText(ChatActivity.this, "Isi pesan kosong, mau kirim apa?", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseAuth.getCurrentUser() != null){
            updateUserInformation(userModel.getId(), userModel.getUsername(),
                    userModel.getUrlPhoto(), System.currentTimeMillis(),
                    true, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth.getCurrentUser() != null){
            updateUserInformation(userModel.getId(), userModel.getUsername(),
                    userModel.getUrlPhoto(), System.currentTimeMillis(),
                    false, false);
        }
    }

    @Override
    public void onStartTyping() {
        updateUserInformation(userModel.getId(), userModel.getUsername(),
                userModel.getUrlPhoto(), System.currentTimeMillis(),
                true, true);
    }

    @Override
    public void onStopTyping() {
        updateUserInformation(userModel.getId(), userModel.getUsername(),
                userModel.getUrlPhoto(), System.currentTimeMillis(),
                true, false);
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(idSender).setValue(token1);
    }
}
