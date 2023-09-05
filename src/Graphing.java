
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
    double xmin=-10.0f;
    double xmax=10.0f;
    double ymin=-10.0f;
    double ymax=10.0f;
    double delta_x=xmax-xmin;
    double delta_y=ymax-ymin;
    double xpixel=delta_x/_width;
    double ypixel=delta_y/_height;
    double delta_x_scroll=delta_x*0.1f;
    double delta_y_scroll=delta_y*0.1f;
    //String input_buffer="2uxu*1u+";//2x+1
    String input_buffer="";
    //u=push
    //d=pop
    //*=multiply
    ///=divide
    //+=add
    //-=subtract
    //x=x_value

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
                    t=0.0f;
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
                    t=0.0f;
                    break;
                case '+'://positive
                    y+=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0f;
                    break;
                case '*'://multiply
                    y*=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0f;
                    break;
                case '/'://divide
                    if(x==0){
                        return x;
                    }
                    y/=x;
                    x=y;
                    y=z;
                    z=t;
                    t=0.0f;
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
                case 'a'://absolute
                    x=Math.abs(x);
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
        //g.fillRect(10, 10, 20, 20);
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
        for(double x=xmin;x<xmax;x+=xpixel){
            g.drawLine(pixel_x_from_plot_x(x),
                    pixel_y_from_plot_y(parse(x)),
                    pixel_x_from_plot_x(x+xpixel),
                    pixel_y_from_plot_y(parse(x+xpixel)));
        }
    }

    void set_buffer(String string) {
        input_buffer=string;
    }
    void set_xmin(String string) {
        xmin=Double.parseDouble(string);
        delta_x=xmax-xmin;
        delta_x_scroll=delta_x*0.1f;
    }
    void set_xmax(String string) {
        xmax=Double.parseDouble(string);
        delta_x=xmax-xmin;
        delta_x_scroll=delta_x*0.1f;
    }
    void set_ymin(String string) {
        ymin=Double.parseDouble(string);
        delta_y=ymax-ymin;
        delta_y_scroll=delta_y*0.1f;
    }
    void set_ymax(String string) {
        ymax=Double.parseDouble(string);
        delta_y=ymax-ymin;
        delta_y_scroll=delta_y*0.1f;
    }

    void start() {
        Thread t=new Thread(this);
        t.start();
        /*for(double x=xmin;x<xmax;x+=xpixel){
            System.out.println(parse(x));
        }*/
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
                xmin-=delta_x_scroll;
                xmax-=delta_x_scroll;
                repaint();
                break;
            case Canvas.KEY_NUM6:
            case Canvas.RIGHT:
            case -4:
                xmin+=delta_x_scroll;
                xmax+=delta_x_scroll;
                repaint();
                break;
        }
    }
}
