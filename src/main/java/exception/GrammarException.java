package exception;

/**
 * Created by 53068 on 2017/11/16 0016.
 */
public class GrammarException extends Exception {

    public GrammarException() {
        super("This is ambiguous grammar");
    }
}
