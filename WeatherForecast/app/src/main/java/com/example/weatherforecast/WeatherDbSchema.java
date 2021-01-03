package com.example.weatherforecast;

public class WeatherDbSchema {
    public static final class WeatherTable {
        public static final String NAME = "weathers";

        public static final class Cols {
            private static final String DATE = "date";
            private static final String TEMPMAX = "tempmax";
            private static final String TEMPMIN = "tempmin";
            private static final String ICONDAY = "iconday";
            private static final String TEXTDAT = "textday";
            private static final String HUMIDITY = "humidity";
            private static final String PRESSURE = "pressure";
            private static final String WINDSPEEDDAY = "windspeedday";
        }
    }
}

