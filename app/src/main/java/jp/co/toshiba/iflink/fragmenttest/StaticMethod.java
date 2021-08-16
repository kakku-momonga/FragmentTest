package jp.co.toshiba.iflink.fragmenttest;

public class StaticMethod {
    /*
     *
     * 指定桁数以上の文字を…に置き換える
     *
     */
    static String limittedString( String str, int max){
        String t1 = str;
        String t2 = "";
        int size =0;
        for(int i =0; i< t1.length();i++){
            String s = t1.substring(i, i+1);
            if(judge(s)==1){
                if((size+1)>max){
                    t2 = t2 + "…";
                    break;
                }
                t2=t2+s;
                size++;
            }else{
                if((size+2)>max){
                    t2=t2+"…";
                    break;
                }
                t2=t2+s;
                size+=2;
            }
        }
        return t2;
    }
    static     private int judge(String b){
        byte[] bytes = b.getBytes();
        return bytes.length;
    }

}
