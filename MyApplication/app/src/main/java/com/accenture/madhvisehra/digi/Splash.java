package com.accenture.madhvisehra.digi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



/**
 * Created by dell on 12-08-2016.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer=new Thread(){
            public void run(){

                try{

                    sleep(8000); // 8 sec

                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }finally {

                    Intent openStartingPoint=new Intent("com.accenture.madhvisehra.digi.CAMERA");
                    startActivity(openStartingPoint);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish(); // destroy urself
    }
}
