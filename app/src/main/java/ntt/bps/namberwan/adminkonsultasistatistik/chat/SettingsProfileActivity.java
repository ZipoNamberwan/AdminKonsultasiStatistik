package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ntt.bps.namberwan.adminkonsultasistatistik.R;

public class SettingsProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        //updateProfil(firebaseAuth.getCurrentUser().getUid(),"Indra Achmad Sofian Souri S.ST, M.Si", "https://community.bps.go.id/images/avatar/340015532_20170620102514.jpg");
        updateProfil(firebaseAuth.getCurrentUser().getUid(),"Nofriana Florida Djami Raga SST", "https://community.bps.go.id/images/avatar/57164.jpg");
    }

    private void updateProfil(String idUser, String username, String photo){

        Uri uri = Uri.parse(photo);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Do Something
                            Toast.makeText(SettingsProfileActivity.this,"Sukses", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String key = reference.child(idUser).getKey();

        Map<String, Object> childUpdates = ChatUtils.updateUserInformation(key, username, photo, System.currentTimeMillis(), true, false);

        reference.updateChildren(childUpdates);*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
