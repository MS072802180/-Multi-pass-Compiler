package assign7.typechecker;

import assign7.lexer.* ;
import assign7.parser.* ;
import assign7.ast.* ;
import assign7.visitor.* ;

public class TypeChecker extends ASTVisitor {

    public Parser parser = null;
    public CompilationUnit cu = null; 

    int level = 0;
    String indent = "...";
    int breakChecker=0;

    public TypeChecker(Parser parser) {

        this.parser = parser;
        cu = parser.cu; 
        // visit(this.parser.cu);
        visit(cu);
    }

    public TypeChecker() {

        visit(this.parser.cu);
    }


    void error(String s){
        
        println(s);
        exit(1);
    }

    void exit(int n){
        
        System.exit(n);
    }

    void print(String s) {

        System.out.print(s);
    }

    void println(String s) {

        System.out.println(s);
    }

    void printSpace() {

        System.out.print(" ");
    }

    //visit methods 

    public void visit (CompilationUnit n) {

        System.out.println("*******************************");
        System.out.println("*      TypeChecker starts     *");
        System.out.println("*******************************");
        System.out.println();
        System.out.println("CompilationUnit");

        n.block.accept(this) ;

    }

    public void visit (BlockStatementNode n){

        System.out.println("BlockStatementNode") ;

        for (DeclarationNode decl : n.decls)
            decl.accept(this) ;
        for (StatementNode stmt : n.stmts)
            stmt.accept(this) ;
    }

    // public void visit (Declarations n){

    //     if (n.decls != null){
    //         n.decl.accept(this);
    //         n.decls.accept(this);
    //     }
    // }

    public void visit(DeclarationNode n){

        System.out.println("DeclarationNode") ;

        n.type.accept(this) ;
        n.id.accept(this) ;
    }

    public void visit (TypeNode n){

        System.out.println("TypeNode: " + n.basic) ;

        if (n.array != null){
            n.array.accept(this) ;
        }
    }

    public void visit (ArrayTypeNode n){

        System.out.println("ArrayTypeNode: " + n.size);

        if (n.type != null){
            n.type.accept(this) ;
        }
    }

    // public void visit (Statements n){

    //     if (n.stmts != null){

    //         n.stmt.accept(this) ;
    //         n.stmts.accept(this) ;
    //     }
    // }

    public void visit (ParenthesesNode n) {

        System.out.println("ParenthesesNode") ;

        n.expr.accept(this) ;
        System.out.println(n.expr.type);
        n.type=n.expr.type;
    }

    public void visit (IfStatementNode n) {

        System.out.println("IfStatementNode") ;

        n.cond.accept(this) ;
        if(n.cond.type!=Type.Bool)
        {
            error("Please check the type for the ifstatement. It must be of a boolean type.");
        }

        n.stmt.accept(this) ;

        if (n.else_stmt != null){

            System.out.println("Else Clause") ;

            n.else_stmt.accept(this) ;
        }

    }

    public void visit (WhileStatementNode n) {

        System.out.println("WhileStatementNode") ;
        breakChecker++;
        n.cond.accept(this) ;
        if(n.cond.type!=Type.Bool)
        {
            error("Please check the type for the whileStatement. It must be of a boolean type.");
        }
        n.stmt.accept(this) ;
        breakChecker--;
    }

    public void visit (DoWhileStatementNode n) {
        breakChecker++;
        System.out.println("DoWhileStatementNode") ;

        n.stmt.accept(this) ;

        // BUG 5 FIX: visit condition temps that were extracted from the enclosing
        // block during parsing (see Parser fix) so they are still type-checked.
        for (StatementNode cs : n.condStmts) {
            cs.accept(this);
        }

        n.cond.accept(this) ;
        breakChecker--;
    }

    public void visit (ArrayAccessNode n) {

        System.out.println("ArrayAccessNode") ;
        n.id.accept(this);
        
        n.index.accept(this) ;
    }

    public void visit (ArrayDimsNode n) {

        System.out.println("ArrayDimsNode") ;

        n.size.accept(this) ;
        
        if (n.dim != null){

            n.dim.accept(this) ;
        }
    }

    public void visit (BreakStatementNode n) {
        if(breakChecker==0)
        {
            error("Break is not in a loop");
        }
        System.out.println("BreakStatementNode") ;
    }

    public void visit (TrueNode n) {

        System.out.println("TrueNode") ;
    }

