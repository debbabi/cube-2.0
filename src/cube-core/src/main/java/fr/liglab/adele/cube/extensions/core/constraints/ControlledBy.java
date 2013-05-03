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
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;
import fr.liglab.adele.cube.cmf.InvalidNameException;
import fr.liglab.adele.cube.cmf.PropertyExistException;
import fr.liglab.adele.cube.cmf.PropertyNotExistException;
import fr.liglab.adele.cube.cmf.Reference;
import fr.liglab.adele.cube.extensions.core.model.Master;
import fr.liglab.adele.cube.extensions.core.model.Scope;

import java.util.List;

/**
 * Author: debbabi
 * Date: 4/29/13
 * Time: 2:36 AM
 */
public class ControlledBy implements ConstraintResolver {

    private static ConstraintResolver instance = new ControlledBy();

    public static ConstraintResolver instance() {
        return instance;
    }

    public void init(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        System.out.println("init using controlledby... ");
        Object instance1_uuid = subjectVariable.getValue();
        if (instance1_uuid != null) {
            //System.out.println("init using controlledby... not null subject");
            RuntimeModelController rmController = agent.getRuntimeModelController();
            if (rmController != null) {
                //System.out.println("init using controlledby... subject_uuid:" + instance1_uuid);
                //System.out.println("init using controlledby... prop:" + Scope.CORE_SCOPE_MASTER);
                List<String> tmp = rmController.getReferencedElements(instance1_uuid.toString(), Scope.CORE_SCOPE_MASTER);
                //System.out.println("init using controlledby... references:" + tmp.size());
                for (String s : rmController.getReferencedElements(instance1_uuid.toString(), Scope.CORE_SCOPE_MASTER)) {
                    //System.out.println("init using controlledby... OK " + s);
                    objectVariable.setValue(s);
                }
            }
        }
    }

    public boolean check(CubeAgent agent, Variable subjectVariable, Variable objectVariable) {
        System.out.println("-1");
        Object instance1_uuid = subjectVariable.getValue();
        Object instance2_uuid = objectVariable.getValue();
        if (instance1_uuid == null) System.out.println("subject NULL");
        if (instance2_uuid == null) System.out.println("object NULL");
        if (instance1_uuid != null && instance2_uuid != null) {
            RuntimeModelController rmController = agent.getRuntimeModelController();
            System.out.println("0");
            if (rmController != null) {
                System.out.println("1");
                if (rmController.hasReferencedElements(instance1_uuid.toString(),
                        Scope.CORE_SCOPE_MASTER,
                        instance2_uuid.toString())) {
                    System.out.println("2");
                    /*
                    if (rmController.hasReferencedElements(instance2_uuid.toString(), Master.CORE_MASTER_SCOPE_LEADERS, instance1_uuid.toString())) {
                        return true;
                    }   */
                    return true;
                }
            }
        }
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
        if (subjectVariable != null && objectVariable != null && objectVariable.getValue() != null) {

            Reference ref = subjectVariable.getReference(Scope.CORE_SCOPE_MASTER);
            if (ref == null) {
                try {
                    Reference r = subjectVariable.addReference(Scope.CORE_SCOPE_MASTER, true);
                    if (r != null) {
                        r.addReferencedElement(objectVariable.getValue().toString());
                        return true;
                    }
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            } else {
                ref.addReferencedElement(objectVariable.getValue().toString());
                return true;
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
                    String scope_id=rmController.getPropertyValue(instance1_uuid.toString(), Scope.CORE_SCOPE_ID);
                    if (rmController.addReference(instance1_uuid.toString(), Scope.CORE_SCOPE_MASTER, instance2_uuid.toString())) {
                        if (rmController.addReference(instance2_uuid.toString(), scope_id, instance1_uuid.toString())) {
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
                if (rmController.removeReference(instance1_uuid.toString(), Scope.CORE_SCOPE_MASTER, instance2_uuid.toString())) {
                    if (rmController.removeReference(instance2_uuid.toString(), objectVariable.getValue().toString(), instance1_uuid.toString())) {
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
                List<String> sleaders = rmController.getReferencedElements(instance2_uuid.toString(), objectVariable.getValue().toString());
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
