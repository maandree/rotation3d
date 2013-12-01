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
    static final float DEGREES_PER_SECOND_PER_SECOND = DEGREES_PER_SECOND;
    
    static float[] xs = new float[8 + 6 + 3 + 3];
    static float[] ys = new float[8 + 6 + 3 + 3];
    static float[] zs = new float[8 + 6 + 3 + 3];
    
    static float[] normal_xs = {0f, 0f, 1f, -1f,  0f,  0f};
    static float[] normal_ys = {1f, 0f, 0f,  0f,  0f, -1f};
    static float[] normal_zs = {0f, 1f, 0f,  0f, -1f,  0f};
    
    static float[] rotation = {1f, 0f, 0f};
    static float rotation_speed = 0f;
    
    static int rx = 0;
    static int ry = 0;
    static int rxy = 0;
    static int rg = 0;
    
    static int srx = 0;
    static int sry = 0;
    static int srxy = 0;
    
    static int zrx = 0;
    static int zry = 0;
    static int zrxy = 0;
    
    static float[][] Pm = UNIT;
    static float[][] Qm = UNIT;
    
    static boolean sreset = false;
    static boolean zreset = false;
    static boolean camera_to_rotation = false;
    static boolean rotation_to_camera = false;
    
    public static void main(String... args) throws InterruptedException
    {
	resetRotation();
	
	xs[8]  = +1;  xs[11] = -1;
	ys[9]  = +1;  ys[12] = -1;
	zs[10] = +1;  zs[13] = -1;
	
	xs[14] = +1;
	ys[15] = +1;
	zs[16] = +1;
	
	xs[17] = +1;
	ys[18] = +1;
	zs[19] = +1;
	
	final JFrame window = new JFrame();
	final JPanel panel;
	window.pack();
	window.setLayout(new BorderLayout());
	window.add(panel = new JPanel()
	    {
		float[] pxs = new float[8 + 7];
		float[] pys = new float[8 + 7];
		float[] pzs = new float[8 + 7];
		
		float[] normal_pxs = new float[6];
		float[] normal_pys = new float[6];
		float[] normal_pzs = new float[6];
		
		{
		    this.setBackground(Color.BLACK);
		}
		
		@Override
		public void paint(Graphics g)
		{
		    super.paint(g);
		    
		    
		    /* Camera */
		    for (int i = 8; i < 8 + 6; i++)
		    {
			pxs[i] = xs[i];
			pys[i] = ys[i];
			pzs[i] = zs[i];
		    }
		    for (int i = 0; i < 8; i++)
		    {
			float[] v = mul(Qm, mul(Pm, xs[i], ys[i], zs[i]));
			pxs[i] = v[0];
			pys[i] = v[1];
			pzs[i] = v[2];
		    }
		    for (int i = 0; i < 6; i++)
		    {
			float[] v = mul(Qm, mul(Pm, normal_xs[i], normal_ys[i], normal_zs[i]));
			normal_pxs[i] = v[0];
			normal_pys[i] = v[1];
			normal_pzs[i] = v[2];
		    }
		    /* End camera */
		    
		    
		    /* */
		    int[] z = new int[6];
		    for (int i = 0; i < 6; i++)
		    {
			int v = (int)(normal_pzs[i] * 1000f);
			z[i] = (v << 8) | i;
		    }
		    
		    Arrays.sort(z);
		    
		    for (int i = 0; i < 6; i++)
		    {
			int side = z[i] & 7;
			switch (side)
			{
			    case 0:  draw(188, 188, 188, 0, 1, 5, 4, g, normal_pzs[0]);  break;
			    case 1:  draw(188,   0,   0, 0, 1, 3, 2, g, normal_pzs[1]);  break;
			    case 2:  draw(  0, 188,   0, 0, 2, 6, 4, g, normal_pzs[2]);  break;
			    case 3:  draw(  0,   0, 188, 1, 3, 7, 5, g, normal_pzs[3]);  break;
			    case 4:  draw(188, 128,   0, 4, 5, 7, 6, g, normal_pzs[4]);  break;
			    case 5:  draw(188, 188,   0, 2, 3, 7, 6, g, normal_pzs[5]);  break;
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
		    
		    /* */
		    draw(new Color(255,   0,   0),  8, 14, g);
		    draw(new Color(  0, 255,   0),  9, 14, g);
		    draw(new Color(  0,   0, 255), 10, 14, g);
		    draw(new Color(  0, 255, 255), 11, 14, g);
		    draw(new Color(255,   0, 255), 12, 14, g);
		    draw(new Color(255, 255,   0), 13, 14, g);
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
		    //float z = SCREEN_Z / (pzs[i] + MODEL_Z);
		    //return (int)(pxs[i] * z * 300f + 400f);
		    
		    /* Perspective projection */
		    float model_z = pzs[i] + MODEL_Z;
		    float model_x = pxs[i];
		    float p = VIEWER_Z / (model_z - CAMERA_Z) * (model_x - CAMERA_X) - VIEWER_X;
		    return (int)(p * 300f + 400f);
		}
		
		private int Py(int i)
		{
		    /* Orthogonal projection */
		    //float z = SCREEN_Z / (pzs[i] + MODEL_Z);
		    //return (int)(pys[i] * z * 300f + 300f);
		    
		    /* Perspective projection */
		    float model_z = pzs[i] + MODEL_Z;
		    float model_y = pys[i];
		    float p = VIEWER_Z / (model_z - CAMERA_Z) * (model_y - CAMERA_Y) - VIEWER_Y;
		    return (int)(p * 300f + 300f);
		}
		
	    });
	window.setBackground(Color.BLACK);
	final Insets in = window.getInsets();
	window.setSize(new Dimension(in.left + 800 + in.right, in.top + 600 + in.bottom));
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setVisible(true);
	
	final float SPEED = (float)(Math.sqrt(25.5f / 2f)) * DEGREES_PER_SECOND_PER_SECOND;
	
	final float SSPEED = 25.5f * DEGREES_PER_SECOND;
	
	window.addKeyListener(new KeyListener()
	    {
		public void keyTyped(KeyEvent e) {}
		
		public void keyReleased(KeyEvent e)
		{
		    switch (e.getKeyCode())
		    {
			case KeyEvent.VK_D:  zrx  = srx  = rx  = 0;  break;
			case KeyEvent.VK_A:  zrx  = srx  = rx  = 0;  break;
			case KeyEvent.VK_W:  zry  = sry  = ry  = 0;  break;
			case KeyEvent.VK_S:  zry  = sry  = ry  = 0;  break;
			case KeyEvent.VK_E:  zrxy = srxy = rxy = 0;  break;
			case KeyEvent.VK_Q:  zrxy = srxy = rxy = 0;  break;
			case KeyEvent.VK_F:  rg   = 0;               break;
			case KeyEvent.VK_C:  rg   = 0;               break;
		    }
		}
		
		public void keyPressed(KeyEvent e)
		{
		    boolean shift = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
		    boolean alt   = (e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK)   != 0;
		    boolean ctrl  = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK)  != 0;
		    
		    int v = ctrl ? 2 : 1;
		    
		    if ((shift == false) && (alt == false))
			switch (e.getKeyCode())
			{
			    case KeyEvent.VK_D:  rx  = +v;  break;
			    case KeyEvent.VK_A:  rx  = -v;  break;
			    case KeyEvent.VK_W:  ry  = +v;  break;
			    case KeyEvent.VK_S:  ry  = -v;  break;
			    case KeyEvent.VK_E:  rxy = +v;  break;
			    case KeyEvent.VK_Q:  rxy = -v;  break;
			    case KeyEvent.VK_F:  rg  = +1;  break;
			    case KeyEvent.VK_C:  rg  = -1;  break;
				
			    case KeyEvent.VK_X:
				rotation_to_camera = true;
				break;
				
			    case KeyEvent.VK_R:
				rotation_speed = 0;
				break;
			}
		    else if (shift && (alt == false))
			switch (e.getKeyCode())
			{
			    case KeyEvent.VK_D:  srx  = +v;  break;
			    case KeyEvent.VK_A:  srx  = -v;  break;
			    case KeyEvent.VK_W:  sry  = +v;  break;
			    case KeyEvent.VK_S:  sry  = -v;  break;
			    case KeyEvent.VK_E:  srxy = +v;  break;
			    case KeyEvent.VK_Q:  srxy = -v;  break;
				
			    case KeyEvent.VK_X:
				camera_to_rotation = true;
				break;
				
			    case KeyEvent.VK_R:
				sreset = true;
				break;
			}
		    else if ((shift == false) && alt)
			switch (e.getKeyCode())
			{
			    case KeyEvent.VK_D:  zrx  = +v;  break;
			    case KeyEvent.VK_A:  zrx  = -v;  break;
			    case KeyEvent.VK_W:  zry  = +v;  break;
			    case KeyEvent.VK_S:  zry  = -v;  break;
			    case KeyEvent.VK_E:  zrxy = +v;  break;
			    case KeyEvent.VK_Q:  zrxy = -v;  break;
				
			    case KeyEvent.VK_R:
				zreset = true;
				break;
			}
		}
	    });
	
	for (;;)
	{
	    Thread.sleep(50);
	    
	    if (camera_to_rotation)
	    {
		transform(on_inv(Pm));
		camera_to_rotation = false;
		sreset = true;
	    }
	    
	    if (sreset)
	    {
		Pm = UNIT;
		for (int i = 8; i < 8 + 6; i++)
		    xs[i] = ys[i] = zs[i] = 0;
		xs[8]  = +1;  xs[11] = -1;
		ys[9]  = +1;  ys[12] = -1;
		zs[10] = +1;  zs[13] = -1;
		sreset = false;
	    }
	    
	    if (zreset)
	    {
		Qm = UNIT;
		for (int i = 8 + 6 + 3; i < 8 + 6 + 3 + 3; i++)
		    xs[i] = ys[i] = zs[i] = 0;
		xs[17] = +1;
		ys[18] = +1;
		zs[19] = +1;
		zreset = false;
	    }
	    
	    if (rotation_to_camera)
	    {
		stransform(resetRotation());
		rotation_to_camera = false;
	    }
	    
	    if (rx * rx == 1)
	    {
		float[] v = mul(rx * SPEED, createRotation("x", Pm));
		v = add(mul(rotation_speed, rotation), v);
		rotation_speed = length(v);
		rotation = normalise(v);
	    }
	    
	    if (ry * ry == 1)
	    {
		float[] v = mul(ry * SPEED, createRotation("y", Pm));
		v = add(mul(rotation_speed, rotation), v);
		rotation_speed = length(v);
		rotation = normalise(v);
	    }
	    
	    if (rxy * rxy == 1)
	    {
		float[] v = mul(rxy * SPEED, createRotation("xy", Pm));
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
	    
	    if (rx  * rx  == 4)  transform(createRotation("x",  45 * rx  / 2));
            if (ry  * ry  == 4)  transform(createRotation("y",  45 * ry  / 2));
            if (rxy * rxy == 4)  transform(createRotation("xy", 45 * rxy / 2));
	    if (rx  * rx  == 4)  rx = 0;
	    if (ry  * ry  == 4)  ry = 0;
	    if (rxy * rxy == 4)  rxy = 0;
	    
	    if (srx  * srx  == 1)  stransform(createRotation("x",  srx  * SSPEED));
	    if (sry  * sry  == 1)  stransform(createRotation("y",  sry  * SSPEED));
	    if (srxy * srxy == 1)  stransform(createRotation("xy", srxy * SSPEED));
	    
	    if (srx  * srx  == 4)  stransform(createRotation("x",  45 * srx  / 2));
            if (sry  * sry  == 4)  stransform(createRotation("y",  45 * sry  / 2));
            if (srxy * srxy == 4)  stransform(createRotation("xy", 45 * srxy / 2));
	    if (srx  * srx  == 4)  srx = 0;
	    if (sry  * sry  == 4)  sry = 0;
	    if (srxy * srxy == 4)  srxy = 0;
	    
	    if (zrx  * zrx  == 1)  ztransform(createRotation("x",  zrx  * SSPEED));
	    if (zry  * zry  == 1)  ztransform(createRotation("y",  zry  * SSPEED));
	    if (zrxy * zrxy == 1)  ztransform(createRotation("xy", zrxy * SSPEED));
	    
	    if (zrx  * zrx  == 4)  ztransform(createRotation("x",  45 * zrx  / 2));
            if (zry  * zry  == 4)  ztransform(createRotation("y",  45 * zry  / 2));
            if (zrxy * zrxy == 4)  ztransform(createRotation("xy", 45 * zrxy / 2));
	    if (zrx  * zrx  == 4)  zrx = 0;
	    if (zry  * zry  == 4)  zry = 0;
	    if (zrxy * zrxy == 4)  zrxy = 0;
	    
	    panel.repaint();
	}
    }
    
    
    public static float[][] resetRotation()
    {
	float[][] r = new float[3][3];
	
	r[0][0] = xs[14];
	r[1][0] = ys[14];
	r[2][0] = zs[14];
	
	r[0][1] = xs[15];
	r[1][1] = ys[15];
	r[2][1] = zs[15];
	
	r[0][2] = xs[16];
	r[1][2] = ys[16];
	r[2][2] = zs[16];
	
	for (int i = 0; i < 8; i++)
	{
	    xs[i] = ((i & 1) != 0 ? 1f : 0f) - 0.5f;
	    ys[i] = ((i & 2) != 0 ? 1f : 0f) - 0.5f;
	    zs[i] = ((i & 4) != 0 ? 1f : 0f) - 0.5f;
	}
	xs[14] = xs[15] = xs[16] = 0;
	ys[14] = ys[15] = ys[16] = 0;
	zs[14] = zs[15] = zs[16] = 0;
	xs[14] = ys[15] = zs[16] = 1;
	
	normal_xs = new float[] {0f, 0f, 1f, -1f,  0f,  0f};
	normal_ys = new float[] {1f, 0f, 0f,  0f,  0f, -1f};
	normal_zs = new float[] {0f, 1f, 0f,  0f, -1f,  0f};
	
	return r;
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
	
	for (int i = 8 + 6; i < 8 + 6 + 3; i++)
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
    
    public static void stransform(float[][] m)
    {
	for (int i = 8; i < 8 + 6; i++)
	{
	    float[] v = mul(m, xs[i], ys[i], zs[i]);
	    xs[i] = v[0];
	    ys[i] = v[1];
	    zs[i] = v[2];
	}
	
	if (Pm == UNIT)
	{
	    Pm = new float[3][3];
	    for (int i = 0; i < 3; i++)
		for (int j = 0; j < 3; j++)
		    Pm[i][j] = UNIT[i][j];
	}
	
	Pm[0][0] = xs[8];
	Pm[1][0] = ys[8];
	Pm[2][0] = zs[8];
	
	Pm[0][1] = xs[9];
	Pm[1][1] = ys[9];
	Pm[2][1] = zs[9];
	
	Pm[0][2] = xs[10];
	Pm[1][2] = ys[10];
	Pm[2][2] = zs[10];
    }
    
    public static void ztransform(float[][] m)
    {
	for (int i = 8 + 6 + 3; i < 8 + 6 + 3 + 3; i++)
	{
	    float[] v = mul(m, xs[i], ys[i], zs[i]);
	    xs[i] = v[0];
	    ys[i] = v[1];
	    zs[i] = v[2];
	}
	
	if (Qm == UNIT)
	{
	    Qm = new float[3][3];
	    for (int i = 0; i < 3; i++)
		for (int j = 0; j < 3; j++)
		    Qm[i][j] = UNIT[i][j];
	}
	
	Qm[0][0] = xs[17];
	Qm[1][0] = ys[17];
	Qm[2][0] = zs[17];
	
	Qm[0][1] = xs[18];
	Qm[1][1] = ys[18];
	Qm[2][1] = zs[18];
	
	Qm[0][2] = xs[19];
	Qm[1][2] = ys[19];
	Qm[2][2] = zs[19];
    }
    
}

