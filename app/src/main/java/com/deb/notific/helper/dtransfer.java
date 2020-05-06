package com.deb.notific.helper;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

public class dtransfer {
    private List<String> namelist;
    private List<String> pnumber;
    private List<String> mTimeList;

    public dtransfer(List<String> namelist, List<String> pnumber, List<String> timeList) {
        this.namelist = namelist;
        this.pnumber = pnumber;
        mTimeList = timeList;
    }

        public List<String> getNamelist() {
            return namelist;
        }

        public List<String> getPnumber() {
            return pnumber;
        }

        public List<String> getTimeList() {
            return mTimeList;
    }
}

