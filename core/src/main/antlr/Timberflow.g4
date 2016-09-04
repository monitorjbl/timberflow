grammar Timberflow;

@header {
package com.monitorjbl.timberflow.dsl;
}

compilationUnit
  : Newline* inputBlock? Newline* filterBlock? Newline* outputBlock? Newline* EOF
  ;

inputBlock
  : Inputs BlockOpen blockStatement* BlockClose
  ;

filterBlock
  : Filters BlockOpen blockStatement* BlockClose
  ;

outputBlock
  : Outputs BlockOpen blockStatement* BlockClose
  ;

blockStatement
  : plugin
  | branch
  ;

plugin
  : Identifier BlockOpen configuration* BlockClose
  ;

branch
  : If ParenOpen condition ParenClose BlockOpen plugin* BlockClose
  ;

condition
  : Identifier Comparison StringLiteral (BooleanOperator Identifier Comparison StringLiteral)*
  ;

configuration
  : Identifier Equals BooleanLiteral
  | Identifier Equals IntegerLiteral
  | Identifier Equals StringLiteral
  | Identifier ParenOpen map ParenClose
  ;

map
  : BlockOpen StringLiteral Colon StringLiteral (Comma StringLiteral Colon StringLiteral)* BlockClose
  ;

// Lexer Rules
Newline
  : ('\r'?'\n'|'\r') -> skip;
Whitespace
  : [ \t\u000C]+ -> skip;
Comment
  : '/*' .*? '*/' -> skip;
LineComment
  : '//' ~[\n]* -> skip;

//keywords
Inputs
  : 'inputs';
Filters
  : 'filters';
Outputs
  : 'outputs';
If
  : 'if';

// Language constants
BlockOpen
  : Newline* '{' Newline*;
BlockClose
  : Newline* '}' Newline*;
BraceOpen
  : Newline* '[' Newline*;
BraceClose
  : Newline* ']' Newline*;
ParenOpen
  : Newline* '(' Newline*;
ParenClose
  : Newline* ')' Newline*;
Comma
  : ',' Newline*;
Dot
  : '.' Newline*;
Comparison
  : '==' | '!=';
Equals
  : '=' Newline*;
Colon
  : ':' Newline*;
BooleanOperator
  : 'or' | 'and';
BooleanLiteral
    : 'true' | 'false';
Identifier
  : [a-zA-Z_][a-zA-Z_0-9]*;

StringLiteral
  : '"' ~('\r' | '\n' | '"')* '"'
  ;
IntegerLiteral
  : DecimalNumeral;

fragment PrintableChar
  : '\u0020' .. '\u007F' ;
fragment StringElement
  :  '\u0020'| '\u0021'|'\u0023' .. '\u007F'
  |  CharEscapeSeq;
fragment CharEscapeSeq
  : '\\' ('b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\');
fragment DecimalNumeral
  :  '0' | NonZeroDigit Digit*;
fragment Digit
  :  '0' | NonZeroDigit;
fragment NonZeroDigit
  :  '1' .. '9';