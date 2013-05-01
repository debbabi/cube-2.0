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

import fr.liglab.adele.cube.agent.RuntimeModel;
import fr.liglab.adele.cube.agent.RuntimeModelController;
import fr.liglab.adele.cube.agent.cmf.*;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/27/13
 * Time: 6:49 PM
 */
public class RuntimeModelControllerImpl implements RuntimeModelController {

    RuntimeModel runtimeModel;

    public RuntimeModelControllerImpl(RuntimeModel rm) {
        this.runtimeModel = rm;
    }

    protected String validate(ManagedElement element) {
        if (element != null) {
            ((AbstractManagedElement)element).updateState(ManagedElement.VALID);
        }
        /*
        if (isInRuntimeModel(this.runtimeModel, element)) {

        } */
        ((RuntimeModelImpl)this.runtimeModel).addUnmanagedElement(element);
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
        ((RuntimeModelImpl)this.runtimeModel).addUnmanagedElement(element);
        return element.getUUID();
    }

    public List<Property> getProperties(String managed_element_uri) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getPropertyValue(String managed_element_uri, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean addProperty(String managed_element_uri, String name, String value) throws PropertyExistException, InvalidNameException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String updateProperty(String managed_element_uri, String name, String newValue) throws PropertyNotExistException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getReferencedElements(String managed_element_uri, String reference_name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
