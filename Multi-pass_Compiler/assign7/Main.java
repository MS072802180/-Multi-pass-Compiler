package assign7 ;

import assign7.lexer.* ;
import assign7.parser.* ;
import assign7.unparser.* ;
import assign7.typechecker.* ;
import assign7.inter.*;

public class Main {

    public static void main (String[] args) 
    
    {

        Lexer lexer = new Lexer() ;
        Parser parser = new Parser(lexer) ;
        //TreePrinter tree = new TreePrinter(parser);
        TypeChecker checker = new TypeChecker(parser);
        InterCode inter=new InterCode(checker);
        Unparser unparser = new Unparser(inter);

    }
}