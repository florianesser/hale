/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.schemaprovider.model;

import java.util.Set;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.About;

/**
 * Schema element
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaElement extends AbstractDefinition implements Comparable<SchemaElement> {

	/**
	 * The element name
	 */
	private final Name elementName;
	
	/**
	 * The type name
	 */
	private final Name typeName;
	
	/**
	 * The element type
	 */
	private TypeDefinition type;
	
	/**
	 * The attribute/feature type
	 */
	private AttributeType attributeType;
	
	/**
	 * Create a schema element
	 * 
	 * @param elementName the element name
	 * @param typeName the type name
	 * @param type the corresponding type definition, may be <code>null</code>
	 *   for later assignment
	 */
	public SchemaElement(Name elementName, Name typeName, TypeDefinition type) {
		this.elementName = elementName;
		this.typeName = typeName;
		
		setType(type);
	}
	
	/**
	 * @param resolving the types that are already in the process of creating a
	 *   feature type, may be <code>null</code>
	 * 
	 * @return the attribute type
	 */
	public AttributeType getAttributeType(Set<TypeDefinition> resolving) {
		if (attributeType == null) {
			if (type == null) {
				throw new IllegalStateException("May not be called yet");
			}
			
			if (!type.isAttributeTypeSet()) {
				attributeType = type.createFeatureType(elementName, resolving);
			}
			else {
				attributeType = type.getType(resolving);
			}
		}
		
		return attributeType;
	}
	
	/**
	 * Get the feature type if available
	 * 
	 * @return the feature type or <code>null</code> if no type could be
	 *   determined or the type is not a feature type
	 */
	public FeatureType getFeatureType() {
		AttributeType type = getAttributeType(null);
		if (type != null && type instanceof FeatureType) {
			return (FeatureType) type;
		}
		else {
			return null;
		}
	}

	/**
	 * @return the type
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TypeDefinition type) {
		if (this.type != null) {
			this.type.removeDeclaringElement(this);
		}
		
		this.type = type;
		
		if (type != null) {
			type.addDeclaringElement(this);
		}
	}

	/**
	 * @return the elementName
	 */
	public Name getElementName() {
		return elementName;
	}

	/**
	 * @return the typeName
	 */
	public Name getTypeName() {
		return typeName;
	}

	/**
	 * @see Definition#getDisplayName()
	 */
	public String getDisplayName() {
		return elementName.getLocalPart();
	}

	/**
	 * @see Definition#getEntity()
	 */
	public Entity getEntity() {
		return new FeatureClass(
				new About(elementName.getNamespaceURI(), 
						elementName.getLocalPart()));
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	public String getIdentifier() {
		return elementName.getNamespaceURI() + "/" + elementName.getLocalPart();
	}
	
	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(SchemaElement other) {
		int result = elementName.getLocalPart().compareToIgnoreCase(other.elementName.getLocalPart());
		if (result == 0) {
			return elementName.getNamespaceURI().compareToIgnoreCase(other.elementName.getNamespaceURI());
		}
		
		return result;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "[element] " + getIdentifier();
	}
	
}
