package de.fhkl.imst.i.cgma.raytracer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import de.fhkl.imst.i.cgma.raytracer.file.I_Sphere;
import de.fhkl.imst.i.cgma.raytracer.file.RTFile;
import de.fhkl.imst.i.cgma.raytracer.file.RTFileReader;
import de.fhkl.imst.i.cgma.raytracer.file.RT_Object;
import de.fhkl.imst.i.cgma.raytracer.file.T_Mesh;
import de.fhkl.imst.i.cgma.raytracer.gui.IRayTracerImplementation;
import de.fhkl.imst.i.cgma.raytracer.gui.RayTracerGui;

public class Raytracer03 implements IRayTracerImplementation {
	// viewing volume with infinite end
	private float fovyDegree;
	private float near;
	private float fovyRadians;

	// one hardcoded point light as a minimal solution :-(
	private float[] Ia = { 0.25f, 0.25f, 0.25f }; // ambient light color
	private float[] Ids = { 1.0f, 1.0f, 1.0f }; // diffuse and specular light
	// color
	private float[] ICenter = { 4.0f, 4.0f, 2.0f }; // center of point light

	RayTracerGui gui = new RayTracerGui(this);

	private int resx, resy; // viewport resolution
	private float h, w, aspect; // window height, width and aspect ratio

	Vector<RT_Object> objects;

