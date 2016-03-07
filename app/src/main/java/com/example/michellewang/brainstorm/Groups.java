package com.example.michellewang.brainstorm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
1) SET TIMER TO START NEW ACTIVITY
2) FIX TIMER NOT SHOWING RIGHT NUMBER
*/
/**
 * // * Created by Victor on 3/5/2016.
 * //
 */
public class Groups extends AppCompatActivity {
    EditText group_text, group_name;
    ListView member_list, UserList;
    SearchView member_search;
    ArrayList<String> CurrentUsers = new ArrayList<>();
    ArrayList<Integer> Checked = new ArrayList<>();
    ArrayAdapter<String> DisplayUsers_toAdd;
    Map<String, Boolean> userMap = new HashMap<String, Boolean>();
    CheckBox[] cbArray = new CheckBox[20];
    int cbArray_size = 0;

    public final static String member_key = "com.example.michellewang.brainstorm.member_key";
    public final static String group_key = "com.example.michellewang.brainstorm.group_key";
    public final static String groupName_key = "com.example.michellewang.brainstorm.group_key";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://csm117-brainstorm.firebaseio.com/");
        group_name = (EditText) findViewById(R.id.group_text);
        member_list=(ListView) findViewById(R.id.UsersList);
        member_search=(SearchView) findViewById(R.id.member_search);


        UserList = (ListView) findViewById(R.id.UsersList);
        final Firebase UsernameList = ref.child("users");
        UsernameList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userMap = (HashMap<String, Boolean>)dataSnapshot.getValue();
                createUserCB();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });


        //go to new activity and create a new group object
        Button createGroup = (Button) findViewById(R.id.create_group);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("Checked items: " +UserList.getCheckedItemCount());
                String groupName = group_name.getText().toString();
                if (groupName.length() == 0) {
                    group_name.setError("invalid group name");
                }
                else {
                    Firebase groupRef = ref.child("groups").child(groupName);
                    Firebase UserRef;
                    Map<String, Boolean> selectedUserMap = new HashMap<>();
                    for (int i = 0; i < cbArray_size; i++)
                    {
                        if (cbArray[i].isChecked())
                        {
                            selectedUserMap.put(cbArray[i].getText().toString(), Boolean.TRUE);
                            UserRef = ref.child("users").child(cbArray[i].getText().toString());
                            UserRef.child(groupName).setValue("none"); //set group to true for that user
                        }
                    }
                    //PUSH USER WHO MADE GROUP INTO USERMAP
                    //UPDATE USER WHO MADE GROUP
                    groupRef.setValue(selectedUserMap);

                    Intent newGroup = new Intent(Groups.this, New_Session.class);
                    newGroup.putExtra(groupName_key, groupName);
                    startActivity(newGroup);
                }
            }
        });

    }

    public void createUserCB()
    {
        LinearLayout groupLayout = (LinearLayout)findViewById(R.id.groupLayout);
        groupLayout.setOrientation(LinearLayout.VERTICAL);
        ListView usersListView = (ListView)findViewById(R.id.UsersList);
        for (HashMap.Entry<String, Boolean> entry : userMap.entrySet())
        {
            cbArray[cbArray_size] = new CheckBox(this);
            CheckBox tempCB = cbArray[cbArray_size];
            tempCB.setText(entry.getKey());
            int cbID = View.generateViewId();
            tempCB.setId(cbID);
            groupLayout.addView(tempCB);
            cbArray_size++;

        }

    }

}
