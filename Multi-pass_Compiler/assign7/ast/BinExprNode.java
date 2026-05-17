package assign7.ast ;

import assign7.inter.*;
import assign7.visitor.* ;
import assign7.lexer.* ;
// binexprnode examples:
// a = 1 ;          
// b = c ;          
// d = e + f ;      
// x = y - 2 ;

public class BinExprNode extends ExprNode 
{

    public Node   left ;  
    public Node   right ; 
    public Token  op ;  
    public IdentifierNode temp;

   
    public BinExprNode () {

    }
   
    public BinExprNode (Node left, BinExprNode right) {

        this.left   = left ;
        this.right  = right ;
    }
    
    public BinExprNode (Token op, Node left, Node right) {

        this.op     = op ;
        this.left   = left ;
        this.right  = right ;
    }
   
    public void accept(ASTVisitor v) {

        v.visit(this) ;
    }
}