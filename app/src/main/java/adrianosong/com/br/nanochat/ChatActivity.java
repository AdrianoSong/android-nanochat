package adrianosong.com.br.nanochat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import adrianosong.com.br.nanochat.adapter.FirebaseRecyclerAdapter;
import adrianosong.com.br.nanochat.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private static final String TAG = ChatActivity.class.getSimpleName();

    private EditText editMessage;
    private Button btnSend;
    private RecyclerView recyclerViewMessages;
    private ProgressBar progressBarLoadChat;
    private ImageView imgPickImage;

    private List<ChatMessage> chatMessageList;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editMessage = (EditText) findViewById(R.id.editMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        recyclerViewMessages = (RecyclerView) findViewById(R.id.recyclerViewMessages);
        progressBarLoadChat = (ProgressBar) findViewById(R.id.progressBarLoadChat);
        imgPickImage = (ImageView) findViewById(R.id.imgPickImage);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());

        progressBarLoadChat.animate();

        recyclerViewMessages.setLayoutManager(mLinearLayoutManager);

        chatMessageList = new ArrayList<>();

        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        if (firebaseApp != null) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
            FirebaseStorage storage = FirebaseStorage.getInstance(firebaseApp);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            databaseReference = firebaseDatabase.getReference("chat");
            storageReference = storage.getReference("chat_photos");

            user = auth.getCurrentUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            uploadSelectedImage(data);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editMessage.getText().toString().equals("")){
                    addItem(editMessage);
                }
            }
        });

        imgPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // your action here
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecione imagem a ser compartilhada"), PICK_IMAGE);
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                chatMessageList.add(chatMessage);
                addItemFromFirebaseCloud(chatMessageList, recyclerViewMessages);

                progressBarLoadChat.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * When users stop typing and press "Enviar" button
     * @param editMessage EditText
     */
    private void addItem(EditText editMessage){

        ChatMessage chatMessage = new ChatMessage(user.getEmail(), editMessage.getText().toString());

        editMessage.setText("");

        databaseReference.push().setValue(chatMessage);

    }

    /**
     * Fetch recyclerview with firebase cloud database data
     * @param chatMessageList List<ChatMessage>
     * @param recyclerView RecyclerView
     */
    private void addItemFromFirebaseCloud(List<ChatMessage> chatMessageList, RecyclerView recyclerView){

        if (chatMessageList.size() <= 1){
            FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter(getApplicationContext(), chatMessageList);
            recyclerView.setAdapter(adapter);

        }else{
            recyclerView.getAdapter().notifyItemInserted(chatMessageList.size() + 1);
            recyclerView.smoothScrollToPosition(chatMessageList.size() + 1);
        }
    }

    /**
     * Upload image taken from gallery to firebase storage
     * @param data Intent
     */
    private void uploadSelectedImage(Intent data){
        //get image uri
        Uri selectedImage = data.getData();

        //photo ref with abslotute path
        StorageReference photoRef = storageReference.child(selectedImage.getLastPathSegment());

        //UploadImage to firebase
        photoRef.putFile(selectedImage)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        if (downloadUrl != null) {
                            editMessage.setText(downloadUrl.toString());
                            addItem(editMessage);

                        }else{
                            Log.e(TAG, "Nao deu certo para pegar a url da imagem");
                        }
                    }
                });
    }
}
