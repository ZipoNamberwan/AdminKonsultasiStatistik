package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import android.app.Activity;

import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntt.bps.namberwan.adminkonsultasistatistik.R;

public class ChatUtils {

    public static List<UserModel> getAdminList(){
        ArrayList<UserModel> adminUsers = new ArrayList<>();
        adminUsers.add(new UserModel("HV8p5WYKBDVewiRiYyJkePhpNbi1", "Test Admin"));
        adminUsers.add(new UserModel("xZMJl3pua3N5BIj96O4PcZ5iPB53", "Test Admin 2"));
        //adminUsers.add(new UserModel("oDEQuIwa5tf8ElXfeqja3aVb0Ux2", "Test Admin 3"));

        return adminUsers;
    }

    public static UserModel getAdminById(String id){
        UserModel u = null;
        for (UserModel user : getAdminList()){
            if (id.equals(user.getId())){
                u = user;
                break;
            }
        }
        return u;
    }

    public static Map<String, Object> updateUserInformation(String key, String username, String urlPhoto, long lastSeen, boolean isOnline, boolean isTyping) {

        Map<String, Object> update = new HashMap<>();
        update.put("username", username);
        if (urlPhoto != null){
            update.put("urlPhoto", urlPhoto);
        } else {
            update.put("urlPhoto", "");
        }

        update.put("lastSeen", lastSeen);
        if (isOnline){
            update.put("isOnline", true);
        } else {
            update.put("isOnline", false);
        }

        update.put("isTyping", isTyping);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Users/" + key + "/information/", update);

        return childUpdates;
    }


    public static String getStatusString(Activity activity, UserModel temp) {
        String status;
        if (temp.getIsOnline()){
            status = "Online";
            if (temp.getIsTyping()){
                status = "is typing...";
            }
        }else {
            status = getLastSeen(activity, temp.getLastSeen());
        }
        return status;
    }

    public static String getLastSeen(Activity activity, long lastSeen){
        Date date = new Date(lastSeen);
        String status = "last seen ";
        String time = DateFormatter.format(date, DateFormatter.Template.TIME);
        if (DateFormatter.isToday(date)){
            status = status + activity.getString(R.string.date_header_today) + " at " + time;
        } else if (DateFormatter.isYesterday(date)){
            status = status + activity.getString(R.string.date_header_yesterday) + " at " + time;
        } else {
            status = status + DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR) + " at " + time;
        }
        return status;
    }
}
