package assign7.inter ;

import assign7.parser.*;
import assign7.typechecker.*;
import assign7.visitor.* ;
import assign7.lexer.* ;
import assign7.ast.*;


public class InterCode extends ASTVisitor 
{
    public TypeChecker checker=null;
    public CompilationUnit cu=null;
    ExprNode lhs=null;
    ExprNode last=null;
    ExprNode temp1=null;
    private LabelNode globalLabel;
    
    
    int level=0;
    String indent="...";
    int tempCount=0;

    public InterCode (TypeChecker checker) 
    {
        this.checker=checker;
        cu=checker.cu;
        visit(cu);
    }
    public InterCode()
    {
        visit(this.checker.cu);
    }



    //////////////Utility code///////////////////
    

    void error (String s) {

	    //throw new Error ("near line " + lexer.line + ": " + s) ;
        println(s);
        exit(1) ;
    }

    

    void print(String s){

        System.out.print(s) ;
    }
    
    void println(String s){
        
        System.out.println(s) ;
    }

    void exit(int n){

        System.exit(n) ;
    }
    void printSpace()
    {
        System.out.print(" ");
    }
    /////////////////Visit methods////////////////
    public void visit(CompilationUnit n)
    {
        System.out.println("\n*****************\n            Intercode Starts          \n*****************");
        System.out.println("Compilation Unit");
        n.block.accept(this);
        System.out.println("\n*****************\n            Intercode Ends          \n*****************");
    }
    public void visit(BlockStatementNode n)
    {
        System.out.println("BlockStatementNode");
        for(DeclarationNode decl:n.decls)
        {
            decl.accept(this);
        }
        for(StatementNode stmt:n.stmts)
        {
            stmt.accept(this);
        }
    }
    public void visit(DeclarationNode n)
    {
        System.out.println("Declaration Node");
        n.type.accept(this);
        n.id.accept(this);
    }
    public void visit(TypeNode n)
    {
        System.out.println("TypeNode: "+n.basic);
        if(n.array!=null)
        {
            n.array.accept(this);
        }
    }
    public void visit(ArrayTypeNode n)
    {
        System.out.println("ArrayTypeNode: "+n.size);
        if(n.type!=null)
        {
            n.type.accept(this);
        }
    }
    public void visit(ParenthesesNode n)
    {
        System.out.println("ParenthesesNode");
        n.expr.accept(this);
    }
    

    public void visit(IfStatementNode n)
    {
        System.out.println("IfStatementNode");
        n.cond.accept(this);
        IdentifierNode temp=TempNode.newTemp();
        
        ParenthesesNode cond=n.cond;
        ExprNode expr=null;
        
        if(cond.expr instanceof BinExprNode)
        {
            expr=(BinExprNode)cond.expr;
        }
        else if(cond.expr instanceof TrueNode)
        {
            expr=(TrueNode)cond.expr;
        }
        else if(cond.expr instanceof FalseNode)
        {
            expr=(FalseNode)cond.expr;
        }
        else if(cond.expr instanceof ParenthesesNode)
        {
            expr=(ParenthesesNode)cond.expr;
        }
        AssignmentNode assign=new AssignmentNode(temp,expr);
        n.assigns.add(assign);

        n.cond.expr=temp;

        n.falseLabel=LabelNode.newLabel();
        n.stmt.accept(this);

        if(n.else_stmt!=null)
        {
            n.trueLabel=LabelNode.newLabel();
            System.out.println("Else clause");
            n.else_stmt.accept(this);
        }
        else
        {
            n.trueLabel=n.falseLabel;
        }
    }

    //Need to do stuff here
    public void visit(WhileStatementNode n)
    {
        System.out.println("WhileStatementNode");
        n.cond.accept(this);
        IdentifierNode temp=TempNode.newTemp();
        ParenthesesNode cond=n.cond;
        ExprNode expr=null;

        if(cond.expr instanceof BinExprNode)
        {
            expr=(BinExprNode)cond.expr;
        }
        else if(cond.expr instanceof TrueNode)
        {
            expr=(TrueNode)cond.expr;
        }
        else if(cond.expr instanceof FalseNode)
        {
            expr=(FalseNode)cond.expr;
        }
        AssignmentNode assign=new AssignmentNode(temp,expr);
        n.assigns.add(assign);

        n.cond.expr=temp;
        n.trueLabel=LabelNode.newLabel();
        n.stmt.accept(this);
        n.falseLabel=LabelNode.newLabel();
    }
    public void visit(DoWhileStatementNode n)
    {
        n.trueLabel=LabelNode.newLabel();
        System.out.println("DoWhileStatementNode");
        n.stmt.accept(this);

        // BUG 5 FIX: visit condition temps extracted during parsing so the
        // InterCode pass still traverses them even though they are no longer
        // in the enclosing block's stmt list.
        for (StatementNode cs : n.condStmts) {
            cs.accept(this);
        }

        n.cond.accept(this);
        IdentifierNode temp=TempNode.newTemp();
        ParenthesesNode cond=n.cond;
        ExprNode expr=null;

        if(cond.expr instanceof BinExprNode)
        {
            expr=(BinExprNode)cond.expr;
        }
        else if(cond.expr instanceof TrueNode)
        {
            expr=(TrueNode)cond.expr;
        }
        else if(cond.expr instanceof FalseNode)
        {
            expr=(FalseNode)cond.expr;
        }
        AssignmentNode assign=new AssignmentNode(temp,expr);
        n.assigns.add(assign);

        n.cond.expr=temp;
        n.falseLabel=LabelNode.newLabel();
    }
    

    public void visit(ArrayAccessNode n)
    {
        println("ArrayAccessNode");
        n.id.accept(this);
        n.index.accept(this);
    }
    public void visit(ArrayDimsNode n)
    {
        System.out.println("ArrayDimsNode");
        n.size.accept(this);
        if(n.dim!=null)
        {
            n.dim.accept(this);
        }
    }

    /////////Need work here////////////
    public void visit(BreakStatementNode n)
    {
        
    }
    


    public void visit(TrueNode n)
    {
        System.out.println("TrueNode");
    }
    public void visit(FalseNode n)
    {
        System.out.println("FalseNode");
    }
    public void visit(AssignmentNode n)
    {
        System.out.println("AssignmentNode");
        n.left.accept(this);
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
    public void visit(BinExprNode n)
    {
        System.out.println("BinExprNode: "+n.op);

        if (n.left instanceof IdentifierNode)
            ((IdentifierNode)n.left).accept(this) ;
        else if (n.left instanceof NumNode)
            ((NumNode)n.left).accept(this) ;
        else if (n.left instanceof RealNode)
            ((RealNode)n.left).accept(this) ;
        else if(n.left instanceof ParenthesesNode)
            ((ParenthesesNode)n.left).accept(this);
        else if(n.left instanceof ArrayAccessNode)
            ((ArrayAccessNode)n.left).accept(this);
        else if(n.left instanceof BinExprNode)
        {
            ((BinExprNode)n.left).accept(this) ;
        }
        else
        {
            
        }

        if(n.right!=null)
        {
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
    }
    public void visit(IdentifierNode n)
    {

    }
    public void visit(NumNode n)
    {

    }
    public void visit(RealNode n)
    {

    }

    //////////IntermediateCode////////
    //public void visit(GotoNode n)
    //{

    //}
    public void visit(LabelNode n)
    {
        
    }
    public void visit(TempNode n)
    {
        
    }


}

