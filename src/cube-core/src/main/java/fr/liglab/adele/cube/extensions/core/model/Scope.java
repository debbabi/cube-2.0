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
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;
import fr.liglab.adele.cube.agent.cmf.PropertyNotExistException;
import fr.liglab.adele.cube.agent.cmf.Reference;
import fr.liglab.adele.cube.extensions.core.CoreExtensionBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: debbabi
 * Date: 4/15/13
 * Time: 4:28 PM
 */
public class Scope extends AbstractManagedElement {

    public static final String NAME = "Scope";

    public static final String CORE_SCOPE_ID = "core.scope.id";
    public static final String CORE_SCOPE_MASTER = "core.scope.master";
    public static final String CORE_SCOPE_NODES = "core.scope.nodes";



    public Scope(CubeAgent agent) {
        super(agent);
    }

    /**
     * Set Scope identifier.
     * Can not be changed from outside of this class
     * @param scope_identifier
     */
    private void setScopeId(String scope_identifier) {
        try {
            if (this.getProperty(CORE_SCOPE_ID) == null) {
                this.addProperty(CORE_SCOPE_ID, scope_identifier);
            } else {
                this.updateProperty(CORE_SCOPE_ID, scope_identifier);
            }
        } catch (PropertyNotExistException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            e.printStackTrace();
        } catch (PropertyExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Scope Id
     * @return
     */
    public String getScopeId() {
         return this.getProperty(CORE_SCOPE_ID);
    }

    /**
     * Sets the URL of the master server managing this scope leader.
     * @param master_url
     */
    public void setMaster(String master_url) {
        Reference r = null;
        try {
            r = this.addReference(CORE_SCOPE_MASTER, true);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        r.addReferencedElement(master_url);
    }

    /**
     * Gets the URL of the master server containing the scope leaders.
     * @return
     */
    public String getMaster() {
        Reference r = getReference(CORE_SCOPE_MASTER);
        if (r != null && r.getReferencedElements().size() > 0) {
            return r.getReferencedElements().get(0);
        }
        return null;
    }

    /**
     * Add Node to the scope
     * @param nodeURI
     * @return
     */
    public boolean addNode(String nodeURI) {
        Reference r = null;
        try {
            r = addReference(CORE_SCOPE_NODES, false);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }
        return r.addReferencedElement(nodeURI);
    }

    /**
     * Get nodes of this scope
     * @return
     */
    public List<String> getNodes() {
        Reference r = this.getReference(CORE_SCOPE_NODES);
        if (r != null) {
            return r.getReferencedElements();
        }
        return new ArrayList<String>();
    }

    public String getNamespace() {
        return CoreExtensionBundle.NAMESPACE;
    }

    public String getName() {
        return NAME;
    }
}