    public void visit (FalseNode n) {

        System.out.println("FalseNode") ;
    }

    public void visit(AssignmentNode n){

        System.out.println("AssignmentNode") ;
        Type leftType=null;
        n.left.accept(this) ;
        // save n.left of the IdentifierNode to the IdentifierNode leftId
        if(n.left instanceof IdentifierNode)
        {
            IdentifierNode left = (IdentifierNode)n.left ;
            leftType = left.type ;   
        }
        else
        {
            ArrayAccessNode left=(ArrayAccessNode)n.left;
            leftType=left.type;
        }

        println("In TypeChecker, AssignmentNode's left type: " + leftType) ;

        Type rightType = null ; 

        if(n.right instanceof IdentifierNode)
            ((IdentifierNode)n.right).accept(this) ;
        else if (n.right instanceof NumNode){
            ((NumNode)n.right).accept(this) ;
            rightType = Type.Int ; 
        }
        else if (n.right instanceof RealNode)
            ((RealNode)n.right).accept(this) ;
        else if (n.right instanceof ArrayAccessNode)
        {
            
            ((ArrayAccessNode)n.right).accept(this) ;
        }
        else if (n.right instanceof ParenthesesNode)
            ((ParenthesesNode)n.right).accept(this) ;
        else { //BinExpr ???
            ((BinExprNode)n.right).accept(this) ;
            
            rightType = ((BinExprNode)n.right).type ;
        }

        if (leftType == Type.Int)
            println("********** leftType is Type.Int ") ; 

        if(leftType==Type.Float&&rightType==Type.Int)
        {
            println("The right type is of type int, but it can be converted to type float");
        }
        else if(leftType==Type.Int&&rightType==Type.Float)
        {
            println("The right type is of type float, this conversion may result in loss of accuracy");
        }

    }

    public void visit (BinExprNode n) {

        System.out.println("BinExprNode: " + n.op) ;

        Type leftType = null ; 
        IdentifierNode leftId = null ; 

        if (n.left instanceof IdentifierNode){
            ((IdentifierNode)n.left).accept(this) ;

            leftId = (IdentifierNode)n.left ; 
            leftType = leftId.type ; 
        }
        else if (n.left instanceof NumNode)
            ((NumNode)n.left).accept(this) ;
        else if (n.left instanceof RealNode)
            ((RealNode)n.left).accept(this) ;
        else if (n.left instanceof ArrayAccessNode)
            ((ArrayAccessNode)n.left).accept(this) ;
        else if (n.left instanceof ParenthesesNode)
            ((ParenthesesNode)n.left).accept(this) ;
        else
            ((BinExprNode)n.left).accept(this) ;

        Type rightType = null ; 
        
        if (n.right != null) {

            if (n.right instanceof IdentifierNode){
                ((IdentifierNode)n.right).accept(this) ;

                IdentifierNode rightId = (IdentifierNode)n.right ;
                rightType = rightId.type ; 
            }
            else if (n.right instanceof NumNode)
                ((NumNode)n.right).accept(this) ;
            else if (n.right instanceof RealNode)
                ((RealNode)n.right).accept(this) ;
            else if (n.right instanceof ArrayAccessNode)
                ((ArrayAccessNode)n.right).accept(this) ;
            else if (n.right instanceof ParenthesesNode)
                ((ParenthesesNode)n.right).accept(this) ;
            else{
                ((BinExprNode)n.right).accept(this) ;
            }
            
        } else {
            //System.out.println("@@@ n.right == null in BinExprNode: " + n.right) ;
        }

        int holder=parser.getPrecedence(n.op.tag);
        
        if(holder!=9&&holder!=8)
        {
            if(leftType!=Type.Bool&&rightType!=Type.Bool&&
                leftType!=Type.Char&&rightType!=Type.Char)
            {
                if(leftType==Type.Float||rightType==Type.Float)
                {
                    n.type=Type.Float;
                }
                else
                {
                    n.type=Type.Int;
                }
            }
            else
            {
                error("Please check your types in your assignment opperation");
            }
        }
        else if(holder==9||holder==8)
        {
            n.type=Type.Bool;
        }
    }

    public void visit (IdentifierNode n) {

        n.printNode();
    }

    public void visit (NumNode n) {

        n.printNode();
    }

    public void visit (RealNode n) {

        n.printNode();
    }
}