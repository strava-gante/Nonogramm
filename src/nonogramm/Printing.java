package nonogramm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;

/**
 *
 * @author Tom Richter
 */
public class Printing implements Printable{
    
    protected BufferedImage img;
    private String[] x;
    private String[] y;
    private int maxX;
    private int maxY;
    private final int maxSize = 50;
    
    public Printing(BufferedImage img) {
        this.img=img;
        Riddle();
    }
    
    @Override
    public int print(Graphics gr, PageFormat pageFormat, int pageIndex) {
        if (pageIndex != 0) {
            return NO_SUCH_PAGE;
        }
        
        final int offset = 20;
        final double faktor = 1;
        final double x = pageFormat.getImageableX()+offset;
        final double y = pageFormat.getImageableY()+offset;
        final double w = pageFormat.getImageableWidth()-2*offset;
        final double h = pageFormat.getImageableHeight()-2*offset;
        Graphics2D g = (Graphics2D)gr;
        g.translate(x, y);
        
        int fieldWidth = (int) (w / (maxX + img.getWidth()));
        if (fieldWidth > maxSize) {
            fieldWidth = 50;
        }
        int fontWidth = fieldWidth / 2;
        int fontHeight = FontHeight(fontWidth, gr);
        gr.setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontHeight));
        for (int i = 0; i < this.x.length; i++) {  // X Zahlen
            gr.drawString(this.x[i], 0, maxY * fontHeight+i*fieldWidth+(int)(fontHeight*faktor));
        }
        for (int i = 0; i < this.y.length; i++) {  //Y Zahlen
            String[] newY = this.y[i].split("#");
            for (int c = 0; c < maxY; c++) {
                if(newY[c].length()==1) {
                    gr.drawString(newY[c], maxX*fontWidth+i*fieldWidth+fieldWidth/2-fontWidth/2, c*fontHeight+(int)(fontHeight*faktor));
                } else {
                    gr.drawString(newY[c], maxX*fontWidth+i*fieldWidth+fieldWidth/2-fontWidth, c*fontHeight+(int)(fontHeight*faktor));
                }
            }
        }
        g.setStroke(new BasicStroke((float)0.5));
        //X small lines
        for(int i=0;i<img.getHeight();i++) {
            g.drawLine(0, maxY*fontHeight+i*fieldWidth, maxX*fontWidth+fieldWidth*img.getWidth(), maxY*fontHeight+i*fieldWidth);
        }
        //Y small lines
        for(int i=0;i<img.getWidth();i++) {
            g.drawLine(maxX*fontWidth+i*fieldWidth,0,maxX*fontWidth+i*fieldWidth,maxY*fontHeight+img.getHeight()*fieldWidth);
        }
        g.setStroke(new BasicStroke(1));
        //X fat lines
        for(int i=0;i<img.getHeight();i+=5) {
            g.drawLine(0, maxY*fontHeight+i*fieldWidth, maxX*fontWidth+fieldWidth*img.getWidth(), maxY*fontHeight+i*fieldWidth);
        }
        //Y fat lines
        for(int i=0;i<img.getWidth();i+=5) {
            g.drawLine(maxX*fontWidth+i*fieldWidth, 0, maxX*fontWidth+i*fieldWidth+1, maxY*fontHeight+img.getHeight()*fieldWidth);
        }
        
        return PAGE_EXISTS;
    }
    
    public void Drucken() {
         PrinterJob job = PrinterJob.getPrinterJob();
         job.setPrintable(this);
         boolean ok = job.printDialog();
         if (ok) {
             try {
                  job.print();
             } catch (PrinterException ex) {
                 System.out.println("The job did not successfully complete");
             }
         }
    }
    
    private void Riddle() {
        int w = img.getWidth();
        int h = img.getHeight();
        x = new String[h];
        y = new String[w];
        int counter = 0;
        
        for(int i=0;i<h;i++) {  // X Part
            x[i]="";
            for(int j=0;j<w;j++) {
                if(img.getRGB(j, i)==Color.BLACK.getRGB()) {
                    counter++;
                } else {
                    if(counter!=0) {
                        x[i]=x[i]+counter+" ";
                    }
                    counter=0;
                }
            }
            if(counter!=0) {
                x[i]=x[i]+counter+" ";
                counter=0;
            }
            if(x[i].length()>0) {
                x[i]=x[i].substring(0, x[i].length()-1);
            }
        }
        for(int j=0;j<w;j++) { //Y Part
            y[j]="";
            for(int i=0;i<h;i++) {
                if(img.getRGB(j, i)==Color.BLACK.getRGB()) {
                    counter++;
                } else {
                    if(counter!=0) {
                        y[j]=y[j]+counter+"#";
                    }
                    counter=0;
                } 
            }
            if(counter!=0) {
                y[j]=y[j]+counter+"#";
                counter=0;
            }
            if(y[j].length()>0) {
                y[j]=y[j].substring(0, y[j].length()-1);
            }
        }
        maxX=MaxLength(x);
        for(int i=0;i<x.length;i++) {
            while(x[i].length()<maxX) {
                x[i]=" "+x[i];
            }
        }
        maxY=MaxLength(y);
        for(int i=0;i<y.length;i++) {
            while(y[i].length()-y[i].replace("#", "").length()+1<maxY) { //Nur Rauten Zählen
                y[i]=" #"+y[i];
            }
        }
        System.out.println("Done");
    }
    
    private int MaxLength(String[] array) {
        int max=0;
        for(int i=0;i<array.length;i++) {
            if(!array[i].contains("#")) {
                if(max<array[i].length()) {
                    max=array[i].length();
                }
            } else {
                int number = array[i].length()-array[i].replace("#", "").length()+1;
                if(max<number) {
                    max=number;
                }
            }
        }
        return max;
    }
    
    private int FontHeight(int fontWidth, Graphics gr) {
        int i=2;
        int newWidth;
        while(true) {
            gr.setFont(new Font(Font.MONOSPACED, Font.PLAIN, i));
            FontMetrics metric = gr.getFontMetrics();
            newWidth = metric.stringWidth("1");
            if(fontWidth==newWidth) {
                break;
            } else {
                i++;
            }
        }
        return i;
    }
}