	private Raytracer03() {
		try {

			// gui.addObject(RTFileReader.read(I_Sphere.class, new
			// File("data/ikugel.dat")));

			gui.addObject(RTFileReader.read(T_Mesh.class, new File("data/kugel1.dat")));
//			gui.addObject(RTFileReader.read(T_Mesh.class, new File("data/dreieck2.dat")));

			
			objects = gui.getObjects();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setViewParameters(float fovyDegree, float near) {
		// set attributes fovyDegree, fovyRadians, near
		this.fovyDegree = fovyDegree;
		this.fovyRadians = (float) Math.PI * fovyDegree / 180;
		this.near = near;

		// set attributes resx, resy, aspect
		this.resx = gui.getResX();
		this.resy = gui.getResY();
		this.aspect = ((float) resx) / (float) (resy);

		// set attributes h, w
		this.h = (float) (2 * this.near * Math.tan(this.fovyRadians / 2));
		this.w = h * aspect;
	}

	@Override
	public void doRayTrace() {
		float x, y, z; // intersection point in viewing plane
		float rayEx, rayEy, rayEz; // eye point==ray starting point
		float rayVx, rayVy, rayVz; // ray vector
		Color color = Color.RED;

		y = 0;

		rayEx = 0;
		rayEy = 0;
		rayEz = 0;

		// hardcoded viewing volume with fovy and near
		setViewParameters(90.0f, 1.0f);

		// set eye point
		z = -near;

		// xp, yp: pixel coordinates
		for (int xp = 0; xp < resx; ++xp) {
			for (int yp = 0; yp < resy; ++yp) {

				x = (xp * w / (resx - 1)) - w / 2;
				y = (((resy - 1 - yp) * h / (resy - 1)) - h / 2);

				// ray vector
				rayVx = x - rayEx;
				rayVy = y - rayEy;
				rayVz = z - rayEz;

				// for demo purposes
				// gui.setPixel(xp, yp, Color.WHITE.getRGB());
				color = traceRayAndGetColor(rayEx, rayEy, rayEz, rayVx, rayVy, rayVz);
				if (color != null) {
					gui.setPixel(xp, yp, color.getRGB());
				}
			}
		}
	}

	// returns Color object or null if no intersection was found
	private Color traceRayAndGetColor(float rayEx, float rayEy, float rayEz, float rayVx, float rayVy, float rayVz) {
		// RTFile scene = gui.getFile();

		float minT = Float.MAX_VALUE;
		int minObjectsIndex = -1;
		int minIndex = -1;
		float[] minIP = new float[3];
		float[] minN = new float[3];
		float[] minMaterial = new float[3];
		float minMaterialN = 1;

		float[] v = new float[3];
		float[] l = new float[3];

		// viewing vector at intersection point
		v[0] = -rayVx;
		v[1] = -rayVy;
		v[2] = -rayVz;
		normalize(v);

		RTFile scene;
		I_Sphere sphere;
		T_Mesh mesh;

		// loop over all scene objects to find the nearest intersection, that
		// is:
		// object with number minObjectIndex
		// minT is the minimal factor t of the ray equation s(t)=rayE+t*rayV
		// where the nearest intersection takes place
		for (int objectsNumber = 0; objectsNumber < objects.size(); objectsNumber++) {
			scene = objects.get(objectsNumber);

			// object is an implicit sphere?
			if (scene instanceof I_Sphere) {
				sphere = (I_Sphere) scene;


				float t;

				// ray intersection uses quadratic equation
				/// float a, b, c, d;
				float a = rayVx * rayVx + rayVy * rayVy + rayVz * rayVz;
				float b = 2 * (rayVx * (rayEx - sphere.center[0]) + rayVy * (rayEy - sphere.center[1])
						+ rayVz * (rayEz - sphere.center[2]));
				float c = ((rayEx - sphere.center[0]) * (rayEx - sphere.center[0])
						+ (rayEy - sphere.center[1]) * (rayEy - sphere.center[1])
						+ (rayEz - sphere.center[2]) * (rayEz - sphere.center[2])) - sphere.radius * sphere.radius;

				// positive discriminant determines intersection
				float d = b * b - 4 * a * c;
				// no intersection point? => next object
				if (d <= 0)
					continue;

				// from here: intersection takes place!

				// calculate first intersection point with sphere along the
				// ray
				t = (float) ((-b - Math.sqrt(d)) / (2 * a));

				// already a closer intersection point? => next object
				if (t >= minT)
					continue;

				// from here: t < minT
				// I'm the winner until now!

				minT = t;
				minObjectsIndex = objectsNumber;

				// prepare everything for phong shading
				// the intersection point
				minIP[0] = rayEx + minT * rayVx;
				minIP[1] = rayEy + minT * rayVy;
				minIP[2] = rayEz + minT * rayVz;

				// the normal vector at the intersection point
				minN[0] = minIP[0] - sphere.center[0];
				minN[1] = minIP[1] - sphere.center[1];
				minN[2] = minIP[2] - sphere.center[2];
				normalize(minN);

				// the material
				minMaterial = sphere.material;
				minMaterialN = sphere.materialN;

			} else if (scene instanceof T_Mesh) {
				mesh = (T_Mesh) scene;

				float t;
				float[] n;
				float[] ip = new float[3];

				float a, rayVn, pen;
				float[] p1, p2, p3;
				float[] ai = new float[3];

				// loop over all triangles
				for (int i = 0; i < mesh.triangles.length; i++) {
					// get the three vertices
					p1 = mesh.vertices[mesh.triangles[i][0]];
				    p2 = mesh.vertices[mesh.triangles[i][1]];
				    p3 = mesh.vertices[mesh.triangles[i][2]];

					// intermediate version
					// calculate normal n and triangle area a
				    n = new float[3];
				    a = calculateN(n, p1, p2, p3);

				    rayVn =  rayVx*n[0]+ rayVy*n[1]+ rayVz*n[2];
				    
				    // backface? => next triangle
				    if (rayVn >= 0)
					continue;	    

				    // no intersection point? => next triangle
				    if (Math.abs(rayVn) < 1E-7)
					continue;

					pen = (p1[0]  -rayEx) * n[0] + (p1[1] - rayEy)  *n[1] + (p1[2] - rayEz) * n[2];

					// calculate intersection point with plane along the ray
					// rayVn = v* n

					// Fall 1 : v * n nicht senkrecht -> rayVn != 0
					t = pen / rayVn;

					// already a closer intersection point? => next triangle
					if (t >= minT)
						continue;

					// the intersection point with the plane
					ip[0] = rayEx + t * rayVx;
					ip[1] = rayEy + t * rayVy;
					ip[2] = rayEz + t * rayVz;

					// no intersection point with the triangle? => next
					// triangle
					if (!triangleTest(ip, p1, p2, p3, a, ai))
						continue;

					// from here: t < minT and triangle intersection
					// I'm the winner until now!

					minT = t;
					minObjectsIndex = objectsNumber;
					minIndex = i;

					// prepare everything for shading alternatives

					// the intersection point
					minIP[0] = ip[0];
					minIP[1] = ip[1];
					minIP[2] = ip[2];

					switch (mesh.fgp) {
					case 'f':
					case 'F':

						// the normal is the surface normal
						minN[0] = n[0];
						minN[1] = n[1];
						minN[2] = n[2];

						// the material is the material of the first triangle point
						int matIndex = mesh.verticesMat[mesh.triangles[minIndex][0]];
						minMaterial = mesh.materials[matIndex];
						minMaterialN = mesh.materialsN[matIndex];

						break;

					}
				}
			} else
				continue; // return null;
		}

		// no intersection point found => return with no result
		if (minObjectsIndex == -1)
			return null;

		// light vector at the intersection point
		l[0] = ICenter[0] - minIP[0];
		l[1] = ICenter[1] - minIP[1];
		l[2] = ICenter[2] - minIP[2];
		normalize(l);

		// decide which shading model will be applied

		// implicit: only phong shading available => shade=illuminate
		if (objects.get(minObjectsIndex) instanceof I_Sphere)
			return phongIlluminate(minMaterial, minMaterialN, l, minN, v, Ia, Ids);

		// triangle mesh: flat, gouraud or phong shading according to file data
		else if (objects.get(minObjectsIndex).getHeader() == "TRIANGLE_MESH") {
			mesh = ((T_Mesh) objects.get(minObjectsIndex));
			switch (mesh.fgp) {
			case 'f':
			case 'F':
				// illumination can be calculated here
				// this is a variant between flat und phong shading
				return phongIlluminate(minMaterial, minMaterialN, l, minN, v, Ia, Ids);

			}
		}

		return null;
		// // intermediate version
		// Random rd = new Random();
		// return new Color(rd.nextFloat(), rd.nextFloat(), rd.nextFloat());

	}

	// calculate phong illumination model with material parameters material and
	// materialN, light vector l, normal vector n, viewing vector v, ambient
	// light Ia, diffuse and specular light Ids
	// return value is a new Color object
	private Color phongIlluminate(float[] material, float materialN, float[] l, float[] n, float[] v, float[] Ia,
			float[] Ids) {
		float ir = 0, ig = 0, ib = 0; // reflected intensity, rgb channels
		float[] r = new float[3]; // reflection vector
		float ln, rv; // scalar products <l,n> and <r,v>

		// <l,n>
		ln = n[0] * l[0] + n[1] * l[1] + n[2] * l[2];

		// ambient component, Ia*ra
		ir += Ia[0] * material[0];
		ig += Ia[1] * material[1];
		ib += Ia[2] * material[2];

		// diffuse component, Ids*rd*<l,n>
		if (ln > 0) {
			ir += Ids[0] * material[3] * ln;
			ig += Ids[1] * material[4] * ln;
			ib += Ids[2] * material[5] * ln;

			// reflection vector r=2*<l,n>*n-l
			r[0] = 2 * Math.max(0, ln) * n[0] - l[0];
			r[1] = 2 * Math.max(0, ln) * n[1] - l[1];
			r[2] = 2 * Math.max(0, ln) * n[2] - l[2];
			normalize(r);
			
			// <r,v>
			rv = r[0] * v[0] + r[1] * v[1] + r[2] * v[2];

			// specular component, Ids*rs*<r,v>^n
			if (rv > 0) {
				float pow =(float) Math.pow(Math.max(0, rv), materialN);
				ir += Ids[0] * material[6] * pow;
				ig += Ids[1] * material[7] * pow;
				ib += Ids[2] * material[8] * pow;
			}
		}

		ir = ir > 1 ? 1 : ir;
		ig = ig > 1 ? 1 : ig;
		ib = ib > 1 ? 1 : ib;

		//System.out.println(ir + " " + ig + " " + ib);
		return new Color(ir, ig, ib);
	}

	// calculate normalized face normal fn of the triangle p1, p2 and p3
	// the return value is the area of triangle
	// CAUTION: fn is an output parameter; the referenced object will be
	// altered!
	private float calculateN(float[] fn, float[] p1, float[] p2, float[] p3) {
		float ax, ay, az, bx, by, bz;

		// a = Vi2-Vi1, b = Vi3-Vi1

		ax = p2[0] - p1[0];
		ay = p2[1] - p1[1];
		az = p2[2] - p1[2];

		// n =a x b

		bx = p3[0] - p1[0];
		by = p3[1] - p1[1];
		bz = p3[2] - p1[2];

		// normalize n, calculate and return area of triangle
		fn[0] = ay * bz - az * by;
		fn[1] = az * bx - ax * bz;
		fn[2] = ax * by - ay * bx;

		return normalize(fn) / 2;
	}

	// calculate triangle test
	// is p (the intersection point with the plane through p1, p2 and p3) inside
	// the triangle p1, p2 and p3?
	// the return value answers this question
	// a is an input parameter - the given area of the triangle p1, p2 and p3
	// ai will be computed to be the areas of the sub-triangles to allow to
	// compute barycentric coordinates of the intersection point p
	// ai[0] is associated with bu (p1p2p) across from p3
	// ai[1] is associated with bv (pp2p3) across from p1
	// ai[2] is associated with bw (p1pp3) across form p2
	// CAUTION: ai is an output parameter; the referenced object will be
	// altered!
	private boolean triangleTest(float[] p, float[] p1, float[] p2, float[] p3, float a, float ai[]) {
		float tmp[] = new float[3];
		
		ai[0] = calculateN(tmp,p1,p2,p);
		ai[1] = calculateN(tmp,p,p2,p3);
		ai[2] = calculateN(tmp,p1,p,p3);

		if( Math.abs(ai[0] + ai[1] + ai[2] - a) < 1E-5 )
			return true;

		return false;
	}

	// vector normalization
	// CAUTION: vec is an in-/output parameter; the referenced object will be
	// altered!
	private float normalize(float[] vec) {
		float l;

		float[] v = Arrays.copyOf(vec, vec.length);

		v[0] = v[0] * v[0];
		v[1] = v[1] * v[1];
		v[2] = v[2] * v[2];

		l = (float) Math.sqrt(v[0] + v[1] + v[2]);

		if(l < 1.E-07) {
			return 0f;
		}
		
		vec[0] = (1 / l) * vec[0];
		vec[1] = (1 / l) * vec[1];
		vec[2] = (1 / l) * vec[2];

		return l;
	}

	public static void main(String[] args) {
		Raytracer03 rt = new Raytracer03();

		rt.doRayTrace();
	}
}