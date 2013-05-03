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


package fr.liglab.adele.cube.extensions.core.constraints;

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.RuntimeModelController;
import fr.liglab.adele.cube.cmf.InvalidNameException;
import fr.liglab.adele.cube.cmf.ManagedElement;
import fr.liglab.adele.cube.cmf.PropertyExistException;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;
import fr.liglab.adele.cube.cmf.PropertyNotExistException;
import fr.liglab.adele.cube.extensions.core.model.Component;
import fr.liglab.adele.cube.extensions.core.model.Node;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:07 AM
 */
public class HasNodeType implements ConstraintResolver {

    private static ConstraintResolver instance = new HasNodeType();

    public static ConstraintResolver instance() {
        return instance;
    }

    public void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        // no initialization for Unary constraints
    }

    public boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance1_uuid = subjectVariable.getValue();
        Object type = objectVariable.getValue();

        if (instance1_uuid != null && type != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {

                String value = rmController.getPropertyValue(instance1_uuid.toString(), Node.CORE_NODE_TYPE);
                if (value != null && value.equalsIgnoreCase(type.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean applyDescription(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        if (subjectVariable != null && objectVariable != null && objectVariable.getValue() != null) {
            if (subjectVariable.getProperty(Node.CORE_NODE_TYPE) == null) {
                try {
                    subjectVariable.addProperty(Node.CORE_NODE_TYPE, objectVariable.getValue().toString());
                    return true;
                } catch (PropertyExistException e) {
                    e.printStackTrace();
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    subjectVariable.updateProperty(Node.CORE_NODE_TYPE, objectVariable.getValue().toString());
                } catch (PropertyNotExistException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Apply the objective constraint.
     * This should modify the two elements in relation!
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public boolean performObjective(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Cancel the applied objective constraint.
     * This should remove properties or references added by the equivalent 'apply' function.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public boolean cancelObjective(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Find value from object variable.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public String find(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
