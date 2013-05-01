package fr.liglab.adele.cube.agent;

import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.extensions.Extension;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 5:39 PM
 */
public interface CubeAgent {

    /**
     * Gets the URI
     * @return
     */
    public String getUri();

    /**
     * Gets the platform
     * @return
     */
    public CubePlatform getPlatform();

    /**
     * Gets the initial Agent Configuration
     * @return
     */
    public AgentConfig getConfig();

    /**
     * Gets the associated Archetype.
     * @return
     */
    public Archetype getArchetype();

    /**
     * Gets the associated Extensions
     * @return
     */
    public List<Extension> getExtensions();

    /**
     * Gets the extension having the given id.
     * @param namespace
     */
    public Extension getExtension(String namespace);

    /**
     * Gets the Cube Agent's Communicator.
     * @return
     */
    public Communicator getCommunicator();

    /**
     * Gets the Runtime Model
     * @return
     */
    public RuntimeModel getRuntimeModel();

    public void run();
    public void stop();
    public void destroy();


}
