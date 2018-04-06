import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.event.*;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Math.max;
import javax.swing.event.*;


/*
    Matthew Payne 
    908500
*/
public class A1cthead extends JFrame {
    
    JButton mip_button; 
    JLabel image_icon1; 
    JLabel image_icon2; 
    JLabel image_icon3;
    JLabel image_icon4;
    JLabel image_icon5;
    JSlider zslice_slider, yslice_slider, xslice_slider, histogram_slider, resize_slider, resizeSlice_slider; //sliders to step through the slices (z and y directions) (remember 113 slices in z direction 0-112)
    BufferedImage image1, image2, image3, image4; //storing the image in memory
    short cthead[][][]; //store the 3D volume data set
    short min, max; //min/max value in the 3D volume data set

    /*
        This function sets up the GUI and reads the data set
    */
    public void A1cthead() throws IOException {
        //File name is hardcoded here - much nicer to have a dialog to select it and capture the size from the user
        File file = new File("CThead");
        
        //Create a BufferedImage to store the image data
        image1=new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        image2=new BufferedImage(256, 112, BufferedImage.TYPE_3BYTE_BGR);
        image3=new BufferedImage(256, 112, BufferedImage.TYPE_3BYTE_BGR);
        image4=new BufferedImage(256, 256, BufferedImage.TYPE_3BYTE_BGR);
        //Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find the equivalent in Java)
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        int i, j, k; //loop through the 3D data set

        min=Short.MAX_VALUE; max=Short.MIN_VALUE; //set to extreme values
        short read; //value read in
        int b1, b2; //data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around

        cthead = new short[113][256][256]; //allocate the memory - note this is fixed for this data set
        //loop through the data reading it in
        for (k=0; k<113; k++) {
            for (j=0; j<256; j++) {
                for (i=0; i<256; i++) {
                    //because the Endianess is wrong, it needs to be read byte at a time and swapped
                    b1=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
                    b2=((int)in.readByte()) & 0xff; //the 0xff is because Java does not have unsigned types (C++ is so much easier!)
                    read=(short)((b2<<8) | b1); //and swizzle the bytes around
                    if (read<min) min=read; //update the minimum
                    if (read>max) max=read; //update the maximum
                    cthead[k][j][i]=read; //put the short into memory (in C++ you can replace all this code with one fread)
                }
            }
        }
        System.out.println(min+" "+max); //diagnostic - for CThead this should be -1117, 2248
        //(i.e. there are 3366 levels of grey (we are trying to display on 256 levels of grey)
        //therefore histogram equalization would be a good thing



        // Set up the simple GUI
        // First the container:
        Container container = getContentPane();
        container.setLayout(new FlowLayout());
        
        // Then our image (as a label icon)
        image_icon1=new JLabel(new ImageIcon(image1));
        container.add(image_icon1);
        
        // Zslice slider
        zslice_slider = new JSlider(0,112);
        zslice_slider.setOrientation(1);
        zslice_slider.setMajorTickSpacing(50);
        zslice_slider.setMinorTickSpacing(10);
        zslice_slider.setPaintTicks(true);
        zslice_slider.setPaintLabels(true);
        container.add(zslice_slider);
        
        
        image_icon2=new JLabel(new ImageIcon(image2));
        container.add(image_icon2);
        
         // Yslice slider 
        yslice_slider = new JSlider(0,255);
        yslice_slider.setOrientation(1);
        yslice_slider.setMajorTickSpacing(50);
        yslice_slider.setMinorTickSpacing(10);
        yslice_slider.setPaintTicks(true);
        yslice_slider.setPaintLabels(true);
        container.add(yslice_slider);
        
        
        image_icon3 = new JLabel(new ImageIcon(image3));
        container.add(image_icon3);
        
        // Xslice slider
        xslice_slider = new JSlider(0,255);
        xslice_slider.setOrientation(1);
        xslice_slider.setMajorTickSpacing(50);
        xslice_slider.setMinorTickSpacing(10);
        xslice_slider.setPaintTicks(true);
        xslice_slider.setPaintLabels(true);
        container.add(xslice_slider);
        
        
        mip_button = new JButton("MIP");
        container.add(mip_button);
        
        // HistogramEq
        image_icon4 = new JLabel(new ImageIcon(image4));
        container.add(image_icon4);
     
        histogram_slider = new JSlider(0,112);
        histogram_slider.setOrientation(1);
        histogram_slider.setMajorTickSpacing(50);
        histogram_slider.setMinorTickSpacing(10);
        histogram_slider.setPaintTicks(true);
        histogram_slider.setPaintLabels(true);
        container.add(histogram_slider);
        
        // resize 
        image_icon5 = new JLabel(new ImageIcon());
        container.add(image_icon5);
        resize_slider = new JSlider(100,512);
        resize_slider.setOrientation(1);
        resize_slider.setMajorTickSpacing(50);
        resize_slider.setMinorTickSpacing(10);
        resize_slider.setPaintTicks(true);
        resize_slider.setPaintLabels(true);
        container.add(resize_slider);
        
        //resizeSlice_slider
        resizeSlice_slider = new JSlider(0,112);
        resizeSlice_slider.setOrientation(1);
        resizeSlice_slider.setMajorTickSpacing(50);
        resizeSlice_slider.setMinorTickSpacing(10);
        resizeSlice_slider.setPaintTicks(true);
        resizeSlice_slider.setPaintLabels(true);
        container.add(resizeSlice_slider);
        
        // Now all the handlers class
        GUIEventHandler handler = new GUIEventHandler();

        // associate appropriate handlers
        mip_button.addActionListener(handler);
        yslice_slider.addChangeListener(handler);
        zslice_slider.addChangeListener(handler);
        xslice_slider.addChangeListener(handler);
        histogram_slider.addChangeListener(handler);
        resize_slider.addChangeListener(handler);
        resizeSlice_slider.addChangeListener(handler);
        
        // ... and display everything
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /*
        This is the event handler for the application
    */
    private class GUIEventHandler implements ActionListener, ChangeListener {
	
        
        //Change handler
        @Override
        public void stateChanged(ChangeEvent e) {
            // Z
            if(e.getSource()== zslice_slider){
                image1 = Zslice(image1,zslice_slider.getValue());
                image_icon1.setIcon(new ImageIcon(image1));
            }
        
            // Y
            if(e.getSource()== yslice_slider){
                image2 = Yslice(image2,yslice_slider.getValue());
                image_icon2.setIcon(new ImageIcon(image2));
            }
        
            // X
            if(e.getSource()== xslice_slider){
                image3 = Xslice(image3,xslice_slider.getValue());
                image_icon3.setIcon(new ImageIcon(image3));
            }
            
            // HistogramEq
            if(e.getSource() == histogram_slider) {
                image4 = HistogramEq(image4,histogram_slider.getValue());
                image_icon4.setIcon(new ImageIcon(image4));
            }
            if(e.getSource() == resizeSlice_slider || e.getSource() == resize_slider) {
                int newSize = resize_slider.getValue();
                BufferedImage image5 = new BufferedImage(newSize, newSize, BufferedImage.TYPE_3BYTE_BGR);
                image5 = Resize(image5, resizeSlice_slider.getValue());
                image_icon5.setIcon(new ImageIcon(image5));
            } 
        }
        
        //action handlers
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource()==mip_button) {
                image1=MIP(image1);
                image_icon1.setIcon(new ImageIcon(image1));
                
                image2 = MIP_Y(image2);
                image_icon2.setIcon(new ImageIcon(image2));
                
                image3 = MIP_X(image3);
                image_icon3.setIcon(new ImageIcon(image3)); 
            }
        }
    }

    /*
        This function will return a pointer to an array
        of bytes which represent the image data in memory.
        Using such a pointer allows fast access to the image
        data for processing (rather than getting/setting
        individual pixels)
    */
    public static byte[] GetImageData(BufferedImage image) {
            WritableRaster WR=image.getRaster();
            DataBuffer DB=WR.getDataBuffer();
            if (DB.getDataType() != DataBuffer.TYPE_BYTE)
                throw new IllegalStateException("That's not of type byte");
          
            return ((DataBufferByte) DB).getData();
    }

    public BufferedImage Resize(BufferedImage image, int slice) {
        int size = image.getWidth();
        float col, nX,nY;
        short datum;
        float scale = size/256.0f;
        
        
        short nCthead[][][] = new short[113][size][size];
        int j,i,c, x, y, x1, x2, x3, x4;
        
        int nW  =  image.getWidth();
        int nH  =  image.getHeight();
        
        BufferedImage outputImage = new BufferedImage(nW, nH, BufferedImage.TYPE_3BYTE_BGR);
        byte[] nData = GetImageData(outputImage);
        
        for(j=0; j<(nH-1); j++) {
            for(i=0; i<(nW-1); i++) {
                // point in original image that maps to (i,j)
                int p = (int) (j/scale);
                int q = (int) (i/scale);
                
                // find colour of 4 points around new point
                x1 = cthead[slice][p][q];
                x2 = cthead[slice][p+1][q];
                x3 = cthead[slice][p][q+1];
                x4 = cthead[slice][p+1][q+1];
                
                
                x = (int)(scale*i);
                y = (int)(scale*j);
                nX = ((scale*i)-x);
                nY = ((scale*j)-y);
                
                // bilinear interpolation
                float iV  = x1 + (x3-x1)*(1-nX);
                float iV2 = x2 + (x4-x2)*(1-nX);
                float finalVal = iV + (iV2-iV)*(1-nY); 
                
                nCthead[slice][j][i] = (short)finalVal;
               
                datum=nCthead[slice][j][i];
               
                col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                for(c=0; c<3; c++) {
                    nData[c+3*i+3*j*nW]=(byte) col;
                }
            }
        }
        return outputImage;
    }
    
    
    
    public BufferedImage HistogramEq(BufferedImage image, int slice) {
        max = 2248;
        min = -1117;
        int w = image.getWidth(), h = image.getHeight(), index=0;
        int[] histogram = new int[max-min+1];
        float[] mapping = new float[max-min+1];
        byte[] data = GetImageData(image);
        float col;
        short datum;
        
        // create the histogram
        for(int k=0; k<113; k++) {
            for(int j=0; j<h; j++ ) {
                for(int i=0; i<w; i++) {
                    index = cthead[k][j][i] - min;
                    histogram[index]++; 
                }
            }
        }
        
        // cumulative distribution 
        int[] t = new int[max-min+1];
        t[0]= histogram[0];
        for(int i=1; i<max-min+1; i++) {
            t[i] = t[i-1] + histogram[i];
        }
        // mapping
        for (int i=0; i<max-min+1; i++) {
            mapping[i]= ((255.0f*t[i]/(h*w*113)));
        }
       
       // create the image
        for(int j=0; j<h; j++) {
            for(int i=0; i<w; i++) {
                datum = (short) cthead[slice][j][i];
                col = mapping[datum-min];
                for(int c=0; c<3; c++) {
                    data[c+3*i+3*j*w]=(byte) col;
                }   
            }
        }
        return image;
    }
    
    
    /*
        This function shows how to carry out an operation on an image.
        It obtains the dimensions of the image, and then loops through
        the image carrying out the copying of a slice of data into the
        image.
    */
    public BufferedImage MIP(BufferedImage image) {
        int w=image.getWidth(), h=image.getHeight(), i, j, c,k, maximum;
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
               maximum = MIN_VALUE;
                for(k=0; k< 113; k++){
                    maximum=max(cthead[k][j][i],maximum);
                    for (c=0; c<3; c++) {
                        datum=(short) maximum;
                        col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                        data[c+3*i+3*j*w]=(byte) col;
                   } 
                } 
            } 
        } 
        return image;
    }
    
    
    public BufferedImage MIP_Y(BufferedImage image) {
        //Get image dimensions, and declare loop variables
        int w=image.getWidth(), h=image.getHeight(), i, j, c, k, maximum;
        //Obtain pointer to data for fast processing
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (k=0; k<112; k++) {
            for (i=0; i<w; i++) {
                maximum = MIN_VALUE;
                for(j=0; j<h; j++){
                    maximum=max(cthead[k][j][i],maximum);
                    for (c=0; c<3; c++) {
                        datum=(short) maximum;
                        col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                        data[c+3*i+3*k*w]=(byte) col;
                   }
                }
            }
        }

        return image;
    }
    
    public BufferedImage MIP_X(BufferedImage image) {
        int w=image.getWidth(), h=image.getHeight(), i, j, c, k, maximum;
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (k=0; k<112; k++) {
            for (j=0; j<w; j++) {
                maximum = MIN_VALUE;
                for(i=0; i<h; i++){
                    maximum=max(cthead[k][j][i],maximum);
                    for (c=0; c<3; c++) {
                        datum=(short) maximum;
                        col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                        data[c+3*j+3*k*w]=(byte) col;
                   }
                }
            }
        }
        return image;
    }   
    
    public BufferedImage Zslice(BufferedImage image,int slice) {
        int w=image.getWidth(), h=image.getHeight(), i, j, c;
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (j=0; j<h; j++) {
            for (i=0; i<w; i++) {
                datum=cthead[slice][j][i];
                col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                for (c=0; c<3; c++) {
                    data[c+3*i+3*j*w]=(byte) col;
                }
            }
        }

        return image;
    }
    
    public BufferedImage Yslice(BufferedImage image,int slider) {
        int w=image.getWidth(), i, k, c;
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (k=0; k<112; k++) {
            for (i=0; i<w; i++) {
                datum = cthead[k][slider][i];
                col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                    for (c=0; c<3; c++) {
                        data[c+3*i+3*k*w]=(byte) col;
                    }
            }
        }

        return image;
    }
     
    public BufferedImage Xslice(BufferedImage image,int slider) {
        int w=image.getWidth(), j, k, c;
        byte[] data = GetImageData(image);
        float col;
        short datum;
        for (k=0; k<112; k++) {
            for (j=0; j<w; j++) {
                datum=cthead[k][j][slider];
                col=(255.0f*((float)datum-(float)min)/((float)(max-min)));
                for (c=0; c<3; c++) {
                    data[c+3*j+3*k*w]=(byte) col;
                }
            }
        } 
        return image;
    }
   
    public static void main(String[] args) throws IOException {
       A1cthead e = new A1cthead();
       e.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       e.A1cthead();
    }
}