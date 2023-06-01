package rs.etf.sab.operations;

import java.util.Calendar;

public interface GeneralOperations {
    void setInitialTime(Calendar time);

    Calendar time(int days);

    Calendar getCurrentTime();

    void eraseAll();
}
