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
 * InstanceSelectionFilterIF.java
 * Copyright (C) 2012 Universidad de Burgos
 */

package weka.filters.supervised.instance;

import weka.core.Instances;

/**
 * <b>Descripción</b><br>
 * Superclase para los filtros de selección de instancias.
 * <p>
 * <b>Detalles</b><br>
 * Asegura que todos los filtros tengan el método getSolutionSet que devuelve el conjunto solución calculado
 * por el algoritmo de selección de instancias ejecutado.
 * </p>
 * <p>
 * <b>Funcionalidad</b><br>
 * Se utiliza para la biblioteca de algoritmos de selección de instancias realizada para el proyecto de final
 * de carrera en la Universidad de Burgos. Tutelado por: César García Osorio y Juan José Rodríguez Díez.
 * </p>
 * 
 * @author Álvar Arnáiz González
 * @version 1.3
 */
public interface InstanceSelectionFilterIF {

	/**
	 * Devuelve el conjunto de instancias devuelto por el algoritmo.
	 * 
	 * @return Conjunto de instancias solución.
	 */
	public Instances getSolutionSet ();
	
	/**
	 * Devuelve el tiempo de CPU empleado en el filtrado por el algoritmo de selección de instancias 
	 * seleccionado.
	 * 
	 * @return Tiempo de CPU utilizado en el filtrado.
	 */
	public long getFilterCPUTime ();
	
	/**
	 * Devuelve el tiempo empleado en el filtrado por el algoritmo de selección de instancias 
	 * seleccionado.
	 * 
	 * @return Tiempo utilizado en el filtrado.
	 */
	public long getFilterUserTime ();
	
} // FilterInstanceSelection
