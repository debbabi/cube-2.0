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
import fr.liglab.adele.cube.agent.defaults.resolver.Variable;

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

    public void init(Variable subjectVariable, Variable objectVariable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean check(Variable subjectVariable, Variable objectVariable) {
        System.out.println("** checking onnode..");
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
    public boolean applyCharacteristic(Variable subjectVariable, Variable objectVariable) {
        if (subjectVariable != null && objectVariable != null && objectVariable.values.size() > 0) {
            //String s = objectVariable.getTestedValue();
            /*if (s != null) {
                subjectVariable.setCubeAgent(s);
                // TODO: Component node..

                return true;
            } */
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
    public boolean applyObjective(Variable subjectVariable, Variable objectVariable) {
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
    public boolean cancelObjective(Variable subjectVariable, Variable objectVariable) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Find value from object variable.
     *
     * @param subjectVariable
     * @param objectVariable
     * @return
     */
    public String find(Variable subjectVariable, Variable objectVariable) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
