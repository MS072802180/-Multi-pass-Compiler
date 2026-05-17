package assign7.parser ;

import assign7.visitor.* ;
import assign7.lexer.* ;
import assign7.ast.*;

import java.io.* ;
import assign7.inter.*;

public class Parser extends ASTVisitor {
    // instance variables = java fields
    public CompilationUnit cu   = null ;
    public Lexer lexer          = null ;

    public Token look = null ;
    public Env top = null ; 
    boolean override=false;

    public BlockStatementNode enclosingBlock = null ; 

    int     level = 0 ;
    String indent = "..." ;

    public Parser (Lexer lexer) {

        this.lexer = lexer ;
        cu = new CompilationUnit() ;

        move() ;    
        visit(cu) ;
    }

    public Parser () {

        cu = new CompilationUnit() ;

        move() ;   
        visit(cu) ;
    }


    void move () {     

        try {

            look = lexer.scan() ;   
        }                       
        catch(IOException e) {

            System.out.println("IOException") ;
        }
    }

    void error (String s) {

	    //throw new Error ("near line " + lexer.line + ": " + s) ;
        println("Line " + lexer.line + ": " + s);
        exit(1) ;
    }

    void match (int t) {

        try {
            
            if (look.tag == t)
                move() ;
            else if (look.tag == Tag.EOF)
                error("Syntax error: \";\" or \"}\" expected");
            else
                error("Syntax error: \"" + (char)t + "\" expected");
            }
            catch(Error e) {
            
            }	
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

   
    private boolean opt(int... tags){

        for(int tag : tags)
            if (look.tag == tag)
                return true;
        return false; 
    }

    
    public void visit (CompilationUnit n) {

        System.out.println("CompilationUnit") ; 

        n.block = new BlockStatementNode(null) ;
        level ++ ;
        n.block.accept(this) ; 
        level -- ;
    }

    public void visit (BlockStatementNode n) {
       
        for (int i=0 ; i<level ; i++) System.out.print(indent) ;
        System.out.println("BlockStatementNode") ; 

        match('{') ; 

        
        n.sTable = top; 
        top = new Env(top) ; 
        enclosingBlock = n ; 

        // n.decls = new Declarations();
        // level ++;
        // n.decls.accept(this);   
        // level --; 

        override=true;
        level++ ;
        while(opt(Tag.BASIC)){  
            DeclarationNode decl = new DeclarationNode();
            n.decls.add(decl) ;
            decl.accept(this) ; 
        }
        level-- ;
        override=false;

        // n.stmts = new Statements() ;
        // n.stmts.accept(this) ;  

        level++ ; 
        while(opt(Tag.ID, Tag.IF, Tag.WHILE, Tag.DO, Tag.BREAK)){

            n.stmts.add(parseStatementNode()) ;
        }
        level-- ; 
        
        match('}') ;

         
        top = n.sTable; 
        enclosingBlock = n.parent; 
    }

    // public void visit (Declarations n){

    //     for (int i=0; i<level; i++) System.out.print(indent);
    //     System.out.println("Declarations") ;
 
    //     if (look.tag == Tag.BASIC){ 
    //     // declaration or just binaryexpression
    //     // if it is an integer or a float, go down
    //         n.decl = new DeclarationNode() ;    
    //         level ++ ;
    //         n.decl.accept(this) ;   
    //         level -- ; 

    //         n.decls = new Declarations() ;  
    //         n.decls.accept(this) ;          
    //     }
    // }

    // decl --> type id ; 
    public void visit (DeclarationNode n){
        
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("DeclarationNode");
 
        n.type = new TypeNode() ;
        level ++ ;
        n.type.accept(this) ;
        level -- ; 
        
        n.id = new IdentifierNode() ; 
        n.id.type = n.type.basic ; 
        
        level ++ ;
        n.id.accept(this) ;
        level -- ;

        
        top.put(n.id.w, n.id) ;

        
        IdentifierNode tmp = (IdentifierNode)top.get(n.id.w) ;
        println("&&&&&& tmp.type: " + tmp.type) ;
        println("&&&&&& tmp.w: " + tmp.w) ;
        // println("&&&&& tmp.w: " + tmp.w.getObject()) ;

        match(';') ;
    }

    // int i ; || int[2] j ; 
    public void visit (TypeNode n){

        for (int i=0; i<level; i++) System.out.print(indent) ;
        System.out.println("TypeNode: " + look) ;

        //System.out.println("****** look:  " + look);
        // for debugging purpose
        if (look.toString().equals("int"))
            n.basic = Type.Int ;
        else if (look.toString().equals("float"))
            n.basic = Type.Float ; 

        match(Tag.BASIC) ;

        //if look is "[", this type should be array type
        if (look.tag == '[') {
            n.array = new ArrayTypeNode() ;
            level ++ ;
            n.array.accept(this) ;
            level -- ; 
        }
    }

     public void visit (ArrayTypeNode n){
        
        for (int i=0; i<level; i++) System.out.print(indent) ;
        System.out.println("ArrayTypeNode: ") ;

        match('[') ;
        
        n.size = ((Num)look).value ;

        for (int i=0; i<level; i++) System.out.print(indent) ;
        //System.out.println("ArrayDimension: " + look.toString()) ;
       
        System.out.println("ArrayDimension: " + ((Num)look).value) ;
      
        match(Tag.NUM) ;

        match(']') ;

        if (look.toString().equals("[")){
            n.type = new ArrayTypeNode() ;
            level ++ ;
            n.type.accept(this) ;
            level -- ; 
        }
    }


    // public void visit (Statements n) {

    //     // if (look.tag != '}') {  //if this '}' is missing there is going to be problem
    //     if (look.tag != '}' && look.tag != Tag.EOF) {

    //         level ++;
    //         n.stmt = parseStatementNode(n.stmt);
    //         level --;

    //         n.stmts = new Statements();
    //         level ++;
    //         n.stmts.accept(this);
    //         level --;
    //     }
    // }

    public StatementNode parseStatementNode () {

        for (int i=0; i <level; i++ ) System.out.print(indent);
       // System.out.println("**** parseStatementNode");

        StatementNode stmt ;

      
        switch (look.tag) { 

            case Tag.ID:
                stmt = new AssignmentNode() ;
                ((AssignmentNode)stmt).accept(this) ;
                return stmt ;

            case Tag.IF: 

                stmt = new IfStatementNode() ;
                ((IfStatementNode)stmt).accept(this) ;
                return stmt ;

            case Tag.WHILE:
                stmt = new WhileStatementNode() ;
                ((WhileStatementNode)stmt).accept(this) ;
                return stmt ;

            case Tag.DO:
                stmt = new DoWhileStatementNode() ;
                ((DoWhileStatementNode)stmt).accept(this) ;
                return stmt ;

            case Tag.BREAK:
                stmt = new BreakStatementNode() ;
                ((BreakStatementNode)stmt).accept(this) ;
                return stmt ;
                
            default: 
                error("Syntax error: Statement needed") ;
                return null;        
        }
    }

    public void visit(ParenthesesNode n) {
        // ( ExprNode ) (a[(i+j)]) ((i+j))
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("ParenthesesNode");

        match ('(');

        if (look.tag == '(') {
            
            n.expr = new ParenthesesNode();
            level ++;
            n.expr.accept(this);
            level --;

        } else if (look.tag == Tag.ID ) { // (i+j), (a[i])

            n.expr = new IdentifierNode();
            level ++;
            n.expr.accept(this);
            level --;

            if (look.tag == '[') {
                n.expr = parseArrayAccessNode((IdentifierNode)n.expr);
            }

        } else if (look.tag == Tag.NUM ) { 
            n.expr = new NumNode();
            level ++;
            n.expr.accept(this);
            level --;

        } else if (look.tag == Tag.REAL ) {
            n.expr = new RealNode();
            level ++;
            n.expr.accept(this);
            level --;

        } else if (look.tag == Tag.TRUE ) {
            n.expr = new TrueNode();
            level ++;
            n.expr.accept(this);
            level --;

        } else if (look.tag == Tag.FALSE ) {
            n.expr = new FalseNode();
            level ++;
            n.expr.accept(this);
            level --;
        } 

        if (look.tag != ')') {  // ( i+j )
            level ++ ;
            n.expr = parseBinExprNode(n.expr, 0);
            level --;
            
        }

        match(')');
    }


    public void visit(IfStatementNode n) {

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("IfStatementNode");

        ///////////////////////////////////////////////////////////////////
        // Add an AssignmentNode, i = 219; , before IfStatementNode
        ///////////////////////////////////////////////////////////////////

        // IdentifierNode leftId = new IdentifierNode(new Word("i",Tag.ID), Type.Int);
        // AssignmentNode newAssign1 = new AssignmentNode(leftId, new NumNode(new Num (2)));
        // AssignmentNode newAssign2 = new AssignmentNode(leftId, new NumNode(new Num(19)));
        // AssignmentNode newAssign3 = new AssignmentNode(leftId, new NumNode(new Num(219)));

        // enclosingBlock.stmts.add(newAssign1); 
        // enclosingBlock.stmts.add(newAssign2);
        // enclosingBlock.stmts.add(newAssign3);

        // AssignmentNode newAssign4 = new AssignmentNode(leftId, new NumNode(new Num(518)));

        // int idx = enclosingBlock.stmts.indexOf(newAssign2);
        // enclosingBlock.stmts.add(idx, newAssign4);

        // for (StatementNode s : enclosingBlock.stmts)    // print each statement from the enclosing blocks statements
        //     System.out.println(s) ;
        
        // // This IfStatementNode will be added into enclosingBlock.stmts after this visit (IfStatementNode n) is done.
        // if (enclosingBlock.stmts.contains(n))
        //     System.out.println("******** enclosingBlock has this IfStatementNode");
        // else
        //     System.out.println("######## enclosingBlock doesn't have this IfStatementNode");

        match(Tag.IF); 
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("operator: if");

        
        n.cond = new ParenthesesNode(); 
        level ++;
        n.cond.accept(this);    
        level --;               

        if (look.tag == '{') {  
            n.stmt = new BlockStatementNode(null);  
            level ++;
            n.stmt.accept(this);    
            level --;
        } else {    
            n.stmt = parseStatementNode();    
        }

        if (look.tag == Tag.ELSE) { 
            match(Tag.ELSE);
            
            for (int i=0; i<level; i++) System.out.print(indent);
            System.out.println("operator: else");

            if (look.tag == '{') {  
                n.else_stmt = new BlockStatementNode(null);

                level ++;
                n.else_stmt.accept(this);
                level --;

            } else {
                n.else_stmt = parseStatementNode();
            }
        }
    }


    public void visit (WhileStatementNode n) {
        
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("WhileStatementNode");

        match(Tag.WHILE);
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("operator: while");

        n.cond = new ParenthesesNode();
        level ++;
        n.cond.accept(this);
        level --;

        if (look.tag == '{') {
            n.stmt = new BlockStatementNode(null);
            level ++;
            n.stmt.accept(this);
            level --;

        } else {
            n.stmt = parseStatementNode();
        }
    }


    public void visit (DoWhileStatementNode n) {

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("DoWhileStatementNode");

        match(Tag.DO);
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("operator: do");

        if (look.tag == '{') {
            n.stmt = new BlockStatementNode(null);
            level ++;
            n.stmt.accept(this);
            level --;

        } else {
            n.stmt = parseStatementNode();
        }

        match(Tag.WHILE);
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("operator: while");

        // BUG 5 FIX: record stmt count before parsing the condition.
        // parseBinExprNode() may inject array-access temps (e.g. t1 = a[i])
        // directly into enclosingBlock.stmts.  Without this, those temps end up
        // BEFORE the do-while in the block's list and are evaluated only once.
        // We extract them here and store in n.condStmts so the Unparser can emit
        // them AFTER the body, just before the ifTrue check.
        int sizeBeforeCond = enclosingBlock.stmts.size();

        n.cond = new ParenthesesNode();
        level ++;
        n.cond.accept(this);
        level --;

        while (enclosingBlock.stmts.size() > sizeBeforeCond) {
            n.condStmts.add(enclosingBlock.stmts.remove(sizeBeforeCond));
        }

        match(';');

    }


    public void visit (ArrayAccessNode n) 
    {

    }

    public void visit (ArrayDimsNode n) { // a[(i+j)][j][k]

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("ArrayDimsNode");

        match('[');

        ExprNode index = null; 

        if (look.tag == '(') {
            index = new ParenthesesNode();
            level ++;
            ((ParenthesesNode)index).accept(this);
            level --;
        } else if (look.tag == Tag.ID) {
            index = new IdentifierNode();
            level ++;
            ((IdentifierNode)index).accept(this);
            level --;

        } 
        else if (look.tag == Tag.NUM) {
            index = new NumNode();
            level ++;
            ((NumNode)index).accept(this);
            level --;
        }
        
        if (look.tag != ']') {
            
            level ++;
            index = parseBinExprNode(index, 0);
            level --;
        }

        match(']');
        if(index instanceof ParenthesesNode)
        {
            IdentifierNode temp=TempNode.newTemp();
            AssignmentNode newAssign1 = new AssignmentNode(temp, index);
            enclosingBlock.stmts.add(newAssign1); 
            index=temp;
        }
        else if(index instanceof BinExprNode)
        {
            IdentifierNode temp=TempNode.newTemp();
            AssignmentNode newAssign1 = new AssignmentNode(temp, index);
            enclosingBlock.stmts.add(newAssign1); 
            index=temp;
        }

        n.size = index;

        if (look.tag == '[') {
            n.dim = new ArrayDimsNode();
            level ++;
            n.dim.accept(this);
            level --;
        }
    }

    ExprNode parseArrayAccessNode (IdentifierNode id) {
        
        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("parseArrayAccessNode");
        ArrayDimsNode index = new ArrayDimsNode();
        
        level ++; 
        ((ArrayDimsNode)index).accept(this);
        level --;
        
        return new ArrayAccessNode(id,index);
    }


    public void visit(AssignmentNode n){ // a[i] = i + j ;

        for (int i=0; i<level; i++) System.out.print(indent) ;
        System.out.println("AssignmentNode") ;

        n.left = new IdentifierNode() ;
        
        level ++;
        n.left.accept(this) ;
        level --;
        
        IdentifierNode id = (IdentifierNode)top.get(((IdentifierNode)n.left).w) ;
        println("In parser, AssignmentNode's left type: " + id.type) ;

        ((IdentifierNode)n.left).type = id.type;
        
        
                
        if (look.tag == '[') {
            n.left = parseArrayAccessNode((IdentifierNode)n.left) ;
        }
        // BUG 3 FIX: the block below was replacing the LHS ArrayAccessNode with a
        // read-temp (temp = a[i]) and then assigning to that temp instead of back
        // to a[i].  This silently dropped every array write.  The ArrayAccessNode
        // must stay as the LHS so the Unparser emits the correct store instruction.
        // (The corresponding ClassCastException risk in Unparser.visit(AssignmentNode)
        //  is fixed by Bug 4b below.)
        
        
        
        
        
        match('=');
        for (int i=0; i<level; i++) System.out.print(indent) ;
        System.out.println("operator: =");
        
        ExprNode rhs_assign = null;


        if(look.tag == '(') {
            rhs_assign = new ParenthesesNode(); 
            level ++;
            ((ParenthesesNode)rhs_assign).accept(this);
            level -- ; 
        } else if (look.tag == Tag.ID) { // a = i[k] + j ;

            rhs_assign = new IdentifierNode() ;
            level ++ ;
            ((IdentifierNode)rhs_assign).accept(this) ;
            level -- ; 

            if(look.tag == '[') {
                rhs_assign = parseArrayAccessNode(((IdentifierNode)rhs_assign)) ;
            }
        
        } else if (look.tag == Tag.NUM) {

            rhs_assign = new NumNode() ;
            level ++ ;
            ((NumNode)rhs_assign).accept(this) ;
            level -- ;  
        } else if (look.tag == Tag.REAL) {

            rhs_assign = new RealNode() ;
            level ++;
            ((RealNode)rhs_assign).accept(this);
            level -- ;  
        }
        if (look.tag == ';') 
        { 
            n.right = rhs_assign ;
        } 
        else 
        { 
            if(look.tag == '(') {
                rhs_assign = new ParenthesesNode(); 
                level ++;
                ((ParenthesesNode)rhs_assign).accept(this);
                level -- ; 
            } 
            else if (look.tag == Tag.ID) 
            { // a = i[k] + j ;

                rhs_assign = new IdentifierNode() ;
                level ++ ;
                ((IdentifierNode)rhs_assign).accept(this) ;
                level -- ; 

                if(look.tag == '[') {
                    rhs_assign = parseArrayAccessNode(((IdentifierNode)rhs_assign)) ;
                }
        
            } else if (look.tag == Tag.NUM) {

                rhs_assign = new NumNode() ;
                level ++ ;
                ((NumNode)rhs_assign).accept(this) ;
                level -- ;  
            } else if (look.tag == Tag.REAL) {

                rhs_assign = new RealNode() ;
                level ++;
                ((RealNode)rhs_assign).accept(this);
                level -- ;  
            }
            if (look.tag == ';') 
            { 
                n.right = rhs_assign ;
            } 
            else 
            { 
                
                for (int i=0; i<level; i++) System.out.print(indent) ;
                System.out.println("operator: " + look) ;
                level ++;
                n.right = (BinExprNode) parseBinExprNode( rhs_assign, 0);
                level --;
                
            }
            
            
            
            
            //System.out.println("**** Root Node operator: " + ((BinExprNode)n.right).op);
        }

        match(';');
    }
    

    public void visit(BreakStatementNode n){

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("BreakStatementNode: break");

        if (look.tag != Tag.BREAK)
            error("Syntax error: \"break\" needed") ;

        match(Tag.BREAK);
        match(';');

    }

    public void visit(TrueNode n){

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("TrueNode");

        if (look.tag != Tag.TRUE)
            error("Syntax error: \"true\" needed") ;

        match(Tag.TRUE);
    }

    public void visit(FalseNode n){

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("FalseNode");

        if (look.tag != Tag.FALSE)
            error("Syntax error: \"false\" needed") ;        

        match(Tag.FALSE) ;
    }

    public void visit(BinExprNode n){ // (i[k] + j);

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("BinExprNode");

        if (look.tag == '(') {
            
            n.left = new ParenthesesNode();
            level ++;
            ((ParenthesesNode)n.left).accept(this);
            level --;

        } else if (look.tag == Tag.ID ) { 

            n.left = new IdentifierNode();
            level ++;
            ((IdentifierNode)n.left).accept(this); 
            level --;

            if (look.tag == '[') {
                n.left = parseArrayAccessNode((IdentifierNode)n.left);
            }

        } else if (look.tag == Tag.NUM ) {

            n.left = new NumNode();
            level ++;
            ((NumNode)n.left).accept(this);
            level --;

        } else if (look.tag == Tag.REAL ) {

            n.left = new RealNode();
            level ++;
            ((RealNode)n.left).accept(this);
            level --;
        }

        for (int i=0; i<level; i++) System.out.print(indent);
        System.out.println("&&&&&& operator: " + look);
        System.out.println("&&&&&& n.left: " + n.left);

        level ++; 
        
        BinExprNode binary = (BinExprNode) parseBinExprNode((ExprNode)n.left, 0);
        n.op    = binary.op;
        n.right = binary.right;
        level --;
    }


    /*
        < Operator Precedence >
        Operators are listed top to bottom in ascending precedence

        01. assignment =, +=, -=, *=, /=, %=, &=, ^=, \=, <<=, >>=, >>>=
        02. tenary ? :
        03. logical OR ||
        04. logical AND &&
        05. bitwise inclusive OR |
        06. bitwise exclusive OR ^
        07. bitwise AND &
        08. equality ==, !=
        09. relational <, >, <=, >=
        10. shift <<, >>, >>>
        11. additive +, -
        12. multiplicative *, /, %
        13. unary ++expr, --expr, +expr, -expr
        14. postfix expr++, expr--
        */
    public int getPrecedence (int op) {

        switch ( op ) {
            case '*': case '/': case '%':   return 12 ;     // multiplicative
            case '+': case '-':             return 11 ;     // addititive
            case '<': case '>':             return 9 ;      // relational
            case Tag.LE: case Tag.GE:       return 9 ;      // relational
            case Tag.EQ: case Tag.NE:       return 8 ;      // equality

            default:
                return -1 ;     // ';'
        }
    }
    

    ExprNode parseBinExprNode (ExprNode lhs, int precedence) {
        
        while ( getPrecedence(look.tag) >= precedence ) {
            
            Token token_op = look ;
            int op = getPrecedence(look.tag) ;

            move() ;

            for (int i=0 ; i<level ; i++) System.out.print(indent) ;


            ExprNode rhs = null ;

            if (look.tag == '(') {
                rhs = new ParenthesesNode();
                level ++; 
                ((ParenthesesNode)rhs).accept(this);
                level --;

            } else if (look.tag == Tag.ID) {
                
                rhs = new IdentifierNode() ;
                level ++ ;
                ((IdentifierNode)rhs).accept(this) ;
                level -- ;

                if (look.tag == '[') {
                    rhs = parseArrayAccessNode(((IdentifierNode)rhs));
                }

            } else if (look.tag == Tag.NUM) {

                rhs = new NumNode() ;
                level ++ ;
                ((NumNode)rhs).accept(this) ;
                level -- ;

            } else if (look.tag == Tag.REAL) {

                rhs = new RealNode() ;
                level ++ ;
                ((RealNode)rhs).accept(this);
                level -- ;
            }

            // System.out.println("op = " + op) ;
            // System.out.println("token_op = " + token_op) ;
            // System.out.println("next_op = " + getPrecedence(look.tag)) ;

            // Whenever the next op's precedence is higher than that
            // of the current operator, keep recursively calling itself.
            
            
            
            while(getPrecedence(look.tag)>op)
            {
                
                rhs=parseBinExprNode(rhs,getPrecedence(look.tag));
                
            }
            
            ///////////////////////////////New Code Block
            if(lhs instanceof BinExprNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1 = new AssignmentNode(temp, lhs);
                enclosingBlock.stmts.add(newAssign1); 
                if(rhs instanceof BinExprNode)
                {
                    temp=TempNode.newTemp();
                    AssignmentNode newAssign2=new AssignmentNode(temp,rhs);
                    enclosingBlock.stmts.add(newAssign2);
                    lhs=new BinExprNode(token_op,newAssign1.left,newAssign2.left);
                }
                else
                {
                    lhs=new BinExprNode(token_op,newAssign1.left,rhs);
                }
                 
            }
            if(lhs instanceof ArrayAccessNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1 = new AssignmentNode(temp, lhs);
                enclosingBlock.stmts.add(newAssign1); 
                lhs=new BinExprNode(token_op,newAssign1.left,rhs);
            }
            else if(rhs instanceof BinExprNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1 = new AssignmentNode(temp, rhs);
                enclosingBlock.stmts.add(newAssign1); 
                if(lhs instanceof BinExprNode)
                {
                    temp=TempNode.newTemp();
                    AssignmentNode newAssign2=new AssignmentNode(temp,lhs);
                    enclosingBlock.stmts.add(newAssign2);
                    lhs=new BinExprNode(token_op,newAssign2.left,newAssign1.left);
                }
                else
                {
                    lhs=new BinExprNode(token_op,lhs,newAssign1.left);
                }
            }
            else if(rhs instanceof ParenthesesNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1=new AssignmentNode(temp,rhs);
                enclosingBlock.stmts.add(newAssign1);
                if(lhs instanceof BinExprNode|| lhs instanceof ParenthesesNode)
                {
                    temp=TempNode.newTemp();
                    AssignmentNode newAssign2=new AssignmentNode(temp,lhs);
                    enclosingBlock.stmts.add(newAssign2);
                    lhs=new BinExprNode(token_op,newAssign2.left,newAssign1.left);
                }
                else
                {
                    lhs=new BinExprNode(token_op,lhs,newAssign1.left);
                }
            }
            else if(lhs instanceof ParenthesesNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1 = new AssignmentNode(temp, lhs);
                enclosingBlock.stmts.add(newAssign1); 
                if(rhs instanceof BinExprNode||rhs instanceof ParenthesesNode)
                {
                    temp=TempNode.newTemp();
                    AssignmentNode newAssign2=new AssignmentNode(temp,rhs);
                    enclosingBlock.stmts.add(newAssign2);
                    lhs=new BinExprNode(token_op,newAssign1.left,newAssign2.left);
                }
                else
                {
                    lhs=new BinExprNode(token_op,newAssign1.left,rhs);
                }
            }
            else if(lhs instanceof ArrayAccessNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1 = new AssignmentNode(temp, lhs);
                enclosingBlock.stmts.add(newAssign1); 
                lhs=new BinExprNode(token_op,newAssign1.left,rhs);
            }
            else if(rhs instanceof ArrayAccessNode)
            {
                IdentifierNode temp=TempNode.newTemp();
                AssignmentNode newAssign1=new AssignmentNode(temp,rhs);
                enclosingBlock.stmts.add(newAssign1);
                lhs=new BinExprNode(token_op,lhs,newAssign1.left);
            }
            else
            {
                lhs = new BinExprNode(token_op, lhs, rhs) ;
            }
                
            
            
            //System.out.println("**** Created BinExprNode with op " + token_op) ;
        }
        
        return lhs ;
    }

    public void visit (NumNode n) {

        n.value = ((Num)look).value ;

        if(look.tag != Tag.NUM)
            error("Syntax error: Integer number needed, instead of " + n.value);

        match(Tag.NUM) ;        // expect look.tag == Tag.NUM

        for (int i=0 ; i<level ; i++) System.out.print(indent) ;
        n.printNode() ;
        //System.out.println("look in IdentifierNode: " + look) ;
    }

    public void visit (RealNode n) {

        n.value = ((Real)look).value ;

        if(look.tag != Tag.REAL)
            error("Syntax error: Identifier or variable needed, instead of " + n.value);

        match(Tag.REAL); //expect look.tag == Tag.REAL

        for (int i=0; i<level; i++) System.out.print(indent);
        n.printNode();
        //System.out.print("look in IdentifierNode: " + look);
    }
    public void lookUP(IdentifierNode id)
    {
        IdentifierNode tmp=new IdentifierNode();
        tmp=top.get(id.w);
        if(tmp==null)
        {
            error("This identifier \""+id.id+"\" does not exist");
        }
    }

    public void visit (IdentifierNode n) {

        n.id = look.toString() ;
        n.w = (Word)look ;
        if(override==false)
        {
            lookUP(n);
        }
        println("***** n.type: " + n.type);

        if(look.tag != Tag.ID)
            error("Syntax error: Identifier or variable needed, instead of " + n.id);

        match(Tag.ID) ;     
        
        for (int i=0 ; i<level ; i++) 
            System.out.print(indent) ;
        n.printNode() ;
        //System.out.println("IdentifierNode: " + n.id) ;
        //System.out.println("look in IdentifierNode: " + look) ;
    }
}