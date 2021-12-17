package com.example.one.speail;

import java.util.Observable;

/**
 * @author :JianTao
 * @date :2021/12/15 16:53
 * @description : 被观察者老师类
 */
public class Teacher extends Observable {
    private Teacher(){
    }

    private volatile static Teacher teacher;

    public static Teacher getInstance(){
        if (teacher ==null){
            synchronized (Teacher.class){
                if (teacher==null){
                    teacher = new Teacher();
                }
            }
        }
        return teacher;
    }

    public void postMessage(String eventType){
        setChanged();
        notifyObservers(eventType);
    }
}
