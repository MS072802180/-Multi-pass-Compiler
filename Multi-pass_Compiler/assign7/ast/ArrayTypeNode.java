package assign7.ast;

import assign7.visitor.*;

public class ArrayTypeNode extends TypeNode{
    
    public TypeNode type; 
    public int size = 1;  

    public ArrayTypeNode(){

    }

    public ArrayTypeNode(int size, TypeNode type){

        this.size = size;  
        this.type = type; 
    }

    public void accept(ASTVisitor v) {

        v.visit(this);
    }

}
