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
 * InstanceIS.java
 * Copyright (C) 2010 Universidad de Burgos
 */

package main.core.util;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.Vector;

import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.unsupervised.instance.Resample;

/**
 * <b>Descripción</b><br>
 * Instancia auxiliar para ayudar a los algoritmos de selección de instancias.
 * <p>
 * <b>Detalles</b><br>
 * Extiende la funcionalidad de weka.core.Instance y de weka.core.Instances.  
 * </p>
 * <p>
 * <b>Funcionalidad</b><br>
 * Implementa una instancia para los algoritmos de selección de instancias.
 * </p>
 * 
 * @author Álvar Arnáiz González
 * @version 1.3
 */
public class InstanceIS implements Serializable {
	
	/**
	 * Para la serialización.
	 */
	private static final long serialVersionUID = 2648309073056729096L;

	/**
	 * Comparador de instancias.
	 * Una instancia es igual a otra si tiene el mismo peso, el mismo número de atributos y el mismo
	 * valor.
	 * 
	 * @param i1 Primera instancia a comparar.
	 * @param i2 Segunda instancia a comparar.
	 * @return Verdadero si son iguales, falso en caso contrario.
	 */
	public static boolean equals (Instance i1, Instance i2) {
		// Comprobar el número de atributos.
		if (i1.numAttributes() != i2.numAttributes())
			return false;
		
		// Comprobar el peso.
		if (i1.weight() != i2.weight())
			return false;
		
		// Recorrer todos los atributos comparando los de ambas instancias.
		for (int i = 0; i < i1.numAttributes(); i++) {
			// Si i1 no tiene valor para el atributo e i2 si -> no son iguales.
			if (i1.isMissing(i) && !i2.isMissing(i))
				return false;
			
			// Si i2 no tiene valor para el atributo e i1 si -> no son iguales.
			if (i2.isMissing(i) && !i1.isMissing(i))
				return false;
			
			// Si ambos atributos tienen valor y no es el mismo -> no son iguales.
			if (!i2.isMissing(i) && !i1.isMissing(i)
					&& i1.value(i) != i2.value(i))
				return false;
		}
		
		return true;
	} // equals
	
	/**
	 * Devuelve un vector con las instancias del Dataset que se pasa por parámetro.
	 * 
	 * @param dataset Conjunto de instancias a pasar a un vector.
	 * @return Vector de instancias.
	 */
	public static Vector<Instance> getVectorOfInstance (Instances dataset) {
		Vector<Instance> vInstances = new Vector<Instance>(dataset.numInstances(), 0);
		
		for (int i = 0; i < dataset.numInstances(); i++)
			vInstances.add(dataset.instance(i));
		
		return vInstances;
	} // getVectorOfInstance
	
	/**
	 * Devuelve la posición donde se encuentra la instancia dentro del conjunto dado.
	 * La posición comienza desde 0. 
	 * 
	 * @param set Conjunto donde buscar la instancia.
	 * @param inst Instancia a buscar..
	 * @return Índice donde esta la instancia o -1 si no ha sido encontrada.
	 */
	public static int getPosOfInstance (Instances set, Instance inst) {
		for (int i = 0; i < set.numInstances(); i++)
			if (InstanceIS.equals(set.instance(i), inst))
				return i;
		
		return -1;
	} // getPosOfInstance
	
	/**
	 * Borra del vector que sel pas la instancia que sea igual a la instancia dada.
	 * En caso de haber más de una instancia igual eliminará solo la primera que encuentre.
	 * 
	 * @param inst Instancia a borrar.
	 * @param set Vector de instancias.
	 * @return Verdadero si la instancia se encontraba en el conjunto (y ha sido borrada), falso en caso
	 * contrario.
	 */
	public static boolean removeInstanceFromVector (Instance inst, Vector<Instance> set) {
		for (int i = 0; i < set.size(); i++)
			if (InstanceIS.equals(set.elementAt(i), inst)) {
				set.remove(i);
				return true;
			}
		
		return false;
	} // removeInstanceFromVector

