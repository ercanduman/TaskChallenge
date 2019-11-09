package ercanduman.taskdemo.util;

public interface ProcessListener {
    void onStarted();

    void onFinished(String data);
}
