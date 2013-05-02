package fr.liglab.adele.cube.extensions;


import fr.liglab.adele.cube.agent.CubeAgent;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:06 PM
 */
public interface ExtensionFactory {

    public String getName();
    public String getPrefix();
    public String getNamespace();

    public Extension getExtensionInstance(CubeAgent agent, Properties properties);

}
