package coursework.manager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Pattern compile = Pattern.compile("по горизонтали ([а-я]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE  | Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = compile.matcher("по горизонтали правый по вертикали нижний");
        System.out.println(m.find());
        System.out.println(m.group(1));
    }

}