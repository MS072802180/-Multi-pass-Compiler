# -Multi-pass-Compiler
A five-stage compiler front-end for a simple imperative language. Reads source code from input.txt and emits three-address intermediate code.

OVERVIEW
--------
A five-stage compiler front-end for a simple imperative language.
Reads source code from input.txt and emits three-address intermediate code.

  STAGE 1  Lexer        Scans raw characters and produces a token stream.
  STAGE 2  Parser       Consumes tokens, builds an AST, and decomposes
                        compound expressions into temporary variables.
  STAGE 3  TypeChecker  Walks the AST and verifies that operand types are
                        consistent throughout.
  STAGE 4  InterCode    Labels every loop and branch node with jump targets
                        (L1, L2 ...) and wraps conditions in temporaries
                        (t1, t2 ...).
  STAGE 5  Unparser     Traverses the decorated AST and prints the final
                        three-address intermediate code.


REQUIREMENTS
------------
  Java 8 or later.  No external libraries required.


HOW TO COMPILE AND RUN
-----------------------
  1. Unzip Multi-pass_Compiler.zip so the layout is:
       <root>/assign7/Main.java
       <root>/assign7/lexer/
       <root>/assign7/parser/
       <root>/assign7/ast/
       <root>/assign7/typechecker/
       <root>/assign7/inter/
       <root>/assign7/unparser/
       <root>/assign7/visitor/

  2. From <root>, compile all sources:
       find assign7 -name "*.java" | xargs javac -d out

  3. Place input.txt in <root> (already included in the zip).

  4. Run:
       java -cp out assign7.Main


  HOW EACH LINE OF input.txt IS PROCESSED


Below is every logical line of the source file followed by a one-line
description of its path through the five stages.

