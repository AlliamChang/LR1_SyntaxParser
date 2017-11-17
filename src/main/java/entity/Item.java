package entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 53068 on 2017/11/14 0014.
 */
public class Item {

    private Set<Integer> predictors;//预测符集
    private int position;           //移进符的位置(即"."的位置)
    private Production production;  //产生式

    /**
     * 项, 项集Ii中的每个独立项
     */
    public Item(Set<Integer> predictors, int position, Production production) {
        this( position, production);
        this.predictors = predictors;
    }

    /**
     * 项
     * @param position   移进符的位置
     */
    public Item(int position, Production production) {
        this.position = position;
        this.production = production;
        predictors = new HashSet<>();
    }

    public void addPredictor(Integer predictor){
        predictors.add(predictor);
    }

    public void addPredictors(Set<Integer> predictors){
        this.predictors.addAll(predictors);
    }

    public Set<Integer> getPredictors() {
        return predictors;
    }

    public int getProductionNum() {
        return production.getId();
    }

    public Production getProduction() {
        return production;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Item){
            Item item = (Item)obj;
            boolean isEquals = (this.predictors.equals(item.predictors)
                    && (this.getProductionNum() == item.getProductionNum())
                    && (this.position == item.position) );
            return isEquals;
        }else {
            return super.equals(obj);
        }
    }

    /**
     * 拥有相同的核心
     * @param item
     * @return
     */
    public boolean equalsCore(Item item){
        boolean hasEqualCore = (this.position == item.position)
                && (this.getProductionNum() == item.getProductionNum());
        return hasEqualCore;
    }

    private boolean flag = true;
    public void print(){
        System.out.print(production.getName()+":");
        for (int i = 0; i < production.getSigns().size(); i++) {
            if (i == position) {
                System.out.print(".");
            }
            System.out.print(production.getSigns().get(i)+" ");
        }

        predictors.iterator().forEachRemaining(i -> {
            if(flag){
                System.out.print(","+i);
                flag = false;
            }else {
                System.out.print("/"+i);
            }
        });
        System.out.println();
    }
}
