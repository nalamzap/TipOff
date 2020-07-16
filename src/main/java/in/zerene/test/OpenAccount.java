package in.zerene.test;

import android.content.Context;
import android.content.Intent;

class OpenAccount {
    private Intent intent;
    OpenAccount(Context context,Class<?> cls,String uid,String username,String name,String bio,String hobbies,long g,boolean verified){
        intent = new Intent(context,cls);
        intent.putExtra("uid",uid);
        intent.putExtra("username",username);
        intent.putExtra("name",name);
        intent.putExtra("about",bio);
        intent.putExtra("hobbies",hobbies);
        intent.putExtra("gender",g);
        intent.putExtra("verified",verified);

    }

    Intent getIntent(){
        return intent;
    }
}
