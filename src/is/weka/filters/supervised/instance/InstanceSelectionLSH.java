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
 * InstanceSelectionLSH.java
 * Copyright (C) 2016 Universidad de Burgos
 */
package weka.filters.supervised.instance;

import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;
import weka.filters.supervised.instance.hash.EuclideanHashTable;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import main.core.util.InstanceIS;

/**
 * <b>Descripción</b><br>
 * Filter for instance selection algorithm based on LSH: families AND - OR.
 * <br>
 * Please, cite the code with:<br>
 * Arnaiz-González, Á., Díez-Pastor, J. F., Rodríguez, J. J., García-Osorio, C. (2016). 
 * Instance selection of linear complexity for big data. Knowledge-Based Systems.
 * <p>
 * </p>
 * @author Álvar Arnaiz González
 * @version 1.1
 */
public class InstanceSelectionLSH extends Filter implements SupervisedFilter, OptionHandler, InstanceSelectionFilterIF {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 8190462114579437586L;
	
	/**
	 * CPU filtering time.
	 */
	protected long mCPUTimeElapsed;
	
	/**
	 * User filtering time.
	 */
	protected long mUserTimeElapsed;
	
	/**
	 * Filtered data set.
	 */
	protected Instances mFilteredDataset;
	
	/**
	 * Number of tables to use in OR combination.
	 */
	protected int mFunctionsO = 4;
	
	/**
	 * Number of tables to use in AND combination.
	 */
	protected int mFunctionsY = 10;
	
	/**
	 * Bucket's width. If the data set is normalized, don't change it. 
	 */
	protected double mW = 1.0;
	
	/** 
	 * The random number generator seed.
	 */
	protected long m_RandomSeed = 1;

	/**
	 * Type of LSH-IS to use.
	 */
	protected int mTypeOfLSHIS = TYPE_ONE_OF_EACH_CLASS;
		
	/**
	 * One instance of each class in every single bucket.
	 */
	public static final int TYPE_ONE_OF_EACH_CLASS = 0;
	
	/**
	 * If the bucket has more than one class: removes if there's only one instance of one class in the bucket.
	 */
	public static final int TYPE_FILTER_ONE_INST_CLASS_BUCKET= 1;
	
	/**
	 * Algoritmos implementados.
	 */
	public static final Tag[] TAGS_LSH_IS_TYPE = {new Tag (TYPE_ONE_OF_EACH_CLASS, "One instance of each class in each bucket"),
	                                              new Tag (TYPE_FILTER_ONE_INST_CLASS_BUCKET, "Only remove in buckets with one class")};
	
	/** 
	 * List of hash tables.
	 */
	private List<EuclideanHashTable> mHashTable; 

	/**
	 * Constructor por defecto.
	 */
	public InstanceSelectionLSH () {
		super();
	} // InstanceSelectionLSH
	
	public void setLSHISType (SelectedTag value) {
		if (value.getTags() == TAGS_LSH_IS_TYPE)
			mTypeOfLSHIS = value.getSelectedTag().getID();
	} // setLSHISType

	public SelectedTag getLSHISType () {
		
		return new SelectedTag(mTypeOfLSHIS, TAGS_LSH_IS_TYPE);
	} // getLSHISType
	
	public String distanceTipText () {
		
		return "Type of LSH-IS Algorithm to use.";
	} // typeTipText
	
	public int getNumberFunctionsY () {
		
		return mFunctionsY;
	} // getNumberFunctionsY
	
	public void setNumberFunctionsY (int num) {
		mFunctionsY = num;
	} // setNumberFunctionsY
	
	public String nmberFunctionsYTipText () {
		
		return "AND functions number to used.";
	} // nmberFunctionsYTipText

	public int getNumberFunctionsO () {
		
		return mFunctionsO;
	} // getNumberFunctionsO
	
	public void setNumberFunctionsO (int num) {
		mFunctionsO = num;
	} // setNumberFunctionsO
	
	public String numberFunctionsOTipText () {
		
		return "OR functions number to used.";
	} // numberFunctionsOTipText

	/**
	 * Devuelve el radio.
	 * 
	 * @return Radio.
	 */
	public double getW () {
		
		return mW;
	} // getW
	
	public void setW (double radius) {
		mW = radius;
	} // setW
	
	public String radiusTipText () {
		
		return "Bucket's width to used.";
	} // radiusTipText
	
	public String randomSeedTipText() {
		return "Sets the random number seed for LSH functions.";
	}

	public long getRandomSeed() {
		return m_RandomSeed;
	}

	public void setRandomSeed(long newSeed) {
		m_RandomSeed = newSeed;
	}

