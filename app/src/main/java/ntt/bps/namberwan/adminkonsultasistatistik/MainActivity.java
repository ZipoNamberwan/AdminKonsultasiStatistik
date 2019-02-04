package ntt.bps.namberwan.adminkonsultasistatistik;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;

import ntt.bps.namberwan.adminkonsultasistatistik.chat.ChatActivity;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.ChatUtils;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.RecyclerViewClickListener;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.UserDialogAdapter;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.UserModel;

public class MainActivity extends AppCompatActivity {

    public static final int SIGN_IN_REQUEST_CODE = 0;

    private CardView listCardView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private UserModel userModel;

    private ArrayList<String> listIdUsers;
    private ArrayList<UserModel> userModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        progressBar = findViewById(R.id.progress_bar);
        listCardView = findViewById(R.id.card_view);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            setupDialogList();
        }
    }

    private void setupDialogList() {

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        createUserModel();

        updateUserInformation(userModel.getId(), userModel.getUsername(),
                userModel.getUrlPhoto(), userModel.getLastSeen(), true, false);

        displayUserDialog();
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

    private void displayUserDialog() {

        listCardView.setVisibility(View.GONE);

        listIdUsers = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().contains(userModel.getId())){
                    String userId = dataSnapshot.getKey().replace(userModel.getId(), "");
                    if (!userId.equals("")){
                        listIdUsers.add(userId);
                    }
                }
                getDialogs();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void getDialogs() {

        userModels = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for (String id : listIdUsers){
                    if (id.equals(dataSnapshot.getKey())){
                        UserModel temp = dataSnapshot.child("information").getValue(UserModel.class);
                        temp.setId(id);
                        if (!isUserExist(temp)){
                            userModels.add(temp);
                        }
                    }
                }

                UserDialogAdapter adapter = new UserDialogAdapter(userModels, MainActivity.this, new RecyclerViewClickListener() {
                    @Override
                    public void onItemClick(Object object) {
                        Intent i = new Intent(MainActivity.this, ChatActivity.class);
                        i.putExtra(ChatActivity.ID_ADMIN_RECEIVER, ((UserModel) object).getId());
                        i.putExtra(ChatActivity.ID_USER_SENDER, userModel.getId());
                        i.putExtra(ChatActivity.USERNAME_RECEIVER, ((UserModel) object).getUsername());
                        i.putExtra(ChatActivity.URL_PHOTO_RECEIVER, ((UserModel) object).getUrlPhoto());
                        startActivity(i);
                    }
                });

                recyclerView.setAdapter(adapter);

                listCardView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private boolean isUserExist(UserModel temp) {

        boolean isExist = false;

        for (int i = 0; i < userModels.size(); i++){
            if (userModels.get(i).getId().equals(temp.getId())){
                isExist = true;
                break;
            }
        }

        return isExist;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firebaseAuth.getCurrentUser() != null) {
            updateUserInformation(userModel.getId(), userModel.getUsername(),
                    userModel.getUrlPhoto(), System.currentTimeMillis(),
                    true, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth.getCurrentUser() != null) {
            updateUserInformation(userModel.getId(), userModel.getUsername(),
                    userModel.getUrlPhoto(), System.currentTimeMillis(),
                    false, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                setupDialogList();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                onPause();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            if(firebaseAuth.getCurrentUser() != null) {
                updateUserInformation(userModel.getId(), userModel.getUsername(),
                        userModel.getUrlPhoto(), System.currentTimeMillis(),
                        false, false);
            }
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }/*else if (item.getItemId() == R.id.menu_edit_profile){
            Intent intent = new Intent(this, SettingsProfileActivity.class);
            startActivity(intent);
        }*/
        return super.onOptionsItemSelected(item);
    }
}
