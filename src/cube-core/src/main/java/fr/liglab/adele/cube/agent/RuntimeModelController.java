package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.agent.cmf.*;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 7:15 PM
 */
public interface RuntimeModelController {

    public String addManagedElement(ManagedElement element);

    public List<Property> getProperties(String managed_element_uri);

    public String getPropertyValue(String managed_element_uri, String name) ;

    public boolean addProperty(String managed_element_uri, String name, String value) throws PropertyExistException, InvalidNameException;

    public String updateProperty(String managed_element_uri, String name, String newValue) throws PropertyNotExistException;

    public List<String> getReferencedElements(String managed_element_uri, String reference_name);

}
