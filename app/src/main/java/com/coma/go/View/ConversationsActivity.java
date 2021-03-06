package com.coma.go.View;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.coma.go.Custom.ConversationAdapter;
import com.coma.go.Custom.EventAdapter;
import com.coma.go.Model.Conversation;
import com.coma.go.Model.Event;
import com.coma.go.R;
import com.coma.go.Service.FBIO;
import com.coma.go.Service.Singleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.coma.go.Misc.Constants.FB_DIRECTORY_CONVERSATIONS;
import static com.coma.go.Misc.Constants.FB_DIRECTORY_USERS;

public class ConversationsActivity extends AppCompatActivity {


    ArrayList<String> cidList = null;

    ConversationAdapter conversationAdapter;

    @Bind(R.id.listView_conversations)
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        final Singleton singleton = Singleton.getInstance();
        final String uid = singleton.getUser().userInfo.getUid();

        ButterKnife.bind(this);


        Task task = FBIO.getMyCids(uid).getTask();
        task.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                cidList = (ArrayList<String>) task.getResult();
                getMyConversations(uid);

            }
        });

        conversationAdapter = new ConversationAdapter(this, listConversations);


        listView.setAdapter(conversationAdapter);

    }

    ArrayList<Conversation> listConversations = new ArrayList<>();
    public ArrayList<Conversation> getMyConversations(String uid){//можно создать таблицу title of COnversation для view(меньше трафика)

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FB_DIRECTORY_CONVERSATIONS);

        for(String cid : cidList){
            Log.w("heh", "Loaded conversation");
            ref.child(cid).addListenerForSingleValueEvent(//глобальный и постоянный прослушиватель всех данных marks
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                Conversation conversation = dataSnapshot.getValue(Conversation.class);

                                listConversations.add(conversation);

                                conversationAdapter.notifyDataSetChanged();




                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });


        }


        return listConversations;
    }



    public ArrayList<Conversation> getConversationList(String uid){
        final ArrayList<Conversation> listConversations = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FB_DIRECTORY_USERS);

        ref.addListenerForSingleValueEvent(//глобальный и постоянный прослушиватель всех данных marks
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            //Conversation conversation = dataSnapshot.getValue(Conversation.class);
                            Conversation conversation = snapshot.getValue(Conversation.class);

                            listConversations.add(conversation);

                            conversationAdapter.notifyDataSetChanged();
                        }



                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

        return listConversations;
    }

}
