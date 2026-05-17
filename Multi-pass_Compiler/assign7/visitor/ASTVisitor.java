package assign7.visitor ;

import assign7.inter.*;
import assign7.ast.* ;

public class ASTVisitor {

    public void visit (CompilationUnit n) {

        n.block.accept(this) ;
    }

    public void visit (BlockStatementNode n){

        //n.decls.accept(this);
        for (DeclarationNode decl : n.decls)//java enhanced for-loop loops through every decls
            // of the ArrayList
            decl.accept(this);
        //n.stmts.accept(this);
        for (StatementNode stmt : n.stmts)
            stmt.accept(this);
    }

    // public void visit (Declarations n){
    // // check if n.decls is null to eliminate left recursion
    //     if (n.decls != null){

    //         n.decl.accept(this);
    //         n.decls.accept(this);
    //     }
    //}

    public void visit (DeclarationNode n){

        n.type.accept(this);
        n.id.accept(this);
    }

    public void visit (TypeNode n) {

        n.array.accept(this) ;
    }

    public void visit (ArrayTypeNode n) {

        n.type.accept(this) ;
    }

    // public void visit (Statements n){

    //     if (n.stmts != null){

    //         n.stmt.accept(this);
    //         n.stmts.accept(this);
    //     }
    // }

    public void visit (StatementNode n){

    }

    public void visit (ParenthesesNode n) {

        n.expr.accept(this);
    }

    public void visit (IfStatementNode n) {

        n.cond.accept(this);
        n.stmt.accept(this);

        if (n.else_stmt != null)
            n.else_stmt.accept(this);
    }

    public void visit (WhileStatementNode n) {

        n.cond.accept(this);
        
        n.stmt.accept(this);
        
    }

    public void visit ( DoWhileStatementNode n){

        n.stmt.accept(this);
        n.cond.accept(this);
    }

    //a[i][j]
    public void visit ( ArrayAccessNode n){

    }

    public void visit ( ArrayDimsNode n){

        n.size.accept(this);

        if(n.dim != null)
            n.dim.accept(this);
    }

    public void visit(AssignmentNode n){

        n.left.accept(this) ;
        //n.right.accept(this);
        //check if it is an integer or a float:
        if (n.right instanceof IdentifierNode)
            ((IdentifierNode)n.right).accept(this) ;
        else if (n.right instanceof NumNode)
            ((NumNode)n.right).accept(this) ;
        else if (n.right instanceof RealNode)
            ((RealNode)n.right).accept(this) ;
        else if(n.right instanceof ParenthesesNode)
            ((ParenthesesNode)n.right).accept(this);
        else if(n.right instanceof ArrayAccessNode)
            ((ArrayAccessNode)n.right).accept(this);
        else
            ((BinExprNode)n.right).accept(this) ;
    }

    public void visit (BreakStatementNode n) {

    }

    public void visit (TrueNode n) {
        
    }

    public void visit (FalseNode n) {
        
    }

    public void visit (ExprNode n){

    }

    public void visit (BinExprNode n){
        //n.left.accept(this);
        //n.right.accept(this);
    }

    public void visit (IdentifierNode n) {

    }

    public void visit( NumNode n){
        
    }

    public void visit (RealNode n){
        
    }
    //public void visit(GotoNode n)
    //{

    //}
    public void visit(LabelNode n)
    {

    }
    public void visit(TempNode n)
    {

    }
/*
    public void visit (BoolNode n){
        
    }

    public void visit (BreakNode n){
        
    }

    public void visit (DoWhileLoopNode n){
        
    }

    public void visit (EqualityNode n){
        
    }

    public void visit (FactorNode n){
        
    }

    public void visit (FalseNode n){
        
    }

    public void visit (IfElseNode n){
        
    }

    public void visit (IfNode n){
        
    }

    public void visit (RelNode n){
        
    }

    public void visit (TermNode n){
        
    }

    public void visit (TrueNode n){
        
    }

    public void visit (UnaryNode n){
        
    }

    public void visit (WhileNode n){
        
    } */

}
