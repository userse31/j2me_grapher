
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Qwerty
 */
class Graphing extends Canvas implements Runnable{
    int _width=getWidth();
    int _height=getHeight();
    //int _width=128;
    //int _height=128;
    int MACLAURIN_STEPS=20;
    int E_POW_STEPS=15;
    double ROUND_CONST=1e12;
    boolean do_cursor=false;
    double cursor_x=0;
    double xmin=-10.0d;
    double xmax=10.0d;
    double ymin=-10.0d;
    double ymax=10.0d;
    double delta_x=xmax-xmin;
    double delta_y=ymax-ymin;
    double xpixel=delta_x/_width;
    double ypixel=delta_y/_height;
    double delta_x_scroll=delta_x*0.1d;
    double delta_y_scroll=delta_y*0.1d;
    double mult_zoom_in=0.9d;
    double mult_zoom_out=1.1d;
    //String input_buffer="2uxu*1u+";//2x+1
    String input_buffer="";
    //u=push
    //d=pop
    //*=multiply
    ///=divide
    //+=add
    //-=subtract
    //x=x_value

    //I wish death and misery among the asshole who stripped stuff like pow and log from the J2ME Math object...
    //BOILERPLATE!!!!
    public double pow_int(double x,int y){
        double tmp=x;
        if(y==0){
            return 1;
        }
        if(y==1){
            return x;
        }
        for(int i=y;y>1;y--){
            tmp*=x;
        }
        return tmp;
    }
    public double factorial(int x){
        if(x==0){
            return 1;
        }
        double tmp=1;
        for(int i=2;i<x;i++){
            tmp*=i;
        }
        return tmp;
    }
    public double e_pow(double x){
        if(x<-7){//Some weird confusing crap...
            //return (7.25146660e-04)+((2.78240853e-05)*x)+((2.06407069e-07)*x)*((2.06407069e-07)*x);
            return -1/(x*100);
        }
        double tmp=1+x;
        tmp+=pow_int(x,2)/2;
        tmp+=pow_int(x,3)/6;
        tmp+=pow_int(x,4)/24;
        tmp+=pow_int(x,5)/120;
        tmp+=pow_int(x,6)/720;
        tmp+=pow_int(x,7)/5040;
        tmp+=pow_int(x,8)/40320;
        tmp+=pow_int(x,9)/362880;
        tmp+=pow_int(x,10)/3628800;
        tmp+=pow_int(x,11)/39916800;
        tmp+=pow_int(x,12)/479001600;
        tmp+=pow_int(x,13)/6227020800.0d;
        tmp+=pow_int(x,14)/87178291200.0d;
        tmp+=pow_int(x,15)/(15.0d*87178291200.0d);
        tmp+=pow_int(x,16)/(240.0d*87178291200.0d);
        tmp+=pow_int(x,17)/(4080.0d*87178291200.0d);
        tmp+=pow_int(x,18)/(73440.0d*87178291200.0d);
        tmp+=pow_int(x,19)/(19.0d*73440.0d*87178291200.0d);
        tmp+=pow_int(x,20)/(20.0d*19.0d*73440.0d*87178291200.0d);
        return tmp;
    }
    public double arctanh_maclaurin(double z){
        double tmp=0;
        for(int n=1;n<MACLAURIN_STEPS;n++){
            tmp+=pow_int(z,((2*n)-1))/((2*n)-1);
        }
        return tmp;
    }
    public double ln(double w){
        if(w<=0){
            return Double.NaN;
        }
        return 2*arctanh_maclaurin((w-1)/(w+1));
    }
    public double pwr_double(double a,double b){
        if(a==0&&b==0){
            return Double.NaN;
        }
        if(a==0){
            return 0;
        }
        if(b==0){
            return 1;
        }
        if(a<0){
            return e_pow(b*ln(-a));
        }
        return e_pow(b*ln(a));
    }
    public double round(double x){
        return Math.floor((x*ROUND_CONST))/ROUND_CONST;
    }
    
