/**
 * Copyright (c) 2013, Mattias Andrée
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package v;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

import static v.VMaths.*;


public class D3
{
    static final float DEGREES_PER_SECOND = 50f / 1000f;
    static final float[][] UNIT = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
    
    static float[] xs = new float[8];
    static float[] ys = new float[8];
    static float[] zs = new float[8];
    
    static float[] normal_xs = {0f, 0f, 1f, -1f,  0f,  0f};
    static float[] normal_ys = {1f, 0f, 0f,  0f,  0f, -1f};
    static float[] normal_zs = {0f, 1f, 0f,  0f, -1f,  0f};
    
    static float[] rotation = {1f, 0f, 0f};
    static float rotation_speed = 0f;
    
    static int rx = 0;
    static int ry = 0;
    static int rxy = 0;
    static int rg = 0;
    
    public static void main(String... args) throws InterruptedException
    {
	for (int i = 0; i < 8; i++)
	{
	    xs[i] = ((i & 1) != 0 ? 1f : 0f) - 0.5f;
	    ys[i] = ((i & 2) != 0 ? 1f : 0f) - 0.5f;
	    zs[i] = ((i & 4) != 0 ? 1f : 0f) - 0.5f;
	}
	
	final JFrame window = new JFrame();
	final JPanel panel;
	window.pack();
	window.setLayout(new BorderLayout());
	window.add(panel = new JPanel()
	    {
		{
		    this.setBackground(Color.BLACK);
		}
		
		@Override
		public void paint(Graphics g)
		{
		    super.paint(g);
		    
		    /* */
		    int[] z = new int[6];
		    for (int i = 0; i < 6; i++)
		    {
			int v = (int)(normal_zs[i] * 1000f);
			z[i] = (v << 8) | i;
		    }
		    
		    Arrays.sort(z);
		    
		    for (int i = 0; i < 6; i++)
		    {
			int side = z[i] & 7;
			switch (side)
			{
			    case 0:  draw(188, 188, 188, 0, 1, 5, 4, g, normal_zs[0]);  break;
			    case 1:  draw(188,   0,   0, 0, 1, 3, 2, g, normal_zs[1]);  break;
			    case 2:  draw(  0, 188,   0, 0, 2, 6, 4, g, normal_zs[2]);  break;
			    case 3:  draw(  0,   0, 188, 1, 3, 7, 5, g, normal_zs[3]);  break;
			    case 4:  draw(188, 128,   0, 4, 5, 7, 6, g, normal_zs[4]);  break;
			    case 5:  draw(188, 188,   0, 2, 3, 7, 6, g, normal_zs[5]);  break;
			}
		    }
		    /* */
		    
		    /* */
		    draw(new Color(255,   0,   0), 0, 1, g);
		    draw(new Color(  0,   0, 255), 0, 2, g);
		    draw(new Color(210,   0, 210), 0, 4, g);
		    draw(new Color(255, 255,   0), 1, 3, g);
		    draw(new Color(210, 128,   0), 1, 5, g);
		    draw(new Color(  0, 255,   0), 2, 3, g);
		    draw(new Color(  0, 210, 210), 2, 6, g);
		    draw(new Color(188, 210,   0), 3, 7, g);
		    draw(new Color(188,   0,   0), 4, 5, g);
		    draw(new Color(  0,   0, 188), 4, 6, g);
		    draw(new Color(188, 188,   0), 5, 7, g);
		    draw(new Color(  0, 188,   0), 6, 7, g);
		    /* */
		}
		
		private void draw(int R, int G, int B, int a, int b, int c, int d, Graphics g, float normal_z)
		{
		    int A = (int)(255f * normal_z);
		    if (A > 0)
		    {
			A = A / 2 + 128;
			
			if (A > 255)
			    A = 255;
			g.setColor(new Color(R, G, B, A));
			g.fillPolygon(new int[] {Px(a), Px(b), Px(c), Px(d)},
				      new int[] {Py(a), Py(b), Py(c), Py(d)}, 4);
		    }
		}
		
		private void draw(Color colour, int i, int j, Graphics g)
		{
		    g.setColor(colour);
		    g.drawLine(Px(i), Py(i), Px(j), Py(j));
		}
		
		static final float SCREEN_Z = 50f;
		static final float MODEL_Z = 55f;
		static final float VIEWER_Z = 50f;
		static final float VIEWER_X = 0f;
		static final float VIEWER_Y = 0f;
		static final float CAMERA_Z = 10f;
		static final float CAMERA_X = 0f;
		static final float CAMERA_Y = 0f;
		
		private int Px(int i)
		{
		    /* Orthogonal projection */
		    //float z = SCREEN_Z / (zs[i] + MODEL_Z);
		    //return (int)(xs[i] * z * 300f + 400f);
		    
		    /* Perspective projection */
		    float model_z = zs[i] + MODEL_Z;
		    float model_x = xs[i];
		    float p = VIEWER_Z / (model_z - CAMERA_Z) * (model_x - CAMERA_X) - VIEWER_X;
		    return (int)(p * 300f + 400f);
		}
		
		private int Py(int i)
		{
		    /* Orthogonal projection */
		    //float z = SCREEN_Z / (zs[i] + MODEL_Z);
		    //return (int)(ys[i] * z * 300f + 300f);
		    
		    /* Perspective projection */
		    float model_z = zs[i] + MODEL_Z;
		    float model_y = ys[i];
		    float p = VIEWER_Z / (model_z - CAMERA_Z) * (model_y - CAMERA_Y) - VIEWER_Y;
		    return (int)(p * 300f + 300f);
		}
		
	    });
	window.setBackground(Color.BLACK);
	final Insets in = window.getInsets();
	window.setSize(new Dimension(in.left + 800 + in.right, in.top + 600 + in.bottom));
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setVisible(true);
	
	final float SPEED = (float)(Math.sqrt(25.5f / 2f)) * DEGREES_PER_SECOND;
	
	window.addKeyListener(new KeyListener()
	    {
		public void keyTyped(KeyEvent e) {}
		
		public void keyReleased(KeyEvent e)
		{
		    switch (e.getKeyCode())
		    {
			case KeyEvent.VK_D:  rx  = 0;  break;
			case KeyEvent.VK_A:  rx  = 0;  break;
			case KeyEvent.VK_W:  ry  = 0;  break;
			case KeyEvent.VK_S:  ry  = 0;  break;
			case KeyEvent.VK_E:  rxy = 0;  break;
			case KeyEvent.VK_Q:  rxy = 0;  break;
			case KeyEvent.VK_F:  rg  = 0;  break;
			case KeyEvent.VK_C:  rg  = 0;  break;
		    }
		}
		
		public void keyPressed(KeyEvent e)
		{
		    switch (e.getKeyCode())
		    {
			case KeyEvent.VK_D:  rx  = +1;  break;
			case KeyEvent.VK_A:  rx  = -1;  break;
			case KeyEvent.VK_W:  ry  = +1;  break;
			case KeyEvent.VK_S:  ry  = -1;  break;
			case KeyEvent.VK_E:  rxy = +1;  break;
			case KeyEvent.VK_Q:  rxy = -1;  break;
			case KeyEvent.VK_F:  rg  = +1;  break;
			case KeyEvent.VK_C:  rg  = -1;  break;
			    
			case KeyEvent.VK_R:
			    rotation_speed = 0;
			    break;
		    }
		}
	    });
	
	for (;;)
	{
	    Thread.sleep(50);
	    
	    if (rx != 0)
	    {
		float[] v = mul(rx * SPEED, createRotation("x"));
		v = add(mul(rotation_speed, rotation), v);
		rotation_speed = length(v);
		rotation = normalise(v);
	    }
	    
	    if (ry != 0)
	    {
		float[] v = mul(ry * SPEED, createRotation("y"));
		v = add(mul(rotation_speed, rotation), v);
		rotation_speed = length(v);
		rotation = normalise(v);
	    }
	    
	    if (rxy != 0)
	    {
		float[] v = mul(rxy * SPEED, createRotation("xy"));
		v = add(mul(rotation_speed, rotation), v);
		rotation_speed = length(v);
		rotation = normalise(v);
	    }
	    
	    if (rg == 1)
		rotation_speed *= 1.1f;
	    else if (rg == -1)
		rotation_speed /= 1.1f;
	    
	    if (rotation_speed != 0)
		transform(createRotation(rotation, rotation_speed));
	    
	    panel.repaint();
	}
    }
    
    
    public static void transform(float[][] m)
    {
	/* This is not a transformation of the object, but
	 * rather its projection onto the screen. (This
	 * program combines the object and the projection.) */
	
	for (int i = 0; i < 8; i++)
	{
	    float[] v = mul(m, xs[i], ys[i], zs[i]);
	    xs[i] = v[0];
	    ys[i] = v[1];
	    zs[i] = v[2];
	}
	
	for (int i = 0; i < 6; i++)
	{
	    float[] v = mul(m, normal_xs[i], normal_ys[i], normal_zs[i]);
	    normal_xs[i] = v[0];
	    normal_ys[i] = v[1];
	    normal_zs[i] = v[2];
	}
    }
    
}

