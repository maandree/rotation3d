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

/**
 * Orthonormalised vector maths
 */
public class VMaths
{
    public static final float[][] UNIT = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
    
    
    public static float[] createRotation(String side)
    {
	return createRotation(side, UNIT);
    }
    
    public static float[] createRotation(String side, float[][] proj)
    {
	switch (side.toLowerCase())
	{
	    case "x":
	    case "xz":
	    case "zx":
		return mul(-1, proj[1]);
	    
	    case "y":
	    case "yz":
	    case "zy":
		return mul(-1, proj[0]);
	    
	    case "xy":
	    case "yx":
		return proj[2];
	}
	
	return null;
    }
    
    public static float[][] createRotation(String side, float thetaDegrees)
    {
	switch (side.toLowerCase())
	{
	    case "x":
	    case "xz":
	    case "zx":
		return createRotation( 0f, -1f,  0f, thetaDegrees);
	    
	    case "y":
	    case "yz":
	    case "zy":
		return createRotation(-1f,  0f,  0f, thetaDegrees);
	    
	    case "xy":
	    case "yx":
		return createRotation( 0f,  0f, +1f, thetaDegrees);
	}
	
	return null;
    }
    
    public static float[][] createRotation(float x, float y, float z, float thetaDegrees)
    {
	float theta = thetaDegrees * (float)(Math.PI) / 180f;
	float[][] m = new float[3][3];
	
	float cos = (float)(Math.cos(theta));
	float sin = (float)(Math.sin(theta));
	float _cos = 1f - cos;
	
	m[0][0] = x * x * _cos + cos;
	m[1][0] = x * y * _cos + z * sin;
	m[2][0] = x * z * _cos - y * sin;
	
	m[0][1] = y * x * _cos - z * sin;
	m[1][1] = y * y * _cos + cos;
	m[2][1] = y * z * _cos + x * sin;
	
	m[0][2] = z * x * _cos + y * sin;
	m[1][2] = z * y * _cos - x * sin;
	m[2][2] = z * z * _cos + cos;
	
	return m;
    }
    
    public static float[][] createRotation(float[] v, float thetaDegrees)
    {
	return createRotation(v[0], v[1], v[2], thetaDegrees);
    }
    
    public static float[] mul(float[][] m, float[] v)
    {
	int n, k = v.length;
	float[] rc = new float[n = m.length];
	
	for (int r = 0; r < n; r++)
	    for (int i = 0; i < k; i++)
		rc[r] += m[r][i] * v[i];
	
	return rc;
    }
    
    public static float[] mul(float[][] m, float x, float y, float z)
    {
	int n;
	float[] rc = new float[n = m.length];
	
	for (int i = 0; i < n; i++)
	    rc[i] = m[i][0] * x
		  + m[i][1] * y
		  + m[i][2] * z;
	
	return rc;
    }
    
    public static float[][] mul(float[][] a, float[][] b)
    {
	int n, m, k = b.length;
	float[][] rc = new float[n = a.length][m = b[0].length];
	
	for (int r = 0; r < n; r++)
	    for (int c = 0; c < m; c++)
		for (int i = 0; i < k; i++)
		    rc[r][c] += a[i][r] * b[c][i];
	
	return rc;
    }
    
    public static float[][] mul(float a, float[][] b)
    {
	int n, m;
	float[][] rc = new float[n = b.length][m = b[0].length];
	
	for (int r = 0; r < n; r++)
	    for (int c = 0; c < m; c++)
		rc[r][c] = a * b[r][c];
	
	return rc;
    }
    
    public static float[] mul(float a, float[] b)
    {
	int n;
	float[] rc = new float[n = b.length];
	
	for (int r = 0; r < n; r++)
	    rc[r] = a * b[r];
	
	return rc;
    }
    
    public static float[][] add(float[][] a, float[][] b)
    {
	int n, m;
	float[][] rc = new float[n = a.length][m = b[0].length];
	
	for (int r = 0; r < n; r++)
	    for (int c = 0; c < m; c++)
		rc[r][c] = a[r][c] + b[r][c];
	
	return rc;
    }
    
    public static float[] add(float[] a, float[] b)
    {
	int n;
	float[] rc = new float[n = a.length];
	
	for (int r = 0; r < n; r++)
	    rc[r] = a[r] + b[r];
	
	return rc;
    }
    
    public static float length(float[] v)
    {
	int n = v.length;
	float length = 0;
	
	for (int r = 0; r < n; r++)
	    length += v[r] * v[r];
	
	length = (float)Math.sqrt(length);
	
	return length;
    }
    
    public static float[] normalise(float[] v)
    {
	int n;
	float[] rc = new float[n = v.length];
	float length = length(v);
	
	if (length * length == 0f)
	{
	    for (int r = 0; r < n; r++)
		rc[r] = 0;
	    rc[0] = 1;
	}
	else
	    for (int r = 0; r < n; r++)
		rc[r] = v[r] / length;
	
	return rc;
    }
    
    public static float[][] on_inv(float[][] m)
    {
	return trans(m);
    }
    
    public static float[][] inv(float[][] m)
    {
	return mul(1f / det(m), trans(m));
    }
    
    public static float[][] trans(float[][] m)
    {
	int x, y;
	float[][] rc = new float[y = m[0].length][x = m.length];

	for (int r = 0; r < y; r++)
            for (int c = 0; c < x; c++)
		rc[r][c] = m[c][r];
	
	return rc;
    }
    
    public static float det(float[][] m)
    {
	float rc = 0;
	int mul = 1;
	int n = m.length;
	
	if (n == 0)  return 1;
	if (n == 1)  return m[0][0];
	
	for (int i = 0; i < n; i++)
	{
	    float[][] sub = new float[n - 1][n - 1];
	    
	    for (int y = 0; y < n - 1; y++)
		for (int x = 0; x < n - 1; x++)
		    sub[y][x] = m[y + 1][x < i ? x : x + 1];
	    
	    rc += mul * det(sub);
	    mul *= -1;
	}
	
	return rc;
    }
    
}

