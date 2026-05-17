package assign7.ast ;

import assign7.inter.*;
import assign7.visitor.* ;
import java.util.*;
public class DoWhileStatementNode extends StatementNode 

{

    public StatementNode   stmt; 
    public ParenthesesNode cond; 
    public LabelNode trueLabel;
    public LabelNode falseLabel;
    public List<AssignmentNode> assigns = new ArrayList<AssignmentNode>();

    // BUG 5 FIX: temps generated while parsing the do-while condition (e.g. t1 = a[i])
    // were being injected into the enclosing block's stmts list BEFORE the do-while node
    // itself, so they were only evaluated once before the loop started instead of after
    // every iteration.  They are now extracted from the enclosing block during parsing
    // and stored here so the Unparser can emit them at the correct position (after the
    // body, just before the ifTrue check).
    public List<StatementNode> condStmts = new ArrayList<StatementNode>();
    
    public DoWhileStatementNode()
    {

    }
  
    public DoWhileStatementNode (ParenthesesNode cond, StatementNode stmt) {

        this.cond = cond;
        this.stmt = stmt;
    } 
    
    public void accept(ASTVisitor v){

        v.visit(this);
    }
    
}
