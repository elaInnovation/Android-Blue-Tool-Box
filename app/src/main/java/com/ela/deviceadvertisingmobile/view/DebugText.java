package com.ela.deviceadvertisingmobile.view;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class DebugText
{
    private static DebugText instance = null;
    private Context context;
    private TextView textView;

    public static DebugText getInstance()
    {
        if(instance == null)
            instance = new DebugText();
        return instance;
    }

    public void setInit(Context context, TextView textView)
    {
        this.context = context;
        this.textView = textView;
        this.textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void write(String data)
    {
        if(textView != null) {
            this.textView.append(data);
            this.textView.append("\n");
        }
    }

    public void clear()
    {
        if(textView != null) {
            this.textView.setText("");
        }
    }
}
