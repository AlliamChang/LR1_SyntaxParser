package syntax_parser;

import entity.*;
import exception.GrammarException;

import java.util.*;

/**
 * Created by 53068 on 2017/11/9 0009.
 */
public class REToLRDFA {

    private Map<Integer, List<Production>> productions;
    private DFA<ItemSet> itemSets;
    private PPT ppt;

    private int[] thisAction;
    private int[] thisGoto;

    /**
     * 构造步骤
     * 1. 使用closure构造I0
     * 2. 使用goto和closure推出下一个Ii
     */
    public REToLRDFA(Map<Integer, List<Production>> productions){
        this.productions = productions;
        this.itemSets = new DFA<>();
        this.ppt = new PPT();
    }

    public PPT createPPT() throws GrammarException{
        ItemSet firstSet = new ItemSet();
        Set<Integer> firstPre = new HashSet<>();
        firstPre.add(0);
        Item firstItem = new Item(firstPre, 0, SyntaxAnalyzer.productionList.get(0));
        firstSet.add(firstItem);
        itemSets.add(closure(firstSet));

        for (int i = 0; i < itemSets.size(); i++) {
            thisAction = new int[SyntaxAnalyzer.terminator.size()];
            thisGoto = new int[SyntaxAnalyzer.nonTerminator.size()];
            //填写要归约的状态
            addReduction(itemSets.get(i));
            goTo(itemSets.get(i));
            ppt.addState(thisAction, thisGoto);
        }

        ppt.print();

        return ppt;
    }

    /**
     * 将项与[A->α.Bβ, a]进行匹配
     * 对于每个产生式B->γ和First(βa)中的终结符号b
     * 将项[B->.γ, b]加入到闭包中
     * e.g: [S'->.S, $], α= ε, B = S, β = ε, a = $
     *      B->γ即S的所有产生式
     *      First(βa) = First(ε$)
     */
    private ItemSet closure(ItemSet items){
        List<Item> closure = items.getItems();
        for (int i = 0; i < closure.size(); i++) {
            List<Integer> production = closure.get(i).getProduction().getSigns();
            if(production.get(closure.get(i).getPosition()) < 0){
                //计算预测符集b
                Set<Integer> predictor = first(production.subList(closure.get(i).getPosition()+1, production.size()));
                if(predictor.size() == 0) {
                    predictor.addAll(closure.get(i).getPredictors());
                }
                //计算所有B->γ
                List<Production> gamma = productions.get(- production.get(closure.get(i).getPosition()));
                //将每个项[B->.γ, b]加入到闭包中
                for (int j = 0; j < gamma.size(); j++) {
                    items.add( new Item(predictor, 0, gamma.get(j)) );
                }
            }
        }

//        System.out.println("I"+items.getId());
//        items.print();
        return items;
    }

    /**
     * 计算target的所有GOTO
     * @param target
     * @return
     */
    private void goTo(ItemSet target) throws GrammarException{
        DFA<ItemSet> gotoSet = new DFA<>();

        //所有可能的下一步
        List<Integer> possibleX = new ArrayList<>();
        target.getItems().forEach(item -> {
            int next = item.getProduction().getSigns().get(item.getPosition());
            if(!possibleX.contains(next) && next != 0){
                possibleX.add(next);
//                System.out.println(next);
            }
        });

        //推导所有下一步的闭包项集
        for (int i = 0; i < possibleX.size(); i++) {
            ItemSet newItemSet = new ItemSet();
            for (Item item : target.getItems()) {
                if (item.getProduction().getSigns().get(item.getPosition()) == possibleX.get(i)) {
                    Item newItem = new Item(item.getPredictors(), item.getPosition()+1, item.getProduction());
                    newItemSet.add(newItem);
                }
            }
            int index = itemSets.addFromIndex(closure(newItemSet)) + 1;
            if(possibleX.get(i) < 0){
                thisGoto[ - possibleX.get(i)] = index ;
            }else {
                if (thisAction[possibleX.get(i)] == 0) {
                    thisAction[possibleX.get(i)] = index;
                }else {
                    throw new GrammarException();
                }
            }
        }

    }

    private void addReduction(ItemSet items) throws GrammarException{
        for (Item item : items.getItems()) {
            if (item.getProduction().getSigns().get(item.getPosition()) == 0) {
                for (Integer i : item.getPredictors()) {
                    if (thisAction[i] != 0) {
                        throw new GrammarException();
                    } else {
                        if (item.getProductionNum() == 0 && i == 0) {
                            thisAction[i] = Integer.MIN_VALUE;
                        }else {
                            thisAction[i] = -item.getProductionNum();
                        }
                    }
                }
            }
        }
    }

    /**
     * First()算法
     */
    private Set<Integer> first(List<Integer> beta){
        Set<Integer> first = new HashSet<>();
        for (int i = 0; i < beta.size(); i++) {
            if(beta.get(i) < 0){
                productions.get(- beta.get(i)).forEach(production -> {
                   first.addAll(production.getSigns());
                });
                break;
            }else if(beta.get(i) > 0){
                first.add(beta.get(i));
                break;
            } else if(beta.get(i) == 0){
                break;
            }
        }
        return first;
    }

}
