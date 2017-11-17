package syntax_parser;

import entity.PPT;
import entity.Production;
import exception.GrammarException;
import exception.ProductionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by 53068 on 2017/11/8 0008.
 */
public class SyntaxAnalyzer {

    private static SyntaxAnalyzer analyzer;

    public static List<String> terminator = new ArrayList<>();      //0号位代表$符号，没有值
    public static List<String> nonTerminator = new ArrayList<>();   //0号位代表0号式S',没有值
    public static List<Production> productionList = new ArrayList<>();
    private Map<Integer, List<Production>> productions = new HashMap<>();
    private PPT ppt;

    private SyntaxAnalyzer(){
        terminator.add("");
        nonTerminator.add("");
        Production firstProduction = new Production("");
        firstProduction.addSign(-1);
        firstProduction.addSign(0);
        productionList.add(firstProduction);
        productions.put(0, new ArrayList<>());
        productions.get(0).add(firstProduction);
    }

    public static SyntaxAnalyzer getAnalyzer(){
        if(analyzer == null){
            analyzer = new SyntaxAnalyzer();
        }
        return analyzer;
    }

    /**
     * 输入的格式：
     * 在首行先声明产生式的名字(层次最高的产生式名字放在第一，用大括号包围，逗号分隔名字)：
     * {[Name], [Name], ...}
     * [Name]: ["[终结符]"|[非终结符]]+
     *    |    ["[终结符]"|[非终结符]]+
     *    |    ...
     *   ...   ...
     * e.g. S: "if" S "else" S
     *      |  "if" S
     */
    public void getInput(){
        System.out.print("Please enter the rule file: ");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File input = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String line;

            //获取产生式的名字
            StringBuilder names = new StringBuilder();
            boolean begin = false;
            while ((line = reader.readLine()) != null){
                if(line.trim().length() > 0) {
                    if (line.trim().contains("{") && !begin) {
                        begin = true;
                    } else if (line.trim().contains("{") && begin) {
                        throw new ProductionException();
                    }
                    if(begin){
                        names.append(line);
                    }
                    if (line.trim().contains("}") && begin) {
                        break;
                    }else if(line.trim().contains("}") && !begin){
                        throw new ProductionException();
                    }
                }
            }
            for (String s : names.substring(names.indexOf("{") + 1, names.indexOf("}")).split(",")) {
                if(s.trim().length() > 0){
                    nonTerminator.add(s.trim());
                }
            }

            //解析产生式
            String name = "";
            while ((line = reader.readLine()) != null){
                if(line.trim().length() > 0){
                    char[] c = line.trim().toCharArray();
                    StringBuilder each = new StringBuilder();
                    int i = 0;
                    if(c[0] == '|'){
                        if(name.length() > 0){
                            i = 1;
                        }else {
                            throw new ProductionException();
                        }
                    }else {
                        for(; i < c.length; i ++){
                            if(c[i] == ':') break;
                            if(c[i] != ' ') {
                                each.append(c[i]);
                            }
                        }
                        i++;
                        name = each.toString();
                        if(!nonTerminator.contains(name)){
                            nonTerminator.add(name);
                        }
                    }
                    Production production = new Production(name);
                    if(!productions.containsKey(nonTerminator.indexOf(name))){
                        productions.put(nonTerminator.indexOf(name), new ArrayList<>());
                    }
                    productions.get(nonTerminator.indexOf(name)).add(production);

                    for(; i < c.length; i ++){
                        if(c[i] == ' ') continue;
                        if(c[i] == '"'){
                            i ++;
                            each = new StringBuilder();
                            while (c[i] != '"'){
                                if(i == c.length) throw new ProductionException();
                                each.append(c[i]);
                                i++;
                            }
                            if(!terminator.contains(each.toString())){
                                terminator.add(each.toString());
                            }
                            production.addSign(terminator.indexOf(each.toString()));
                        }else {
                            each = new StringBuilder();
                            while (i < c.length && c[i] != ' ' && c[i] != '"'){
                                each.append(c[i]);
                                i ++;
                            }
                            if(!nonTerminator.contains(each.toString())){
                                throw new ProductionException(each.toString());
                            }
                            production.addSign(- nonTerminator.indexOf(each.toString()));
                            i --;
                        }
                    }
                    production.addSign(0);
                    productionList.add(production);
                    production.print(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void compile() throws GrammarException{
        ppt = new REToLRDFA(productions).createPPT();
    }

    /**
     * 输入要分析的内容
     * 以空格为间隔符分隔每个符号
     * e.g. if abc else ab c
     */
    public void analyze(){
        System.out.println("Please enter what you want to analyze (enter 'exit' to exit): ");
        Scanner scanner = new Scanner(System.in);
        String line;
        Stack<Integer> state = new Stack<>();
        while (!(line = scanner.nextLine()).equals("exit")){
            List<String> inputs = new ArrayList<>();
            for (String s : line.trim().split(" +")) {
                inputs.add(s);
            }
            inputs.add("");

            //移入初值
            state.add(1);
            try {
                for (int i = 0; i < inputs.size(); i++) {
                    int index = terminator.indexOf(inputs.get(i));
                    int thisState;
                    if (index >= 0) {
                        thisState = ppt.action(state.peek(), index);
                        if (thisState > 0) {
                            state.push(thisState);
                        } else if (thisState < 0) {
                            if (thisState == Integer.MIN_VALUE) {
                                System.out.println("Finish!");
                                state.removeAllElements();
                                break;
                            } else {
                                thisState = -thisState;
                                for (int j = 0; j < productionList.get(thisState).getSigns().size() - 1; j++) {
                                    state.pop();
                                }
                                int nonTerminatorNum = nonTerminator.indexOf(productionList.get(thisState).getName());
                                int actualState = ppt.goTo(state.peek(), nonTerminatorNum);
                                if (actualState != 0) {
                                    state.push(actualState);
                                    productionList.get(thisState).print(false);
                                } else {
                                    System.out.println("Can not interpret this string!");
                                    break;
                                }
                                i--;
                            }
                        }
                    } else {
                        System.out.println("There is no terminator {" + inputs.get(i) + "}");
                        break;
                    }
                }

                if (state.size() != 0) {
                    System.out.println("Can not interpret this string!");
                }
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println("Error string");
            }
        }
    }

}
