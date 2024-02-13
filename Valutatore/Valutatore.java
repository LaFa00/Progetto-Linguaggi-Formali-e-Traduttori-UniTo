import java.io.*; 
import java.util.*;

public class Valutatore {
    private Lexer3 lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer3 l, BufferedReader br) { 
	lex = l; 
	pbr = br;
	move(); 
    }
   
    void move() { 
	 look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) { 
	
    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	  if (look.tag == t) {
        if (look.tag != Tag.EOF) move();
    } else error("syntax error");
    }

    public void start() { 
	    int expr_val = 0;
        switch(look.tag) {
        case '(':
        case Tag.NUM:
            expr_val = expr();
            match(Tag.EOF);
            System.out.println(expr_val);
            break;
        default:
                error("Errore start");
                break;
        }
    }

    public int expr() { 
	    int term_val, exprp_val = 0;
        switch(look.tag) {
        case '(':
        case Tag.NUM:
            term_val = term();
	        exprp_val = exprp(term_val);
	        break;
           
        default:
            error("Errore in expr");
        }
        return exprp_val;
    }

   public int exprp(int exprp_i) {
	    int term_val, exprp_val = 0;
	    switch (look.tag) {
	   case '+':
            match('+');
            term_val = term();
            exprp_val = exprp(exprp_i + term_val);
            break;

        case '-':
            match('-');
            term_val = term();
            exprp_val = exprp(exprp_i - term_val);
            break;

        case ')':
        case Tag.EOF:
            exprp_val = exprp_i;
            break;

            default:
                error("Errore trovato nel metodo Exprp");
            }
        return exprp_val;
    }

    public int term() { 
	int fact_val, termp_val = 0;
    switch(look.tag) {
    case '(':
    case Tag.NUM:
        fact_val = fact();
        termp_val = termp(fact_val);
        break;
    default:
        error("Errore in term");

    }
    return termp_val;
    }
    
   public int termp(int termp_i) { 
	int fact_val, termp_val = 0;
    switch(look.tag) {
    case '*':
        match(Token.mult.tag);
        fact_val = fact();
        termp_val = termp(termp_i * fact_val);
        break;
    case '/':
        match(Token.div.tag);
        fact_val = fact();
        termp_val = termp(termp_i / fact_val);
        break;
    case '+':
    case '-':
    case ')':
    case Tag.EOF:
        termp_val = termp_i;
        break;
    default:
    	error("Errore in termp");
    }
    return termp_val;
    }
    
   public int fact() { 
	int fact_val = 0;
	int expr_val = 0;
    switch(look.tag) {
    case '(':
        match(Token.lpt.tag);
        expr_val = expr();
        match(Token.rpt.tag);
        fact_val = expr_val;
        break;
    case Tag.NUM:
        fact_val = (int) (((NumberTok) look).value);
        match(Tag.NUM);
        break;
    default:
        error("Errore in fact");

    }
    return fact_val;
    }

    public static void main(String[] args) {
        Lexer3 lex = new Lexer3();
        String path = "testvalutatore.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}