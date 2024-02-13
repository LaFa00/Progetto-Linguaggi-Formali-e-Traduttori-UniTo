
import java.io.*;
import java.util.*;


// lexer con underscore che ignora i commmenti
public class Lexer3 {

    public static int line = 1;
    private char peek = ' ';

    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
            if (peek == '\n') {
                line++;
            }
            readch(br);
        }

        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
            	 peek = ' ';
            	readch(br);
            	if(peek == '*'){
                	int state = 0;
               		peek = ' ';
                while(state != 2){
                    switch(state){
                        case 0:
                        	readch(br);
                        	if(peek == '*') {
                            	state = 1; 
                            	peek = ' ';
                        	}
                       		else if(peek == -1){
                            	state = 2;
                        	}
                        	else {
                            	state = 0;
                            	peek = ' ';
                        	}
                        	break;
    
                        case 1:
                        	readch(br);
                        	if(peek == '/'){
                            	state = 2;
                            	peek = ' ';
                        	}
                        	else {
                            	state = 0;
                            	peek = ' ';
                        	}
                        	break;
                    }	
                }
                return lexical_scan(br);
            }
            else if(peek == '/'){
                while(peek != -1 && peek != '\n' && peek != '\r'){
                    peek = ' ';
                    readch(br);
                }
                return lexical_scan(br);
            }
            else{
                return Token.div;
            }

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character at line " + line + " after & : " + peek);
                    return null;
                }

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character at line " + line + " after | : " + peek);
                    return null;
                }

            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if (peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else {
                    return Word.lt;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }

            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    return Token.assign;
                }

            case (char) -1:
                return new Token(Tag.EOF);

            default:

                if (Character.isLetter(peek) || peek == '_') {
  // ... gestire il caso degli identificatori e delle parole chiave //
    				String s= "";
    				while(Character.isLetter(peek) || Character.isDigit(peek) || peek == '_'){
      				s= s + peek;
      				readch(br);
    				}

                    switch (s) {

                        case "cond":
                            return Word.cond;

                        case "when":
                            return Word.when;

                        case "else":
                            return Word.elsetok;

                        case "while":
                            return Word.whiletok;

                        case "do":
                            return Word.dotok;

                        case "print":
                            return Word.print;

                        case "read":
                            return Word.read;

                        case "seq":
                            return Word.seq;

                        default:
                            return new Word(Tag.ID, s);
                        }

                } else if (Character.isDigit(peek)) {
                    String n = "";
                    do {
                        n = n + peek;
                        readch(br);
                    } while (Character.isDigit(peek));
                    return new NumberTok(n);

                } else {
                    System.err.println("Erroneous character: " + peek);
                    return null;
                }
        }
    }

    public static void main(String[] args) {
        Lexer3 lex = new Lexer3();
        String path = "textfile3.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
