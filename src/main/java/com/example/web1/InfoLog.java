package com.example.web1;

import java.util.Date;

public class InfoLog {
    public Date date;
    public Long period;
    public String URL;
    public InfoLog(Date date, Long period, String url) {
        this.date = date;
        this.period = period;
        this.URL = url;
    }

}