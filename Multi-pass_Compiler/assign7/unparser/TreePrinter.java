package assign7.unparser;

import assign7.parser.* ;
import assign7.ast.* ;
import assign7.visitor.* ;

public class TreePrinter extends ASTVisitor {

    public Parser parser = null ;

    int     level = 0 ;
    String indent = "..." ;

    public TreePrinter (Parser parser) {

        this.parser = parser ;
        visit(this.parser.cu) ;
    }

    public TreePrinter () {

        visit(this.parser.cu) ;
    }



    void print(String s) {

        System.out.print(s) ;
    }

    void println(String s) {

        System.out.println(s) ;
    }

    void printSpace() {

        System.out.print(" ") ;
    }

    int indent_level = 0 ;

    void indentUp() {

        indent_level ++ ;
    }

    void indentDown() {

        indent_level -- ;
    }

    void printIndent() {

        String s = "" ;
        for (int i=0; i<indent_level; i++) {

            s += "  " ;
        }

        print (s) ;
    }

    void printDotDotDot() {

        String s = "" ;
        for (int i=0 ; i<indent_level ; i++)
            s += "..." ;
        
        print(s) ;
    }


    public void visit (CompilationUnit n) {

        System.out.println("******************************");
        System.out.println("*     Tree Printer starts    *");
        System.out.println("******************************");
        System.out.println();
        System.out.println("CompilationUnit");

        indentUp() ;
        n.block.accept(this) ;
        indentDown() ;
    }


    public void visit (BlockStatementNode n){

        printDotDotDot();
        System.out.println("BlockStatementNode");
        
        for (DeclarationNode decl : n.decls) {
            
            indentUp() ;
            decl.accept(this);
            indentDown() ;
        }

        for (StatementNode stmt : n.stmts) {
            indentUp() ;
            stmt.accept(this);
            indentDown() ;
        }
    }

    // public void visit (Declarations n){

    //     if (n.decls != null){

    //         n.decl.accept(this) ;

    //         n.decls.accept(this) ;
    //     }
    // }

    public void visit(DeclarationNode n){

        printDotDotDot() ;
        System.out.println("DeclarationNode") ;
        
        indentUp() ;
        n.type.accept(this) ;
        indentDown();

        printDotDotDot() ;
        n.id.accept(this) ;
        indentDown() ;

    }

    public void visit (TypeNode n){

        printDotDotDot() ;
        System.out.println("TypeNode: " + n.basic) ;

        if (n.array != null){

            indentUp() ;
            n.array.accept(this) ;
            indentDown() ;

        }
    }

    public void visit (ArrayTypeNode n){

        printDotDotDot() ;
        System.out.println("ArrayTypeNode: " + n.size) ;

        if (n.array != null){

            indentUp() ;
            n.type.accept(this) ;
            indentDown() ;

        }
    }

    // public void visit (Statements n) {

    //     if (n.stmts != null) {

    //         n.stmt.accept(this) ;

    //         n.stmts.accept(this) ;
    //     }
    // }

    public void visit (ParenthesesNode n) {

        printDotDotDot();
        System.out.println("ParenthesesNode");
        
        indentUp();
        n.expr.accept(this);
        indentDown();

    }

    public void visit (IfStatementNode n) {

        printDotDotDot();
        System.out.println("IfStatementNode");
        
        indentUp();
        n.cond.accept(this);
        indentDown();

        indentUp();
        n.stmt.accept(this);
        indentDown();

        if (n.else_stmt != null){
            printDotDotDot();
            System.out.println("Else Clause");

            indentUp();
            n.else_stmt.accept(this);
            indentDown();
        }

    }

    public void visit (WhileStatementNode n) {

        printDotDotDot();
        System.out.println("WhileStatementNode");
        
        indentUp();
        n.cond.accept(this);
        indentDown();

        indentUp();
        n.stmt.accept(this);
        indentDown();
    }

    public void visit (DoWhileStatementNode n) {

        printDotDotDot();
        System.out.println("DoWhileStatementNode");

        indentUp();
        n.stmt.accept(this);
        indentDown();
        
        indentUp();
        n.cond.accept(this);
        indentDown();

    }


    public void visit (ArrayAccessNode n) {

        printDotDotDot();
        System.out.println("ArrayAccessNode");

        indentUp();
        n.id.accept(this);
        indentDown();
        
        indentUp();
        n.index.accept(this);
        indentDown();

    }

    public void visit (ArrayDimsNode n) {

        printDotDotDot();
        System.out.println("ArrayDimsNode");

        indentUp();
        n.size.accept(this);
        indentDown();
        
        if (n.dim != null){
            
            indentUp();
            n.dim.accept(this);
            indentDown();
        }
    }

    public void visit (BreakStatementNode n) {

        printDotDotDot();
        System.out.println("BreakStatementNode");
    }

    public void visit (TrueNode n) {

        printDotDotDot();
        System.out.println("TrueNode");
    }

    public void visit (FalseNode n) {

        printDotDotDot();
        System.out.println("FalseNode");
    }


    public void visit (AssignmentNode n) {

        printDotDotDot() ;
        System.out.println("AssignmentNode") ;

        indentUp() ;
        n.left.accept(this) ;
        indentDown() ;
           
        indentUp();
        printDotDotDot() ;
        println("operator: = ") ;
        indentDown() ;

        indentUp() ;

        if (n.right instanceof IdentifierNode)
            ((IdentifierNode)n.right).accept(this) ;
        else if (n.right instanceof NumNode)
            ((NumNode)n.right).accept(this) ;
        else if (n.right instanceof RealNode)
            ((RealNode)n.right).accept(this) ;
        else if (n.right instanceof ArrayAccessNode)
            ((ArrayAccessNode)n.right).accept(this); 
        else { // BinExpr ???
                ((BinExprNode)n.right).accept(this) ;
        }

        indentDown() ;

    }

    public void visit (BinExprNode n) {
        
        printDotDotDot() ;
        System.out.println("BinExprNode: " + n.op) ;

        indentUp() ;

        if (n.left instanceof IdentifierNode)
            
            ((IdentifierNode)n.left).accept(this) ;
        else if (n.left instanceof NumNode)
            ((NumNode)n.left).accept(this) ;
        else if (n.left instanceof RealNode)
            ((RealNode)n.left).accept(this) ;
        else if (n.left instanceof ArrayAccessNode)
            ((ArrayAccessNode)n.left).accept(this);
        else
            ((BinExprNode)n.left).accept(this) ;

        if (n.right != null)   { 

            if (n.right instanceof IdentifierNode)
                ((IdentifierNode)n.right).accept(this) ;
            else if (n.right instanceof NumNode)
                ((NumNode)n.right).accept(this) ;
            else if (n.right instanceof RealNode)
                ((RealNode)n.right).accept(this) ;
            else if (n.right instanceof ArrayAccessNode)
                ((ArrayAccessNode)n.right).accept(this);
            else {
                ((BinExprNode)n.right).accept(this) ;
            }
        }else {
            
        }

        indentDown() ;
    }

    public void visit (IdentifierNode n) {

        printDotDotDot() ;
        n.printNode() ;
    }

    public void visit (NumNode n) {

        printDotDotDot() ;
        n.printNode() ;
    }
    
    public void visit (RealNode n) {

        printDotDotDot() ;
        n.printNode() ;

    }

}

