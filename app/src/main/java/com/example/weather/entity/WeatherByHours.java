package com.example.weather.entity;

import android.app.Activity;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vilagra on 16.11.2016.
 */

//!!! У меня 2 разных ресиклвью - в одном по дням, во втором по часам, я под каждый создал класс,я выделил у них общие данные и создал класс weather от которого их наследую,
//  так вышло, что этот класс, который по часам не имеет дополнительных данных, все что ему нужно есть в потомке, создал его что б просто имя понятное было, не знаю насколько правильно это.
//  И второй вопрос в жсоне что по дням, что по часам выдает в принципе одинаковый список параметров, но я вывожу разные параметры, одни когда по дням, и немного другие когда по часам
 //   и отсюда вопрос стоит ли делать подобную иерархию типа моей, или может создать один класс который будет считывать все параметры(в случае с почасовым лишние),просто лишние, выходит, не будут использываться
public class WeatherByHours extends Weather{
    public WeatherByHours(Activity context, Date date, int idDrawable, double temperature, double wind) {
        super(context,date, idDrawable, temperature, wind);
    }


}
