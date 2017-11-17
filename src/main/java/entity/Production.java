package entity;

import syntax_parser.SyntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 53068 on 2017/11/9 0009.
 */
public class Production {


    private static int count = 0;
    /**
     * 产生式的标号
     * 为0则表示第0号产生式：S'->S
     */
    private int id;

    private String name;
    /**
     * 符号
     * 如果小于0则表示是非终结符，绝对值等于产生式标号
     * 如果大于0则表示是终结符，绝对值等于终结符号表的位置
     * 如果等于0则表示是$R,产生式的结束
     */
    private List<Integer> signs;

    /**
     * 产生式实体类
     * @param name 产生式的名字
     */
    public Production(String name) {
        this.id = count;
        this.name = name;
        signs = new ArrayList<>();
        count ++;
    }

    public void addSign(int sign){
        signs.add(sign);
    }

    public void print(boolean hasNum){
        if(hasNum)
            System.out.print("("+id+")");
        System.out.print(name + "->");
        signs.forEach(i -> {
            if(i < 0){
                System.out.print(SyntaxAnalyzer.nonTerminator.get(-i)+" ");
            }else if(i > 0){
                System.out.print("\""+ SyntaxAnalyzer.terminator.get(i)+"\" ");
            }
        });
        System.out.println();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getSigns() {
        return signs;
    }
}
