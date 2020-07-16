package in.zerene.test;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class ContactsChange {
    final static int SEND_REQUEST = 2331,SENT = 23311,RESPOND=2332,ACCEPTED=23321,REJECTED=23322,CONTACT_OPTIONS = 2333,
            REMOVED=23331,UPGRADED=23332,CLOSE_OPTIONS=2334,DOWNGRADED=23341,BLOCK=2339,BLOCKED=23391,CANCELLED = 2300,ERROR=2020;
    private DatabaseReference database;
    private boolean flag;

    String t,m;
    ContactsChange(){
        database = FirebaseDatabase.getInstance().getReference();
    }

    boolean sendRequest(final Context context, final String me, final String target, final String extra){
        flag=true;
        database.child(target+"/received/"+me)
                .setValue(extra+"!@#$").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.child(me+"/sent/"+target)
                        .setValue(extra+"!@#$").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag = false;
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }

    boolean deleteRequest(final Context context, final String me,int sent, final String target){

        if(sent==1){t="received";m="sent";}
        else if(sent==2){t="contacts";m="contacts";}
        else if(sent==3){t="close";m="close";}
        else{m="received";t="sent";}
        flag = true;
        database.child(target+"/"+t+"/"+me)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.child(me+"/"+m+"/"+target)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Request cancelled!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag = false;
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }

    boolean confirmRequest(final Context context, final String me, final String target){
        flag = true;
        database.child(target+"/contacts/"+me)
                .setValue(me).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.child(me+"/contacts/"+target)
                        .setValue(target).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        deleteRequest(context,me,0,target);

                        Toast.makeText(context, "Request confirmed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag = false;
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }

    boolean upgradeContact(final Context context, final String me, final String target){
        flag = true;
        database.child(target+"/close/"+me)
                .setValue(me).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.child(me+"/close/"+target)
                        .setValue(target).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        deleteRequest(context,me,2,target);

                        Toast.makeText(context, "Contact upgraded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag = false;
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }

    boolean downgradeContact(final Context context, final String me, final String target){
        flag = true;
        database.child(target+"/contacts/"+me)
                .setValue(me).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                database.child(me+"/contacts/"+target)
                        .setValue(target).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        deleteRequest(context,me,3,target);

                        Toast.makeText(context, "Contact downgraded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag = false;
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        return flag;
    }

    boolean block(final Context context, final String me, final String target){
        flag = true;
        database.child(me+"/blocked/"+target).setValue(true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deleteRequest(context,me,0,target);
                        deleteRequest(context,me,1,target);
                        deleteRequest(context,me,2,target);
                        deleteRequest(context,me,3,target);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag = false;
                    }
                });
        return flag;
    }
    boolean unblock(final Context context, final String me, final String target){
        flag = true;
        database.child(me+"/blocked/"+target).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Contact unblocked!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                flag = false;
            }
        });
        return flag;
    }

}
