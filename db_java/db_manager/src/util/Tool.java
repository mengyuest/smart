
/**
 * Created by dynamit on 7/28/16.
 */

package util;

import sun.awt.image.ImageWatched;

import javax.sound.sampled.Line;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

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

    public static String intArrayToString(int[] a){
        if(a == null){
            return null;
        }
        int count = a.length;
        if(count==0){
            return "";
        }
        String str = "";
        for(int i=0;i<count;i++){
            str = a[i]+",";
        }
        return str.substring(0,str.length()-1);
    }

    public static String doubleArrayToString(double[] a){
        if(a == null){
            return null;
        }
        int count = a.length;
        if(count==0){
            return "";
        }
        String str = "";
        for(int i=0;i<count;i++){
            str = a[i]+",";
        }
        return str.substring(0,str.length()-1);
    }



    public static boolean testMap(HashMap a, HashMap b){
        if(a==null||b==null){
            return false;
        }
        if(a.size()!=b.size()){
            return false;
        }
        if(a.keySet()!=b.keySet()){
            return false;
        }

        for(Object key:a.keySet()){
            if(a.get(key)!=b.get(key)){
                return false;
            }
        }
        return true;
    }

    public static boolean testIntArrayMap(HashMap a, HashMap b){
        if(a==null||b==null){
            return false;
        }
        if(a.size()!=b.size()){
            return false;
        }
        if(a.keySet().iterator().next().getClass()!=b.keySet().iterator().next().getClass()){
            return false;
        }
        for(Object key:a.keySet()){
            Object o1 = a.get(key);
            Object o2 = b.get(key);
            int[] oo1 = (int[]) o1;
            int[] oo2 = (int[]) o2;
            if(!testStringSame(intArrayToString(oo1) ,intArrayToString(oo2))){
                return false;
            }
        }

        return true;
    }

    public static boolean testDoubleArrayMap(HashMap a, HashMap b){
        if(a==null||b==null){
            return false;
        }
        if(a.size()!=b.size()){
            return false;
        }
        if(a.keySet().iterator().next().getClass()!=b.keySet().iterator().next().getClass()){
            return false;
        }
        for(Object key:a.keySet()){
            Object o1 = a.get(key);
            Object o2 = b.get(key);
            double[] oo1 = (double[]) o1;
            double[] oo2 = (double[]) o2;
            if(!testStringSame(doubleArrayToString(oo1) ,doubleArrayToString(oo2))){
                return false;
            }
        }

        return true;
    }

    public static boolean testDoubleLinkedListArrayMap(HashMap a, HashMap b){
        if(a==null||b==null){
            return false;
        }
        if(a.size()!=b.size()){
            return false;
        }
        if(a.keySet().iterator().next().getClass()!=b.keySet().iterator().next().getClass()){
            return false;
        }
        for(Object key:a.keySet()){
            Object o1 = a.get(key);
            Object o2 = b.get(key);
            LinkedList<double[]> oo1 = (LinkedList<double[]>) o1;
            LinkedList<double[]> oo2 = (LinkedList<double[]>) o2;
            if(oo1.size()!=oo2.size()) {
                return false;
            }
            int count = oo1.size();
            for(int i=0;i<count;i++){
                if(!testStringSame(doubleArrayToString(oo1.get(i)),doubleArrayToString(oo2.get(i)))){
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean testIntLinkedListArrayMap(HashMap a, HashMap b){
        if(a==null||b==null){
            return false;
        }
        if(a.size()!=b.size()){
            return false;
        }
        if(a.keySet().iterator().next().getClass()!=b.keySet().iterator().next().getClass()){
            return false;
        }
        for(Object key:a.keySet()){
            Object o1 = a.get(key);
            Object o2 = b.get(key);
            LinkedList<int[]> oo1 = (LinkedList<int[]>) o1;
            LinkedList<int[]> oo2 = (LinkedList<int[]>) o2;
            if(oo1.size()!=oo2.size()) {
                return false;
            }
            int count = oo1.size();
            for(int i=0;i<count;i++){
                if(!testStringSame(intArrayToString(oo1.get(i)),intArrayToString(oo2.get(i)))){
                    return false;
                }
            }
        }

        return true;
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
