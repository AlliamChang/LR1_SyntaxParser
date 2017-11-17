package exception;

/**
 * Created by 53068 on 2017/11/15 0015.
 */
public class ProductionException extends Exception {

    public ProductionException(){
        super("Error format in input");
    }

    public ProductionException(String name){
        super("There is no production {"+name+"}");
    }
}
