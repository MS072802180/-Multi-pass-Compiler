package assign7.unparser;

import assign7.lexer.* ;
import assign7.parser.* ;
import assign7.ast.* ;
import assign7.visitor.* ;
import assign7.typechecker.*;
import assign7.inter.*;

public class Unparser extends ASTVisitor {

    public InterCode inter=null;
    public CompilationUnit cu = null ;
    public int binExprValue=0;

    public Unparser (InterCode inter) {

        this.inter = inter ;
        cu=inter.cu;
        visit(cu) ;
    }

    public Unparser () {

        visit(this.inter.cu) ;
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

    int indent = 0 ;

    void indentUp() {

        indent ++ ;
    }

    void indentDown() {

        indent -- ;
    }

    void printIndent() {

        String s = "" ;
        for (int i=0; i<indent; i++) {

            s += "  " ;
        }

        print (s) ;
    }

    ///////////Unparser starts/////////////
    public void visit (CompilationUnit n) {
        System.out.println("Unparser Starting");
        indentUp();
        n.block.accept(this) ;
        indentDown();
    }

    public void visit (BlockStatementNode n) {
        for(StatementNode stmt: n.stmts)
        {
            if(stmt instanceof BreakStatementNode)
            {
                ((BreakStatementNode)stmt).label=n.label;
            }
            else if(stmt instanceof IfStatementNode)
            {
                ((IfStatementNode)stmt).label=n.label;
            }
            
            stmt.accept(this);
            
        }
    }


    // public void visit(Declarations n){

    //     if (n.decls != null){
            
    //         n.decl.accept(this) ;
    //         n.decls.accept(this) ;
    //     }
    // }

    public void visit(DeclarationNode n){
    
        n.type.accept(this) ;    
       
        print(" "); 
        n.id.accept(this) ;      

        println(" ;") ;
        
    }

    public void visit (TypeNode n){

        printIndent() ;
        print(n.basic.toString()) ;

        if (n.array != null)
            n.array.accept(this) ;
    }

    
    public void visit (ArrayTypeNode n){

        print("[") ;
        print("" + n.size) ;
        print("]") ;
        
        if (n.type != null) 
            n.type.accept(this) ; 
    }


    // public void visit (Statements n) {

    //     if (n.stmts != null) {

    //         n.stmt.accept(this) ;
    //         n.stmts.accept(this) ;
    //     }
    // }
    
    public void visit(StatementNode n){
        
    }


    public void visit(ParenthesesNode n){

        
        n.expr.accept(this);
    }

    public void visit(IfStatementNode n)
    {
        if(n.stmt instanceof BreakStatementNode)
        {
            ((BreakStatementNode)n.stmt).label=n.label;
        }
        for(AssignmentNode assign:n.assigns)
        {
            assign.accept(this);
        }
        printIndent();
        print("ifFalse ");
        n.cond.accept(this);
        println(" goto "+n.falseLabel.id);
        if(n.stmt instanceof BlockStatementNode)
        {
            
            ((BlockStatementNode)n.stmt).label=n.label;
            
        }
        n.stmt.accept(this);
        
        if(n.else_stmt!=null)
        {
            printIndent();
            println("goto "+n.trueLabel.id);
            println(n.falseLabel.id+":");
            n.else_stmt.accept(this);
            println(n.trueLabel .id+":");
        }
        else
        {
            println(n.falseLabel.id+":");
        }
        
    }

    public void visit(WhileStatementNode n){

        // BUG 1 FIX: assigns were printed BEFORE the trueLabel, meaning the
        // condition temp (e.g. t6 = false) was only evaluated once before the
        // loop started.  Moving them AFTER the label means they re-execute on
        // every iteration when control jumps back to trueLabel.
        println(n.trueLabel.id+":");
        for(AssignmentNode assign:n.assigns)
        {
            assign.accept(this);
        }
        printIndent();
        print("ifFalse ");
        n.cond.accept(this);
        println(" goto "+n.falseLabel.id);
        
        if (!(n.stmt instanceof BlockStatementNode))
            indentUp();
        if(n.stmt instanceof BreakStatementNode)
        {
            ((BreakStatementNode)n.stmt).label=n.falseLabel;
        }
        if(n.stmt instanceof IfStatementNode)
        {
            ((IfStatementNode)n.stmt).label=n.falseLabel;
        }
        if(n.stmt instanceof BlockStatementNode)
        {
            ((BlockStatementNode)n.stmt).label=n.falseLabel;
        }
        n.stmt.accept(this);
        if (!(n.stmt instanceof BlockStatementNode))
            indentDown();
        
        
        
        printIndent();
        println("goto "+n.trueLabel.id);
        println(n.falseLabel.id+":");
    }

    public void visit(DoWhileStatementNode n){

        // BUG 2 FIX: assigns were printed BEFORE the trueLabel so the condition
        // temp (e.g. t5 = v < t1) was computed once before the loop.  A do-while
        // condition must be evaluated AFTER every execution of the body.
        println(n.trueLabel.id+":");

        if (!(n.stmt instanceof BlockStatementNode))
            indentUp();
        if(n.stmt instanceof BlockStatementNode)
        {
            ((BlockStatementNode)n.stmt).label=n.falseLabel;
        }

        n.stmt.accept(this);
        if (!(n.stmt instanceof BlockStatementNode))
            indentDown();

        // BUG 5 FIX: emit the condition sub-temps (e.g. t1 = a[i]) that were
        // extracted from the enclosing block during parsing.  They must appear
        // here — after the body and before the condition BinExpr — so they are
        // re-evaluated on every iteration.
        for(StatementNode condStmt : n.condStmts)
        {
            condStmt.accept(this);
        }
        // BUG 2 FIX (cont.): condition BinExpr assign also goes here, after body.
        for(AssignmentNode assign : n.assigns)
        {
            assign.accept(this);
        }

        printIndent();
        print("ifTrue ");
        n.cond.accept(this);
        println(" goto "+n.trueLabel.id);
        println(n.falseLabel.id+":");

    }

    public void visit (ArrayAccessNode n ) {
        n.id.accept(this);
        n.index.accept(this);
    }

    public void visit (ArrayDimsNode n) {

        print("[");
        n.size.accept(this);
        print("]");

        if (n.dim != null) 
            n.dim.accept(this);
    }

    /////This will need some work as well//////
    public void visit(AssignmentNode n){

        printIndent() ;
        
        
        
        n.left.accept(this) ;
        print(" = ") ;
        
        if (n.right instanceof ParenthesesNode)
            ((ParenthesesNode)n.right).accept(this);
        // BUG 4a FIX: was a bare 'if', not 'else if'.  Although the class
        // hierarchy means no node can be both ParenthesesNode and IdentifierNode,
        // the missing else made the intent unclear and could hide future regressions.
        else if (n.right instanceof IdentifierNode)
            ((IdentifierNode)n.right).accept(this);
        else if (n.right instanceof NumNode)
            ((NumNode)n.right).accept(this);
        else if (n.right instanceof RealNode)
            ((RealNode)n.right).accept(this);
        else if (n.right instanceof ArrayAccessNode)
            ((ArrayAccessNode)n.right).accept(this);
        else if (n.right instanceof BinExprNode)
        {
            // BUG 4b FIX: the cast (IdentifierNode)n.left threw ClassCastException
            // once Bug 3 was fixed and LHS array accesses (e.g. a[i] = t+4) were
            // no longer replaced by a temp IdentifierNode.  The temp field is only
            // used for propagating context in BinExprNode traversal; guard the cast.
            if (n.left instanceof IdentifierNode)
                ((BinExprNode)n.right).temp=(IdentifierNode)n.left;
            ((BinExprNode)n.right).accept(this);
        }
        else if (n.right instanceof TrueNode)
            ((TrueNode)n.right).accept(this);
        else if(n.right instanceof FalseNode)
            ((FalseNode)n.right).accept(this);
        else
        {
        }
        println(" ;") ;
        
    }
    

    public void visit (BinExprNode n) {
        
        if (n.left instanceof ParenthesesNode)
        {
            ((ParenthesesNode)n.left).temp=n.temp;
            ((ParenthesesNode)n.left).accept(this);
        }
        else if (n.left instanceof IdentifierNode)
        {
            ((IdentifierNode)n.left).temp=n.temp;
            ((IdentifierNode)n.left).accept(this);
        }
        else if (n.left instanceof NumNode)
        {
            ((NumNode)n.left).temp=n.temp;
            ((NumNode)n.left).accept(this);
        }
        else if (n.left instanceof RealNode)
        {
            ((RealNode)n.left).temp=n.temp;
            ((RealNode)n.left).accept(this);
        }
        else if (n.left instanceof ArrayAccessNode)
        {
            ((ArrayAccessNode)n.left).temp=n.temp;
            ((ArrayAccessNode)n.left).accept(this);
        }
        else if (n.left instanceof BinExprNode)
        {
            ((BinExprNode)n.left).temp=n.temp;
            ((BinExprNode)n.left).accept(this);
        }
        else
        {

        }
        
        
        if (n.op != null)
        {
            print(" " + n.op.toString() + " ") ;
        }

        if (n.right != null) 
        {
            if (n.right instanceof ParenthesesNode)
            {
                ((ParenthesesNode)n.right).temp=n.temp;
                ((ParenthesesNode)n.right).accept(this);
            }
            else if (n.right instanceof IdentifierNode)
            {
                ((IdentifierNode)n.right).temp=n.temp;
                ((IdentifierNode)n.right).accept(this);
            }
            else if (n.right instanceof NumNode)
            {
                ((NumNode)n.right).temp=n.temp;
                ((NumNode)n.right).accept(this);
            }
            else if (n.right instanceof RealNode)
            {
                ((RealNode)n.right).temp=n.temp;
                ((RealNode)n.right).accept(this);
            }
            else if (n.right instanceof ArrayAccessNode)
            {
                ((ArrayAccessNode)n.right).temp=n.temp;
                ((ArrayAccessNode)n.right).accept(this);
            }
            else if (n.right instanceof BinExprNode)
            {
                ((BinExprNode)n.right).temp=n.temp;
                ((BinExprNode)n.right).accept(this);
            }
            else
            {
                
            }
        }
        
        
    }

    public void visit (IdentifierNode n) {

        //printIndent() ;
        print(n.id) ;
        //println(" ;") ;
    }

    public void visit (NumNode n) {

        //printIndent() ;
        print("" + n.value) ; 
        //println(" ;") ;
    }

    public void visit (RealNode n) {

        print("" + n.value) ;

    }
    public void visit (BreakStatementNode n) {
        indentUp();
        printIndent();
        //println("break ;");
        println("goto "+n.label.id);
        indentDown();
    }

    public void visit (TrueNode n) {

        print("true");
    }

    public void visit (FalseNode n) {

        print("false");
    }
}
