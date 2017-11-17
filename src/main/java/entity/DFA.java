package entity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by 53068 on 2017/11/16 0016.
 */
public class DFA<ItemSet> extends ArrayList<ItemSet> {

    @Override
    public int indexOf(Object o) {
        if(!(o instanceof entity.ItemSet)) return super.indexOf(o);
        int index = -1;
        for (int i = 0; i < this.size(); i++) {
            if(this.get(i).equals(o)){
                index = i;
                break;
            }
        }
        return index;
    }

    public int addFromIndex(ItemSet itemSet) {
        int index = indexOf(itemSet);
        if(index < 0){
            super.add(itemSet);
            return this.size()-1;
        }else {
            return index;
        }
    }

//    @Override
//    public boolean addAll(Collection<? extends ItemSet> c) {
//        for (ItemSet itemSet : c) {
//            this.addFromIndex(itemSet);
//        }
//        return true;
//    }
}
