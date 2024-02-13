public class Es1_4 {
    public static boolean scan(String s) {
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);
            switch (state) {
                case 0:
                if(ch == 32)
                    state = 0;
                else if(ch == 49 || ch == 51 || ch == 53 || ch == 55 || ch == 57)
                    state = 1;
                else if(ch == 48 || ch == 50 || ch == 52 || ch == 54 || ch == 56)
                    state = 2;
                else
                    state = -1;
                break;
                case 1:
                if(ch == 49 || ch == 51 || ch == 53 || ch == 55 || ch == 57)
                    state = 1;
                else if(ch == 48 || ch == 50 || ch == 52 || ch == 54 || ch == 56)
                    state = 2;
                else if(ch == 32)
                    state = 11; //stato intermedio q'odd
                else if(ch >= 76 && ch <= 90)
                    state = 3;
                else 
                    state = -1;
                break;
                case 2:
                if(ch == 48 || ch == 50 || ch == 52 || ch == 54 || ch == 56)
                    state = 2;
                else if(ch == 49 || ch == 51 || ch == 53 || ch == 55 || ch == 57)
                    state = 1;
                else if(ch == 32)
                    state = 22; //stato interdemio q'even
                else if(ch >= 66 && ch <= 75)
                    state = 4;
                else
                    state = -1;
                break;
                case 11: //stato intermedio q'odd
                if(ch == 32)
                    state = 11; //stadio intermedio q'odd
                else if(ch >= 76 && ch <= 90)
                    state = 3;
                else
                    state = -1;
                break;
                case 22: //stato intermedio q'even
                if(ch == 32)
                    state = 22; //stato intermedio  q'even
                else if(ch >= 66 && ch <= 75)
                    state = 4;
                else
                    state = -1;
                break;
                case 3:
                if(ch >= 97 && ch <= 122)
                    state = 3;
                else if(ch == 32)
                    state = 33; //stato finale q'3
                else 
                    state = -1;
                break;
                case 4:
                if(ch >= 97 && ch <= 122)
                    state = 4;
                else if(ch == 32)
                    state = 44; //stato finale q'4
                else 
                    state = -1;
                break;
                case 33: //stato finale q'3
                if(ch == 32)
                    state = 33; //stato finale q'3
                else if(ch >= 66 && ch <= 90)
                    state = 3;
                else 
                    state = -1;
                break;
                case 44: //stato finale q'4
                if(ch == 32)
                    state = 44;
                else if(ch >= 66 && ch <= 90)
                    state = 4;
                else 
                    state = -1;
                break;
            }
        }
        return (state == 3) || (state == 4) || (state ==33) || (state == 44);
                
                
    }
    
    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
    
    
    
}