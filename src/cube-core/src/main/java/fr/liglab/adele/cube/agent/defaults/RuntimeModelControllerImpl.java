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
import fr.liglab.adele.cube.agent.RuntimeModelController;
import fr.liglab.adele.cube.cmf.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:49 PM
 */
public class RuntimeModelControllerImpl implements RuntimeModelController {

    CubeAgent agent;

    public RuntimeModelControllerImpl(CubeAgent agent) {
        this.agent = agent;
    }

    protected String validate(ManagedElement element) {
        if (element != null) {
            ((AbstractManagedElement)element).updateState(ManagedElement.VALID);
        }
        /*
        if (isInRuntimeModel(this.runtimeModel, element)) {

        } */
        //((RuntimeModelImpl)this.runtimeModel).addUnmanagedElement(element);
        return element.getUUID();
    }

    /**
     * Add an element to the local Runtime Model.
     * It puts its initial state to UNMANAGED.
     * @param element
     * @return
     */
    public String addManagedElement(ManagedElement element) {
        if (element != null) {
            ((AbstractManagedElement)element).updateState(ManagedElement.UNMANAGED);
        }
        //((RuntimeModelImpl)this.runtimeModel).addUnmanagedElement(element);
        return element.getUUID();
    }

    public String getAgentOfElement(String managed_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.getCubeAgent();
        }
        return null;
    }

    public boolean setAgentOfElement(String managed_element_uuid, String agentUri) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            ((AbstractManagedElement)me1).setCubeAgent(agentUri);
            return true;
        }
        return false;
    }

    public List<Property> getProperties(String managed_element_uuid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getPropertyValue(String managed_element_uuid, String name) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            return me1.getProperty(name);
        }
        return null;
    }

    public boolean addProperty(String managed_element_uuid, String name, String value) throws PropertyExistException, InvalidNameException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String updateProperty(String managed_element_uuid, String name, String newValue) throws PropertyNotExistException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getReferencedElements(String managed_element_uuid, String reference_name) {
        System.out.println("CONTROLLER: get referenced elements for: " + managed_element_uuid + " >" + reference_name);
        List<String> result = new ArrayList<String>();
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            System.out.println("CONTROLLER: element>\n" + me1.getTextualDescription());
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.getReferencedElements();
            } else {
                System.out.println("CONTROLLER: no reference with name: " + reference_name);
            }
        } else {
            System.out.println("CONTROLLER: no element with uuid: " + managed_element_uuid);
        }
        return result;
    }

    public boolean addReference(String managed_element_uuid, String reference_name, String referenced_element_uuid) throws InvalidNameException {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.addReference(reference_name, false);
            if (r != null) {
                r.addReferencedElement(referenced_element_uuid);
                return true;
            }
        }
        return false;
    }

    public boolean removeReference(String managed_element_uuid, String reference_name, String referenced_element_uuid) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.removeReferencedElement(referenced_element_uuid);
            }
        }
        return false;
    }

    public boolean hasReferencedElements(String managed_element_uuid, String reference_name, String referenced_element_uuri) {
        ManagedElement me1 = getLocalElement(managed_element_uuid);
        if (me1 != null) {
            Reference r = me1.getReference(reference_name);
            if (r != null) {
                return r.hasReferencedElement(referenced_element_uuri);
            }
        }
        return false;
    }

    public ManagedElement getLocalElement(String managed_element_uuid) {
        ManagedElement me = agent.getRuntimeModel().getManagedElement(managed_element_uuid);
        if (me != null)
            return me;
        else {
            for (ManagedElement unme : agent.getUnmanagedElements()) {
                if (unme.getUUID().equalsIgnoreCase(managed_element_uuid)) {
                    return unme;
                }
            }
        }
        System.out.println("[WARNING] RM.Constroller has not find the element " + managed_element_uuid);
        return null;
    }


}
