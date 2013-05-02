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
import fr.liglab.adele.cube.cmf.PropertyNotExistException;
import fr.liglab.adele.cube.cmf.Reference;
import fr.liglab.adele.cube.agent.defaults.AbstractManagedElement;
import fr.liglab.adele.cube.extensions.core.CoreExtensionFactory;

import java.util.Properties;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 9:27 PM
 */
public class Master extends AbstractManagedElement {


    public static final String NAME = "Master";

    public static final String CORE_MASTER_URI = "core.master.uri";
    public static final String CORE_MASTER_SCOPE_LEADERS = "core.master.scopeleaders";

    public Master(CubeAgent agent) {
        super(agent);
    }

    public Master(CubeAgent agent, Properties properties) throws PropertyExistException, InvalidNameException {
        super(agent, properties);
    }


     public void setMasterURI(String master_uri) {
         try {
             if (this.getProperty(master_uri) == null) {
                 this.addProperty(CORE_MASTER_URI, master_uri);
             } else {
                this.updateProperty(CORE_MASTER_URI, master_uri);
             }
         } catch (PropertyNotExistException e) {
             e.printStackTrace();
         } catch (InvalidNameException e) {
             e.printStackTrace();
         } catch (PropertyExistException e) {
             e.printStackTrace();
         }
     }

    public String getMasterUri() {
        return this.getProperty(CORE_MASTER_URI);
    }

    /**
     * Add Scope Leader to the master table.
     * @param scope_id
     * @param scope_uri
     * @return
     */
    public boolean addScopeLeader(String scope_id, String scope_uri) {
        Reference r = this.getReference(scope_id);
        if (r == null) {
            try {
                r = new Reference(scope_id, true);
                return r.addReferencedElement(scope_uri);
            } catch (InvalidNameException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Gets the Scope Leader of the given id.
     * @return
     */
    public String getScopeLeader(String scope_id) {
        Reference r = this.getReference(scope_id);
        if (r != null) {
            if (r.getReferencedElements() != null && r.getReferencedElements().size() > 0)
                return r.getReferencedElements().get(0);
        }
        return null;
    }

    public String getNamespace() {
        return CoreExtensionFactory.NAMESPACE;
    }

    public String getName() {
        return NAME;
    }
}