	/**
	 * Elimina del conjunto de instancias todas aquellas duplicadas, es decir, que tengan el mismo valor en
	 * sus atributos. Si existen una o más instancias duplicadas tan sólo deja una en la salida, no tiene
	 * por qué ser la primera que aparezca.
	 * 
	 * @param instances Conjunto de instancias donde se eliminarán las instancias duplicadas. 
	 * @param vIndex Vector de posiciones de cada instancia del dataset a filtrar.
	 */
	public static void removeDuplicateInstances (Instances instances, Vector<Integer> vIndex) {
		TreeSet<Instance> hashSet = new TreeSet<Instance>(new InstanceComparator(true));
		int i = 0;
		
		// Recorrer todas las instancias;
		while (i < instances.numInstances())
			// Si la instancia ya existe la eliminamos, sino pasa a la siguiente. 
			if (hashSet.add(instances.instance(i))) {
				i++;
			}
			else {
				instances.delete(i);
				vIndex.remove(i);
			}
	} // removeDuplicateInstances
	
	/**
	 * Elimina del conjunto de instancias todas aquellas duplicadas, es decir, que tengan el mismo valor en
	 * sus atributos. Si existen una o más instancias duplicadas tan sólo deja una en la salida, no tiene
	 * por qué ser la primera que aparezca.
	 * 
	 * @param instances Conjunto de instancias donde se eliminarán las instancias duplicadas. 
	 */
	public static void removeDuplicateInstances (Instances instances) {
		TreeSet<Instance> hashSet = new TreeSet<Instance>(new InstanceComparator(true));
		int i = 0;
		
		// Recorrer todas las instancias;
		while (i < instances.numInstances())
			// Si la instancia ya existe la eliminamos, sino pasa a la siguiente. 
			if (hashSet.add(instances.instance(i)))
				i++;
			else
				instances.delete(i);
	} // removeDuplicateInstances
	
	/**
	 * Devuelve un subconjunto del dataset original con el tamaño dado por percentage.
	 * 
	 * @param trainSet Conjunto de datos del que se desea obtener el subconjunto.
	 * @param percentage Porcentaje de instancias a devolver.
	 * @return Dataset con el porcentaje de instancias del conjunto inicial.
	 * @throws Exception Excepción lanzada si el subconjunto no puede ser creado.
	 */
	public static Instances getRandomSubset (Instances trainSet, double percentage) throws Exception {
		Instances tmpInstances;
		Instance tmpInst;
		Resample resample = new Resample();
		
		tmpInstances = new Instances(trainSet, (int)(trainSet.numInstances() * percentage / 100));
		
		// Si el conjunto original no tiene instancias -> Devolver un conjunto vacío.
		if (trainSet.numInstances() == 0)
			return tmpInstances;
		
		// Establece el formato de las instancias de entrada.
		resample.setInputFormat(tmpInstances);
		
		// Especificar que no se desean instancias repetidas en el conjunto seleccionado.
		resample.setNoReplacement(true);
		
		// Cargar el filtro con las instancias dadas.
		for (int i = 0; i < trainSet.numInstances(); i++)
			resample.input(trainSet.instance(i));
		
		// Asignar el porcentaje a tratar.
		resample.setSampleSizePercent(percentage);
		
		// Si no ha podido ser subdividido -> devolver un dataset con una única instancia al azar.
		if (!resample.batchFinished()){
			tmpInstances.add(trainSet.instance((int)(Math.random() * trainSet.numInstances())));
			return tmpInstances;
		}
		
		// Añadir las instancias seleccionadas por Resample al dataset a devolver.
		tmpInst = resample.output();
		
		while (tmpInst != null) {
			tmpInstances.add(tmpInst);
			tmpInst = resample.output();
		}
		
		return tmpInstances;
	} // getRandomSubset
	
	/**
	 * Transforma la lista de vecinos cercanos en una distribución de probabilidad.
	 * Copiado del clasificador IBk de Weka.
	 *
	 * @param neighbours the list of nearest neighboring instances
	 * @return the probability distribution
	 * @throws Exception if computation goes wrong or has no class attribute
	 */
	public static double makeDistribution (Instances neighbours) throws Exception {
		double total = 0;
		double[] distribution = new double[1];

		for(int i=0; i < neighbours.numInstances(); i++) {
			try {
				distribution[0] += neighbours.instance(i).classValue();
			} catch (Exception ex) {
				throw new Error("Data has no class attribute!");
			}
			
			total++;      
		}

		// Normalise distribution
		if (total > 0)
			Utils.normalize(distribution, total);
		
		return distribution[0];
	} // makeDistribution
	  
} // InstanceIS
