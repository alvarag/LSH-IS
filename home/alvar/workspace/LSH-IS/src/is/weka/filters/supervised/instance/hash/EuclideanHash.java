/*
 * This file is part of Instance Selection Library.
 * 
 * Instance Selection Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Instance Selection Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Instance Selection Library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * EuclideanHash.java
 * Copyright (C) 2016 Universidad de Burgos
 */
package weka.filters.supervised.instance.hash;

import java.io.Serializable;
import java.util.Random;

import weka.core.Instance;

/**
 * <b>Descripción</b><br>
 * Hash class for Euclidean distance.
 * <p>
 * </p>
 * 
 * @author Álvar Arnaiz González
 * @version 1.1
 */
public class EuclideanHash implements Serializable {

	/**
	 * For serialization 
	 */
	private static final long serialVersionUID = -1350707348519781796L;
	
	/**
	 * Coordinates for random projection array.
	 */
	private Double mRandomProjection[];
	
	/**
	 * Array's offset.
	 */
	private double mOffset;
	
	/**
	 * Width of the bucket.
	 */
	private double mW;
	
	/**
	 * Default constructor.
	 * 
	 * @param dimensions Dimensions of the array.
	 * @param w width of the bucket.
	 * @param seed for random generator.
	 */
	public EuclideanHash(int dimensions, double w, long seed){
		Random rand = new Random(seed);
		mW = w;
		
		if (w < 1.0)
			this.mOffset = (rand.nextInt((int)(w*10))/10.0);
		else
			this.mOffset = rand.nextInt((int)w);
		
		mRandomProjection = new Double[dimensions];
		
		for(int d=0; d<dimensions; d++) {
			//mean 0
			//standard deviation 1.0
			double val = rand.nextGaussian();
			mRandomProjection[d] = val;
		}
	} // EuclideanHash
	
	/**
	 * Computes the hash code for an instance.
	 * 
	 * @param inst Instance.
	 * @return Hash Hash code for inst.
	 */
	public int hash(Instance inst){
		double sum = 0.0, hashValue;
		
		for(int i=0; i < mRandomProjection.length; i++)
			sum += mRandomProjection[i] * inst.value(i);

		hashValue = (sum+mOffset)/Double.valueOf(mW);
		
		return (int) Math.round(hashValue);
	} // hash
	
} // EuclideanHash
