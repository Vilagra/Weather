package com.example.weather;

import android.app.Application;

import com.example.weather.entity.CurrentDisplayedWeather;
import com.example.weather.entity.WeatherByDay;
import com.example.weather.entity.WeatherByHours;

import java.util.List;

/**
 * Created by Vilagra on 16.11.2016.
 */
/* !!!Много вопросов по этому классу, активность вроде одна, но я не знаю как сохранять правильно состояние при том же повороте экрана, вначале я сохранял состояние всех вьюшек, потом их
стало слишком много, я решил создавть объект погода и ханить все в нем, допустим можно было не создавать, а таки сохранять состояние всех вьюшек, но потом появились еще ресиклер вью
и мне ж надо как-то запоминать и аррайлист для них, в общем я сохраняю тут, я понимаю что это может не правильно, есть вариант класса со стаческим полями, есть вариант хранить просто название
город и делать повторный запрос по погоде(наверно самый не верный путь), есть вариант превращать объект в жсон, ео потом в строку и сохранять список этих строк в sharedPreference и с него
востанавливать, наверно можно через парсел, но я особо в нем не разбирался, в общем какой путь лучше? может ты предложишь свое решение. А то выходит стат. поля плохо, данное решение тоже
плохо, так я слушал лекцию и там сказали что это костыль не реккомендуемый к использыванию, в общем надеюсь ты меня направишь в правильную сторону.*/
/*public class MyApplication extends Application {
    private List<WeatherByDay> weatherByDays;
    private List<WeatherByHours> weatherByHoursList;
    private CurrentDisplayedWeather currentDisplay;
    String nameOfCity;
    String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNameOfCity() {
        return nameOfCity;
    }

    public void setNameOfCity(String nameOfCity) {
        this.nameOfCity = nameOfCity;
    }

    public CurrentDisplayedWeather getCurrentDisplay() {
        return currentDisplay;
    }

    public void setCurrentDisplay(CurrentDisplayedWeather currentDisplay) {
        this.currentDisplay = currentDisplay;
    }

    public List<WeatherByDay> getWeatherByDays() {
        return weatherByDays;
    }

    public void setWeatherByDays(List<WeatherByDay> currentWeatherList) {
        this.weatherByDays = currentWeatherList;
    }

    public List<WeatherByHours> getWeatherByHoursList() {
        return weatherByHoursList;
    }

    public void setWeatherByHoursList(List<WeatherByHours> weatherByHoursList) {
        this.weatherByHoursList = weatherByHoursList;
    }
}*/
