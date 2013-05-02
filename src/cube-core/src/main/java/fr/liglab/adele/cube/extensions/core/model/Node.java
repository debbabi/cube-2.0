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


package fr.liglab.adele.cube.extensions.core.model;

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.cmf.InvalidNameException;
import fr.liglab.adele.cube.cmf.PropertyExistException;
import fr.liglab.adele.cube.cmf.Reference;
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;
import fr.liglab.adele.cube.cmf.PropertyNotExistException;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 1:00 PM
 */
public class Node extends AbstractManagedElement {

    public static final String NAME = "Node";

    public static final String CORE_NODE_ID = "core.node.id";
    public static final String CORE_NODE_TYPE = "core.node.type";
    public static final String CORE_NODE_SCOPE = "core.node.scope";
    public static final String CORE_NODE_COMPONENTS = "core.node.components";

    public Node(CubeAgent agent) {
        super(agent);
    }

    public Node(CubeAgent agent, Properties properties) throws PropertyExistException, InvalidNameException {
        super(agent, properties);
    }

    /**
     * Sets the Node's local identifier.
     *
     * @param node_identifier
     */
    public void setNodeId(String node_identifier) {
        try {
            if (this.getProperty(CORE_NODE_ID) == null)
                this.addProperty(CORE_NODE_ID, node_identifier);
            else
                this.updateProperty(CORE_NODE_ID, node_identifier);
        } catch (PropertyNotExistException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Node's local Id
     * @return
     */
    public String getNodeId() {
        return this.getProperty(CORE_NODE_ID);
    }

    /**
     * Sets the Node's local identifier.
     *
     * @param node_type
     */
    public void setNodeType(String node_type) {
        try {
            if (this.getProperty(CORE_NODE_TYPE) == null)
                this.addProperty(CORE_NODE_TYPE, node_type);
            else
                this.updateProperty(CORE_NODE_TYPE, node_type);
        } catch (PropertyNotExistException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Node's type
     * @return
     */
    public String getNodeType() {
        return this.getProperty(CORE_NODE_TYPE);
    }

    public void setScope(String scope_url) {
        Reference r = null;
        try {
            r = this.addReference(CORE_NODE_SCOPE, true);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        r.addReferencedElement(scope_url);
    }

    public String getScope() {
        Reference r = getReference(CORE_NODE_SCOPE);
        if (r != null && r.getReferencedElements().size() > 0) {
            return r.getReferencedElements().get(0);
        }
        return null;
    }

    /**
     * Add Component to the node
     * @param componentURI
     * @return
     */
    public boolean addComponent(String componentURI) {
        Reference r = null;
        try {
            r = this.addReference(CORE_NODE_COMPONENTS, false);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        if (r != null) {
            return r.addReferencedElement(componentURI);
        }
        return false;
    }

    /**
     * Get nodes of this scope
     * @return
     */
    public List<String> getComponents() {
        Reference r = this.getReference(CORE_NODE_COMPONENTS);
        if (r != null) {
            return r.getReferencedElements();
        }
        return new ArrayList<String>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getNamespace() {
        return CoreExtensionFactory.NAMESPACE;
    }
}
