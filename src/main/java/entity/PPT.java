package entity;

import syntax_parser.SyntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 53068 on 2017/11/9 0009.
 */
public class PPT {

    /**
     * 动作表
     * 表中值为正，则表示移进，绝对值为下一状态号
     * 表中值为负，则表示归约，绝对值为产生式的标号
     * 表中值为int最小值，则表示接受(accept)
     */
    private List<int[]> action = new ArrayList<>();
    private List<int[]> goTo = new ArrayList<>();   //移进表，表中值表示下一状态的值

    /**
     * 预测分析表(predictable parsing table)
     * 第0行没有内容
     */
    public PPT() {
        action.add(new int[1]);
        goTo.add(new int[1]);
    }

    public void addState(int[] action, int[] goTo){
        if(action != null)
            this.action.add(action);
        else
            this.action.add(new int[SyntaxAnalyzer.terminator.size()]);
        if(goTo != null)
            this.goTo.add(goTo);
        else
            this.goTo.add(new int[SyntaxAnalyzer.nonTerminator.size()]);
    }

    public int action(int nowState, int terminator){
        return action.get(nowState)[terminator];
    }

    public int goTo(int nowState, int nonTerminator) {
        return goTo.get(nowState)[nonTerminator];
    }

    public void print(){
        String tab = "\t";
        System.out.print("   |");
        for (String s : SyntaxAnalyzer.terminator) {
            if(s.equals("")){
                System.out.print("$R"+tab);
            }else {
                System.out.print(s+tab);
            }
        }
        System.out.print("|");
        for (int i = 1; i < SyntaxAnalyzer.nonTerminator.size(); i++) {
            System.out.print(SyntaxAnalyzer.nonTerminator.get(i)+tab);
        }
        System.out.println();
        for (int i = 1; i < action.size(); i++) {
            if(i < 10){
                System.out.print(i + "  |");
            }else {
                System.out.print(i + " |");
            }
            for (int j = 0; j < action.get(i).length; j++) {
                if(action.get(i)[j] == Integer.MIN_VALUE){
                    System.out.print("acc");
                } else if (action.get(i)[j] < 0) {
                    System.out.print("r"+(-action.get(i)[j]));
                } else if(action.get(i)[j] > 0){
                    System.out.print("s"+action.get(i)[j]);
                }
                System.out.print(tab);
            }
            System.out.print("|");
            for (int j = 1; j < goTo.get(i).length; j++) {
                if(goTo.get(i)[j] != 0){
                    System.out.print(goTo.get(i)[j]);
                }
                System.out.print(tab);
            }
            System.out.println();
        }
    }
}

