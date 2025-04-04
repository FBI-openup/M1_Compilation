
package mini_turtle;

import java_cup.runtime.*;
import java.util.*;
import static mini_turtle.sym.*;

%%

%class Lexer
%unicode
%cup
%cupdebug
%line
%column
%yylexthrow Exception

%{
    /* 用于构建一个词素，可以调用 "symbol" 方法 */
    /* Pour construire un lexème, on appelle l'une des deux méthodes "symbol"
    suivantes, en lui passant en argument un symbole de lexème introduit dans Parser
    (EOF, etc.) et éventuellement une valeur (entier, chaîne, etc.) */

    private Symbol symbol(int id)
    {
	return new Symbol(id, yyline, yycolumn);
    }

    private Symbol symbol(int id, Object value)
    {
	return new Symbol(id, yyline, yycolumn, value);
    }

%}
/* 定义空白符 */
/* Ici on peut déclarer des raccourcis pour des expressions régulières,
   par exemple */
WhiteSpace         = [ \t\f\r\n]



/* À COMPLÉTER (si besoin) */


/* 定义注释语法 */
/* 单行注释以 // 开头，直到行尾 */
SingleLineComment  = "//".* 

/* 多行注释以 /* 开始，以 * / 结束，支持跨行 */
//MultiLineComment   = "/*"([^*]|\*+[^*/])*"*/"

%%

/* Le point d'entrée de l'analyseur lexical. */

<YYINITIAL> {

    /* ici une liste d'expressions régulières, chacune suivie d'une action
    (du code Java) entre accolades. Cette action peut être vide (auquel cas
    l'analyseur est relancé) ou se terminer par return new Symbol(...); 
    pour renvoyer un lexème. */
    
    /* À COMPLÉTER */
    //!Q1
    /* 忽略空白符 */
    {WhiteSpace}+ 
        { /* 跳过空白符 */ }

    /* 忽略单行注释 */
    {SingleLineComment} 
        { /* 跳过单行注释 */ }

    /* 忽略多行注释 */
    //{MultiLineComment} 
    //    { /* 跳过多行注释 */ }

    //!Q2

    /* 处理非法字符 */
    .
    { 


        //return new Symbol(EOF, yyline, yycolumn);
        System.out.println("Illegal character:");
        throw new Exception (String.format (
        "Line %d, column %d: illegal character: '%s'\n", yyline, yycolumn, yytext()
      ));
    }

}
