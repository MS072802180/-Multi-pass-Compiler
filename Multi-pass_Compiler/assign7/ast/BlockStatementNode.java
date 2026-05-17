package assign7.ast ;

import assign7.visitor.* ;
import assign7.parser.* ;
import java.util.* ;
import assign7.inter.*;


public class BlockStatementNode extends StatementNode{

    // public Declarations decls ;
    // public Statements   stmts ;
    public List<DeclarationNode> decls = new ArrayList<DeclarationNode>() ;
    public List<StatementNode> stmts = new ArrayList<StatementNode>() ;

    public BlockStatementNode parent ; 
    public Env sTable ; 
    public LabelNode label;

    
    public BlockStatementNode(BlockStatementNode parent) {
        
        this.decls = new ArrayList<DeclarationNode>() ; 
        this.stmts = new ArrayList<StatementNode>() ; 

        this.parent = parent ;  
    }
   
    public BlockStatementNode (List<DeclarationNode> decls, List<StatementNode> stmts, BlockStatementNode parent){
        
        this.decls = decls ; 
        this.stmts = stmts ; 
        this.parent = parent ; 
    }
    
    public void accept(ASTVisitor v) {

        v.visit(this) ;
    }
    
}