	public String[] getOptions () {
		Vector<String> result = new Vector<String>();
		
		result.add("-L");
		result.add("" + getLSHISType());
		
		result.add("-Y");
		result.add("" + getNumberFunctionsY());
		
		result.add("-O");
		result.add("" + getNumberFunctionsO());
		
		result.add("-W");
		result.add("" + getW());
		
		result.add("-S");
		result.add("" + getRandomSeed());
		
		return result.toArray(new String[result.size()]); 
	} // getOptions

	public Enumeration<Option> listOptions () {
		Vector<Option> newVector = new Vector<Option>();
		
		newVector.addElement(new Option("\tSpecifies the number of hashes of each table\n" +
		                                "\t(default 10)", "Y", 1, "-F <num>"));

		newVector.addElement(new Option("\tSpecifies the number of hash tables\n" +
		                                "\t(default 4)", "O", 1, "-H <num>"));

		newVector.addElement(new Option("\tSpecifies the radius\n" +
		                                "\t(default 1.0)", "W", 1, "-W <double>"));

		newVector.addElement(new Option("\tSpecifies the random seed\n" + 
		                                "\t(default 1.0)", "S", 1, "-S <long>"));

		newVector.addElement(new Option("\tSet type of LSH-IS (default: 0)\n"+
		                                "\t\t 0 = Maintains one instance of each class in each bucket\n"+
		                                "\t\t 1 = Removes instances in buckets where are only one instance of this class\n",
		                                "L", 0, "-L <int>"));
		
		return newVector.elements();
	} // listOptions

