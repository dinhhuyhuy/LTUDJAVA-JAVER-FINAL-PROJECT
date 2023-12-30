package utils;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Utils {
    public static void clearTable(JTable table){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        while (model.getRowCount() > 0) {                
            model.removeRow(0);
        }
    }
    public static boolean isLeapYear(int year){
        boolean result;
        if(year % 4 == 0){
            if( year % 100 == 0){
                result = year % 400 == 0;
            }
            else result = true;
        } else result = false;
        return result;
    }
    public static boolean isInt(String num){
        try{int n = Integer.parseInt(num);}
        catch (NumberFormatException e){return false;}
        return true;
    }
    public static boolean checkDate(String date) {
        String[] token = date.split("-");
        if(token.length > 3) {
            return false;
        }
        if (isInt(token[0]) && isInt(token[1]) && isInt(token[2])) {
            int year = Integer.parseInt(token[0]);
            int month = Integer.parseInt(token[1]);
            int day = Integer.parseInt(token[2]);
            if (year < 1900) return false;
            if (month < 1 || month > 12) return false;
            if(day < 1) return false;
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 ||
                    month == 10 || month == 12 && day > 31) return false;
            if (month == 4 || month == 6 || month == 9 || month == 11 &&
                    day > 30) return false;
            if (month == 2 && isLeapYear(year) && day > 29) return false;
            if (month == 2 && !isLeapYear(year) && day > 28) return false;
        }else {
            return false;
        }
        return true;
    }
}
