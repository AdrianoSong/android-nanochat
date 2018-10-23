package adrianosong.com.br.nanochat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import adrianosong.com.br.nanochat.R;
import adrianosong.com.br.nanochat.model.ChatMessage;
import adrianosong.com.br.nanochat.singleton.MyVolleySingleton;

/**
 * Created by song on 17/10/16.
 *
 */

public class FirebaseRecyclerAdapter extends RecyclerView.Adapter<FirebaseRecyclerAdapter.MessageViewHolder> {

    private List<ChatMessage> chatMessageList;
    private Context context;

    public FirebaseRecyclerAdapter(Context context, List<ChatMessage> chatMessageList){
        this.chatMessageList = chatMessageList;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item , parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {

        ChatMessage chatMessage = chatMessageList.get(position);

        if (chatMessage.getMessage().contains("https://firebasestorage")) {

            holder.msgImage.setVisibility(View.VISIBLE);
            holder.txtName.setText(chatMessage.getName());

            try {
                // Retrieves an image specified by the URL, displays it in the UI. Via Volley
                ImageRequest imageRequest = new ImageRequest(chatMessage.getMessage(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        holder.msgImage.setImageBitmap(response);
                    }
                }, 0, 0, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                //adicionando a requisicao
                MyVolleySingleton.getInstance(context).addToRequestQueue(imageRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {

            holder.txtName.setText(chatMessage.getName());
            holder.txtMessage.setText(chatMessage.getMessage());
        }

        setAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    /**
     * Here is the key method to apply the animation
     * @param viewToAnimate View
     */
    private void setAnimation(View viewToAnimate)
    {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtMessage;
        ImageView msgImage;

        MessageViewHolder(View itemView){
            super(itemView);

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            msgImage = (ImageView) itemView.findViewById(R.id.msgImage);
        }
    }
}
