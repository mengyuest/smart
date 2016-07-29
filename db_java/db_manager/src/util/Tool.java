
/**
 * Created by dynamit on 7/28/16.
 */

package util;

public class Tool {
    public static String unComment(String str){
        if(str!=null && (str.contains("//") || str.contains("#"))){
            int index = str.length();
            int index1 = str.indexOf('/');
            int index2 = str.indexOf('#');
            if(index1 >= 0 && index1 < index-1){
                index = index1+1;
            }
            if(index2 >= 0 && index2 < index-1){
                index = index2+1;
            }
            str = str.substring(0,index);
        }
        return str;
    }

    public static void println(String a){
        System.out.println("THU>>>"+a);
    }


    public void println(int n){
        System.out.println(n);
    }

    public void println(Boolean b){
        System.out.println(b);
    }

    public void print(String str){
        System.out.print(str);
    }

    public void print(int n){
        System.out.print(n);
    }

    public void print(Boolean b){
        System.out.print(b);
    }
}
