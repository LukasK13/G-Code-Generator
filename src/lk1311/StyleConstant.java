package lk1311;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
 
public class StyleConstant {
   
    private static final int FONTSIZE = 16;
    private static final String FONTFAMILY = "Helvetica";
   
    public static SimpleAttributeSet BLACK = new SimpleAttributeSet();
    public static SimpleAttributeSet RED = new SimpleAttributeSet();
   
    static {
        StyleConstants.setForeground(BLACK, Color.black);
        StyleConstants.setFontFamily(BLACK, FONTFAMILY);
        StyleConstants.setFontSize(BLACK, FONTSIZE);
       
        StyleConstants.setForeground(RED, Color.red);
        StyleConstants.setFontFamily(RED, FONTFAMILY);
        StyleConstants.setFontSize(RED, FONTSIZE);
    }
}