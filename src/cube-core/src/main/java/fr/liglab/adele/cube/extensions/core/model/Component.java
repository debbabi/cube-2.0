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
import fr.liglab.adele.cube.agent.cmf.InvalidNameException;
import fr.liglab.adele.cube.agent.cmf.PropertyExistException;
import fr.liglab.adele.cube.agent.cmf.PropertyNotExistException;
import fr.liglab.adele.cube.agent.cmf.Reference;
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 1:00 PM
 */
public class Component extends AbstractManagedElement {

    public static final String NAME = "Component";

    public static final String CORE_COMPONENT_ID = "core.component.id";
    public static final String CORE_COMPONENT_TYPE = "core.component.type";
    public static final String CORE_COMPONENT_NODE = "core.component.node";

    public Component(CubeAgent agent) {
        super(agent);
    }

    public Component(CubeAgent agent, Properties properties) throws PropertyExistException, InvalidNameException {
        super(agent, properties);
    }
    /**
     * Sets the Component's local identifier.
     *
     * @param component_identifier
     */
    public void setComponentId(String component_identifier) {
        try {
            if (this.getProperty(CORE_COMPONENT_ID) == null)
                this.addProperty(CORE_COMPONENT_ID, component_identifier);
            else
                this.updateProperty(CORE_COMPONENT_ID, component_identifier);
        } catch (PropertyNotExistException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Component's local Id
     * @return
     */
    public String getComponentId() {
        return this.getProperty(CORE_COMPONENT_ID);
    }

    /**
     * Sets the Node's local identifier.
     *
     * @param component_type
     */
    public void setComponentType(String component_type) {
        try {
            if (this.getProperty(CORE_COMPONENT_TYPE) == null)
                this.addProperty(CORE_COMPONENT_TYPE, component_type);
            else
                this.updateProperty(CORE_COMPONENT_TYPE, component_type);
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
    public String getComponentType() {
        return this.getProperty(CORE_COMPONENT_TYPE);
    }

    public void setNode(String node_url) {
        Reference r = null;
        try {
            r = this.addReference(CORE_COMPONENT_NODE, true);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        r.addReferencedElement(node_url);
    }

    public String getNode() {
        Reference r = getReference(CORE_COMPONENT_NODE);
        if (r != null && r.getReferencedElements().size() > 0) {
            return r.getReferencedElements().get(0);
        }
        return null;
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
