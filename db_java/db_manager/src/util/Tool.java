package util;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * <p>This class implements some useful tool function for the whole project</p>
 * <p>Be free to implement new method (or you call tips) under this class which may be used everywhere</p>
 * @author Meng Yue
 * @since 2016/07/28
 * */
public class Tool {
    /**
     *This function is to uncomment the string
     * @param str This is the raw string which may contain '#' or '//'
     * @return String This returns the string without comment part
     */
    public static String unComment(String str){
        if(str!=null && (str.contains("//") || str.contains("#"))){
            int count = str.length();
            int index = 0;
            int countOfQuoteMark = 0;
            while(index!=count){
                switch (str.charAt(index)){
                    case '\"':
                        countOfQuoteMark++;
                        break;
                    case '/':
                        if(index!=count-1&&str.charAt(index+1)=='/'){
                            if(countOfQuoteMark%2==0)
                            {
                                str = str.substring(0,index);
                                return str;
                            }
                        }
                        break;
                    case '#':
                        if(countOfQuoteMark%2==0)
                        {
                            str = str.substring(0,index);
                            return str;
                        }
                        break;
                }
                index++;
            }
        }
        return str;
    }

    /**
     * This function is to unquote the string
     * @param str The raw string formats like '...' or "..."
     * @return String The relished string without quotation mark
     */
    public static String unquote(String str){
        if(str==null){
            return null;
        }
        str = str.trim();
        if(str.charAt(0)=='\''||str.charAt(0)=='\"'){
            str=str.substring(1);
        }
        int count = str.length();
        if(str.charAt(count-1)=='\''||str.charAt(count-1)=='\"'){
            str = str.substring(0,count-1);
        }
        return str;
    }

    /**
     * This function is to parse a String to double array
     * @param arrayStr Format: XXX123.0,0.01,2.14,3.33YYY which X is not numeric or '-', and Y is not numeric
     * @return double array Parsed from the string given
     */
    public static double[] parseDoubleArray(String arrayStr){
        while(arrayStr!=null&&arrayStr.length()>1&&arrayStr.charAt(0)!='-'&&(arrayStr.charAt(0)<'0'||arrayStr.charAt(0)>'9')){
            arrayStr = arrayStr.substring(1);
        }
        while(arrayStr!=null&&arrayStr.length()>1&&(arrayStr.charAt(arrayStr.length()-1)<'0'||arrayStr.charAt(arrayStr.length()-1)>'9')){
            arrayStr = arrayStr.substring(0,arrayStr.length()-1);
        }
        if(arrayStr!=null && arrayStr.length()>0){
            String[] seg = arrayStr.split(",");
            int count = seg.length;
            double[] array = new double[count];
            for(int i=0;i<count;i++){
                array[i] = Double.parseDouble(seg[i].trim());
            }
            return array;
        }
        return null;
    }

    /**
     * This function is to parse a String to int array
     * @param arrayStr Format: XXX123,0,2,3YYY which X is not numeric or '-', and Y is not numeric
     * @return int array Parsed from the string given
     */
    public static int[] parseIntArray(String arrayStr){
        while(arrayStr!=null&&arrayStr.length()>1&&arrayStr.charAt(0)!='-'&&(arrayStr.charAt(0)<'0'||arrayStr.charAt(0)>'9')){
            arrayStr = arrayStr.substring(1);
        }
        while(arrayStr!=null&&arrayStr.length()>1&&(arrayStr.charAt(arrayStr.length()-1)<'0'||arrayStr.charAt(arrayStr.length()-1)>'9')){
            arrayStr = arrayStr.substring(0,arrayStr.length()-1);
        }
        if(arrayStr!=null && arrayStr.length()>0){
            String[] seg = arrayStr.split(",");
            int count = seg.length;
            int[] array = new int[count];
            for(int i=0;i<count;i++){
                array[i] = Integer.parseInt(seg[i].trim());
            }
            return array;
        }
        return null;
    }

    public static boolean testIntArraySame(String arrayStr, int[] array){
        int[] testArray = parseIntArray(arrayStr);
        if(testArray==null || array == null){
            return false;
        }
        if(testArray.length!=array.length){
            return false;
        }
        for(int i=0;i<testArray.length;i++){
            if(testArray[i]!=array[i]){
                return false;
            }
        }

        return true;
    }

    public static boolean testIntArraySame(String arrayStr, LinkedList<Integer> linked){
        int[] array = new int[linked.size()];
        int count = linked.size();
        for (int i=0;i<count;i++){
            array[i] = linked.get(i);
        }
        int[] testArray = parseIntArray(arrayStr);
        if(testArray==null || array == null){
            return false;
        }
        if(testArray.length!=array.length){
            return false;
        }
        for(int i=0;i<testArray.length;i++){
            if(testArray[i]!=array[i]){
                return false;
            }
        }

        return true;
    }

    public static boolean testDoubleArraySame(String arrayStr, double[] array){
        double[] testArray = parseDoubleArray(arrayStr);
        if(testArray==null || array == null){
            return false;
        }
        if(testArray.length!=array.length){
            return false;
        }
        for(int i=0;i<testArray.length;i++){
            if(testArray[i]!=array[i]){
                return false;
            }
        }

        return true;
    }

    public static boolean testDoubleArraySame(String arrayStr, LinkedList<Double> linked){
        double[] array = new double[linked.size()];
        int count = linked.size();
        for (int i=0;i<count;i++){
            array[i] = linked.get(i);
        }
        double[] testArray = parseDoubleArray(arrayStr);
        if(testArray==null || array == null){
            return false;
        }
        if(testArray.length!=array.length){
            return false;
        }
        for(int i=0;i<testArray.length;i++){
            if(testArray[i]!=array[i]){
                return false;
            }
        }

        return true;
    }

    public static boolean testStringArraySame(String str, String[] strb){
        if(str==null){
            return false;
        }
        String[] stra = str.split(",");
        if(stra==null||strb==null){
            return false;
        }
        if(stra.length!=strb.length){
            return false;
        }
        int count = stra.length;
        for(int i=0;i<count;i++){
            if(!testStringSame(stra[i].trim(),strb[i].trim())){
                return false;
            }
        }
        return true;
    }

    public static boolean testStringSame(String stra, String strb){
        if(stra==null||strb==null){
            return false;
        }
        if(stra.compareTo(strb)!=0){
            return false;
        }
        return true;
    }

    public static void IfNotExistThenCreateDir(String path){

        if(path.contains(".")){
            path = path.substring(0,path.lastIndexOf("/")+1);
        }
        File thisDir = new File(path);
        if(!thisDir.exists()) {
            thisDir.mkdir();
        }
    }

    public static int[] getSizeOfMatrix(double[][] matrix){
        int [] size = new int[2];
        int count = matrix.length;
        double []projection = matrix[0];
        size[1] = projection.length;
        size[0] = count;
        return size;
    }


    public static void println(String a){
        System.out.println("THU>>>"+a);
    }

    public static void println(int n){
        System.out.println(n);
    }

    public static void println(double n){System.out.println(String.format("%f",n));}

    public static void println(Boolean b){
        System.out.println(b);
    }

    public static void print(String str){
        System.out.print(str);
    }

    public static void print(int n){
        System.out.print(n);
    }

    public static void print(Boolean b){
        System.out.print(b);
    }
}