    public int pixel_x_from_plot_x(double plot_x){
        return (int) ((_width / (xmax-xmin)) * (plot_x - xmin));
    }
    public int pixel_y_from_plot_y(double plot_y){
        return (int) (_height-((_height / delta_y) * (plot_y - ymin)));
    }
    public double parse(double x_val){
        double x=0;
        double y=0;
        double z=0;
        double t=0;
        String number_buffer="";
        for(int i=0;i<input_buffer.length();i++){
            switch(input_buffer.charAt(i)){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '.':
                    number_buffer+=input_buffer.charAt(i);
                    break;
                case '_'://negative number sign
                    number_buffer+="-";
                    break;
                case 'x':
                    number_buffer=String.valueOf(x_val);
                    break;
                case 'u'://push
                    t=z;
                    z=y;
                    y=x;
                    x=Double.parseDouble(number_buffer);
                    number_buffer="";
                    break;
                case 'd'://pop
                    x=y;
                    y=z;
                    z=t;
                    t=0.0d;
                    break;
                case 'r':
                    double yes=x;
                    x=y;
                    y=yes;
                    break;
                case '-'://negative
                    y-=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0d;
                    break;
                case '+'://positive
                    y+=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0d;
                    break;
                case '*'://multiply
                    y*=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0d;
                    break;
                case '/'://divide
                    if(x==0){
                        return Double.NaN;
                    }
                    y/=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0d;
                    break;
                case 'e'://sin
                    x=e_pow(x);
                    break;
                case 's'://sin
                    x=Math.sin(x);
                    break;
                case 'c'://cos
                    x=Math.cos(x);
                    break;
                case 't'://tan
                    x=Math.tan(x);
                    break;
                case ','://sqrt
                    x=Math.sqrt(x);
                    break;
                case 'l'://ln
                    x=ln(x);
                    break;
                case 'a'://absolute
                    x=Math.abs(x);
                    break;
                case 'p'://power
                    if(x==0&&y==0){
                        return Double.NaN;
                    }
                    y=pwr_double(y,x);
                    x=y;
                    y=z;
                    z=t;
                    t=0.0d;
                    break;
                default:
                    break;
            }
        }
        return x;
    }
    public void run() {
        repaint();
    }
    protected void paint(Graphics g) {
        g.setColor(0xFFFFFF);
        g.fillRect(0,0,_width,_height);
        g.setColor(0x000000);
        //Draw the coordinates.
        g.drawLine(pixel_x_from_plot_x(0),
                pixel_y_from_plot_y(ymin),
                pixel_x_from_plot_x(0),
                pixel_y_from_plot_y(ymax)
                );
        for(double i=Math.floor(ymin);i<Math.ceil(ymax);i++){
            g.drawLine(pixel_x_from_plot_x(0),
                pixel_y_from_plot_y(i),
                pixel_x_from_plot_x(xpixel),
                pixel_y_from_plot_y(i)
                );
        }
        g.drawLine(pixel_x_from_plot_x(xmin),
                pixel_y_from_plot_y(0),
                pixel_x_from_plot_x(xmax),
                pixel_y_from_plot_y(0)
                );
        for(double i=Math.floor(xmin);i<Math.ceil(xmax);i++){
            g.drawLine(pixel_x_from_plot_x(i),
                pixel_y_from_plot_y(0),
                pixel_x_from_plot_x(i),
                pixel_y_from_plot_y(ypixel)
                );
        }
        //Do the ACTUAL graphing.
        double parse_0,parse_1;//So we don't calculate the thing twice (the java compiler might optimize for that, but better be on the safe side eh?
        for(double x=xmin;x<xmax;x+=xpixel){
            parse_0=parse(x);
            parse_1=parse(x+xpixel);
            if(!(Double.isNaN(parse_0) || Double.isNaN(parse_1))){//We don't want to drawing lines using "NaN", that just slams the line to the top left corner.
                g.drawLine(pixel_x_from_plot_x(x),
                    pixel_y_from_plot_y(parse_0),
                    pixel_x_from_plot_x(x+xpixel),
                    pixel_y_from_plot_y(parse_1));
            }
        }
        if(do_cursor){
            parse_0=parse(cursor_x);
            g.drawString("X:"+String.valueOf(round(cursor_x)), 0, 0, Graphics.TOP|Graphics.LEFT);
            g.drawString("Y:"+String.valueOf(round(parse_0)), 0, 10, Graphics.TOP|Graphics.LEFT);
            if(!Double.isNaN(parse_0)){//Likewise, we don't need need the cursor slammed to the top of the screen when "y=0/0", so we'll pull a Casio and make it disappear on invalid/illegal sections of the graph.
                int draw_x=pixel_x_from_plot_x(cursor_x);
                int draw_y=pixel_y_from_plot_y(parse(cursor_x));
                g.drawLine(draw_x, draw_y+1, draw_x, draw_y+5);
                g.drawLine(draw_x, draw_y-1, draw_x, draw_y-5);
                g.drawLine(draw_x+1, draw_y, draw_x+5, draw_y);
                g.drawLine(draw_x-1, draw_y, draw_x-5, draw_y);
            }
        }
    }