--------------------------------------------------------------------------------
  {
--------------------------------------------------------------------------------
  Lexer reads '{' as a block-open token  ->  Parser creates a BlockStatementNode
  and pushes a new symbol-table scope  ->  TypeChecker enters that scope  ->
  InterCode enters the block  ->  Unparser begins iterating the block's
  statement list.

--------------------------------------------------------------------------------
  int i ;   |   int j ;   |   float v ;   |   float x ;
--------------------------------------------------------------------------------
  Lexer produces a type keyword + identifier + semicolon  ->  Parser builds a
  DeclarationNode and stores the identifier with its type in the symbol table  ->
  TypeChecker confirms the type is legal  ->  InterCode records the declaration  ->
  Unparser emits nothing (declarations are not part of three-address output).

--------------------------------------------------------------------------------
  float[100] a ;
--------------------------------------------------------------------------------
  Lexer reads "float", '[', "100", ']', "a", ';'  ->  Parser builds a
  DeclarationNode with an ArrayTypeNode (element type float, size 100) and
  registers 'a' in the symbol table  ->  TypeChecker validates the array type  ->
  InterCode records it  ->  Unparser skips it (no output for declarations).

--------------------------------------------------------------------------------
  while(false)
--------------------------------------------------------------------------------
  Lexer emits WHILE + '(' + FALSE + ')'  ->  Parser creates a
  WhileStatementNode and wraps the condition in a ParenthesesNode containing
  a FalseNode  ->  TypeChecker verifies the condition is boolean  ->  InterCode
  assigns the loop's entry label (L1), wraps "false" in a temp (t4 = false),
  and assigns the exit label (L7)  ->  Unparser emits:
    L1:
      t4 = false ;
      ifFalse t4 goto L7
      ...body...
      goto L1
    L7:

--------------------------------------------------------------------------------
  {                                      (while body block)
--------------------------------------------------------------------------------
  Same block-open processing as the outer '{': Lexer -> Parser pushes a new
  nested BlockStatementNode and scope -> TypeChecker enters nested scope ->
  InterCode enters block -> Unparser iterates inner statement list.

--------------------------------------------------------------------------------
  do
      i = i+1 ;
  while( v < a[i]) ;
--------------------------------------------------------------------------------
  Lexer emits DO ... WHILE sequence  ->  Parser builds a DoWhileStatementNode
  whose body is "i = i+1" (an AssignmentNode with a BinExprNode on the right)
  and whose condition "v < a[i]" causes parseBinExprNode to extract a[i] into
  a temp (t1 = a[i]) stored in the node's condStmts list  ->  TypeChecker
  checks that i is int and v is float-compatible  ->  InterCode assigns the
  loop label (L2), wraps "v < t1" in a temp (t5), and assigns the exit label
  (L3)  ->  Unparser emits:
    L2:
      i = i + 1 ;
      t1 = a[i] ;
      t5 = v < t1 ;
      ifTrue t5 goto L2
    L3:

--------------------------------------------------------------------------------
  do
      i = i+2 ;
  while(a[i] > v) ;
--------------------------------------------------------------------------------
  Same pipeline as the first do-while: Lexer -> Parser builds a second
  DoWhileStatementNode; the condition "a[i] > v" extracts a[i] into condStmts
  (t2 = a[i])  ->  TypeChecker checks types  ->  InterCode assigns labels L4
  and L5 and wraps "t2 > v" in temp t6  ->  Unparser emits:
    L4:
      i = i + 2 ;
      t2 = a[i] ;
      t6 = t2 > v ;
      ifTrue t6 goto L4
    L5:

--------------------------------------------------------------------------------
  if( i >= j )
      break ;
--------------------------------------------------------------------------------
  Lexer emits IF + '(' + ID + ">=" + ID + ')' + BREAK  ->  Parser builds an
  IfStatementNode containing a BinExprNode ("i >= j") and a
  BreakStatementNode  ->  TypeChecker confirms both operands are int  ->
  InterCode wraps "i >= j" in temp t7 and assigns the false-exit label L6  ->
  Unparser resolves 'break' to the enclosing while's exit label (L7) and emits:
    t7 = i >= j ;
    ifFalse t7 goto L6
      goto L7
    L6:

--------------------------------------------------------------------------------
  x = a[i] ;
--------------------------------------------------------------------------------
  Lexer produces ID + '=' + ID + '[' + ID + ']' + ';'  ->  Parser builds an
  AssignmentNode with IdentifierNode "x" on the left and an ArrayAccessNode
  "a[i]" on the right  ->  TypeChecker confirms x is float and a[i] yields
  float  ->  InterCode traverses the node  ->  Unparser emits:
    x = a[i] ;

--------------------------------------------------------------------------------
  a[i] = a[j] + 4 ;
--------------------------------------------------------------------------------
  Lexer reads an array-access assignment  ->  Parser keeps the ArrayAccessNode
  "a[i]" as the LHS; on the right, parseBinExprNode detects that "a[j]" is an
  array access and extracts it into a temp (t3 = a[j]), leaving the right side
  as BinExprNode(t3 + 4)  ->  TypeChecker checks float compatibility  ->
  InterCode traverses  ->  Unparser emits:
    t3 = a[j] ;
    a[i] = t3 + 4 ;

--------------------------------------------------------------------------------
  a[j] = x ;
--------------------------------------------------------------------------------
  Lexer reads array-access assignment  ->  Parser keeps ArrayAccessNode "a[j]"
  as the LHS and IdentifierNode "x" as the RHS (no temporaries needed)  ->
  TypeChecker confirms types match  ->  InterCode traverses  ->  Unparser emits:
    a[j] = x ;

--------------------------------------------------------------------------------
  }    (closes while body)
  }    (closes outer block)
--------------------------------------------------------------------------------
  Lexer reads '}'  ->  Parser closes the BlockStatementNode and pops the symbol-
  table scope  ->  TypeChecker exits the scope  ->  InterCode exits the block  ->
  Unparser finishes iterating the statement list; the while loop's "goto L1" and
  final exit label "L7:" are emitted as part of the WhileStatementNode visit.

================================================================================
  FULL OUTPUT FOR input.txt
================================================================================

  L1:
    t4 = false ;
    ifFalse t4 goto L7
  L2:
      i = i + 1 ;
    t1 = a[i] ;
    t5 = v < t1 ;
    ifTrue t5 goto L2
  L3:
  L4:
      i = i + 2 ;
    t2 = a[i] ;
    t6 = t2 > v ;
    ifTrue t6 goto L4
  L5:
    t7 = i >= j ;
    ifFalse t7 goto L6
      goto L7
  L6:
    x = a[i] ;
    t3 = a[j] ;
    a[i] = t3 + 4 ;
    a[j] = x ;
    goto L1
  L7:

================================================================================
