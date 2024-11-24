import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Game_PRICE extends MIDlet implements CommandListener {

    private Command goCommand; //Enter Grapher
    private Command backCommand; //Enter string entry
    private Display display;     //The display for this MIDlet
    private Graphing cunt;
    //private TextBox t;
    private Form t;
    private TextField rpn_str;
    private TextField xmin;
    private TextField xmax;
    private TextField ymin;
    private TextField ymax;
    String HELP_TEXT="This graphing calculator uses \"RPN\" equations as input.\nBy the way, this thing isn't even REMOTELY accurate when it comes to ln or e^x. Do NOT use it for mission critical stuff! DON'T EVEN THINK ABOUT IT!!!\nSupported commands:\n'_': Used to indicate negative number\n'x':The x input variable\n'u':Push\n'd':Pop\n'r':Swap X and Y\n'-':Y-X\n'+':Y+X\n'*':Y*X\n'/':Y/X\n's':Sin(x)\n'c':Cos(x)\n't':Tan(x)\n',':Sqrt(x)\n'a':Abs(x)\n'l':ln(x)\n'e':e^x\n'p':Y^X";
    public Game_PRICE() {
        display = Display.getDisplay(this);
        goCommand = new Command("Go", Command.OK, 0);
        backCommand = new Command("Back", Command.BACK, 0);
    }

    public void startApp() {
        cunt=new Graphing();
        /*t = new TextBox("Input RPN string", "xu2u+", 1024, 0);
        t.addCommand(goCommand);
        t.setCommandListener(this);*/

        t=new Form("Grapher");
        t.addCommand(goCommand);
        t.setCommandListener(this);
        rpn_str=new TextField("RPN String","xu2u+",1024,TextField.ANY);
        t.append(rpn_str);
        xmin=new TextField("Xmin","-10",64,TextField.DECIMAL);
        t.append(xmin);
        xmax=new TextField("Xmax","10",64,TextField.DECIMAL);
        t.append(xmax);
        ymin=new TextField("Ymin","-10",64,TextField.DECIMAL);
        t.append(ymin);
        ymax=new TextField("Ymax","10",64,TextField.DECIMAL);
        t.append(ymax);
        t.append(HELP_TEXT);

        cunt.addCommand(backCommand);
        cunt.setCommandListener(this);

        display.setCurrent(t);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == goCommand) {
            //cunt.set_buffer(t.getString());
            cunt.set_buffer(rpn_str.getString());
            cunt.set_xmin(xmin.getString());
            cunt.set_xmax(xmax.getString());
            cunt.set_ymin(ymin.getString());
            cunt.set_ymax(ymax.getString());
            cunt.start();
            Display.getDisplay(this).setCurrent(cunt);
        }
        if (c == backCommand) {
            Display.getDisplay(this).setCurrent(t);
        }
    }

}
