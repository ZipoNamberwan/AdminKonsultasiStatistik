package ntt.bps.namberwan.adminkonsultasistatistik.chat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;

import java.util.List;

import ntt.bps.namberwan.adminkonsultasistatistik.R;

public class UserDialogAdapter extends RecyclerView.Adapter<UserDialogAdapter.Holder> {

    private List<UserModel> list;
    private Context context;
    private RecyclerViewClickListener listener;

    public UserDialogAdapter(List<UserModel> list, Context context, RecyclerViewClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_dialog_adapter, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final UserModel user = getList().get(position);
        holder.bind(user, listener);
        holder.nama.setText(user.getUsername());

        if (user.getIsOnline()){
            holder.lastSeen.setText("Online");
            holder.lastSeen.setTextColor(context.getResources().getColor(R.color.md_green_500));
        }else {
            holder.lastSeen.setText(ChatUtils.getLastSeen((Activity) context, user.getLastSeen()));
        }

        if (user.getUrlPhoto() != null){
            if (!user.getUrlPhoto().equals("")){
                Picasso.get()
                        .load(user.getUrlPhoto())
                        .error(new IconicsDrawable(context).color(ContextCompat.getColor(context, R.color.md_grey_300)).icon(GoogleMaterial.Icon.gmd_broken_image))
                        .placeholder(new IconicsDrawable(context).color(ContextCompat.getColor(context, R.color.md_grey_300)).icon(GoogleMaterial.Icon.gmd_image))
                        .fit()
                        .into(holder.photo);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getList().size();
    }

    public List<UserModel> getList() {
        return list;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView nama;
        TextView lastSeen;
        ImageView photo;

        public Holder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.nama);
            lastSeen = itemView.findViewById(R.id.last_seen);
            photo = itemView.findViewById(R.id.foto);
        }

        public void bind(final UserModel user, final RecyclerViewClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(user);
                }
            });
        }
    }
}
