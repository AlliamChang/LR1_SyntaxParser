package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 53068 on 2017/11/15 0015.
 */
public class ItemSet {

    private static int count = 0;

    private int id;
    private List<Item> items = new ArrayList<>();

    /**
     * 项集
     */
    public ItemSet() {
        id = count;
        count ++;
    }

    public void print(){
        items.forEach(item -> item.print());
    }

    public void add(Item item){
        int index = coreIndexOf(item);
        if(index < 0)
            items.add(item);
        else {
            items.get(index).addPredictors(item.getPredictors());
        }
    }

    public void addAll(List<Item> items) {
        items.forEach(item -> add(item));
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemSet)) return super.equals(obj);

        ItemSet other = (ItemSet)obj;
        boolean isEquals = true;
        if(this.items.size() == other.items.size()) {
            for (int i = 0; i < other.items.size(); i++) {
                if(this.indexOf(other.items.get(i)) < 0){
                    isEquals = false;
                    break;
                }
            }
        }else {
            isEquals = false;
        }

        return isEquals;
    }

    public int indexOf(Item item){
        int index = -1;
        for(int i = 0; i < this.items.size(); i ++){
            if(this.items.get(i).equals(item)){
                index = i;
                break;
            }
        }
        return index;
    }

    public int coreIndexOf(Item item){
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (this.items.get(i).equalsCore(item)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }
}
