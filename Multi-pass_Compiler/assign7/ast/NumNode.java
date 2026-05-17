package assign7.ast ;

import assign7.visitor.* ;
import assign7.lexer.* ;


public class NumNode extends ExprNode
{

    public int value; 
    public Num v; 
    public IdentifierNode temp;
    
    public NumNode() {

    }
    
    public NumNode (Num v) {

        this.value = v.value;
        this.v = v;
    }
    
    public void accept(ASTVisitor v){

        v.visit(this);
    }
    
    public void printNode() {
        System.out.println("NumNode: " + value);
    }
    
}