    void set_buffer(String string) {
        input_buffer=string;
    }
    void set_xmin(String string) {
        xmin=Double.parseDouble(string);
        delta_x=xmax-xmin;
        delta_x_scroll=delta_x*0.1f;
        xpixel=delta_x/_width;
        ypixel=delta_y/_height;
    }
    void set_xmax(String string) {
        xmax=Double.parseDouble(string);
        delta_x=xmax-xmin;
        delta_x_scroll=delta_x*0.1f;
        xpixel=delta_x/_width;
        ypixel=delta_y/_height;
    }
    void set_ymin(String string) {
        ymin=Double.parseDouble(string);
        delta_y=ymax-ymin;
        delta_y_scroll=delta_y*0.1f;
        xpixel=delta_x/_width;
        ypixel=delta_y/_height;
    }
    void set_ymax(String string) {
        ymax=Double.parseDouble(string);
        delta_y=ymax-ymin;
        delta_y_scroll=delta_y*0.1f;
        xpixel=delta_x/_width;
        ypixel=delta_y/_height;
    }

    void start() {
        Thread t=new Thread(this);
        t.start();
    }
    protected void keyPressed(int keyCode){
        //System.out.println(keyCode);
        switch(keyCode){
            case Canvas.KEY_NUM2:
            case Canvas.UP:
            case -1:
                ymin+=delta_y_scroll;
                ymax+=delta_y_scroll;
                repaint();
                break;
            case Canvas.KEY_NUM8:
            case Canvas.DOWN:
            case -2:
                ymin-=delta_y_scroll;
                ymax-=delta_y_scroll;
                repaint();
                break;
            case Canvas.KEY_NUM4:
            case Canvas.LEFT:
            case -3:
                if(do_cursor){
                    cursor_x-=xpixel;
                    if(cursor_x<xmin){
                        xmin-=delta_x_scroll;
                        xmax-=delta_x_scroll;
                    }
                    if(parse(cursor_x)>ymax){
                        ymin+=delta_y_scroll;
                        ymax+=delta_y_scroll;
                    }
                    if(parse(cursor_x)<ymin){
                        ymin-=delta_y_scroll;
                        ymax-=delta_y_scroll;
                    }
                }else{
                    xmin-=delta_x_scroll;
                    xmax-=delta_x_scroll;
                }
                repaint();
                break;
            case Canvas.KEY_NUM6:
            case Canvas.RIGHT:
            case -4:
                if(do_cursor){
                    cursor_x+=xpixel;
                    if(cursor_x>xmax){
                        xmin+=delta_x_scroll;
                        xmax+=delta_x_scroll;
                    }
                    if(parse(cursor_x)>ymax){
                        ymin+=delta_y_scroll;
                        ymax+=delta_y_scroll;
                    }
                    if(parse(cursor_x)<ymin){
                        ymin-=delta_y_scroll;
                        ymax-=delta_y_scroll;
                    }
                }else{
                    xmin+=delta_x_scroll;
                    xmax+=delta_x_scroll;
                }
                repaint();
                break;
            case Canvas.FIRE:
            case Canvas.KEY_NUM5:
            case -5:
                do_cursor=!do_cursor;
                if(do_cursor){
                    cursor_x=xmin+((xmax-xmin)/2);
                }
                repaint();
                break;
            case Canvas.KEY_STAR://Zoom in
                xmin*=mult_zoom_in;
                xmax*=mult_zoom_in;
                ymin*=mult_zoom_in;
                ymax*=mult_zoom_in;
                delta_x=xmax-xmin;
                delta_y=ymax-ymin;
                delta_x_scroll=delta_x*0.1d;
                delta_y_scroll=delta_y*0.1d;
                repaint();
                break;
            case Canvas.KEY_POUND://Zoom out
                xmin*=mult_zoom_out;
                xmax*=mult_zoom_out;
                ymin*=mult_zoom_out;
                ymax*=mult_zoom_out;
                delta_x=xmax-xmin;
                delta_y=ymax-ymin;
                delta_x_scroll=delta_x*0.1d;
                delta_y_scroll=delta_y*0.1d;
                repaint();
                break;
            default:
                break;
        }
    }
}
