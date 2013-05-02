package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.cmf.InvalidNameException;
import fr.liglab.adele.cube.agent.cmf.ManagedElement;
import fr.liglab.adele.cube.agent.cmf.PropertyExistException;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:56 PM
 */
public interface Extension {

    public String getUri();
    public CubeAgent getCubeAgent();
    public ExtensionFactory getExtensionBundle();
    public Properties getProperties();

    public void run();
    public void stop();
    public void destroy();

    public ConstraintResolver getConstraintResolver(String name);

    /**
     * Creates a new Managed Element Instance of the given name.
     * @param element_name
     * @return
     */
    ManagedElement newManagedElement(String element_name);

    /**
     * Creates a new Managed Element Instance of the given name and the given properties.
     *
     * @param element_name
     * @param properties
     * @return
     */
    ManagedElement newManagedElement(String element_name, Properties properties) throws InvalidNameException, PropertyExistException;
}
