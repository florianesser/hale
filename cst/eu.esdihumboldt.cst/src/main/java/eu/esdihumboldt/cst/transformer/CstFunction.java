/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : cst
 * 	 
 * Classname    : eu.esdihumboldt.cst.transformer/ITransformer.java 
 * 
 * Author       : schneidersb
 * 
 * Created on   : Aug 13, 2009 -- 1:50:08 PM
 *
 */
package eu.esdihumboldt.cst.transformer;

import java.util.List;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.oml.ext.Function;

/**
 * Interface which defines the two basic methods to transform a
 * {@link Feature} and a {@link FeatureCollection}.
 * 
 * @author Thorsten Reitz
 */
public interface CstFunction {
	/**
	 * This method can be employed to transform an entire data set.
	 * @param fc a {@link FeatureCollection} with {@link Feature}s to be 
	 * transformed, with source {@link FeatureType}s.
	 * @return a {@link FeatureCollection} with the transformed Features, with 
	 * target {@link FeatureType}s.
	 * @deprecated use {@link #transform(Feature, Feature)} instead.
	 */
	public FeatureCollection<? extends FeatureType, ? extends Feature> 
		transform(FeatureCollection<? extends FeatureType, ? extends Feature> fc);

	
	/**
	 * Apply a function to a single source-target Feature combination.
	 * @param source a source {@link Feature} for the function.
	 * @param target a target {@link Feature} to be affected by the function.
	 * @return the transformed {@link Feature} (identical to target).
	 */
	public Feature transform(Feature source, Feature target);
	
	/**
	 * @param cell a full {@link ICell} to be used for configuration. This
	 * operation is useful if the {@link CstFunction} requires information
	 * about the entities, relations and other structures, but is more complex 
	 * to implement.
	 * @return false if this mode of configuration is not supported.
	 */
	public boolean configure(ICell cell);
	
	/**
	 * @param parameters a {@link List} with {@link IParameter} objects used
	 * for configuring a {@link Function}.
	 * @return false if this mode of configuration is not supported.
	 * @deprecated use {@link #configure(ICell)} instead.
	 */
	public boolean configure(List<IParameter> parameters);
	
	/**
	 * @param parameters a {@link Map} of parameter names and values.
	 * @return false if this mode of configuration is not supported.
	 * @deprecated use {@link #configure(ICell)} instead.
	 */
	public boolean configure(Map<String, String> parametersValues);
	
	
	/**
	 * @return a {@link Map} with parameter names and their type
	 * @deprecated to be replaced by a method based on Cells
	 */	
	public Map<String, Class<?>> getParameterTypes();

}
