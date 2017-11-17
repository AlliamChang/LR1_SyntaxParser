import exception.GrammarException;
import syntax_parser.SyntaxAnalyzer;

/**
 * Created by 53068 on 2017/11/8 0008.
 */
public class Main {

    public static void main(String[] args){
        SyntaxAnalyzer analyzer = SyntaxAnalyzer.getAnalyzer();
        analyzer.getInput("test");
        try {
            analyzer.compile();
            analyzer.analyze();
        } catch (GrammarException e) {
            e.printStackTrace();
        }

    }
}
