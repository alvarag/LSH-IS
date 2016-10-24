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
 * HashTable.java
 * Copyright (C) 2016 Universidad de Burgos
 */
package weka.filters.supervised.instance.hash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import weka.core.Instance;

/**
 * <b>Descripción</b><br>
 * Index for hash's functions.
 * <p>
 * </p>
 * 
 * @author Álvar Arnaiz González
 * @version 1.1
 */
public class EuclideanHashTable implements Serializable {

	private static final long serialVersionUID = -5410017645908038641L;

	/**
	 * Map with the buckets of the hash.
	 */
	private HashMap<Integer,List<Instance>> mHashTable;
	
	/**
	 * Array of hash functions.
	 */
	private EuclideanHash[] mHashFunctions;
	
	/**
	 * Initializes the table of hash functions.
	 * 
	 * @param numberOfHashes Number of hash functions to use.
	 * @param dimensions Dimension of each function.
	 * @param w Width of the bucket. 
	 */
	public EuclideanHashTable(int numberOfHashes, int dimensions, double w, long seed){
		Random rand = new Random(seed);
		mHashTable = new HashMap<Integer, List<Instance>>();
		
		mHashFunctions = new EuclideanHash[numberOfHashes];
		
		for(int i=0;i<numberOfHashes;i++)
			mHashFunctions[i] = new EuclideanHash(dimensions, w, rand.nextLong());
	} // HashTable

	/**
	 * Computes the combinated hash code for the instance
	 * 
	 * @param query Instance for querying.
	 * @return List of instances of the bucket, empty arraylist if there aren't any instance in the bucket.
	 */
	public List<Instance> query(Instance query) {
		Integer combinedHash = hash(query);
		
		if(mHashTable.containsKey(combinedHash))
			return mHashTable.get(combinedHash);
		else
			return new ArrayList<Instance>();
	} // query

	/**
	 * Adds the instance to the table.
	 * 
	 * @param inst Instance to add.
	 */
	public void add(Instance inst) {
		Integer combinedHash = hash(inst);
		
		if (!mHashTable.containsKey(combinedHash))
			mHashTable.put (combinedHash, new ArrayList<Instance>());
		
		mHashTable.get (combinedHash).add(inst);
	} // add
	
	/**
	 * Computes the combinated hash. AND construction.
	 * 
	 * Uses <code>Arrays.hashCode</code> for combining.
	 * 
	 * @param inst Instance to compute.
	 * @return The combined hash code.
	 */
	public int hash (Instance inst){
		int hashes[] = new int[mHashFunctions.length];
		
		for(int i = 0 ; i < mHashFunctions.length ; i++)
			hashes[i] = mHashFunctions[i].hash(inst);
		
		return Arrays.hashCode(hashes);
	} // hash
	
	/**
	 * Returns the hash table 
	 * 
	 * @return Hash Map with pairs: <code>key, List < Instance ></code>.
	 */
	public HashMap<Integer,List<Instance>> getHashTable () {
		
		return mHashTable;
	} // getHashTable
	
} // HashTable
