/*
 * Copyright 2011-2013 Adele Research Group (http://adele.imag.fr/) 
 * LIG Laboratory (http://www.liglab.fr)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package fr.liglab.adele.cube.agent.defaults;

import fr.liglab.adele.cube.agent.*;
import fr.liglab.adele.cube.CubePlatform;
import fr.liglab.adele.cube.archetype.Archetype;
import fr.liglab.adele.cube.archetype.ArchetypeException;
import fr.liglab.adele.cube.extensions.Extension;
import fr.liglab.adele.cube.extensions.ExtensionBundle;
import fr.liglab.adele.cube.util.Utils;
import fr.liglab.adele.cube.util.parser.ArchetypeParser;
import fr.liglab.adele.cube.util.parser.ArchtypeParsingException;
import fr.liglab.adele.cube.util.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: debbabi
 * Date: 4/26/13
 * Time: 6:16 PM
 */
public class CubeAgentImpl implements CubeAgent {


    /**
     * Cube Agent URI.
     */
    private String uri = null;

    /**
     * Associated Cube Platform.
     */
    private CubePlatform platform;

    /**
     * Configuration.
     */
    private AgentConfig config;

    /**
     * Archetype.
     */
    private Archetype archetype;

    /**
     * Cube Agent Extensions.
     */
    private List<Extension> extensions;

    /**
     * Communicator
     */
    private Communicator communicator;

    /**
     * Runtime Model.
     */
    private RuntimeModel runtimeModel;

    /**
     * Runtime Model Resolver.
     */
    private Resolver resolver;

    /**
     * Life Controller.
     */
    private LifeController lifeController;

    /**
     * Constructor
     * @param cp
     * @param config
     * @throws CubeAgentException
     */
    public CubeAgentImpl(CubePlatform cp, AgentConfig config) throws CubeAgentException {

        this.platform = cp;

        this.config = config;

        if (config == null)
            throw new CubeAgentException("Cube Agent's Configuration error!");

        String host = config.getHost();
        long port = config.getPort();
        this.uri = "cube://" + host + ":" + port;

        // archetype
        try {
            this.archetype = ArchetypeParser.parse(cp, new URL(this.config.getArchetypeUrl()));
        } catch (ParseException e) {
            throw new CubeAgentException(e.getMessage());
        } catch (ArchtypeParsingException e) {
            throw new CubeAgentException(e.getMessage());
        } catch (ArchetypeException e) {
            throw new CubeAgentException(e.getMessage());
        } catch (MalformedURLException e) {
            throw new CubeAgentException(e.getMessage());
        }

        // communicator
        this.communicator = cp.getCommunicator(this.config.getCommunicatorName());

        // Runtime Model
        this.runtimeModel = new RuntimeModelImpl(this);

        // Life Controller
        this.lifeController = new LifeControllerImpl(this);

        // resolver
        resolver = new ResolverImpl(this);

        // get extensions
        this.extensions = new ArrayList<Extension>();
        for (ExtensionConfig ex : this.config.getExtensions()) {
            String id = ex.getId();
            if (id != null && id.length() > 0) {
                ExtensionBundle eb = getPlatform().getExtensionBundle(id);
                if (eb != null) {
                    Extension extension = eb.getExtensionInstance(this, ex.getProperties());
                    if (extension != null) {
                        extensions.add(extension);
                    }
                }   else {
                    System.out.println("[WARNING] the extension '"+id+"' was not found in this OSGi Platform. Check that you have already deployed the adequate bundle!");
                }
            }
        }


    }

    public String getUri() {
        return this.uri;
    }

    public CubePlatform getPlatform() {
        return this.platform;
    }

    public AgentConfig getConfig() {
        return this.config;
    }

    /**
     * Gets the associated Archetype.
     *
     * @return
     */
    public Archetype getArchetype() {
        return this.archetype;
    }

    public List<Extension> getExtensions() {
        return this.extensions;
    }

    /**
     * Gets the extension having the given id.
     *
     * @param namespace
     */
    public Extension getExtension(String namespace) {
        for (Extension e : getExtensions()) {
            if (e.getExtensionBundle().getNamespace().equalsIgnoreCase(namespace))
                return e;
        }
        return null;
    }

    /**
     * Gets the Cube Agent's Communicator.
     *
     * @return
     */
    public Communicator getCommunicator() {
        return this.communicator;
    }

    /**
     * Gets the Runtime Model
     *
     * @return
     */
    public RuntimeModel getRuntimeModel() {
        return this.runtimeModel;
    }

    public void run() {
        System.out.println("\n  +--------------------+");
        System.out.println("  | RUNNING CUBE AGENT | " + uri.toString());
        System.out.println("  +--------------------+\n");
        if (this.communicator != null) {
            try {
                this.communicator.run(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.lifeController != null)
            this.lifeController.run();
        for (Extension ex: this.getExtensions()) {
            ex.run();
        }
    }

    public void stop() {
        System.out.println("[INFO] ... stopping CubeAgent: " + uri.toString());
        for (Extension ex: this.getExtensions()) {
            ex.stop();
        }
        if (this.lifeController != null)
            this.lifeController.stop();
        if (this.communicator != null)
            this.communicator.stop();
    }

    public void destroy() {
        System.out.println("[INFO] ... destroying CubeAgent: " + uri.toString());
        for (Extension ex: this.getExtensions()) {
            ex.destroy();
        }
        if (this.lifeController != null)
            this.lifeController.destroy();
        if (this.communicator != null)
            this.communicator.destroy();
    }
}
