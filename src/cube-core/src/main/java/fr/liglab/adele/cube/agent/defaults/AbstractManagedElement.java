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

import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.cmf.ManagedElement;
import fr.liglab.adele.cube.agent.cmf.*;
import fr.liglab.adele.cube.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Author: debbabi
 * Date: 4/25/13
 * Time: 5:34 PM
 */
public abstract class AbstractManagedElement extends Observable implements ManagedElement, Serializable {

    /**
     * Current state of the Managed Element.
     */
    private int state = UNMANAGED;

    /**
     * Concept name.
     */
    private String name = null;

    /**
     * Concept namespace.
     */
    private String namespace = null;

    /**
     * Instance uuid.
     */
    private String uuid = null;

    /**
     * Hosted Cube Agent URI.
     */
    private String cubeAgent = null;

    /**
     * Instance properties.
     */
    private List<Property> properties = new ArrayList<Property>();

    /**
     * Instance references.
     */
    private List<Reference> references = new ArrayList<Reference>();

    /**
     * Index used to create the URIs.
     */
    private static int index=0;

    /**
     * Gets the instance current state.
     * @return
     */
    public int getState() {
        return this.state;
    }

    public AbstractManagedElement(CubeAgent agent) {
        setCubeAgent(agent.getUri());
        this.uuid = Utils.GenerateUUID();
    }

    int updateState(int newState) {
        int oldState = this.state;
        this.state = newState;
        if (newState == VALID) {
            //updateProperties();
            //notifyObservers(new Notification(ManagedElementListener.CHANGED_STATE));
        }
        return oldState;
    }

    public abstract String getName();

    public abstract String getNamespace();

    public String getCubeAgent() {
        return this.cubeAgent;
    }

    public void setCubeAgent(String uri) {
        this.cubeAgent = uri;
    }

    public String getUUID() {
        return this.uuid;
    }

    /**
     * Gets the URI of the current Managed Element.
     * @return
     */
    public String getUri() {
        return getCubeAgent() + "/" + getUUID();
    }

    /**
     * Get Managed Element Properties
     * @return
     */
    public List<Property> getProperties() {
        return this.properties;
    }

    /**
     * Get Property
     * @param name
     * @return NULL if name is null or no property found with the name 'name'; the found property other else.
     */
    private Property _getProperty(String name) {

        if (name != null && name.length() > 0) {
            for (Property p : this.getProperties()) {
                if (p.getName() != null && p.getName().equalsIgnoreCase(name.toLowerCase())) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Get Property value
     * @param name
     * @return NULL if 'name' is null or no property found with 'name' name; the value of the found property other else
     */
    public String getProperty(String name) {
        if (name != null && name.length() > 0) {
            Property p = _getProperty(name);
            if (p != null) {
                return p.getValue();
            }
        }
        return null;
    }

    /**
     * Adding property
     * @param name
     * @param value
     * @return TRUE if the property added; FALSE other else.
     * @throws PropertyExistException
     */
    public boolean addProperty(String name, String value) throws PropertyExistException, InvalidNameException {
         if (name == null || name.length() == 0) {
             return false;
         }
        if (_getProperty(name.toLowerCase()) != null) {
            throw new PropertyExistException("You are trying to add an existing property '"+name+"'!");
        }
        return this.properties.add(new Property(name.toLowerCase(), value));
    }

    /**
     * Update property
     * @param name
     * @param newValue
     * @return NULL is name is null; or Old value if a property exist with 'name' name.
     * @throws PropertyNotExistException
     */
    public String updateProperty(String name, String newValue) throws PropertyNotExistException {
        if (name == null || name.length() <= 0) {
            if (_getProperty(name) == null)
                throw new PropertyNotExistException("You are trying to update unexistant property '" + name + "'!");
            Property p = _getProperty(name);
            String oldValue = p.getValue();
            p.setValue(newValue);
            return oldValue;
        }
        return null;
    }

    /**
     * Get Managed Element References
     * @return
     */
    public List<Reference> getReferences() {
        return this.references;
    }

    /**
     * Get Regerence
     * @param name
     * @return
     */
    public Reference getReference(String name) {
        if (name != null && name.length() > 0) {
            for (Reference r : this.getReferences()) {
                if (r.getName() != null && r.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
                    return r;
                }
            }
        }
        return null;
    }

    public Reference addReference(String name, boolean onlyOne) throws InvalidNameException {
        Reference r = getReference(name);
        if (getReference(name) == null) {
            r = new Reference(name, false);
            this.references.add(r);
        }
        return r;
    }

    /*
    public boolean addReferencedElement(String referenceName, String referencedElementURI) {
        if (referenceName == null || referencedElementURI == null) {
            return false;
        }
        Reference r = getReference(referenceName);
        if (r == null) {

        }
        return false;
    } */
    // TODO: update references


}