	public void setOptions (String[] options) throws Exception {
		String numStr = Utils.getOption('S', options);
		
		if (numStr.length() != 0)
			setRandomSeed(Integer.parseInt(numStr));
		else
			setRandomSeed(1);

		numStr = Utils.getOption('O', options);
		
		if (numStr.length() != 0)
			setNumberFunctionsO(Integer.parseInt(numStr));
		else
			setNumberFunctionsO(4);
		
		numStr = Utils.getOption('Y', options);
		
		if (numStr.length() != 0)
			setNumberFunctionsY(Integer.parseInt(numStr));
		else
			setNumberFunctionsY(4);
	    
		numStr = Utils.getOption('W', options);
		
		if (numStr.length() != 0)
			setW(Double.parseDouble(numStr));
		else
			setW(1.0);
		
		String tmpStr = Utils.getOption('L', options);

	    if (tmpStr.length() != 0)
	    	setLSHISType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_LSH_IS_TYPE));
	    else
	    	setLSHISType(new SelectedTag(TYPE_ONE_OF_EACH_CLASS, TAGS_LSH_IS_TYPE));
	} // setOptions

	public boolean setInputFormat (Instances instanceInfo) throws Exception {
		super.setInputFormat(instanceInfo);
		super.setOutputFormat(instanceInfo);
	    
		return true;
	} // setInputFormat

	/**
	 * Adds a new instance to the filter.
	 * The filter needs all instances before starting with the filter(editing) process.
	 *
	 * @param instance Instancia de entrada.
	 * @return Verdadero si la instancia puede ser introducida al filtro.
	 * @throws IllegalStateException Si no se ha definido la estructura de entrada de las instancias.
	 */
	public boolean input (Instance instance) {
		if (getInputFormat() == null)
			throw new IllegalStateException("No input instance format defined");
		
		// New batch
		if (m_NewBatch) {
			resetQueue();
			m_NewBatch = false;
		}
		
		// Whether a new batch has already performed
		if (m_FirstBatchDone) {
			push(instance);
			return true;
		}
		else {
			bufferInput(instance);
			return false;
		}
	} // input

	public boolean batchFinished () throws Exception {
		// Si no se dispone de la cabecera.
		if (getInputFormat() == null)
			throw new IllegalStateException("No input instance format defined");
		
		// Realizar la selección de instancias.
		if (!m_FirstBatchDone)
			filter(getInputFormat());
		
		flushInput();

		m_NewBatch = true;
		m_FirstBatchDone = true;
		
		return (numPendingOutput() != 0);
	} // batchFinished
	
	/**
	 * Performs the instance selection process.
	 * 
	 * @param instances Data set to filter.
	 */
	public void filter (Instances instances) {
		ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
		boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
		
		// Crear el conjunto de datos filtrado.
		mFilteredDataset = new Instances (instances, instances.numInstances());
		
		// Si se puede medir la CPU
		if(canMeasureCPUTime && !thMonitor.isThreadCpuTimeEnabled())
			thMonitor.setThreadCpuTimeEnabled(true);
		
		long thID = Thread.currentThread().getId();
		long CPUStartTime=-1, userTimeStart;
		
		userTimeStart = System.currentTimeMillis();
		
		if(canMeasureCPUTime)
			CPUStartTime = thMonitor.getThreadUserTime(thID);

		// -----------------------------------------------Starts the LSH-IS
		Random r = new Random(m_RandomSeed);
		int[] classes = new int[instances.classAttribute().numValues()];
		mHashTable = new ArrayList<EuclideanHashTable>();
		
		for(int i = 0 ; i < mFunctionsO ; i++ )
			mHashTable.add(new EuclideanHashTable(mFunctionsY, instances.numAttributes() - 1, mW, r.nextLong()));

		// Depending on the type.
		switch (mTypeOfLSHIS) {
			// One instance of each class in each bucket.
			case TYPE_ONE_OF_EACH_CLASS:
				// First step
				mFilteredDataset.add(instances.firstInstance());
				
				for (EuclideanHashTable table : mHashTable)
					table.add(instances.firstInstance());
				
				// All steps
				for (int i = 1; i < instances.numInstances(); i++) {
					// Añadir la instancia a la solución y el vector al índice.
					if (oneInstanceOfEachClass(instances.instance(i))) {
						for (EuclideanHashTable table : mHashTable)
							table.add(instances.instance(i));
						
						mFilteredDataset.add(instances.instance(i));
					}
				}
				break;
			// One instance of each class in each bucket but with an advantage:
			// - First pass: all instances are accumulated in the hash table.
			// - Second pass: selects one instance in each bucket with one exception: if there's more
			//   than one class in the bucket but there's only one instance of that class -> it's 
			//   considered as noise. 
			case TYPE_FILTER_ONE_INST_CLASS_BUCKET:
				// First pass.
				for (int i = 0; i < instances.numInstances(); i++)
					for (EuclideanHashTable table : mHashTable)
						table.add(instances.instance(i));

				// Second pass.
				for (EuclideanHashTable table : mHashTable) {
					for (List<Instance> list : table.getHashTable().values()) {
						// If there's only instances of one class -> selects one randomly.
						if (countInstPerClass (list, classes) == 1) {
							mFilteredDataset.add(list.get(0));
						}
						// If there's more than one class.
						else {
							for (Instance inst : list) {
								// Select one randomly if there's more than one instance of that class.
								if (classes[(int)inst.classValue()] > 1) {
									mFilteredDataset.add(inst);
									classes[(int)inst.classValue()] = 0;
								}
							}
						}
					}
				}
				break;
		}
		
		// Remove duplicated instances.
		InstanceIS.removeDuplicateInstances(mFilteredDataset);
		for (int i = 0; i < mFilteredDataset.numInstances(); i++)
			push(mFilteredDataset.instance(i));
		// -----------------------------------------------End of the process
		
		if(canMeasureCPUTime)
			mCPUTimeElapsed = (thMonitor.getThreadUserTime(thID) - CPUStartTime) / 1000000;
		
		mUserTimeElapsed = System.currentTimeMillis() - userTimeStart;
		
		thMonitor = null;
	} // filter
	
	/**
	 * LSH-IS: one instance of each class in each bucket.
	 * 
	 * @param inst Instance for checking.
	 * @return True if it must be retained, false otherwise.
	 */
	private boolean oneInstanceOfEachClass (Instance test) {
		boolean add;
		
		for (EuclideanHashTable hashTable : mHashTable) {
			add = true;
			
			for (Instance d : hashTable.query(test))
				if (d.classValue() == test.classValue())
					add = false;
			
			if (add)
				return true;
		}

		return false;
	} // oneInstanceOfEachClass
	
	/**
	 * Computes the number of instances of each class.
	 * 
	 * @param list Instances.
	 * @param classes Array with the number of instances of each class.
	 * @return Number of different classes of the list.
	 */
	private int countInstPerClass (List<Instance> list, int[] classes) {
		int numClasses = 0;

		for (int i = 0; i < classes.length; i++)
			classes[i] = 0;
		
		// Contar el número de instancias de cada clase.
		for (Instance inst : list)
			classes[(int)inst.classValue()]++;
		
		// Contar el número de clases que hay en el bucket.
		for (int c : classes)
			if (c != 0)
				numClasses++;
		
		return numClasses;
	} // countInstPerClass

	/**
	 * Returns the Capabilities of this filter.
	 * 
	 * @return the capabilities of this object
	 * @see Capabilities
	 */
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		
		// class
		result.disable(Capability.NUMERIC_CLASS);
		result.disable(Capability.DATE_CLASS);
		result.enable(Capability.NOMINAL_CLASS);

		return result;
	} // getCapabilities
	
	public long getFilterCPUTime () {
	
		return mCPUTimeElapsed;
	} // getFilterCPUTime
	
	public long getFilterUserTime () {
	
		return mUserTimeElapsed;
	} // getFilterUserTime

	public Instances getSolutionSet() {
		
		return mFilteredDataset;
	} // getSolutionSet

} // InstanceSelectionLSH
