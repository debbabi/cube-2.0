package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.cmf.*;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 7:15 PM
 */
public interface RuntimeModelController {

    public String addManagedElement(ManagedElement element);

    public String getAgentOfElement(String managed_element_uuid);

    public boolean setAgentOfElement(String managed_element_uuid, String agentUri);

    public List<Property> getProperties(String managed_element_uuid);

    public String getPropertyValue(String managed_element_uuid, String name) ;

    public boolean addProperty(String managed_element_uuid, String name, String value) throws PropertyExistException, InvalidNameException;

    public String updateProperty(String managed_element_uuid, String name, String newValue) throws PropertyNotExistException;

    public List<String> getReferencedElements(String managed_element_uuid, String reference_name);

    public boolean addReference(String managed_element_uuid, String reference_name, String referenced_element_uuid) throws InvalidNameException;

    public boolean removeReference(String managed_element_uuid, String reference_name, String referenced_element_uuid);

    public ManagedElement getLocalElement(String managed_element_uuid);

    boolean hasReferencedElements(String managed_element_uuid, String reference_name, String referenced_element_uuri);
}
