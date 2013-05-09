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


package fr.liglab.adele.cube.plugins.core.constraints;

import fr.liglab.adele.cube.agent.ConstraintResolver;
import fr.liglab.adele.cube.agent.CubeAgent;
import fr.liglab.adele.cube.agent.RuntimeModelController;
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;
import fr.liglab.adele.cube.metamodel.InvalidNameException;
import fr.liglab.adele.cube.metamodel.Reference;
import fr.liglab.adele.cube.plugins.core.model.Component;
import fr.liglab.adele.cube.plugins.core.model.Node;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/28/13
 * Time: 10:24 PM
 */
public class OnNode implements ConstraintResolver {

    private static ConstraintResolver instance = new OnNode();

    public static ConstraintResolver instance() {
        return instance;
    }

    public void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance1_uuid = subjectVariable.getValue();
        if (instance1_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                for (String s : rmController.getReferencedElements(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE)) {
                    objectVariable.setValue(s);
                    break;
                }
            }
        }
    }

    public boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();
        if (instance1_uuid != null && instance2_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                if (rmController.hasReferencedElement(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE, instance2_uuid.toString())) {
                    //if (rmController.hasReferencedElement(instance2_uuid.toString(), Node.CORE_NODE_COMPONENTS, instance1_uuid.toString())) {
                        return true;
                    //}
                }
            }
        }
        //System.out.println("5");
        return false;
    }

    /**
     * Applies the predicate information located on the objectVariable, on the subjectVariable
     * to limit the research domain space.
     * <p/>
     * It should be only implemented for UnaryConstraints.
     *
     * @param subjectVariable
     * @param objectVariable
     */
    public boolean applyDescription(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        if (subjectVariable != null && objectVariable != null && objectVariable.hasValue()) {
            Reference ref = subjectVariable.getReference(Component.CORE_COMPONENT_NODE);
            if (ref == null) {
                try {
                    Reference r = subjectVariable.addReference(Component.CORE_COMPONENT_NODE, true);
                    if (r != null) {
                        r.addReferencedElement(objectVariable.getValue().toString());

                        String agent_uri = agent.getRuntimeModelController().getAgentOfElement(objectVariable.getValue().toString());
                        if (agent_uri != null)     {
                            subjectVariable.setCubeAgent(agent_uri);
                            return true;
                        }
                    }
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            } else {

                ref.addReferencedElement(objectVariable.getValue().toString());

                String agent_uri = agent.getRuntimeModelController().getAgentOfElement(objectVariable.getValue().toString());
                if (agent_uri != null)     {
                    subjectVariable.setCubeAgent(agent_uri);
                    return true;
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
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();

        if (instance1_uuid != null && instance2_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                try {
                    if (rmController.addReferencedElement(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE, instance2_uuid.toString())) {
                        if (rmController.addReferencedElement(instance2_uuid.toString(), Node.CORE_NODE_COMPONENTS, instance1_uuid.toString())) {
                            return true;
                        }
                    }
                } catch (InvalidNameException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
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
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();

        if (instance1_uuid != null && instance2_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                if (rmController.removeReferencedElement(instance1_uuid.toString(), Component.CORE_COMPONENT_NODE, instance2_uuid.toString())) {
                    if (rmController.removeReferencedElement(instance2_uuid.toString(), Node.CORE_NODE_COMPONENTS, instance1_uuid.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find value from object variable.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public String find(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        Object instance2_uuid = objectVariable.getValue();

        if (instance2_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                List<String> sleaders = rmController.getReferencedElements(instance2_uuid.toString(), Node.CORE_NODE_COMPONENTS);
                for (String s : sleaders) {
                    if (!subjectVariable.hasValue(s)) {
                        return s;
                    }
                }
            }
        }
        return null;
    }
}
