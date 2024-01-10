package com.example.josechat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.josechat.Utils.AndroidUtil;
import com.example.josechat.Utils.FirebaseUtil;
import com.example.josechat.adapter.ChatRecyclerAdapter;
import com.example.josechat.model.ChatMessageModel;
import com.example.josechat.model.ChatroomModel;
import com.example.josechat.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser= AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId= FirebaseUtil.getChatroomId(FirebaseUtil.currentUserID(), otherUser.getUserId());
        messageInput=findViewById(R.id.chat_message_input);
        sendMessageBtn=findViewById(R.id.message_send_btn);
        backBtn=findViewById(R.id.back_btn);
        otherUsername=findViewById(R.id.other_username);
        recyclerView=findViewById(R.id.chat_recyclerview);
        imageView=findViewById(R.id.profile_pìc_image_view);

        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl().addOnCompleteListener(t -> {
            if(t.isSuccessful()){
                Uri uri =t.getResult();
                AndroidUtil.setProfilePic(this, uri, imageView);
            }
        });

        backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUserName());

        sendMessageBtn.setOnClickListener(view -> {
            String message=messageInput.getText().toString().trim();
            if(message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
        });

        getOrCreateChatroomModel();
        setUpChatRecyclerView();
    }

    void setUpChatRecyclerView(){
        Query query= FirebaseUtil.getChatroomMessageReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>().setQuery(query, ChatMessageModel.class).build();

        adapter=new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager= new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if (task.getResult().exists()) {
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                } else {
                    chatroomModel = new ChatroomModel(chatroomId, Arrays.asList(FirebaseUtil.currentUserID(), otherUser.getUserId()), Timestamp.now(), "");
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    void sendMessageToUser(String message){
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserID());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserID(), Timestamp.now());

        // Aquí se corrige para agregar el chatMessageModel a la colección de mensajes
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    messageInput.setText("");
                    sendNotification(message);
                }
            }
        });
    }

    void sendNotification(String message) {
        FirebaseUtil.currentUsersDetails().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                UserModel currentUser=task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject=new JSONObject();

                    JSONObject notificationObj=new JSONObject();
                    notificationObj.put("title", currentUser.getUserName());
                    notificationObj.put("body", message);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);

                }catch (Exception e){

                }
            }
        });
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json");

        OkHttpClient client = new OkHttpClient();
        String url="https://fcm.googleapis.com/fcm/send";
        RequestBody body=RequestBody.create(jsonObject.toString(),JSON);
        Request request= new Request.Builder().url(url).post(body).header("Authorization", "Bearer AAAAwws0dJE:APA91bHLF45LFQrgxJXsAo_r1PhE-c_pMyXyPkrMZVVSz1nxn4QdpZIZ3qPZplv-yAesHrncv-HWWK0meFgGl5Czajbc14Ng41y6lEKKviJtKnJ-ExhPxEw6ZXJBvebcRkFJIGguTTw_").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}