package fr.liglab.adele.cube.extensions;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.ConstraintResolver;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 4:56 PM
 */
public interface Extension {

    public String getUri();
    public CubeAgent getCubeAgent();
    public ExtensionBundle getExtensionBundle();
    public Properties getProperties();

    public void run();
    public void stop();
    public void destroy();

    public ConstraintResolver getConstraintResolver(String name);
}
